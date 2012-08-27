package naamakahvi.android;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.android.utils.SaldoItemAdapter;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.naamakahviclient.IUser;
import naamakahvi.naamakahviclient.SaldoItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ConfirmPurchaseActivity extends Activity {

	public static final int REQUEST_USERNAME_CHANGE = 1;

	final short COUNTDOWN_LENGTH = 60;
	private CountDownTimer cd;
	private Intent intent;
	private String username;
	private Handler handler;
	public static final String TAG = "ConfirmPurchaseActivity";

	/**
	 * Buys or brings the products in the basket
	 * @throws ClientException Network/server errors
	 */
	private void buyOrBringProducts() throws ClientException {
		final Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
		final IUser buyer = c.getUser(this.username);
		final Map.Entry productAndAmountPair = convertBasketIntoProduct();
		final IProduct product = (IProduct) productAndAmountPair.getKey();
		final int amount = (Integer) productAndAmountPair.getValue();
		if (product.isBuyable()) {
			c.buyProduct(buyer, product, amount);
		} else {
			c.bringProduct(buyer, product, amount);
		}
	}

	private void configureUserView(final String name) {
		this.username = name;
		startGetIUserThread();
	}
	/**
	 * Gets the first product from the basket passed in the intent
	 * @return Map.Entry<IProduct, Integer> containing the product object and the amount purchased
	 */
	private Map.Entry convertBasketIntoProduct() {
		final Basket b = this.intent.getParcelableExtra(ExtraNames.PRODUCTS);
		final Map<IProduct, Integer> itemsBought = b.getItems();
		final Iterator productsAndAmounts = itemsBought.entrySet().iterator();
		return (Map.Entry) productsAndAmounts.next();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		setCountdown();
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		if (requestCode == REQUEST_USERNAME_CHANGE) {
			setContentView(R.layout.loading_screen);
			configureUserView(data.getStringExtra(ExtraNames.USERS));

		}
	}
	/**
	 * onClick handler for the cancel button
	 */
	public void onCPCancelClick(final View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
	/**
	 * onClick handler for the confirm button
	 */
	public void onCPOkClick(final View v) {
		this.cd.cancel();
		startBuyingThread();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		this.intent = getIntent();
		this.handler = new Handler();
		setCountdown();
		final String listOfPossibleUsers = this.intent.getStringExtra(ExtraNames.USERS);
		configureUserView(listOfPossibleUsers);

	}
	/**
	 * onClick handler for the user list button 
	 */
	public void onUserListClick(final View v) {
		final Intent i = new Intent(this, LoginwithusernameActivity.class);
		this.cd.cancel();
		startActivityForResult(i, REQUEST_USERNAME_CHANGE);
	}
	/**
	 * Sets the countdown timer that returns you to the main screen after one minute
	 */
	private void setCountdown() {
		this.cd = new CountDownTimer(1000 * this.COUNTDOWN_LENGTH, 1000) {

			@Override
			public void onFinish() {
				setResult(RESULT_CANCELED);
				finish();
			}

			@Override
			public void onTick(final long timeLeft) {
			}
		};
		this.cd.start();
	}
	
	/**
	 * Sets the label for the "recognised as" text view
	 * @param buyer the user whose information to show
	 */
	private void setRecognizedText(final IUser buyer) {
		final TextView recognized = (TextView) findViewById(R.id.cp_nametext);
		final String newRecognizedText = "You were recognized as:\n" + buyer.getGivenName() + " "
				+ buyer.getFamilyName() + " (" + this.username + ")";
		recognized.setText(newRecognizedText);
	}
	/**
	 * Populates the list of balances
	 * @param buyer User whose balances to use
	 */
	private void setSaldos(final IUser buyer) {
		final ListView coffeeSaldoView = (ListView) findViewById(R.id.coffeeSaldos);
		final List<SaldoItem> userBalance = buyer.getBalance();
		final double[] deltas = new double[userBalance.size()];
		final Map.Entry productAndAmountPair = convertBasketIntoProduct();
		final IProduct product = (IProduct) productAndAmountPair.getKey();
		final int amount = (Integer) productAndAmountPair.getValue();

		for (int i = 0; i < userBalance.size(); i++) {
			final SaldoItem saldoItem = userBalance.get(i);
			if (saldoItem.getGroupId() == product.getProductGroup()) {
				deltas[i] = product.getPrice() * amount;
				if (product.isBuyable()) {
					deltas[i] = -deltas[i];
				}
			} else {
				deltas[i] = 0;
			}
			;
		}

		final SaldoItemAdapter adapter = new SaldoItemAdapter(this, userBalance, deltas);
		coffeeSaldoView.setAdapter(adapter);
	}
	/**
	 * Sets the label for the product text view
	 */
	private void setWhatYouAreBuyingText() {
		final Map.Entry productAndAmountPair = convertBasketIntoProduct();
		final TextView whatYouAreBuying = (TextView) findViewById(R.id.whatYouBought);
		final IProduct product = (IProduct) productAndAmountPair.getKey();
		whatYouAreBuying.setText("You are buying " + productAndAmountPair.getValue() + " x " + product.getName()
				+ "(s)");
	}
	
	/**
	 * Starts network communication thread to buy products
	 */
	private void startBuyingThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					buyOrBringProducts();
				} catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}
		}).start();
	}
	/**
	 * Starts thread to get user info from server
	 */
	private void startGetIUserThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					final Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
					final IUser user = c.getUser(ConfirmPurchaseActivity.this.username);
					ConfirmPurchaseActivity.this.handler.post(new Runnable() {
						public void run() {
							setContentView(R.layout.confirm_purchase);
							setSaldos(user);
							setRecognizedText(user);
							setWhatYouAreBuyingText();
						}
					});
				} catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}
		}).start();
	}
}
