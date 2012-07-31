package naamakahvi.swingui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import naamakahvi.naamakahviclient.SaldoItem;

public class ShortList extends JPanel implements ActionListener{
	private CafeUI master;
	private JButton switchUser;
	private JButton[] userbuttons;
	private JLabel selectedUser;
	private CloseableView container;
	private JPanel saldolist;
	
	public ShortList(CafeUI master, CloseableView container){
		this.master = master;
		this.container = container;
		setLayout(new GridLayout(0,1));
		switchUser = new JButton("User List");
		switchUser.setFont(master.UI_FONT);
		switchUser.addActionListener(this);
	}
	
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
		/*
		for(int i = 0; i < 2; i++){
			JLabel saldo = new JLabel("Saldo" + (i+1) + ": 0");
			saldo.setFont(master.UI_FONT_SMALL);
			add(saldo);
		}*/
		
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
	
	private void loadBalance(){
		List<SaldoItem> saldos = master.getSaldo();
		if (saldos == null){
			//TODO restore when saldos are working
			return;
			//throw new NullPointerException("Could not retrieve user balance");
		}
		saldolist.removeAll();
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
			master.continueLocation = master.currentLocation;
			master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
		}
		if (userbuttons == null) return;
		for (int i = 0; i < userbuttons.length; i++){
			if (s == userbuttons[i]){
				container.closeView();
				String username = userbuttons[i].getText();
				master.switchUser(username);
				selectedUser.setText(username);
				loadBalance();
			}
		}
	}
}
