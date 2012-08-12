package naamakahvi.swingui;

import java.awt.event.*;
import java.awt.*;
import java.util.Arrays;

import javax.swing.*;
/**A class implementing a list of all registered usernames for the Facecafe
 * swingui component.
 * The page allows the user to choose their username and log in to the system
 * from a list of all registered usernames.
 * 
 * @author Antti Hietasaari
 *
 */
public class UserListPage extends JPanel implements ActionListener{
	private String[] usernames;
	private JButton[] userButtons;
	private JButton cancel;
	private JPanel userPanel;
	private JScrollPane userScroll;
	private CafeUI master;
	private JLabel helptext;
	/**
	 * The default help text that is visible when the page is initially shown
	 */
	public static String defaulthelp = "Please select your username from the list below:";
	/**Creates a new UserListPage.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The user list page accesses the methods of the
	 * 					CafeUI object when responding to user input.
	 */
	public UserListPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		setLayout(layout);
		
		constraints.gridheight = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		helptext = new JLabel(defaulthelp);
		helptext.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(helptext, constraints);
		add(helptext);
		
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(0,1));
		
		constraints.gridheight = 8;
		constraints.weighty = 1.0;
		
		userScroll = new JScrollPane(userPanel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setConstraints(userScroll, constraints);
		add(userScroll);
		
		constraints.gridheight = 1;
		constraints.weighty = 0.1;
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setFont(master.UI_FONT_BIG);
		layout.setConstraints(cancel, constraints);
		add(cancel);
	}
	/**Creates a list of buttons corresponding to the usernames registered
	 * in the system.
	 * 
	 * @param users		A String array containing all usernames.
	 */
	protected void listUsers(String[] users){
		userPanel.removeAll();
		Arrays.sort(users, String.CASE_INSENSITIVE_ORDER);
		usernames = users;
		userButtons = new JButton[users.length];
		for (int i = 0; i < users.length; i++){
			if (users[i] != null){
				userButtons[i] = new JButton(users[i]);
				userButtons[i].setFont(master.UI_FONT);
				userButtons[i].addActionListener(this);
				userPanel.add(userButtons[i]);
			}
		}
		userPanel.revalidate();
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String s){
		helptext.setText(s);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s == cancel){
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			setHelpText(defaulthelp);
		}
		
		for(int i = 0; i < userButtons.length; i++){
			if (s == userButtons[i]){
				master.loginUser(usernames[i]);
				master.switchPage(CafeUI.CONTINUE);
				setHelpText(defaulthelp);
			}
		}
	}

}
