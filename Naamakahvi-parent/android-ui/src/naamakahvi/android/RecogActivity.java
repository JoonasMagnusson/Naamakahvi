package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class RecogActivity extends Activity {
	/**
	 * Timer for periodically taking photos
	 */
	class ShotTimer extends Thread {
		private final Handler hand;
		private boolean canceled = false;

		public ShotTimer(final Handler hand) {
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
			return this.canceled;
		}

		@Override
		public void run() {
			final FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);

			try {
				Thread.sleep(1000); // wait for camera to initialize & auto
									// white balance etc.
			} catch (final InterruptedException e2) {
				e2.printStackTrace();
			}

			final String[] namearray = new String[5];
			int identifycount = 0;
			while (!isCanceled()) {
				try {
					Log.d(TAG, "canceled thread: " + isCanceled());

					final Client client = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);

					final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					face.grabFrame().compress(CompressFormat.PNG, 0, bos);
					final byte[] bitmapdata = bos.toByteArray();

					final String user = client.identifyImage(bitmapdata);

					namearray[identifycount] = user;
					Log.d(TAG, "" + user);
					identifycount++;

					if (identifycount == NUM_PHOTOS) {
						cancel();
						final String recoguser = rankNames(namearray);

						this.hand.post(new Runnable() {
							public void run() {
								final Intent i = new Intent();
								i.putExtra(ExtraNames.USERS, recoguser);
								i.putExtra(ExtraNames.PRODUCTS, RecogActivity.this.mOrder);
								setResult(RESULT_OK, i);
								finish();
							}
						});

					}

				} catch (final ClientException e) {
					//Connection problem
					e.printStackTrace();
				} catch (final Exception e) {
					// Zero or too many faces detected (or catastrophic failure)
					e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}

		}

	}

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

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == SELECT_USERNAME) {
			switch (resultCode) {
			case RESULT_OK:
				final Intent i = new Intent();
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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recog_user);

		final FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.openCamera();

		this.mOrder = getIntent().getParcelableExtra(ExtraNames.PRODUCTS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mShotTimer.cancel();
		Log.d(TAG, "canceled: " + this.mShotTimer.isCanceled());
		final FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.releaseCamera();

	}

	@Override
	protected void onResume() {
		super.onResume();
		final FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
		face.openCamera();
		final Handler hand = new Handler(getMainLooper());
		this.mShotTimer = new ShotTimer(hand);
		this.mShotTimer.start();
	}

	private String rankNames(final String[] params) {
		if (params.length < 1) {
			return null;
		} else if (params.length == 1) {
			return params[0];
		}

		final HashMap<String, Integer> namePoints = new HashMap<String, Integer>();

		for (final String name : params) {

			final Integer d = namePoints.get(name);
			if (d == null) {
				namePoints.put(name, 1);
			} else {
				namePoints.put(name, d + 1);
			}

		}
		Map.Entry<String, Integer> max = null;
		for (final Map.Entry<String, Integer> e : namePoints.entrySet()) {
			if (max == null) {
				max = e;
				continue;
			}
			if (e.getValue() > max.getValue()) {
				max = e;
			}
		}

		return (max.getValue() >= NUM_RECOG) ? (String) max.getKey() : null;
	}

}
