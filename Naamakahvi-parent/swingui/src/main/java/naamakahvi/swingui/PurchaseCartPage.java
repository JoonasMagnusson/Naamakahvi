package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**A class implementing a shopping cart view for the Facecafe swingui component.
 * The page allows the user to select multiple produts of different types to
 * buy or bring simultaneously.
 * 
 * @author Antti Hietasaari
 *
 */
public class PurchaseCartPage extends JPanel implements ActionListener, CloseableView{
	private JButton cancelButton, confirmButton, clearButton;
	
	private JButton[] prodButtons;
	private IProduct[] products;
	
	private ArrayList<IProduct> cartContents;
	private ArrayList<Integer> amounts;
	private ArrayList<JButton> removebuttons;
	private JLabel header;
	private ArrayList<JLabel> cartLabels;
	
	private JPanel cartPanel, menuView, cartView;
	private JScrollPane cartScroll, menuScroll;
	private CafeUI master;
	
	private GridBagLayout cartIntLayout;
	private GridBagConstraints cartIntConstraints;
	
	private String mode;
	private ShortList userlist;
	/**Creates a new shopping cart view.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The cart page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 * @param mode		A String indicating whether the view should be used
	 * 					to buy or bring products. CafeUI.MODE_BUY indicates
	 * 					that the cart is for buyable products while
	 * 					CAFEUI.MODE_BRING indicates that the cart view should
	 * 					be used to bring products.
	 * @throws IllegalArgumentException		The mode parameter passed to the
	 * 		constructor was neither CafeUI.MODE_BUY nor CafeUI.MODE_BRING.
	 */
	public PurchaseCartPage(CafeUI master, String mode){
		this.master = master;
		if (!mode.equals(CafeUI.MODE_BUY) && !mode.equals(CafeUI.MODE_BRING)){
			throw new IllegalArgumentException("selected purchase mode was neither buy nor bring");
		}
		this.mode = mode;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		
		constraints.gridheight = 9;
		constraints.gridwidth = 2;
		
		//User list
		
		userlist = new ShortList(master, this);
		layout.setConstraints(userlist, constraints);
		add(userlist);
		
		//Shopping cart
		
		cartPanel = new JPanel();
		GridBagLayout cartExtLayout = new GridBagLayout();
		cartPanel.setLayout(cartExtLayout);		
		
		cartView = new JPanel();
		cartIntLayout = new GridBagLayout();
		cartView.setLayout(cartIntLayout);
		cartIntConstraints = new GridBagConstraints();
		cartIntConstraints.weightx = 0.5;
		cartIntConstraints.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints cartExtConstraints = new GridBagConstraints();
		cartExtConstraints.fill = GridBagConstraints.BOTH;
		cartExtConstraints.weightx = 0.5;
		cartExtConstraints.weighty = 1.0;
		
		cartExtConstraints.gridheight = GridBagConstraints.RELATIVE;
		cartExtConstraints.gridwidth = GridBagConstraints.REMAINDER;
		cartScroll = new JScrollPane(cartView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cartExtLayout.setConstraints(cartScroll, cartExtConstraints);
		cartPanel.add(cartScroll);
		
		cartExtConstraints.gridheight = GridBagConstraints.REMAINDER;
		cartExtConstraints.weighty = 0.2;
		
		clearButton = new JButton("Clear Cart");
		clearButton.setFont(master.UI_FONT);
		clearButton.addActionListener(this);
		cartExtLayout.setConstraints(clearButton, cartExtConstraints);
		cartPanel.add(clearButton);
		
		String s = (mode.equals(CafeUI.MODE_BUY)?"Buying:":"Bring:");
		header = new JLabel(s);
		header.setFont(master.UI_FONT);
		
		clearCart();
		constraints.gridwidth = 3;
		layout.setConstraints(cartPanel, constraints);
		add(cartPanel);
		
		//Product list
		constraints.weightx = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		menuView = new JPanel();
		menuView.setLayout(new GridLayout(0, 1));
		menuScroll = new JScrollPane(menuView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setConstraints(menuScroll, constraints);
		add(menuScroll);
				
		loadProducts(mode);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 4;
		constraints.weightx = 0.0;
		constraints.weighty = 0.2;
		
		//confirm/cancel buttons
		
		confirmButton = new JButton();
		if (mode.equals(CafeUI.MODE_BUY)){
			confirmButton.setText("Buy Selected Products");
		}
		if (mode.equals(CafeUI.MODE_BRING)){
			confirmButton.setText("Bring Selected Products");
		}
		confirmButton.setFont(master.UI_FONT_BIG);
		confirmButton.addActionListener(this);
		layout.setConstraints(confirmButton, constraints);
		add(confirmButton);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(master.UI_FONT_BIG);
		cancelButton.addActionListener(this);
		layout.setConstraints(cancelButton, constraints);
		add(cancelButton);
	}
	/**
	 * Empties the shopping cart.
	 */
	protected void clearCart(){
		cartView.removeAll();
		cartIntConstraints.gridwidth = GridBagConstraints.REMAINDER;
		cartIntLayout.setConstraints(header, cartIntConstraints);
		cartView.add(header);

		cartContents = new ArrayList<IProduct>();
		amounts = new ArrayList<Integer>();
		cartLabels = new ArrayList<JLabel>();
		removebuttons = new ArrayList<JButton>();
		
		cartView.revalidate();

		cartView.repaint();
	}
	/**Loads products from the server and generates buttons that allow the user
	 * to add them to the shopping cart.
	 * 
	 * @param mode		A String indicating whether the method should load
	 * 					buyable or raw products. CafeUI.MODE_BUY indicates
	 * 					buyable, CafeUI.MODE_BRING indicates raw.
	 * @throws IllegalArgumentException		If the passed mode parameter is
	 * 		neither CafeUI.MODE_BUY nor CafeUI.MODE_BRING
	 */
	protected void loadProducts(String mode){
		
		List<IProduct> prodlist;
		if (CafeUI.MODE_BUY.equals(mode)){
			prodlist = master.getBuyableProducts();
		}
		else if (CafeUI.MODE_BRING.equals(mode)){
			prodlist = master.getRawProducts();
		}
		else{
			throw new IllegalArgumentException("Cart page mode is neither buy or bring");
		}

		menuView.removeAll();
		
		products = new IProduct[prodlist.size()];
		prodlist.toArray(products);
				
		prodButtons = new JButton[products.length];
		for (int i = 0; i < products.length; i++){
			prodButtons[i] = initProductButton(products[i].getName() + 
					": " + products[i].getPrice(), menuView);
		}
	}
	/**Initializes a JButton and adds it to a JPanel
	 * 
	 * @param name		The text on the button
	 * @param panel		The panel that the button should be added to
	 * @return			The initialized button
	 */
	private JButton initProductButton(String name, JPanel panel){
		JButton button = new JButton(name);
		button.setFont(master.UI_FONT_SMALL);
		panel.add(button);
		button.addActionListener(this);
		return button;
	}
	/**Adds a product to the user's shopping cart.
	 * 
	 * @param p		An IProduct object specifying the product to be added.
	 */
	private void addToCart(IProduct p){
		int index = cartContents.indexOf(p);
		if (index != -1){
			int amount = amounts.get(index);
			amount++;
			amounts.remove(index);
			amounts.add(index, amount);
			cartLabels.get(index).setText(cartContents.get(index).getName() + " x " + amount);
		}
		else {
			cartContents.add(p);
			amounts.add(1);
			JLabel label = new JLabel(p.getName() + " x " + 1);
			cartIntConstraints.gridwidth = GridBagConstraints.RELATIVE;
			cartIntLayout.setConstraints(label, cartIntConstraints);
			cartLabels.add(label);
			cartView.add(label);
			
			JButton remove = new JButton("-");
			remove.setFont(master.UI_FONT);
			remove.setName("remove_" + p.getName());
			remove.addActionListener(this);
			cartIntConstraints.gridwidth = GridBagConstraints.REMAINDER;
			cartIntLayout.setConstraints(remove, cartIntConstraints);
			removebuttons.add(remove);
			cartView.add(remove);
			
		}
		cartView.revalidate();
	}
	/**Removes a product from the shopping cart.
	 * 
	 * @param index		An integer indicating the position of the product
	 * 					in the shopping cart.
	 */
	private void removeFromCart(int index){
		int amount = amounts.get(index);
		amount--;
		if (amount > 0){
			amounts.remove(index);
			amounts.add(index, amount);
			cartLabels.get(index).setText(cartContents.get(index).getName() + " x " + amount);
		}
		else {
			amounts.remove(index);
			JLabel label = cartLabels.get(index);
			JButton button = removebuttons.get(index);
			cartView.remove(button);
			cartView.remove(label);
			cartContents.remove(index);
			cartLabels.remove(index);
			removebuttons.remove(index);
		}
		cartView.revalidate();
		cartView.repaint();
	}
	/**Sets the recognized users for this page. The first user on the list
	 * is assumed to be logged in.
	 * 
	 * @see	ShortList.setUsers()
	 * 
	 * @param usernames		A String array containing the usernames of the
	 * 						recognized users.
	 */
	protected void setUsers(String[] users){
		userlist.setUsers(users);
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancelButton){
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
			clearCart();
		}
		if (s == clearButton){
			clearCart();
		}
		if (s == confirmButton){
			if (cartContents.size() < 1){
				return;
			}
			//~*~C A S T I N G T Y P E S~*~
			IProduct[] p = new IProduct[cartContents.size()];
			Integer[] a1 = new Integer[amounts.size()];
			cartContents.toArray(p);
			amounts.toArray(a1);
			int[] a2 = new int[a1.length];
			for(int i = 0; i < a1.length; i++){
				a2[i] = a1[i];
			}
			//~*~C A S T I N G T Y P E S~*~
			
			master.selectProduct(p, a2);
			master.setPurchaseMode(mode);
			master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
			clearCart();
		}
		for (int i = 0; i < prodButtons.length; i++){
			if (s == prodButtons[i]){
				addToCart(products[i]);
			}
		}
		int index = removebuttons.indexOf(s);
		if (index != -1){
			removeFromCart(index);
		}
	}

	public void closeView() {
		clearCart();
		
	}

}
