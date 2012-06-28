import cv2 
import cv
import sys,numpy
import scipy
from scipy import spatial

#Uncomment to view full arrays
#numpy.set_printoptions(threshold='nan')

class CvModule:

	
	def __init__(self):
		print "Initialized."
		self.SAMPLES = 0
		self.mean = None
		self.eigens = None
		self.tmat = None
		self.projection = None
	
	def distance(self,mat1,mat2,i):

		mat2 =  mat2.flatten()
			
	 	print "Result ",i
	 	print numpy.linalg.norm(mat1-mat2)		
	
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

	def train(self,train):
		
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
		
		if(self.SAMPLES > 0):
			cv2.namedWindow("average")
			cv2.imshow("average",self.mean.reshape((192,168)))	#For test images
		print "Eigens"
		print self.eigens.shape
		
		print "Projection"
		print self.projection.shape
		self.SAMPLES += 1
		print "Number of samples",self.SAMPLES
	
	def identify(self,imatrix):
		print "Identifying"
		#print projection
		rec = self.prepareImage(imatrix)
		#print rec
		#print self.mean
		#print self.eigens.shape
		proj = cv2.PCAProject(rec, self.mean[0], self.eigens)
		
		r_proj = cv2.PCABackProject(proj, self.mean[0], self.eigens)

		cv2.namedWindow("back")
		cv2.imshow("back",r_proj.reshape((192,168)))
		#cv2.waitKey()
		
		#print self.projection
		for i in range(0, self.SAMPLES):
			self.distance(self.projection[i], proj, i)

			r_proj = cv2.PCABackProject(self.projection[i], self.mean[0], self.eigens)
			cv2.namedWindow(str(i))
			cv2.imshow(str(i),r_proj.reshape((192,168)))
		
		

###################################################################################################

def main():
	for i in range(0,1000):
		print "Started"
		cvmod = CvModule()
		#cvmod.train("1.pgm")
		#cvmod.train("2.pgm")
		#cvmod.train("3.pgm")
		#cvmod.train("4.pgm")
		#cvmod.train("5.pgm")
		#cvmod.train("6.pgm")
		#cvmod.train("7.pgm")
		cvmod.train("8.pgm")
		cvmod.train("10.pgm")
		cvmod.train("11.pgm")
		cvmod.train("capture2.jpg")
		cvmod.train("capture4.jpg")
		cvmod.train("capture.jpg")
		#cvmod.train("13.pgm")
		#cvmod.train("14.pgm")
		#cvmod.train("15.pgm")
		#cvmod.train("16.pgm")
		#cvmod.train("17.pgm")
		#cvmod.train("18.pgm")
		#cvmod.train("19.pgm")
		cvmod.identify("capture3.jpg")
	cv2.waitKey()	
	
if __name__ == "__main__":
    main()
	
