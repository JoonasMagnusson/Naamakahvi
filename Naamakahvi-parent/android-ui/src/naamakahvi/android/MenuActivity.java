package naamakahvi.android;

import android.app.Activity;

public class MenuActivity extends Activity {
/*
	private Basket mBasket = new Basket();

	/** Called when the activity is first created. *//*
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
	}*/
}