package stringcreator;

public abstract class Extender {
    
    public void fastExtend(int maxExtendWidth) {
        int w;
        maxExtendWidth -= getWidth();
        while ((w = tryExtend(maxExtendWidth)) > 0) {
            maxExtendWidth -= w;
        }
    }
    
    public abstract int tryExtend(int maxExtendWidth);
    
    public abstract String getString();
    
    public abstract int getWidth();
    
}
