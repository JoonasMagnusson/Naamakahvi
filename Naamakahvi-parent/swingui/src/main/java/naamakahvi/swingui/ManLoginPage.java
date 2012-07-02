package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ManLoginPage extends JPanel implements ActionListener{
	private JTextField username;
	private JButton ok, cancel;
	private JLabel header, help;
	private CafeUI master;
	private static final String defaultHelp = "Syötä käyttäjätunnus:";
	
	public ManLoginPage(CafeUI master){
		this.master = master;
		
		header = new JLabel("Manuaalinen kirjautuminen", SwingConstants.CENTER);
		header.setFont(CafeUI.UI_FONT_BIG);
		header.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		help = new JLabel(defaultHelp, SwingConstants.CENTER);
		help.setFont(CafeUI.UI_FONT_SMALL);
		help.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		username = new JTextField();
		username.setFont(CafeUI.UI_FONT_BIG);
		username.setPreferredSize(new Dimension(CafeUI.X_RES-20, 40));
		
		ok = new JButton("Kirjaudu sisään");
		ok.setFont(CafeUI.UI_FONT_BIG);
		ok.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		ok.addActionListener(this);
		
		cancel = new JButton("Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		cancel.addActionListener(this);
		
		add(header);
		add(help);
		add(username);
		add(ok);
		add(cancel);
	}
	
	protected void setHelpText(String text){
		help.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancel){
			setHelpText(defaultHelp);
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == ok){
			if (master.LoginUser(username.getText())){
				setHelpText(defaultHelp);
				master.switchPage(CafeUI.VIEW_MENU_PAGE);
			}
		}
	}
}
