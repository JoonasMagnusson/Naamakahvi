import unittest
import logging
import neuralmodule
import sys


#This test needs ORL face database. Caveat emptor.


class Testneuralmodule(unittest.TestCase):
    
    def setUp(self):

        self.log = logging.getLogger( "Testneuralmodule.TestConnect" )
        self.neural = neuralmodule.neuralmodule()
        
    
    
    #Test recognition with image used in training
    def testRecogIdentical(self):
        
        imagesToTrain = 6
        sets = 40
        recog_rate = 0
        
        for i in range(0,sets):
            id = i+1
            
            for z in range (0,imagesToTrain):
                n =str(z+1)
                t = "ORL/s" + str(id) +"/" + n + ".pgm"
                self.neural.train(t,("u_"+str(id)))
                #print t
        
        for d in range(0,sets):
            id = d+1
            t = "ORL/s" + str(id) +"/2.pgm"
            r = self.neural.identify(t)
            
            real =  "u_" + str(id)
            #print r[0][0]
            if(r[0][0] == real):
                recog_rate += 1

        print recog_rate
        self.assertGreaterEqual(recog_rate, 36, "Recognition rate should be 90% or more")

if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testneuralmodule.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()