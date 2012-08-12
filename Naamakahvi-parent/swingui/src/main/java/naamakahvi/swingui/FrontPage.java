package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

/**A class implementing the front page view for the Facecafe swingui component.
 * The front page is the initial view presented to the user after the program
 * is initialized and allows the user to log in manually, create a new user
 * or select a product to purchase and proceed to login by face detection.
 * 
 * @author Antti Hietasaari
 *
 */
public class FrontPage extends JPanel implements ActionListener{
	private JButton register, manlogin, buycart, bringcart, add;
	private JButton[][] buyprodButtons, bringprodButtons;
	private IProduct[] buyproducts, bringproducts;
	private JLabel helpText;
	private JPanel buyProdView, bringProdView;
	private CafeUI master;
	/**
	 * The default help text shown to the user after the program launches.
	 */
	protected static final String DEFAULTHELP = "Select a product from below to start face recognition";
	/**Creates a new front page view.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The front page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public FrontPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		
		constraints.gridheight = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		helpText = new JLabel(DEFAULTHELP, SwingConstants.CENTER);
		helpText.setFont(master.UI_FONT);
		layout.setConstraints(helpText, constraints);
		add(helpText);
				
		constraints.gridheight = 7;
		constraints.gridwidth = 1;
		
		buyProdView = new JPanel();
		buyProdView.setLayout(new GridBagLayout());
		layout.setConstraints(buyProdView, constraints);
		add(buyProdView);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		bringProdView = new JPanel();
		bringProdView.setLayout(new GridBagLayout());
		layout.setConstraints(bringProdView, constraints);
		add(bringProdView);
		
		constraints.gridheight = 1;
		
		loadProducts();
		manlogin = new JButton("Manual Login");
		manlogin.setFont(master.UI_FONT_BIG);
		manlogin.addActionListener(this);
		
		register = new JButton("New User");
		register.setFont(master.UI_FONT_BIG);
		register.addActionListener(this);
		
		layout.setConstraints(manlogin, constraints);
		add(manlogin);
		
		constraints.gridwidth = 1;
		
		layout.setConstraints(register, constraints);
		add(register);
		
		add = new JButton("Add Pictures to Existing Account");
		add.setFont(master.UI_FONT);
		add.addActionListener(this);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(add, constraints);
		add(add);
	}
	
	/**Loads a list of buyable products from the server and generates the
	 * buttons for purchasing products.
	 */
	private void loadProducts(){
		
		List<IProduct> tempbuy = master.getBuyableProducts();
		List<IProduct> tempbring = master.getRawProducts();
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		GridBagLayout buyLayout = (GridBagLayout)buyProdView.getLayout();
		GridBagLayout bringLayout = (GridBagLayout)bringProdView.getLayout();
		
		buyProdView.removeAll();
		bringProdView.removeAll();
		
		JLabel buyheader = new JLabel("Buy Products:");
		buyheader.setFont(master.UI_FONT);
		buyLayout.setConstraints(buyheader, c);
		buyProdView.add(buyheader);
		
		JLabel bringheader = new JLabel("Bring Products:");
		bringheader.setFont(master.UI_FONT);
		bringLayout.setConstraints(bringheader, c);
		bringProdView.add(bringheader);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		buycart = new JButton("Buy Multiple Products");
		buycart.setFont(master.UI_FONT_SMALL);
		buycart.addActionListener(this);
		buyLayout.setConstraints(buycart, c);
		buyProdView.add(buycart);
		
		bringcart = new JButton("Bring Multiple Products");
		bringcart.setFont(master.UI_FONT_SMALL);
		bringcart.addActionListener(this);
		bringLayout.setConstraints(bringcart, c);
		bringProdView.add(bringcart);
		
		buyproducts = new IProduct[tempbuy.size()];
		buyprodButtons = new JButton[tempbuy.size()][5];
		tempbuy.toArray(buyproducts);
		
		bringproducts = new IProduct[tempbring.size()];
		bringprodButtons = new JButton[tempbring.size()][5];
		tempbring.toArray(bringproducts);
		
		for (int i = 0; i < buyproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(buyproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			buyLayout.setConstraints(prodname, c);
			buyProdView.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			buyLayout.setConstraints(prodline, c);
			buyProdView.add(prodline);
			
			for (int j = 0; j < 5; j++){
				buyprodButtons[i][j] = new JButton("" + (j+1));
				buyprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				buyprodButtons[i][j].addActionListener(this);
				buyprodButtons[i][j].setName("buy" + i + ":" + j);
				prodline.add(buyprodButtons[i][j]);
			}
		}
		
		buyProdView.revalidate();
		
		for (int i = 0; i <bringproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(bringproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			bringLayout.setConstraints(prodname, c);
			bringProdView.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			bringLayout.setConstraints(prodline, c);
			bringProdView.add(prodline);
			
			for (int j = 0; j < 5; j++){
				bringprodButtons[i][j] = new JButton("" + (j+1));
				bringprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				bringprodButtons[i][j].addActionListener(this);
				bringprodButtons[i][j].setName("bring" + i + ":" + j);
				prodline.add(bringprodButtons[i][j]);
			}
		}
		bringProdView.revalidate();
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String text){
		helpText.setText(text);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == manlogin){
			setHelpText(DEFAULTHELP);
			master.continueLocation = CafeUI.VIEW_MENU_PAGE;
			master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
		}
		if (s == register){
			setHelpText(DEFAULTHELP);
			master.switchPage(CafeUI.VIEW_REGISTRATION_PAGE);
		}
		if (s == add){
			setHelpText(DEFAULTHELP);
			master.continueLocation = CafeUI.VIEW_ADD_PICTURE_PAGE;
			master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
		}
		if (s == buycart){
			setHelpText(DEFAULTHELP);
			master.setContinueLocation(CafeUI.VIEW_BUY_LIST_PAGE);
			master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
		}
		if (s == bringcart){
			setHelpText(DEFAULTHELP);
			master.setContinueLocation(CafeUI.VIEW_BRING_LIST_PAGE);
			master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
		}
		for (int i = 0; i < buyprodButtons.length; i++){
			for (int j = 0; j < buyprodButtons[i].length; j++){
				if (s == buyprodButtons[i][j]){
					//canvas.deactivate();
					setHelpText(DEFAULTHELP);
					IProduct[] prods = new IProduct[1];
					prods[0] = buyproducts[i];
					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BUY);
					master.selectProduct(prods, amounts);
					master.setContinueLocation(CafeUI.VIEW_CHECKOUT_PAGE);
					master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
				}
			}
		}
		for (int i = 0; i < bringprodButtons.length; i++){
			for (int j = 0; j < bringprodButtons[i].length; j++){
				if (s == bringprodButtons[i][j]){
					//canvas.deactivate();
					setHelpText(DEFAULTHELP);
					IProduct[] prods = new IProduct[1];
					prods[0] = bringproducts[i];
					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BRING);
					master.selectProduct(prods, amounts);
					master.setContinueLocation(CafeUI.VIEW_CHECKOUT_PAGE);
					master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
				}
			}
		}
	}
}