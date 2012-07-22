package naamakahvi.android;

import java.util.ArrayList;
import java.util.List;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
		// TODO Auto-generated method stub
		super.onPause();
		((FaceDetectView) findViewById(R.id.faceDetectView1)).releaseCamera();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();
	}

	public void onRegistrationClick(View v) {
		try {
			Client client = new Client("127.0.0.1", 5000, null);
			String username = ((EditText) findViewById(R.id.editTextUsername))
					.getText().toString();
			String etunimi = ((EditText) findViewById(R.id.editTextEtunimi))
					.getText().toString();
			String sukunimi = ((EditText) findViewById(R.id.editTextSukunimi))
					.getText().toString();

			IUser user = client.registerUser(username, etunimi, sukunimi, null);
			// tarkistetaan onko username varattu. jos on, kirjoitetaan se ja
			// pyydet��n uutta else finish()
			Toast.makeText(
					getApplicationContext(),
					"Sinut on rekister�ity onnistuneesti nimell� "
							+ username, Toast.LENGTH_LONG).show();
			finish();
		} catch (Exception ex) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Log.d(TAG, "Exception: " + ex.getMessage());
			ex.printStackTrace();
			builder.setMessage(
					"Registration failed. Reason: " + ex.getMessage())
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			builder.show();
		}
	}

	public void addPicture(View v) {
		if (mPics.size() < 6) {
			Bitmap bmp;
			try {
				bmp = ((FaceDetectView) findViewById(R.id.faceDetectView1))
						.grabFrame();
			} catch (Exception e) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				Log.d(TAG, "Exception: " + e.getMessage());
				e.printStackTrace();
				
				builder.setMessage("Error: " + e.getMessage())
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
				builder.show();
				return;
			}
			mPics.add(bmp);
			GridView g = (GridView) findViewById(R.id.thumbGrid);
			((BaseAdapter) g.getAdapter()).notifyDataSetChanged();
		}
	}

	public class ThumbAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public ThumbAdapter(Context context, List<Bitmap> data) {
			this.inflater = LayoutInflater.from(context);
			this.mBitmaps = data;
		}

		private List<Bitmap> mBitmaps;

		public int getCount() {
			return 6;
		}

		public Bitmap getItem(int position) {
			if (position < 0 || position >= mBitmaps.size())
				return null;
			return mBitmaps.get(position);
		}

		public long getItemId(int position) {
			if (position < getCount() && position >= 0) {
				return position;
			}
			return -1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final Bitmap bmp = getItem(position);

			if (convertView == null) {
				convertView = this.inflater
						.inflate(R.layout.thumb_layout, null);
			}

			ImageView thumb = ((ImageView) convertView
					.findViewById(R.id.imageView1));
			thumb.setImageBitmap(bmp);
			thumb.setMaxHeight(100);
			return convertView;
		}

	}

}
