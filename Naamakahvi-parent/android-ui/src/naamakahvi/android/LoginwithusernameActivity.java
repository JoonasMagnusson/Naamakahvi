package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.DialogHelper;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class LoginwithusernameActivity extends Activity {

	/**
	 * Adapter that sorts a string array alphabetically and splits it into
	 * sections
	 */
	private static class AlphabeticalStringArrayAdapter extends BaseAdapter implements SectionIndexer {

		public static final int TYPE_HEADER = 0, TYPE_USERNAME = 1;

		private final LayoutInflater inflater;
		private final List<String> data;
		List<String> names;
		List<Integer> indices;

		public AlphabeticalStringArrayAdapter(final Context con, final String[] data) {
			Arrays.sort(data, String.CASE_INSENSITIVE_ORDER);

			this.inflater = LayoutInflater.from(con);
			this.data = new ArrayList<String>();
			this.names = new ArrayList<String>(); // keeps track of section names
			this.indices = new ArrayList<Integer>(); // keeps track of section start points
														// section names[i] starts at index indices[i]
			int numSections = 0;

			for (int i = 0; i < data.length; ++i) { // initialize sections
				String s = data[i];
				if (s.equals("")) {
					s = "a";
				}
				final char firstletter = s.toUpperCase().charAt(0);

				if (isEmpty() || lastSectionName().charAt(0) != firstletter) { // new section
					this.data.add("-- HEADER -- SHOULD NOT BE VISIBLE --"); // add placeholder in data for easier indexing
					addSection(Character.toString(firstletter), i + numSections);
					++numSections; // keep track of index offset caused by section headers
				}
				this.data.add(s);
			}
		}

		/**
		 * Adds a section
		 * 
		 * @param name
		 *            Section name
		 * @param index
		 *            section index
		 */
		private void addSection(final String name, final int index) {
			this.names.add(name);
			this.indices.add(index);
		}

		public int getCount() {
			return this.data.size();
		}

		public Object getItem(final int position) {
			if (position < 0 || position >= this.data.size() || (this.indices.indexOf(position) >= 0)) {
				return null;
			}
			return this.data.get(position);
		}

		public long getItemId(final int position) {
			return position;
		}

		@Override
		public int getItemViewType(final int position) {
			return (this.indices.indexOf(position) >= 0) ? TYPE_HEADER : TYPE_USERNAME;
		}

		public int getPositionForSection(final int sectionid) {
			if (sectionid < 0 || sectionid >= this.indices.size()) {
				return -1;
			}
			return this.indices.get(sectionid);
		}

		public int getSectionForPosition(final int position) {
			if (position < 0) {
				return 0;
			}
			if (position >= this.data.size()) {
				return this.indices.size() - 1;
			}
			for (int i = 1; i < this.indices.size(); ++i) {
				if (position >= this.indices.get(i - 1) && position < this.indices.get(i)) {
					return i - 1;
				}
			}
			return this.indices.size() - 1;

		}

		public Object[] getSections() {
			return this.names.toArray();
		}

		public View getView(final int position, View convertView, final ViewGroup parent) {
			if (convertView == null) {
				if (getItemViewType(position) == TYPE_HEADER) {
					convertView = this.inflater.inflate(R.layout.list_header, null);
				} else {
					convertView = this.inflater.inflate(R.layout.list_item_text, null);
				}
			}

			final int headerIndex = this.indices.indexOf(position);

			if (headerIndex >= 0) {
				((TextView) convertView).setText(this.names.get(headerIndex));
			} else {
				final String item = (String) getItem(position);
				((TextView) convertView).setText(item);
			}

			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEmpty() {
			return this.names.size() == 0;
		}

		/**
		 * Returns the name of the latest added section
		 * 
		 * @return name of latest section
		 */
		private String lastSectionName() {
			if (isEmpty()) {
				return null;
			} else {
				return this.names.get(this.names.size() - 1);
			}
		}

	}

	public static final String TAG = "LoginwithUsernameActivity";

	private Basket mOrder;

	/**
	 * Called after fetching data from server
	 * 
	 * @param users
	 *            List of usernames from the server
	 */
	public void loaded(final String[] users) {
		setContentView(R.layout.loginwithusername);
		final ListView userlistView = (ListView) findViewById(R.id.userListView);

		final AlphabeticalStringArrayAdapter adapter = new AlphabeticalStringArrayAdapter(this, users);
		userlistView.setAdapter(adapter);
		userlistView.setFastScrollEnabled(true);
		userlistView.setFastScrollAlwaysVisible(true);

		userlistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final String item = (String) parent.getAdapter().getItem(position);
				if (item == null) {
					return;
				}
				final Intent i = new Intent();
				i.putExtra(ExtraNames.USERS, item);
				i.putExtra(ExtraNames.PRODUCTS, LoginwithusernameActivity.this.mOrder);
				setResult(RESULT_OK, i);
				finish();
			}
		});

	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		if (getIntent().hasExtra(ExtraNames.PRODUCTS)) {
			this.mOrder = getIntent().getExtras().getParcelable(ExtraNames.PRODUCTS);
		} else {
			this.mOrder = null;
		}

		final Handler hand = new Handler(getMainLooper());

		final Context con = this;
		final Activity act = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					final Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
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

							DialogHelper
									.errorDialog(con, con.getString(R.string.errorFetchData) + ex.getMessage(), act)
									.show();

						}
					});
				}

			}
		}).start();

	}

}
