import cv2 
import cv
import numpy
#import scipy
#from scipy import spatial

#Uncomment to view full arrays
#numpy.set_printoptions(threshold='nan')

class CvModule:

	
	def __init__(self):
		#print "CV Initialized."
		self.SAMPLES = 0
		self.mean = None
		self.eigens = None
		self.tmat = None
		self.projection = None
		self.userlist = []
		self.ANN = None
		self.ANN_params = dict(train_method = cv2.ANN_MLP_TRAIN_PARAMS_RPROP)
		self.ANN_matrix = None
		self.ANN_persons = 0
		self.ANN_names = []	
		self.PANN = []

	#Calculates distance, should be replaced with more suitable algorithm (Mahalanobis)
	def distance(self,mat1,mat2,i):

		mat2 =  mat2.flatten()
		res = numpy.linalg.norm(mat1-mat2)
		return res	
	
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
		
		#print self.tmat.shape

		#print "Doing PCA"		
		self.mean,self.eigens = cv2.PCACompute(self.tmat,maxComponents=self.SAMPLES)
		self.projection = cv2.PCAProject(self.tmat, self.mean, self.eigens)
		
		#print "Training ANN_MLP"
		if(self.SAMPLES > 1):
			
			'''
			id= numpy.identity(self.SAMPLES,numpy.float32)
			print "eigens",self.projection.shape
			print "eigenusers",len(self.userlist)
			print "persons",len(self.ANN_names)
		'''
			
			
			#normp = self.projection.copy()
			
			#cv2.normalize(self.projection, normp, 0,10, cv2.NORM_MINMAX)
			
			#print normp
			
			g = numpy.zeros(shape=(self.SAMPLES,self.ANN_persons),dtype=numpy.float32)	
			
			trainmap = dict(zip(self.ANN_names,numpy.arange(self.ANN_persons)))
			
			for i,val in enumerate(self.userlist):
				g[i][trainmap[val]] = 1
			#print g
			
			shape = numpy.array([self.SAMPLES,40,(self.ANN_persons+2),self.ANN_persons]) #40,35
			#print shape
			#shape = numpy.array([self.SAMPLES,16,8,self.ANN_persons]) #40,35

			net = cv2.ANN_MLP()
			net.create(shape)
			net.train(self.projection,g,None,params = self.ANN_params)
			self.ANN = net
		#	print "Done training neural network, shape is:",shape
		#else:
		#	print "One sample, skipping"

		'''
		print "Mean"
		print self.mean.shape
		
		print "Eigens"
		print self.eigens.shape
		'''
		#print "Projection"
		#print self.projection.shape
		
		#print "Number of samples",self.SAMPLES
		#print "Discrete persons",self.ANN_persons
	
	#Tries to identify person based on input image.
	#Returs array containing matches sorted from best to worst
	def identify(self,imatrix):
		
		rank = []
		

		rec = self.prepareImage(imatrix)

		proj = cv2.PCAProject(rec, self.mean[0], self.eigens)

		pre = numpy.float32([proj.flatten()]) 
		
		#print "Identifying: ANN_MLP"
		
		nil,result = self.ANN.predict(pre)
		#print result
		
		#print "Identifying: Euclidean"
	
		for i in range(0, self.SAMPLES):
			rank.append(self.distance(self.projection[i], proj, i))
		
		#print rank
		
		
		#Construct matchlist for euclidean
		z = zip(self.userlist,rank)
		m = dict(z)
		se = sorted(m,key=m.__getitem__)
		
		#Neural matchlist
		zn = zip(self.ANN_names,result[0])
		mn = dict(zn)
		sa = sorted(mn,key=mn.__getitem__)
		
		return se,sa[::-1]

###################################################################################################

def main():
		#print "Started"
		cvmod = CvModule()
		
		sets = 20
		timages = 5
		
		for i in range(0,sets):
			id = i+1
			for z in range (0,timages):
				n =str(z+1)
				t = "ORL/s" + str(id) +"/" + n + ".pgm"
				cvmod.train(t,("u_"+str(id)))
				#print t
		
		###
		
		rimages = 5
		ecount = 0
		ncount = 0
		
		for z in range(0,sets):
			id = z+1
			eres = []
			nres = []
			for x in range(0,rimages):
				n = str(x+6)
				t = "ORL/s" + str(id) +"/" + n + ".pgm"
				er,nr = cvmod.identify(t)
				eres.append(er[0])
				nres.append(nr[0])
			ecount += eres.count(("u_"+str(id)))
			ncount += nres.count(("u_"+str(id)))
		
		total = rimages*sets
		ep = (ecount/float(total))*100
		np = (ncount/float(total))*100
		print "Euclidean result",ecount,ep
		print "Neuralnet result",ncount,np
		print "Total",str(total)
		
if __name__ == "__main__":
    main()
	
