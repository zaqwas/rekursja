package lesson._02C_Logarithm;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class LogarithmSpecialFunction extends SpecialFunctionBehavior {
    
    private BigInteger maxA = BigInteger.TEN.pow(3);
    private BigInteger minA = BigInteger.TEN.pow(2).negate();
    private BigInteger maxN = BigInteger.TEN.pow(10);
    private BigInteger minN = BigInteger.TEN.pow(9).negate();
    
    
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
        BigInteger valueA = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        BigInteger valueN = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        String valueAStr = valueA.compareTo(maxA) >= 0 || valueA.compareTo(minA) <= 0 ? "??" : valueA.toString();
        String valueNStr = valueN.compareTo(maxN) >= 0 || valueN.compareTo(minN) <= 0 ? "??????????" : valueN.toString();
        return new StringBuilder("log(").append(valueAStr)
                .append(",").append(valueNStr).append(")").toString();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "logarytm";
    }
    //</editor-fold>
    
}
