package naamakahvi.swingui;
import javax.imageio.ImageIO;
import javax.swing.*;

import naamakahvi.naamakahviclient.*;
import naamakahvi.swingui.FaceCapture.FaceCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;
/**
 * Käyttöliittymän pääluokka, joka huolehtii tiedonsiirrosta clientin ja näkymien
 * välillä sekä käsittelee käyttäjän syötteet
 */
public class CafeUI extends JFrame{
	//Käyttöliittymän näkymät
	private JPanel container;
	private CardLayout viewSwitcher;
	private FrontPage front;
	private MenuPage menu;
	private PurchaseCartPage et;
	private CheckoutPage checkout;
	private RegistrationPage register;
	private ManLoginPage manual;
	private StationSelect stations;
	private UserListPage userlist;
	protected String continueLocation = VIEW_FRONT_PAGE;
	protected String currentLocation;
	
	//veloitukseen tarvittavat tiedot
	//toisteiset muuttujat (client ja cli, selectedProduct ja seProd, etc.
	//ovat olemassa väliaikaisesti testausta varten, kunnes backendilta saa
	//oikeaa dataa
	private FaceCapture face;
	//private CoffeeClient client;
	private Client cli;
	private String[] usernames = new String[1];
	private IUser user;
	private IProduct selProd;
	private int quantity;
	
	//käyttöiittymän resoluutio
	public final int X_RES = 800;
	public final int Y_RES = 600;
	//fontit
	public final Font UI_FONT = new Font("Helvetica", Font.PLAIN, 20);
	public final Font UI_FONT_BIG = new Font("Helvetica", Font.PLAIN, 28);
	public final Font UI_FONT_SMALL = new Font("Helvetica", Font.PLAIN, 16);
	
	//Näkymien tunnistamisessa käytetyt vakiot
	public static final String VIEW_FRONT_PAGE = "front";
	public static final String VIEW_REGISTRATION_PAGE = "register";
	public static final String VIEW_CHECKOUT_PAGE = "checkout";
	public static final String VIEW_MAN_LOGIN_PAGE = "manual";
	public static final String VIEW_PROD_LIST_PAGE = "et";
	public static final String VIEW_MENU_PAGE = "menu";
	public static final String VIEW_STATION_PAGE = "station";
	public static final String VIEW_USERLIST_PAGE = "userlist";
	public static final String CONTINUE = "continue";
	
	//muut vakiot
	public static final int MAX_IDENTIFIED_USERS = 5;
	public static final String OUTPUT_IMAGE_FORMAT = "png";
	
	//Backendin osoite
	public static final String ip = "naama.zerg.fi";
	public static final int port = 5001;
	
	/**Luo uuden Swing-käyttöliittymäikkunan ja näyttää Station-valintanäkymän
	 * 
	 */
	public CafeUI(){
		container = new JPanel();
		//client = new DummyClient();
		
		viewSwitcher = new CardLayout();
		container.setLayout(viewSwitcher);
		
		add(container);
		
		face = new FaceCapture();
		new Thread(face).start();
		
		try {
			stations = new StationSelect(this, 
					Client.listStations(ip, port));
			System.out.println("Stations received");
			container.add(stations, VIEW_STATION_PAGE);
			viewSwitcher.show(container, VIEW_STATION_PAGE);
		}
		catch (ClientException e){
			System.out.println("Warning: station information not received," +
							" creating client with null station");
			e.printStackTrace();
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
		
		et = new PurchaseCartPage(this);
		container.add(et, VIEW_PROD_LIST_PAGE);
		
		checkout = new CheckoutPage(this);
		container.add(checkout, VIEW_CHECKOUT_PAGE);
		
		register = new RegistrationPage(this);
		container.add(register, VIEW_REGISTRATION_PAGE);
		
		manual = new ManLoginPage(this);
		container.add(manual, VIEW_MAN_LOGIN_PAGE);
		
		userlist = new UserListPage(this);
		container.add(userlist, VIEW_USERLIST_PAGE);
		
		switchPage(VIEW_FRONT_PAGE);
	}
	
	protected void switchPage(String page){
		if (CONTINUE.equals(page) && !CONTINUE.equals(continueLocation)){
			switchPage(continueLocation);
			currentLocation = continueLocation;
			continueLocation = VIEW_FRONT_PAGE;
		}
		
		if (VIEW_FRONT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			front.resetPage();
			user = null;
			usernames = new String[1];
			currentLocation = page;
		}
		if (VIEW_MENU_PAGE.equals(page)){
			menu.setUser(usernames[0]);
			//menu.setCoffeeSaldo(client.getCoffeeSaldo(userName));
			//menu.setEspressoSaldo(client.getEspressoSaldo(userName));
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_MAN_LOGIN_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_REGISTRATION_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			register.activate();
			currentLocation = page;
		}
		if (VIEW_PROD_LIST_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_CHECKOUT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			currentLocation = page;
			checkout.setUsers(usernames);
			IProduct[] prods = new IProduct[1];
			prods[0] = selProd;
			int[] q = new int[1];
			q[0] = quantity;
			checkout.setProducts(prods, q);
			checkout.startCountdown();
		}
		if (VIEW_USERLIST_PAGE.equals(page)){
			try{
				userlist.listUsers(cli.listUsernames());
			}
			catch (ClientException e){
				e.printStackTrace();
				userlist.setHelpText(e.getMessage());
			}
			viewSwitcher.show(container, VIEW_USERLIST_PAGE);
			currentLocation = page;
		}
		
	}
	
	protected FaceCanvas getCanvas(){
		return face.getCanvas();
	}
	
	protected boolean buySelectedProduct(){
		return buyProduct(selProd, quantity);
	}
	
	protected boolean buyProduct(IProduct prod, int quantity){
		try{
			cli.buyProduct(user, prod, quantity);
			return true;
		}
		catch (ClientException e){
			e.printStackTrace();
			//checkout.setPurchaseText(e.getMessage());
			return false;
		}
	}
	
	protected boolean bringProduct(IProduct prod, int quantity){
		try{
			cli.bringProduct(user, prod, quantity);
			return true;
		}
		catch (ClientException e){
			//checkout.setPurchaseText(e.getMessage());
			return false;
		}
	}
	
	protected boolean RegisterUser(String userName, String givenName,
			String familyName, BufferedImage[] images){
		try {
			ByteArrayOutputStream[] streams = new ByteArrayOutputStream[images.length];
			for (int i = 0; i < images.length; i++){
				if (images[i] != null){
					streams[i] = new ByteArrayOutputStream();
					ImageIO.write(resizeImage(images[i]), OUTPUT_IMAGE_FORMAT, streams[i]);
				}
			}
			
			user = cli.registerUser(userName, givenName, familyName);
			for (int i = 0; i< streams.length; i++){
				cli.addImage(user.getUserName(), streams[i].toByteArray());
			}
			register.setHelpText("Registered user " + user.getUserName());
			this.usernames[0] = user.getUserName();
			return true;
		}
		catch (ClientException ex) {
			register.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		} catch (IOException ex) {
			register.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	protected boolean switchUser(String username){
		if (usernames.length < 2) return false;
		for (int i = 1; i < usernames.length; i++){
			if (usernames[i].equals(username)){
				usernames[i] = usernames[0];
				return loginUser(username);
			}
		}
		return false;
	}
	
	protected boolean loginUser(String userName){
		try {
			user = cli.authenticateText(userName);
			this.usernames[0] = user.getUserName();
			return true;
		}
		catch (ClientException ex) {
			manual.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	protected List<IProduct> getBuyableProducts(){
		try{
			return cli.listBuyableProducts();
		}
		catch (ClientException e){
			e.printStackTrace();
			return null;
		}
	}
	protected List<IProduct> getRawProducts(){
		try{
			return cli.listRawProducts();
		}
		catch (ClientException e){
			e.printStackTrace();
			return null;
		}
	}
	
	protected List<IProduct> getDefaultProducts(){
		try{
			//TODO
			return cli.listBuyableProducts();
		}
		catch (ClientException e){
			e.printStackTrace();
			return null;
		}
	}
	
	protected void selectProduct(IProduct prod, int count){
		selProd = prod;
		quantity = count;
	}
	
	protected BufferedImage takePic(){
		return face.takePic();
	}
	
	protected boolean validateImage(){
		BufferedImage img = face.takePic();
		if (img == null){
			front.setHelpText("No faces detected");
			return false;
		}
		String[] users = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(resizeImage(img), OUTPUT_IMAGE_FORMAT, stream);
			byte[] output = stream.toByteArray();
			users = cli.identifyImage(output);
			loginUser(users[0]);
		}
		catch (ClientException e){
			e.printStackTrace();
			front.setHelpText(e.getMessage());
			front.resetPage();
			return false;
		}
		catch (IOException e){
			e.printStackTrace();
			front.setHelpText("Error: Unable to convert image to byte array");
			front.resetPage();
			return false;
		}
		
		
		if (users.length > MAX_IDENTIFIED_USERS){
			switchPage(VIEW_USERLIST_PAGE);
		}
		else{
			switchPage(CONTINUE);
		}
		return true;
	}
	
	private BufferedImage resizeImage(BufferedImage img){
		BufferedImage resize = new BufferedImage(200,200,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = resize.createGraphics();
		g2.drawImage(img, 0, 0, 200, 200, null);
		g2.dispose();
		return resize;
	}
	
	protected void setContinueLocation(String loc){
		continueLocation = loc;
	}
	
	public static void main(String[] args) {
		CafeUI ikkuna = new CafeUI();
		ikkuna.setTitle("Facecafe");
		ikkuna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ikkuna.setVisible(true);
		ikkuna.setSize(800, 600);
	}
}