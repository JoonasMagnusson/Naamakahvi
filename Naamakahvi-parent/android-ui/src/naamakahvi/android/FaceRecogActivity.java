package naamakahvi.android;

import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.components.FaceDetectView;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FaceRecogActivity extends Activity {

	private final short REQUEST_CONFIRM = 1;
	private int mFastorderUnits = 1;
	private Resources mRes;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recog_user);
		mRes=  getResources(); 

		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();

		// Client client = new Client("127.0.0.1", 5000, null);
		// try {
		// List<IProduct> bestproducts = client.listBuyableProducts();
		// IProduct bestProduct = bestproducts.get(0);

		GridView productView = (GridView) findViewById(R.id.gridView1);

		// t�h�n tulee 3? ensimm�ist� / parasta tuotetta

		String[] testiTuotteet = new String[] { "Kahvi", "Espresso",
				"Tuplaespresso" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				testiTuotteet);

		productView.setAdapter(adapter);

		productView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = (String) parent.getAdapter().getItem(position);
				Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG)
						.show();
			}
		});

		// } catch (ClientException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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

	public void tmpOnImageButtonClick(View v) {
		Intent i = new Intent(this, ConfirmActivity.class);
		i.putExtra("prompt_text", getString(R.string.rec_string_prefix)
				+ " Test Name\n" + getString(R.string.rec_string_suffix));
		startActivityForResult(i, REQUEST_CONFIRM);
	}

	public void onRegButtonClick(View v) {
		Intent i = new Intent(this, NewUserActivity.class);
		startActivityForResult(i, 0);
	}

	public void onUserListButtonClick(View v) {
		Intent i = new Intent(this, LoginwithusernameActivity.class);
		startActivityForResult(i, 1);
	}

	public void onMoreUnitsClick(View v) {
		if (mFastorderUnits < mRes.getInteger(R.integer.MAX_FASTORDER_UNITS)){
			mFastorderUnits++;
		}
		((EditText)findViewById(R.id.noOfUnits)).setText(Integer.toString(mFastorderUnits));
	}

	public void onLessUnitsClick(View v) {
		if (mFastorderUnits > 1){
			mFastorderUnits--;
		}
		((EditText)findViewById(R.id.noOfUnits)).setText(Integer.toString(mFastorderUnits));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CONFIRM) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent(this, MenuActivity.class);
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}
}
