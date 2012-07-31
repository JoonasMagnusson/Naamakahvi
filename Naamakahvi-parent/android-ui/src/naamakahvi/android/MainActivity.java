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
import naamakahvi.naamakahviclient.IStation;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final short REQUEST_LOGIN = 1;
	public static final String TAG = "MainActivity";
	private LayoutInflater mInflater;

	private SharedPreferences mPreferences;
	
	private static final int[] PRODUCT_QTY_BUTTONS = new int[] { R.id.bQtyO,
			R.id.bQty1, R.id.bQty2, R.id.bQty3, R.id.bQty4 };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		mInflater = getLayoutInflater();

		
		mPreferences = getPreferences(MODE_PRIVATE);
		String server = mPreferences.getString("server", null);
		int port = mPreferences.getInt("port", -1);

		if (server == null || port < 1) {
			showServerDialog();
		} else {
			loadData();
		}

	}

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

	private void showPortDialog(){
		final EditText portEdit = new EditText(this);
		
		final Handler hand = new Handler(getMainLooper());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this);
		builder.setCancelable(false);
		builder.setTitle("Port");
		builder.setView(portEdit);
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int which) {
						int port;
						try {
						port = Integer.parseInt(portEdit.getText().toString());
						}catch(Exception e){
							e.printStackTrace();
							return;
						}
						Config.SERVER_PORT = port;
						dialog.dismiss();	
					    
						Editor e = mPreferences.edit();
						e.putString("server",Config.SERVER_URL);
						e.putInt("port", Config.SERVER_PORT);
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

	private void loadData() {
		final Handler hand = new Handler(getMainLooper());

		final Context con = this;

		new Thread(new Runnable() {

			public void run() {
				try {
					List<IStation> s = Client.listStations(Config.SERVER_URL,
							Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,
							Config.SERVER_PORT, s.get(0));

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
							AlertDialog.Builder builder = new AlertDialog.Builder(
									con);
							builder.setCancelable(false);
							builder.setTitle("Error");
							builder.setMessage("Fetching data from server failed: "
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

	private void loaded() {
		setContentView(R.layout.main);
		List<IProduct> products = ProductCache.listBuyableItems();
		; // client.listBuyableProducts();

		TableLayout tl = (TableLayout) findViewById(R.id.buyTable);

		for (IProduct p : products) {
			tl.addView(makeProductRow(p));
		}
		products = ProductCache.listRawItems();
		tl = (TableLayout) findViewById(R.id.payTable);

		for (IProduct p : products) {
			tl.addView(makeProductRow(p));
		}
	}

	private TableRow makeProductRow(final IProduct product) {
		TableRow t = (TableRow) mInflater.inflate(R.layout.product_table_row,
				null);
		TextView name = (TextView) t.findViewById(R.id.product_name);
		name.setText(product.getName());

		Button other = (Button) t.findViewById(PRODUCT_QTY_BUTTONS[0]);
		other.setOnClickListener(null); // TODO

		final Context c = this;

		for (int i = 1; i < PRODUCT_QTY_BUTTONS.length; ++i) {
			Button b = (Button) t.findViewById(PRODUCT_QTY_BUTTONS[i]);
			final int qty = i;

			b.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					Intent in = new Intent(c, RecogActivity.class);
					Basket b = new Basket();
					b.addProduct(product, qty);
					in.putExtra(ExtraNames.PRODUCTS, b);
					startActivityForResult(in, REQUEST_LOGIN);
				}
			});
		}

		return t;
	}

	public void onRegButtonClick(View v) {
		Intent i = new Intent(this, NewUserActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent(this, ConfirmPurchaseActivity.class);
				i.putExtra(ExtraNames.USERS,
						data.getExtras().getStringArray(ExtraNames.USERS));
				i.putExtra(ExtraNames.PRODUCTS,
						data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}
}
