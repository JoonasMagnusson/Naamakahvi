package naamakahvi.swingui;
import javax.swing.JButton;
/*
 * Apuluokka, jonka avulla voidaan tunnistaa helposti, mitä nappia on klikattu
 */
public class IDButton extends JButton{
	private int buttonID;
	
	public IDButton(int id, String text){
		super(text);
		buttonID = id;
	}
	public int getID(){
		return buttonID;
	}
}