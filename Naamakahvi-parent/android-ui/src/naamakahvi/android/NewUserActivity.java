package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import naamakahvi.android.R;
import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.DialogHelper;
import naamakahvi.android.utils.ThumbAdapter;

public class NewUserActivity extends Activity {
	private final String TAG = "NewUserActivity";
	private List<Bitmap> mPics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);

		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();

		mPics = new ArrayList<Bitmap>();

		GridView thumbs = (GridView) findViewById(R.id.thumbGrid);

		thumbs.setAdapter(new ThumbAdapter(this, mPics));

		thumbs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ThumbAdapter a = (ThumbAdapter) parent.getAdapter();
				if (a.getItem(position) != null) {

					Bitmap b = mPics.remove(position);
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

	@Override
	protected void onResume() {
		super.onResume();
		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();
	}

	/**
	 * OnClick handler for the "OK" button
	 * 
	 * @param v
	 *            The view that handled the onClick event
	 */
	public void onRegistrationClick(View v) {

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

					Client client = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
					client.registerUser(username, firstname, lastname);

					for (Bitmap b : mPics) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						b.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
						byte[] bitmapdata = bos.toByteArray();

						client.addImage(username, bitmapdata);
					}

					hand.post(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), con.getString(R.string.successRegistration) + username,
									Toast.LENGTH_LONG).show();
						}
					});

					finish();

				} catch (final Exception ex) {
					hand.post(new Runnable() {
						public void run() {
							pd.dismiss();
						}
					});
					Log.d(TAG, ex.getMessage());
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

	public void addPicture(View v) {
		if (mPics.size() < 6) {
			Bitmap bmp;
			try {
				bmp = ((FaceDetectView) findViewById(R.id.faceDetectView1)).grabFrame();
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.getMessage());
				e.printStackTrace();

				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			mPics.add(bmp);
			GridView g = (GridView) findViewById(R.id.thumbGrid);
			((BaseAdapter) g.getAdapter()).notifyDataSetChanged();
		}
	}

}
