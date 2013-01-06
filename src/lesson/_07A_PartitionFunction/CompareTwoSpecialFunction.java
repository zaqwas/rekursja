package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class CompareTwoSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private BigInteger minusOne = BigInteger.ONE.negate();
    private boolean started;
    private int lastIndex1, lastIndex2;
    private boolean lastCompared1, lastCompared2;
    
    public CompareTwoSpecialFunction(ArrayFrame arrayFrame) {
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
        
        lastCompared1 = arrayFrame.isValueCompared(lastIndex1);
        lastCompared2 = arrayFrame.isValueCompared(lastIndex2);
        //TODO throw error
        assert !lastCompared1 || !lastCompared2;
        arrayFrame.setValueCompared(lastIndex1, true);
        arrayFrame.setValueCompared(lastIndex2, true);
        
        int cmp = arrayFrame.getArrayValue(lastIndex1) - 
                arrayFrame.getArrayValue(lastIndex2);
        BigInteger value = cmp == 0 ? BigInteger.ZERO
                : cmp < 0 ? minusOne : BigInteger.ONE;
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
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call) node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.setValueCompared(lastIndex1, lastCompared1);
        arrayFrame.setValueCompared(lastIndex2, lastCompared2);
    }
    
    public void pauseStart(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        started = true;
        arrayFrame.compareStart(lastIndex1, lastIndex2);
    }
    
    public void pauseStop() {
        if ( started ) { 
            arrayFrame.compareStop(lastIndex1, lastIndex2);
            started = false;
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "porownaj";
    }
    //</editor-fold>
    
}
