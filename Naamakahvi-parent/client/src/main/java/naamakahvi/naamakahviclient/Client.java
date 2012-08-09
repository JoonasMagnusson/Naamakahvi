package naamakahvi.naamakahviclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
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
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * A client class that passes data from user interface to server and vice 
 * versa. Communication between client and server is done using HTTP. The client expects
 * the server response data to be in JSON format.
 *
 * @author Janne Ronkonen
 * @author Eeva Terkki
 */
public class Client {
    private String host;
    private int port;
    private String station;

    /**
     * Using the specified host name and port number, sends a HTTP request to server 
     * to get all station names.
     * Method: Get
     * Path: /list_stations/ 
     * 
     * Example server response:
     * {
     *      "status":"ok",
     *      "stations":["station1", "station2"]      
     * }
     * 
     * 
     * @param host server host name
     * @param port server port number
     * @return the list of station names
     */
    public static List<String> listStations(String host, int port) throws ClientException {
        try {
            JsonObject obj = new Client(host, port, null).doGet("/list_stations/");

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                List<String> ans = new ArrayList();
                
                for (JsonElement e : obj.get("stations").getAsJsonArray()) {
                    ans.add(e.getAsString());
                }
                
                return ans;
            } else {
                throw new GeneralClientException("Could not fetch list of stations");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    /**
     * Creates a new Client object that uses the specified host name and
     * port number and is located at the given station.
     * 
     * @param host server host name
     * @param port server port number
     * @param station coffee syndicate location
     */
    public Client(String host, int port, String station) {
        this.host = host;
        this.port = port;
        this.station = station;
    }

    private String[] jsonArrayToStringArray(JsonArray jsonArray) {
        String[] ans = new String[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            ans[i] = jsonArray.get(i).getAsString();
        }

        return ans;
    }

    private static JsonParser parser = new JsonParser();

    private JsonObject responseToJson(HttpResponse response) throws IOException {
        String s = Util.readStream(response.getEntity().getContent());
        JsonObject obj = parser.parse(s).getAsJsonObject();
        return obj;
    }

    private URI buildURI(String path) throws Exception {
        return new URI("http://" + this.host + ":" + this.port + path);
    }

    private JsonObject doPost(String path, String... params) throws Exception {
        if ((params.length % 2) != 0) {
            throw new IllegalArgumentException("Odd number of parameters");
        }

        final URI uri = buildURI(path);
        final HttpClient c = new DefaultHttpClient();
        final HttpPost post = new HttpPost(uri);
        final List<NameValuePair> plist = new ArrayList<NameValuePair>();

        for (int i = 0; i < params.length; i += 2) {
            plist.add(new BasicNameValuePair(params[i], params[i + 1]));
        }

        post.setEntity(new UrlEncodedFormEntity(plist, "UTF-8"));
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
            throw new IllegalArgumentException("Odd number of parameters");
        }

        final HttpClient c = new DefaultHttpClient();

        String suri = "http://" + this.host + ":" + this.port + path + "?";

        for (int i = 0; i < params.length; i += 2) {
            suri = suri + params[i] + "=" + params[i + 1] + "&";
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

    /**
     * Gets all usernames from the server. 
     * Method: Get
     * Path: /list_usernames/
     * 
     * Example server response:
     * {
     *      "status":"ok"
     *      "usernames":["example1","example2"]      
     * }
     * 
     * @return list of all usernames
     */
    public String[] listUsernames() throws ClientException {
        try {
            JsonObject obj = doGet("/list_usernames/");
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                JsonArray jarr = obj.get("usernames").getAsJsonArray();
                return jsonArrayToStringArray(jarr);
            } else {
                throw new GeneralClientException("asdf");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.getMessage());
        }
    }

    /**
     * Sends new user's user data to the server.
     * Method: Post
     * Path: /register/
     * Parameters: username
     *             given
     *             family
     * 
     * Example server response:
     * {
     *      "status":"ok"
     * }
     * 
     * @param username the new user's username
     * @param givenName the new user's given name
     * @param familyName the new user's family name
     * @return the newly registered user
     */
    public void registerUser(String username, String givenName,
            String familyName) throws RegistrationException {
        try {
            JsonObject obj = doPost("/register/",
                    "username", username,
                    "given", givenName,
                    "family", familyName);

            if (!obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                throw new RegistrationException("Registration failed: Try another username");
            }

        } catch (RegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new RegistrationException(e.getClass().toString() + ": " + e.toString());
        }
    }

    /**
     * Sends an image of a user to the server. 
     * Method: Post 
     * Path: /upload/ 
     * Parameters: file
     *             username
     *
     * Example server response: 
     * {
     *      "status":"ok"
     * }
     * 
     * @param username username
     * @param imagedata the image data
     */
    public void addImage(String username, byte[] imagedata) throws GeneralClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(buildURI("/upload/"));

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("file", new ByteArrayBody(imagedata, "snapshot.jpg", "image/jpeg"));
            entity.addPart("username", new StringBody(username, "text/plain", Charset.forName("UTF-8")));
            post.setEntity(entity);
            httpClient.execute(post);
        } catch (Exception ex) {
            throw new GeneralClientException(ex.toString());
        }
    }

    /**
     * Gets user data related to the specified username from the server and creates a User 
     * instance.
     * Method: Post
     * Path: /get_user/
     * Parameters: username
     * 
     * Example server response: 
     * 
     * {
     *      "status":"ok",
     *      "data": 
     *      {
     *          "username":"example",
     *          "given":"example given name",
     *          "family":"example family name",
     *          "balance": 
     *          [
     *              {
     *              "groupName":"example group"
     *              "saldo": 11.1
     *              },
     *              {
     *              "groupName":"example group 2"
     *              "saldo": 22.2
     *              }
     *          ]
     *      }
     * }
     * 
     * @param username username
     * @return the user instance
     */
    public IUser authenticateText(String username) throws AuthenticationException {
        try {
            JsonObject obj = doPost("/get_user/",
                    "username", username);

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                JsonObject data = obj.get("data").getAsJsonObject();
                String uname = data.get("username").getAsString();
                String given = data.get("given").getAsString();
                String family = data.get("family").getAsString();
                List<SaldoItem> balance = jsonToSaldoList(data.get("balance").getAsJsonArray());

                return new User(uname, given, family, balance);
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

    private List<IProduct> jsonToProductList(JsonArray ar, boolean buyable) {
        List<IProduct> ans = new ArrayList<IProduct>();
        
        for (JsonElement e : ar) {
            JsonObject product = e.getAsJsonObject();
            String productName = product.get("product_name").getAsString();
            double productPrice = product.get("product_price").getAsDouble();
            int productId = product.get("product_id").getAsInt();
            int groupId = product.get("group_id").getAsInt();
            int sizeId = -1;
            
            if (!buyable) {
                sizeId = product.get("size_id").getAsInt();
            }
            
            ans.add(new Product(productId, productName, productPrice, buyable, groupId, sizeId));

        }
        return ans;
    }

    /**
     * Gets all buyable products from the server and makes a list of IProduct objects 
     * Method: Get
     * Path: /list_buyable_products/
     * 
     * Example server response: 
     * {
     *      "status":"ok",
     *      "buyable_products":
     *      [
     *        {
     *          "product_name":"coffee",
     *          "product_price":1,
     *          "product_id":1
     *        },
     *        {
     *          "product_name":"espresso",
     *          "product_price":1,
     *          "product_id":2
     *        }
     *      ]
     * }
     * 
     * @return list of all buyable products
     */
    public List<IProduct> listBuyableProducts() throws ClientException {
        try {
            JsonObject obj = doGet("/list_buyable_products/",
                    "station_name", this.station);
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                return jsonToProductList(obj.get("buyable_products").getAsJsonArray(), true);
            } else {
                throw new GeneralClientException("Could not fetch list of buyable products");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.getMessage());
        }
    }

    /**
     * Sends information about a user's purchase to the server.
     * Method: Post
     * Path: /buy_product/
     * Parameters: product_id
     *             amount
     *             username
     * 
     * Example server response:
     * {
     *      "status":"ok"
     * }
     * 
     * @param user the buying user
     * @param product bought product
     * @param amount amount of bought product
     */
    public void buyProduct(IUser user, IProduct product, int amount) throws ClientException {
        try {
            JsonObject obj = doPost("/buy_product/",
                    "product_id", Integer.toString(product.getId()),
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

    /**
     * Gets all raw products from the server and makes a list of IProduct objects 
     * Method: Get
     * Path: /list_raw_products/
     * Example server response:
     * {
     *      "status":"ok",
     *      "raw_products":
     *      [
     *        {
     *          "product_name":"coffee beans",
     *          "product_price":1,
     *          "product_id":3
     *        },
     *        {
     *          "product_name":"filter",
     *          "product_price":1,
     *          "product_id":4
     *        }
     *      ]
     * }
     * 
     * @return list of raw product objects
     */
    public List<IProduct> listRawProducts() throws ClientException {
        try {
            JsonObject obj = doGet("/list_raw_products/",
                    "station_name", this.station);
            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                return jsonToProductList(obj.get("raw_products").getAsJsonArray(), false);
            } else {
                throw new GeneralClientException("Could not fetch list of raw products");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new GeneralClientException(e.toString());
        }
    }

    /**
     * Sends the server information about a user bringing some product as a payment.
     * Method: Post
     * Path: /bring_product/
     * Parameters: product_name
     *             station_name
     *             amount
     *             username
     * 
     * Example server response:
     * {
     *      "status":"ok"
     * }
     * 
     * @param user the user bringing the product
     * @param product the product to be brought
     * @param amount the amount of the brought product
     */
    public void bringProduct(IUser user, IProduct product, int amount) throws ClientException {
        try {
            JsonObject obj = doPost("/bring_product/",
                    "product_name", product.getName(),
                    "station_name", this.station,
                    "amount", "" + amount,
                    "username", user.getUserName());

            if (obj.get("status").getAsString().equalsIgnoreCase("ok")) {
                return;
            } else {
                throw new GeneralClientException("Bringing the product failed");
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    /**
     * Sends an image of a user to the server to get an ordered list of matching user names,
     * best match first.
     * 
     * Example server response:
     * {
     *      "status":"ok",
     *      "idlist":["user1","user2","user3","user4"]
     * }
     * 
     * @param imagedata the image
     * @return list of identified usernames 
     */
    public String[] identifyImage(byte[] imagedata) throws ClientException {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(buildURI("/identify/"));

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("file", new ByteArrayBody(imagedata, "snapshot.jpg", "image/jpeg"));
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);

            JsonObject jsonResponse = responseToJson(response);
            String status = jsonResponse.get("status").getAsString();
            if (status.equalsIgnoreCase("ok")) {

                JsonArray jarr = jsonResponse.get("idlist").getAsJsonArray();

                return jsonArrayToStringArray(jarr);
            } else {
                throw new AuthenticationException("Failed to identify user");
            }
        } catch (Exception ex) {
            throw new AuthenticationException(ex.toString());
        }
    }

    private List<SaldoItem> jsonToSaldoList(JsonArray ar) {
        List<SaldoItem> ans = new ArrayList();
        for (JsonElement e : ar) {
            JsonObject saldoitem = e.getAsJsonObject();
            String groupName = saldoitem.get("groupName").getAsString();
            int groupId = saldoitem.get("group_id").getAsInt();
            double saldo = saldoitem.get("saldo").getAsDouble();
            ans.add(new SaldoItem(groupName, groupId, saldo));
        }
        return ans;
    }

    /**
     * Gets a user's product balances from the server.
     * Method: Get
     * Path: /list_user_saldos/
     * Parameters: username
     * 
     * Example server response: 
     * 
     * {
     *      "status":"ok",
     *      "saldo_list":
     *      [
     *        {
     *          "groupName":"coffee",
     *          "saldo":-30
     *        },
     *        {
     *          "groupName":"espresso",
     *          "saldo":7
     *        }
     *      ]
     * }
     * 
     * @param user the user whose balance is wanted
     * @return list of the user's balances of different products
     * @throws ClientException 
     */
    public List<SaldoItem> listUserSaldos(IUser user) throws ClientException {
        try {
            JsonObject obj = doGet("/list_user_saldos/",
                    "username", user.getUserName());

            String status = obj.get("status").getAsString();

            if (status.equalsIgnoreCase("ok")) {
                return jsonToSaldoList(obj.get("saldo_list").getAsJsonArray());
            } else {
                throw new GeneralClientException("Failed to get user saldos: " + status);
            }
        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

    /**
     * Gets all product group names from the server.
     * Method: Get
     * Path: /list_product_groups/
     * 
     * Example server response: 
     * 
     * {
     *      "status":"ok",
     *      "product_groups":["group1","group2","group3"]
     * }
     * 
     * @return the list of the product group names
     * @throws ClientException 
     */
    public List<String> listProductGroups() throws ClientException {
        try {
            JsonObject obj = doGet("/list_product_groups/");

            String status = obj.get("status").getAsString();

            if (status.equalsIgnoreCase("ok")) {
                List<String> ans = new ArrayList<String>();
                for (JsonElement e : obj.get("product_groups").getAsJsonArray()) {
                    ans.add(e.getAsString());
                }
                return ans;
            } else {
                throw new GeneralClientException("Failed to get product groups: " + status);
            }

        } catch (Exception e) {
            throw new GeneralClientException(e.toString());
        }
    }

//    public static void main(String[] args) throws AuthenticationException, GeneralClientException, RegistrationException, ClientException {
//        Client c = new Client("naama.zerg.fi", 5001, new Station("aasd"));
//        c.listRawProducts();
//        c.listBuyableProducts();
//    }
} 
