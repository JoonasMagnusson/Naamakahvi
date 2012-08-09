package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

/*
 * Käyttöliittymän päävalikkonäkymä, josta valitaan mitä tuotetta ostetaan
 */
public class MenuPage extends JPanel implements ActionListener, CloseableView{
	//private JLabel username;
	private JButton buycart, bringcart, logout;
	private JButton[][] buyprodButtons, bringprodButtons;
	private IProduct[] buyproducts, bringproducts;
	private JPanel buyprodPanel, bringprodPanel;
	private CafeUI master;
	private JScrollPane buyscroll, bringscroll;
	private ShortList userlist;
	
	public MenuPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		
		constraints.gridheight = 8;
		constraints.gridwidth = 1;
		
		userlist = new ShortList(master, this);
		layout.setConstraints(userlist, constraints);
		add(userlist);
		//userlist.setPreferredSize(new Dimension(master.X_RES/3 - layout.getHgap(),
		//		master.Y_RES/8*7 - layout.getVgap()));
		/*
		prodPanel = new JPanel();
		prodPanel.setPreferredSize(new Dimension(master.X_RES/3*2 - layout.getHgap(),
				master.Y_RES/8*7 - layout.getVgap()));*/
		
		buyprodPanel = new JPanel();
		//buyprodPanel.setPreferredSize(new Dimension(master.X_RES/2-20, master.Y_RES/2-20));
		
		constraints.gridheight = 4;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		buyscroll = new JScrollPane(buyprodPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setConstraints(buyscroll, constraints);
		add(buyscroll);
		//buyscroll.setPreferredSize(new Dimension(master.X_RES/3*2 - layout.getHgap(),
		//		master.Y_RES/16*7 - layout.getVgap()));
		
		bringprodPanel = new JPanel();
		//bringprodPanel.setPreferredSize(new Dimension(master.X_RES/2-20, master.Y_RES/2-20));
		
		bringscroll = new JScrollPane(bringprodPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setConstraints(bringscroll, constraints);
		add(bringscroll);
		//bringscroll.setPreferredSize(new Dimension(master.X_RES/3*2 - layout.getHgap(),
		//		master.Y_RES/16*7 - layout.getVgap()));
		
		/*
		buycart = new JButton("Buy multiple products");
		buycart.setFont(master.UI_FONT_BIG);
		buycart.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/8));
		buycart.addActionListener(this);
		
		bringcart = new JButton("Bring multiple products");
		bringcart.setFont(master.UI_FONT_BIG);
		bringcart.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/8));
		bringcart.addActionListener(this);
		*/
		constraints.gridheight = 1;
		
		logout = new JButton("Log Out");
		logout.setFont(master.UI_FONT_BIG);
		//logout.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		logout.addActionListener(this);
		layout.setConstraints(logout, constraints);
		add(logout);
		
		setProducts();
		
		/*/add(username);
		add(userlist);
		//add(prodPanel);
		prodPanel.add(buyscroll);
		prodPanel.add(bringscroll);
		//add(buycart);
		//add(bringcart);
		setProducts();
		
		add(logout);*/
	}
	
	/*
	 * Näyttää kirjautuneen käyttäjän nimen
	 */
	public void setUser(String[] usernames){
		userlist.setUsers(usernames);
	}
	
	private void setProducts(){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		List<IProduct> tempbuy = master.getBuyableProducts();
		List<IProduct> tempbring = master.getRawProducts();
		
		if (tempbuy == null) return;
		
		GridBagLayout buyLayout = new GridBagLayout();
		GridBagLayout bringLayout = new GridBagLayout();
		
		buyprodPanel.removeAll();
		bringprodPanel.removeAll();
		
		buyprodPanel.setLayout(buyLayout);
		bringprodPanel.setLayout(bringLayout);
		
		JLabel buyheader = new JLabel("Buy Products:");
		buyheader.setFont(master.UI_FONT);
		buyLayout.setConstraints(buyheader, c);
		buyprodPanel.add(buyheader);
		
		JLabel bringheader = new JLabel("Bring Products:");
		bringheader.setFont(master.UI_FONT);
		bringLayout.setConstraints(bringheader, c);
		bringprodPanel.add(bringheader);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		buycart = new JButton("Buy Multiple Products");
		buycart.setFont(master.UI_FONT_SMALL);
		buycart.addActionListener(this);
		buyLayout.setConstraints(buycart, c);
		buyprodPanel.add(buycart);
		
		bringcart = new JButton("Bring Multiple Products");
		bringcart.setFont(master.UI_FONT_SMALL);
		bringcart.addActionListener(this);
		bringLayout.setConstraints(bringcart, c);
		bringprodPanel.add(bringcart);
		
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
		
		for (int i = 0; i <bringproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(bringproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			bringLayout.setConstraints(prodname, c);
			bringprodPanel.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			bringLayout.setConstraints(prodline, c);
			bringprodPanel.add(prodline);
			
			
			for (int j = 0; j < 5; j++){
				bringprodButtons[i][j] = new JButton("" + (j+1));
				bringprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				bringprodButtons[i][j].addActionListener(this);
				bringprodButtons[i][j].setName("bring"+i+":"+j);
				prodline.add(bringprodButtons[i][j]);
			}
		}
		bringprodPanel.revalidate();
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
		for (int i = 0; i < bringprodButtons.length; i++){
			for (int j = 0; j < bringprodButtons[i].length; j++){
				if (s == bringprodButtons[i][j]){
					IProduct[] prods = new IProduct[1];
					prods[0] = bringproducts[i];

					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BRING);
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
	}

	public void closeView() {
		//Nothing to close
	}
}
