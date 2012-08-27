package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.DialogHelper;
import naamakahvi.android.utils.ThumbAdapter;
import naamakahvi.naamakahviclient.Client;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

public class NewUserActivity extends Activity {
	private final String TAG = "NewUserActivity";
	private List<Bitmap> mPics;

	/**
	 * onClick handler for camera preview
	 */
	public void addPicture(final View v) {
		if (this.mPics.size() < 6) {
			Bitmap bmp;
			try {
				bmp = ((FaceDetectView) findViewById(R.id.faceDetectView1)).grabFrame();
			} catch (final Exception e) {
				Log.d(this.TAG, "Exception: " + e.getMessage());
				e.printStackTrace();

				DialogHelper.makeToast(this, e.getMessage()).show();
				return;
			}
			this.mPics.add(bmp);
			final GridView g = (GridView) findViewById(R.id.thumbGrid);
			((BaseAdapter) g.getAdapter()).notifyDataSetChanged();
		}
		((Button) findViewById(R.id.clientRegisterButton)).setEnabled(this.mPics.size() == 6 || this.mPics.size() == 0);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);

		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();

		this.mPics = new ArrayList<Bitmap>();

		final GridView thumbs = (GridView) findViewById(R.id.thumbGrid);

		thumbs.setAdapter(new ThumbAdapter(this, this.mPics));

		thumbs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final ThumbAdapter a = (ThumbAdapter) parent.getAdapter();
				if (a.getItem(position) != null) {

					final Bitmap b = NewUserActivity.this.mPics.remove(position);
					b.recycle();
					a.notifyDataSetChanged();
				}

			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		((FaceDetectView) findViewById(R.id.faceDetectView1)).releaseCamera();

	}

	/**
	 * OnClick handler for the "OK" button
	 * 
	 * @param v
	 *            The view that handled the onClick event
	 */
	public void onRegistrationClick(final View v) {

		final String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
		final String firstname = ((EditText) findViewById(R.id.editTextEtunimi)).getText().toString();
		final String lastname = ((EditText) findViewById(R.id.editTextSukunimi)).getText().toString();

		if (username.equals("")) {
			DialogHelper.errorDialog(this, getString(R.string.pleaseUsername)).show();
			return;
		}
		if (firstname.equals("")) {
			DialogHelper.errorDialog(this, getString(R.string.pleaseFirstName)).show();
			return;
		}
		if (lastname.equals("")) {
			DialogHelper.errorDialog(this, getString(R.string.pleaseLastName)).show();
			return;
		}

		final Handler hand = new Handler(getMainLooper());
		final Context con = this;

		final ProgressDialog pd = new ProgressDialog(con);
		pd.setMessage(getString(R.string.pleasewait));
		pd.setIndeterminate(true);
		pd.setCancelable(false);

		new Thread(new Runnable() {

			public void run() {
				try {

					hand.post(new Runnable() {
						public void run() {
							pd.show();
						}
					});

					final Client client = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
					client.registerUser(username, firstname, lastname);

					for (final Bitmap b : NewUserActivity.this.mPics) {
						final ByteArrayOutputStream bos = new ByteArrayOutputStream();
						b.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
						final byte[] bitmapdata = bos.toByteArray();

						client.addImage(username, bitmapdata);
					}

					hand.post(new Runnable() {
						public void run() {
							DialogHelper.makeToast(getApplicationContext(), R.string.successRegistration).show();
						}
					});

					finish();

				} catch (final Exception ex) {
					hand.post(new Runnable() {
						public void run() {
							pd.dismiss();
						}
					});

					Log.d(NewUserActivity.this.TAG, ex.getMessage());
					ex.printStackTrace();

					hand.post(new Runnable() {
						public void run() {
							DialogHelper.errorDialog(con, con.getString(R.string.errorCantRegister) + ex.getMessage())
									.show();
						}
					});
				}

				hand.post(new Runnable() {
					public void run() {
						pd.dismiss();
					}
				});

			}
		}).start();

	}

	@Override
	protected void onResume() {
		super.onResume();
		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();
	}

}
