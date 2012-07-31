package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IStation;
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

	public static final String TAG ="RecogActivity";
	
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
			List<IStation> s;
	
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		
			while (!isCanceled()) {
				try {
					Log.d(TAG,"canceled thread: "+isCanceled());
					s = Client.listStations(Config.SERVER_URL,
							Config.SERVER_PORT);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					face.grabFrame().compress(CompressFormat.PNG, 0, bos);
					byte[] bitmapdata = bos.toByteArray();

					Client client = new Client(Config.SERVER_URL,
							Config.SERVER_PORT, s.get(0));

					final String[] users = client.identifyImage(bitmapdata);

					hand.post(new Runnable() {
						public void run() {
							Intent i = new Intent();
							i.putExtra(ExtraNames.USERS, users);
							i.putExtra(ExtraNames.PRODUCTS, mOrder);
							setResult(RESULT_OK, i);
							finish();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(500);
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
		Log.d(TAG,"canceled: "+mShotTimer.isCanceled());
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
		startActivityForResult(i, SELECT_USERNAME);
	}

	public void onBackClick(View v) {
		final Context con = this;
		final Handler hand = new Handler(getMainLooper());
		new Thread(new Runnable() {

			public void run() {
				FaceDetectView face = (FaceDetectView) findViewById(R.id.faceDetectView1);
				List<IStation> s;
				try {
					s = Client.listStations(Config.SERVER_URL,
							Config.SERVER_PORT);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					face.grabFrame().compress(CompressFormat.PNG, 0, bos);
					byte[] bitmapdata = bos.toByteArray();

					Client client = new Client(Config.SERVER_URL,
							Config.SERVER_PORT, s.get(0));

					final String[] users = client.identifyImage(bitmapdata);

					hand.post(new Runnable() {
						public void run() {
							Intent i = new Intent();
							i.putExtra(ExtraNames.USERS, users);
							i.putExtra(ExtraNames.PRODUCTS, mOrder);
							setResult(RESULT_OK, i);
							finish();
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_USERNAME) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent();
				i.putExtra(ExtraNames.USERS, new String[] { data.getExtras()
						.getString(ExtraNames.SELECTED_USER) });
				i.putExtra(ExtraNames.PRODUCTS, mOrder);
				setResult(RESULT_OK, i);
				finish();
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}

}
