package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.Timer;
import java.util.TimerTask;

import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class RecogActivity extends Activity {
	public static final short SELECT_USERNAME = 0;
	/**
	 * Number of photos to take while identifying user
	 */
	public static final int NUM_PHOTOS = 5;
	/**
	 * Number of times user has to be recognized correctly
	 */
	public static final int NUM_RECOG = 3;

	private Basket mOrder;
	private ShotTimer mShotTimer;

	public static final String TAG = "RecogActivity";

	/**
	 * Timer for periodically taking photos
	 */
	class ShotTimer extends Thread {
		private Handler hand;
		private boolean canceled = false;

		public ShotTimer(Handler hand) {
			this.hand = hand;
		}

		/**
		 * Cancels the timer
		 */
		synchronized public void cancel() {
			this.canceled = true;
		}

		/**
		 * Returns whether the timer is canceled
		 * 
		 * @return True if canceled, false if still running
		 */
		synchronized public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void run() {
			FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);

			try {
				Thread.sleep(1000); // wait for camera to initialize & auto
									// white balance etc.
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}

			final String[] namearray = new String[5];
			int identifycount = 0;
			while (!isCanceled()) {
				try {
					Log.d(TAG, "canceled thread: " + isCanceled());

					Client client = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					face.grabFrame().compress(CompressFormat.PNG, 0, bos);
					byte[] bitmapdata = bos.toByteArray();

					final String user = client.identifyImage(bitmapdata);

					namearray[identifycount] = user;
					Log.d(TAG,"" + user);
					identifycount++;

					if (identifycount == NUM_PHOTOS) {
						this.cancel();
						final String recoguser = rankNames(namearray);

						hand.post(new Runnable() {
							public void run() {
								Intent i = new Intent();
								i.putExtra(ExtraNames.USERS, recoguser);
								i.putExtra(ExtraNames.PRODUCTS, mOrder);
								setResult(RESULT_OK, i);
								finish();
							}
						});

					}

				} catch (ClientException e) {
					//Connection problem
					e.printStackTrace();
				} catch (Exception e) {
					// Zero or too many faces detected (or catastrophic failure)
					e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recog_user);

		FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.openCamera();

		mOrder = getIntent().getParcelableExtra(ExtraNames.PRODUCTS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mShotTimer.cancel();
		Log.d(TAG, "canceled: " + mShotTimer.isCanceled());
		FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.releaseCamera();

	}

	@Override
	protected void onResume() {
		super.onResume();
		FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.openCamera();
		final Handler hand = new Handler(getMainLooper());
		mShotTimer = new ShotTimer(hand);
		mShotTimer.start();
	}

	private String rankNames(String[] params) {
		if (params.length < 1)
			return null;
		else if (params.length == 1)
			return params[0];

		HashMap<String, Integer> namePoints = new HashMap<String, Integer>();

		for (String name : params) {

			Integer d = namePoints.get(name);
			if (d == null) {
				namePoints.put(name, 1);
			} else {
				namePoints.put(name, d + 1);
			}

		}
		Map.Entry<String, Integer> max = null;
		for (Map.Entry<String, Integer> e : namePoints.entrySet()) {
			if (max == null) {
				max = e;
				continue;
			}
			if (e.getValue() > max.getValue())
				max = e;
		}

		return (max.getValue() >= NUM_RECOG) ? (String) max.getKey() : null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_USERNAME) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent();
				i.putExtra(ExtraNames.USERS, data.getExtras().getString(ExtraNames.USERS));
				i.putExtra(ExtraNames.PRODUCTS, data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				setResult(RESULT_OK, i);
				finish();
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}

}
