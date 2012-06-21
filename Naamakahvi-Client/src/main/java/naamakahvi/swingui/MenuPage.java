package naamakahvi.swingui;
import java.awt.*;

import javax.swing.*;

/*
 * Käyttöliittymän päävalikkonäkymä, josta valitaan mitä tuotetta ostetaan
 */
public class MenuPage extends JPanel{
	private FlowLayout layout;
	private JLabel username, coffeeSaldo, espressoSaldo;
	private IDButton etButton, coffeeButton, espressoButton, cancel;
	
	public MenuPage(CafeUI master){
		layout = new FlowLayout();
		setLayout(layout);
		
		username = new JLabel("Tunnistettu käyttäjä: Protokäyttäjä", SwingConstants.CENTER);
		username.setFont(CafeUI.UI_FONT);
		username.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/8));
		
		coffeeSaldo = new JLabel("Saldo: 0", SwingConstants.CENTER);
		coffeeSaldo.setFont(CafeUI.UI_FONT);
		coffeeSaldo.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		espressoSaldo = new JLabel("Saldo: 0", SwingConstants.CENTER);
		espressoSaldo.setFont(CafeUI.UI_FONT);
		espressoSaldo.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		coffeeButton = new IDButton(CafeUI.BUTTON_BUY_COFFEE, "Osta Kahvia");
		coffeeButton.setFont(CafeUI.UI_FONT_BIG);
		coffeeButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		coffeeButton.addActionListener(master);
		
		espressoButton = new IDButton(CafeUI.BUTTON_BUY_ESPRESSO, "Osta Espressoa");
		espressoButton.setFont(CafeUI.UI_FONT_BIG);
		espressoButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		espressoButton.addActionListener(master);
		
		etButton = new IDButton(CafeUI.BUTTON_ET, "E.T.");
		etButton.setFont(CafeUI.UI_FONT_BIG);
		etButton.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		etButton.addActionListener(master);
		
		cancel = new IDButton(CafeUI.BUTTON_CANCEL, "Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/4));
		cancel.addActionListener(master);
		
		add(username);
		add(coffeeSaldo);
		add(espressoSaldo);
		add(coffeeButton);
		add(espressoButton);
		add(etButton);
		add(cancel);
	}
	/*
	 * Näyttää kirjautuneen käyttäjän nimen
	 */
	public void setUser(String user){
		username.setText(user);
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
}
