package stringcreator;

import java.awt.FontMetrics;
import java.math.BigInteger;
import java.util.ArrayList;

public class FlexibleStringCreator extends StringCreator {

    private ArrayList array = new ArrayList();
    private int initialLength = 0;
    private String initialString;
    private ArrayList<NewExtender> extenders = new ArrayList<>();

    
    public FlexibleStringCreator addString(String str) {
        assert fontMetrics == null;

        if (str == null || str.isEmpty()) {
            return this;
        }
        array.add(str);
        return this;
    }

    public FlexibleStringCreator addStringToExtend(String str, int initialLength,
            boolean ellipsis, int lettersExtendingInOneStep) {
        assert initialLength > 0 && lettersExtendingInOneStep > 0;

        if (str == null || str.isEmpty()) {
            return this;
        }
        if (str.length() <= initialLength) {
            array.add(str);
            return this;
        }
        array.add(new NewStringExtender(str, initialLength, ellipsis, lettersExtendingInOneStep));
        return this;
    }

    public FlexibleStringCreator addBigIntegerToExtend(BigInteger bigInt,
            int initialDigitsAfterComa, int digitsExtendingInOneStep) {
        assert initialDigitsAfterComa >= 0 && digitsExtendingInOneStep > 0;

        if (bigInt == null) {
            array.add("?");
            return this;
        }
        String str = bigInt.toString();
        int modulLength = str.length();
        if (bigInt.signum() < 0) {
            modulLength--;
        }
        if (modulLength <= initialDigitsAfterComa + 10) {
            array.add(str);
            return this;
        }
        array.add(new NewIntegerExtender(str, initialDigitsAfterComa, digitsExtendingInOneStep));
        return this;
    }

    @Override
    public void setFontMetrics(FontMetrics fontMetrics) {
        assert this.fontMetrics == null;
        assert fontMetrics != null;

        this.fontMetrics = fontMetrics;

        int idx = 0;
        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            if (obj instanceof NewExtender) {
                NewExtender extender = (NewExtender) obj;
                extender.setFontMetrics(fontMetrics);
                if (extender.getAllWidth() <= extender.getInitialWidth()) {
                    String str = extender.getString();
                    array.set(idx, str);
                    initialLength += fontMetrics.stringWidth(str);
                    sb.append(str);
                } else {
                    extenders.add(extender);
                    initialLength += extender.getInitialWidth();
                    sb.append(extender.getString());
                }
            } else if (obj instanceof String) {
                String str = (String) obj;
                initialLength += fontMetrics.stringWidth(str);
                sb.append(str);
            }
            idx++;
        }
        initialString = sb.toString();
    }

    @Override
    public String getString(int maxWidth) {
        assert fontMetrics != null;

        if (maxWidth <= initialLength || extenders.isEmpty()) {
            return initialString;
        }

        maxWidth -= initialLength;
        while (true) {
            boolean extended = false;
            for (NewExtender extender : extenders) {
                int width = extender.tryExtend(maxWidth);
                if (width > 0) {
                    extended = true;
                    maxWidth -= width;
                }
            }
            if (!extended) {
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            if (obj instanceof NewExtender) {
                NewExtender extender = (NewExtender) obj;
                sb.append(extender.getString());
                extender.reset();
            } else if (obj instanceof String) {
                String str = (String) obj;
                sb.append(str);
            }
        }
        return sb.toString();
    }
}
