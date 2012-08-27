package naamakahvi.naamakahviclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClientTest {

    private LocalTestServer server = null;
    private HashMap<String, IUser> users = new HashMap<String, IUser>();
    private Client client;
    private int port;
    private String host;
    private String station;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private HttpRequestHandler registrationHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            HashMap<String, String> userData = getUserData(request);
            String username = userData.get("username");
            String status;
            if (users.containsKey(username)) {
                status = "fail";
            } else {
                status = "ok";
            }
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive(status));
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler textAuthenticationHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            HashMap<String, String> userData = getUserData(request);
            JsonObject ans = new JsonObject();

            String username = userData.get("username");

            if (users.containsKey(username)) {
                IUser user = users.get(username);
                JsonObject data = new JsonObject();
                data.add("username", new JsonPrimitive(username));
                data.add("given", new JsonPrimitive(user.getGivenName()));
                data.add("family", new JsonPrimitive(user.getFamilyName()));

                JsonArray balance = new JsonArray();
                List<SaldoItem> userBalance = user.getBalance();
                for (SaldoItem i : userBalance) {
                    JsonObject item = new JsonObject();
                    item.add("groupName", new JsonPrimitive(i.getGroupName()));
                    item.add("group_id", new JsonPrimitive(i.getGroupId()));
                    item.add("saldo", new JsonPrimitive(i.getSaldo()));
                    balance.add(item);
                }

                data.add("balance", balance);
                ans.add("status", new JsonPrimitive("ok"));
                ans.add("data", data);
            } else {
                ans.add("status", new JsonPrimitive("fail"));
            }

            stringResponse(response, ans.toString());
        }
    };

    private void stringResponse(HttpResponse r, String s) throws UnsupportedEncodingException {
        r.setEntity(new StringEntity(s, ContentType.create("text/plain", "UTF-8")));
        r.setStatusCode(200);
    }
    private HttpRequestHandler listUsernamesHandler = new HttpRequestHandler() {

        public void handle(HttpRequest hr, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();

            for (String s : new String[]{"user1", "user2", "user3", "user4"}) {
                ar.add(new JsonPrimitive(s));
            }

            ans.add("usernames", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listBuyableProductsHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();
            for (String s : new String[]{"kahvi", "espresso", "tuplaespresso", "megaespresso", "joku harvinainen tuote"}) {
                JsonObject product = new JsonObject();
                final int price = 1;
                final int id = 1;
                final int groupId = 2;
                product.add("product_name", new JsonPrimitive(s));
                product.add("product_price", new JsonPrimitive(price));
                product.add("product_id", new JsonPrimitive(id));
                product.add("group_id", new JsonPrimitive(groupId));
                ar.add(product);
            }
            ans.add("buyable_products", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listDefaultProductsHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();

            for (String s : new String[]{"kahvi", "espresso", "tuplaespresso"}) {
                JsonObject product = new JsonObject();
                final int price = 1;
                final int id = 1;
                final int groupId = 3;
                product.add("product_name", new JsonPrimitive(s));
                product.add("product_price", new JsonPrimitive(price));
                product.add("product_id", new JsonPrimitive(id));
                product.add("group_id", new JsonPrimitive(groupId));
                ar.add(product);
            }
            ans.add("default_products", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler buyProductHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listRawProductsHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();

            for (String s : new String[]{"suodatinkahvi", "espressopavut", "kahvisuodatin", "sokeri", "puhdistuspilleri"}) {
                JsonObject product = new JsonObject();
                final int price = 1;
                final int id = 1;
                final int size = 2;
                product.add("product_name", new JsonPrimitive(s));
                product.add("product_price", new JsonPrimitive(price));
                product.add("product_id", new JsonPrimitive(id));
                product.add("group_id", new JsonPrimitive(4));
                product.add("size_id", new JsonPrimitive(size));
                ar.add(product);
            }
            ans.add("raw_products", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listStationsHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();

            for (String s : new String[]{"asema1", "asema2", "asema3"}) {
                ar.add(new JsonPrimitive(s));
            }
            ans.add("stations", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler bringProductHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler identifyImageHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();

            for (String s : new String[]{"user1", "user2", "user3", "user4"}) {
                ar.add(new JsonPrimitive(s));
            }

            ans.add("idlist", ar);
            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listUserSaldosHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));

            JsonArray ar = new JsonArray();

            JsonObject s1 = new JsonObject();
            s1.add("group_name", new JsonPrimitive("kahvi"));
            s1.add("saldo", new JsonPrimitive(-30));
            ar.add(s1);

            JsonObject s2 = new JsonObject();
            s2.add("group_name", new JsonPrimitive("espresso"));
            s2.add("saldo", new JsonPrimitive(7));
            ar.add(s2);

            ans.add("saldo_list", ar);

            stringResponse(response, ans.toString());
        }
    };
    private HttpRequestHandler listGroupnamesHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));

            JsonArray ar = new JsonArray();
            ar.add(new JsonPrimitive("group1"));
            ar.add(new JsonPrimitive("group2"));
            ar.add(new JsonPrimitive("group3"));

            ans.add("product_groups", ar);

            stringResponse(response, ans.toString());
        }
    };

    private HashMap<String, String> getUserData(HttpRequest request) throws IOException {
        HttpEntity entity = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) request).getEntity();
        }

        String postBody = EntityUtils.toString(entity);
        String[] data = postBody.split("&|=");
        HashMap<String, String> userData = new HashMap<String, String>();
        for (int i = 0; i < data.length; i += 2) {
            userData.put(data[i], data[i + 1]);
        }

        return userData;
    }

    @Before
    public void setUp() {
        List<SaldoItem> balance = new ArrayList<SaldoItem>();
        balance.add(new SaldoItem("group", 2, 1.3));
        users.put("Teemu", new User("Teemu", "Teemu", "Lahti", balance));

        server = new LocalTestServer(null, null);
        server.register("/register/*", registrationHandler);
        server.register("/get_user/*", textAuthenticationHandler);
        server.register("/list_usernames/*", listUsernamesHandler);
        server.register("/list_buyable_products/*", listBuyableProductsHandler);
        server.register("/list_default_products/*", listDefaultProductsHandler);
        server.register("/buy_product/*", buyProductHandler);
        server.register("/list_raw_products/*", listRawProductsHandler);
        server.register("/list_stations/*", listStationsHandler);
        server.register("/bring_product/*", bringProductHandler);
        server.register("/identify/*", identifyImageHandler);
        server.register("/list_user_saldos/*", listUserSaldosHandler);
        server.register("/list_product_groups/*", listGroupnamesHandler);

        try {
            server.start();
            port = server.getServiceAddress().getPort();
            host = server.getServiceAddress().getHostName();
            station = Client.listStations(host, port).get(0);
            System.out.println("HOST: " + host + ", PORT: " + port);
        } catch (Exception ex) {
            System.out.println("Starting the test server failed: " + ex.getMessage());
        }

        client = new Client(host, port, station);
    }

    @After
    public void tearDown() {
        try {
            server.stop();
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void registrationWithNewNameSuccessful() throws Exception {
        try {
            client.registerUser("Pekka", "Pekka", "Virtanen");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Test
    public void authenticationWithExistingNameSuccessful() throws Exception {
        try {
            IUser u = client.getUser("Teemu");
            assertEquals(u.getUserName(), "Teemu");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Test
    public void registrationWithExistingNameFails() throws RegistrationException {
        thrown.expect(ClientException.class);
        thrown.expectMessage("fail");

        client.registerUser("Teemu", "Teemu", "Lahti");
    }

    @Test
    public void registrationWithEmptyNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser("", "Veikko", "Nieminen");
    }

    @Test
    public void registrationWithEmptyGivenNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser("Veikko", "", "Nieminen");
    }

    @Test
    public void registrationWithEmptyFamilyNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser("Veikko", "Veikko", "");
    }

    @Test
    public void registrationWithNullNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser(null, "Veikko", "Nieminen");
    }

    @Test
    public void registrationWithNullGivenNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser("Veikko", null, "Nieminen");
    }

    @Test
    public void registrationWithNullFamilyNameFails() throws RegistrationException {
        thrown.expect(RegistrationException.class);
        client.registerUser("Veikko", "Veikko", null);
    }


    @Test
    public void authenticationWithUnknownNameFails() throws AuthenticationException {
        thrown.expect(ClientException.class);
        thrown.expectMessage("fail");

        client.getUser("Matti");
    }

    @Test
    public void listUsernamesCorrectAmount() throws ClientException {
        String[] usernames = client.listUsernames();
        assert (usernames.length == 4);
    }

    @Test
    public void listUsernamesCorrectNames() throws ClientException {
        String[] usernames = client.listUsernames();
        assert (usernames[0].equals("user1")
                && usernames[1].equals("user2")
                && usernames[2].equals("user3")
                && usernames[3].equals("user4"));

    }

    @Test
    public void correctBuyableProductsListed() throws ClientException {
        List<IProduct> ps = client.listBuyableProducts();
        assertTrue(ps.get(0).getName().equals("kahvi")
                && ps.get(1).getName().equals("espresso")
                && ps.get(2).getName().equals("tuplaespresso")
                && ps.get(3).getName().equals("megaespresso")
                && ps.get(4).getName().equals("joku harvinainen tuote"));
    }

    @Test
    public void rightBuyableProductsAmount() throws ClientException {
        List<IProduct> ps = client.listBuyableProducts();

        assertTrue(ps.size() == 5);
    }

    @Test
    public void buyProduct() throws ClientException {
        IProduct p = client.listBuyableProducts().get(0);
        IUser u = client.getUser("Teemu");
        final int amount = 3;
        client.buyProduct(u, p, 3);
        System.out.println("Bought " + amount + " " + p.getName() + "(s)");
    }

    @Test
    public void correctRawProductsListed() throws ClientException {
        List<IProduct> ps = client.listRawProducts();

        assertTrue(ps.get(0).getName().equals("suodatinkahvi")
                && ps.get(1).getName().equals("espressopavut")
                && ps.get(2).getName().equals("kahvisuodatin")
                && ps.get(3).getName().equals("sokeri")
                && ps.get(4).getName().equals("puhdistuspilleri"));
    }

    @Test
    public void rightRawProductsAmount() throws ClientException {
        List<IProduct> ps = client.listRawProducts();

        assertTrue(ps.size() == 5);
    }

    @Test
    public void bringProduct() throws ClientException {
        IProduct p = client.listBuyableProducts().get(0);
        IUser u = client.getUser("Teemu");
        final int amount = 3;
        client.bringProduct(u, p, 3);
        System.out.println("Brought " + amount + " " + p.getName() + "(s)");
    }

    @Test
    public void correctStationsListed() throws ClientException {
        List<String> ss = Client.listStations(host, port);

        assertTrue(ss.get(0).equals("asema1")
                && ss.get(1).equals("asema2")
                && ss.get(2).equals("asema3"));
    }

    @Test
    public void rightStationsAmount() throws ClientException {
        List<String> ss = Client.listStations(host, port);

        assertTrue(ss.size() == 3);
    }

    @Test
    public void imageAuthenticationListsCorrectUsers() throws ClientException {
        byte[] bytes = new byte[2];
        String[] usernames = client.identifyImage(bytes);

        assertTrue(usernames[0].equals("user1")
                && usernames[1].equals("user2")
                && usernames[2].equals("user3")
                && usernames[3].equals("user4"));
    }

    @Test
    public void saldoTest() throws ClientException {
        IUser u = client.getUser("Teemu");
        List<SaldoItem> balance = u.getBalance();
        assertTrue(balance.size() == 1);

    }

    @Test
    public void listProductGroupsCorrectAmount() throws ClientException {
        Client c = new Client(host, port, station);
        List<String> groupnames = c.listProductGroups();
        assertTrue(groupnames.size() == 3);
    }

    @Test
    public void listProductGroupsCorrectGroupnames() throws ClientException {
        Client c = new Client(host, port, station);
        List<String> groupnames = c.listProductGroups();
        assertTrue(groupnames.get(0).equals("group1")
                && groupnames.get(1).equals("group2")
                && groupnames.get(1).equals("group2"));
    }
}
