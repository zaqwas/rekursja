package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class CompareOneSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private BigInteger minusOne = BigInteger.ONE.negate();
    private boolean started;
    private int selectedPart = 1;
    private int lastIndex;
    
    public CompareOneSpecialFunction(ArrayFrame arrayFrame) {
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
        return VariableType.INTEGER;
    }

    @Override
    public SyntaxNode commit(Instance instance) {
        BigInteger n = arrayFrame.getArraySizeBigInt();
        BigInteger idx = ((ArgInteger)instance.getArgument(0)).getValue();
        
        //TODO throw error
        assert idx.signum() >= 0 && idx.compareTo(n) < 0;
        
        lastIndex = idx.intValue();
        
        if ( selectedPart == 2) {
            boolean compared = arrayFrame.isValueCompared(lastIndex);
            //TODO throw error
            assert !compared;
            arrayFrame.setValueCompared(lastIndex, true);
        }
        
        int cmp = arrayFrame.getArrayValue(lastIndex) - 
                arrayFrame.getArrayValue(0);
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
    
    
    public void setSelectedPart(int nr) {
        selectedPart = nr;
    }
    
    public void undo(SyntaxNode node) {
        assert selectedPart == 2;
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.setValueCompared(lastIndex, false);
    }
    
    public void pauseStart(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        started = true;
        arrayFrame.compareStart(0, lastIndex);
    }
    
    public void pauseStop() {
        if ( started ) { 
            arrayFrame.compareStop(0, lastIndex);
            started = false;
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "porownaj";
    }
    //</editor-fold>
    
}
