package naamakahvi.swingui;
import javax.swing.*;

import naamakahvi.naamakahviclient.*;

import java.awt.*;
/*
 * Käyttöliittymän pääluokka, joka huolehtii tiedonsiirrosta clientin ja näkymien
 * välillä sekä käsittelee käyttäjän syötteet
 */
public class CafeUI extends JFrame{
	//Käyttöliittymän näkymät
	private JPanel container;
	private CardLayout viewSwitcher;
	private FrontPage front;
	private MenuPage menu;
	private ETPage et;
	private CheckoutPage checkout;
	private RegistrationPage register;
	private ManLoginPage manual;
	private StationSelect stations;
	
	//veloitukseen tarvittavat tiedot
	private CoffeeClient client;
	private Client cli;
	private String userName = "test";
	private IUser user;
	private String selectedProduct;
	private int quantity;
	
	//käyttöiittymän resoluutio
	public static final int X_RES = 800;
	public static final int Y_RES = 600;
	//fontit
	public static final Font UI_FONT = new Font("Arial", Font.PLAIN, 20);
	public static final Font UI_FONT_BIG = new Font("Arial", Font.PLAIN, 28);
	public static final Font UI_FONT_SMALL = new Font("Arial", Font.PLAIN, 16);
	
	//Näkymien tunnistamisessa käytetyt vakiot
	public static final String VIEW_FRONT_PAGE = "front";
	public static final String VIEW_REGISTRATION_PAGE = "register";
	public static final String VIEW_CHECKOUT_PAGE = "checkout";
	public static final String VIEW_MAN_LOGIN_PAGE = "manual";
	public static final String VIEW_PROD_LIST_PAGE = "et";
	public static final String VIEW_MENU_PAGE = "menu";
	public static final String VIEW_STATION_PAGE = "station";
	
	//Backendin osoite
	public static final String ip = "0.0.0.0";
	public static final int port = 5000;
	
	public CafeUI(){
		container = new JPanel();
		client = new DummyClient();
		
		viewSwitcher = new CardLayout();
		container.setLayout(viewSwitcher);
		
		add(container);
		
		try {
			stations = new StationSelect(this, 
					Client.listStations(ip, port));
			container.add(stations, VIEW_STATION_PAGE);
			viewSwitcher.show(container, VIEW_STATION_PAGE);
		}
		catch (ClientException e){
			System.out.println("Warning: station information not received," +
							" creating client with null station");
			createStore(null);
		}
		
	}
	
	protected void createStore(IStation station){
		try{
			cli = new Client(ip, port, station);
		}
		catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		front = new FrontPage(this);
		container.add(front, VIEW_FRONT_PAGE);
		
		menu = new MenuPage(this);
		container.add(menu, VIEW_MENU_PAGE);
		
		et = new ETPage(this);
		container.add(et, VIEW_PROD_LIST_PAGE);
		
		checkout = new CheckoutPage(this);
		container.add(checkout, VIEW_CHECKOUT_PAGE);
		
		register = new RegistrationPage(this);
		container.add(register, VIEW_REGISTRATION_PAGE);
		
		manual = new ManLoginPage(this);
		container.add(manual, VIEW_MAN_LOGIN_PAGE);
		
		viewSwitcher.show(container, VIEW_FRONT_PAGE);
	}
	
	protected void switchPage(String page){
		if (VIEW_FRONT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			front.resetPage();
			user = null;
			userName = "test";
		}
		if (VIEW_MENU_PAGE.equals(page)){
			menu.setUser(userName);
			menu.setCoffeeSaldo(client.getCoffeeSaldo(userName));
			menu.setEspressoSaldo(client.getEspressoSaldo(userName));
			viewSwitcher.show(container, page);
		}
		if (VIEW_MAN_LOGIN_PAGE.equals(page)){
			viewSwitcher.show(container, page);
		}
		if (VIEW_REGISTRATION_PAGE.equals(page)){
			viewSwitcher.show(container, page);
		}
		if (VIEW_PROD_LIST_PAGE.equals(page)){
			viewSwitcher.show(container, page);
		}
		if (VIEW_CHECKOUT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			if (user != null){
			checkout.setPurchaseText("Käyttäjältä " + user.getUserName() +
					" veloitetaan " + quantity + " kpl tuotetta " +
					selectedProduct);
			}
			else
				checkout.setPurchaseText("Käyttäjältä " + userName +
						" veloitetaan " + quantity + " kpl tuotetta " +
						selectedProduct);
			checkout.startCountdown();
		}
		
	}
	
	protected boolean buyProduct(){
		//TODO
		return true;
	}
	
	protected boolean RegisterUser(String userName, String givenName,
			String familyName){
		try {
			user = cli.registerUser(userName, givenName, familyName, null);
			register.setHelpText("Registered user " + user.getUserName());
			this.userName = user.getUserName();
			return true;
		}
		catch (ClientException ex) {
			register.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	protected boolean LoginUser(String userName){
		try {
			user = cli.authenticateText(userName);
			this.userName = user.getUserName();
			return true;
		}
		catch (ClientException ex) {
			manual.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	protected void selectProduct(String prod, int count){
		selectedProduct = prod;
		quantity = count;
	}
	
	public static void main(String[] args) {
		CafeUI ikkuna = new CafeUI();
		ikkuna.setTitle("Naamakahvi");
		ikkuna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ikkuna.setVisible(true);
		ikkuna.setSize(X_RES, Y_RES);
	}
}