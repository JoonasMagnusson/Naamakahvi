package naamakahvi.swingui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import naamakahvi.swingui.FaceCapture.FaceCanvas;

public class FaceLoginPage extends JPanel implements ActionListener{
	private FaceCanvas canvas;
	private JButton login, cancel;
	private JLabel helptext;
	protected final static String DEFAULT_HELP = "Press 'Take Picture' to start face recognition";
	
	private CafeUI master;
	
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
		//helptext.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		helptext.setFont(master.UI_FONT_SMALL);
		layout.setConstraints(helptext, constraints);
		add(helptext);
		
		constraints.weighty = 1.0;
		constraints.gridheight = 6;
		//constraints.fill = GridBagConstraints.NONE;
		
		JPanel canvasContainer = new JPanel();
		
		canvas = master.getCanvas();
		canvas.setName("canvas");
		//canvas.setPreferredSize(new Dimension(master.X_RES/4*3 - layout.getHgap(),
		//		master.Y_RES/4*3 - layout.getVgap()));
		layout.setConstraints(canvas, constraints);
		add(canvas);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 0.0;
		
		login = new JButton("Take Picture");
		//login.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		login.setFont(master.UI_FONT_BIG);
		login.addActionListener(this);
		layout.setConstraints(login, constraints);
		add(login);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancel = new JButton("Cancel");
		//cancel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		cancel.setFont(master.UI_FONT_BIG);
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
