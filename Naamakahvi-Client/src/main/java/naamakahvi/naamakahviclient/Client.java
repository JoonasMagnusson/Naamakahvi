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
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.http.HttpResponse;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Client {

    private String host;
    private int port;


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
        ub.setScheme("http").setHost(this.host).setPort(this.port).setPath("/register/").setParameter("username", username).setParameter("imagedata", "asdf");
        URI uri = ub.build();
        HttpPost post = new HttpPost(uri);
        HttpResponse hr = hc.execute(post);
        int status = hr.getStatusLine().getStatusCode();
        if (200 == status) {
            byte[] buf = new byte[128];
            int r = hr.getEntity().getContent().read(buf);
            byte[] buf2 = new byte[r];
            System.arraycopy(buf, 0, buf2, 0, r);
            String name = new String(buf2);

            if (!name.equals(username)) {
                throw new RuntimeException();
            }

            return new User(name, null);
        } else {
            throw new RuntimeException();
        }
    }

    class UserExistsException extends Throwable {
    }

    class UserDoesNotExistException extends Throwable {
    }

    /*
     * Feikkikirjautuminen kälin testaukseen
     */
    public IUser authenticateText(String username) throws Exception {
        HttpClient hc = new DefaultHttpClient();
        URIBuilder ub = new URIBuilder();
        ub.setScheme("http").setHost(this.host).setPort(this.port).setPath("/authenticate_text/").setParameter("username", username);
        URI uri = ub.build();
        HttpPost post = new HttpPost(uri);
        HttpResponse hr = hc.execute(post);
        int status = hr.getStatusLine().getStatusCode();
        if (200 == status) {
            byte[] buf = new byte[128];
            int r = hr.getEntity().getContent().read(buf);
            byte[] buf2 = new byte[r];
            System.arraycopy(buf, 0, buf2, 0, r);
            String name = new String(buf2);

            if (!name.equals(username)) {
                throw new RuntimeException();
            }

            return new User(name, null);
        } else {
            throw new RuntimeException();
        }
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