package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/*
 * Kälin erikoistoimintosivu
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
	
	public PurchaseCartPage(CafeUI master, String mode){
		this.master = master;
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
		
		userlist = new ShortList(master, this);
		//userlist.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
		//		master.Y_RES/8*7 - layout.getVgap()));
		layout.setConstraints(userlist, constraints);
		add(userlist);
		
		//Ostoskori
		
		cartPanel = new JPanel();
		GridBagLayout cartExtLayout = new GridBagLayout();
		cartPanel.setLayout(cartExtLayout);
		//cartPanel.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
		//		master.Y_RES/8*7 - layout.getVgap()));
		
		
		cartView = new JPanel();
		cartIntLayout = new GridBagLayout();
		cartView.setLayout(cartIntLayout);
		cartIntConstraints = new GridBagConstraints();
		cartIntConstraints.weightx = 1.0;
		cartIntConstraints.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints cartExtConstraints = new GridBagConstraints();
		cartExtConstraints.fill = GridBagConstraints.BOTH;
		cartExtConstraints.weightx = 1.0;
		cartExtConstraints.weighty = 1.0;
		
		cartExtConstraints.gridheight = GridBagConstraints.RELATIVE;
		cartExtConstraints.gridwidth = GridBagConstraints.REMAINDER;
		cartScroll = new JScrollPane(cartView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//cartScroll.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
		//		master.Y_RES/8*6 - layout.getVgap()));
		cartExtLayout.setConstraints(cartScroll, cartExtConstraints);
		cartPanel.add(cartScroll);
		
		cartExtConstraints.gridheight = GridBagConstraints.REMAINDER;
		cartExtConstraints.weighty = 0.2;
		
		clearButton = new JButton("Clear Cart");
		clearButton.setFont(master.UI_FONT);
		clearButton.addActionListener(this);
		//clearButton.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(), 
		//		master.Y_RES/8 - layout.getVgap()));
		cartExtLayout.setConstraints(clearButton, cartExtConstraints);
		cartPanel.add(clearButton);
		
		header = new JLabel("Buying:");
		header.setFont(master.UI_FONT);
		
		clearCart();
		layout.setConstraints(cartPanel, constraints);
		add(cartPanel);
		
		//Tuotelista
		constraints.weightx = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		menuView = new JPanel();
		//menuView.setPreferredSize(new Dimension(master.X_RES/2  - layout.getHgap(),
		//		master.Y_RES/8*7 - layout.getVgap()));
		menuView.setLayout(new GridLayout(0, 1));
		menuScroll = new JScrollPane(menuView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//menuScroll.setPreferredSize(new Dimension(master.X_RES/2  - layout.getHgap(),
		//		master.Y_RES/8*7 - layout.getVgap()));
		layout.setConstraints(menuScroll, constraints);
		add(menuScroll);
		
		
		loadProducts(mode);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		constraints.weightx = 0.0;
		constraints.weighty = 0.2;
		
		confirmButton = new JButton();
		if (mode.equals(CafeUI.MODE_BUY)){
			confirmButton.setText("Buy Selected Products");
		}
		if (mode.equals(CafeUI.MODE_BRING)){
			confirmButton.setText("Bring Selected Products");
		}
		confirmButton.setFont(master.UI_FONT_BIG);
		confirmButton.addActionListener(this);
		//confirmButton.setPreferredSize(new Dimension(master.X_RES/2 -layout.getVgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		layout.setConstraints(confirmButton, constraints);
		add(confirmButton);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(master.UI_FONT_BIG);
		cancelButton.addActionListener(this);
		//cancelButton.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		layout.setConstraints(cancelButton, constraints);
		add(cancelButton);
	}
	
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
	}
	
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
	
	private JButton initProductButton(String name, JPanel panel){
		JButton button = new JButton(name);
		button.setFont(master.UI_FONT);
		panel.add(button);
		button.addActionListener(this);
		return button;
	}
	
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
	}
	
	protected void setUsers(String[] users){
		userlist.setUsers(users);
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancelButton){
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
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
		// Nothing to close
		
	}

}
