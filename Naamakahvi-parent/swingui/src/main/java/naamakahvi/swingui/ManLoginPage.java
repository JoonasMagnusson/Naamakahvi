package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ManLoginPage extends JPanel implements ActionListener{
	private JTextField username;
	private JButton ok, cancel;
	private JLabel header, help;
	private CafeUI master;
	private static final String defaultHelp = "Please enter your username:";
	
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
		//header.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5 - layout.getVgap()));
		layout.setConstraints(header, constraints);
		add(header);
		
		help = new JLabel(defaultHelp, SwingConstants.CENTER);
		help.setFont(master.UI_FONT_SMALL);
		//help.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5 - layout.getVgap()));
		layout.setConstraints(help, constraints);
		add(help);
		
		username = new JTextField();
		username.setFont(master.UI_FONT_BIG);
		//username.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5 - layout.getVgap()));
		layout.setConstraints(username, constraints);
		add(username);
		
		ok = new JButton("OK");
		ok.setFont(master.UI_FONT_BIG);
		//ok.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5 - layout.getVgap()));
		ok.addActionListener(this);
		layout.setConstraints(ok, constraints);
		add(ok);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		//cancel.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/5 - layout.getVgap()));
		cancel.addActionListener(this);
		layout.setConstraints(cancel, constraints);
		add(cancel);
	}
	
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
