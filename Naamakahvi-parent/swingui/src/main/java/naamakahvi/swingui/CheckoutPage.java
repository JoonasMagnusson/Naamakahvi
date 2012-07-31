package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;
/*
 * Oston vahvistusnäkymä
 */
public class CheckoutPage extends JPanel implements ActionListener, CloseableView{
	private static final int countdownLength = 10;
	
	private JLabel countdownText;
	private JButton ok, menu, cancel, switchUser;
	private Timer countdownTimer;
	private int countdownSecs;
	private IProduct[] products;
	private int[] quantities;
	private CafeUI master;
	private JPanel purchasePanel;
	private ShortList userlist;
	
	public CheckoutPage(CafeUI master){
		this.master = master;
		FlowLayout layout = new FlowLayout();
		//layout.setHgap(0);
		//layout.setVgap(0);
		setLayout(layout);
		
		userlist = new ShortList(master, this);
		userlist.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/4*3 - layout.getVgap()));
		add(userlist);
		//userPanel.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/4*3));
		//add(userPanel);
		
		purchasePanel = new JPanel();
		purchasePanel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/4*3 - layout.getVgap()));
		purchasePanel.setLayout(new GridLayout(0,1));
		add(purchasePanel);
		
		ok = new JButton("OK");
		ok.setFont(master.UI_FONT_BIG);
		ok.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		ok.addActionListener(this);
		add(ok);	
		
		menu = new JButton("Change Products");
		menu.setFont(master.UI_FONT_BIG);
		//menu.setPreferredSize(new Dimension(master.X_RES/2 - 10, master.Y_RES/4));
		menu.addActionListener(this);
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/8 - layout.getVgap()));
		cancel.addActionListener(this);	
		add(cancel);
		
		countdownText = new JLabel("Placeholder", SwingConstants.CENTER);
		countdownText.setFont(master.UI_FONT);
		countdownText.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
				master.Y_RES/6 - layout.getVgap()));
		add(countdownText);
		
		countdownTimer = new Timer(1000, this);

		switchUser = new JButton("Switch User");
		switchUser.setFont(master.UI_FONT);
		switchUser.addActionListener(this);
		
	}
	
	protected void setUsers(String[] usernames){
		userlist.setUsers(usernames);
	}
	
	
	protected void setProducts(IProduct[] products, int[] quantities, String mode){
		if (products == null || quantities == null ||
				products.length != quantities.length
				 || products.length < 1){
			throw new IllegalArgumentException("Unable to parse purchase parameters");
		}
		
		this.products = products;
		this.quantities = quantities;
		purchasePanel.removeAll();
		
		JLabel header;
		if (mode.equals(CafeUI.MODE_BUY)){
			header = new JLabel("Buying:");
		}
		else if (mode.equals(CafeUI.MODE_BRING)){
			header = new JLabel("Bringing:");
		}
		else{
			throw new IllegalArgumentException(
					"Mode passed to checkout was neither buy nor sell");
		}
		header.setFont(master.UI_FONT_BIG);
		purchasePanel.add(header);
		
		for (int i = 0; i < products.length; i++){
			JLabel product = new JLabel(products[i].getName() + " x " + quantities[i]);
			product.setFont(master.UI_FONT);
			purchasePanel.add(product);
		}
		
		purchasePanel.add(menu);
		purchasePanel.revalidate();
		
	}
	/*
	 * Käynnistää ajastimen, jonka loppuessa ostos kirjataan automaattisesti
	 */
	protected void startCountdown(){
		countdownSecs = countdownLength;
		countdownTimer.start();
		countdownText.setText("The transaction will complete automatically in " + 
				countdownSecs + " seconds");
	}
	/*
	 * Pysäyttää automaattikirjausajastimen
	 */
	public void closeView(){
		countdownTimer.stop();
	}
	
	protected void setHelpText(String text){
		closeView();
		countdownText.setText(text);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == countdownTimer){
			countdownSecs--;
			if (countdownSecs > 0){
				countdownText.setText("The transaction will complete automatically in " + 
						countdownSecs + " seconds");
				countdownTimer.restart();
			}
			else {
				ok.doClick();
			}
		}
		if (s == menu){
			closeView();
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
		if (s == cancel){
			closeView();
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == ok){
			closeView();
			boolean error = false;
			for (int i = 0; i < products.length;i++){
				if (!master.buyProduct(products[i], quantities[i])){
					error = true;
				}
			}
			if (!error){
				master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			}
			
		}
	}
}
