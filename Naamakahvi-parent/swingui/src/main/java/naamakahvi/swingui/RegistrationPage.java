package naamakahvi.swingui;

import javax.swing.*;

import naamakahvi.swingui.FaceCapture.FaceCanvas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
/**A class implementing the registration page of the Facecafe swingui component.
 * The page allows the user to enter user information, take pictures and
 * register a new user.
 * 
 * @author Antti Hietasaari
 *
 */
public class RegistrationPage extends JPanel implements ActionListener{
	private JTextField userName, firstName, lastName;
	private JButton takePic, register, cancel;
	private JLabel header, help, unLabel, fnLabel, lnLabel;
	private CafeUI master;
	/**
	 * The default help text shown when the view is opened.
	 */
	protected static final String DEFAULT_HELP = "Please enter the following information:";
	private JPanel actionPanel, thumbPanel;
	private FaceCanvas canvas;
	private Thumbnail[] thumbs;
	private BufferedImage[] images;
	private int thumbCount = 0;
	/**
	 * The maximum amount of pictures the user is allowed to associate with
	 * their account during initial account creation.
	 */
	protected static final int MAX_THUMBCOUNT = 6;
	/**Creates a new registration page.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The registration page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public RegistrationPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 0.1;
		setLayout(layout);
		
		constraints.gridheight = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		header = new JLabel("Register New User", SwingConstants.CENTER);
		header.setFont(master.UI_FONT_BIG);
		layout.setConstraints(header, constraints);
		add(header);
		
		help = new JLabel(DEFAULT_HELP, SwingConstants.CENTER);
		help.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(help, constraints);
		add(help);
		
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weighty = 1.0;
		
		canvas = master.getCanvas();
		canvas.setName("canvas");
		layout.setConstraints(canvas, constraints);
		add(canvas);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 0.5;
		
		actionPanel = new JPanel();
		GridBagLayout actionLayout = new GridBagLayout();
		GridBagConstraints actionConstraints = new GridBagConstraints();
		actionPanel.setLayout(actionLayout);
		layout.setConstraints(actionPanel, constraints);
		add(actionPanel);
		
		actionConstraints.fill = GridBagConstraints.BOTH;
		actionConstraints.weightx = 0.3;
		actionConstraints.weighty = 1.0;
		actionConstraints.gridwidth = 1;
		
		unLabel = new JLabel("Username:", SwingConstants.CENTER);
		unLabel.setFont(master.UI_FONT);
		actionLayout.setConstraints(unLabel, actionConstraints);
		actionPanel.add(unLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		userName = new JTextField();
		userName.setName("un");
		userName.setFont(master.UI_FONT_BIG);
		actionLayout.setConstraints(userName, actionConstraints);
		actionPanel.add(userName);
		
		actionConstraints.gridwidth = 1;
		actionConstraints.weightx = 0.3;
		
		fnLabel = new JLabel("Given Name:", SwingConstants.CENTER);
		fnLabel.setFont(master.UI_FONT);
		actionLayout.setConstraints(fnLabel, actionConstraints);
		actionPanel.add(fnLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		firstName = new JTextField();
		firstName.setName("fn");
		firstName.setFont(master.UI_FONT_BIG);
		actionLayout.setConstraints(firstName, actionConstraints);
		actionPanel.add(firstName);
		
		actionConstraints.gridwidth = 1;
		actionConstraints.weightx = 0.3;
		
		lnLabel = new JLabel("Family Name:", SwingConstants.CENTER);
		lnLabel.setFont(master.UI_FONT);
		actionLayout.setConstraints(lnLabel, actionConstraints);
		actionPanel.add(lnLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		lastName = new JTextField();
		lastName.setName("ln");
		lastName.setFont(master.UI_FONT_BIG);
		actionLayout.setConstraints(lastName, actionConstraints);
		actionPanel.add(lastName);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 0.5;
		
		thumbPanel = new JPanel();
		GridLayout thumbgrid = new GridLayout(1, MAX_THUMBCOUNT);
		
		thumbPanel.setLayout(thumbgrid);
		layout.setConstraints(thumbPanel, constraints);
		add(thumbPanel);
		
		resetPage();
		
		constraints.gridwidth = 1;
		constraints.weightx = 0.3;
		
		takePic = new JButton("Take Picture");
		takePic.setFont(master.UI_FONT);
		layout.setConstraints(takePic, constraints);
		takePic.addActionListener(this);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		register = new JButton("Register");
		register.setFont(master.UI_FONT);
		layout.setConstraints(register, constraints);
		register.addActionListener(this);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		layout.setConstraints(cancel, constraints);
		cancel.addActionListener(this);
		
		add(takePic);
		add(register);
		add(cancel);
	}
	/**
	 * Removes thumbnails when the view is closed.
	 */
	private void resetPage(){
		thumbPanel.removeAll();
		thumbs = new Thumbnail[MAX_THUMBCOUNT];
		for (int i=0; i < thumbs.length; i++){
			thumbs[i] = new Thumbnail();
			thumbs[i].setName("thumb"+i);
			thumbPanel.add(thumbs[i]);
		}
		images = new BufferedImage[MAX_THUMBCOUNT];
		thumbPanel.revalidate();
		thumbCount = 0;
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String text){
		help.setText(text);
	}
	/**
	 * Activates the FaceCanvas embedded on the page, causing it to start
	 * showing a feed from the system's webcam, if available. Should be called
	 * whenever the registration view is shown.
	 */
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
			setHelpText(DEFAULT_HELP);
		}
		
		if (s == register){
			String un = userName.getText();
			if (un.length() < 1){
				setHelpText("Username can't be empty!");
				return;
			}
			if (un.length() > 8){
				setHelpText("Usernames aren't allowed to be over 8 characters in length");
				return;
			}
			if (master.registerUser(userName.getText(),
					firstName.getText(), lastName.getText(), images)){
				setHelpText(DEFAULT_HELP);
				master.switchPage(CafeUI.VIEW_FRONT_PAGE);
				canvas.deactivate();
				resetPage();
			}
		}
		if (s == cancel){
			setHelpText(DEFAULT_HELP);
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			canvas.deactivate();
			resetPage();
		}
		
	}
}
