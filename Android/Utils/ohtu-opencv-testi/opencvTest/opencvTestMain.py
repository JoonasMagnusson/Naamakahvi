import cv2 
import cv
import sys,numpy
import scipy
from scipy import spatial

#numpy.set_printoptions(threshold='nan')

cv.NamedWindow("Original", cv.CV_WINDOW_AUTOSIZE)
cv.MoveWindow("Original", 0, 0)
cv.NamedWindow("Average", cv.CV_WINDOW_NORMAL)
cv.MoveWindow("Average", 700, 0)
cv.NamedWindow("RevProj", cv.CV_WINDOW_NORMAL)
cv.MoveWindow("RevProj",700,300)
cv.NamedWindow("Util", cv.CV_WINDOW_NORMAL)
cv.MoveWindow("Util", 300, 0)


#This type of matrix seems to work
MATRIX_T = cv2.CV_32FC1
#Training sample dimensions
DIM = 200
#How many samples do we take
SAMPLES = 6

def mah_distance(mat1,mat2,i):
    
    #print mat1 #[0]
    #print mat2
    print "Result ", i
    print numpy.linalg.norm(mat1[0]-mat2)


def flatten(frame,numpymat,seq):
    
        #We don't know the resolution        
        image_size = cv.GetSize(frame)
        
        #Drop colours and change format
        grayscale = cv.CreateImage(image_size, 8, 1)
        cv.CvtColor(frame, grayscale, cv.CV_BGR2GRAY)
        cv.EqualizeHist(grayscale, grayscale)
    
        faces = isolateFaces(grayscale)
        if faces:
            for i in faces:
                    cropped = cropFace(frame,faces,i)
                    frame = markFace(frame,i)

            resized = cv.CreateImage((DIM,DIM),8,3)
            cv.Resize(cropped,resized)
    
            gray = cv.CreateImage((DIM,DIM),8,1)
            cv.CvtColor(resized, gray, 6)
            cv.EqualizeHist(gray,gray)
                            
            #Get the probably non-continuous matrix from captured picture (C-stuff)
            data = cv.GetMat(gray)
            
            
            #Flatten the data to numpy 1D array (FIXME: pure cv2 does not need old cvMat type)
            z = 0
            for x in range(0,data.rows):
                for y in range(0,data.cols):
                    numpymat[seq,z] = data[x,y]
                    #numpymat[z,seq] = data[x,y]
                    z=z+1
        return numpymat




def markFace(frame,i):
    
    i = i[0]
    cv.Rectangle(frame,( int(i[0]),int(i[1]) ), ( (int(i[2])+int(i[0])),(int(i[3])+int(i[1])) ),cv.RGB(255, 0, 0), 1, 8, 0)
    return frame


def cropFace(frame,faces,i):
    
    i = i[0]
    cropped = cv.CreateImage( (i[2], i[3]),8, frame.nChannels)
    src_region = cv.GetSubRect(frame, ( int(i[0]), int(i[1]) , int(i[2]), int(i[3]) ))
    cv.Copy(src_region, cropped)        
    return cropped

def isolateFaces(frame):
    
    #Load the Haar-cascades
        
    #Weird C-stuff, need to manage memory 
    storage = cv.CreateMemStorage(0)
    cascade = cv.Load('haarcascade_frontalface_alt.xml')

    return cv.HaarDetectObjects(frame, cascade, storage, 1.2, 2, cv.CV_HAAR_DO_CANNY_PRUNING,(50, 50))


def takePic():
    
        camera_index = 0
        capture = cv.CaptureFromCAM(camera_index)

        
        #Snap the pic
        #frame = cv.LoadImage("sam.jpg")
        frame = cv.QueryFrame(capture)
        if not frame:
            print("No frame")
            
        #It seems that we're coding in C instead of Python    
        image_size = cv.GetSize(frame) 
        frame2 = cv.CreateImage(image_size, 8, 3)    
        cv.Copy(frame, frame2)
        
        #Flip it for fun
        cv.Flip(frame2, None, 1)
        
        del capture
        return frame2




def repeat():
        
    #Create matrix for flattened images

    data_n = numpy.zeros((SAMPLES,(DIM*DIM)))
    #data_n = numpy.zeros(((DIM*DIM),SAMPLES))
        
    for ref in range(0,SAMPLES):
        
        print "Press space to take picture. Training image:", ref+1

        cv.WaitKey()
        frame = takePic()
        flatten(frame,data_n,ref)
        cv.ShowImage("Original", frame)

    #Is this normalization good?
    print data_n
    cv2.normalize(data_n, data_n, 0, 1, cv2.NORM_MINMAX)


    #Find the eigenvectors. (Do we need the whole set? Might make things slow...)
    mean, eigenvectors = cv2.PCACompute(data_n)
    
    print "Eigenvectors Calculated: ",eigenvectors.size
    print "Mean"
    print mean
    print "Vectors"
    print eigenvectors
    
    #Project the subspaces
    projection = cv2.PCAProject(data_n, mean, eigenvectors)
    print "Projected PCA subspace"

    print "Projection"
    print projection    
    
    rev_projection = cv2.PCABackProject(projection, mean, eigenvectors)
    print "R projection"
    print rev_projection
    
    
    cv2.normalize(rev_projection, rev_projection, 0,1, cv2.NORM_MINMAX)
    
    n_eigens = eigenvectors.copy()
    cv2.normalize(eigenvectors,n_eigens,0,1,cv2.NORM_MINMAX)
    
    cv2.imshow("Average", mean.reshape((DIM,DIM)))
    cv.ShowImage("Original", frame)
    for i in range(1,SAMPLES):
        cv2.imshow("RevProj", rev_projection[i].reshape((DIM,DIM)))
        cv2.imshow("Util", n_eigens[i].reshape((DIM,DIM)))
        cv2.waitKey()
    
    print "Done training. Press any key for recognition test"
    cv2.waitKey()
    
    #The rest is for show
    data_r = numpy.zeros((1,(DIM*DIM)))
    frame2  = takePic()
    cv.ShowImage("Util",frame2)
    flatten(frame2,data_r,0) 
    cv2.normalize(data_r, data_r, 0, 1, cv2.NORM_MINMAX)


    proj = cv2.PCAProject(data_r,mean,eigenvectors)   
    r_proj = cv2.PCABackProject(proj, mean, eigenvectors)
    
    for i in range(0,SAMPLES):
        mah_distance(proj,projection[i],i)
         
    cv2.normalize(r_proj, r_proj, 0,1, cv2.NORM_MINMAX)
    cv.ShowImage("Original",frame2)
    cv2.imshow("RevProj", r_proj.reshape((DIM,DIM)))

    print "DONE"
    cv2.waitKey()

repeat()