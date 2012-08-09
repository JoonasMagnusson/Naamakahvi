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
	private Basket mOrder;
	private ShotTimer mShotTimer;

	public static final String TAG = "RecogActivity";

	class ShotTimer extends Thread {
		private Handler hand;
		private boolean canceled = false;

		public ShotTimer(Handler hand) {
			this.hand = hand;
		}

		synchronized public void cancel() {
			this.canceled = true;
		}

		synchronized public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void run() {
			FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			final String[][] namearrays = new String[5][];
			int identifycount = 0;
			while (!isCanceled()) {
				try {
					Log.d(TAG, "canceled thread: " + isCanceled());

					Client client = new Client(Config.SERVER_URL,
							Config.SERVER_PORT, Config.STATION);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					face.grabFrame().compress(CompressFormat.PNG, 0, bos);
					byte[] bitmapdata = bos.toByteArray();

					final String[] users = client.identifyImage(bitmapdata);

					namearrays[identifycount] = users;
					identifycount++;

					if (identifycount == 5) {
						this.cancel();

						hand.post(new Runnable() {
							public void run() {
								Intent i = new Intent();
								i.putExtra(ExtraNames.USERS,
										rankNames(namearrays));
								i.putExtra(ExtraNames.PRODUCTS, mOrder);
								setResult(RESULT_OK, i);
								finish();
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
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

	public void userListClick(View v) {
		Intent i = new Intent(this, LoginwithusernameActivity.class);
		i.putExtra(ExtraNames.PRODUCTS, mOrder);
		startActivityForResult(i, SELECT_USERNAME);
	}

	public void onBackClick(View v) {
		this.onBackPressed();
	}

	private class ValueComparator implements Comparator<String> {
		private Map<String, Double> m;

		public ValueComparator(Map<String, Double> m) {
			this.m = m;
		}

		public int compare(String a, String b) {
			if (a == null && b != null)
				return 1;
			if (a != null && b == null)
				return -1;
			if (a == null && b == null)
				return 0;

			if (m.get(a) < m.get(b)) {
				return 1;
			} else if (m.get(a) == m.get(b)) {
				return 0;
			} else {
				return -1;
			}

		}

	}

	private String[] rankNames(String[][] params) {
		if (params.length < 1)
			return null;
		else if (params.length == 1)
			return params[0];

		HashMap<String, Double> namePoints = new HashMap<String, Double>();

		for (String[] names : params) {
			double score = 1.0;
			for (String s : names) {
				Double d = namePoints.get(s);
				if (d == null) {
					namePoints.put(s, score);
				} else {
					namePoints.put(s, d + score);
				}
				score /= 2;
			}
		}
		Comparator<String> comp = new ValueComparator(namePoints);
		TreeMap<String, Double> sortedNames = new TreeMap<String, Double>(comp);
		sortedNames.putAll(namePoints);
		Object[] nameList = sortedNames.keySet().toArray();
		return Arrays.copyOf(nameList, nameList.length, String[].class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_USERNAME) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent();
				i.putExtra(ExtraNames.USERS,
						data.getExtras().getStringArray(ExtraNames.USERS));
				i.putExtra(ExtraNames.PRODUCTS,data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				setResult(RESULT_OK, i);
				finish();
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}

}
