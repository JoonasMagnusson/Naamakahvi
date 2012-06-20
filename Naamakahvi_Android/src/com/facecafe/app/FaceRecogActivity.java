package com.facecafe.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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