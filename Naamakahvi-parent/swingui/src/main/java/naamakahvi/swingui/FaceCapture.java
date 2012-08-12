package naamakahvi.swingui;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/**A face recognition class utilizing the JavaCV library. A FaceCapture
 * object reads images from a camera attached to the computer and draws
 * them on one or more associated FaceCanvas objects. The object can also
 * detect faces from the camera input and pass them to other objects.
 * 
 * @author Antti Hietasaari
 *
 */
public class FaceCapture implements Runnable{

	private FrameGrabber grab;
	private IplImage img = null, gray = null;
	private CvSeq detectedFaces = null;
	private CvHaarClassifierCascade classifier;
	private CvMemStorage store;
	private boolean takePic = false;
	private boolean doFaceDetect = false;
	private ArrayList<FaceCanvas> canvases;
	private AffineTransformOp flip;
	private BufferedImage returnImage = null;
	private boolean offline = false;
	private boolean noInterrupt = false;
	private double ratio = 4/3;
	
	/**Creates a new FaceCapture object that can be started with the run()
	 * method. The object will capture images from camera 0 and will not
	 * do active face recognition.
	 */
	protected FaceCapture(){
		initialize(0, false, false);
	}
	
	/**Creates a new FaceCapture object that can be started with the run()
	 * method.
	 * 
	 * @param camera		An integer designating the camera to be used
	 * 						by the FaceCapture object.
	 * @param doFaceDetect	Indicates whether the FaceCapture object should
	 * 						do active face recognition. If set to true, the
	 * 						object will detect faces from every frame recorded
	 * 						by the camera and draw a bounding box around
	 * 						all faces while drawing the image on screen.
	 * 						If set to false, the object will only do face
	 * 						detection when asked to detect currently visible
	 * 						faces via the takePic() method.
	 * @param offline		A boolean specifying whether the object should
	 * 						receive input from a camera or not. If true is
	 * 						passed, the created object will not attempt to
	 * 						receive camera input of do face detection, but
	 * 						will behave otherwise normally. This parameter
	 * 						is mainly intended for use in testing or situations
	 * 						where the OpenCV library is not available.
	 */
	protected FaceCapture(int camera, boolean doFaceDetect, boolean offline){
		initialize(camera, doFaceDetect, offline);
	}
	
	private void initialize(int camera, boolean doFaceDetect, boolean offline){
		canvases = new ArrayList<FaceCanvas>();
		this.doFaceDetect = doFaceDetect;
		this.offline = offline;
		if (offline){
			return;
		}
		try{
			//The haar cascade must be located in the same folder as
			//FaceCapture.class in order to be loaded properly
			File haarFile = Loader.extractResource("haarcascade_frontalface_alt.xml", null, null, null);
			if (!haarFile.exists()){
				throw new IOException();
			}
			classifier = new CvHaarClassifierCascade(cvLoad(haarFile.getAbsolutePath()));
			
		}
		catch (NullPointerException e){
			System.err.println("Error: failed to locate haarcascade_frontalface_alt.xml\n" +
					"Camera functionality disabled");
			e.printStackTrace();
			offline = true;
		}
		catch (IOException e){
			System.err.println("Error: failed to load haarcascade_frontalface_alt.xml\n" +
					"Camera functionality disabled");
			e.printStackTrace();
			offline = true;
		}
		
		try {
			store = CvMemStorage.create();
			grab = FrameGrabber.createDefault(camera);
		}
		catch (FrameGrabber.Exception e){
			System.err.println("Error: failed to initialize camera " + camera +"\n" +
					"Please confirm that the camera is connected and online\n" +
					"Camera functionality disabled");
			offline = true;
		}
		
	}
	
	/**Creates a new FaceCanvas UI component associated with this FaceCapture
	 * object. After the FaceCanvas component has been activated with its
	 * activate() method, it will show images captured by this FaceCapture
	 * object's associated camera.
	 * 
	 * @return		the newly created FaceCanvas object
	 */
	protected FaceCanvas getCanvas(){
		FaceCanvas c = new FaceCanvas();
		canvases.add(c);
		return c;
	}
	/**Removes a FaceCanvas object from this FaceCapture object's list of
	 * associated canvases. Afterwards, the FaceCanvas will no longer
	 * receive any images from this FaceCapture object.
	 * 
	 * @param c		the FaceCanvas object to be deactivated
	 */
	protected void deactivateCanvas(FaceCanvas c){
		canvases.remove(c);
	}

	/**Activates this FaceCapture object.
	 * 
	 */
	public void run() {
		if (offline)
			return;
		try{
			
			grab.setImageWidth(640);
			grab.setImageHeight(480);
			grab.start();
			img = grab.grab();
			ratio = (double)img.width()/(double)img.height();
			gray = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
			
			AffineTransform trans = AffineTransform.getScaleInstance(-1, 1);
			trans.translate(-img.width(), 0);
			flip = new AffineTransformOp(trans, 
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			
			while (true){
				grab.grab();
				if (checkActiveCanvases()){
					img = grab.grab();
					if (doFaceDetect || takePic){
						//System.out.println("finding faces");
						findFaces();
						//System.out.println("faces found");
					}
					if (takePic){
						//System.out.println("saving picture");
						savePic();
						//System.out.println("picture saved");
					}
					Iterator<FaceCanvas> i = canvases.iterator();
					while (i.hasNext())
						i.next().repaint();
					
					if (doFaceDetect || takePic)
						cvClearMemStorage(store);
					noInterrupt = false;
				}
			}
		}
		catch (FrameGrabber.Exception e){
			System.err.println("Error: did not receive a new frame from camera");
			e.printStackTrace();
		}
	}
	
	private boolean checkActiveCanvases(){
		if (noInterrupt && !takePic){
			return false;
		}
		if (takePic){
			return true;
		}
		Iterator<FaceCanvas> i = canvases.iterator();
		FaceCanvas c;
		while (i.hasNext()){
			c = i.next();
			if (c.active && c.ready){
				noInterrupt = true;
				return true;
			}
				
		}
		return false;
	}
	
	/**Attempts to detect faces currently visible on the camera.
	 * 
	 * @return		A grayscale BufferedImage containing an image of a face
	 * 				detected by the camera. If there are multiple faces
	 * 				visible, the largest one will be returned. Returns null
	 * 				if there are no visible faces or the FaceCapture object
	 * 				has not been properly initialized.
	 */
	protected BufferedImage takePic(){
		if (offline){
			return null;
		}
		takePic = true;
		while (takePic);
		return returnImage;
	}
	
	private void savePic(){
		
		BufferedImage largest = null;
		for (int i = 0;  i < detectedFaces.total(); i++){
			CvRect r = new CvRect(cvGetSeqElem(detectedFaces, i));
			BufferedImage crop = gray.getBufferedImage().getSubimage(r.x(), r.y(), r.width(), r.height());
			
			if (largest == null || 
					largest.getWidth()*largest.getHeight() < 
					crop.getWidth() * crop.getHeight()){
				largest = crop;
			}
		}
		//Uncomment to write detected faces to disk
		//System.out.println(detectedFaces.total() + " picture(s) taken!");
		//File output = new File("face.png");
		//ImageIO.write(largest, "png", output);
		
		takePic = false;
		
		returnImage = largest;
		
	}
	
	private void findFaces(){
		opencv_imgproc.cvCvtColor(img, gray, CV_BGR2GRAY);
		detectedFaces = opencv_objdetect.cvHaarDetectObjects(
				gray, classifier, store, 
				1.1, 3, opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING);
		
	}
	
	/**A user interface component that shows images captured by a FaceCapture
	 * object.
	 * IMPORTANT: a FaceCanvas object should always be created using the
	 * getCanvas() method of a FaceCapture object. FaceCanvas objects created
	 * by directly invoking the constructor will not display an image.
	 * 
	 * @author Antti Hietasaari
	 *
	 */
	protected class FaceCanvas extends JPanel{
		protected boolean active = false;
		protected boolean ready = true;
		
		
		public void paint(Graphics g){
			
			
			if (img != null && active && ready && !offline){
				ready = false;
				BufferedImage bimg = img.getBufferedImage();
				
				if (doFaceDetect && detectedFaces != null){
					Graphics2D gimg = bimg.createGraphics();
					gimg.setColor(Color.RED);
					gimg.setStroke(new BasicStroke(3.0F));
					for (int i = 0;  i < detectedFaces.total(); i++){
						CvRect r = new CvRect(cvGetSeqElem(detectedFaces, i));
						gimg.drawRect(r.x(), r.y(), r.width(), r.height());
					}
					gimg.dispose();
				}
				int x = Math.min(this.getWidth(), (int)(this.getHeight()*ratio));
				int y = Math.min(this.getHeight(), (int)(this.getWidth()/ratio));
				BufferedImage resize = new BufferedImage(x,
						y, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D gimg = resize.createGraphics();
				gimg.drawImage(flip.filter(bimg, null), 0, 0, x, y, null);
				gimg.dispose();
				
				g.drawImage(resize, 0,0,null);
				
				ready = true;
			}

			g.dispose();
		}
		public void update(Graphics g){
			paint(g);
		}
		
		/**
		 * Activates this FaceCanvas object. Newly created FaceCanvas objects
		 * default to a deactivated state, so this method must be called in
		 * order to display an image on the FaceCanvas.
		 */
		protected void activate(){
			active = true;
		}
		/**
		 * Deactivates a FaceCanvas object, preventing its associated
		 * FaceCapture object from drawing any new images on it. FaceCanvas
		 * objects that are not visible to the user should be deactivated
		 * in order to conserve processing power.
		 */
		protected void deactivate(){
			active = false;
		}
	}
}
