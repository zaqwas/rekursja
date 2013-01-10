package stringcreator;

import java.awt.FontMetrics;

public class SimpleEagerStringCreator extends StringCreator {
    
    private String string;
    private int strWidth = Integer.MAX_VALUE;
    private int dotsWidth;
    
    public SimpleEagerStringCreator(String string) {
        this.string = string;
    }

    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        if (this.fontMetrics != null) {
            return;
        }
        super.setFontMetrics(fontMetrics);
        strWidth = fontMetrics.stringWidth(string);
        dotsWidth = fontMetrics.charWidth('…');
    }
    
    @Override
    public String getString(int maxWidth) {
        assert fontMetrics != null : "FontMetrics not initialized.";

        if (strWidth <= maxWidth) {
            return string;
        }
        if (maxWidth < dotsWidth) {
            return "";
        }
        int length = 0, width = dotsWidth;
        while (true) {
            char c = string.charAt(length);
            int newWidth = width + fontMetrics.charWidth(c);
            if (newWidth > maxWidth) {
                break;
            }
            width = newWidth;
            length++;
        }
        return string.substring(0, length) + '…';
    }
}
