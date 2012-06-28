package naamakahvi.swingui;
import javax.swing.*;

import naamakahvi.naamakahviclient.Client;
import naamakahvi.naamakahviclient.IUser;

import java.awt.*;
import java.awt.event.*;
/*
 * Käyttöliittymän pääluokka, joka huolehtii tiedonsiirrosta clientin ja näkymien
 * välillä sekä käsittelee käyttäjän syötteet
 */
public class CafeUI extends JFrame implements ActionListener{
	//Käyttöliittymän näkymät
	private JPanel container;
	private CardLayout viewSwitcher;
	private FrontPage front;
	private ConfirmationPage confirm;
	private MenuPage menu;
	private ETPage et;
	private CheckoutPage checkout;
	private RegistrationPage register;
	
	//veloitukseen tarvittavat tiedot
	private CoffeeClient client;
	private String userName;
	private String selectedProduct;
	private int quantity;
	
	//käyttöiittymän resoluutio
	public static final int X_RES = 640;
	public static final int Y_RES = 480;
	//fontit
	public static final Font UI_FONT = new Font("Arial", Font.PLAIN, 20);
	public static final Font UI_FONT_BIG = new Font("Arial", Font.PLAIN, 28);
	//käyttöliittymän nappien tunnistamiseen tarvittavat vakiot
	public static final int BUTTON_START = 1;
	public static final int BUTTON_REGISTER_VIEW = 2;
	public static final int BUTTON_CANCEL = 3;
	
	public static final int BUTTON_CONFIRM_USER = 4;
	
	public static final int BUTTON_BUY_COFFEE = 5;
	public static final int BUTTON_BUY_ESPRESSO = 6;
	public static final int BUTTON_ET = 7;
	
	public static final int BUTTON_CONFIRM_PURCHASE = 8;
	
	public static final int BUTTON_REGISTER_USER = 9;
	
	public CafeUI(CoffeeClient client){
		setLayout(new BorderLayout());
		container = new JPanel();
		this.client = client;
		
		viewSwitcher = new CardLayout();
		container.setLayout(viewSwitcher);
		
		front = new FrontPage(this);
		container.add(front, "front");
		
		confirm = new ConfirmationPage(this);
		container.add(confirm, "confirm");
		
		menu = new MenuPage(this);
		container.add(menu, "menu");
		
		et = new ETPage(this);
		container.add(et, "et");
		
		checkout = new CheckoutPage(this);
		container.add(checkout, "checkout");
		
		register = new RegistrationPage(this);
		container.add(register, "register");
		
		viewSwitcher.show(container, "front");
		add(container);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().getClass() == IDButton.class){
			IDButton source = (IDButton)e.getSource();
			
			if (source.getID() == BUTTON_START){
				userName = client.recogUser();
				confirm.setUser(userName);
				viewSwitcher.show(container, "confirm");
			}
			if (source.getID() == BUTTON_REGISTER_VIEW){
				viewSwitcher.show(container, "register");
			}
			if (source.getID() == BUTTON_CANCEL){
				checkout.stopCountdown();
				viewSwitcher.show(container, "front");
			}
			if (source.getID() == BUTTON_CONFIRM_USER){
				menu.setUser(userName);
				menu.setCoffeeSaldo(client.getCoffeeSaldo(userName));
				menu.setEspressoSaldo(client.getEspressoSaldo(userName));
				viewSwitcher.show(container, "menu");
			}
			if (source.getID() == BUTTON_BUY_COFFEE){
				checkout.setPurchaseText("Tililtäsi veloitetaan 1 kahvi");
				selectedProduct = "kahvi";
				quantity = 1;
				checkout.startCountdown();
				viewSwitcher.show(container, "checkout");
			}
			if (source.getID() == BUTTON_BUY_ESPRESSO){
				checkout.setPurchaseText("Tililtäsi veloitetaan 1 espresso");
				selectedProduct = "espresso";
				quantity = 1;
				checkout.startCountdown();
				viewSwitcher.show(container, "checkout");
			}
			if (source.getID() == BUTTON_ET){
				viewSwitcher.show(container, "et");
			}
			if (source.getID() == BUTTON_CONFIRM_PURCHASE){
				checkout.stopCountdown();
				client.purchaseProduct(userName, selectedProduct, quantity);
				viewSwitcher.show(container, "front");
			}
			if (source.getID() == BUTTON_REGISTER_USER){
				try {
					Client c = new Client("127.0.0.1", 5000, null);
					IUser user = c.registerUser(register.getUsername(), null);
					register.setHelpText("Registered user " + user.getUserName());
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		CafeUI ikkuna = new CafeUI(new DummyClient());
		ikkuna.setTitle("Kahviproto");
		ikkuna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ikkuna.setVisible(true);
		ikkuna.setSize(X_RES, Y_RES);
	}
}