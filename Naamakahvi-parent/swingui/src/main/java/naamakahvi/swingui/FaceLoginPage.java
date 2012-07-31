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
	private String defaultHelp = "Press 'Take Picture' to start face recognition";
	
	private CafeUI master;
	
	public FaceLoginPage(CafeUI master){
		this.master = master;
		FlowLayout layout = new FlowLayout();
		//layout.setHgap(0);
		//layout.setVgap(0);
		setLayout(layout);
		
		helptext = new JLabel(defaultHelp);
		helptext.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		helptext.setFont(master.UI_FONT_SMALL);
		add(helptext);
		
		canvas = master.getCanvas();
		canvas.setPreferredSize(new Dimension(master.X_RES/4*3 - layout.getHgap(),
				master.Y_RES/4*3 - layout.getVgap()));
		add(canvas);
		
		login = new JButton("Take Picture");
		login.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		login.setFont(master.UI_FONT_BIG);
		login.addActionListener(this);
		add(login);
		
		cancel = new JButton("Cancel");
		cancel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		cancel.setFont(master.UI_FONT_BIG);
		cancel.addActionListener(this);
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
			setHelpText(defaultHelp);
		}
		if (s == login){
			canvas.deactivate();
			if (!master.validateImage()){
				canvas.activate();
				setHelpText(defaultHelp);
			}
		}
	}
}
