package stringcreator;

import java.awt.FontMetrics;

public class WrappedLazyStringCreator extends StringCreator {

    private StringCreator stringCreator;
    private String string;
    private int strWidth;

    public WrappedLazyStringCreator(String string, StringCreator stringCreator) {
        this.string = string;
        this.stringCreator = stringCreator;
    }

    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        super.setFontMetrics(fontMetrics);
        strWidth = fontMetrics.stringWidth(string);
    }

    @Override
    public String getString(int maxWidth) {
        if (maxWidth <= strWidth) {
            return string;
        }
        return string + stringCreator.getString(maxWidth - strWidth);
    }
}
