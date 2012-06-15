/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naamakahvi.naamakahviclient;

import java.util.List;

/*
 * Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä
 * mahdollisesti saattaisi tulla, ehdotuksia otetaan vastaan.
 *
 * Tämän kikkareen on siis tarkoitus keskustella serverin kanssa ja tarjota eri
 * käyttöliittymille API jotta käyttöliittymien ei tarvitse välittää siitä miten
 * serverin kanssa kommunikointi tapahtuu.
 *
 * t. Janne
 */

/*
 * Tämän ImageData-luokan olis tarkoitus olla se data mitä liikkuu clientistä
 * serverille päin ja jonka serveri sitten tallentaa jotta se voi myöhemmin
 * käyttää sitä tunnistamiseen.
 *
 * Tämän luokan toteutus riippuu aika paljon siitä mitä OpenCV:ltä päin on
 * tulossa.
 */
class ImageData {
}

public class Client {
    // Tämä User-luokka on Clientin-luokan "sisällä" jotta Client voi kutsua sen privaatti-konstruktoria.

    class User {
        /*
         * Koska serveri "luo" uudet käyttäjät, tämän konstruktori on privaatti
         * ja käyttäjiin on tarkoitus päästä käsiksi Clientin registerUser() ja
         * authenticateUser() metodien kautta.
         */

        private User(String username, ImageData data) {
        }

        public String getUserName() {
            // TODO
            return null;
        }

        public ImageData getImageData() {
            // TODO
            return null;
        }

        /*
         * Varsinainen osto ja tuonti tapahtuvat näillä metodeilla
         */
//        public void osta(tuote, määrä) throws Exception {
//        }
//
//        public void tuo(tuote, määrä) throws Exception {
//        }
    }

    /*
     * Konstruktori ottaa yhteyttä serveriin, jonka jälkeen clienttiä voi
     * käyttää. Tarkoitus on että jokaista osto/tuonti-tapahtumaa varten luodaan
     * uusi Clientti.
     */
    public Client(String host, int port) //throws Exception // jos ei saada yhteyttä jne. niin heitetään poikkeus
    {
        // TODO
    }

    /*
     * Tätä on tarkoitus kutsua kun clienttiä ei enää kaivata.
     */
    public void close() {
        // TODO 
    }

//    public User registerUser(String username, ImageData imagedata)
//            throws Exception {
//        // TODO
//    }

    /*
     * Tälle jutulle annetaan OpenCV:ltä/kameralta kuvadataa, pusketaan se
     * serverille joka antaa tuloksena käyttäjän tai joukon käyttäjiä joita kuva
     * vastaa.
     */
//    public List<User> authenticateUser(ImageData imagedata) {
//        // TODO 
//        return null;
//    }
}