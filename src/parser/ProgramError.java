package parser;

import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import stringcreator.WrappedLazyStringCreator;

public class ProgramError extends Exception {
    
    private final static int UNSET = 0;
    private final static int SET_INDEX = 1;
    private final static int SET_LINE_AND_POSITON = 2;
    
    private int state = UNSET;
    
    private int leftIndex;
    private int rightIndex;
    private StringCreator stringCreator;
    
    
    public ProgramError(String message, int leftIndex, int rightIndex) {
        assert leftIndex <= rightIndex;
        
        state = SET_INDEX;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        stringCreator = new SimpleLazyStringCreator(message);
    }
    public ProgramError(String message, int index) {
        state = SET_INDEX;
        this.leftIndex = index;
        this.rightIndex = index;
        stringCreator = new SimpleLazyStringCreator(message);
    }
    public ProgramError(String message) {
        stringCreator = new SimpleLazyStringCreator(message);
    }
    
    
    public void setIndexes(int leftIndex, int rightIndex) {
        assert leftIndex <= rightIndex;
        assert state != SET_LINE_AND_POSITON;
        
        state = SET_INDEX;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }

    
    public boolean isIndexSet() {
        return state != UNSET;
    }
    
    public int getLeftIndex() {
        return leftIndex;
    }
    public int getRightIndex() {
        return rightIndex;
    }
    
    public StringCreator getStringCreator() {
        return stringCreator;
    }
    
    public void setLineAndPosition(int line, int position) {
        assert leftIndex != UNSET;
        if ( state == SET_LINE_AND_POSITON ) {
            return;
        }
        state = SET_LINE_AND_POSITON;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(Lang.line).append(line).append("; ")
                .append(Lang.position).append(position).append("] ");
        String linePositionString = sb.toString();
        
        stringCreator = new WrappedLazyStringCreator(linePositionString, stringCreator);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static String line = "Linia: ";
        public static String position = "Pozycja: ";
    }
    //</editor-fold>
}
