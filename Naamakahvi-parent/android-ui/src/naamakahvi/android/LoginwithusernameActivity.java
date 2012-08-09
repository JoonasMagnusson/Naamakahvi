package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import naamakahvi.android.R;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
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
	private Basket mOrder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);
		mRes = getResources();

		mInflater = getLayoutInflater();

		mOrder = getIntent().getExtras().getParcelable(ExtraNames.PRODUCTS);

		final Handler hand = new Handler(getMainLooper());

		final Context con = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
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
							AlertDialog.Builder builder = new AlertDialog.Builder(con);
							builder.setCancelable(false);
							builder.setMessage("Fetching data from server failed. Reason: " + ex.getMessage());
							builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = (String) parent.getAdapter().getItem(position);
				if (item == null) return;
				Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
				Intent i = new Intent();
				i.putExtra(ExtraNames.USERS, new String[] { item });
				i.putExtra(ExtraNames.PRODUCTS, mOrder);
				setResult(RESULT_OK, i);
				finish();
			}
		});

	}

	private static class AlphabeticalStringArrayAdapter extends BaseAdapter implements SectionIndexer {

		public static final int TYPE_HEADER = 0, TYPE_USERNAME = 1;

		private LayoutInflater inflater;
		private List<String> data;
		List<String> names;
		List<Integer> indices;

		public AlphabeticalStringArrayAdapter(Context con, String[] data) {
			Arrays.sort(data, String.CASE_INSENSITIVE_ORDER);

			inflater = LayoutInflater.from(con);
			this.data = new ArrayList<String>();
			this.names = new ArrayList<String>();
			this.indices = new ArrayList<Integer>();

			int numSections = 0;

			for (int i = 0; i < data.length; ++i) { // initialize sections
				String s = data[i];
				char firstletter = s.toUpperCase().charAt(0);

				if (isEmpty() || lastSectionName().charAt(0) != firstletter) { // new
																				// section
					this.data.add("-- HEADER -- SHOULD NOT BE VISIBLE --");
					// add placeholder in data for easier indexing
					this.addSection(Character.toString(firstletter), i + numSections);
					++numSections; // keep track of index offset caused by
									// section headers
				}
				this.data.add(s);
			}
		}

		private void addSection(String name, int index) {
			names.add(name);
			indices.add(index);
		}

		public int getPositionForSection(int sectionid) {
			if (sectionid < 0 || sectionid >= indices.size())
				return -1;
			return indices.get(sectionid);
		}

		public int getSectionForPosition(int position) {
			if (position < 0)
				return 0;
			if (position >= data.size())
				return indices.size() - 1;
			for (int i = 1; i < indices.size(); ++i) {
				if (position >= indices.get(i - 1) && position < indices.get(i)) {
					return i - 1;
				}
			}
			return indices.size() - 1;

		}

		/**
		 * Returns the name of the latest added section
		 * 
		 * @return name of latest section
		 */
		private String lastSectionName() {
			if (isEmpty())
				return null;
			else
				return names.get(names.size() - 1);
		}

		public boolean isEmpty() {
			return names.size() == 0;
		}

		public Object[] getSections() {
			return names.toArray();
		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			if (position < 0 || position >= data.size() || (indices.indexOf(position) >= 0))
				return null;
			return data.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return (indices.indexOf(position) >= 0) ? TYPE_HEADER : TYPE_USERNAME;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				if (getItemViewType(position) == TYPE_HEADER)
					convertView = inflater.inflate(R.layout.list_header, null);
				else
					convertView = inflater.inflate(R.layout.new_list_bigger_text, null);
			}
			int headerIndex = indices.indexOf(position);
			if (headerIndex >= 0) {
				((TextView) convertView).setText(names.get(headerIndex));
			} else {
				String item = (String) getItem(position);
				((TextView) convertView).setText(item);
			}

			return convertView;
		}

	}

}
