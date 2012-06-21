package naamakahvi.swingui;
import java.awt.*;

import javax.swing.*;

/*
 * Näkymä, jossa käyttäjä vahvistaa olevansa kuvasta tunnistettu henkilö
 */
public class ConfirmationPage extends JPanel{
	private FlowLayout layout;
	private JLabel header, username;
	private IDButton confirm;
	private IDButton cancel;
	
	public ConfirmationPage(CafeUI master){
		layout = new FlowLayout();
		setLayout(layout);
		
		header = new JLabel("Tunnistettu käyttäjä:", SwingConstants.CENTER);
		header.setFont(CafeUI.UI_FONT_BIG);
		header.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		username = new JLabel("Placeholder", SwingConstants.CENTER);
		username.setFont(CafeUI.UI_FONT_BIG);
		username.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		confirm = new IDButton(CafeUI.BUTTON_CONFIRM_USER, "OK");
		confirm.setFont(CafeUI.UI_FONT_BIG);
		confirm.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/4));
		confirm.addActionListener(master);
		
		cancel = new IDButton(CafeUI.BUTTON_CANCEL, "Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/4));
		cancel.addActionListener(master);
		
		add(header);
		add(username);
		add(confirm);
		add(cancel);
	}
	/*
	 * Näyttää käyttäjälle tunnistetun henkilön nimen
	 */
	public void setUser(String user){
		username.setText(user);
	}
}
