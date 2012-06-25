package naamakahvi.swingui;
/*
 * Väliaikainen rajapinta, jolla Swing-käli käyttää clienttiä.
 * Luultavasti muuttuu, kun clientin ja kälin rajapinnat suunnitellaan.
 */
public interface CoffeeClient {
	//ota kuva, palauta käyttäjänimi
	public abstract String recogUser();
	//kerro tunnistetun käyttäjän suodatinkahvisaldo
	public abstract int getCoffeeSaldo(String user);
	//kerro tunnistetun käyttäjän espressosaldo
	public abstract int getEspressoSaldo(String user);
	//kirjaa järjestelmään ostos
	public abstract boolean purchaseProduct(String user, String product,
			int quantity);

}