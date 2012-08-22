package naamakahvi.android;

import java.util.List;
import java.util.prefs.Preferences;

import naamakahvi.android.R;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.android.utils.ProductCache;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final short REQUEST_LOGIN = 1, REQUEST_USER_SETTINGS = 0;
	public static final boolean MODE_BUY = true, MODE_BRING = false;
	public static final String TAG = "MainActivity";
	private LayoutInflater mInflater;
	private boolean mActionStarted = false;

	private boolean mModeBuying = true;

	private SharedPreferences mPreferences;

	private static final int[] PRODUCT_QTY_BUTTONS = new int[] { R.id.bQtyO, R.id.bQty1, R.id.bQty2, R.id.bQty3, R.id.bQty4 };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		mInflater = getLayoutInflater();

		mPreferences = getPreferences(MODE_PRIVATE);
		// get saved config
		String server = mPreferences.getString("server", null);
		int port = mPreferences.getInt("port", -1);
		String station = mPreferences.getString("station", null);

		if (server == null || station == null || port < 1) { // no config saved
																// or invalid
																// config
			showServerDialog();
		} else {
			Config.SERVER_URL = server;
			Config.SERVER_PORT = port;
			Config.STATION = station;
			loadData();
		}

	}

	/**
	 * Shows the server picker dialog and continues the start configuration
	 * process
	 */
	private void showServerDialog() {
		final EditText servEdit = new EditText(this);

		final Handler hand = new Handler(getMainLooper());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Server address");
		builder.setView(servEdit);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Config.SERVER_URL = servEdit.getText().toString();
				dialog.dismiss();
				hand.post(new Runnable() {

					public void run() {
						showPortDialog();
					}
				});
			}
		});
		builder.show();
	}

	/**
	 * Shows the port picker dialog and continues the start configuration
	 * process
	 */
	private void showPortDialog() {
		final EditText portEdit = new EditText(this);

		final Handler hand = new Handler(getMainLooper());
		final Context con = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Port");
		builder.setView(portEdit);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int port;
				try {
					port = Integer.parseInt(portEdit.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				Config.SERVER_PORT = port;
				dialog.dismiss();

				fetchStations(con, hand);

			}
		});
		builder.show();
	}

	/**
	 * Fetches the station list from the server and show the station picker
	 * dialog.
	 * 
	 * @param con
	 *            context for dialogs
	 * @param hand
	 *            handler to which UI events are posted
	 */
	private void fetchStations(final Context con, final Handler hand) {
		new Thread(new Runnable() {

			public void run() {
				String[] stations = null;
				try {

					Object[] st = Client.listStations(Config.SERVER_URL, Config.SERVER_PORT).toArray();
					stations = new String[st.length];
					for (int i = 0; i < st.length; ++i) {
						stations[i] = (String) st[i];
					}
				} catch (final Exception e) {

					hand.post(new Runnable() {

						public void run() {

							showErrorDialog(con, e);
						}
					});
					return;
				}

				final String[] finalStations = stations;
				hand.post(new Runnable() {

					public void run() {

						showStationDialog(finalStations);
					}
				});
			}
		}).start();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mActionStarted = false; // enable buttons
	}

	/**
	 * Shows the station picker dialog
	 * 
	 * @param stations
	 *            list of stations to pick from
	 */
	private void showStationDialog(final String[] stations) {

		final Handler hand = new Handler(getMainLooper());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Station");
		builder.setItems(stations, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

				Config.STATION = stations[which];

				Editor e = mPreferences.edit();
				e.putString("server", Config.SERVER_URL);
				e.putInt("port", Config.SERVER_PORT);
				e.putString("station", Config.STATION);
				e.commit();

				hand.post(new Runnable() {

					public void run() {
						loadData();
					}
				});
			}
		});
		builder.show();
	}

	/**
	 * Fetches product data from the server
	 */
	private void loadData() {
		final Handler hand = new Handler(getMainLooper());

		final Context con = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);

					ProductCache.loadBuyableItems(c.listBuyableProducts());
					ProductCache.loadRawItems(c.listRawProducts());

					hand.post(new Runnable() {
						public void run() {
							loaded();
						}
					});
				} catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
					hand.post(new Runnable() {

						public void run() {
							showErrorDialog(con, ex);
						}
					});
				}
			}
		}).start();

	}

	/**
	 * Displays a non-cancelable error dialog with a message from an exception
	 * 
	 * @param con
	 *            Dialog context
	 * @param ex
	 *            The exception whose message to show.
	 */
	private void showErrorDialog(Context con, Exception ex) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setCancelable(false);
		builder.setTitle("Error");
		builder.setMessage("Fetching data from server failed: " + ex.getMessage());
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.show();
	}

	public void onUserSettingsClick(View v) {
		if (!mActionStarted) {
			mActionStarted = true;
			Intent i = new Intent(getApplicationContext(), LoginwithusernameActivity.class);
			startActivityForResult(i, REQUEST_USER_SETTINGS);
		}
	}

	/**
	 * Called after product data has been fetched from server.
	 */
	private void loaded() {
		setContentView(R.layout.main);
		setMode(MODE_BUY);
	}

	public void onModeButtonClick(View v) {
		setMode(!mModeBuying);
	}

	private void setMode(boolean mode) {
		mModeBuying = mode;

		setModeText();

		List<IProduct> products = (mModeBuying) ? ProductCache.listBuyableItems() : ProductCache.listRawItems();

		TableLayout t = (TableLayout) findViewById(R.id.productTable);
		TableRow[] children = new TableRow[t.getChildCount()];

		for (int i = children.length - 1; i > 0; --i) {
			children[i] = (TableRow) t.getChildAt(i);
			t.removeView(children[i]);
		}

		for (int i = 0; i < products.size(); ++i) {
			TableRow row;
			if (i < children.length - 1) {
				row = makeProductRow(products.get(i), children[i]);
			} else {
				row = makeProductRow(products.get(i), null);
			}
			t.addView(row);
		}
	}

	/**
	 * Sets the appropriate labels for the current mode (buy/bring products)
	 */
	private void setModeText() {
		TextView mode = (TextView) findViewById(R.id.modeText);
		Button modeButton = (Button) findViewById(R.id.mode_button);

		if (mModeBuying) {
			mode.setText(getString(R.string.buy_products));
			modeButton.setText(getString(R.string.bring_products));
		} else {
			mode.setText(getString(R.string.bring_products));
			modeButton.setText(getString(R.string.buy_products));
		}
	}

	private TableRow makeProductRow(final IProduct product, TableRow t) {

		if (t == null) {
			t = (TableRow) mInflater.inflate(R.layout.product_table_row, null);
		}

		TextView name = (TextView) t.findViewById(R.id.product_name);
		name.setText(product.getName());

		final Context c = this;

		for (int i = 1; i < PRODUCT_QTY_BUTTONS.length; ++i) {
			Button b = (Button) t.findViewById(PRODUCT_QTY_BUTTONS[i]);
			final int qty = i;

			b.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (!mActionStarted) {
						mActionStarted = true;
						Intent in = new Intent(c, RecogActivity.class);
						Basket b = new Basket();
						b.addProduct(product, qty);
						in.putExtra(ExtraNames.PRODUCTS, b);
						startActivityForResult(in, REQUEST_LOGIN);
					}
				}
			});

			b.setOnLongClickListener(new View.OnLongClickListener() {

				public boolean onLongClick(View v) {
					if (!mActionStarted) {
						mActionStarted = true;
						Intent in = new Intent(c, LoginwithusernameActivity.class);
						Basket b = new Basket();
						b.addProduct(product, qty);
						in.putExtra(ExtraNames.PRODUCTS, b);
						startActivityForResult(in, REQUEST_LOGIN);
						return true;
					}
					return false;
				}
			});
		}

		return t;
	}

	public void onRegButtonClick(View v) {
		if (!mActionStarted) {
			mActionStarted = true;
			Intent i = new Intent(this, NewUserActivity.class);
			startActivityForResult(i, 0);
		}
	}

	public static void setViewGroupEnebled(ViewGroup view, boolean enabled) {
		int childern = view.getChildCount();

		for (int i = 0; i < childern; i++) {
			View child = view.getChildAt(i);
			if (child instanceof ViewGroup) {
				setViewGroupEnebled((ViewGroup) child, enabled);
			}
			child.setEnabled(enabled);
		}
		view.setEnabled(enabled);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent(this, ConfirmPurchaseActivity.class);
				i.putExtra(ExtraNames.USERS, data.getExtras().getStringArray(ExtraNames.USERS));
				i.putExtra(ExtraNames.PRODUCTS, data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}

		if (requestCode == REQUEST_USER_SETTINGS) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent(this, UserSettingsActivity.class);
				i.putExtra(ExtraNames.USERS, data.getExtras().getStringArray(ExtraNames.USERS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}

	}
}
