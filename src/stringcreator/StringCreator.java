package stringcreator;

import java.awt.FontMetrics;

public abstract class StringCreator {
    
    protected FontMetrics fontMetrics;
    
    public void setFontMetrics(FontMetrics fontMetrics)
    {
        this.fontMetrics = fontMetrics;
    }
    
    public abstract String getString(int maxWidth);
    
}
