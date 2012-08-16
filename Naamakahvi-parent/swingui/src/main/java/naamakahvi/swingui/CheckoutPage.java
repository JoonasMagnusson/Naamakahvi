package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;
/**A class implementing the checkout view in the Facecafe swingui component.
 * Shows the logged in user as well as the selected products, allowing the
 * user to confirm their purchase or abort the transaction if the order
 * details are wrong.
 * 
 * @author Antti Hietasaari
 *
 */
public class CheckoutPage extends JPanel implements ActionListener, CloseableView{
	
	/**
	 * Indicates the timeout of the checkout page in seconds. If the checkout
	 * times out, the transaction is aborted.
	 */
	public static final int countdownLength = 90;
	
	private JLabel countdownText;
	private JButton ok, menu, cancel;
	/**
	 * The countdown timer for the checkout page.
	 */
	protected Timer countdownTimer;
	private int countdownSecs;
	private IProduct[] products;
	private int[] quantities;
	private CafeUI master;
	private JPanel purchasePanel;
	private ShortList userlist;
	protected boolean countingDown = false;
	
	/**Creates a new checkout page.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The checkout page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public CheckoutPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		constraints.gridheight = 6;
		constraints.gridwidth = 1;
		
		userlist = new ShortList(master, this);
		layout.setConstraints(userlist, constraints);
		add(userlist);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		purchasePanel = new JPanel();
		purchasePanel.setLayout(new GridLayout(0,1));
		layout.setConstraints(purchasePanel, constraints);
		add(purchasePanel);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 0.2;
		
		ok = new JButton("OK");
		ok.setFont(master.UI_FONT_BIG);
		ok.addActionListener(this);
		layout.setConstraints(ok, constraints);
		add(ok);	
		
		menu = new JButton("Change Products");
		menu.setFont(master.UI_FONT_BIG);
		menu.addActionListener(this);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		cancel.addActionListener(this);	
		layout.setConstraints(cancel, constraints);
		add(cancel);
		
		countdownText = new JLabel("Placeholder", SwingConstants.CENTER);
		countdownText.setFont(master.UI_FONT);
		layout.setConstraints(countdownText, constraints);
		add(countdownText);
		
		countdownTimer = new Timer(1000, this);
	}
	
	/**Sets the recognized users for this page. The first user on the list
	 * is assumed to be logged in.
	 * 
	 * @see	ShortList.setUsers()
	 * 
	 * @param usernames		A String array containing the usernames of the
	 * 						recognized users.
	 */
	protected void setUsers(String[] usernames){
		userlist.setUsers(usernames);
	}
	
	/**Sets the products that the user intends to buy. The selected products
	 * are shown on the interface and will be charged from the user's account
	 * when the OK button is clicked.
	 * 
	 * @param products		An array containing the IProduct objects that the
	 * 						user intends to purchase
	 * @param quantities	An integer array indicating how many of each 
	 * 						product the user intends to purchase. The array
	 * 						index of each quantity must be the same as the
	 * 						array index of the corresponding product in
	 * 						the product array parameter
	 * @param mode			A String indicating whether the product will be
	 * 						marked as bought or brought when the OK button is
	 * 						clicked. CafeUI.MODE_BUY indicates that the products
	 * 						will be bought, while CafeUI.MODE_BRING indicates
	 * 						that the products will be brought.
	 * @throws IllegalArgumentException		Thrown if either the product or
	 * 		quantities array is null, if the two arrays are different lengths,
	 * 		if the arrays are empty or the selected mode is neither 
	 * 		CafeUI.MODE_BUY nor CafeUI.MODE_BRING.
	 */
	protected void setProducts(IProduct[] products, int[] quantities, String mode){
		if (products == null || quantities == null ||
				products.length != quantities.length
				 || products.length < 1){
			throw new IllegalArgumentException("Unable to parse purchase parameters");
		}
		
		this.products = products;
		this.quantities = quantities;
		purchasePanel.removeAll();
		
		JLabel header;
		if (mode.equals(CafeUI.MODE_BUY)){
			header = new JLabel("Buying:");
		}
		else if (mode.equals(CafeUI.MODE_BRING)){
			header = new JLabel("Bringing:");
		}
		else{
			throw new IllegalArgumentException(
					"Mode passed to checkout was neither buy nor bring");
		}
		header.setFont(master.UI_FONT_BIG);
		header.setName("header");
		purchasePanel.add(header);
		
		for (int i = 0; i < products.length; i++){
			JLabel product = new JLabel(products[i].getName() + " x " + quantities[i]);
			product.setFont(master.UI_FONT);
			purchasePanel.add(product);
		}
		
		purchasePanel.add(menu);
		purchasePanel.revalidate();
		
	}
	/**
	 * Starts the checkout countdown. When the countdown finishes, the
	 * transaction will be automatically aborted.
	 */
	protected void startCountdown(){
		countdownSecs = countdownLength;
		countdownTimer.start();
		countdownText.setText("The transaction will be automatically canceled in " + 
				countdownSecs + " seconds");
		countingDown = true;
	}
	/**
	 * Closes the checkout view and stops the timer, preventing the timer from
	 * aborting the transaction. Should always be called before showing the
	 * user a different page. 
	 */
	public void closeView(){
		countdownTimer.stop();
		countingDown = false;
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String text){
		closeView();
		countdownText.setText(text);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == countdownTimer){
			countdownSecs--;
			if (countdownSecs > 0){
				countdownText.setText("The transaction will be automatically canceled in " + 
						countdownSecs + " seconds");
				countdownTimer.restart();
			}
			else {
				cancel.doClick();
			}
		}
		if (s == menu){
			closeView();
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
		if (s == cancel){
			closeView();
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == ok){
			closeView();
			boolean error = false;
			for (int i = 0; i < products.length;i++){
				if (!master.buyProduct(products[i], quantities[i])){
					error = true;
				}
			}
			if (!error){
				master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			}
			
		}
	}
}
