package naamakahvi.android;

import naamakahvi.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }
    
    public void onCoffeeClick(View v){
    	order("Coffee");
    }
    
    public void onEspressoClick(View v){
    	order("Espresso");
    }
    
    private void order(String tmp){
    	Intent i = new Intent(this, ConfirmActivity.class);
    	i.putExtra("prompt_text", getString(R.string.chk_order) + "\n1 x " + tmp);
    	startActivityForResult(i,1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1){
    		switch (resultCode){
    			case RESULT_OK:
    				finish();
    				break;
    			case RESULT_CANCELED:
    				break;
    		}
    		
    	}
    }
    
}