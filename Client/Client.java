/*
  Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä mahdollisesti saattaisi tulla,
  ehdotuksia otetaan vastaan.

  t. Janne
*/

package naamakahvi; /* Sovitaanko joku paketin nimi tai jotain? */

/*
  Tämän ImageData-luokan olis tarkoitus olla se data mitä liikkuu
  clientistä serverille päin ja jonka serveri sitten tallentaa jotta se
  voi myöhemmin käyttää sitä tunnistamiseen.

  Tämän luokan toteutus riippuu aika paljon siitä mitä OpenCV:ltä päin on tulossa.
 */
class ImageData {}



public class Client 
{
    class User {
        /*
          Koska serveri "luo" uudet käyttäjät, tämän konstruktori on privaatti
          ja käyttäjiin on tarkoitus päästä käsiksi Clientin registerUser() ja authenticateUser() metodien kautta.
        */
        private User(String username, ImageData) {}
        public String getUserName() {}
    }

    /*
      Konstruktori ottaa yhteyttä serveriin, jonka jälkeen clienttiä voi käyttää.
      Tarkoitus on että jokaista osto/tuonti-tapahtumaa varten luodaan uusi Clientti.
     */
    public Client(String host, int port)
        throws JokuRandomException // jos ei saada yhteyttä jne. niin heitetään poikkeus
    {
        // TODO
    }

    /*
      Tätä on tarkoitus kutsua kun clienttiä ei enää kaivata.
     */
    public void close()
    {
        // TODO 
    }
    
    public User registerUser(String username, ImageData imagedata)
        throws UsernameTakenException etc.
    {
        // TODO
    }

    /*
      Tälle jutulle annetaan OpenCV:ltä/kameralta kuvadataa, pusketaan se serverille
      joka antaa tuloksena käyttäjän tai joukon käyttäjiä joita kuva vastaa.
     */
    public User tai joukko käyttäjiä  authenticateUser(ImageData imagedata)
    {
        // TODO 
    }

    
}