package naamakahvi.android;

import java.util.List;

import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.DialogHelper;
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
	/**
	 * Activity result codes
	 */
	public static final short REQUEST_LOGIN = 1, REQUEST_USER_SETTINGS = 0, REQUEST_IDENTIFY = 2;
	public static final boolean MODE_BUY = true, MODE_BRING = false;

	public static final String TAG = "MainActivity";

	public static void setViewGroupEnebled(final ViewGroup view, final boolean enabled) {
		final int childern = view.getChildCount();

		for (int i = 0; i < childern; i++) {
			final View child = view.getChildAt(i);
			if (child instanceof ViewGroup) {
				setViewGroupEnebled((ViewGroup) child, enabled);
			}
			child.setEnabled(enabled);
		}
		view.setEnabled(enabled);
	}

	private LayoutInflater mInflater;

	private boolean mActionStarted = false;

	private boolean mModeBuying = true;

	private SharedPreferences mPreferences;

	private static final int[] PRODUCT_QTY_BUTTONS = new int[] { R.id.bQty1, R.id.bQty2, R.id.bQty3, R.id.bQty4 };

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
		final Activity act = this;
		new Thread(new Runnable() {

			public void run() {
				String[] stations = null;
				try {

					final Object[] st = Client.listStations(Config.SERVER_URL, Config.SERVER_PORT).toArray();
					stations = new String[st.length];
					for (int i = 0; i < st.length; ++i) {
						stations[i] = (String) st[i];
					}
				} catch (final Exception e) {

					hand.post(new Runnable() {

						public void run() {

							DialogHelper.errorDialog(con, con.getString(R.string.errorFetchData) + e.getMessage(), act)
									.show();
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

	/**
	 * Fetches product data from the server
	 */
	private void loadData() {
		final Handler hand = new Handler(getMainLooper());

		final Context con = this;
		final Activity act = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					final Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);

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
							DialogHelper
									.errorDialog(con, con.getString(R.string.errorFetchData) + ex.getMessage(), act)
									.show();
						}
					});
				}
			}
		}).start();

	}

	/**
	 * Called after product data has been fetched from server.
	 */
	private void loaded() {
		setContentView(R.layout.main);
		setMode(MODE_BUY);
	}

	private TableRow makeProductRow(final IProduct product, TableRow t) {

		if (t == null) {
			t = (TableRow) this.mInflater.inflate(R.layout.product_table_row, null);
		}

		final TextView name = (TextView) t.findViewById(R.id.product_name);
		name.setText(product.getName());

		final Context c = this;

		for (int i = 0; i < PRODUCT_QTY_BUTTONS.length; ++i) {
			final Button b = (Button) t.findViewById(PRODUCT_QTY_BUTTONS[i]);
			final int qty = i + 1;

			b.setOnClickListener(new View.OnClickListener() {

				public void onClick(final View v) {
					if (!MainActivity.this.mActionStarted) {
						MainActivity.this.mActionStarted = true;
						final Intent in = new Intent(c, RecogActivity.class);
						final Basket b = new Basket();
						b.addProduct(product, qty);
						in.putExtra(ExtraNames.PRODUCTS, b);
						startActivityForResult(in, REQUEST_IDENTIFY);
					}
				}
			});

			b.setOnLongClickListener(new View.OnLongClickListener() {

				public boolean onLongClick(final View v) {
					if (!MainActivity.this.mActionStarted) {
						MainActivity.this.mActionStarted = true;
						final Intent in = new Intent(c, LoginwithusernameActivity.class);
						final Basket b = new Basket();
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

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			switch (resultCode) {
			case RESULT_OK:
				final Intent i = new Intent(this, ConfirmPurchaseActivity.class);
				i.putExtra(ExtraNames.USERS, data.getExtras().getString(ExtraNames.USERS));
				i.putExtra(ExtraNames.PRODUCTS, data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}
		}

		if (requestCode == REQUEST_IDENTIFY) {
			switch (resultCode) {
			case RESULT_OK:

				if (data.getStringExtra(ExtraNames.USERS) == null) { // user was not identified correctly
					DialogHelper.makeToast(getApplicationContext(), R.string.errorIdentify).show();
					this.mActionStarted = true;
					final Intent in = new Intent(this, LoginwithusernameActivity.class);
					in.putExtra(ExtraNames.PRODUCTS, data.getParcelableExtra(ExtraNames.PRODUCTS));
					startActivityForResult(in, REQUEST_LOGIN);
					return;
				}

				final Intent i = new Intent(this, ConfirmPurchaseActivity.class);
				i.putExtra(ExtraNames.USERS, data.getExtras().getString(ExtraNames.USERS));
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
				final Intent i = new Intent(this, UserSettingsActivity.class);
				i.putExtra(ExtraNames.USERS, data.getExtras().getString(ExtraNames.USERS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		this.mInflater = getLayoutInflater();

		this.mPreferences = getPreferences(MODE_PRIVATE);
		// get saved config
		final String server = this.mPreferences.getString("server", null);
		final int port = this.mPreferences.getInt("port", -1);
		final String station = this.mPreferences.getString("station", null);

		if (server == null || station == null || port < 1) { // no config saved or invalid config
			showServerDialog();
		} else {
			Config.SERVER_URL = server;
			Config.SERVER_PORT = port;
			Config.STATION = station;
			loadData();
		}

	}

	public void onModeButtonClick(final View v) {
		setMode(!this.mModeBuying);
	}

	public void onRegButtonClick(final View v) {
		if (!this.mActionStarted) {
			this.mActionStarted = true;
			final Intent i = new Intent(this, NewUserActivity.class);
			startActivityForResult(i, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.mActionStarted = false; // enable buttons
	}

	/**
	 * onClick handler for Update user photos button
	 */
	public void onUserSettingsClick(final View v) {
		if (!this.mActionStarted) {
			this.mActionStarted = true;
			final Intent i = new Intent(getApplicationContext(), LoginwithusernameActivity.class);
			startActivityForResult(i, REQUEST_USER_SETTINGS);
		}
	}

	/**
	 * Sets the operation mode
	 * 
	 * @param mode
	 *            true for buy mode, false for pay mode
	 */
	private void setMode(final boolean mode) {
		this.mModeBuying = mode;

		setModeText();

		final List<IProduct> products = (this.mModeBuying) ? ProductCache.listBuyableItems() : ProductCache
				.listRawItems();

		final TableLayout t = (TableLayout) findViewById(R.id.productTable);
		final TableRow[] children = new TableRow[t.getChildCount()];

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
		final TextView mode = (TextView) findViewById(R.id.modeText);
		final Button modeButton = (Button) findViewById(R.id.mode_button);

		if (this.mModeBuying) {
			mode.setText(getString(R.string.buy_products));
			modeButton.setText(getString(R.string.bring_products));
		} else {
			mode.setText(getString(R.string.bring_products));
			modeButton.setText(getString(R.string.buy_products));
		}
	}

	/**
	 * Shows the port picker dialog and continues the start configuration
	 * process
	 */
	private void showPortDialog() {

		final EditText portEdit = new EditText(this);
		portEdit.setSingleLine();
		final Handler hand = new Handler(getMainLooper());
		final Context con = this;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Port");
		builder.setView(portEdit);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
				int port;
				try {
					port = Integer.parseInt(portEdit.getText().toString());
				} catch (final Exception e) {
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
	 * Shows the server picker dialog and continues the start configuration
	 * process
	 */
	private void showServerDialog() {
		final EditText servEdit = new EditText(this);
		servEdit.setSingleLine();
		final Handler hand = new Handler(getMainLooper());

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Server address");
		builder.setView(servEdit);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
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
	 * Shows the station picker dialog
	 * 
	 * @param stations
	 *            list of stations to pick from
	 */
	private void showStationDialog(final String[] stations) {

		final Handler hand = new Handler(getMainLooper());

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("Station");
		builder.setItems(stations, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {

				dialog.dismiss();

				Config.STATION = stations[which];

				final Editor e = MainActivity.this.mPreferences.edit();
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
}
