package naamakahvi.android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import naamakahvi.naamakahviclient.IStation;
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
					List<IStation> s = Client.listStations(Config.SERVER_URL,Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,Config.SERVER_PORT, s.get(0));
					final IUser user = c.authenticateText(username);
					handler.post(new Runnable() {
						public void run() {
							setSaldos(user);
						}
					});
				}
				catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}			
		}).start();
	}
	
	private void setListView(ListView listView, String[] list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        		int position, long id) {
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
        setRecognizedText();
	}
	
	private void setRecognizedText() {
		TextView recognized = (TextView) findViewById(R.id.cp_nametext);
		String newRecognizedText = "You were recognized as: " + username;
		recognized.setText(newRecognizedText);
	}
	
	private void setSaldos(IUser buyer) {
		ListView coffeeSaldoView = (ListView) findViewById(R.id.coffeeSaldos);
		List<SaldoItem> userBalance = buyer.getBalance();	
		String[] userSaldoTexts = new String[userBalance.size()];
		Map.Entry productAndAmountPair = convertBasketIntoProduct();
		IProduct product = (IProduct) productAndAmountPair.getKey();
		int amount = (Integer) productAndAmountPair.getValue();
		for (int i = 0; i < userBalance.size(); i++) {
			SaldoItem saldoItem = userBalance.get(i);
			userSaldoTexts[i] = "Your " + saldoItem.getGroupName() + " saldo is " + saldoItem.getSaldo() +
					" - " + (amount*product.getPrice());
//			if (product.getProductGroup() == null) {
//				userSaldoTexts[i] = "BÖÖ!";
//			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, userSaldoTexts);
		coffeeSaldoView.setAdapter(adapter);
		
//			saldoEspresso.setTextColor(Color.GREEN);
//		else
//			saldoEspresso.setTextColor(Color.RED);
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
		buyProducts();
		setResult(RESULT_OK);
		finish();
	}
	
	private void buyProducts() {
		new Thread(new Runnable() {
			public void run() {
				try {
					List<IStation> s = Client.listStations(Config.SERVER_URL,Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,Config.SERVER_PORT, s.get(0));
					IUser buyer = c.authenticateText(username);
				    Map.Entry productAndAmountPair = convertBasketIntoProduct();
				    IProduct product = (IProduct) productAndAmountPair.getKey();
				    int amount = (Integer) productAndAmountPair.getValue();
				    c.buyProduct(buyer, product, amount);
				}
				catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}			
		}).start();
	}
	
	private void setWhatYouAreBuyingText() {
		Map.Entry productAndAmountPair = convertBasketIntoProduct();
		TextView whatYouAreBuying = (TextView) findViewById(R.id.whatYouBought);
		IProduct product = (IProduct) productAndAmountPair.getKey();
		whatYouAreBuying.setText("You are buying " + productAndAmountPair.getValue() + " "
				+ product.getName() + "(s)");
	}
	
	private Map.Entry convertBasketIntoProduct() {
		Basket b = intent.getParcelableExtra(ExtraNames.PRODUCTS);
		Map<IProduct, Integer> itemsBought = b.getItems();
		Iterator productsAndAmounts = itemsBought.entrySet().iterator();
		return (Map.Entry)productsAndAmounts.next();
	}
	
	public void onCPCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
