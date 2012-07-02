package naamakahvi.android;

import java.util.List;

import naamakahvi.android.R;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;



public class FaceRecogActivity extends Activity {
	
	private final short REQUEST_CONFIRM = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recog_user);
//        Client client = new Client("127.0.0.1", 5000, null);
//        try {
//			List<IProduct> bestproducts = client.listBuyableProducts();
//			IProduct bestProduct = bestproducts.get(0);
//			GridView productView = (GridView)findViewById(R.id.gridView1);
//			
//		} catch (ClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    
    public void tmpOnImageButtonClick(View v){
    	Intent i = new Intent(this, ConfirmActivity.class);
    	i.putExtra("prompt_text", getString(R.string.rec_string_prefix) + " Test Name\n"+ getString(R.string.rec_string_suffix));
    	startActivityForResult(i,REQUEST_CONFIRM);
    }
    
    public void tmpOnRegButtonClick(View v){
    	setContentView(R.layout.new_user);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_CONFIRM){
    		switch (resultCode){
    			case RESULT_OK:
    				Intent i = new Intent(this,MenuActivity.class);
    				startActivity(i);
    				break;
    			case RESULT_CANCELED:
    				break;
    		}
    		
    	}
    }
}