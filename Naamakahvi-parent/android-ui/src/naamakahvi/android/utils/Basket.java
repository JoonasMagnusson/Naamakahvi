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
	private final Map<IProduct, Integer> items;

	public static final Parcelable.Creator<Basket> CREATOR = new Parcelable.Creator<Basket>() {
		public Basket createFromParcel(final Parcel in) {
			return new Basket(in);
		}

		public Basket[] newArray(final int size) {
			return new Basket[size];
		}
	};

	public Basket() {
		this.items = new HashMap<IProduct, Integer>();
	}

	public Basket(final Parcel in) {
		this();
		while (in.dataAvail() != 0) {
			final String name = in.readString();

			final IProduct i = ProductCache.getProduct(name);

			this.items.put(i, in.readInt());
		}
	}

	/**
	 * Adds one unit of a product to the cart
	 * 
	 * @param product
	 *            Product to be added
	 */
	public void addProduct(final IProduct product) {
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
	public void addProduct(final IProduct product, final int amount) {
		Integer n = this.items.get(product);
		if (n == null) {
			n = 0;
		}
		this.items.put(product, n + amount);
	}

	public int describeContents() {
		return 0;
	}

	public int getCount(final IProduct product) {
		final Integer n = this.items.get(product);
		if (n == null) {
			return 0;
		}
		return n;
	}

	public Map<IProduct, Integer> getItems() {
		return this.items;
	}

	/**
	 * Removes all units of the product from the cart
	 * 
	 * @param product
	 *            Product to be removed
	 */
	public void removeProduct(final IProduct product) {
		this.items.remove(product);
	}

	/**
	 * Removes a specified number units of the product from the cart
	 * 
	 * @param product
	 *            Product to be removed
	 * @param amount
	 *            Number of units
	 */
	public void removeProduct(final IProduct product, final int amount) {
		final Integer n = this.items.get(product);
		if (n == null) {
			return;
		}
		if (n - amount < 1) {
			removeProduct(product);
			return;
		}
		this.items.put(product, n - amount);
	}

	/**
	 * Returns the numer of items in the basket
	 * 
	 * @return the numer of items in the basket
	 */
	public int size() {
		return this.items.size();
	}

	public void writeToParcel(final Parcel dest, final int flags) {
		for (final IProduct i : this.items.keySet()) {
			dest.writeString(i.toString());
			dest.writeInt(this.items.get(i));
		}
	}

}
