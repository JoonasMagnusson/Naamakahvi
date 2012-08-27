package naamakahvi.android;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import naamakahvi.android.R;
import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.DialogHelper;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.android.utils.ThumbAdapter;

public class UserSettingsActivity extends Activity {
	private final String TAG = "NewUserActivity";
	private List<Bitmap> mPics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);

		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();

		mPics = new ArrayList<Bitmap>();

		((TextView) findViewById(R.id.textView1)).setText(R.string.user_settings);

		GridView thumbs = (GridView) findViewById(R.id.thumbGrid);

		thumbs.setAdapter(new ThumbAdapter(this, mPics));

		thumbs.setOnItemClickListener(new AdapterView.OnItemClickListener() { // set thumbnail onlcick listener
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ThumbAdapter a = (ThumbAdapter) parent.getAdapter();
				if (a.getItem(position) != null) {

					Bitmap b = mPics.remove(position);
					b.recycle();
					a.notifyDataSetChanged();
				}

			}
		});

		final Handler hand = new Handler(getMainLooper());
		final Intent i = getIntent();
		final Context con = this;
		final Activity act = this;

		new Thread(new Runnable() { // fetch user data

					public void run() {
						try {
							Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
							final IUser user = c.getUser(i.getExtras().getString(ExtraNames.USERS));

							hand.post(new Runnable() {
								public void run() {
									loaded(user);
								}
							});
						} catch (final ClientException e) {
							hand.post(new Runnable() {

								public void run() {
									DialogHelper.errorDialog(con, "Unable to fetch user data : " + e.getMessage(), act)
											.show();
								}
							});

							e.printStackTrace();
						}
					}
				}).start();

	}

	/**
	 * Called after data for this screen has been fetched from the server.
	 * 
	 * @param user
	 *            The user whose photos are being updated
	 */
	private void loaded(IUser user) {

		EditText edit = ((EditText) findViewById(R.id.editTextUsername));
		edit.setEnabled(false);
		edit.setText(user.getUserName());

		edit = ((EditText) findViewById(R.id.editTextEtunimi));
		edit.setEnabled(false);
		edit.setText(user.getGivenName());

		edit = ((EditText) findViewById(R.id.editTextSukunimi));
		edit.setEnabled(false);
		edit.setText(user.getFamilyName());
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
		final Handler hand = new Handler(getMainLooper());
		final Context con = this;

		final ProgressDialog pd = new ProgressDialog(con);
		pd.setMessage("Please Wait...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);

		new Thread(new Runnable() { // update user photos

					public void run() {
						try {

							hand.post(new Runnable() {
								public void run() {
									pd.show();
								}
							});

							Client client = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);

							final String username = ((EditText) findViewById(R.id.editTextUsername)).getText()
									.toString();

							for (Bitmap b : mPics) {
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								b.compress(CompressFormat.PNG, 0, bos);
								byte[] bitmapdata = bos.toByteArray();
								client.addImage(username, bitmapdata);
							}

							hand.post(new Runnable() {

								public void run() {
									Toast.makeText(getApplicationContext(), "Updated photos for " + username,
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
									DialogHelper.errorDialog(con, "Unable to update photos: " + ex.getMessage()).show();
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

	/**
	 * OnClick handler for camera preview
	 * 
	 * @param v
	 *            The view that handled the onClick event
	 */
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

		((Button) findViewById(R.id.clientRegisterButton)).setEnabled(mPics.size() == 6);

	}

}
