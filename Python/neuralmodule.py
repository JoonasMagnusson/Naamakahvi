import cv2
import cv
import sys,numpy
import scipy
from scipy import spatial
import pickle

#Uncomment to view full arrays
#numpy.set_printoptions(threshold='nan')

class neuralmodule:
    

    def __init__(self):
        #print "CV Initialized."
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
        
        
        
    def saveData(self):
        
        output = open('data.pkl', 'wb')
        
        pickle.dump(self.SAMPLES, output)
        pickle.dump(self.tmat, output)
        pickle.dump(self.userlist, output)
        pickle.dump(self.ANN_persons, output)
        pickle.dump(self.ANN_names, output)
        
        output.close()
            
    def loadData(self,datafile):
        
        input = open('data.pkl', 'rb')
        
        self.SAMPLES = pickle.load(input)
        self.tmat = pickle.load(input)
        self.userlist = pickle.load(input)
        self.ANN_persons = pickle.load(input)
        self.ANN_names = pickle.load(input)
        
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
    
    #Trains the recognizer and updates userlist
    def train(self,train,name):
        
        #print self.userlist
        
        
        self.SAMPLES += 1

        #Count discrete users for ANN_MLP
        if name not in self.userlist:
            self.ANN_names.append(name)
            self.ANN_persons += 1
        
        
        self.userlist.append(name)

        #print "Training", train
        tempmat = self.prepareImage(train)
        
        if (self.tmat != None):
            self.tmat = numpy.vstack((self.tmat,tempmat))

        else:
            self.tmat = tempmat.copy()
        
        self.computeNets()
        
        #print self.tmat.shape

    def computeNets(self):

        #print "Doing PCA"        
        self.mean,self.eigens = cv2.PCACompute(self.tmat,maxComponents=self.SAMPLES)
        self.projection = cv2.PCAProject(self.tmat, self.mean, self.eigens)
        
        #print "Training ANN_MLP"
        if(self.SAMPLES > 1):
            
            
            g = numpy.zeros(shape=(self.SAMPLES,self.ANN_persons),dtype=numpy.float32)    
            
            trainmap = dict(zip(self.ANN_names,numpy.arange(self.ANN_persons)))
            
            for i,val in enumerate(self.userlist):
                g[i][trainmap[val]] = 1
            
            
            for m in range(0, g.shape[1]):
        #        print i
                shape2 = numpy.array([self.SAMPLES,20,1])
                net2 = cv2.ANN_MLP()
                net2.create(shape2)
                net2.train(self.projection,g[:,m],None,params = self.ANN_params)
                if (m >= len(self.PANN)):
                    self.PANN.append(net2)
                else:
                    self.PANN[m] = net2
    
    #Tries to identify person based on input image.
    #Returs array containing matches sorted from best to worst
    def identify(self,imatrix):


        rec = self.prepareImage(imatrix)

        proj = cv2.PCAProject(rec, self.mean[0], self.eigens)

        pre2 = numpy.float32([proj.flatten()]) 

        major = []
        for h in range(0,len(self.PANN)):
            net = self.PANN[h]
            nil, res = net.predict(pre2)
            major.append(res[0].flatten()[0])
            
        
        zy = zip(self.ANN_names,major)
        my = dict(zy)
        sy = sorted(my,key=my.__getitem__)
        
        return sy[::-1],my
