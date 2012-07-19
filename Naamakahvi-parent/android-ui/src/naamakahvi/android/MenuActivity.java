package naamakahvi.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.ExtraNames;
import naamakahvi.android.utils.RandomString;
import naamakahvi.naamakahviclient.IProduct;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends Activity {

	private Basket mBasket = new Basket();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_gridview);
		((GridView) findViewById(R.id.product_grid))
				.setAdapter(new ProductAdapter(this, products()));
		((GridView) findViewById(R.id.payproduct_grid))
				.setAdapter(new ProductAdapter(this, pay_products()));
		((ListView) findViewById(R.id.cart_list)).setAdapter(new CartAdapter(
				this, mBasket));
	}

	private ArrayList<IProduct> products() {
		ArrayList<IProduct> products = new ArrayList<IProduct>();
		products.add(new IProduct() {

			public String getName() {
				return "Kahvi";
			}

			public String toString() {
				return getName();
			}

			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		products.add(new IProduct() {

			public String getName() {
				return "Espresso";
			}

			public String toString() {
				return getName();
			}

			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		products.add(new IProduct() {

			public String getName() {
				return "Tuplaespresso";
			}

			public String toString() {
				return getName();
			}

			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
		products.add(new IProduct() {

			public String getName() {
				return "Tee?";
			}

			public String toString() {
				return getName();
			}

			public double getPrice() {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		return products;
	}

	private ArrayList<IProduct> pay_products() {
		final RandomString r = new RandomString(20);
		ArrayList<IProduct> products = new ArrayList<IProduct>();

		for (int i = 0; i < 30; ++i) {
			products.add(new IProduct() {

				public String getName() {
					return r.nextString();
				}

				public double getPrice() {
					// TODO Auto-generated method stub
					return 0;
				}
			});

		}
		return products;
	}

	private void refreshCart() {
		ListView cart = (ListView) findViewById(R.id.cart_list);
		((CartAdapter) cart.getAdapter()).notifyDataSetChanged();
	}
	
	public void doCheckout(View v){
		Intent i = new Intent(this,ConfirmPurchaseActivity.class);
		i.putExtra(ExtraNames.USERS, new String[]{getIntent().getExtras().getString(ExtraNames.SELECTED_USER)}); //TODO: actual username
		i.putExtra(ExtraNames.PRODUCTS, mBasket);
		startActivity(i);
	}

	public class ProductAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<IProduct> data;

		public ProductAdapter(Context context, ArrayList<IProduct> data) {
			this.inflater = LayoutInflater.from(context);
			this.data = data;
		}

		public int getCount() {
			return this.data.size();
		}

		public IProduct getItem(int position) throws IndexOutOfBoundsException {
			return this.data.get(position);
		}

		public long getItemId(int position) throws IndexOutOfBoundsException {
			if (position < getCount() && position >= 0) {
				return position;
			}
			return -1;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final IProduct product = getItem(position);

			if (convertView == null) {
				convertView = this.inflater.inflate(
						R.layout.product_single_view, null);
			}

			((TextView) convertView.findViewById(R.id.product_name))
					.setText(product.getName());
			((Button) convertView.findViewById(R.id.button_add))
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							mBasket.addProduct(product);
							refreshCart();
						}
					});

			return convertView;
		}
	}

	public class CartAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Basket data;

		public CartAdapter(Context context, Basket data) {
			this.inflater = LayoutInflater.from(context);
			this.data = data;
		}

		public int getCount() {
			return this.data.size();
		}

		public IProduct getItem(int position) throws IndexOutOfBoundsException {
			return ((IProduct) (data.getItems().keySet().toArray()[position]));
		}

		public long getItemId(int position) throws IndexOutOfBoundsException {
			if (position < getCount() && position >= 0) {
				return position;
			}
			return -1;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final IProduct product = getItem(position);

			if (convertView == null) {
				convertView = this.inflater.inflate(R.layout.product_cart_view,
						null);
			}

			((TextView) convertView.findViewById(R.id.product_name))
					.setText(product.getName());
			((TextView) convertView.findViewById(R.id.numberOfUnits))
					.setText(data.getCount(product) + " x");
			((Button) convertView.findViewById(R.id.button_add))
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							mBasket.removeProduct(product, 1);
							refreshCart();
						}
					});

			return convertView;
		}
	}
}