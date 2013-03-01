package lesson._07B_QuickSort;

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
        return 1;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.REFERENCE;
    }

    @Override
    public SyntaxNode commit(Instance instance) {
        BigInteger n = arrayFrame.getArraySizeBigInt();
        ((ArgReference) instance.getArgument(0)).setValue(n);
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
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        int idx = ((ArgReference)instance.getArgument(0)).getValueAtTheEnd().intValue();
        return "Start(" + idx + ")";
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
