package stringcreator;

import java.awt.FontMetrics;

class NewIntegerExtender implements NewExtender {

    private FontMetrics fontMetrics;
    private String string;
    private int initialDigitsAfterComa;
    private int digitsExtendingInOneStep;
    private int allWidth, initialWidth, currentWidth;
    private int modulLength, index;
    private boolean minus, done, all;

    public NewIntegerExtender(String string,
            int initialDigitsAfterComa, int digitsExtendingInOneStep) {

        assert initialDigitsAfterComa >= 1;
        assert digitsExtendingInOneStep >= 1;

        this.string = string;
        this.initialDigitsAfterComa = initialDigitsAfterComa;
        this.digitsExtendingInOneStep = digitsExtendingInOneStep;

        modulLength = string.length();
        index = initialDigitsAfterComa + 1;
        if (string.charAt(0) == '-') {
            minus = true;
            modulLength--;
            index++;
        }
        assert modulLength > initialDigitsAfterComa + 10;
    }

    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        assert this.fontMetrics == null;
        assert fontMetrics != null;
        this.fontMetrics = fontMetrics;

        allWidth = fontMetrics.stringWidth(string);
        initialWidth = fontMetrics.stringWidth(".…·10^");
        initialWidth += fontMetrics.stringWidth(Integer.toString(modulLength - 1));
        initialWidth += fontMetrics.stringWidth(string.substring(0, index));
        currentWidth = initialWidth;

        if (allWidth <= initialWidth) {
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
        for (int i = 0; i < digitsExtendingInOneStep && !done; i++) {
            int stepWidth = tryExtendOneStep(maxExtendWidth);
            maxExtendWidth -= stepWidth;
            width += stepWidth;
        }
        return width;
    }

    private int tryExtendOneStep(int maxExtendWidth) {
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

        if (allWidth <= initialWidth) {
            return;
        }
        index = initialDigitsAfterComa + 1;
        if (minus) {
            index++;
        }
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
        int start;
        if (minus) {
            sb.append(string, 0, 2);
            start = 2;
        } else {
            sb.append(string, 0, 1);
            start = 1;
        }
        sb.append('.');
        sb.append(string, start, index);
        sb.append("…·10^");
        sb.append(modulLength - 1);

        return sb.toString();
    }
}
