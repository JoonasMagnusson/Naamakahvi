package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Basket;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.naamakahviclient.IUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

	public static final short USERNAME_SELECTED = 1;
	private int mFastorderUnits = 1;
	private Resources mRes;

	
	private class RecogThread implements Runnable {
		private Intent mAction;

		public RecogThread(Intent i){
			mAction = i;
		}
		
		public void run() {
		  IUser u = doFaceRecog();
		  mAction.putExtra("naamakahvi.android.username", u.getUserName());
		  startActivity(mAction);
		}
	}	
	
	private IUser doFaceRecog(){
		//TODO: server side face recognition
		IUser u = new IUser() {
			public String getUserName() {
				return "Test";
			}
			
			public String getGivenName() {
				return "Senor";
			}
			
			public String getFamilyName() {
				return "Testman";
			}
		   };
		   return u;
	}
	
	private List<IProduct> products(){
		List<IProduct> products = new ArrayList<IProduct>();
		products.add(new IProduct() {
			
			public String getName() {
				return "Kahvi";
			}
			public String toString(){return getName();}
		});
		
        products.add(new IProduct() {
			
			public String getName() {
				return "Espresso";
			}
			public String toString(){return getName();}
		});
        
        products.add(new IProduct() {
			
			public String getName() {
				return "Tuplaespresso";
			}
			public String toString(){return getName();}
		});
       products.add(new IProduct() {
			
			public String getName() {
				return "Tee?";
			}
			public String toString(){return getName();}
		});
        
        return products;
	}
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recog_user);
		mRes=  getResources(); 

		((FaceDetectView) findViewById(R.id.faceDetectView1)).openCamera();
        //Client client = new Client("127.0.0.1", 5000, null);
		// try {
		
		List<IProduct> bestproducts =   products();          ; //client.listBuyableProducts();

		GridView productView = (GridView) findViewById(R.id.gridView1);

		// t�h�n tulee 3? ensimm�ist� / parasta tuotetta

		ArrayAdapter<IProduct> adapter = new ArrayAdapter<IProduct>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				bestproducts);

		productView.setAdapter(adapter);
		
		productView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				IProduct item = (IProduct) parent.getAdapter().getItem(position);
				
				Intent i = new Intent(view.getContext(), ConfirmPurchaseActivity.class);
				Basket b = new Basket();
				b.addProduct(item,mFastorderUnits);
				i.putExtra("naamakahvi.android.products", b);
				String[] testUsers = new String[] {"aapeli", "kahvikonkari", "moikkaaja", "testi"};
				i.putExtra("naamakahvi.android.users", testUsers);
				startActivity(i);
//			    new Thread(new RecogThread(i)).start();   // does server side face recognition, then launches intent
//				
//				Toast.makeText(getApplicationContext(), mFastorderUnits + " x " + item, Toast.LENGTH_LONG)
//						.show();
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
		startActivityForResult(i, USERNAME_SELECTED);
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
		if (mFastorderUnits == -1)
			mFastorderUnits = 1;
		else if (mFastorderUnits < mRes.getInteger(R.integer.MAX_FASTORDER_UNITS)){
			mFastorderUnits++;
		}
		((EditText)findViewById(R.id.noOfUnits)).setText(Integer.toString(mFastorderUnits));
	}

	public void onLessUnitsClick(View v) {
		if (mFastorderUnits > 1 || mFastorderUnits < 0)
			mFastorderUnits--;
		else
			mFastorderUnits = -1;
		((EditText)findViewById(R.id.noOfUnits)).setText(Integer.toString(mFastorderUnits));
	}

	public void captureImg(View v){
		Bitmap bmp = ((FaceDetectView)findViewById(R.id.faceDetectView1)).grabFrame();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == USERNAME_SELECTED) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent(this, MenuActivity.class);
				i.putExtra("naamakahvi.android.selectedUser", data.getExtras().getString("naamakahvi.android.selectedUser"));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}
}
