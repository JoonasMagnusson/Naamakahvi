package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
/**DEPRECATED: replaced with UserListPage.
 * 
 * A class implementing a manual login page for the Facecafe swingui component.
 * Allows the user to enter their username into a text box and manually log into
 * the system.
 * 
 * @author Antti Hietasaari
 *
 */
public class ManLoginPage extends JPanel implements ActionListener{
	private JTextField username;
	private JButton ok, cancel;
	private JLabel header, help;
	private CafeUI master;
	/**
	 * The default help text shown to the user when there are no error messages
	 * to show.
	 */
	public static final String defaultHelp = "Please enter your username:";
	/**Creates a new manual login page.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The manual login page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public ManLoginPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		
		header = new JLabel("Manual Login", SwingConstants.CENTER);
		header.setFont(master.UI_FONT_BIG);
		layout.setConstraints(header, constraints);
		add(header);
		
		help = new JLabel(defaultHelp, SwingConstants.CENTER);
		help.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(help, constraints);
		add(help);
		
		username = new JTextField();
		username.setFont(master.UI_FONT_BIG);
		layout.setConstraints(username, constraints);
		add(username);
		
		ok = new JButton("OK");
		ok.setFont(master.UI_FONT_BIG);
		ok.addActionListener(this);
		layout.setConstraints(ok, constraints);
		add(ok);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		cancel.addActionListener(this);
		layout.setConstraints(cancel, constraints);
		add(cancel);
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String text){
		help.setText(text);
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancel){
			setHelpText(defaultHelp);
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == ok){
			if (master.loginUser(username.getText())){
				setHelpText(defaultHelp);
				master.switchPage(CafeUI.VIEW_MENU_PAGE);
			}
		}
	}
}
