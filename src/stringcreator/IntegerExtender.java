
package stringcreator;

import java.awt.FontMetrics;
import java.math.BigInteger;

    
public class IntegerExtender extends Extender {
    private FontMetrics fontMetrics;
    private String str;
    private int allWidth, currWidth;
    private int modulLength, index;
    private boolean done, minus, all;
    
    public IntegerExtender(BigInteger integer, FontMetrics fontMetrics) {
        this.fontMetrics = fontMetrics;
        if ( integer==null ) {
            str = "?";
            allWidth = fontMetrics.charWidth('?');
            setAll();
            return;
        }
        
        str = integer.toString();
        allWidth = fontMetrics.stringWidth(str);
        modulLength = str.length();
        index = 2;
        if ( str.charAt(0)=='-') {
            minus = true;
            modulLength--;
            index++;
        }
        
        if ( modulLength<=10 ) {
            setAll();
            return;
        }
        
        currWidth = fontMetrics.stringWidth(".…·10^");
        currWidth += fontMetrics.stringWidth(Integer.toString(modulLength-1));
        currWidth += fontMetrics.stringWidth(str.substring(0,index));
        if ( allWidth<=currWidth ) {
            setAll();
        }
    }
    
    private void setAll() {
        currWidth = allWidth;
        all = true;
        done = true;
    }
    
    @Override
    public int tryExtend(int maxExtendWidth) {
        if (done) {
            return 0;
        }
        int letterWidth = fontMetrics.charWidth(str.charAt(index));
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
        if ( all ) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int start;
        if (minus) {
            sb.append(str, 0, 2);
            start = 2;
        } else {
            sb.append(str, 0, 1);
            start = 1;
        }
        sb.append('.');
        sb.append(str, start, index);
        sb.append("…·10^");
        sb.append(modulLength-1);
        
        return sb.toString();
    }

    @Override
    public int getWidth() {
        return currWidth;
    }    
}
