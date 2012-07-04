package naamakahvi.android;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import naamakahvi.android.R;

public class NewUserActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
    }
    
    public void onRegistrationClick(View v)  {
		try {
//			Client client = new Client("127.0.0.1", 5000, null);
			String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
			String etunimi = ((EditText)findViewById(R.id.editTextEtunimi)).getText().toString();
			String sukunimi = ((EditText)findViewById(R.id.editTextSukunimi)).getText().toString();
//			IUser user = client.registerUser(username, etunimi, sukunimi, null);
			// tarkistetaan onko username varattu. jos on, kirjoitetaan se ja pyydetään uutta else finish()
			Toast.makeText(getApplicationContext(), "Sinut on rekisteröity onnistuneesti nimellä " + username,
					Toast.LENGTH_LONG).show();
			finish();
		}
		catch (Exception ex) {
			Toast.makeText(getApplicationContext(), "Sinua ei voitu rekisteröidä koska asia x ",
					Toast.LENGTH_LONG).show();
			finish();
		}
    }
}
