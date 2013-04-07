package lesson._08_Fibonacci;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class FibonacciSpecialFunction extends SpecialFunctionBehavior {
    
    private boolean part4;
    private boolean currentThreadPart4;
    
    private BigInteger part1MaxN = BigInteger.TEN.pow(2);
    private BigInteger part1MinN = BigInteger.TEN.negate();
    private BigInteger part4MaxN = BigInteger.TEN.pow(4);
    private BigInteger part4MinN = BigInteger.TEN.pow(3).negate();
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return part4 ? 3 : 1;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return index == 0 ? VariableType.INTEGER : VariableType.REFERENCE;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger valueN = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        String valueNStr;
        if (currentThreadPart4) {
            valueNStr = valueN.compareTo(part4MaxN) >= 0 || valueN.compareTo(part4MinN) <= 0
                    ? "????" : valueN.toString();
        } else {
            valueNStr = valueN.compareTo(part1MaxN) >= 0 || valueN.compareTo(part1MinN) <= 0
                    ? "??" : valueN.toString();
        }
        return new StringBuilder("f(").append(valueNStr).append(")").toString();
    }
    
    public void setSelectedPart(int part) {
        this.part4 = part == 4;
    }
    
    public void threadStart() {
        currentThreadPart4 = part4;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "fibonacci";
    }
    //</editor-fold>
    
}
