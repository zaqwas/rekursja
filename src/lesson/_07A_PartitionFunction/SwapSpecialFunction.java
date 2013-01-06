package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class SwapSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private int lastIndex1, lastIndex2;
    
    public SwapSpecialFunction(ArrayFrame arrayFrame) {
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
        BigInteger n = arrayFrame.getArraySizeBigInt();
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        //TODO throw error
        assert idx1.signum() >= 0 && idx1.compareTo(n) < 0;
        
        lastIndex1 = idx1.intValue();
        lastIndex2 = idx2.intValue();
        arrayFrame.swapValue(lastIndex1, lastIndex2);
        
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
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.swapValue(lastIndex1, lastIndex2);
    }
    
    
    public void pauseStart(SyntaxNode node, int time) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.animateSwap(lastIndex1, lastIndex2);        
    }
    public void pauseStop() {
        while (arrayFrame.isAnimating()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "zamien";
    }
    //</editor-fold>
    
}
