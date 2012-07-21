package stringcreator;

import java.awt.FontMetrics;

public class FastStringCreator extends StringCreator {
    
    private String string;
    private int strWidth = Integer.MAX_VALUE;
    private int dotsWidth;
    
    public FastStringCreator(String string)
    {
        this.string = string;
    }

    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        assert this.fontMetrics==null : "FontMetrics not initialized.";
        super.setFontMetrics(fontMetrics);
        strWidth = fontMetrics.stringWidth(string);
        dotsWidth = fontMetrics.charWidth('…');
    }
    
    @Override
    public String getString(int maxWidth) {
        assert fontMetrics!=null : "FontMetrics not initialized.";
        if ( strWidth <= maxWidth ) {
            return string;
        }
        int length = 0, width = 0;
        while ( true ) {
            char c = string.charAt(length);
            int newWidth = width + fontMetrics.charWidth(c);
            if ( newWidth > maxWidth ) {
                break;
            }
            width = newWidth;
            length++;
        }
        while (length>0) {
            if ( dotsWidth + width <= maxWidth ) {
                break;
            }
            char c = string.charAt(--length);
            width -= fontMetrics.charWidth(c);
        }
        return string.substring(0, length) + '…';
    }
}
