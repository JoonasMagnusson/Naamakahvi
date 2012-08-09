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
	protected static final String DEFAULT_HELP = "Please enter the following information:";
	private JPanel actionPanel, thumbPanel;
	private FaceCanvas canvas;
	private Thumbnail[] thumbs;
	private BufferedImage[] images;
	private int thumbCount = 0;
	protected static final int MAX_THUMBCOUNT = 5;
	private FlowLayout layout;
	
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
		//header.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(header, constraints);
		add(header);
		
		help = new JLabel(DEFAULT_HELP, SwingConstants.CENTER);
		help.setFont(master.UI_FONT_SMALL);
		//help.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(help, constraints);
		add(help);
		
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weighty = 1.0;
		
		canvas = master.getCanvas();
		//canvas.setPreferredSize(new Dimension(master.X_RES/3 - layout.getHgap(),
		//		master.Y_RES/3 - layout.getHgap()));
		canvas.setName("canvas");
		layout.setConstraints(canvas, constraints);
		add(canvas);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 0.5;
		
		actionPanel = new JPanel();
		GridBagLayout actionLayout = new GridBagLayout();
		GridBagConstraints actionConstraints = new GridBagConstraints();
		actionPanel.setLayout(actionLayout);
		//actionPanel.setPreferredSize(new Dimension(master.X_RES/3*2 - layout.getHgap(),
		//		master.Y_RES/3 - layout.getHgap()));
		layout.setConstraints(actionPanel, constraints);
		add(actionPanel);
		
		actionConstraints.fill = GridBagConstraints.BOTH;
		actionConstraints.weightx = 0.3;
		actionConstraints.weighty = 1.0;
		actionConstraints.gridwidth = 1;
		
		unLabel = new JLabel("Username:", SwingConstants.CENTER);
		unLabel.setFont(master.UI_FONT);
		//unLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/10));
		actionLayout.setConstraints(unLabel, actionConstraints);
		actionPanel.add(unLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		userName = new JTextField();
		userName.setName("un");
		userName.setFont(master.UI_FONT_BIG);
		//userName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/10));
		actionLayout.setConstraints(userName, actionConstraints);
		actionPanel.add(userName);
		
		actionConstraints.gridwidth = 1;
		actionConstraints.weightx = 0.3;
		
		fnLabel = new JLabel("Given Name:", SwingConstants.CENTER);
		fnLabel.setFont(master.UI_FONT);
		//fnLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/10));
		actionLayout.setConstraints(fnLabel, actionConstraints);
		actionPanel.add(fnLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		firstName = new JTextField();
		firstName.setName("fn");
		firstName.setFont(master.UI_FONT_BIG);
		//firstName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/10));
		actionLayout.setConstraints(firstName, actionConstraints);
		actionPanel.add(firstName);
		
		actionConstraints.gridwidth = 1;
		actionConstraints.weightx = 0.3;
		
		lnLabel = new JLabel("Family Name:", SwingConstants.CENTER);
		lnLabel.setFont(master.UI_FONT);
		//lnLabel.setPreferredSize(new Dimension(master.X_RES/9*2, master.Y_RES/10));
		actionLayout.setConstraints(lnLabel, actionConstraints);
		actionPanel.add(lnLabel);
		
		actionConstraints.gridwidth = GridBagConstraints.REMAINDER;
		actionConstraints.weightx = 1.0;
		
		lastName = new JTextField();
		lastName.setName("ln");
		lastName.setFont(master.UI_FONT_BIG);
		//lastName.setPreferredSize(new Dimension(master.X_RES/9*3, master.Y_RES/10));
		actionLayout.setConstraints(lastName, actionConstraints);
		actionPanel.add(lastName);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 0.5;
		
		thumbPanel = new JPanel();
		GridLayout thumbgrid = new GridLayout(1, 5);
		//flow.setHgap(0);
		//flow.setVgap(0);
		thumbPanel.setLayout(thumbgrid);
		//thumbPanel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(thumbPanel, constraints);
		add(thumbPanel);
		
		resetPage();
		
		constraints.gridwidth = 1;
		constraints.weightx = 0.3;
		
		takePic = new JButton("Take Picture");
		takePic.setFont(master.UI_FONT);
		//takePic.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(takePic, constraints);
		takePic.addActionListener(this);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		register = new JButton("Register");
		register.setFont(master.UI_FONT);
		//register.setPreferredSize(new Dimension(master.X_RES/4 - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(register, constraints);
		register.addActionListener(this);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		//cancel.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/6 - layout.getHgap()));
		layout.setConstraints(cancel, constraints);
		cancel.addActionListener(this);
		
		add(takePic);
		add(register);
		add(cancel);
	}
	
	private void resetPage(){
		thumbPanel.removeAll();
		thumbs = new Thumbnail[MAX_THUMBCOUNT];
		//int size = (master.X_RES/10 - layout.getHgap() < master.Y_RES/6  - layout.getVgap()?
		//		master.X_RES/10  - layout.getHgap() : master.Y_RES/6  - layout.getHgap());
		for (int i=0; i < thumbs.length; i++){
			thumbs[i] = new Thumbnail();
			thumbs[i].setName("thumb"+i);
			thumbPanel.add(thumbs[i]);
		}
		images = new BufferedImage[MAX_THUMBCOUNT];
		thumbPanel.revalidate();
		thumbCount = 0;
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
			setHelpText(DEFAULT_HELP);
		}
		
		if (s == register){
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
