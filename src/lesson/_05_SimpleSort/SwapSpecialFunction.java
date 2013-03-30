package lesson._05_SimpleSort;

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
    private BigInteger max = BigInteger.TEN.pow(2);
    private BigInteger min = BigInteger.TEN.negate();
    private boolean updateAfterPause;
    private int idx1, idx2;
    
    
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
        BigInteger i1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger i2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        Call call = instance.getCallNode();
        BigInteger size = arrayFrame.getArraySizeBigInt();
        
        if (i1.signum() < 0) {
            throw new ProgramError("Agrument idx1 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (i1.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idx1 powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (i2.signum() < 0) {
            throw new ProgramError("Agrument idx2 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (i2.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idx2 powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        idx1 = i1.intValue();
        idx2 = i2.intValue();
        
        arrayFrame.swapValue(idx1, idx2);
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
        String str = "Przenieś element o indeksie " + idx1 + " na miejsce o indeksie " + idx2;
        return new SimpleLazyStringCreator(str);
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        StringBuilder sb = new StringBuilder("z(");
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0 ? "??" : value.toString());
        sb.append(",");
        value = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0 ? "??" : value.toString());
        return sb.append(")").toString();
    }
    
    
    public void pauseStartSelectionSort(int delayTime) {
        arrayFrame.animateSwapSelectionSort(idx1, idx2, delayTime);
    }
    public void pauseStartBubbleSort(int k, int range, int delayTime) {
        arrayFrame.animateSwapBubbleSort(idx1, k, range, delayTime);
    }
    public void pauseStartInsertionSort(int i, int delayTime) {
        arrayFrame.animateSwapInsertionSort(idx1, i, delayTime);
    }
    
    public void pauseStartUserCode(int delayTime) {
        updateAfterPause = true;
        arrayFrame.animateSwapUserCode(idx1, idx2, delayTime);
    }
    public void pauseStop() {
        if (updateAfterPause) {
            arrayFrame.updateAfterPause(idx1, idx2);
            updateAfterPause = false;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "zamien";
    }
    //</editor-fold>
    
}
