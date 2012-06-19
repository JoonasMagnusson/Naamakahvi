/*
 Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä mahdollisesti saattaisi tulla,
 ehdotuksia otetaan vastaan.

 Tämän kikkareen on siis tarkoitus keskustella serverin kanssa ja tarjota eri käyttöliittymille
 API jotta käyttöliittymien ei tarvitse välittää siitä miten serverin kanssa kommunikointi tapahtuu.

 t. Janne
 */
package naamakahvi.naamakahviclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
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
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post = new HttpPost(buildURI("/register/"));

        post.setEntity(new StringEntity(username));

        HttpResponse response = httpClient.execute(post);
        int status = response.getStatusLine().getStatusCode();

        if (200 == status) {
            String name = readResponseContent(response);

            System.out.println("name: " + name + ", username: " + username);
            
            if (!name.equals(username)) {
                throw new RuntimeException();
            }

            return new User(name, null);
        } else {
            throw new RuntimeException();
        }
    }
    
    private URI buildURI(String path) {
        URIBuilder ub = new URIBuilder();
        ub.setScheme("http").setHost(this.host).setPort(this.port).setPath(path);
        try {
            URI uri = ub.build();

            return uri;
        } catch (URISyntaxException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private String readResponseContent(HttpResponse response) {        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String name = reader.readLine();
            return name;
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;

    }

    class UserExistsException extends Throwable {
    }

    class UserDoesNotExistException extends Throwable {
    }

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