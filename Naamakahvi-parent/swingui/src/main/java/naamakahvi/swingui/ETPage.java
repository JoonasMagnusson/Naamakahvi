package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/*
 * Kälin erikoistoimintosivu
 */
public class ETPage extends JPanel implements ActionListener{
	private JButton cancel, buy;
	private JButton[] buyable, bringable;
	private JPanel cart, menu;
	private CafeUI master;
	
	public ETPage(CafeUI master){
		this.master = master;
		
		cancel = new JButton("Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.addActionListener(this);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		buy = new JButton("Osta");
		buy.setFont(CafeUI.UI_FONT_BIG);
		buy.addActionListener(this);
		buy.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		
		cart = new JPanel();
		cart.setPreferredSize(new Dimension(CafeUI.X_RES/3-10, CafeUI.Y_RES/8*6));

		menu = new JPanel();
		menu.setPreferredSize(new Dimension(CafeUI.X_RES/3*2-10, CafeUI.Y_RES/8*6));
		
		add(cart);
		add(menu);
		add(buy);
		add(cancel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancel){
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
	}

}
