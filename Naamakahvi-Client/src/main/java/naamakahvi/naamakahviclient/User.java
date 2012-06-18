package naamakahvi.naamakahviclient;

public class User {
    /*
     * Koska serveri "luo" uudet käyttäjät, tämän konstruktori on privaatti ja
     * käyttäjiin on tarkoitus päästä käsiksi Clientin registerUser() ja
     * authenticateUser() metodien kautta.
     */

    User(String username, ImageData imagedata) {
    }

    public String getUserName() {
        // TODO
        throw new RuntimeException();
    }

    /*
     * // Varsinainen osto ja tuonti tapahtuvat näillä metodeilla 
     * public void osta(tuote, määrä) throws JokuException {
     *
     * }
     *
     * public void tuo(tuote, määrä) throws JokuException {
     *
     * }
     */
}