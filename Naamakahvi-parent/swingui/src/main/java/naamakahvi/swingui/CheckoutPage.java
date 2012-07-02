package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
/*
 * Oston vahvistusnäkymä
 */
public class CheckoutPage extends JPanel implements ActionListener{
	private JLabel purchaseText, countdownText;
	private JButton ok, menu, wrongUser;
	private Timer countdownTimer;
	private int countdownSecs;
	private static final int countdownLength = 10;
	private CafeUI master;
	
	public CheckoutPage(CafeUI master){
		this.master = master;
		
		purchaseText = new JLabel("Placeholder", SwingConstants.CENTER);
		purchaseText.setFont(CafeUI.UI_FONT);
		purchaseText.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/4));
		
		ok = new JButton("OK");
		ok.setFont(CafeUI.UI_FONT_BIG);
		ok.setPreferredSize(new Dimension(CafeUI.X_RES - 20, CafeUI.Y_RES/4));
		ok.addActionListener(this);
		
		menu = new JButton("Vaihda tuotetta");
		menu.setFont(CafeUI.UI_FONT_BIG);
		menu.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/4));
		menu.addActionListener(this);
		
		wrongUser = new JButton("Peruuta");
		wrongUser.setFont(CafeUI.UI_FONT_BIG);
		wrongUser.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/4));
		wrongUser.addActionListener(this);
		
		countdownText = new JLabel("Placeholder", SwingConstants.CENTER);
		countdownText.setFont(CafeUI.UI_FONT);
		countdownText.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		countdownTimer = new Timer(1000, this);
		
		add(purchaseText);
		add(ok);
		add(menu);
		add(wrongUser);
		add(countdownText);
	}
	/*
	 * Asettaa tekstin, joka kertoo käyttäjälle, mitä ollaan ostamassa
	 */
	protected void setPurchaseText(String text){
		purchaseText.setText(text);
	}
	/*
	 * Käynnistää ajastimen, jonka loppuessa ostos kirjataan automaattisesti
	 */
	protected void startCountdown(){
		countdownSecs = countdownLength;
		countdownTimer.start();
		countdownText.setText("Tuote veloitetaan automaattisesti " + countdownSecs + " sekunnin kuluttua");
	}
	/*
	 * Pysäyttää automaattikirjausajastimen
	 */
	protected void stopCountdown(){
		countdownTimer.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == countdownTimer){
			countdownSecs--;
			if (countdownSecs > 0){
				countdownText.setText("Tuote veloitetaan automaattisesti " + countdownSecs + " sekunnin kuluttua");
				countdownTimer.restart();
			}
			else {
				ok.doClick();
			}
		}
		if (s == menu){
			stopCountdown();
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
		if (s == wrongUser){
			stopCountdown();
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		if (s == ok){
			stopCountdown();
			if (master.buyProduct()){
				master.switchPage(CafeUI.VIEW_FRONT_PAGE);
			}
		}
	}
}
