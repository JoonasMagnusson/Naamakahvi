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
        return jsonArrayToStringList(new Client(host, port, null).doGet("/list_stations/").get("stations").getAsJsonArray());
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

    private void checkStatusCode(final HttpResponse resp) throws GeneralClientException {
        final int status = resp.getStatusLine().getStatusCode();
        
        if (status != 200) {
            throw new GeneralClientException("Server returned HTTP-status code " + status);
        }
    }
    
    private void checkResponseStatus(JsonObject obj) throws GeneralClientException {
        String status = obj.get("status").getAsString();
        
        if (!status.equalsIgnoreCase("ok")) {
            throw new GeneralClientException(status);
        }
    }    

    private String[] jsonArrayToStringArray(JsonArray jsonArray) {
        String[] ans = new String[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            ans[i] = jsonArray.get(i).getAsString();
        }

        return ans;
    }

    private static List<String> jsonArrayToStringList(JsonArray jsonArray) {
        ArrayList<String> ans = new ArrayList(jsonArray.size());

        for (JsonElement e : jsonArray) {
            ans.add(e.getAsString());
        }

        return ans;
    }

    private static JsonParser parser = new JsonParser();
    
    private JsonObject responseToJson(HttpResponse response) throws IOException, GeneralClientException {
        String s = Util.readStream(response.getEntity().getContent());
        JsonObject obj = parser.parse(s).getAsJsonObject();
        checkResponseStatus(obj);
        return obj;
    }

    private URI buildURI(String path) throws Exception {
        return new URI("http://" + this.host + ":" + this.port + path);
    }

    private JsonObject doPost(String path, String... params) throws ClientException {
        try{
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
            checkStatusCode(resp);
            
            return responseToJson(resp);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralClientException("non-ClientException of class " + e.getClass() + " caught: " + e.getMessage());
        }
    }

    private JsonObject doGet(String path, String... params) throws ClientException {
        try {
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
            checkStatusCode(resp);
            
            return responseToJson(resp);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralClientException("non-ClientException of class " + e.getClass() + " caught: " + e.getMessage());
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
        return jsonArrayToStringArray(doGet("/list_usernames/").get("usernames").getAsJsonArray());
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

        if (username.equals("")) {
            throw new RegistrationException("Username must not be empty.");
        }

        try {
            JsonObject obj = doPost("/register/",
                    "username", username,
                    "given", givenName,
                    "family", familyName);
        } catch (Exception e) {
            throw new RegistrationException(e.getClass().toString() + ": " + e.getMessage());
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
            
            HttpResponse response = httpClient.execute(post);
            responseToJson(response);
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
     *              "groupName":"example group",
     *              "group_id":1,
     *              "saldo": 11.1
     *              },
     *              {
     *              "groupName":"example group 2",
     *              "group_id":2,
     *              "saldo": 22.2
     *              }
     *          ]
     *      }
     * }
     * 
     * @param username username
     * @return the user instance
     */
    public IUser getUser(String username) throws AuthenticationException {
        try {
            JsonObject obj = doPost("/get_user/",
                    "username", username);

            JsonObject data = obj.get("data").getAsJsonObject();
            String uname = data.get("username").getAsString();
            String given = data.get("given").getAsString();
            String family = data.get("family").getAsString();
            List<SaldoItem> balance = jsonToSaldoList(data.get("balance").getAsJsonArray());
            
            return new User(uname, given, family, balance);
        } catch (ClientException e) {
            throw new AuthenticationException("Authentication failed");
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
     *          "product_id":1,
     *          "group_id":2
     *        },
     *        {
     *          "product_name":"espresso",
     *          "product_price":1,
     *          "product_id":2,
     *          "group_id":1
     *        }
     *      ]
     * }
     * 
     * @return list of all buyable products
     */
    public List<IProduct> listBuyableProducts() throws ClientException {
        JsonObject obj = doGet("/list_buyable_products/",
                               "station_name", this.station);
        return jsonToProductList(obj.get("buyable_products").getAsJsonArray(), true);
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
        doPost("/buy_product/",
               "product_id", Integer.toString(product.getId()),
               "amount", "" + amount,
               "station_name", this.station,
               "username", user.getUserName());

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
     *          "product_id":3,
     *          "group_id":2
     *        },
     *        {
     *          "product_name":"filter",
     *          "product_price":1,
     *          "product_id":4,
     *          "group_id":1
     *        }
     *      ]
     * }
     * 
     * @return list of raw product objects
     */
    public List<IProduct> listRawProducts() throws ClientException {
        return jsonToProductList(doGet("/list_raw_products/", "station_name", this.station).get("raw_products").getAsJsonArray(), false);
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
    	Product actual = (Product) product;
        doPost("/bring_product/",
               "product_name", actual.getName(),
               "station_name", this.station,
               "amount", "" + amount,
               "username", user.getUserName(),
               "size_id",Integer.toString(actual.getSizeId()));
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
            checkResponseStatus(jsonResponse);
            
            JsonArray jarr = jsonResponse.get("idlist").getAsJsonArray();
            return jsonArrayToStringArray(jarr);
            
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
        return jsonArrayToStringList(doGet("/list_product_groups/").get("product_groups").getAsJsonArray());
    }

//    public static void main(String[] args) throws AuthenticationException, GeneralClientException, RegistrationException, ClientException {
//        Client c = new Client("naama.zerg.fi", 5001, new Station("aasd"));
//        c.listRawProducts();
//        c.listBuyableProducts();
//    }
} 
