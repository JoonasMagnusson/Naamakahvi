package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.components.FaceDetectView;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.ClientException;
import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.naamakahviclient.IUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	public static final short REQUEST_LOGIN = 1;
	private int mFastorderUnits = 1;
	private Resources mRes;
	private LayoutInflater mInflater;
	
	private static final int[] PRODUCT_QTY_BUTTONS = new int[]{R.id.bQtyO,R.id.bQty1,R.id.bQty2,R.id.bQty3,R.id.bQty4};

	
	private List<IProduct> products(){
		List<IProduct> products = new ArrayList<IProduct>();
		products.add(new IProduct() {
			
			public String getName() {
				return "Kahvi";
			}
			public String toString(){return getName();}
			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
		
        products.add(new IProduct() {
			
			public String getName() {
				return "Espresso";
			}
			public String toString(){return getName();}
			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
        
        products.add(new IProduct() {
			
			public String getName() {
				return "Tuplaespresso";
			}
			public String toString(){return getName();}
			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
       products.add(new IProduct() {
			
			public String getName() {
				return "Tee?";
			}
			public String toString(){return getName();}
			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
        
        return products;
	}
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mRes=  getResources(); 
		
		mInflater = getLayoutInflater();
		
		List<IProduct> bestproducts =   products();          ; //client.listBuyableProducts();

		TableLayout tl = (TableLayout) findViewById(R.id.buyTable);

		for (IProduct p : bestproducts){
			tl.addView(makeProductRow(p));			
		}
		
	    tl = (TableLayout) findViewById(R.id.payTable);

		for (IProduct p : bestproducts){
			tl.addView(makeProductRow(p));			
		}
		
		
	}
	
	private TableRow makeProductRow(final IProduct product){
		TableRow t = (TableRow) mInflater.inflate(R.layout.product_table_row,null);
		TextView name = (TextView) t.findViewById(R.id.product_name);
		name.setText(product.getName());
		
		Button other = (Button)t.findViewById(PRODUCT_QTY_BUTTONS[0]);
		other.setOnClickListener(null); //TODO
		
		final Context c = this;
		
		for (int i = 1; i < PRODUCT_QTY_BUTTONS.length; ++i){
			Button b = (Button) t.findViewById(PRODUCT_QTY_BUTTONS[i]);
			final int qty = i;
			
			
			b.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent in =  new Intent(c,RecogActivity.class);
					Basket b = new Basket();
					b.addProduct(product,qty);
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
				i.putExtra(ExtraNames.USERS, new String[]{ data.getExtras().getString(ExtraNames.SELECTED_USER)});
				i.putExtra(ExtraNames.PRODUCTS, data.getExtras().getParcelable(ExtraNames.PRODUCTS));
				startActivity(i);
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}
}
