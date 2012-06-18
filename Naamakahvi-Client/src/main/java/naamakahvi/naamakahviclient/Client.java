/*
 Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä mahdollisesti saattaisi tulla,
 ehdotuksia otetaan vastaan.

 Tämän kikkareen on siis tarkoitus keskustella serverin kanssa ja tarjota eri käyttöliittymille
 API jotta käyttöliittymien ei tarvitse välittää siitä miten serverin kanssa kommunikointi tapahtuu.

 t. Janne
 */
package naamakahvi.naamakahviclient;

import java.util.List;
import java.net.URI;
import java.util.HashMap;
import org.apache.http.HttpResponse;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Client {

    private String host;
    private int port;
    private static HashMap<String, IUser> users;

    /*
     * Konstruktori ainoastaan tallentaa hostin nimen ja portin, joita se
     * käyttää myöhemmissä metodikutsuissaan, jotka muodostavat aina uuden
     * HTTP-yhteyden.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public IUser registerUser(String username, ImageData imagedata) throws Exception {
        HttpClient hc = new DefaultHttpClient();
        URIBuilder ub = new URIBuilder();
        ub.setScheme("http").setHost(this.host).setPort(this.port).setPath("/register").setParameter("username", username).setParameter("imagedata", imagedata.toString());
        URI uri = ub.build();
        HttpGet get = new HttpGet(uri);
        HttpResponse hr = hc.execute(get);
        byte[] buf = new byte[128];
        hr.getEntity().getContent().read(buf);
        String name = new String(buf);
        
        if (!name.equals(username)) {
            throw new RuntimeException();
        }
        
        return new User(name, null);
    }

    class UserExistsException extends Throwable {
    }

    class UserDoesNotExistException extends Throwable {
    }

    /*
     * Feikkirekisteröityminen kälin testaukseen
     */
    public IUser registerUser(String username) throws UserExistsException {
        if (null != users.get(username)) {
            throw new UserExistsException();
        }

        IUser u = new User(username, null);
        users.put(username, u);
        return u;
    }

    /*
     * Feikkikirjautuminen kälin testaukseen
     */
    public IUser authenticateText(String username) throws UserDoesNotExistException {
        if (null == users.get(username)) {
            throw new UserDoesNotExistException();
        }

        return users.get(username);
    }

    /*
     * Tälle jutulle annetaan OpenCV:ltä/kameralta kuvadataa, pusketaan se
     * serverille joka antaa tuloksena käyttäjän tai joukon käyttäjiä joita kuva
     * vastaa.
     */
    public List<IUser> authenticateImage(ImageData imagedata) {
        // TODO 
        throw new RuntimeException();
    }

    public static void main(String[] args) {
        try {
            Client c = new Client("le-host", 1234);
            c.registerUser("veikko nieminen", new ImageData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}