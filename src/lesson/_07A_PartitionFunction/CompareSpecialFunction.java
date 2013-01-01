package lesson._07A_PartitionFunction;

import lesson._06A_MergeFunction.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class CompareSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private BigInteger minusOne = BigInteger.ONE.negate();
    
    public CompareSpecialFunction(ArrayFrame arrayFrame) {
        this.arrayFrame = arrayFrame;
    }
    
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
    public SyntaxNode commit(Instance instance) {
        BigInteger n = arrayFrame.getArraySizeBigInt(1);
        BigInteger m = arrayFrame.getArraySizeBigInt(2);
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        //TODO throw error
        assert idx1.signum() >= 0 && idx1.compareTo(n) < 0;
        assert idx2.signum() >= 0 && idx2.compareTo(m) < 0;
        
        int cmp = arrayFrame.getArrayValue(idx1.intValue(), 1) - 
                arrayFrame.getArrayValue(idx2.intValue(), 2);
        BigInteger value;
        if (cmp ==0) {
            value = BigInteger.ZERO;
        } else if (cmp < 0) {
            value = minusOne;
        } else {
            value = BigInteger.ONE;
        }
        instance.setReturnedValue(value);
        
        return null;
    }

    @Override
    public boolean isStoppedBeforeCall() {
        return false;
    }

    @Override
    public boolean isStoppedAfterCall() {
        return true;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "porownaj";
    }
    //</editor-fold>
    
}
