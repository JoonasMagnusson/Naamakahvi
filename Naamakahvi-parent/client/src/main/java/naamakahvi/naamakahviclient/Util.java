/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naamakahvi.naamakahviclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author jronkone
 */
public class Util {
    public static String readStream(InputStream is) throws IOException {        
 
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String ans = new String();
            String tmp;
            while (null != (tmp = reader.readLine())) {               
                ans += tmp + '\n';
            }
            return ans.trim();        
        
    }
}
