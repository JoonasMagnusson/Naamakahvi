import cv2 
import cv
import sys,numpy
import scipy
from scipy import spatial

#Uncomment to view full arrays
#numpy.set_printoptions(threshold='nan')

class cvmodule:

	
	def __init__(self):
		print "CV Initialized."
		self.SAMPLES = 0
		self.mean = None
		self.eigens = None
		self.tmat = None
		self.projection = None
		self.userlist = []
	
	#Calculates distance, should be replaced with more suitable algorithm (Mahalanobis)
	def distance(self,mat1,mat2,i):

		mat2 =  mat2.flatten()
		res = numpy.linalg.norm(mat1-mat2)
	 	print "Result ",i
	 	print res
		return res	
	
	#Grayscales and normalizes image for PCA
	def prepareImage(self,train):
		print "Preparing"
		
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
		
		self.userlist.append(name)

		print "Training", train
		tempmat = self.prepareImage(train)
		
		if (self.tmat != None):
			self.tmat = numpy.vstack((self.tmat,tempmat))

		else:
			self.tmat = tempmat.copy()
		
		print self.tmat.shape
		self.mean,self.eigens = cv2.PCACompute(self.tmat)
		self.projection = cv2.PCAProject(self.tmat, self.mean, self.eigens)
		print "Mean"
		print self.mean.shape
		
		print "Eigens"
		print self.eigens.shape
		
		print "Projection"
		print self.projection.shape
		self.SAMPLES += 1
		print "Number of samples",self.SAMPLES
	
	#Tries to identify person based on input image.
	#Returs array containing matches sorted from best to worst
	def identify(self,imatrix):
		
		rank = []

		print "Identifying"

		rec = self.prepareImage(imatrix)

		proj = cv2.PCAProject(rec, self.mean[0], self.eigens)
		
		for i in range(0, self.SAMPLES):
			rank.append(self.distance(self.projection[i], proj, i))

		z = zip(self.userlist,rank)
		m = dict(z)
		s = sorted(m,key=m.__getitem__)
		return s

