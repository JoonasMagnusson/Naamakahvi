package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
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
		// t�h�n string-taulukkoon importataan clientist�
		// k�ytt�j�lista
		// Varautuminen: ei k�ytt�ji� / ei yhteytt�

		AlphabeticalStringArrayAdapter adapter = new AlphabeticalStringArrayAdapter(this, users);
		userlistView.setAdapter(adapter);
		userlistView.setFastScrollEnabled(true);
		userlistView.setFastScrollAlwaysVisible(true);

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

	private static class AlphabeticalStringArrayAdapter extends BaseAdapter
			implements SectionIndexer {

		private LayoutInflater inflater;
		private String[] data;
		private TreeMap<String,Integer> sections;

		public AlphabeticalStringArrayAdapter(Context con,String[] data) {
			Arrays.sort(data,String.CASE_INSENSITIVE_ORDER);
			inflater = LayoutInflater.from(con);
			sections = new TreeMap<String, Integer>();
			for (int i = 0; i < data.length; ++i) {
				String s = data[i];
				char firstletter = s.toUpperCase().charAt(0);
				if (sections.isEmpty() || sections.lastKey().charAt(0) != firstletter) {
					sections.put(Character.toString(firstletter),i);
				}
			}
			this.data = data;
		}

		public int getPositionForSection(int sectionIndex) {
			return sections.get(sections.keySet().toArray()[sectionIndex]);
		}

		public int getSectionForPosition(int position) {
			if (position < 0 || position >= data.length) return -1;
			Entry<String,Integer> current = sections.firstEntry();
			int sectionIndex = 0;
			while ((current = sections.higherEntry(current.getKey()) ) != null){
				if (position <= current.getValue()) return sectionIndex;
				sectionIndex++;
			}
			return sectionIndex-1;
		}

		public Object[] getSections() {
			return sections.keySet().toArray();
		}

		public int getCount() {
			return data.length;
		}

		public Object getItem(int position) {
			if (position < 0 || position >= data.length) return null;
			return data[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null){
				convertView =inflater.inflate(R.layout.new_list_bigger_text, null);
			}
			String item = (String) getItem(position);
			((TextView)convertView).setText(item);
			return convertView;
		}

	}

}
