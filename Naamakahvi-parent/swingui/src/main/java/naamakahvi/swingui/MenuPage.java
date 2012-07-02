package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/*
 * Käyttöliittymän päävalikkonäkymä, josta valitaan mitä tuotetta ostetaan
 */
public class MenuPage extends JPanel implements ActionListener{
	private JLabel username, coffeeSaldo, espressoSaldo;
	private JButton etButton, coffeeButton, espressoButton, logout;
	private CafeUI master;
	
	public MenuPage(CafeUI master){
		this.master = master;
		
		username = new JLabel("Tunnistettu käyttäjä: Protokäyttäjä", SwingConstants.CENTER);
		username.setFont(CafeUI.UI_FONT);
		username.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/8));
		
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
		
		etButton = new JButton("E.T.");
		etButton.setFont(CafeUI.UI_FONT_BIG);
		etButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		etButton.addActionListener(this);
		
		logout = new JButton("Kirjaudu ulos");
		logout.setFont(CafeUI.UI_FONT_BIG);
		logout.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		logout.addActionListener(this);
		
		add(username);
		add(coffeeSaldo);
		add(espressoSaldo);
		add(coffeeButton);
		add(espressoButton);
		add(etButton);
		add(logout);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän nimen
	 */
	public void setUser(String user){
		username.setText("Kirjautunut käyttäjä: " + user);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän suodatinkahvisaldon
	 */
	public void setCoffeeSaldo(int saldo){
		coffeeSaldo.setText("Saldo: " + saldo);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän espressosaldon
	 */
	public void setEspressoSaldo(int saldo){
		espressoSaldo.setText("Saldo: " + saldo);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == logout){
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == coffeeButton){
			master.selectProduct("kahvi", 1);
			master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
		}
		if (s == espressoButton){
			master.selectProduct("espresso",1);
			master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
		}
		if (s == etButton){
			master.switchPage(CafeUI.VIEW_PROD_LIST_PAGE);
		}
	}
}
