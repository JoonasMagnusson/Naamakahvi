package naamakahvi.swingui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationPage extends JPanel implements ActionListener{
	private JTextField userName, firstName, lastName;
	private JButton takePic, noPic, cancel;
	private JLabel header, help, unLabel, fnLabel, lnLabel;
	private CafeUI master;
	private static final String defaultHelp = "Syötä käyttäjätiedot";
	
	public RegistrationPage(CafeUI master){
		this.master = master;
		header = new JLabel("Rekisteröityminen", SwingConstants.CENTER);
		header.setFont(CafeUI.UI_FONT_BIG);
		header.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/8));
		
		help = new JLabel(defaultHelp, SwingConstants.CENTER);
		help.setFont(CafeUI.UI_FONT_SMALL);
		help.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/8));
		
		unLabel = new JLabel("Käyttäjätunnus:", SwingConstants.CENTER);
		unLabel.setFont(CafeUI.UI_FONT);
		unLabel.setPreferredSize(new Dimension(CafeUI.X_RES/3-10, CafeUI.Y_RES/8));
		
		userName = new JTextField();
		userName.setFont(CafeUI.UI_FONT_BIG);
		userName.setPreferredSize(new Dimension(CafeUI.X_RES/3*2-10, CafeUI.Y_RES/8));
		
		fnLabel = new JLabel("Etunimi:", SwingConstants.CENTER);
		fnLabel.setFont(CafeUI.UI_FONT);
		fnLabel.setPreferredSize(new Dimension(CafeUI.X_RES/3-10, CafeUI.Y_RES/8));
		
		firstName = new JTextField();
		firstName.setFont(CafeUI.UI_FONT_BIG);
		firstName.setPreferredSize(new Dimension(CafeUI.X_RES/3*2-10, CafeUI.Y_RES/8));
		
		lnLabel = new JLabel("Sukunimi:", SwingConstants.CENTER);
		lnLabel.setFont(CafeUI.UI_FONT);
		lnLabel.setPreferredSize(new Dimension(CafeUI.X_RES/3-10, CafeUI.Y_RES/8));
		
		lastName = new JTextField();
		lastName.setFont(CafeUI.UI_FONT_BIG);
		lastName.setPreferredSize(new Dimension(CafeUI.X_RES/3*2-10, CafeUI.Y_RES/8));
		
		takePic = new JButton("Rekisteröidy");
		takePic.setFont(CafeUI.UI_FONT);
		takePic.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		takePic.addActionListener(this);
		
		noPic = new JButton("Rekisteröidy ilman kuvaa");
		noPic.setFont(CafeUI.UI_FONT);
		noPic.setPreferredSize(new Dimension(CafeUI.X_RES/2-10, CafeUI.Y_RES/8));
		noPic.addActionListener(this);
		
		cancel = new JButton("Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/8));
		cancel.addActionListener(this);
		
		add(header);
		add(help);
		add(unLabel);
		add(userName);
		add(fnLabel);
		add(firstName);
		add(lnLabel);
		add(lastName);
		add(takePic);
		add(noPic);
		add(cancel);
	}
	
	protected void setHelpText(String text){
		help.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == takePic || s == noPic){
			if (master.RegisterUser(userName.getText(),
					firstName.getText(), lastName.getText())){
				setHelpText(defaultHelp);
				master.switchPage(CafeUI.VIEW_MENU_PAGE);
			}
		}
		if (s == cancel){
			setHelpText(defaultHelp);
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		
	}
}
