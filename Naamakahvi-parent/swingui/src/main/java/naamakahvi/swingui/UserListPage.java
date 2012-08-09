package naamakahvi.swingui;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

public class UserListPage extends JPanel implements ActionListener{
	private String[] usernames;
	private JButton[] userButtons;
	private JButton cancel;
	private JPanel userPanel;
	private JScrollPane userScroll;
	private CafeUI master;
	private JLabel helptext;
	private String defaulthelp = "Please select your username from the list below:";

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
		//helptext.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/10 - layout.getVgap()));
		layout.setConstraints(helptext, constraints);
		add(helptext);
		
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(0,1));
		
		constraints.gridheight = 8;
		constraints.weighty = 1.0;
		
		userScroll = new JScrollPane(userPanel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//userScroll.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5*4 - layout.getVgap()));
		layout.setConstraints(userScroll, constraints);
		add(userScroll);
		
		constraints.gridheight = 1;
		constraints.weighty = 0.1;
		
		cancel = new JButton("Cancel");
		//cancel.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/10 - layout.getVgap()));
		cancel.addActionListener(this);
		cancel.setFont(master.UI_FONT_BIG);
		layout.setConstraints(cancel, constraints);
		add(cancel);
		
		
	}
	
	protected void listUsers(String[] users){
		userPanel.removeAll();
		
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
