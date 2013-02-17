//package stringcreator;
//
//import java.awt.FontMetrics;
//import java.math.BigInteger;
//import java.util.ArrayList;
//
//public class StringCreatorBuilder extends StringCreator {
//    
//    private ArrayList array = new ArrayList();
//    
//    private static class StringToExtend {
//        public final String string;
//        public final int initialLength;
//        public final boolean ellipsis;
//        public final int extendedSize;
//        
//        public StringToExtend(String str, int initialLength, 
//                boolean ellipsis, int extendSize) {
//
//            this.string = str;
//            this.initialLength = initialLength;
//            this.ellipsis = ellipsis;
//            this.extendedSize = extendSize;
//        }
//    }
//    private static class BigIntegerToExtend {
//        public final BigInteger bigInteger;
//        public final int initialDigitsAfterComa;
//        public final int extendedSize;
//        
//        public BigIntegerToExtend(BigInteger bigInteger,
//                int initialDigitsAfterComa, int extendSize) {
//
//            this.bigInteger = bigInteger;
//            this.initialDigitsAfterComa = initialDigitsAfterComa;
//            this.extendedSize = extendSize;
//        }
//    }
//
//    public StringCreatorBuilder addString(String str) {
//        assert str != null;
//        array.add(str);
//        return this;
//    }
//    public StringCreatorBuilder addStringToExtend(String str, 
//            int initialLength, boolean ellipsis, int extendSize) {
//        assert str != null && initialLength >= 0 && extendSize > 0; 
//        
//        array.add(str);
//        return this;
//    }
//    public StringCreatorBuilder addBigIntegerToExtend(BigInteger bigInt, 
//            int initialDigitsAfterComa, int extendSize) {
//        assert bigInt != null && initialDigitsAfterComa >= 0 && extendSize > 0;
//        
//        return this;
//    }
//    
//    @Override
//    public void setFontMetrics(FontMetrics fontMetrics) {
//        if (this.fontMetrics != null) {
//            return;
//        }
//        super.setFontMetrics(fontMetrics);
//        strWidth = fontMetrics.stringWidth(string);
//        dotsWidth = fontMetrics.charWidth('…');
//    }
//    
//    @Override
//    public String getString(int maxWidth) {
//        assert fontMetrics != null : "FontMetrics not initialized.";
//
//        if (strWidth <= maxWidth) {
//            return string;
//        }
//        if (maxWidth < dotsWidth) {
//            return "";
//        }
//        int length = 0, width = dotsWidth;
//        while (true) {
//            char c = string.charAt(length);
//            int newWidth = width + fontMetrics.charWidth(c);
//            if (newWidth > maxWidth) {
//                break;
//            }
//            width = newWidth;
//            length++;
//        }
//        return string.substring(0, length) + '…';
//    }
//    
//}
