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
	
	private GridBagLayout cartLayout;
	private GridBagConstraints cartConstraints;
	
	private String mode;
	private ShortList userlist;
	
	public PurchaseCartPage(CafeUI master, String mode){
		this.master = master;
		this.mode = mode;
		FlowLayout layout = new FlowLayout();
		//layout.setHgap(0);
		//layout.setVgap(0);
		setLayout(layout);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(master.UI_FONT_BIG);
		cancelButton.addActionListener(this);
		cancelButton.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		
		confirmButton = new JButton("Buy selected products");
		confirmButton.setFont(master.UI_FONT_BIG);
		confirmButton.addActionListener(this);
		confirmButton.setPreferredSize(new Dimension(master.X_RES/2 -layout.getVgap(),
				master.Y_RES/8 - layout.getVgap()));
		
		
		userlist = new ShortList(master, this);
		userlist.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
				master.Y_RES/8*7 - layout.getVgap()));
		add(userlist);
		
		//Ostoskori
		cartPanel = new JPanel();
		FlowLayout cartflow = new FlowLayout();
		cartflow.setHgap(0);
		cartflow.setVgap(0);
		cartPanel.setLayout(cartflow);
		cartPanel.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
				master.Y_RES/8*7 - layout.getVgap()));
		
		
		cartView = new JPanel();
		cartLayout = new GridBagLayout();
		cartView.setLayout(cartLayout);
		cartConstraints = new GridBagConstraints();
		cartConstraints.weightx = 1.0;
		cartConstraints.fill = GridBagConstraints.BOTH;
		
		cartScroll = new JScrollPane(cartView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cartScroll.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
				master.Y_RES/8*6 - layout.getVgap()));
		cartPanel.add(cartScroll);
		
		clearButton = new JButton("Clear cart");
		clearButton.setFont(master.UI_FONT);
		clearButton.addActionListener(this);
		clearButton.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(), 
				master.Y_RES/8 - layout.getVgap()));
		cartPanel.add(clearButton);
		
		header = new JLabel("Buying:");
		header.setFont(master.UI_FONT);
		
		clearCart();
		add(cartPanel);
		
		//Tuotelista
		menuView = new JPanel();
		menuView.setPreferredSize(new Dimension(master.X_RES/2  - layout.getHgap(),
				master.Y_RES/8*7 - layout.getVgap()));
		menuView.setLayout(new GridLayout(0, 1));
		menuScroll = new JScrollPane(menuView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		menuScroll.setPreferredSize(new Dimension(master.X_RES/2  - layout.getHgap(),
				master.Y_RES/8*7 - layout.getVgap()));
		add(menuScroll);
		
		
		loadProducts(mode);
		
		add(confirmButton);
		add(cancelButton);
	}
	
	protected void clearCart(){
		cartView.removeAll();
		cartConstraints.gridwidth = GridBagConstraints.REMAINDER;
		cartLayout.setConstraints(header, cartConstraints);
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
			cartConstraints.gridwidth = GridBagConstraints.RELATIVE;
			cartLayout.setConstraints(label, cartConstraints);
			cartLabels.add(label);
			cartView.add(label);
			
			JButton remove = new JButton("-");
			remove.setFont(master.UI_FONT);
			remove.addActionListener(this);
			cartConstraints.gridwidth = GridBagConstraints.REMAINDER;
			cartLayout.setConstraints(remove, cartConstraints);
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
