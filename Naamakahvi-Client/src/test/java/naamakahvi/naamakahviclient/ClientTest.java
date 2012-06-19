package naamakahvi.naamakahviclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class ClientTest {

    private LocalTestServer server = null;
    private static HashMap<String, IUser> users = new HashMap<String, IUser>();
    HttpRequestHandler register_handler = new HttpRequestHandler() {

        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            HttpEntity entity = null;
            if (request instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) request).getEntity();
            }

            byte[] data;
            if (entity == null) {
                data = new byte[0];
            } else {
                data = EntityUtils.toByteArray(entity);
            }

            String username = new String(data);
            StringEntity stringEntity = new StringEntity(username, ContentType.create("text/plain", "UTF-8"));
            response.setEntity(stringEntity);
            response.setStatusCode(200);
        }
    };
//    HttpRequestHandler authenticate_text_handler = new HttpRequestHandler() {
//
//        public void handle(HttpRequest request, HttpResponse response, HttpContext hc) throws HttpException, IOException {
//            DefaultedHttpParams params = (DefaultedHttpParams) request.getParams();
//            
//            Set<String> names = params.getNames();
//            for (String s : names) {
//                System.out.println(s);
//            }
//            System.out.println("params: " + request.getParams());
//            //String username = "asdf"; //  request.getParams().getParameter("username").toString();
//            
//            //System.out.println("username: " + username);
//            StringEntity se = new StringEntity("asdf", ContentType.create("text/plain", "UTF-8"));                     
//            response.setEntity(se);
//            response.setStatusCode(200);
//            
//        }
//    };
    static int port;
    static String host;

    @Before
    public void setUp() {
        server = new LocalTestServer(null, null);

        server.register("/register/*", register_handler);
        //server.register("/authenticate_text/*", authenticate_text_handler);


        try {
            server.start();
            port = server.getServiceAddress().getPort();
            host = server.getServiceAddress().getHostName();
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
    public void register_test() throws Exception {
        Client c = new Client(host, port);
        try {
            IUser u = c.registerUser("asdf", null);
            System.out.println(u.getUserName());
            assertEquals(u.getUserName(), "asdf");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Test
    public void authenticate_test() throws Exception {
        Client c = new Client(host, port);
        try {
            IUser u = c.authenticateText("asdf");
            System.out.println(u.getUserName());
            assertEquals(u.getUserName(), "asdf");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
}