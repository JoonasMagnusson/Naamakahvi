package naamakahvi.swingui;

import javax.swing.*;

import naamakahvi.swingui.FaceCapture.FaceCanvas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class RegistrationPage extends JPanel implements ActionListener{
	private JTextField userName, firstName, lastName;
	private JButton takePic, register, cancel;
	private JLabel header, help, unLabel, fnLabel, lnLabel;
	private CafeUI master;
	private static final String defaultHelp = "Please enter the following information:";
	private JPanel actionPanel, thumbPanel;
	private FaceCanvas canvas;
	private Thumbnail[] thumbs;
	private BufferedImage[] images;
	private int thumbCount = 0;
	private static int MAX_THUMBCOUNT = 5;
	
	public RegistrationPage(CafeUI master){
		this.master = master;
		
		header = new JLabel("Register New User", SwingConstants.CENTER);
		header.setFont(master.UI_FONT_BIG);
		header.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/10));
		add(header);
		
		help = new JLabel(defaultHelp, SwingConstants.CENTER);
		help.setFont(master.UI_FONT_SMALL);
		help.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/10));
		add(help);
		
		canvas = master.getCanvas();
		canvas.setPreferredSize(new Dimension(master.X_RES/3, master.Y_RES/3));
		add(canvas);
		
		actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(master.X_RES/3*2-20, master.Y_RES/8*3+20));
		add(actionPanel);
		
		unLabel = new JLabel("Username:", SwingConstants.CENTER);
		unLabel.setFont(master.UI_FONT);
		unLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/8));
		actionPanel.add(unLabel);
		
		userName = new JTextField();
		userName.setFont(master.UI_FONT_BIG);
		userName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/8));
		actionPanel.add(userName);
		
		fnLabel = new JLabel("Given Name:", SwingConstants.CENTER);
		fnLabel.setFont(master.UI_FONT);
		fnLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/8));
		actionPanel.add(fnLabel);
		
		firstName = new JTextField();
		firstName.setFont(master.UI_FONT_BIG);
		firstName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/8));
		actionPanel.add(firstName);
		
		lnLabel = new JLabel("Family Name:", SwingConstants.CENTER);
		lnLabel.setFont(master.UI_FONT);
		lnLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/8));
		actionPanel.add(lnLabel);
		
		lastName = new JTextField();
		lastName.setFont(master.UI_FONT_BIG);
		lastName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/8));
		actionPanel.add(lastName);
		
		thumbPanel = new JPanel();
		thumbPanel.setLayout(new GridLayout(1,5));
		thumbPanel.setPreferredSize(new Dimension(master.X_RES/2, master.Y_RES/8));
		add(thumbPanel);
		
		resetPage();
		
		takePic = new JButton("Take Picture");
		takePic.setFont(master.UI_FONT);
		takePic.setPreferredSize(new Dimension(master.X_RES/4-30, master.Y_RES/8));
		takePic.addActionListener(this);
		
		register = new JButton("Register");
		register.setFont(master.UI_FONT);
		register.setPreferredSize(new Dimension(master.X_RES/4-30, master.Y_RES/8));
		register.addActionListener(this);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/8));
		cancel.addActionListener(this);
		
		add(takePic);
		add(register);
		add(cancel);
	}
	
	private void resetPage(){
		thumbPanel.removeAll();
		thumbs = new Thumbnail[MAX_THUMBCOUNT];
		for (int i=0; i < thumbs.length; i++){
			thumbs[i] = new Thumbnail(master.X_RES/10, master.X_RES/10);
			thumbPanel.add(thumbs[i]);
		}
		images = new BufferedImage[MAX_THUMBCOUNT];
		thumbPanel.revalidate();
	}
	
	protected void setHelpText(String text){
		help.setText(text);
	}
	
	protected void activate(){
		canvas.activate();
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s == takePic){
			BufferedImage i = master.takePic();
			
			if (i == null){
				setHelpText("No faces detected");
				return;
			}
			images[thumbCount] = i;
			thumbs[thumbCount].drawImage(i);
			thumbCount++;
			if (thumbCount == thumbs.length){
				thumbCount = 0;
			}
			setHelpText(defaultHelp);
		}
		
		if (s == register){
			if (master.RegisterUser(userName.getText(),
					firstName.getText(), lastName.getText(), images)){
				setHelpText(defaultHelp);
				master.switchPage(CafeUI.VIEW_MENU_PAGE);
				canvas.deactivate();
				resetPage();
			}
		}
		if (s == cancel){
			setHelpText(defaultHelp);
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			canvas.deactivate();
			resetPage();
		}
		
	}
}
