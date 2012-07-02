package naamakahvi.android;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import naamakahvi.android.R;

public class NewUserActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
    }
    
    public void onRegistrationClick(View v)  {
		try {
			Client client = new Client("127.0.0.1", 5000, null);
			String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
			String etunimi = ((EditText)findViewById(R.id.editTextEtunimi)).getText().toString();
			String sukunimi = ((EditText)findViewById(R.id.editTextSukunimi)).getText().toString();
			IUser user = client.registerUser(username, etunimi, sukunimi, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
    }
}
