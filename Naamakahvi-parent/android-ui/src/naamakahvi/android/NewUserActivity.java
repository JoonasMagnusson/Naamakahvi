package naamakahvi.android;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import naamakahvi.android.R;

public class NewUserActivity extends Activity {
	private final String TAG ="NewUserActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
    }
    
    public void onRegistrationClick(View v)  {
		try {
			Client client = new Client("127.0.0.1", 5000, null);
			String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
			String etunimi = ((EditText)findViewById(R.id.editTextEtunimi)).getText().toString();
			String sukunimi = ((EditText)findViewById(R.id.editTextSukunimi)).getText().toString();
			
			IUser user = client.registerUser(username, etunimi, sukunimi, null);
			// tarkistetaan onko username varattu. jos on, kirjoitetaan se ja pyydet��n uutta else finish()
			Toast.makeText(getApplicationContext(), "Sinut on rekister�ity onnistuneesti nimell� " + username,
					Toast.LENGTH_LONG).show();
			finish();
		}
        catch (Exception ex) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Log.d(TAG, "Exception: " + ex.getMessage());
			ex.printStackTrace();
			builder.setMessage("Registration failed. Reason: " + ex.getMessage()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
		}
    }
}
