package naamakahvi.swingui;
/*
 * Swing-käyttöliittymän testiclient
 */
public class DummyClient implements CoffeeClient {
	private int coffeeSaldo = 0;
	private int espressoSaldo = 0;
	public String recogUser(){
		return "Protokäyttäjä";
	}
	
	public int getCoffeeSaldo(String user){
		return coffeeSaldo;
	}
	
	public int getEspressoSaldo(String user){
		return espressoSaldo;
	}
	
	public boolean purchaseProduct(String user, String product, int quantity){
		System.out.println("Veloitetaan " + quantity + " kpl tuotetta " + product + " käyttäjältä " + user);
		if ("kahvi".equals(product)) coffeeSaldo--;
		if ("espresso".equals(product)) espressoSaldo--;
		return true;
	}
}
