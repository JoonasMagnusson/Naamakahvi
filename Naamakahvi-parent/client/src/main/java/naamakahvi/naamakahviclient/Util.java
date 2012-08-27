
package naamakahvi.naamakahviclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Util {
    private Util() {
    }

    /**
     * Reads an input stream to a string.
     * 
     * @param stream input stream to read 
     * @return the input stream content as a string 
     */
    public static String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String ans = new String();
        String tmp;
        while (null != (tmp = reader.readLine())) {
            ans += tmp + '\n';
        }
        return ans.trim();
    }
}
