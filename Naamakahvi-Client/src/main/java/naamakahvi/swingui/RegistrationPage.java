package naamakahvi.swingui;

import javax.swing.*;
import java.awt.*;

public class RegistrationPage extends JPanel{
	private JTextField username;
	private IDButton ok, cancel;
	private JLabel header, help;
	
	public RegistrationPage(CafeUI master){
		header = new JLabel("Rekisteröityminen", SwingConstants.CENTER);
		header.setFont(CafeUI.UI_FONT_BIG);
		header.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		help = new JLabel("Syötä käyttäjätunnus:", SwingConstants.CENTER);
		help.setFont(CafeUI.UI_FONT);
		help.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		username = new JTextField();
		username.setFont(CafeUI.UI_FONT_BIG);
		username.setPreferredSize(new Dimension(CafeUI.X_RES-20, 40));
		
		ok = new IDButton(CafeUI.BUTTON_REGISTER_USER, "Rekisteröidy");
		ok.setFont(CafeUI.UI_FONT_BIG);
		ok.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		ok.addActionListener(master);
		
		cancel = new IDButton(CafeUI.BUTTON_CANCEL, "Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		cancel.addActionListener(master);
		
		add(header);
		add(help);
		add(username);
		add(ok);
		add(cancel);
	}
	
	public String getUsername(){
		return username.getText();
	}
	
	public void setHelpText(String text){
		help.setText(text);
	}
}
