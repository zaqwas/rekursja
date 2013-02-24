package stringcreator;

import java.awt.FontMetrics;

class NewStringExtender implements NewExtender {
    
    private FontMetrics fontMetrics;
    private String string;
    private int initialLength;
    private boolean ellipsis;
    private int lettersExtendingInOneStep;
    private int index;
    private int allWidth, initialWidth, currentWidth;
    private boolean all, done;
    
    public NewStringExtender(String string, int initialLength, boolean ellipsis,
            int lettersExtendingInOneStep) {

        assert string != null;
        assert initialLength >= 1;
        assert lettersExtendingInOneStep >= 1;
        assert string.length() > initialLength;

        this.string = string;
        this.initialLength = initialLength;
        this.ellipsis = ellipsis;
        this.lettersExtendingInOneStep = lettersExtendingInOneStep;
        
        index = initialLength;
    }
    
    
    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        assert this.fontMetrics == null;
        assert fontMetrics != null;
        this.fontMetrics = fontMetrics;
        
        allWidth = fontMetrics.stringWidth(string);
        initialWidth = fontMetrics.stringWidth(string.substring(0,initialLength));
        if ( ellipsis ) {
            initialWidth += fontMetrics.charWidth('…');
        }
        currentWidth = initialWidth;
        
        if ( allWidth <= initialWidth ) {
            done = true;
            all = true;
        }
    }
    
    
    @Override
    public int tryExtend(int maxExtendWidth) {
        assert fontMetrics != null;
        if (done) {
            return 0;
        }
        int width = 0;
        for (int i = 0; i < lettersExtendingInOneStep && !done; i++) {
            int stepWidth = tryExtendOneStep(maxExtendWidth);
            maxExtendWidth -= stepWidth;
            width += stepWidth;
        }
        return width;
    }
    
    private int tryExtendOneStep(int maxExtendWidth) {
        if (done) {
            return 0;
        }
        int letterWidth = fontMetrics.charWidth(string.charAt(index));
        if (allWidth <= currentWidth + letterWidth) {
            done = true;
            if (allWidth - currentWidth <= maxExtendWidth) {
                all = true;
                int oldWidth = currentWidth;
                currentWidth = allWidth;
                return allWidth - oldWidth;
            }
        } else {
            if (letterWidth <= maxExtendWidth) {
                index++;
                currentWidth += letterWidth;
                return letterWidth;
            }
            done = true;
        }
        return 0;
    }
    
    
    @Override
    public void reset() {
        assert fontMetrics != null;
        
        if ( allWidth <= initialWidth ) {
            return;
        }
        index = initialLength;
        currentWidth = initialWidth;
        done = false;
        all = false;
    }
    
    @Override
    public int getCurrentWidth() {
        assert fontMetrics != null;
        return currentWidth;
    }

    @Override
    public int getInitialWidth() {
        assert fontMetrics != null;
        return initialWidth;
    }
    
    @Override
    public int getAllWidth() {
        assert fontMetrics != null;
        return allWidth;
    }

    @Override
    public String getString() {
        assert fontMetrics != null;
        
        if (all) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(string, 0, index);
        if ( ellipsis ) {
            sb.append('…');
        }
        return sb.toString();
    }
}
