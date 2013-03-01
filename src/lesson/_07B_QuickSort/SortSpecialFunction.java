package lesson._07B_QuickSort;

import lesson._06B_MergeSort.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class SortSpecialFunction extends SpecialFunctionBehavior {
    
    private BigInteger minusTen = BigInteger.TEN.negate();
    private BigInteger hundred = BigInteger.TEN.pow(2);
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return 2;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.INTEGER;
    }

    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        StringBuilder sb = new StringBuilder();
        sb.append("Uporzad(");
        if ( idx1.compareTo(minusTen) <= 0 || idx1.compareTo(hundred) >= 0 ) {
            sb.append("??");
        } else {
            int value = idx1.intValue();
            sb.append(value);
            if (value >=0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(",");
        if ( idx2.compareTo(minusTen) <= 0 || idx2.compareTo(hundred) >= 0 ) {
            sb.append("??");
        } else {
            int value = idx2.intValue();
            sb.append(value);
            if (value >=0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "uporzadkuj";
    }
    //</editor-fold>
    
}
