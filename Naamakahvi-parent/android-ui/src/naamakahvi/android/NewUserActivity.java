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
			Client client = new Client("127.0.0.1", 5000);
			String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
			String name = ((EditText)findViewById(R.id.editTextName)).getText().toString();
			IUser user = client.registerUser(username, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
    }
}
