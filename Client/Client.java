/*
  Tämä on vasta todella karkea luonnos siitä millainen clientistä ehkä mahdollisesti saattaisi tulla,
  ehdotuksia otetaan vastaan.

  Tämän kikkareen on siis tarkoitus keskustella serverin kanssa ja tarjota eri käyttöliittymille
  API jotta käyttöliittymien ei tarvitse välittää siitä miten serverin kanssa kommunikointi tapahtuu.

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
    // Tämä User-luokka on Clientin-luokan "sisällä" jotta Client voi kutsua sen privaatti-konstruktoria.
    class User {
        /*
          Koska serveri "luo" uudet käyttäjät, tämän konstruktori on privaatti
          ja käyttäjiin on tarkoitus päästä käsiksi Clientin registerUser() ja authenticateUser() metodien kautta.
        */
        private User(String username, ImageData) {}

        public String getUserName()
        {
            // TODO
        }

        public ImageData getImageData()
        {
            // TODO
        }

        /* Varsinainen osto ja tuonti tapahtuvat näillä metodeilla */
        public void osta(tuote, määrä) throws JokuException
        {
            
        }

        public void tuo(tuote, määrä) throws JokuException
        {
            
        }
        
        
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