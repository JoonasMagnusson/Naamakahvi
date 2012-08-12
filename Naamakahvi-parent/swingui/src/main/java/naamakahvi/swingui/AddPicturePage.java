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
	public static final String DEFAULT_HELP = "Enter your username below and use the 'Take Picture' button to take pictures to add to your account";
	public static final int MAX_IMAGES = 5;
	
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
	
	protected void activate(){
		canvas.activate();
	}
	
	protected void setHelpText(String text){
		helptext.setText(text);
	}
	
	private void resetThumbs() {
		thumbPanel.removeAll();
		thumbs = new Thumbnail[MAX_IMAGES];
		images = new BufferedImage[MAX_IMAGES];
		for (int i = 0; i < MAX_IMAGES; i++){
			thumbs[i] = new Thumbnail();
			thumbPanel.add(thumbs[i]);
		}
		thumbcount = 0;
	}

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
