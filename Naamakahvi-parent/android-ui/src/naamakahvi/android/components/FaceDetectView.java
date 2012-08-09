package naamakahvi.android.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import naamakahvi.android.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaceDetectView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static File CASCADEFILE = null;

	public static final int GRAB_IMAGE_SIDE = 200;

	private final String TAG = "FaceDetectView";

	private final float RELATIVE_FACE_SIZE = 0.3f;
	private final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

	private int mAbsoluteFaceSize = 0;

	private VideoCapture mCamera;
	private SurfaceHolder mHolder;
	private Mat mGrabFrame;
	private Mat mRgba;
	private Mat mGray;

	private CascadeClassifier mDetector;

	public FaceDetectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);

		try {

			// get the cascade file as a file object
			// this is stupid
			File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			if (CASCADEFILE == null) {
				CASCADEFILE = new File(cascadeDir, "lbpcascade_frontalface.xml");

				if (!CASCADEFILE.exists()) {
					InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);

					FileOutputStream os = new FileOutputStream(CASCADEFILE);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				}
			}

			mDetector = new CascadeClassifier(CASCADEFILE.getAbsolutePath());
			if (mDetector.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mDetector = null;
			} else
				Log.i(TAG, "Loaded cascade classifier from " + CASCADEFILE.getAbsolutePath());
			cascadeDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
		}

	}

	/**
	 * Sets the camera size
	 * 
	 * @param w
	 *            Preferred width of preview
	 * @param h
	 *            Preferred height of preview
	 */
	public void setupCamera(int w, int h) {
		synchronized (this) {
			if (mCamera != null && mCamera.isOpened()) {
				List<Size> sizelist = mCamera.getSupportedPreviewSizes();
				int fWidth = w;
				int fHeight = h;
				double minDelta = Double.MAX_VALUE;

				// use the available size closest to the preferred size
				for (Size s : sizelist) {
					double tmp = Math.abs(s.height - h);
					if (tmp < minDelta) {
						fHeight = (int) s.height;
						fWidth = (int) s.width;
						minDelta = tmp;
					}
				}

				mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, fWidth);
				mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, fHeight);

			}
		}
	}

	/**
	 * Open the camera for use
	 */
	public boolean openCamera() {
		synchronized (this) {
			releaseCamera(); // vapautetaan kamera varmuuden vuoksi
			mCamera = new VideoCapture(Highgui.CV_CAP_ANDROID + 1);
			// +1 = etukamera, pit�isik� olla konffattavissa?
			if (!mCamera.isOpened()) {
				Log.d(TAG, "Could not open camera");
				mCamera.release();
				mCamera = null;
				return false;
			}
			return true;
		}
	}

	public Bitmap grabFrame() throws Exception {
		synchronized (this) {

			MatOfRect faces = new MatOfRect();

			if (mDetector != null)
				mDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
						new Size());

			Rect[] facesArray = faces.toArray();

			if (facesArray.length != 1) {
				if (facesArray.length < 1) {
					throw new Exception("No faces detected");
				} else {
					throw new Exception("More than one face detected");
				}
			}
			Bitmap bmp = Bitmap.createBitmap(GRAB_IMAGE_SIDE, GRAB_IMAGE_SIDE, Bitmap.Config.ARGB_8888);

			Rect face = facesArray[0];

			Size s = new Size(face.width, face.height);
			Point p = new Point(face.x + (face.width / 2), face.y + (face.height / 2));
			Log.d(TAG, "face x:" + face.x + "," + face.y + " face width:" + face.width + " face height:" + face.height
					+ " m size:" + mGrabFrame.cols() + "," + mGrabFrame.rows());
			Imgproc.getRectSubPix(mGrabFrame, s, p, mGrabFrame);

			Core.flip(mGrabFrame, mGrabFrame, 1); // 1 = mirror the image
			Imgproc.resize(mGrabFrame, mGrabFrame, new Size(200, 200)); // resize

			try {
				Utils.matToBitmap(mGrabFrame, bmp);
			} catch (Exception e) {
				Log.d(TAG, "Couldn't map to bitmap: " + e.getMessage());
				bmp.recycle();
				bmp = null;
			}
			Log.d(TAG, "Captured bitmap");
			return bmp;
		}
	}

	/***
	 * Vapauttaa kameran
	 */
	public void releaseCamera() {
		synchronized (this) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}

	}

	public Bitmap processFrame(VideoCapture vc) {
		vc.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		vc.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);

		mGray.copyTo(mGrabFrame);

		if (mAbsoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * RELATIVE_FACE_SIZE) > 0) {
				mAbsoluteFaceSize = Math.round(height * RELATIVE_FACE_SIZE);
			}
		}

		MatOfRect faces = new MatOfRect();

		if (mDetector != null)
			mDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			// tunnistetut naamat k�yd��n l�pi
			Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

		Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);

		Core.flip(mRgba, mRgba, 1); // 1 = peilaus vaakatasossa
		try {
			Utils.matToBitmap(mRgba, bmp);
		} catch (Exception e) {
			Log.d(TAG, "Couldn't map to bitmap: " + e.getMessage());
			bmp.recycle();
			bmp = null;
		}
		return bmp;

	}

	public void run() {
		Log.d(TAG, "Thread running");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			Bitmap bmp = null;

			synchronized (this) {
				if (mCamera == null)
					break;

				if (!mCamera.grab()) {
					// ei saatu kuvaa
					Log.d(TAG, "Couldn't grab frame");
					break;
				}

				bmp = processFrame(mCamera);

			}

			if (bmp != null) {
				Canvas canvas = mHolder.lockCanvas();
				if (canvas != null) {
					canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) / 2,
							(canvas.getHeight() - bmp.getHeight()) / 2, null);
					mHolder.unlockCanvasAndPost(canvas);
				}
				bmp.recycle();
			}
		}

		synchronized (this) {
			// Explicitly deallocate Mats
			if (mRgba != null)
				mRgba.release();
			if (mGray != null)
				mGray.release();
			if (mGrabFrame != null)
				mGrabFrame.release();
			
			mRgba = null;
			mGray = null;
		}

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surface change width:" + width + " height:" + height);
		setupCamera(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			mRgba = new Mat();
			mGray = new Mat();
			mGrabFrame = new Mat();
		}
		(new Thread(this)).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

}
