package naamakahvi.naamakahviclient;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class Client {
    private String host;
    private int port;
    private IStation station;

    private static class Station implements IStation {
        private String name;

        Station(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static List<IStation> listStations(String host, int port) throws ClientException {
        try {
            JsonObject obj = new Client(host, port, null).doGet("/list_stations/");

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                List<IStation> ans = new ArrayList();
                for (JsonElement e : obj.get("stations").getAsJsonArray()) {
                    ans.add(new Station(e.getAsString()));
                }
                return ans;
            } else {
                throw new GeneralClientException("Could not fetch list of stations");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    /*
     * Konstruktori ainoastaan tallentaa hostin nimen ja portin, joita se
     * käyttää myöhemmissä metodikutsuissaan, jotka muodostavat aina uuden
     * HTTP-yhteyden.
     */
    public Client(String host, int port, IStation station) {
        this.host = host;
        this.port = port;
        this.station = station;
    }

    private static JsonObject responseToJson(HttpResponse response) throws IOException {
        String s = Util.readStream(response.getEntity().getContent());
        return new JsonParser().parse(s).getAsJsonObject();
    }

    private URI buildURI(String path) throws Exception {
        return new URI("http://" + this.host + ":" + this.port + path);
    }

    private JsonObject doPost(String path, String... params) throws Exception {
        if (0 != (params.length % 2)) {
            throw new RuntimeException("Odd number of parameters");
        }

        final URI uri = buildURI(path);
        final HttpClient c = new DefaultHttpClient();
        final HttpPost post = new HttpPost(uri);
        final List<NameValuePair> plist = new ArrayList<NameValuePair>();

        for (int i = 0; i < params.length; i += 2) {
            plist.add(new BasicNameValuePair(params[i], params[i + 1]));
        }

        post.setEntity(new UrlEncodedFormEntity(plist, HTTP.UTF_8));
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");

        final HttpResponse resp = c.execute(post);
        final int status = resp.getStatusLine().getStatusCode();

        if (status == 200) {
            return responseToJson(resp);
        } else {
            throw new GeneralClientException("Server returned HTTP-status code " + status);
        }
    }

    private JsonObject doGet(String path, String... params) throws Exception {
        if ((params.length % 2) != 0) {
            throw new RuntimeException("Odd number of parameters");
        }

        final HttpClient c = new DefaultHttpClient();

        String suri = "http://" + this.host + ":" + this.port + path + "?";

        for (int i = 0; i < params.length; i += 2) {
            suri = suri + params[i] + "=" + params[i+1] + "&";
        }

        final URI uri = new URI(suri);
        final HttpGet get = new HttpGet(uri);

        get.addHeader("Content-Type", "application/x-www-form-urlencoded");

        final HttpResponse resp = c.execute(get);
        final int status = resp.getStatusLine().getStatusCode();

        if (status == 200) {
            return responseToJson(resp);
        } else {
            throw new GeneralClientException("Server returned HTTP-status code " + status);
        }
    }
    
    public String[] listUsernames() throws ClientException {
        try {
            JsonObject obj = doGet("/list_usernames/");
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                JsonArray jarr = obj.get("usernames").getAsJsonArray();
                String[] ans = new String[jarr.size()];
                
                for (int i = 0; i < jarr.size(); i++) {
                    ans[i] = jarr.get(i).getAsString();
                }
                
                return ans;
            } else {
                throw new GeneralClientException("asdf");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
        
    }

    public IUser registerUser(String username, String givenName, String familyName, ImageData imagedata) throws RegistrationException {
        try {
            JsonObject obj = doPost("/register/",
                    "username", username,
                    "given", givenName,
                    "family", familyName);

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
        } catch (RegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new RegistrationException(e.getClass().toString() + ": " + e.toString());
        }
    }

    public IUser authenticateText(String username) throws AuthenticationException {
        try {
            JsonObject obj = doPost("/authenticate_text/",
                    "username", username);

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
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(e.toString());
        }
    }

    static class GeneralClientException extends ClientException {
        public GeneralClientException(String s) {
            super(s);
        }
    }

    public List<IProduct> listBuyableProducts() throws ClientException {
        try {
            JsonObject obj = doGet("/list_buyable_products/",
                    "station_name", this.station.getName());
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                List<IProduct> ans = new ArrayList();
                for (JsonElement e : obj.get("buyable_products").getAsJsonArray()) {
                    ans.add(new Product(e.getAsString()));
                }
                return ans;
            } else {
                throw new GeneralClientException("Could not fetch list of buyable products");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    public List<IProduct> listDefaultProducts() throws ClientException {
        try {
            JsonObject obj = doGet("/list_default_products/",
                                   "station_name", this.station.getName());
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                List<IProduct> ans = new ArrayList();
                for (JsonElement e : obj.get("default_products").getAsJsonArray()) {
                    ans.add(new Product(e.getAsString()));
                }
                return ans;
            } else {
                throw new GeneralClientException("Could not fetch list of defalt products");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    public void buyProduct(IUser user, IProduct product, int amount) throws ClientException {
        try {
            JsonObject obj = doPost("/buy_product/",
                    "product_name", product.getName(),
                    "station_name", this.station.getName(),
                    "amount", "" + amount,
                    "username", user.getUserName());

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                return;
            } else {
                throw new GeneralClientException("Buying the product failed");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    public List<IProduct> listRawProducts() throws ClientException {
        try {
            JsonObject obj = doGet("/list_raw_products/",
                    "station_name", this.station.getName());
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                List<IProduct> ans = new ArrayList();
                for (JsonElement e : obj.get("raw_products").getAsJsonArray()) {
                    ans.add(new Product(e.getAsString()));
                }
                return ans;
            } else {
                throw new GeneralClientException("Could not fetch list of raw products");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    public void bringProduct(IUser user, IProduct product, int amount) throws ClientException {
        try {
            JsonObject obj = doPost("/bring_product/",
                    "product_name", product.getName(),
                    "station_name", this.station.getName(),
                    "amount", "" + amount,
                    "username=", user.getUserName());

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                return;
            } else {
                throw new GeneralClientException("Bringing the product failed");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    /*
     * Tälle jutulle annetaan OpenCV:ltä/kameralta kuvadataa, pusketaan se
     * serverille joka antaa tuloksena joukon käyttäjiä joita kuva
     * vastaa.
     */
    public List<IUser> authenticateImage(File file) throws AuthenticationException {
        try {
            HttpResponse response = uploadImage(file);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                Type listType = new TypeToken<ArrayList<IUser>>() {
                }.getType();
                List<IUser> userList = new Gson().fromJson(responseToJson(response), listType);
                return userList;
            } else {
                throw new AuthenticationException("Authentication failed, server status code: " + status);
            }
        } catch (Exception ex) {
            throw new AuthenticationException(ex.toString());
        }
    }

    public HttpResponse uploadImage(File file) throws GeneralClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(buildURI("/upload/"));

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("file", new FileBody(file,"application/octect-stream"));
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            return response;
        } catch (Exception ex) {
            throw new GeneralClientException(ex.toString());
        }
    }

    public static void main(String[] args) throws AuthenticationException, GeneralClientException {
        Client c = new Client("127.0.0.1", 5000, null);
        HttpResponse response = c.uploadImage(new File("lol.png"));
        System.out.println(response.getStatusLine().getStatusCode());
    }
}