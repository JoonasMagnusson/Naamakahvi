package naamakahvi.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import naamakahvi.android.R;

public class ConfirmPurchaseActivity extends Activity {

	final short COUNTDOWN_LENGTH = 10;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_purchase);
		
		// here will be returned list of users from client
        ListView possibleUsersListView = (ListView) findViewById(R.id.possibleUsers);
        String[] testUsers = new String[] {"aapeli", "kahvikonkari", "moikkaaja", "testi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, testUsers);
        possibleUsersListView.setAdapter(adapter);
		
        //User clicking a name from list results in changing into another user
        possibleUsersListView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        		int position, long id) {
        		String item = (String) parent.getAdapter().getItem(position);
        		Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
        	}
        }); 
        
        //Countdown -code
		final TextView countdown = (TextView) findViewById(R.id.cp_rec_countdown);
		countdown.setText(getString(R.string.countdown_prefix) + " "
				+ COUNTDOWN_LENGTH + getString(R.string.countdown_suffix));
		
		CountDownTimer cd = new CountDownTimer(1000 * COUNTDOWN_LENGTH, 1000) {
			
			public void onTick(long timeLeft) {
				countdown.setText(getString(R.string.countdown_prefix) + " "
						+ timeLeft / 1000
						+ getString(R.string.countdown_suffix));

			}
			
			public void onFinish() {
				// TODO: tänne client-koodi ostolle
				setResult(RESULT_OK);
				finish();
			}
		};
		cd.start();
	}
	
	public void onCPOkClick(View v) {
		//TODO: client-koodi ostolle
		setResult(RESULT_OK);
		finish();
	}
	
	public void onCPCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
