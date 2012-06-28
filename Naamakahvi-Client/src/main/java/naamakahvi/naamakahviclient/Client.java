/*
 Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä mahdollisesti saattaisi tulla,
 ehdotuksia otetaan vastaan.

 Tämän kikkareen on siis tarkoitus keskustella serverin kanssa ja tarjota eri käyttöliittymille
 API jotta käyttöliittymien ei tarvitse välittää siitä miten serverin kanssa kommunikointi tapahtuu.

 t. Janne
 */
package naamakahvi.naamakahviclient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
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
    
    private JsonObject responseToJson(HttpResponse response) throws IOException {
        String s = Util.readStream(response.getEntity().getContent());
        return new JsonParser().parse(s).getAsJsonObject();
    }

    public IUser registerUser(String username, ImageData imagedata) throws RegistrationException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String user = "username=" + username;
            HttpPost post = new HttpPost(buildURI("/register/"));
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new StringEntity(user));
            HttpResponse response = httpClient.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {                
                JsonObject obj = responseToJson(response);
                

                if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                    obj.remove("status");
                    User responseUser = new Gson().fromJson(obj, User.class);
                    if (!responseUser.getUserName().equals(username)) {
                        throw new RegistrationException("username returned from server doesn't match given username");
                    }

                    return responseUser;
                } else {
                    throw new RegistrationException("Registration failed: Try another username");
                }
            } else {
                throw new RegistrationException("status code returned from server was " + status);
            }
        } catch (RegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new RegistrationException(e.getClass().toString() + ": " + e.toString());
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

    public IUser authenticateText(String username) throws AuthenticationException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String user = "username=" + username;
            HttpPost post = new HttpPost(buildURI("/authenticate_text/"));
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new StringEntity(user));
            HttpResponse response = httpClient.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                String s = Util.readStream(response.getEntity().getContent());
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                
                if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                    obj.remove("status");
                    User responseUser = new Gson().fromJson(obj, User.class);
                    if (!responseUser.getUserName().equals(username)) {
                        throw new AuthenticationException("username returned from server doesn't match given username");
                    }

                    return responseUser;
                } else {
                    throw new AuthenticationException("Authentication failed");
                }
            } else {
                throw new AuthenticationException("status code returned from server was " + status);
            }
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(e.toString());
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
            System.out.println("P");
        try {
            Client c = new Client("127.0.0.1", 5000);
            IUser user = c.registerUser("dlfsfh", new ImageData());
            System.out.println("Registered user " + user.getUserName());
        } catch (RegistrationException e) {
            System.out.println(e.getMessage());
        }
    }
}