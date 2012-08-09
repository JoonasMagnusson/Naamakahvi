package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;
/*
 * Oston vahvistusnäkymä
 */
public class CheckoutPage extends JPanel implements ActionListener, CloseableView{
	private static final int countdownLength = 90;
	
	private JLabel countdownText;
	private JButton ok, menu, cancel;
	private Timer countdownTimer;
	private int countdownSecs;
	private IProduct[] products;
	private int[] quantities;
	private CafeUI master;
	private JPanel purchasePanel;
	private ShortList userlist;
	protected boolean countingDown = false;
	
	public CheckoutPage(CafeUI master){
		this.master = master;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3,3,3,3);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		setLayout(layout);
		
		constraints.gridheight = 6;
		constraints.gridwidth = 1;
		
		userlist = new ShortList(master, this);
		//userlist.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/4*3 - layout.getVgap()));
		layout.setConstraints(userlist, constraints);
		add(userlist);
		//userPanel.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/4*3));
		//add(userPanel);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		purchasePanel = new JPanel();
		//purchasePanel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/4*3 - layout.getVgap()));
		purchasePanel.setLayout(new GridLayout(0,1));
		layout.setConstraints(purchasePanel, constraints);
		add(purchasePanel);
		
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 0.2;
		
		ok = new JButton("OK");
		ok.setFont(master.UI_FONT_BIG);
		//ok.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		ok.addActionListener(this);
		layout.setConstraints(ok, constraints);
		add(ok);	
		
		menu = new JButton("Change Products");
		menu.setFont(master.UI_FONT_BIG);
		//menu.setPreferredSize(new Dimension(master.X_RES/2 - 10, master.Y_RES/4));
		menu.addActionListener(this);
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		cancel = new JButton("Cancel");
		cancel.setFont(master.UI_FONT_BIG);
		//cancel.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
		//		master.Y_RES/8 - layout.getVgap()));
		cancel.addActionListener(this);	
		layout.setConstraints(cancel, constraints);
		add(cancel);
		
		countdownText = new JLabel("Placeholder", SwingConstants.CENTER);
		countdownText.setFont(master.UI_FONT);
		//countdownText.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
		//		master.Y_RES/6 - layout.getVgap()));
		layout.setConstraints(countdownText, constraints);
		add(countdownText);
		
		countdownTimer = new Timer(1000, this);
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
		countdownText.setText("The transaction will be automatically canceled in " + 
				countdownSecs + " seconds");
		countingDown = true;
	}
	/*
	 * Pysäyttää automaattikirjausajastimen
	 */
	public void closeView(){
		countdownTimer.stop();
		countingDown = false;
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
				countdownText.setText("The transaction will be automatically canceled in " + 
						countdownSecs + " seconds");
				countdownTimer.restart();
			}
			else {
				cancel.doClick();
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
