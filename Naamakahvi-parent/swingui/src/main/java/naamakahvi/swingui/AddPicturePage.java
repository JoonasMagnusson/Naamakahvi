package naamakahvi.swingui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import naamakahvi.swingui.FaceCapture.FaceCanvas;
/**A class implementing the image addition page of the Facecafe swingui component.
 * The image addition page allows a logged in user to associate images captured
 * from a live webcam feed with their account. The images can then be used by the
 * face recognition program on the Facecafe server to identify the user.
 * 
 * @author Antti Hietasaari
 *
 */
public class AddPicturePage extends JPanel implements ActionListener, CloseableView{
	private CafeUI master;
	private JLabel helptext;
	private FaceCanvas canvas;
	private ShortList userlist;
	private JPanel thumbPanel;
	private JButton ok, cancel, takePic;
	private BufferedImage[] images;
	private Thumbnail[] thumbs;
	private int thumbcount = 0;
	/**
	 * The default help message shown when the view is opened.
	 */
	public static final String DEFAULT_HELP = "Enter your username below and use the 'Take Picture' button to take pictures to add to your account";
	/**
	 * The maximum amount of images that can be registered at the same time.
	 */
	public static final int MAX_IMAGES = 5;
	/**Creates a new image addition page.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The image addition page accesses the methods of the 
	 * 					CafeUI object when responding to user input.
	 */
	public AddPicturePage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		setLayout(layout);
		
		helptext = new JLabel(DEFAULT_HELP);
		helptext.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(helptext, constraints);
		add(helptext);
		
		constraints.weighty = 1.0;
		constraints.gridheight = 4;
		constraints.gridwidth = 2;
		
		userlist = new ShortList(master, this);
		layout.setConstraints(userlist, constraints);
		add(userlist);
		
		constraints.gridheight = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		canvas = master.getCanvas();
		layout.setConstraints(canvas, constraints);
		add(canvas);
		
		constraints.gridheight = 1;
		constraints.weighty = 0.3;
		
		thumbPanel = new JPanel();
		thumbPanel.setLayout(new GridLayout(1, MAX_IMAGES));
		layout.setConstraints(thumbPanel, constraints);
		add(thumbPanel);
		
		resetThumbs();
		
		constraints.weighty = 0.2;
		constraints.gridwidth = 1;
		
		takePic = new JButton("Take Picture");
		takePic.setFont(master.UI_FONT);
		takePic.addActionListener(this);
		layout.setConstraints(takePic, constraints);
		add(takePic);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		ok = new JButton("Add Pictures");
		ok.setFont(master.UI_FONT);
		ok.addActionListener(this);
		layout.setConstraints(ok, constraints);
		add(ok);
		
		cancel = new JButton("Back to Menu");
		cancel.setFont(master.UI_FONT);
		cancel.addActionListener(this);
		layout.setConstraints(cancel, constraints);
		add(cancel);
	}
	/**
	 * Activates the canvas element on the page. Must be called when the page is
	 * shown in order to show a live video feed from the webcam.
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
	/**
	 * Removes captured images from view and system memory.
	 */
	private void resetThumbs() {
		thumbPanel.removeAll();
		thumbs = new Thumbnail[MAX_IMAGES];
		images = new BufferedImage[MAX_IMAGES];
		for (int i = 0; i < MAX_IMAGES; i++){
			thumbs[i] = new Thumbnail();
			thumbPanel.add(thumbs[i]);
		}
		thumbcount = 0;
		thumbPanel.revalidate();
		thumbPanel.repaint();
	}
	/**Sets the recognized users for this page. The first user on the list
	 * is assumed to be logged in.
	 * 
	 * @see	ShortList.setUsers()
	 * 
	 * @param usernames		A String array containing the usernames of the
	 * 						recognized users.
	 */
	protected void setUsers(String[] users){
		userlist.setUsers(users);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancel){
			closeView();
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
		if (s == takePic){
			BufferedImage img = master.takePic();
			if (img == null){
				setHelpText("No faces detected");
				return;
			}
			thumbs[thumbcount].drawImage(img);
			images[thumbcount] = img;
			thumbcount++;
			if (thumbcount == MAX_IMAGES){
				thumbcount = 0;
			}
		}
		if (s == ok){
			if (images[0] == null){
				setHelpText("You must take pictures before adding them to your account");
				return;
			}
			if (master.addImages(images)){
				setHelpText("Images have been successfully added to your account!");
				resetThumbs();
			}
		}
		
	}
	
	public void closeView() {
		resetThumbs();
		canvas.deactivate();
		setHelpText(DEFAULT_HELP);
	}

}
