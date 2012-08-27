import unittest
import logging
import neuralmodule as neuralmodule
import sys



#This test needs ORL face database.
#Database must be located in a folder named ORL and it's 
#structure must be intact


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
            #print i
            for z in range (0,imagesToTrain):
                n =str(z+1)
                t = "ORL/s" + str(id) +"/" + n + ".pgm"
                self.neural.train(t,("u_"+str(id)))
                #self.neural.saveData("data.pkl")
                #print t
                
        self.neural.computeNets()
        
        for d in range(0,sets):
            id = d+1
            t = "ORL/s" + str(id) +"/2.pgm"
            r = self.neural.identify(t)
            
            real =  "u_" + str(id)
            #print r[0][0]
            if(r[0][0] == real):
                recog_rate += 1

        print recog_rate
        self.assertGreaterEqual(recog_rate, (sets*0.9), "Recognition rate should be 90% or more")
        
    
    
    #Test recognition with image not used in training
    def testRecogNonIdentical(self):
        
        neural = neuralmodule.neuralmodule()
        
        imagesToTrain = 6
        sets = 40
        recog_rate = 0
        
        for i in range(0,sets):
            id = i+1
            #print i
            for z in range (0,imagesToTrain):
                n =str(z+1)
                t = "ORL/s" + str(id) +"/" + n + ".pgm"
                neural.train(t,("u_"+str(id)))
                #self.neural.saveData("data.pkl")
                #print t
        
        neural.computeNets()
        
        for d in range(0,sets):
            id = d+1
            for g in range (0,4):
                n = (g+7)
                t = "ORL/s" + str(id) +"/" + str(n) + ".pgm"
                r = neural.identify(t)
            
                real =  "u_" + str(id)
                #print r[0][0]
                if(r[0][0] == real):
                    recog_rate += 1 
        d = sets*4            
        sb = recog_rate/float(d)
        self.assertGreaterEqual(sb, 0.8, "Recognition rate should be 80% or more")
        
        
    #Test recognition with image not used in training
    def testOverSixImages(self):
        
        neural = neuralmodule.neuralmodule()
        
        imagesToTrain = 10
        sets = 40
        recog_rate = 0
        
        for i in range(0,sets):
            id = i+1
            #print i
            for z in range (0,imagesToTrain):
                n =str(z+1)
                t = "ORL/s" + str(id) +"/" + n + ".pgm"
                neural.train(t,("u_"+str(id)))
                #self.neural.saveData("data.pkl")
                #print t
        
        neural.computeNets()
        
        for d in range(0,sets):
            id = d+1
            for g in range (0,4):
                n = (g+7)
                t = "ORL/s" + str(id) +"/" + str(n) + ".pgm"
                r = neural.identify(t)
            
                real =  "u_" + str(id)
                #print r[0][0]
                if(r[0][0] == real):
                    recog_rate += 1 
        d = sets*4            
        sb = recog_rate/float(d)
        self.assertGreaterEqual(sb, 0.8, "Recognition rate should be 80% or more")
        

        

if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testneuralmodule.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()