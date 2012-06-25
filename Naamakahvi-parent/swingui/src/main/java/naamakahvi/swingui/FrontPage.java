package naamakahvi.swingui;
import java.awt.*;

import javax.swing.*;

/*
 * Käyttöliittymän aloitussivu
 */
public class FrontPage extends JPanel{
	private IDButton takePic, register;
	private FlowLayout layout;
	
	public FrontPage(CafeUI master){
		layout = new FlowLayout();
		setLayout(layout);
		
		takePic = new IDButton(CafeUI.BUTTON_START, "Ota Kuva");
		takePic.setFont(CafeUI.UI_FONT_BIG);
		takePic.addActionListener(master);
		takePic.setPreferredSize(new Dimension(CafeUI.X_RES - 20, CafeUI.Y_RES - 100));
		
		register = new IDButton(CafeUI.BUTTON_REGISTER_VIEW, "Rekisteröidy");
		register.setFont(CafeUI.UI_FONT);
		register.addActionListener(master);
		register.setPreferredSize(new Dimension(CafeUI.X_RES - 20, 60));
		
		add(takePic);
		add(register);
	}
}