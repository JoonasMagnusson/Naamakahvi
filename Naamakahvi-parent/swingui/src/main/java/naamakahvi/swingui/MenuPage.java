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
public class MenuPage extends JPanel implements ActionListener{
	private JLabel username, coffeeSaldo, espressoSaldo, amountText;
	private JButton cart, coffeeButton, espressoButton, logout;
	private JButton[][] prodButtons;
	private IProduct[] products;
	private JPanel prodPanel;
	private JSpinner spinner;
	private CafeUI master;
	
	public MenuPage(CafeUI master){
		this.master = master;
		
		username = new JLabel("Placeholder", SwingConstants.CENTER);
		username.setFont(master.UI_FONT_BIG);
		username.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/8));
		/*
		coffeeSaldo = new JLabel("Saldo: 0", SwingConstants.CENTER);
		coffeeSaldo.setFont(CafeUI.UI_FONT);
		coffeeSaldo.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		espressoSaldo = new JLabel("Saldo: 0", SwingConstants.CENTER);
		espressoSaldo.setFont(CafeUI.UI_FONT);
		espressoSaldo.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		coffeeButton = new JButton("Osta Kahvia");
		coffeeButton.setFont(CafeUI.UI_FONT_BIG);
		coffeeButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		coffeeButton.addActionListener(this);
		
		espressoButton = new JButton("Osta Espressoa");
		espressoButton.setFont(CafeUI.UI_FONT_BIG);
		espressoButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		espressoButton.addActionListener(this);
		*/
		amountText = new JLabel("Quantity");
		amountText.setFont(master.UI_FONT);
		
		spinner = new JSpinner(new SpinnerNumberModel(1, -99, 99, 1));
		spinner.setFont(master.UI_FONT);
		
		prodPanel = new JPanel();
		prodPanel.setLayout(new GridLayout(0,1));
		prodPanel.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/2-20));
		
		/*
		prodButtons = new JButton[3];
		prodButtons[0] = new JButton("Coffee");
		prodButtons[1] = new JButton("Espresso");
		prodButtons[2] = new JButton("Double Espresso");
		for (int i = 0; i < prodButtons.length; i++){
			prodButtons[i].setFont(CafeUI.UI_FONT);
			prodButtons[i].addActionListener(this);
			prodPanel.add(prodButtons[i]);
		}*/
		
		cart = new JButton("Shopping Cart");
		cart.setFont(master.UI_FONT_BIG);
		cart.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/4));
		cart.addActionListener(this);
		
		logout = new JButton("Log Out");
		logout.setFont(master.UI_FONT_BIG);
		logout.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/4));
		logout.addActionListener(this);
		
		add(username);
		add(amountText);
		add(spinner);
		add(prodPanel);
		/*
		add(coffeeSaldo);
		add(espressoSaldo);
		add(coffeeButton);
		add(espressoButton);*/
		getProducts();
		
		add(logout);
	}
	
	protected void reset(){
		spinner.setValue(1);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän nimen
	 */
	public void setUser(String user){
		username.setText("Recognized user: " + user);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän suodatinkahvisaldon
	 */
	public void setCoffeeSaldo(int saldo){
		//coffeeSaldo.setText("Saldo: " + saldo);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän espressosaldon
	 */
	public void setEspressoSaldo(int saldo){
		//espressoSaldo.setText("Saldo: " + saldo);
	}
	
	
	
	private void getProducts(){
		
		List<IProduct> temp = master.getDefaultProducts();
		
		if (temp == null || temp.size() < 1){
			throw new IllegalArgumentException("No default products received from server");
		}
		
		prodPanel.removeAll();
		
		JLabel header = new JLabel("Quick Buy");
		header.setFont(master.UI_FONT);
		
		
		products = new IProduct[temp.size()];
		prodButtons = new JButton[temp.size()][5];
		temp.toArray(products);
		prodPanel.removeAll();
		for (int i = 0; i < products.length; i++){
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			prodPanel.add(prodline);
			
			JLabel prodname = new JLabel(products[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			prodline.add(prodname);
			
			for (int j = 0; j < 5; j++){
				prodButtons[i][j] = new JButton("" + j);
				prodButtons[i][j].setFont(master.UI_FONT);
				prodButtons[i][j].addActionListener(this);
				prodline.add(prodButtons[i][j]);
			}
		}
		cart = new JButton("Shopping Cart View");
		cart.setFont(master.UI_FONT);
		cart.addActionListener(this);
		prodPanel.add(cart);
		
		prodPanel.revalidate();
	
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == logout){
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		for (int i = 0; i < prodButtons.length; i++){
			for (int j = 0; j < prodButtons[i].length; j++){
				if (s == prodButtons[i]){
					master.selectProduct(products[i].getName(), 
							((Number)spinner.getValue()).intValue());
					master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
				}
			}
		}
		if (s == cart){
			master.switchPage(CafeUI.VIEW_PROD_LIST_PAGE);
		}
	}
}
