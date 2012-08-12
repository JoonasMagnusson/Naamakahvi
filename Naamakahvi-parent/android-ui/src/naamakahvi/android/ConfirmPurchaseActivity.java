package naamakahvi.android;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import naamakahvi.android.R;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.Config;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.naamakahviclient.IUser;
import naamakahvi.naamakahviclient.SaldoItem;

public class ConfirmPurchaseActivity extends Activity {

	final short COUNTDOWN_LENGTH = 10;
	private CountDownTimer cd;
	private Intent intent;
	private String username;
	private Handler handler;
	public static final String TAG = "ConfirmPurchaseActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_purchase);
		intent = getIntent();
		handler = new Handler();
		setCountdown();
		ListView possibleUsersListView = (ListView) findViewById(R.id.possibleUsers);
		String[] listOfPossibleUsers = intent.getStringArrayExtra(ExtraNames.USERS);
		setListView(possibleUsersListView, listOfPossibleUsers);
		configureUserView(listOfPossibleUsers[0]);
		setWhatYouAreBuyingText();
	}

	private void startGetIUserThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
					final IUser user = c.getUser(username);
					handler.post(new Runnable() {
						public void run() {
							setSaldos(user);
							setRecognizedText(user);
						}
					});
				} catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}
		}).start();
	}

	private void setListView(ListView listView, String[] list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.new_list_bigger_text, android.R.id.text1,
				list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String alternativeUser = (String) parent.getAdapter().getItem(position);
				configureUserView(alternativeUser);
				cd.cancel();
				cd.start();
			}
		});
	}

	private void configureUserView(String name) {
		username = name;
		startGetIUserThread();
	}

	private void setRecognizedText(IUser buyer) {
		TextView recognized = (TextView) findViewById(R.id.cp_nametext);
		String newRecognizedText = "You were recognized as:\n" + buyer.getGivenName() + " " + buyer.getFamilyName()
				+ " (" + username + ")";
		recognized.setText(newRecognizedText);
	}

	private void setWhatYouAreBuyingText() {
		Map.Entry productAndAmountPair = convertBasketIntoProduct();
		TextView whatYouAreBuying = (TextView) findViewById(R.id.whatYouBought);
		IProduct product = (IProduct) productAndAmountPair.getKey();
		whatYouAreBuying.setText("You are buying " + productAndAmountPair.getValue() + " " + product.getName() + "(s)");
	}
	
	private void setSaldos(IUser buyer) {
		ListView coffeeSaldoView = (ListView) findViewById(R.id.coffeeSaldos);
		List<SaldoItem> userBalance = buyer.getBalance();
		String[] userSaldoTexts = new String[userBalance.size()];
		String[] userSaldoColors = new String[userBalance.size()];
		Map.Entry productAndAmountPair = convertBasketIntoProduct();
		IProduct product = (IProduct) productAndAmountPair.getKey();
		int amount = (Integer) productAndAmountPair.getValue();
		
		for (int i = 0; i < userBalance.size(); i++) {
			SaldoItem saldoItem = userBalance.get(i);
			userSaldoTexts[i] = generateSaldoText(product, saldoItem, amount);
			userSaldoColors[i] = generateSaldoColors(product, saldoItem, amount);
		}
		
		ColorStringAdapter adapter = new ColorStringAdapter(this, userSaldoTexts, userSaldoColors);
		coffeeSaldoView.setAdapter(adapter);
	}

	private String generateSaldoText(IProduct product, SaldoItem saldoItem, int amount) {
		if (product.getProductGroup() == saldoItem.getGroupId())
			return "Your " + saldoItem.getGroupName() + " saldo is " + saldoItem.getSaldo() + " - "
				+ (amount * product.getPrice());
		return "Your " + saldoItem.getGroupName() + " saldo is " + saldoItem.getSaldo();
	}
	
	private String generateSaldoColors(IProduct product, SaldoItem saldoItem, int amount)  {
		if ((saldoItem.getSaldo() - (amount*product.getPrice())) < 0)
			return "negative";
		return "positive";
	}
	
	private void setCountdown() {
		cd = new CountDownTimer(6000 * COUNTDOWN_LENGTH, 1000) {

			@Override
			public void onTick(long timeLeft) {
			}

			@Override
			public void onFinish() {
				setResult(RESULT_CANCELED);
				finish();
			}
		};
		cd.start();
	}

	public void onCPOkClick(View v) {
		cd.cancel();
		startBuyingThread();
		setResult(RESULT_OK);
		finish();
	}

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
	
	private void buyOrBringProducts() throws ClientException {
		Client c = new Client(Config.SERVER_URL, Config.SERVER_PORT, Config.STATION);
		IUser buyer = c.getUser(username);
		Map.Entry productAndAmountPair = convertBasketIntoProduct();
		IProduct product = (IProduct) productAndAmountPair.getKey();
		int amount = (Integer) productAndAmountPair.getValue();
		if (product.isBuyable())
			c.buyProduct(buyer, product, amount);
		else
			c.bringProduct(buyer, product, amount);
	}

	private Map.Entry convertBasketIntoProduct() {
		Basket b = intent.getParcelableExtra(ExtraNames.PRODUCTS);
		Map<IProduct, Integer> itemsBought = b.getItems();
		Iterator productsAndAmounts = itemsBought.entrySet().iterator();
		return (Map.Entry) productsAndAmounts.next();
	}

	public void onCPCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
