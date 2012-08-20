package naamakahvi.swingui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import naamakahvi.naamakahviclient.*;
/**A short user list used by the Facecafe swingui component.
 * The list shows the logged in user, the user's account balances and the
 * user's closest face matches. It also allows the user to quickly change
 * the logged in account to one of the closest matches or to log in manually
 * through the UserListPage.
 * 
 * @author Antti Hietasaari
 *
 */
public class ShortList extends JPanel implements ActionListener{
	private CafeUI master;
	private JButton switchUser;
	private JButton[] userbuttons;
	private JLabel selectedUser;
	private CloseableView container;
	private JPanel saldolist;
	/**Creates a new short user list.
	 * 
	 * @param master	The CafeUI object that this page is associated with.
	 * 					The user list accesses the methods of the CafeUI
	 * 					object when responding to user input.
	 * @param container		An object implementing the CloseableView interface.
	 * 						Whenever the user interacts
	 */
	public ShortList(CafeUI master, CloseableView container){
		this.master = master;
		this.container = container;
		setLayout(new GridLayout(0,1));
		switchUser = new JButton("User List");
		switchUser.setFont(master.UI_FONT);
		switchUser.addActionListener(this);
	}
	/**Sets the usernames of the best face matches shown in the list.
	 * The first name on the list is assumed to be logged in, and the list
	 * contains buttons that allow the user to quickly switch to the accounts
	 * of the the best face matches.
	 * 
	 * @param usernames		A String array containing the usernames of the
	 * 						best face matches.
	 */
	protected void setUsers(String[] usernames){
		if (usernames == null || usernames.length < 1){
			throw new IllegalArgumentException("No usernames passed");
		}
		
		removeAll();
		
		JLabel userheader = new JLabel("Logged in user:");
		userheader.setFont(master.UI_FONT);
		add(userheader);
		
		selectedUser = new JLabel(usernames[0]);
		selectedUser.setFont(master.UI_FONT_BIG);
		add(selectedUser);
		
		JLabel saldoheader = new JLabel("Account Balance:");
		saldoheader.setFont(master.UI_FONT);
		add(saldoheader);
		
		saldolist = new JPanel();
		add(saldolist);
		loadBalance();
		
		if (usernames.length > 1){
			JLabel userlistheader = new JLabel("Closest face matches:");
			userlistheader.setFont(master.UI_FONT);
			add(userlistheader);
			
			userbuttons = new JButton[usernames.length];
			for(int i = 0; i < usernames.length; i++){
				userbuttons[i] = new JButton(usernames[i]);
				userbuttons[i].setFont(master.UI_FONT_SMALL);
				userbuttons[i].addActionListener(this);
				add(userbuttons[i]);
			}
		}
		add(switchUser);
		revalidate();
	}
	/**
	 * Loads and displays the account balances of the currently logged in user.
	 */
	private void loadBalance(){
		List<SaldoItem> saldos = master.getSaldo();
		if (saldos == null){
			//TODO restore when saldos are working reliably
			System.err.println("Invalid saldo");
			return;
			//throw new NullPointerException("Could not retrieve user balance");
		}
		saldolist.removeAll();
		saldolist.setLayout(new GridLayout(0, 1));
		Iterator<SaldoItem> i = saldos.iterator();
		while (i.hasNext()){
			SaldoItem s = i.next();
			JLabel saldolabel = new JLabel(s.getGroupName() + ": " + s.getSaldo());
			saldolabel.setFont(master.UI_FONT_SMALL);
			saldolist.add(saldolabel);
		}
		saldolist.revalidate();
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s == switchUser){
			container.closeView();
			master.continueLocation = master.getCurrentLocation();
			master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
		}
		if (userbuttons == null) return;
		for (int i = 0; i < userbuttons.length; i++){
			if (s == userbuttons[i]){
				String username = userbuttons[i].getText();
				if (master.switchUser(username)){
					selectedUser.setText(username);
					loadBalance();
				}
			}
		}
	}
}
