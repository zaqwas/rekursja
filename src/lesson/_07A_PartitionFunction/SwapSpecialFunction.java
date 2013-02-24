package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import parser.ProgramError;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
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
    public SyntaxNode commit(Instance instance) throws ProgramError {
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        BigInteger size = arrayFrame.getArraySizeBigInt();
        Call call = instance.getCallNode();
        if (idx1.signum() < 0) {
            throw new ProgramError("Agrument idx1 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx1.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idx1 powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx2.signum() < 0) {
            throw new ProgramError("Agrument idx2 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx2.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idx2 powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
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

    @Override
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        String str = "Zamień elementy o indeksach: " + lastIndex1 + " oraz " + lastIndex2;
        return new SimpleLazyStringCreator(str);
    }
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.swapValue(lastIndex1, lastIndex2);
    }
    
    public boolean pauseStart(SyntaxNode node, int delayTime) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return true;
        }
        arrayFrame.animateSwap(lastIndex1, lastIndex2, delayTime);
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "zamien";
    }
    //</editor-fold>
    
}
