package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

/**A class implementing the menu page view for the Facecafe swingui component.
 * The menu page displays the logged in user and allows them to select products
 * to purchase or to display a shopping cart view.
 * 
 * @author Antti Hietasaari
 *
 */
public class MenuPage extends JPanel implements ActionListener, CloseableView{
	
	private JButton buycart, bringcart, logout, add;
	private JButton[][] buyprodButtons;
	private IProduct[] buyproducts;
	private JPanel buyprodPanel;
	private CafeUI master;
	private JScrollPane buyscroll;
	private ShortList userlist;
	/**Creates a new menu page view.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The menu page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public MenuPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 0.0;
		setLayout(layout);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		JLabel header = new JLabel("User Menu", SwingConstants.CENTER);
		header.setFont(master.UI_FONT_BIG);
		layout.setConstraints(header, constraints);
		add(header);
		
		constraints.weighty = 1.0;
		constraints.weightx = 0.2;
		constraints.gridheight = 8;
		constraints.gridwidth = 1;
		
		userlist = new ShortList(master, this);
		layout.setConstraints(userlist, constraints);
		add(userlist);
		
		buyprodPanel = new JPanel();
		
		constraints.gridheight = 8;
		constraints.weightx = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		buyscroll = new JScrollPane(buyprodPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setConstraints(buyscroll, constraints);
		add(buyscroll);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 0.3;
		constraints.weightx = 0.2;
		
		add = new JButton("Add Images to Account");
		add.setFont(master.UI_FONT);
		add.addActionListener(this);
		layout.setConstraints(add, constraints);
		add(add);
		
		bringcart = new JButton("Bring Products");
		bringcart.setFont(master.UI_FONT);
		bringcart.addActionListener(this);
		layout.setConstraints(bringcart, constraints);
		add(bringcart);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 1.0;
		
		logout = new JButton("Log Out");
		logout.setFont(master.UI_FONT);
		logout.addActionListener(this);
		layout.setConstraints(logout, constraints);
		add(logout);
		
		loadProducts();
	}
	
	/**Sets the recognized users for this page. The first user on the list
	 * is assumed to be logged in.
	 * 
	 * @see	ShortList.setUsers()
	 * 
	 * @param usernames		A String array containing the usernames of the
	 * 						recognized users.
	 */
	public void setUser(String[] usernames){
		userlist.setUsers(usernames);
	}
	
	/**
	 * Loads a list of products from the server and generates buttons that
	 * allow the user to select a product to purchase.
	 */
	private void loadProducts(){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		List<IProduct> tempbuy = master.getBuyableProducts();
		
		GridBagLayout buyLayout = new GridBagLayout();
		
		buyprodPanel.removeAll();
		
		buyprodPanel.setLayout(buyLayout);
		
		JLabel buyheader = new JLabel("Buy Products:");
		buyheader.setFont(master.UI_FONT);
		buyLayout.setConstraints(buyheader, c);
		buyprodPanel.add(buyheader);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		buycart = new JButton("Buy Multiple Products");
		buycart.setFont(master.UI_FONT_SMALL);
		buycart.addActionListener(this);
		buyLayout.setConstraints(buycart, c);
		buyprodPanel.add(buycart);
		
		buyproducts = new IProduct[tempbuy.size()];
		buyprodButtons = new JButton[tempbuy.size()][5];
		tempbuy.toArray(buyproducts);
		
		for (int i = 0; i < buyproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(buyproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			buyLayout.setConstraints(prodname, c);
			buyprodPanel.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			buyLayout.setConstraints(prodline, c);
			buyprodPanel.add(prodline);
			
			
			for (int j = 0; j < 5; j++){
				buyprodButtons[i][j] = new JButton("" + (j+1));
				buyprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				buyprodButtons[i][j].addActionListener(this);
				buyprodButtons[i][j].setName("buy"+i+":"+j);
				prodline.add(buyprodButtons[i][j]);
			}
		}
		
		buyprodPanel.revalidate();
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == logout){
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		for (int i = 0; i < buyprodButtons.length; i++){
			for (int j = 0; j < buyprodButtons[i].length; j++){
				if (s == buyprodButtons[i][j]){
					IProduct[] prods = new IProduct[1];
					prods[0] = buyproducts[i];

					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BUY);
					master.selectProduct(prods, amounts);
					master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
				}
			}
		}
		if (s == buycart){
			master.switchPage(CafeUI.VIEW_BUY_LIST_PAGE);
		}
		if (s == bringcart){
			master.switchPage(CafeUI.VIEW_BRING_LIST_PAGE);
		}
		if (s == add){
			master.switchPage(CafeUI.VIEW_ADD_PICTURE_PAGE);
		}
	}

	public void closeView() {
		//Nothing to close
	}
}
