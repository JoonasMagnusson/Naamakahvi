import cv2
import cv
import sys,numpy
import pickle
import os
import shutil

#Uncomment to view full arrays
#numpy.set_printoptions(threshold='nan')


#userNet class represents users neural network
#It contains users name and trained network
class userNet:

    
    def __init__(self):
        
        self.name = None
        self.net = None
        self.params = dict(bp_dw_scale = 0.1,train_method = cv2.ANN_MLP_TRAIN_PARAMS_BACKPROP)
        self.hidden = 10 #Neurons in the hidden layer, modify if needed. Currently optimal for 40 users with 6 trained faces
        
    def getName(self):
        
        return self.name
    
    
    def train(self,name,eigens,map,samples):
        
        self.name = name
        shape = numpy.array([samples,self.hidden,1])
        self.net = cv2.ANN_MLP()
        self.net.create(shape)
        
        self.net.train(eigens,map,None,params = self.params)

        
        
    def identify(self,imgmat):
        
        nil, res = self.net.predict(imgmat)

        return self.name,res[0][0]



class neuralmodule:
    

    def __init__(self):
        print "Face recognition module initialized."
        self.SAMPLES = 0
        self.mean = None
        self.eigens = None
        self.tmat = None
        self.projection = None
        self.userlist = []
        self.ANN = None
        self.ANN_params = dict(bp_dw_scale = 0.2,train_method = cv2.ANN_MLP_TRAIN_PARAMS_BACKPROP)
        self.ANN_matrix = None
        self.ANN_persons = 0
        self.ANN_names = []    
        self.PANN = []
        self.pnets = []
        
        self.dir = "images"
        
        if not os.path.exists(self.dir):
            os.makedirs(self.dir)
        
        
    #Pickles essential data to a file    
    def saveData(self,file):
        
        output = open(file, 'wb')
        
        pickle.dump(self.SAMPLES, output)
        pickle.dump(self.tmat, output)
        pickle.dump(self.userlist, output)
        pickle.dump(self.ANN_persons, output)
        pickle.dump(self.ANN_names, output)
        #print "Data Saved", file
        output.close()
    
    #Loads essential data from file and trains networks        
    def loadData(self,file):
        
        input = open(file, 'rb')
        
        self.SAMPLES = pickle.load(input)
        self.tmat = pickle.load(input)
        self.userlist = pickle.load(input)
        self.ANN_persons = pickle.load(input)
        self.ANN_names = pickle.load(input)
        #print "Data loaded", file
        self.computeNets()

    
    #Grayscales and normalizes image for PCA
    def prepareImage(self,train):
        #print "Preparing"
        
        tempmat1 = cv2.imread(train,cv.CV_LOAD_IMAGE_GRAYSCALE)
        
        cv2.equalizeHist(tempmat1,tempmat1)
        #tempmat = (tempmat1.shape,numpy.float32)
        tempmat = tempmat1.astype(numpy.float32)
        cv2.normalize(tempmat, tempmat, 0, 1, cv2.NORM_MINMAX)
        tempmat = tempmat.reshape(-1)
        mat = tempmat.copy()
        return mat
    
    #Saves images
    def saveImage(self,name,image):
        
        bdir = self.dir + "/" + name
        
        if not os.path.exists(bdir):
            os.makedirs(bdir)
        
        #save max 100 images
        for i in range(0,100):
            fname = bdir + "/" + name + "_img_" + str(i)
            if not os.path.exists(fname):
                shutil.copyfile(image, fname)
                break    

    
    #Trains the recognizer and updates user list
    def train(self,train,name):
        
        print self.userlist

        self.saveImage(name, train)
        tempmat = self.prepareImage(train)
        
        count = self.userlist.count(name)
        
        print count
        
        if (count > 5):
            
            r = [i for i, x in enumerate(self.userlist) if x == name][0]
            print r
            self.tmat[r,:] = tempmat
            print numpy.shape(self.tmat)
            
        else:
            
            
            self.SAMPLES += 1

            if name not in self.userlist:
                self.ANN_names.append(name)
                self.ANN_persons += 1
    
            self.userlist.append(name)
                
            if (self.tmat != None):
                self.tmat = numpy.vstack((self.tmat,tempmat))
    
            else:
                self.tmat = tempmat.copy()
        
        #self.computeNets()
        
        #print self.tmat.shape

    #Computes individual neural networks
    def computeNets(self):

        pnets = []

        #Doing PCA       
        self.mean,self.eigens = cv2.PCACompute(self.tmat,maxComponents=self.SAMPLES)
        self.projection = cv2.PCAProject(self.tmat, self.mean, self.eigens)
        
        #Training ANN_MLP, creating matrices for opencv neural subsystem
        if(self.SAMPLES > 1):
            
            
            g = numpy.zeros(shape=(self.SAMPLES,self.ANN_persons),dtype=numpy.float32)    
            
            trainmap = dict(zip(self.ANN_names,numpy.arange(self.ANN_persons)))
            
            for i,val in enumerate(self.userlist):
                g[i][trainmap[val]] = 1
            
            
            for m in range(0, g.shape[1]):
                
                verk = userNet()
                verk.train(self.ANN_names[m], self.projection, g[:,m], self.SAMPLES)
                pnets.append(verk)
            
            self.pnets = pnets 
                
    #Tries to identify person based on input image.
    #Returs array containing matches sorted from best to worst
    def identify(self,imatrix):

        match_threshold = 0.7
        match = False

        rec = self.prepareImage(imatrix)

        proj = cv2.PCAProject(rec, self.mean[0], self.eigens)

        pre2 = numpy.float32([proj.flatten()]) 
        
        result = []
        
        for a,b in enumerate(self.pnets):
            
            r = b.identify(pre2)
            result.append(r)
            if(r[1] > match_threshold):
                match = True
            #print r
        
        my = dict(result)
        sy = sorted(my,key=my.__getitem__)
        
        return sy[::-1],match