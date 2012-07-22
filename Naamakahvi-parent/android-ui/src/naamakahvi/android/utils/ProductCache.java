package naamakahvi.android.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import naamakahvi.naamakahviclient.IProduct;

public class ProductCache {

	private static HashMap<String,IProduct> mNameCache = new HashMap<String, IProduct>();;
	private static List<IProduct> mBuyableList;
	private static List<IProduct> mRawList;
	
	
	//TODO add timestamp
	
	
	
	private static boolean mBuyableReady = false;
	private static boolean mRawReady = false;
	
	public static void loadBuyableItems(List<IProduct> products){
		mBuyableList = new ArrayList<IProduct>();
		for (IProduct p : products){
			mNameCache.put(p.getName(),p);
			mBuyableList.add(p);
		}
		mBuyableReady = true;
	}
	
	public static void loadRawItems(List<IProduct> products){
		mRawList = new ArrayList<IProduct>();
		for (IProduct p : products){
			mNameCache.put(p.getName(),p);
			mRawList.add(p);
		}
		mRawReady = true;
	}
	
	public static List<IProduct> listBuyableItems(){
		if (!mBuyableReady) throw new IllegalStateException();
		return mBuyableList;
	}
	
	public static List<IProduct> listRawItems(){
		if (!mRawReady) throw new IllegalStateException();
		return mRawList;
	}
	
	
	public static boolean isReady() { return mBuyableReady&&mRawReady;}
	
	public static IProduct getProduct(String name){
		if (!isReady()) throw new IllegalStateException();
		return mNameCache.get(name);
	}
	
}
