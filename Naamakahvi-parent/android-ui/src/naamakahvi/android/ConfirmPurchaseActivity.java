package naamakahvi.android;

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
		Basket producstThatCustomerIsBuying = intent.getParcelableExtra(ExtraNames.PRODUCTS);
		Map<IProduct, Integer> productsToBeBought = producstThatCustomerIsBuying.getItems();
		ListView coffeeSaldoView = (ListView) findViewById(R.id.coffeeSaldos);

		List<SaldoItem> userBalance = buyer.getBalance();
		String[] userSaldoTexts = new String[1];
		if (userBalance == null)
			userSaldoTexts[0] = "ei onnistunut";
		else
			userSaldoTexts[0] = "onnistui";
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	android.R.layout.simple_list_item_1, android.R.id.text1, userSaldoTexts);
coffeeSaldoView.setAdapter(adapter);
		
//		String[] userSaldoTexts = new String[userBalance.size()];
//		
//		for (int i = 0; i < userBalance.size(); i++) {
//			SaldoItem saldoItem = userBalance.get(i);
//			userSaldoTexts[i] = "Your " + saldoItem.getGroupName() + " is " + saldoItem.getSaldo() +
//					" + TODO later";
//		}
//		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//            	android.R.layout.simple_list_item_1, android.R.id.text1, userSaldoTexts);
//		coffeeSaldoView.setAdapter(adapter);
//		
//		// TODO: alla olevaa muutetaan, kun saadaan productiin metodit, jotka kertovat hinnan!
//		Iterator productsAndAmounts = productsToBeBought.entrySet().iterator();
//	    while (productsAndAmounts.hasNext()) {
//	        Map.Entry productAndAmountPair = (Map.Entry)productsAndAmounts.next();
//	        IProduct product = (IProduct) productAndAmountPair.getKey();
//	        int amount = (Integer) productAndAmountPair.getValue();
//	        if (product.getName().equals("Kahvi"))
//	        	changeInCoffee -= (amount*product.getPrice());
//	        else
//	        	changeInEspresso -= (amount*product.getPrice());
//	    }
//		
//		if ((testSaldoCof + changeInCoffee) >= 0)
//			saldoCoffee.setTextColor(Color.GREEN);
//		else
//			saldoCoffee.setTextColor(Color.RED);
//		
//		if ((testSaldoEsp + changeInEspresso) >= 0)
//			saldoEspresso.setTextColor(Color.GREEN);
//		else
//			saldoEspresso.setTextColor(Color.RED);
	}
	
	private void setCountdown() {
		final TextView countdown = (TextView) findViewById(R.id.cp_rec_countdown);
		countdown.setText(getString(R.string.countdown_prefix) + " "
				+ COUNTDOWN_LENGTH + getString(R.string.countdown_suffix));
		
		cd = new CountDownTimer(1000 * COUNTDOWN_LENGTH, 1000) {
			
			@Override
			public void onTick(long timeLeft) {
				countdown.setText(getString(R.string.countdown_prefix) + " "
						+ timeLeft / 1000
						+ getString(R.string.countdown_suffix));

			}
			
			@Override
			public void onFinish() {
				buyProducts();
				setResult(RESULT_OK);
				finish();
			}
		};
		cd.start();
	}
	
	public void onCPOkClick(View v) {
		buyProducts();
		cd.cancel();
		setResult(RESULT_OK);
		finish();
	}
	
	private void buyProducts() {
		Basket b = intent.getParcelableExtra(ExtraNames.PRODUCTS);
		Map<IProduct, Integer> itemsBought = b.getItems();
		Iterator productsAndAmounts = itemsBought.entrySet().iterator();
		startBuyingThread(productsAndAmounts);
	}
	
	private void startBuyingThread(Iterator its) {
		final Iterator productsAndAmounts = its;
		new Thread(new Runnable() {
			public void run() {
				try {
					List<IStation> s = Client.listStations(Config.SERVER_URL,Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,Config.SERVER_PORT, s.get(0));
					IUser buyer = c.authenticateText(username);
					while (productsAndAmounts.hasNext()) {
				        Map.Entry productAndAmountPair = (Map.Entry)productsAndAmounts.next();
				        IProduct product = (IProduct) productAndAmountPair.getKey();
				        int amount = (Integer) productAndAmountPair.getValue();
				        c.buyProduct(buyer, product, amount);
				    }
				}
				catch (final ClientException ex) {
					Log.d(TAG, ex.getMessage());
					ex.printStackTrace();
				}
			}			
		}).start();
	}
	
	
	public void onCPCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
