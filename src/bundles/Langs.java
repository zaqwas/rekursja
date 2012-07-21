package bundles;

import java.util.Locale;
import java.util.ResourceBundle;

public class Langs {
    private static ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundles/Bundle" );
    public static String getStr(String str) {
        return bundle.getString(str);
    }
    
    private static ResourceBundle parseError = 
            java.util.ResourceBundle.getBundle("bundles/ParseError" );
    public static String getParseError(int code) {
        return parseError.getString( "err" + Integer.toString(code) );
    }
}
