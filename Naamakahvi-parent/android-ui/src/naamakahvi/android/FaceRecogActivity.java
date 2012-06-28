package naamakahvi.android;

import naamakahvi.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;



public class FaceRecogActivity extends Activity {
	
	private final short REQUEST_CONFIRM = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recog_user);
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