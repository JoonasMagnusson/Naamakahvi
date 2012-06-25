package naamakahvi.android;


import naamakahvi.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class ConfirmActivity extends Activity {
	
	final short COUNTDOWN_LENGTH = 10;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);
        
        TextView nameView = (TextView) findViewById(R.id.cnf_text);
        nameView.setText(getIntent().getStringExtra("prompt_text"));
        
        final TextView countdown = (TextView) findViewById(R.id.rec_countdown);
        countdown.setText(getString(R.string.countdown_prefix) +" "+ COUNTDOWN_LENGTH + getString(R.string.countdown_suffix));
        
        CountDownTimer cd = new CountDownTimer(1000 * COUNTDOWN_LENGTH, 1000) {
			
        	public void onTick(long timeLeft) {
        		countdown.setText(getString(R.string.countdown_prefix) +" "+ timeLeft / 1000 + getString(R.string.countdown_suffix));
        		
        	}

        	public void onFinish() {
        		setResult(RESULT_OK);
            	finish();		
        	}
		};
		cd.start();
    }
    
    public void onOkClick(View v){
    	setResult(RESULT_OK);
    	finish();
    }
    
    public void onCancelClick(View v){
    	setResult(RESULT_CANCELED);
    	finish();
    }

	
    
}