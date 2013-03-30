package lesson._05_SimpleSort;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class SortSpecialFunction extends SpecialFunctionBehavior {
    
    private BigInteger max = BigInteger.TEN.pow(2);
    private BigInteger min = BigInteger.TEN.negate();
    
    @Override
    public String getName() {
        return Lang.functionName;
    }
    @Override
    public int getArgumentsLength() {
        return 1;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.INTEGER;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        StringBuilder sb = new StringBuilder("uprz(");
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0 ? "??" : value.toString());
        return sb.append(")").toString();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "uporzadkuj";
    }
    //</editor-fold>
    
}
