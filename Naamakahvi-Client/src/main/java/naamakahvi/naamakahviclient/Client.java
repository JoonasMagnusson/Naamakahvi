package naamakahvi.naamakahviclient;

import java.util.*;
import com.google.gson.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
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

    class GeneralClientException extends ClientException {
        public GeneralClientException(String s) {
            super(s);
        }
        
    }
    
    public List<IProduct> listBuyableProducts() throws ClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(buildURI("/list_buyable_products/"));
            get.addHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse response = httpClient.execute(get);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                String s = Util.readStream(response.getEntity().getContent());
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                
                if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                    List<IProduct> ans = new ArrayList();
                    for (JsonElement e : obj.get("buyable_products").getAsJsonArray()) {
                        ans.add(new Product(e.getAsString()));
                    }
                    return ans;
                } else {
                    throw new GeneralClientException("Could not fetch list of buyable products");
                }
            } else {
                throw new GeneralClientException("status code returned from server was " + status);
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    public void buyProduct(IUser user, IProduct product, int amount) throws ClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(buildURI("/buy_product/"));
            post.setEntity(new StringEntity("product_name=" + product.getName() + "&" + "amount=" + amount + "&" + "username=" + user.getUserName()));
            HttpResponse response = httpClient.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                String s = Util.readStream(response.getEntity().getContent());
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();

                if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                    return;
                } else {
                    throw new GeneralClientException("Buying the product failed");
                }
            } else {
                throw new GeneralClientException("Status code returned from server was " + status);
            }

        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    // bringable? lolwut
    public List<IProduct> listBringableProducts() throws ClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(buildURI("/list_bringable_products/"));
            get.addHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse response = httpClient.execute(get);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                String s = Util.readStream(response.getEntity().getContent());
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                
                if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                    List<IProduct> ans = new ArrayList();
                    for (JsonElement e : obj.get("bringable_products").getAsJsonArray()) {
                        ans.add(new Product(e.getAsString()));
                    }
                    return ans;
                } else {
                    throw new GeneralClientException("Could not fetch list of bringable products");
                }
            } else {
                throw new GeneralClientException("status code returned from server was " + status);
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }


}