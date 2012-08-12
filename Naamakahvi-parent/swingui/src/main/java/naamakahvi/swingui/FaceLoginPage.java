package naamakahvi.swingui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import naamakahvi.swingui.FaceCapture.FaceCanvas;

/**A class implementing the face login page in the Facecafe swingui component.
 * Shows the user a live feed of the system's webcam and allows them to take
 * a picture of themselves in order to verify their identity.
 * 
 * @author Antti Hietasaari
 *
 */
public class FaceLoginPage extends JPanel implements ActionListener{
	private FaceCanvas canvas;
	private JButton login, cancel;
	private JLabel helptext;
	/**
	 * The default help text shown to the user when there are no errors to
	 * show.
	 */
	protected final static String DEFAULT_HELP = "Press 'Take Picture' to start face recognition";
	
	private CafeUI master;
	
	/**Creates a new FaceLoginPage object.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The face login page accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 */
	public FaceLoginPage(CafeUI master){
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
		
		helptext = new JLabel(DEFAULT_HELP);
		helptext.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(helptext, constraints);
		add(helptext);
		
		constraints.weighty = 1.0;
		constraints.gridheight = 6;
		
		canvas = master.getCanvas();
		canvas.setName("canvas");
		layout.setConstraints(canvas, constraints);
		add(canvas);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 0.0;
		
		login = new JButton("Take Picture");
		login.setFont(master.UI_FONT_BIG);
		login.addActionListener(this);
		layout.setConstraints(login, constraints);
		add(login);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		cancel.addActionListener(this);
		layout.setConstraints(cancel, constraints);
		add(cancel);
	}
	/**
	 * Activates the FaceCanvas object associated with this page. Must be
	 * called in order to show live camera feed.
	 */
	protected void activate(){
		canvas.activate();
	}
	/**Shows the user a message, e.g. help or an error message.
	 * 
	 * @param text		A String containing the text to be shown
	 */
	protected void setHelpText(String text){
		helptext.setText(text);
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancel){
			canvas.deactivate();
			master.continueLocation = CafeUI.VIEW_FRONT_PAGE;
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			setHelpText(DEFAULT_HELP);
		}
		if (s == login){
			canvas.deactivate();
			if (!master.validateImage()){
				canvas.activate();
				
			}
			else {
				setHelpText(DEFAULT_HELP);
			}
		}
	}
}
