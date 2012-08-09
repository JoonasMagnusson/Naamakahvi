package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

public class StationSelect extends JPanel implements ActionListener{
	private JLabel helptext;
	private JButton[] stationButtons;
	private List<String> stations;
	private CafeUI master;
	
	public StationSelect(CafeUI master, List<String> stations){
		this.master = master;
		this.stations = stations;
		setLayout(new GridLayout(0,1));
		
		helptext = new JLabel("Select Location:", SwingConstants.CENTER);
		//helptext.setPreferredSize(new Dimension(master.X_RES, master.Y_RES/10));
		helptext.setFont(master.UI_FONT);
		add(helptext);
		
		stationButtons = new JButton[stations.size()];
		for (int i = 0; i < stations.size(); i++){
			if (stations.get(i) != null){
				stationButtons[i] = new JButton(stations.get(i));
				stationButtons[i].setFont(master.UI_FONT);
				stationButtons[i].addActionListener(this);
				add(stationButtons[i]);
			}
		}
	}
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		for (int i = 0; i < stationButtons.length; i++){
			if (s == stationButtons[i]){
				master.createStore(stations.get(i));
			}
		}
		
	}

}
