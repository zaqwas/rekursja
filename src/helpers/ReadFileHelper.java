package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class ReadFileHelper {
    private ReadFileHelper() {};
    
    public static String readFile(InputStream stream)
    {
        try {
            byte[] bytes = new byte[0];
            byte[] buffer = new byte[4096];
            int byteRead = 0, bufferRead;
            while ( (bufferRead = stream.read(buffer)) != -1 )
            {
                if ( bufferRead==0 ) {
                    continue;
                }
                bytes = Arrays.copyOf(bytes, byteRead+bufferRead);
                System.arraycopy(buffer, 0, bytes, byteRead, bufferRead);
                byteRead += bufferRead;
            }
            return new String(bytes,"UTF-8");
        } catch (IOException ex) {
            return ex.getMessage();
        }
                
    }
}
