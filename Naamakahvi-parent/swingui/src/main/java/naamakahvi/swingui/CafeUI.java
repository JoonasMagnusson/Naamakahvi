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
 * The main class of the Facecafe swingui component. Takes care of user
 * interface program logic, contains all of the various views used by swingui
 * and acts as an interface between the user and the program client.
 */
public class CafeUI extends JFrame{
	//Interface views; all of the different pages the user can interact with
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

	/**
	 * A String containing The page that should be shown after exiting certain 
	 * interstitial views (such as the user list page).
	 * When the switchPage() method is called with the CONTINUE constant,
	 * the page indicated by this variable is shown.
	 * Storing any other Strings than location constants (VIEW_<page name>_PAGE)
	 * in this variable will have no effect.
	 */
	protected String continueLocation = VIEW_FRONT_PAGE;
	private String currentLocation;
	
	//Variables for storing and processing user, product and transaction data
	private FaceCapture face;
	private Client client;
	private String[] usernames = new String[1];
	private IUser user;
	private IProduct[] selProd;
	private int[] quantities;
	private String mode;
	
	//Fonts
	/**
	 * The standard font size used by the UI
	 */
	public final Font UI_FONT;
	/**
	 * The large font size used by the UI
	 */
	public final Font UI_FONT_BIG;
	/**
	 * The small font size used by the UI
	 */
	public final Font UI_FONT_SMALL;
	
	//Constants used to identify the various pages in the swingui interface
	/**
	 * A String identifying the the front page view of the UI
	 */
	public static final String VIEW_FRONT_PAGE = "front";
	/**
	 * A String identifying the the registration page view of the UI
	 */
	public static final String VIEW_REGISTRATION_PAGE = "register";
	/**
	 * A String identifying the the checkout page view of the UI
	 */
	public static final String VIEW_CHECKOUT_PAGE = "checkout";
	/**
	 * DEPRECATED: replaced by VIEW_USERLIST_PAGE
	 * A String identifying the the manual login page view of the UI
	 */
	public static final String VIEW_MAN_LOGIN_PAGE = "manual";
	/**
	 * A String identifying the the buy cart page view of the UI
	 */
	public static final String VIEW_BUY_LIST_PAGE = "buycart";
	/**
	 * A String identifying the the bring cart page view of the UI
	 */
	public static final String VIEW_BRING_LIST_PAGE = "bringcart";
	/**
	 * A String identifying the the logged in user menu page view of the UI
	 */
	public static final String VIEW_MENU_PAGE = "menu";
	/**
	 * A String identifying the the station selection page view of the UI
	 */
	public static final String VIEW_STATION_PAGE = "station";
	/**
	 * A String identifying the the user list page view of the UI
	 */
	public static final String VIEW_USERLIST_PAGE = "userlist";
	/**
	 * A String identifying the the face login page view of the UI
	 */
	public static final String VIEW_FACE_LOGIN_PAGE = "facelogin";
	/**
	 * A String identifying the the picture addition page view of the UI
	 */
	public static final String VIEW_ADD_PICTURE_PAGE = "add";
	/**
	 * A String used in conjunction with the switchPage method and the
	 * continueLocation variable to allow interstitial pages such as the login
	 * pages to use multiple continue locations depending on the use case
	 */
	public static final String CONTINUE = "continue";
	
	//Other constants
	/**
	 * The maximum amount of possible face matches that can be returned from
	 * the server before asking the user to login through the user list instead
	 */
	public static final int MAX_IDENTIFIED_USERS = 5;
	/**
	 * A String indicating the encoding used when sending images to server
	 * NOTE: OpenJDK can't do jpg compression, so using another image format
	 * is recommended
	 * 
	 * @See		javax.imageio.ImageIO
	 */
	public static final String OUTPUT_IMAGE_FORMAT = "png";
	/**
	 * A String that should be passed to methods to indicate buying products
	 */
	public static final String MODE_BUY = "buy";
	/**
	 * A String that should be passed to methods to indicate bringing products
	 */
	public static final String MODE_BRING = "bring";
	/**
	 * An integer indicating the resolution of images sent to the Facecafe server
	 */
	public static final int IMAGE_SIZE = 200;
	
	//Server address
	/**
	 * The ip or url of the Facecafe backend server
	 */
	public final String ADDRESS_IP;
	/**
	 * The port of the Facecafe backend server
	 */
	public final int ADDRESS_PORT;
	
	/**Creates a new swingui frame, attempts to contact server and displays the
	 * station selection view
	 * 
	 * @param camera		the number of the camera to use with face detection
	 * 						(see also: the FaceCapture class)
	 * @param font			the font size used by the UI for medium size text
	 * @param doFaceDetect	enables active face detection if set to true
	 * 						(see also: the FaceCapture class)
	 * @param camOffline	disables camera and skips trying to load any associated
	 * 						libraries and resources if set to true
	 * 						(see also: the FaceCapture class)
	 * @param ip			The ip or url of the Facecafe backend server
	 * @param port			The port of the Facecafe backend server
	 */
	public CafeUI(int camera, int font, boolean doFaceDetect, boolean camOffline, String ip, int port){
		JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
		loading.setFont(new Font("Sans Serif", Font.PLAIN, font));
		add(loading);
		
		UI_FONT = new Font("Sans Serif", Font.PLAIN, font);
		UI_FONT_BIG = new Font("Sans Serif", Font.PLAIN, font +10);
		UI_FONT_SMALL = new Font("Sans Serif", Font.PLAIN, font -5);
		
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
	/**Initializes a CafeUI object and creates all of the UI views.
	 * 
	 * @param client	The Client object that the CafeUI object should use
	 * 					in order to contact the server
	 */
	protected void createStore(Client client){
		this.client = client;
		
		front = new FrontPage(this);
		front.setName("front");
		container.add(front, VIEW_FRONT_PAGE);
		
		menu = new MenuPage(this);
		menu.setName("menu");
		container.add(menu, VIEW_MENU_PAGE);
		
		buycart = new PurchaseCartPage(this, MODE_BUY);
		buycart.setName("buycart");
		container.add(buycart, VIEW_BUY_LIST_PAGE);
		
		bringcart = new PurchaseCartPage(this, MODE_BRING);
		bringcart.setName("bringcart");
		container.add(bringcart, VIEW_BRING_LIST_PAGE);
		
		checkout = new CheckoutPage(this);
		checkout.setName("checkout");
		container.add(checkout, VIEW_CHECKOUT_PAGE);
		
		register = new RegistrationPage(this);
		register.setName("register");
		container.add(register, VIEW_REGISTRATION_PAGE);
		
		manual = new ManLoginPage(this);
		manual.setName("manual");
		container.add(manual, VIEW_MAN_LOGIN_PAGE);
		
		userlist = new UserListPage(this);
		userlist.setName("userlist");
		container.add(userlist, VIEW_USERLIST_PAGE);
		
		facelogin = new FaceLoginPage(this);
		facelogin.setName("facelogin");
		container.add(facelogin, VIEW_FACE_LOGIN_PAGE);
		
		add = new AddPicturePage(this);
		add.setName("add");
		container.add(add, VIEW_ADD_PICTURE_PAGE);
		
		switchPage(VIEW_FRONT_PAGE);
	}
	
	/**Switches the page currently shown to the user
	 * 
	 * @param page		A String identifying the view that should be shown.
	 * 					Accepts any of the view identifying constants defined
	 * 					in this class (VIEW_<page name>_PAGE) or the CONTINUE
	 * 					constant, which causes this function to recursively
	 * 					call itself with the contents of the continueLocation
	 * 					variable. Passing any other string will cause this
	 * 					method to do nothing.
	 */
	protected void switchPage(String page){
		if (CONTINUE.equals(page) && !CONTINUE.equals(continueLocation)){
			switchPage(continueLocation);
			currentLocation = continueLocation;
			continueLocation = VIEW_FRONT_PAGE;
		}
		if (VIEW_FRONT_PAGE.equals(page)){
			viewSwitcher.show(container, page);
			user = null;
			usernames = new String[1];
			currentLocation = page;
		}
		if (VIEW_MENU_PAGE.equals(page)){
			menu.setUser(usernames);
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
				userlist.listUsers(client.listUsernames());
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
	
	/**Gets the account balances of the currently logged in user
	 * 
	 * @return		A List of SaldoItem objects containing the account balances
	 * 				of the currently logged in user.
	 * 				Returns null if no one is currently logged in or the user has
	 * 				no associated balances.
	 */
	protected List<SaldoItem> getSaldo(){
		if (user == null){
			System.err.println("null user");
			return null;
		}
		return user.getBalance();
	}
	
	/**Sets the purchase mode of this CafeUI object. The purchase mode determines
	 * whether the object marks products purchased with the buyProduct method as
	 * bought or brought.
	 * 
	 * @param mode		A String indicating whether future transactions should
	 * 					be interpreted as buying or bringing products.
	 * 					CafeUI.MODE_BUY indicates buying, while CafeUI.MODE_BRING
	 * 					indicates bringing
	 * @throws	IllegalArgumentException	If the passed mode is neither
	 * 					CafeUI.MODE_BUY nor CafeUI.MODE_BRING
	 */
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
	/**Gets the current purchase mode of this CafeUI object
	 * 
	 * @return	Either Cafe.UI_MODE_BUY if the purchase mode is "buy",
	 * 			CafeUI.MODE_BRING if the mode is "bring" or null if the mode has
	 * 			not been set.
	 */
	protected String getPurchaseMode(){
		return mode;
	}
	/**Gets the currently displayed view.
	 * 
	 * @return	The view constant (CafeUI.VIEW_<page name>_PAGE) corresponding
	 * 			to the currently visible page.
	 */
	public String getCurrentLocation(){
		return currentLocation;
	}
	
	/**Gets a new FaceCanvas object associated with this CafeUI object's
	 * FaceCapture object.
	 * 
	 * @see		FaceCapture.FaceCanvas
	 * 
	 * @return	A newly created FaceCanvas object
	 */
	protected FaceCanvas getCanvas(){
		return face.getCanvas();
	}
	
	/**Sends a message to the Facecafe server indicating that the currently
	 * logged in user has bought or brought products. Use setPurchaseMode to
	 * set the purchase mode beforehand.
	 * 
	 * @param prod		The product that is being bought or brought
	 * @param quantity	The amount of products of this type being bought or
	 * 					brought
	 * @return			A boolean indicating whether the transaction was successful
	 * @throws	RuntimeException	If the purchase mode has not been set
	 * 								or if the mode did not match the output of
	 * 								the product's isBuyable method
	 */
	protected boolean buyProduct(IProduct prod, int quantity){
		try{
			if (mode.equals(MODE_BUY)){
				if (!prod.isBuyable()){
					throw new RuntimeException(
							"Attempted to buy a raw product");
				}
				client.buyProduct(user, prod, quantity);
				front.setHelpText("Transaction successful");
			}
			else if (mode.equals(MODE_BRING)){
				if (prod.isBuyable()){
					throw new RuntimeException(
							"Attempted to bring a buyable product");
				}
				client.bringProduct(user, prod, quantity);
				front.setHelpText("Transaction successful");
			}
			else {
				throw new RuntimeException(
						"Attempted to buy with illegal buy mode");
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
	
	/**Creates a new user account on the Facecafe server
	 * 
	 * @param userName		The username of the account
	 * @param givenName		The user's given name
	 * @param familyName	The user's family name
	 * @param images		An array of BufferedImage objects containing images
	 * 						that should be associated with the new user account
	 * 						for face recognition purposes.
	 * @return				A boolean indicating whether or not the account was
	 * 						successfully created
	 */
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
			
			client.registerUser(userName, givenName, familyName);
			for (int i = 0; i< streams.length; i++){
				if (streams[i] != null)
					client.addImage(userName, streams[i].toByteArray());
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
	
	/**Associates images with the currently logged in user account for face
	 * recognition purposes
	 * 
	 * @param images	An array of BufferedImage objects containing the images
	 * 					to be added
	 * @return			A boolean indicating whether or not the images were
	 * 					successfully added
	 */
	protected boolean addImages(BufferedImage[] images){
		try{
			ByteArrayOutputStream[] streams = new ByteArrayOutputStream[images.length];
			for (int i = 0; i < images.length; i++){
				if (images[i] != null){
					streams[i] = new ByteArrayOutputStream();
					ImageIO.write(resizeImage(images[i]), OUTPUT_IMAGE_FORMAT, streams[i]);
					client.addImage(user.getUserName(), streams[i].toByteArray());
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
	
	/**Switches the currently logged in user to one of the best face matches
	 * returned during face login
	 * 
	 * @param username	The username that should be logged in
	 * @return			Returns true if the login was successful, returns false
	 * 					if the server returned an error or the username was not
	 * 					one of the best face matches
	 */
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
	
	/**Sets a user as the currently logged in user and fetches their account
	 * information from the server
	 * 
	 * @param userName	The username of the user that should be logged in
	 * @return			A boolean indicating whether the login was successful
	 */
	protected boolean loginUser(String userName){
		try {
			user = client.getUser(userName);
			this.usernames[0] = user.getUserName();
			return true;
		}
		catch (ClientException ex) {
			manual.setHelpText("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	/**Fetches a list of buyable products from the server.
	 * 
	 * @return	A List of IProduct objects containing the currently buyable
	 * 			products.
	 */
	protected List<IProduct> getBuyableProducts(){
		try{
			return client.listBuyableProducts();
		}
		catch (ClientException e){
			e.printStackTrace();
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	/**Fetches a list of raw products from the server.
	 * 
	 * @return	A List of IProduct objects containing the products that can
	 * 			currently be brought
	 */
	protected List<IProduct> getRawProducts(){
		try{
			return client.listRawProducts();
		}
		catch (ClientException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**Selects products. The selected products can be bought or brought from
	 * the checkout view.
	 * 
	 * @param prods
	 * @param amounts
	 */
	protected void selectProduct(IProduct[] prods, int[] amounts){
		selProd = prods;
		quantities = amounts;
	}
	
	/**Uses this CafeUI object's associated FaceCapture object to take a picture
	 * of a face currently visible on the system's webcam
	 * 
	 * @return	A BufferedImage representation of a face or null, if no faces
	 * 			were detected or the camera is not online
	 */
	protected BufferedImage takePic(){
		return face.takePic();
	}
	
	/**Takes a picture using the system's webcam, finds a face from it and logs
	 * in an user account associated with the face
	 * 
	 * @return	A boolean indicating whether the login was successful
	 */
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
			usernames = new String[1];
			usernames[0] = client.identifyImage(output);
			//no face matches
			if (usernames[0] == null){
				facelogin.setHelpText("Unknown face");
				return false;
			}
			/*DEPRECATED			
			//Too many matches, trim to 5
			if (usernames.length > MAX_IDENTIFIED_USERS){
				String[] temp = new String[MAX_IDENTIFIED_USERS];
				for(int i = 0; i < MAX_IDENTIFIED_USERS; i++){
					temp[i] = usernames[i];
				}
				usernames = temp;
			}*/
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
		
		switchPage(CONTINUE);
		return true;
	}
	
	/**Resizes an image to a size that can be sent to the server.
	 * The resolution of the resized image is determined by the IMAGE_SIZE
	 * constant.
	 * 
	 * @param img	A BufferedImage object that should be resized
	 * @return		A resized BufferedImage
	 */
	protected BufferedImage resizeImage(BufferedImage img){
		BufferedImage resize = new BufferedImage(200,200,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = resize.createGraphics();
		g2.drawImage(img, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
		g2.dispose();
		return resize;
	}
	
	/**The main method of the CafeUI class. Creates a new Facecafe UI using
	 * command line parameters.
	 * 
	 * @param args	Command line parameters (remove quotes):
	 * 		"xres:"number	The initial horizontal resolution of the new frame
	 * 		"yres:"number	The initial vertical resolution of the new frame
	 * 		"font":number	The size used by the UI for medium-size text (minimum 10)
	 * 		"cam:"number	The number of the camera that should be used for
	 * 						face detection
	 * 		"ip:"string		The url of the Facecafe server
	 * 		"port:"number	The port of the Facecafe server
	 * 		"dofacedetect"	Activate active face detection
	 * 		"nocam"			Disable camera functionality
	 */
	public static void main(String[] args) {
		int xres = 800;
		int yres = 600;
		int font = 20;
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
			if (args[i].length() > 5 && 
					"font:".equalsIgnoreCase(args[i].substring(0, 5))){
				try{
					font = Integer.parseInt(args[i].substring(5));
				}
				catch (NumberFormatException e){
					System.err.println(
							"Error: could not parse font size: not an integer\n" +
							"Defaulting to 20");
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
		if (font < 10){
			font = 10;
			System.out.println("Font size too small; defaulting to 10");
		}
		
		CafeUI window = new CafeUI(cam, font, doFaceDetect, camOffline, ip, port);
		window.setTitle("Facecafe");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setSize(xres, yres);
		
	}
}
