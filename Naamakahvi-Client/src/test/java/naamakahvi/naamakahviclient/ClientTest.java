package naamakahvi.naamakahviclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ClientTest extends TestCase {

    private LocalTestServer server = null;
   // @Mock
    HttpRequestHandler handler;

    @Before
    public void setUp() {
        server = new LocalTestServer(null, null);
        server.register("/someUrl/*", handler);
        try {
            server.start();
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
        
    }
}