package stringcreator;

import java.awt.FontMetrics;

public class StringExtender extends Extender {
    private String string;
    private FontMetrics fontMetrics;
    private boolean all, done;
    private int allWidth, currWidth;
    private int strLength, index;
    
    public StringExtender(String string, int basicLength, FontMetrics fontMetrics) {
        this.string = string;
        this.fontMetrics = fontMetrics;
        
        allWidth = fontMetrics.stringWidth(string);
        strLength = string.length();
        if ( strLength <= basicLength ) {
            setAll();
            return;
        }
        index = basicLength-1;
        currWidth = fontMetrics.charWidth('…');
        currWidth += fontMetrics.stringWidth(string.substring(0, basicLength-1));
        if ( allWidth <= currWidth ) {
            setAll();
        }
    }
    
    private void setAll() {
        currWidth = allWidth;
        all = true;
        done = true;
    }
    private int max;
    @Override
    public int tryExtend(int maxExtendWidth) {
        if ( max==0 ) {
            max = maxExtendWidth;
        }
        if ( done ) {
            return 0;
        }
        int letterWidth = fontMetrics.charWidth(string.charAt(index));
        if ( allWidth <= currWidth+letterWidth ) {
            done = true;
            if ( allWidth-currWidth <= maxExtendWidth ) {
                all = true;
                int oldWidth = currWidth;
                currWidth = allWidth;
                return allWidth-oldWidth;
            }
        } else {
            if ( letterWidth <= maxExtendWidth ) {
                index++;
                currWidth += letterWidth;
                return letterWidth;
            }
            done = true;
        }
        return 0;
    }

    @Override
    public String getString() {
        if (all) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(string, 0, index);
        sb.append('…');
        return sb.toString();
    }

    @Override
    public int getWidth() {
        return currWidth;
    }
}
