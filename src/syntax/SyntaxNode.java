package syntax;

import interpreter.Instance;
import java.math.BigInteger;
import java.util.ArrayList;
import parser.ProgramError;
import stringcreator.StringCreator;
import syntax.expression.Variable;

public abstract class SyntaxNode {
    protected final static BigInteger MAX_VALUE = BigInteger.TEN.pow(100);
    protected final static BigInteger MIN_VALUE = MAX_VALUE.negate();
    
    public SyntaxNode jump;
    
    public abstract SyntaxNode commit(Instance instance) throws ProgramError;
    
    public boolean isStopNode() {
        return false;
    }
    public boolean isStatisticsNode() {
        return false;
    }
    public StringCreator getStatusCreator(Instance instance) {
        return null;
    }
    
    public interface Committer {
        public SyntaxNode commit(Instance instance) throws ProgramError;
    }
    
    public abstract void printDebug();
    public static void printDebugList(ArrayList<SyntaxNode> list) {
        for (SyntaxNode sn:list) {
            sn.printDebug();
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    protected static final class Lang {
        public static final String exceedMaxValue = "Przekroczenie maksymalnej wartości zmiennej (>10^100)";
        public static final String exceedMinValue = "Przekroczenie minimalnej wartości zmiennej (>10^100)";
        public static final String notInitializedValue = "Niezainiclizowano zmiennej %s";
        public static final String notInitializedArrayValue = "Niezainiclizowano zmiennej %s[%d]";
    }
    //</editor-fold>
}

/*
 * type:
 * 0  !
 * 1  - (unar)
 * 2  *
 * 3  /
 * 4  %
 * 5  + (binar)
 * 6  -
 * 7  <
 * 8  >
 * 9  <=
 * 10 >=
 * 11 ==
 * 12 !=
 * 13 &&
 * 14 ||
 * 15 ( (temp)
 * 
 * 20 call
 * 21 var
 * 22 const
 * 23 string
 * 
 * 30 =
 * 31 *=
 * 32 /=
 * 33 %=
 * 34 &=
 * 35 |=
 * 36 +=
 * 37 -=
 * 
 * 40 ++
 * 41 --
 * 
 * 50 for
 * 51 while
 * 52 do while
 * 53 if
 * 
 * 60 return
 * 61 continue
 * 62 break
 * 
 * 70 function
 * 
*/