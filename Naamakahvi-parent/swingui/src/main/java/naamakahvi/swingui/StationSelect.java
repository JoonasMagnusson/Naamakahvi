package naamakahvi.swingui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.Client;
/**A class implementing the station selection page of the Facecafe swingui
 * component.
 * The station selection page is displayed during program startup and shows
 * a list of possible locations. The user can use the page to select the
 * current location of the Facecafe terminal.
 * 
 * @author Antti Hietasaari
 *
 */
public class StationSelect extends JPanel implements ActionListener{
	private JLabel helptext;
	private JButton[] stationButtons;
	private List<String> stations;
	private CafeUI master;
	/**Creates a new station selection page.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The station selection page accesses the methods of the
	 * 					CafeUI object when responding to user input.
	 * @param stations	A List of Strings containing the names of the stations
	 * 					that are available.
	 */
	public StationSelect(CafeUI master, List<String> stations){
		this.master = master;
		this.stations = stations;
		setLayout(new GridLayout(0,1));
		
		helptext = new JLabel("Select Location:", SwingConstants.CENTER);
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
				master.createStore(new Client(master.ADDRESS_IP, 
						master.ADDRESS_PORT, stations.get(i)));
			}
		}
		
	}

}
