package stringcreator;

import java.awt.FontMetrics;

public interface NewExtender {
    
    public void setFontMetrics(FontMetrics fontMetrics);
    
    public int tryExtend(int maxExtendWidth);
    public void reset();
    
    public int getCurrentWidth();
    public int getInitialWidth();
    public int getAllWidth();
    
    public String getString();
    
}
