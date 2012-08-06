package naamakahvi.android;

import java.util.Arrays;
import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IStation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginwithusernameActivity extends Activity {

	public static final String TAG = "LoginwithUsernameActivity";
	private Resources mRes;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);
		mRes = getResources();

		mInflater = getLayoutInflater();

		final Handler hand = new Handler(getMainLooper());

		final Context con = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					List<IStation> s = Client.listStations(Config.SERVER_URL,
							Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,
							Config.SERVER_PORT, s.get(0));
					final String[] users = c.listUsernames();
					Arrays.sort(users, String.CASE_INSENSITIVE_ORDER);
					hand.post(new Runnable() {

						public void run() {
							loaded(users);
						}
					});
				} catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
					hand.post(new Runnable() {

						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									con);
							builder.setCancelable(false);
							builder.setMessage("Fetching data from server failed. Reason: "
									+ ex.getMessage());
							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											finish();
										}
									});
							builder.show();
						}
					});
				}

			}
		}).start();

	}

	public void loaded(String[] users) {
		setContentView(R.layout.loginwithusername);
		ListView userlistView = (ListView) findViewById(R.id.userListView);
		// t�h�n string-taulukkoon importataan clientist� k�ytt�j�lista
		// Varautuminen: ei k�ytt�ji� / ei yhteytt�

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, users);
		userlistView.setAdapter(adapter);

		userlistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = (String) parent.getAdapter().getItem(position);
				Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG)
						.show();
				Intent i = new Intent();
				i.putExtra(ExtraNames.SELECTED_USER, item);
				setResult(RESULT_OK, i);
				finish();
			}
		});

	}

}
