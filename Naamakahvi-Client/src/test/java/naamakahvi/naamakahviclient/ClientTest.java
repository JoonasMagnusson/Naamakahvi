package naamakahvi.naamakahviclient;

import java.util.*;
import com.google.gson.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClientTest {

    private LocalTestServer server = null;
    private HashMap<String, IUser> users = new HashMap<String, IUser>();
    private int port;
    private String host;
    private IStation station;

    

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private class ResponseUser extends User {

        private final String status;

        private ResponseUser(String uname, ImageData id, String success) {
            super(uname, id);
            this.status = success;
        }
    }
    private HttpRequestHandler registrationHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            HttpEntity entity = null;
            if (request instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) request).getEntity();
            }

            String username = readUsernameFromEntity(entity);
            IUser user;

            if (users.containsKey(username)) {
                user = new ResponseUser(username, null, "fail");
            } else {
                user = new ResponseUser(username, null, "ok");
            }

            makeResponse(user, response);
        }
    };
    private HttpRequestHandler textAuthenticationHandler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            HttpEntity entity = null;
            if (request instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) request).getEntity();
            }

            String username = readUsernameFromEntity(entity);
            IUser user;

            if (users.containsKey(username)) {
                user = new ResponseUser(username, null, "ok");
            } else {
                user = new ResponseUser(username, null, "fail");
            }

            makeResponse(user, response);
        }
    };

    private static void stringResponse(HttpResponse r, String s) throws UnsupportedEncodingException {
        r.setEntity(new StringEntity(s, ContentType.create("text/plain", "UTF-8")));
        r.setStatusCode(200);
    }

    private HttpRequestHandler listBuyableProductsHandler = new HttpRequestHandler() {
    
        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();
            
            for (String s : new String[]{"kahvi", "espresso", "tuplaespresso"}) {
                ar.add(new JsonPrimitive(s));
            }
            ans.add("buyable_products", ar);
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

    private HttpRequestHandler listBringableProductsHandler = new HttpRequestHandler() {
    
        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
            JsonObject ans = new JsonObject();
            ans.add("status", new JsonPrimitive("ok"));
            JsonArray ar = new JsonArray();
            
            for (String s : new String[]{"suodatinkahvi", "espressopavut", "kahvisuodatin", "sokeri", "puhdistuspilleri"}) {
                ar.add(new JsonPrimitive(s));
            }
            ans.add("bringable_products", ar);
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
            


    private void makeResponse(IUser user, HttpResponse response) throws IllegalStateException, UnsupportedCharsetException {
        StringEntity stringEntity = new StringEntity(new Gson().toJson(user, ResponseUser.class), ContentType.create("text/plain", "UTF-8"));
        response.setEntity(stringEntity);
        response.setStatusCode(200);
    }

    private String readUsernameFromEntity(HttpEntity entity) throws IOException {
        byte[] data;
        if (entity == null) {
            data = new byte[0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }
        return new String(data).substring(9);
    }

    @Before
    public void setUp() {
        users.put("Teemu", new User("Teemu", null)); 
        
        server = new LocalTestServer(null, null);
        server.register("/register/*", registrationHandler);
        server.register("/authenticate_text/*", textAuthenticationHandler);
        server.register("/list_buyable_products/*", listBuyableProductsHandler);
        server.register("/buy_product/*", buyProductHandler);
        server.register("/list_bringable_products/*", listBringableProductsHandler);
        server.register("/list_stations/*", listStationsHandler);
        server.register("/bring_product/*", bringProductHandler);

        try {
            server.start();
            port = server.getServiceAddress().getPort();
            host = server.getServiceAddress().getHostName();
            station = Client.listStations(host, port).get(0);
            System.out.println("HOST: " + host + ", PORT: " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        Client c = new Client(host, port, station);
        try {
            IUser u = c.registerUser("Pekka", null);
            assertEquals(u.getUserName(), "Pekka");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Test
    public void authenticationWithExistingNameSuccessful() throws Exception {
        Client c = new Client(host, port, station);
        try {
            IUser u = c.authenticateText("Teemu");
            assertEquals(u.getUserName(), "Teemu");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Test
    public void registrationWithExistingNameFails() throws RegistrationException {
        thrown.expect(ClientException.class);
        thrown.expectMessage("Registration failed: Try another username");
        
        Client c = new Client(host, port, station);
        IUser u = c.registerUser("Teemu", null);
    }

    @Test
    public void authenticationWithUnknownNameFails() throws AuthenticationException {
        thrown.expect(ClientException.class);
        thrown.expectMessage("Authentication failed");
        
        Client c = new Client(host, port, station);
        IUser u = c.authenticateText("Matti");
    }

    @Test
    public void listBuyableProducts() throws ClientException {
        Client c = new Client(host, port, station);
        List<IProduct> ps = c.listBuyableProducts();

        System.out.println("Buyable products are:");

        for (IProduct p : ps) {
            System.out.println(p.getName());
        }

    }

    @Test
    public void buyProduct() throws ClientException {
        Client c = new Client(host, port, station);
        IProduct p = c.listBuyableProducts().get(0);
        IUser u = c.authenticateText("Teemu");
        final int amount = 3;
        c.buyProduct(u, p, 3);
        System.out.println("Bought " + amount + " " + p.getName() + "(s)");
    }

    @Test
    public void listBringableProducts() throws ClientException {
        Client c = new Client(host, port, station);
        List<IProduct> ps = c.listBringableProducts();

        System.out.println("Bringable products are:");

        for (IProduct p : ps) {
            System.out.println(p.getName());
        }

    }

    @Test
    public void bringProduct() throws ClientException {
        Client c = new Client(host, port, station);
        IProduct p = c.listBuyableProducts().get(0);
        IUser u = c.authenticateText("Teemu");
        final int amount = 3;
        c.bringProduct(u, p, 3);
        System.out.println("Bringed " + amount + " " + p.getName() + "(s)");
    }

    @Test
    public void listStations() throws ClientException {
        List<IStation> ss = Client.listStations(host, port);

        System.out.println("Available stations are:");

        for (IStation s : ss) {
            System.out.println(s.getName());
        }

    }


}