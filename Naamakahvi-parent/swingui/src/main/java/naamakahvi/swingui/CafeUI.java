package naamakahvi.swingui;
import javax.imageio.ImageIO;
import javax.swing.*;

import naamakahvi.naamakahviclient.*;
import naamakahvi.swingui.FaceCapture.FaceCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Käyttöliittymän pääluokka, joka huolehtii tiedonsiirrosta clientin ja näkymien
 * välillä sekä käsittelee käyttäjän syötteet
 */
public class CafeUI extends JFrame{
	//Käyttöliittymän näkymät
	protected JPanel container;
	private CardLayout viewSwitcher;
	private FrontPage front;
	private MenuPage menu;
	private PurchaseCartPage buycart, bringcart;
	private CheckoutPage checkout;
	private RegistrationPage register;
	private ManLoginPage manual;
	private StationSelect stations;
	private UserListPage userlist;
	private FaceLoginPage facelogin;
	private AddPicturePage add;
	//Sivu, jolle jatketaan välisivuilta (esim. käyttäjälistasta)
	protected String continueLocation = VIEW_FRONT_PAGE;
	//Juuri nyt esillä oleva sivu
	protected String currentLocation;
	
	//veloitukseen tarvittavat tiedot
	private FaceCapture face;
	private Client cli;
	private String[] usernames = new String[1];
	private IUser user;
	private IProduct[] selProd;
	private int[] quantities;
	private String mode;
	
	//käyttöiittymän resoluutio
	//public final int X_RES;
	//public final int Y_RES;
	//fontit
	public final Font UI_FONT;
	public final Font UI_FONT_BIG;
	public final Font UI_FONT_SMALL;
	
	//Näkymien tunnistamisessa käytetyt vakiot
	public static final String VIEW_FRONT_PAGE = "front";
	public static final String VIEW_REGISTRATION_PAGE = "register";
	public static final String VIEW_CHECKOUT_PAGE = "checkout";
	public static final String VIEW_MAN_LOGIN_PAGE = "manual";
	public static final String VIEW_BUY_LIST_PAGE = "buycart";
	public static final String VIEW_BRING_LIST_PAGE = "bringcart";
	public static final String VIEW_MENU_PAGE = "menu";
	public static final String VIEW_STATION_PAGE = "station";
	public static final String VIEW_USERLIST_PAGE = "userlist";
	public static final String VIEW_FACE_LOGIN_PAGE = "facelogin";
	public static final String VIEW_ADD_PICTURE_PAGE = "add";
	public static final String CONTINUE = "continue";
	
	//muut vakiot
	public static final int MAX_IDENTIFIED_USERS = 5;
	public static final String OUTPUT_IMAGE_FORMAT = "png";
	public static final String MODE_BUY = "buy";
	public static final String MODE_BRING = "bring";
	
	//Backendin osoite
	public final String ADDRESS_IP;
	public final int ADDRESS_PORT;
	
	/**Luo uuden Swing-käyttöliittymäikkunan ja näyttää Station-valintanäkymän
	 * 
	 */
	public CafeUI(int xres, int yres, int camera, boolean doFaceDetect, boolean camOffline, String ip, int port){
		JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
		loading.setFont(new Font("Sans Serif", Font.PLAIN, 20));
		add(loading);
		
		UI_FONT = new Font("Sans Serif", Font.PLAIN, 20);
		UI_FONT_BIG = new Font("Sans Serif", Font.PLAIN, 30);
		UI_FONT_SMALL = new Font("Sans Serif", Font.PLAIN, 15);
		
		ADDRESS_IP = ip;
		ADDRESS_PORT = port;
		
		container = new JPanel();
		
		viewSwitcher = new CardLayout();
		container.setLayout(viewSwitcher);
		
		add(container);
		
		face = new FaceCapture(camera, doFaceDetect, camOffline);
		new Thread(face).start();
		
		try {
			stations = new StationSelect(this, 
					Client.listStations(ip, port));
			System.out.println("Stations received");
			container.add(stations, VIEW_STATION_PAGE);
			viewSwitcher.show(container, VIEW_STATION_PAGE);
			remove(loading);
			validate();
		}
		catch (ClientException e){
			System.err.println("Error: Station information not received from server");
			System.err.println(e.getMessage());
			return;
		}
		
	}
	
	protected void createStore(String station){
		try{
			cli = new Client(ADDRESS_IP, ADDRESS_PORT, station);
		}
		catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		front = new FrontPage(this);
		container.add(front, VIEW_FRONT_PAGE);
		
		menu = new MenuPage(this);
		container.add(menu, VIEW_MENU_PAGE);
		
		buycart = new PurchaseCartPage(this, MODE_BUY);
		container.add(buycart, VIEW_BUY_LIST_PAGE);
		
		bringcart = new PurchaseCartPage(this, MODE_BRING);
		container.add(bringcart, VIEW_BRING_LIST_PAGE);
		
		checkout = new CheckoutPage(this);
		container.add(checkout, VIEW_CHECKOUT_PAGE);
		
		register = new RegistrationPage(this);
		container.add(register, VIEW_REGISTRATION_PAGE);
		
		manual = new ManLoginPage(this);
		container.add(manual, VIEW_MAN_LOGIN_PAGE);
		
		userlist = new UserListPage(this);
		container.add(userlist, VIEW_USERLIST_PAGE);
		
		facelogin = new FaceLoginPage(this);
		container.add(facelogin, VIEW_FACE_LOGIN_PAGE);
		
		add = new AddPicturePage(this);
		container.add(add, VIEW_ADD_PICTURE_PAGE);
		
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
			//front.resetPage();
			user = null;
			usernames = new String[1];
			currentLocation = page;
		}
		if (VIEW_MENU_PAGE.equals(page)){
			menu.setUser(usernames);
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
		if (VIEW_BUY_LIST_PAGE.equals(page)){
			buycart.setUsers(usernames);
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_BRING_LIST_PAGE.equals(page)){
			bringcart.setUsers(usernames);
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_CHECKOUT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			currentLocation = page;
			checkout.setUsers(usernames);
			checkout.setProducts(selProd, quantities, mode);
			checkout.startCountdown();
		}
		if (VIEW_FACE_LOGIN_PAGE.equals(page)){
			facelogin.activate();
			currentLocation = page;
			viewSwitcher.show(container, page);
		}
		if (VIEW_USERLIST_PAGE.equals(page)){
			try{
				userlist.listUsers(cli.listUsernames());
			}
			catch (ClientException e){
				e.printStackTrace();
				userlist.setHelpText(e.getMessage());
			}
			viewSwitcher.show(container, page);
			currentLocation = page;
		}
		if (VIEW_ADD_PICTURE_PAGE.equals(page)){
			add.setUsers(usernames);
			viewSwitcher.show(container, page);
			currentLocation = page;
			add.activate();
		}
	}
	
	protected List<SaldoItem> getSaldo(){
		if (user == null){
			System.err.println("null user");
			return null;
		}
		return user.getBalance();
	}
	
	protected void setPurchaseMode(String mode){
		if (MODE_BUY.equals(mode))
				this.mode = mode;
		else if (MODE_BRING.equals(mode))
				this.mode = mode;
		else {
			throw new IllegalArgumentException(
					"Attempted to set purchase mode to neither buy nor bring");
			
		}
	}
	
	protected FaceCanvas getCanvas(){
		return face.getCanvas();
	}
	
	protected boolean buyProduct(IProduct prod, int quantity){
		try{
			if (mode.equals(MODE_BUY)){
				cli.buyProduct(user, prod, quantity);
				front.setHelpText("Transaction successful");
			}
			else if (mode.equals(MODE_BRING)){
				cli.bringProduct(user, prod, quantity);
				front.setHelpText("Transaction successful");
			}
			else {
				throw new RuntimeException(
						"Attempted to buy wit illegal buy mode");
			}
			return true;
		}
		catch (ClientException e){
			e.printStackTrace();
			checkout.setHelpText(e.getMessage());
			front.setHelpText("");
			return false;
		}
	}
	
	protected boolean registerUser(String userName, String givenName,
			String familyName, BufferedImage[] images){
		try {
			ByteArrayOutputStream[] streams = new ByteArrayOutputStream[images.length];
			for (int i = 0; i < images.length; i++){
				if (images[i] != null){
					streams[i] = new ByteArrayOutputStream();
					ImageIO.write(resizeImage(images[i]), OUTPUT_IMAGE_FORMAT, streams[i]);
				}
			}
			
			cli.registerUser(userName, givenName, familyName);
			for (int i = 0; i< streams.length; i++){
				if (streams[i] != null)
					cli.addImage(userName, streams[i].toByteArray());
			}
			front.setHelpText("Registered user " + userName);
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
	
	protected boolean addImages(BufferedImage[] images){
		try{
			ByteArrayOutputStream[] streams = new ByteArrayOutputStream[images.length];
			for (int i = 0; i < images.length; i++){
				if (images[i] != null){
					streams[i] = new ByteArrayOutputStream();
					ImageIO.write(resizeImage(images[i]), OUTPUT_IMAGE_FORMAT, streams[i]);
					cli.addImage(user.getUserName(), streams[i].toByteArray());
				}
			}
			return true;
		}
		catch (ClientException ex) {
			add.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		} catch (IOException ex) {
			add.setHelpText("Error: " + ex.getMessage());
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
			user = cli.getUser(userName);
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
			System.err.println(e.getMessage());
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
	
	protected void selectProduct(IProduct[] prods, int[] amounts){
		selProd = prods;
		quantities = amounts;
	}
	
	protected BufferedImage takePic(){
		return face.takePic();
	}
	
	protected boolean validateImage(){
		BufferedImage img = face.takePic();
		if (img == null){
			facelogin.setHelpText("No faces detected");
			return false;
		}
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(resizeImage(img), OUTPUT_IMAGE_FORMAT, stream);
			byte[] output = stream.toByteArray();
			usernames = cli.identifyImage(output);
			System.out.println(usernames.length);
			loginUser(usernames[0]);
		}
		catch (ClientException e){
			e.printStackTrace();
			facelogin.setHelpText(e.getMessage());
			return false;
		}
		catch (IOException e){
			e.printStackTrace();
			facelogin.setHelpText("Error: Unable to convert image to byte array");
			return false;
		}
		
		
		if (usernames.length > MAX_IDENTIFIED_USERS){
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
		int xres = 800;
		int yres = 600;
		int cam = 0;
		boolean doFaceDetect = false;
		boolean camOffline = false;
		String ip = "naama.zerg.fi";
		int port = 5001;
		for(int i = 0; i < args.length; i++){
			if (args[i].length() > 5 && 
					"xres:".equalsIgnoreCase(args[i].substring(0, 5))){
				try{
					xres = Integer.parseInt(args[i].substring(5));
				}
				catch (NumberFormatException e){
					System.err.println(
							"Error: could not parse horizontal resolution: not an integer\n" +
							"Defaulting to 800");
				}
			}
			if (args[i].length() > 5 && 
					"yres:".equalsIgnoreCase(args[i].substring(0, 5))){
				try{
					yres = Integer.parseInt(args[i].substring(5));
				}
				catch (NumberFormatException e){
					System.err.println(
							"Error: could not parse vertical resolution: not an integer\n" +
							"Defaulting to 600");
				}
			}
			if (args[i].length() > 4 && 
					"cam:".equalsIgnoreCase(args[i].substring(0, 4))){
				try{
					cam = Integer.parseInt(args[i].substring(4));
				}
				catch (NumberFormatException e){
					System.err.println(
							"Error: could not parse camera index: not an integer\n" +
							"Defaulting to 0");
				}
			}
			if (args[i].length() > 3 && 
					"ip:".equalsIgnoreCase(args[i].substring(0, 3))){
				ip = args[i].substring(3);
			}
			if (args[i].length() > 5 && 
					"port:".equalsIgnoreCase(args[i].substring(0, 5))){
				try{
					port = Integer.parseInt(args[i].substring(5));
				}
				catch (NumberFormatException e){
					System.err.println(
							"Error: could not parse port number: not an integer\n" +
							"Defaulting to 5001");
				}
			}
			if ("dofacedetect".equalsIgnoreCase(args[i])){
				doFaceDetect = true;
			}
			if ("nocam".equalsIgnoreCase(args[i])){
				camOffline = true;
			}
		}
		
		CafeUI window = new CafeUI(xres, yres, cam, doFaceDetect, camOffline, ip, port);
		window.setTitle("Facecafe");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setSize(xres, yres);
		
	}
}
