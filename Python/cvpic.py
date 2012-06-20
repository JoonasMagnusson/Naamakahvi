import cv2 
import cv
import sys,numpy
import scipy
from scipy import spatial

def main():
	cap = cv2.VideoCapture(0)
	
	#For some reason these fields are not a part of cv2, so importing cv is necessary
	cap.set(cv.CV_CAP_PROP_FRAME_WIDTH,1280)
	cap.set(cv.CV_CAP_PROP_FRAME_HEIGHT,800)
	if(cap.isOpened() == None):
		print "No device"
	
		
	key = -1	
	cas = cv2.CascadeClassifier("haarcascade_frontalface_alt.xml")
	cv2.namedWindow("Live")

	while(key == -1):
		ret,img = cap.read()
		img = cv2.flip(img, 1)
		cv2.putText(img, "Press space to capture",(0,20), cv2.FONT_HERSHEY_COMPLEX_SMALL, 1, (0,255,0))
		
		bw = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
		cv2.equalizeHist(bw,bw)
		#These settings give smooth fps at least for core i5
		i = cas.detectMultiScale(bw,1.1,5,minSize=(200,200),maxSize=(220,220))
		if(type(i) != tuple):	
			i = i[0]
			#WTF, THERE MUST BE SMARTER WAY
			cv2.rectangle(img,( int(i[0]),int(i[1])),((int(i[2])+int(i[0])),(int(i[3])+int(i[1])) ),(0, 255, 0),2)
		cv2.imshow("Live",img)
		key = cv2.waitKey(30)
		
	print "Captured!"
	#cv2.putText(img, "CAPTURED!",(0,40), cv2.FONT_HERSHEY_COMPLEX_SMALL, 1, (0,0,255))
	cv2.imshow("Live",img)
	testimg = cv2.getRectSubPix(bw,(int(i[2]),int(i[3])),((int(i[0])+int(i[2])/2),(int(i[1]+int(i[3])/2))))
	testimg = cv2.resize(testimg,(168,192))
	cv2.imshow("Live",testimg)
	cv2.imwrite("capture.jpg", testimg)
	cv2.waitKey()

if __name__ == "__main__":
    main()
	