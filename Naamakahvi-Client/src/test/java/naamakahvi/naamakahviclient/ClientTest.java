package naamakahvi.naamakahviclient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class ClientTest {

    private LocalTestServer server = null;
   // @Mock
    HttpRequestHandler register_handler = new HttpRequestHandler() {

        public void handle(HttpRequest hr, HttpResponse hr1, HttpContext hc) throws HttpException, IOException {
            String username = "asdf";
            System.out.println("username: " + username);
            StringEntity se = new StringEntity(username, ContentType.create("text/plain", "UTF-8"));                     
            hr1.setEntity(se);
            hr1.setStatusCode(200);
            
        }
    };

    static int port;
    static String host;
    
    @Before
    public void setUp() {
        server = new LocalTestServer(null, null);
        
        server.register("/register/*", register_handler);
        
        
        try {
            server.start();
            port  = server.getServiceAddress().getPort();
            host = server.getServiceAddress().getHostName();
            System.out.println("HOST: " + host + ", PORT: " + port);
        } catch (Exception ex) {
            System.out.println("Failed to start LocalTestServer");
        }       
    }

    // do lots of testing!
    @After
    public void tearDown() {
        try {
            server.stop();
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void moi() {
        Client c = new Client(host, port);
        try {
            IUser u = c.registerUser("asdf", null);
            System.out.println(u.getUserName());
            assertEquals(u.getUserName(), "asdf");
        } catch (Exception ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}