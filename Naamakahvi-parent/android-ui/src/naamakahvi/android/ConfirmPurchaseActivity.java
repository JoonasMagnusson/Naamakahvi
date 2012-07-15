package naamakahvi.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import naamakahvi.android.R;
import naamakahvi.android.utils.Basket;
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
	private Client client;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_purchase);
//		try {
//			List<IStation> stations = Client.listStations("naama.zerg.fi", 5001);
//			client = new Client("naama.zerg.fi", 5001, stations.get(0));
//		} catch (ClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		intent = getIntent();
		setCountdown();
        ListView possibleUsersListView = (ListView) findViewById(R.id.possibleUsers);
        String[] testUsers = intent.getStringArrayExtra("naamakahvi.android.users");
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
		Basket b = intent.getParcelableExtra("naamakahvi.android.products");
		Map<IProduct, Integer> itemsBought = b.getItems();
		int changeInEspresso = 0;
		int changeInCoffee = 0;
		
		// TODO: alla olevaa muutetaan, kun saadaan productiin metodit, jotka kertovat hinnan!
		Iterator it = itemsBought.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        IProduct product = (IProduct) pairs.getKey();
	        int amount = (Integer) pairs.getValue();
	        if (product.getName().equals("Espresso"))
	        	changeInEspresso -= amount;
	        else if (product.getName().equals("Kahvi"))
	        	changeInCoffee -= amount;
	        else
	        	changeInEspresso -= (amount*2);
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
		setResult(RESULT_OK);
		finish();
	}
	
	private void buyProducts() {
		// TODO: usernamen hankinta, clientkoodin toimivuus jne
		//IUser user = TODO: get user jostain tiedoista
		Basket b = intent.getParcelableExtra("naamakahvi.android.products");
		Map<IProduct, Integer> itemsBought = b.getItems();
		Iterator it = itemsBought.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        IProduct product = (IProduct) pairs.getKey();
	        int amount = (Integer) pairs.getValue();
	        //TODO: osto
	    }
	}
	
	public void onCPCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
