package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import naamakahvi.naamakahviclient.IStation;
import javax.swing.*;

public class StationSelect extends JPanel implements ActionListener{
	private JLabel helptext;
	private JButton[] stationButtons;
	private List<IStation> stations;
	private CafeUI master;
	
	public StationSelect(CafeUI master, List<IStation> stations){
		this.master = master;
		this.stations = stations;
		
		helptext = new JLabel("Valitse sijainti:");
		helptext.setPreferredSize(new Dimension(CafeUI.X_RES, CafeUI.Y_RES/10));
		helptext.setFont(CafeUI.UI_FONT);
		add(helptext);
		
		stationButtons = new JButton[stations.size()];
		for (int i = 0; i < stations.size(); i++){
			if (stations.get(i) != null){
				stationButtons[i] = new JButton(stations.get(i).getName());
				stationButtons[i].setFont(CafeUI.UI_FONT);
				stationButtons[i].addActionListener(this);
				add(stationButtons[i]);
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		for (int i = 0; i < stationButtons.length; i++){
			if (s == stationButtons[i]){
				master.createStore(stations.get(i));
			}
		}
		
	}

}