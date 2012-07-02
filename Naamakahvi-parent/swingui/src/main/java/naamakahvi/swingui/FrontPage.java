package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/*
 * Käyttöliittymän aloitussivu
 */
public class FrontPage extends JPanel implements ActionListener{
	private JButton register, manlogin;
	private JButton[] products;
	private JLabel quantity;
	private JPanel prodView;
	private JSpinner spinner;
	private CafeUI master;
	
	public FrontPage(CafeUI master){
		this.master = master;
		
		quantity = new JLabel("Ostettavan tuotteen määrä:");
		quantity.setFont(CafeUI.UI_FONT_BIG);
				
		spinner = new JSpinner(new SpinnerNumberModel(1, -99, 99, 1));
		spinner.setFont(CafeUI.UI_FONT_BIG);
		
		add(quantity);
		add(spinner);
		
		prodView = new JPanel();
		prodView.setPreferredSize(new Dimension(CafeUI.X_RES, CafeUI.Y_RES/8*5));
		add(prodView);
		
		products = new JButton[3];
		products[0] = new JButton("Kahvi");
		products[1] = new JButton("Espresso");
		products[2] = new JButton("Tuplaespresso");
		for (int i = 0; i < products.length; i++){
			products[i].setFont(CafeUI.UI_FONT);
			products[i].addActionListener(this);
			products[i].setPreferredSize(new Dimension(CafeUI.X_RES/3 - 10,
					CafeUI.Y_RES/8*5/(products.length/4+1)-15));
			prodView.add(products[i]);
		}
		
		manlogin = new JButton("Manuaalinen kirjautuminen");
		manlogin.setFont(CafeUI.UI_FONT);
		manlogin.addActionListener(this);
		manlogin.setPreferredSize(new Dimension(CafeUI.X_RES - 20, CafeUI.Y_RES/10));
		
		register = new JButton("Rekisteröidy");
		register.setFont(CafeUI.UI_FONT);
		register.addActionListener(this);
		register.setPreferredSize(new Dimension(CafeUI.X_RES - 20, CafeUI.Y_RES/10));
		
		add(manlogin);
		add(register);
	}
	
	protected void resetPage(){
		spinner.setValue(1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == manlogin){
			master.switchPage(CafeUI.VIEW_MAN_LOGIN_PAGE);
		}
		if (s == register){
			master.switchPage(CafeUI.VIEW_REGISTRATION_PAGE);
		}
		for (int i = 0; i < products.length; i++){
			if (s == products[i]){
				master.selectProduct(products[i].getText(), 
						((Number)spinner.getValue()).intValue());
				master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
			}
		}
	}
}