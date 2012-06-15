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
import org.apache.http.HttpResponse;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Client {
    /*
     * Tämän ImageData-luokan olis tarkoitus olla se data mitä liikkuu
     * clientistä serverille päin ja jonka serveri sitten tallentaa jotta se voi
     * myöhemmin käyttää sitä tunnistamiseen.
     *
     * Tämän luokan toteutus riippuu aika paljon siitä mitä OpenCV:ltä päin on
     * tulossa.
     */

 

    // Tämä User-luokka on Clientin-luokan "sisällä" jotta Client voi kutsua sen privaatti-konstruktoria.
    class User {
        /*
         * Koska serveri "luo" uudet käyttäjät, tämän konstruktori on privaatti
         * ja käyttäjiin on tarkoitus päästä käsiksi Clientin registerUser() ja
         * authenticateUser() metodien kautta.
         */

        private User(String username, ImageData imagedata) {
        }

        public String getUserName() {
            // TODO
            throw new RuntimeException();
        }

        /*
         * // Varsinainen osto ja tuonti tapahtuvat näillä metodeilla public
         * void osta(tuote, määrä) throws JokuException {
         *
         * }
         *
         * public void tuo(tuote, määrä) throws JokuException {
         *
         * }
         */
    }
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

    public User registerUser(String username, ImageData imagedata) throws Exception {
        HttpClient hc = new DefaultHttpClient();
        URIBuilder ub = new URIBuilder();
        ub.setScheme("http").setHost(this.host).setPath("/register").setParameter("username", username).setParameter("imagedata", imagedata.toString());
        URI uri = ub.build();
        HttpGet get = new HttpGet(uri);
        HttpResponse hr = hc.execute(get);
        System.out.println(hr);
        return null;
    }

    /*
     * Tälle jutulle annetaan OpenCV:ltä/kameralta kuvadataa, pusketaan se
     * serverille joka antaa tuloksena käyttäjän tai joukon käyttäjiä joita kuva
     * vastaa.
     */
    public List<User> authenticateImage(ImageData imagedata) {
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