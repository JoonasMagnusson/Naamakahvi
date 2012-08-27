package naamakahvi.android.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import naamakahvi.naamakahviclient.IProduct;

/**
 * Library class used for client-side caching of products.
 */
public class ProductCache {

	private static HashMap<String, IProduct> mNameCache = new HashMap<String, IProduct>();;
	private static List<IProduct> mBuyableList;
	private static List<IProduct> mRawList;

	private static boolean mBuyableReady = false;
	private static boolean mRawReady = false;

	/**
	 * Get product information from the cache.
	 * 
	 * @param name
	 *            The toString of the product
	 * @return Product data for the given product name
	 */
	public static IProduct getProduct(final String name) {
		if (!isReady()) {
			throw new IllegalStateException();
		}
		return mNameCache.get(name);
	}

	/**
	 * Returns whether the cache is ready for use
	 * 
	 * @return True if products have been loaded, false otherwise
	 */
	public static boolean isReady() {
		return mBuyableReady && mRawReady;
	}

	/**
	 * Returns a list of buyable items
	 * 
	 * @return List of buyable items
	 */
	public static List<IProduct> listBuyableItems() {
		if (!mBuyableReady) {
			throw new IllegalStateException("Buyable items not loaded");
		}
		return mBuyableList;
	}

	/**
	 * Returns a list of raw products
	 * 
	 * @return List of raw products
	 */
	public static List<IProduct> listRawItems() {
		if (!mRawReady) {
			throw new IllegalStateException("Raw products not loaded");
		}
		return mRawList;
	}

	/**
	 * Load buyable items into the product cache
	 * 
	 * @param products
	 *            List of products to cache
	 */
	public static void loadBuyableItems(final List<IProduct> products) {
		mBuyableList = new ArrayList<IProduct>();
		for (final IProduct p : products) {
			mNameCache.put(p.toString(), p);
			mBuyableList.add(p);
		}
		mBuyableReady = true;
	}

	/**
	 * Load raw products into the product cache
	 * 
	 * @param products
	 *            List of products to cache
	 */
	public static void loadRawItems(final List<IProduct> products) {
		mRawList = new ArrayList<IProduct>();
		for (final IProduct p : products) {
			mNameCache.put(p.toString(), p);
			mRawList.add(p);
		}
		mRawReady = true;
	}

}
