package naamakahvi.swingui;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JPanel;

import naamakahvi.naamakahviclient.*;

public class DummyCafeUI extends CafeUI implements CloseableView{
	public String purchaseMode;
	public IProduct[] prods;
	public int[] amounts;
	public CardLayout l;
	public ArrayList<IProduct> boughtprods;
	public ArrayList<Integer> boughtamounts;
	public boolean pictureTaken = false;
	public String selectedUserUN;
	public String selectedUserFN;
	public String selectedUserLN;
	public boolean closed = false;
	public ArrayList<String> stations;
	public boolean uiCreated = false;
	public String location;
	
	public DummyCafeUI(){
		super(0, 15, false, true, "999.999.999.999", -1);
		//setVisible(false);
		container.removeAll();
		l = new CardLayout();
		container.setLayout(l);
		location = "initial";
		
	}
	
	public void add(JPanel p){
		container.add(p, "test");
		l.show(container, "test");
		setName("Facecafe");
	}
	
	@Override
	protected void createStore(Client client){
		uiCreated = true;
	}
	
	public void resetCreation(){
		uiCreated = false;
	}
	
	@Override
	protected void switchPage(String page){
		location = page;
	}
	
	@Override
	public String getCurrentLocation(){
		return location;
	}

	@Override
	protected void setPurchaseMode(String mode){
		purchaseMode = mode;
	}
	
	@Override
	protected boolean buyProduct(IProduct prod, int quantity){
		if (boughtprods == null){
			boughtprods = new ArrayList<IProduct>();
		}
		if (boughtamounts == null){
			boughtamounts = new ArrayList<Integer>();
		}
		boughtprods.add(prod);
		boughtamounts.add(quantity);
		return true;
	}
	
	
	@Override
	protected List<IProduct> getBuyableProducts(){
		ArrayList<IProduct> r = new ArrayList<IProduct>();
		for (int i = 0; i < 5; i++){
			FakeProduct p = new FakeProduct("BuyProd" + i, i, i + 0.5,
					i, true);
			r.add(p);
		}
		return r;
	}
	
	@Override
	protected List<IProduct> getRawProducts(){
		ArrayList<IProduct> r = new ArrayList<IProduct>();
		for (int i = 0; i < 5; i++){
			FakeProduct p = new FakeProduct("BringProd" + i, i, i + 0.5,
					i, false);
			r.add(p);
		}
		return r;
	}
	
	@Override 
	protected void selectProduct(IProduct[] prods, int[] quantities){
		this.prods = prods;
		amounts = quantities;
	}
	
	@Override
	protected boolean registerUser(String username, String givenname,
			String familyname, BufferedImage[] images){
		selectedUserUN = username;
		selectedUserFN = givenname;
		selectedUserLN = familyname;
		return true;
	}
	
	@Override
	protected BufferedImage takePic(){
		pictureTaken = true;
		return null;
	}
	
	@Override
	protected boolean validateImage(){
		pictureTaken = true;
		return true;
	}
	
	@Override
	protected boolean switchUser(String username){
		this.selectedUserUN = username;
		return true;
	}
	
	@Override
	protected boolean loginUser(String username){
		this.selectedUserUN = username;
		return true;
	}
	
	public IProduct[] generateProducts(){
		IProduct[] prods = new IProduct[5];
		for(int i = 0; i < prods.length; i++){
			prods[i] = new FakeProduct("prod" +i, i, 1.0, i, true);
		}
		return prods;
	}
	
	public void initStations(){
		stations = new ArrayList<String>();
		for (int i =0; i < 10; i++){
			stations.add("station" + i);
		}
	}

	public void closeView() {
		this.closed = true;
	}
	
}
