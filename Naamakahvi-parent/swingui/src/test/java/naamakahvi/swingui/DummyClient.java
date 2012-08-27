package naamakahvi.swingui;


import java.util.ArrayList;
import java.util.List;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.naamakahviclient.IUser;

public class DummyClient extends Client {
	public String registeredUN, registeredFN, registeredLN, addedTo, loggedIn;
	public String purchase;
	public int idResults = 0;
	
	public DummyClient(){
		super("999.999.999.999", -1, "station");
	}
	
	@Override
	public String[] listUsernames(){
		String[] ret = new String[10];
		for (int i =0; i < 10; i++){
			ret[i] = "user" + i;
		}
		return ret;
	}
	
	@Override
	public void registerUser(String username, String givenName,
            String familyName){
		registeredUN = username;
		registeredFN = givenName;
		registeredLN = familyName;
	}
	
	@Override
	public void addImage(String username, byte[] imagedata){
		addedTo = username;
	}
	
	@Override
	public IUser getUser(String username){
		loggedIn = username;
		return new FakeUser(username);
	}
	
	@Override
	public List<IProduct> listBuyableProducts(){
		ArrayList<IProduct> l = new ArrayList<IProduct>();
		for(int i = 0; i < 10; i++){
			l.add(new FakeProduct("buyprod" + i, i, i, i, true));
		}
		
		return l;
	}
	
	@Override
	public List<IProduct> listRawProducts(){
		ArrayList<IProduct> l = new ArrayList<IProduct>();
		for(int i = 0; i < 10; i++){
			l.add(new FakeProduct("bringprod" + i, i, i, i, false));
		}
		
		return l;
	}
	
	@Override
	public void buyProduct(IUser user, IProduct product, int amount){
		purchase = user.getUserName() + " bought " + amount + "x " + product.getName();
	}
	
	@Override
	public void bringProduct(IUser user, IProduct product, int amount){
		purchase = user.getUserName() + " brought " + amount + "x " + product.getName();
	}
	
	public String identifyImage(byte[] imagedata){
		return "username";
	}
}
