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

	private final float RELATIVE_FACE_SIZE = 0.6f;
	private final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

	private int mAbsoluteFaceSize = 0;

	private VideoCapture mCamera;
	private final SurfaceHolder mHolder;

	private Mat mGrabFrame;
	private int mNumFacesInGrabFrame = 0;

	private Mat mRgba;
	private Mat mGray;

	private CascadeClassifier mDetector;

	public FaceDetectView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);

		try {

			// get the cascade file as a file object
			// this is stupid
			final File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			if (CASCADEFILE == null) {
				CASCADEFILE = new File(cascadeDir, "lbpcascade_frontalface.xml");

				if (!CASCADEFILE.exists()) {
					final InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);

					final FileOutputStream os = new FileOutputStream(CASCADEFILE);

					final byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				}
			}

			this.mDetector = new CascadeClassifier(CASCADEFILE.getAbsolutePath());
			if (this.mDetector.empty()) {
				Log.e(this.TAG, "Failed to load cascade classifier");
				this.mDetector = null;
			} else {
				Log.i(this.TAG, "Loaded cascade classifier from " + CASCADEFILE.getAbsolutePath());
			}
			cascadeDir.delete();

		} catch (final IOException e) {
			e.printStackTrace();
			Log.e(this.TAG, "Failed to load cascade. Exception thrown: " + e);
		}

	}

	public Bitmap grabFrame() throws Exception {
		synchronized (this) {

			if (this.mNumFacesInGrabFrame != 1) {
				if (this.mNumFacesInGrabFrame < 1) {
					throw new Exception("No faces detected");
				} else {
					throw new Exception("More than one face detected");
				}
			}

			Bitmap bmp = Bitmap.createBitmap(GRAB_IMAGE_SIDE, GRAB_IMAGE_SIDE, Bitmap.Config.ARGB_8888);

			try {
				Utils.matToBitmap(this.mGrabFrame, bmp);
			} catch (final Exception e) {
				Log.d(this.TAG, "Couldn't map to bitmap: " + e.getMessage());
				bmp.recycle();
				bmp = null;
			}
			Log.d(this.TAG, "Captured bitmap");
			return bmp;
		}
	}

	/**
	 * Open the camera for use
	 */
	public boolean openCamera() {
		synchronized (this) {
			releaseCamera(); // vapautetaan kamera varmuuden vuoksi
			this.mCamera = new VideoCapture(Highgui.CV_CAP_ANDROID + 1);
			// +1 = etukamera, pit�isik� olla konffattavissa?
			if (!this.mCamera.isOpened()) {
				Log.d(this.TAG, "Could not open camera");
				this.mCamera.release();
				this.mCamera = null;
				return false;
			}
			return true;
		}
	}

	public Bitmap processFrame(final VideoCapture vc) {
		vc.retrieve(this.mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		vc.retrieve(this.mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);

		if (this.mAbsoluteFaceSize == 0) {
			final int height = this.mGray.rows();
			if (Math.round(height * this.RELATIVE_FACE_SIZE) > 0) {
				this.mAbsoluteFaceSize = Math.round(height * this.RELATIVE_FACE_SIZE);
			}
		}

		final MatOfRect faces = new MatOfRect();

		if (this.mDetector != null) {
			this.mDetector.detectMultiScale(this.mGray, faces, 1.1, 2, 2, new Size(this.mAbsoluteFaceSize,
					this.mAbsoluteFaceSize), new Size());
		}

		final Rect[] facesArray = faces.toArray();

		for (int i = 0; i < facesArray.length; i++) {
			// tunnistetut naamat k�yd��n l�pi
			Core.rectangle(this.mRgba, facesArray[i].tl(), facesArray[i].br(), this.FACE_RECT_COLOR, 3);
		}

		this.mNumFacesInGrabFrame = facesArray.length;

		if (this.mNumFacesInGrabFrame == 1) {
			this.mGray.copyTo(this.mGrabFrame);
			final Rect face = facesArray[0];
			final Size s = new Size(face.width, face.height);
			final Point p = new Point(face.x + (face.width / 2), face.y + (face.height / 2));
			Imgproc.getRectSubPix(this.mGrabFrame, s, p, this.mGrabFrame);

			Core.flip(this.mGrabFrame, this.mGrabFrame, 1); // 1 = mirror the image
			Imgproc.resize(this.mGrabFrame, this.mGrabFrame, new Size(200, 200)); // resize
		}

		Bitmap bmp = Bitmap.createBitmap(this.mRgba.cols(), this.mRgba.rows(), Bitmap.Config.ARGB_8888);

		Core.flip(this.mRgba, this.mRgba, 1); // 1 = peilaus vaakatasossa
		try {
			Utils.matToBitmap(this.mRgba, bmp);
		} catch (final Exception e) {
			Log.d(this.TAG, "Couldn't map to bitmap: " + e.getMessage());
			bmp.recycle();
			bmp = null;
		}
		return bmp;

	}

	/***
	 * Vapauttaa kameran
	 */
	public void releaseCamera() {
		synchronized (this) {
			if (this.mCamera != null) {
				this.mCamera.release();
				this.mCamera = null;
			}
		}

	}

	public void run() {
		Log.d(this.TAG, "Thread running");
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			Bitmap bmp = null;

			synchronized (this) {
				if (this.mCamera == null) {
					break;
				}

				if (!this.mCamera.grab()) {
					// ei saatu kuvaa
					Log.d(this.TAG, "Couldn't grab frame");
					break;
				}

				bmp = processFrame(this.mCamera);

			}

			if (bmp != null) {
				final Canvas canvas = this.mHolder.lockCanvas();
				if (canvas != null) {
					canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) / 2,
							(canvas.getHeight() - bmp.getHeight()) / 2, null);
					this.mHolder.unlockCanvasAndPost(canvas);
				}
				bmp.recycle();
			}
		}

		synchronized (this) {
			// Explicitly deallocate Mats
			if (this.mRgba != null) {
				this.mRgba.release();
			}
			if (this.mGray != null) {
				this.mGray.release();
			}
			if (this.mGrabFrame != null) {
				this.mGrabFrame.release();
			}

			this.mRgba = null;
			this.mGray = null;
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
	public void setupCamera(final int w, final int h) {
		synchronized (this) {
			if (this.mCamera != null && this.mCamera.isOpened()) {
				final List<Size> sizelist = this.mCamera.getSupportedPreviewSizes();
				int fWidth = w;
				int fHeight = h;
				double minDelta = Double.MAX_VALUE;

				// use the available size closest to the preferred size
				for (final Size s : sizelist) {
					final double tmp = Math.abs(s.height - h);
					if (tmp < minDelta) {
						fHeight = (int) s.height;
						fWidth = (int) s.width;
						minDelta = tmp;
					}
				}

				this.mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, fWidth);
				this.mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, fHeight);

			}
		}
	}

	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		Log.d(this.TAG, "surface change width:" + width + " height:" + height);
		setupCamera(width, height);
	}

	public void surfaceCreated(final SurfaceHolder holder) {
		synchronized (this) {
			this.mRgba = new Mat();
			this.mGray = new Mat();
			this.mGrabFrame = new Mat();
		}
		(new Thread(this)).start();
	}

	public void surfaceDestroyed(final SurfaceHolder holder) {
		releaseCamera();
	}

}
