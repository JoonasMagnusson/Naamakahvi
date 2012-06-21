package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
/*
 * Oston vahvistusnäkymä
 */
public class CheckoutPage extends JPanel implements ActionListener{
	private FlowLayout layout;
	private JLabel purchaseText, countdownText;
	private IDButton ok, cancel;
	private Timer countdownTimer;
	private int countdownSecs = 10;
	
	public CheckoutPage(CafeUI master){
		layout = new FlowLayout();
		setLayout(layout);
		
		purchaseText = new JLabel("Placeholder", SwingConstants.CENTER);
		purchaseText.setFont(CafeUI.UI_FONT_BIG);
		purchaseText.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/3));
		
		ok = new IDButton(CafeUI.BUTTON_CONFIRM_PURCHASE, "OK");
		ok.setFont(CafeUI.UI_FONT_BIG);
		ok.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/3));
		ok.addActionListener(master);
		
		cancel = new IDButton(CafeUI.BUTTON_CANCEL, "Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.setPreferredSize(new Dimension(CafeUI.X_RES/2 - 10, CafeUI.Y_RES/3));
		cancel.addActionListener(master);
		
		countdownText = new JLabel("Placeholder", SwingConstants.CENTER);
		countdownText.setFont(CafeUI.UI_FONT);
		countdownText.setPreferredSize(new Dimension(CafeUI.X_RES-20, CafeUI.Y_RES/6));
		
		countdownTimer = new Timer(1000, this);
		
		add(purchaseText);
		add(ok);
		add(cancel);
		add(countdownText);
	}
	/*
	 * Asettaa tekstin, joka kertoo käyttäjälle, mitä ollaan ostamassa
	 */
	public void setPurchaseText(String text){
		purchaseText.setText(text);
	}
	/*
	 * Käynnistää ajastimen, jonka loppuessa ostos kirjataan automaattisesti
	 */
	public void startCountdown(){
		countdownTimer.start();
		countdownText.setText("Tuote veloitetaan automaattisesti " + countdownSecs + " sekunnin kuluttua");
	}
	/*
	 * Pysäyttää automaattikirjausajastimen
	 */
	public void stopCountdown(){
		countdownTimer.stop();
		countdownSecs = 10;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == countdownTimer){
			countdownSecs--;
			if (countdownSecs > 0){
				countdownText.setText("Tuote veloitetaan automaattisesti " + countdownSecs + " sekunnin kuluttua");
				countdownTimer.restart();
			}
			else {
				ok.doClick();
			}
		}
		
	}
}
