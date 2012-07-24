package naamakahvi.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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

public class ConfirmPurchaseActivity extends Activity {

	final short COUNTDOWN_LENGTH = 10;
	private CountDownTimer cd;
	private Intent intent;
	private String username;
	public static final String TAG = "ConfirmPurchaseActivity";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_purchase);
		intent = getIntent();
		setCountdown();
        ListView possibleUsersListView = (ListView) findViewById(R.id.possibleUsers);
        String[] testUsers = intent.getStringArrayExtra(ExtraNames.USERS);
        setListView(possibleUsersListView, testUsers);
        username = testUsers[0];
        setSaldos(testUsers[0]);
        setRecognizedText(testUsers[0]);
	}

	
	private void setListView(ListView listView, String[] list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        		int position, long id) {
        		String item = (String) parent.getAdapter().getItem(position);
        		setSaldos(item);
        		setRecognizedText(item);
        		username = item;
        		cd.cancel();
        		cd.start();
        	}
        }); 
	}
	
	private void setRecognizedText(String username) {
		TextView recognized = (TextView) findViewById(R.id.cp_nametext);
		String newRecognizedText = "You were recognized as: " + username;
		recognized.setText(newRecognizedText);
	}
	
	private void setSaldos(String username) {
		Basket b = intent.getParcelableExtra(ExtraNames.PRODUCTS);
		Map<IProduct, Integer> itemsBought = b.getItems();
		int changeInEspresso = 0;
		int changeInCoffee = 0;
		
		// TODO: alla olevaa muutetaan, kun saadaan productiin metodit, jotka kertovat hinnan!
		Iterator it = itemsBought.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        IProduct product = (IProduct) pairs.getKey();
	        int amount = (Integer) pairs.getValue();
	        if (product.getName().equals("Kahvi"))
	        	changeInCoffee -= (amount*product.getPrice());
	        else
	        	changeInEspresso -= (amount*product.getPrice());
	    }
		
		TextView saldoEspresso = (TextView) findViewById(R.id.saldoEspresso);
		TextView saldoCoffee = (TextView) findViewById(R.id.saldoCoffee);
		
		//TODO: get saldos from client, currently testSaldos used instead.
		int testSaldoCof = -2;
		int testSaldoEsp = 4;
		String newTextForSaldoEspresso = "Your espressosaldo is " + testSaldoEsp + " + " + changeInEspresso;
		String newTextForSaldoCoffee = "Your coffeesaldo is " + testSaldoCof + " + " + changeInCoffee;
		saldoCoffee.setText(newTextForSaldoCoffee);
		saldoEspresso.setText(newTextForSaldoEspresso);
		
		if ((testSaldoCof + changeInCoffee) >= 0)
			saldoCoffee.setTextColor(Color.GREEN);
		else
			saldoCoffee.setTextColor(Color.RED);
		
		if ((testSaldoEsp + changeInEspresso) >= 0)
			saldoEspresso.setTextColor(Color.GREEN);
		else
			saldoEspresso.setTextColor(Color.RED);
	}
	
	private void setCountdown() {
		final TextView countdown = (TextView) findViewById(R.id.cp_rec_countdown);
		countdown.setText(getString(R.string.countdown_prefix) + " "
				+ COUNTDOWN_LENGTH + getString(R.string.countdown_suffix));
		
		cd = new CountDownTimer(1000 * COUNTDOWN_LENGTH, 1000) {
			
			public void onTick(long timeLeft) {
				countdown.setText(getString(R.string.countdown_prefix) + " "
						+ timeLeft / 1000
						+ getString(R.string.countdown_suffix));

			}
			
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
		final Iterator it = itemsBought.entrySet().iterator();
		new Thread(new Runnable() {
			public void run() {
				try {
					List<IStation> s = Client.listStations(Config.SERVER_URL,Config.SERVER_PORT);
					Client c = new Client(Config.SERVER_URL,Config.SERVER_PORT, s.get(0));
					while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
				        IProduct product = (IProduct) pairs.getKey();
				        int amount = (Integer) pairs.getValue();
				        IUser buyer = c.authenticateText(username);
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
