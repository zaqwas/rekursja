package lesson._07A_PartitionFunction;

import lesson._06A_MergeFunction.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class StartSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    
    public StartSpecialFunction(ArrayFrame arrayFrame) {
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
        return VariableType.REFERENCE;
    }

    @Override
    public SyntaxNode commit(Instance instance) {
        BigInteger n = arrayFrame.getArraySizeBigInt(1);
        BigInteger m = arrayFrame.getArraySizeBigInt(1);
        ((ArgReference) instance.getArgument(0)).setValue(n);
        ((ArgReference) instance.getArgument(1)).setValue(m);
        return null;
    }

    @Override
    public boolean isStoppedBeforeCall() {
        return false;
    }

    @Override
    public boolean isStoppedAfterCall() {
        return false;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
