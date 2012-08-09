package naamakahvi.android.utils;

import java.util.HashMap;
import java.util.Map;

import naamakahvi.naamakahviclient.IProduct;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Shopping cart for IProducts
 * 
 * @author Ossi
 * 
 */
public class Basket implements Parcelable {
	private Map<IProduct, Integer> items;

	public Basket() {
		items = new HashMap<IProduct, Integer>();
	}

	public Map<IProduct, Integer> getItems() {
		return items;
	}

	/**
	 * Adds one unit of a product to the cart
	 * 
	 * @param product
	 *            Product to be added
	 */
	public void addProduct(IProduct product) {
		addProduct(product, 1);
	}

	/**
	 * Adds a specified number of units of a product to the cart
	 * 
	 * @param product
	 *            Product to be added
	 * @param amount
	 *            Number of units
	 */
	public void addProduct(IProduct product, int amount) {
		Integer n = items.get(product);
		if (n == null)
			n = 0;
		items.put(product, n + amount);
	}

	/**
	 * Removes all units of the product from the cart
	 * 
	 * @param product
	 *            Product to be removed
	 */
	public void removeProduct(IProduct product) {
		items.remove(product);
	}

	/**
	 * Removes a specified number units of the product from the cart
	 * 
	 * @param product
	 *            Product to be removed
	 * @param amount
	 *            Number of units
	 */
	public void removeProduct(IProduct product, int amount) {
		Integer n = items.get(product);
		if (n == null)
			return;
		if (n - amount < 1) {
			removeProduct(product);
			return;
		}
		items.put(product, n - amount);
	}

	public Basket(Parcel in) {
		this();
		while (in.dataAvail() != 0) {
			final String name = in.readString();

			IProduct i = ProductCache.getProduct(name);

			items.put(i, in.readInt());
		}
	}

	public int describeContents() {
		return 0;
	}

	public int getCount(IProduct product) {
		Integer n = items.get(product);
		if (n == null)
			return 0;
		return n;
	}

	public void writeToParcel(Parcel dest, int flags) {
		for (IProduct i : items.keySet()) {
			dest.writeString(i.toString());
			dest.writeInt(items.get(i));
		}
	}

	public static final Parcelable.Creator<Basket> CREATOR = new Parcelable.Creator<Basket>() {
		public Basket createFromParcel(Parcel in) {
			return new Basket(in);
		}

		public Basket[] newArray(int size) {
			return new Basket[size];
		}
	};

	public int size() {
		return items.size();
	}

}
