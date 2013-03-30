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

class CompareSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private BigInteger minusOne = BigInteger.ONE.negate();
    private BigInteger max = BigInteger.TEN.pow(2);
    private BigInteger min = BigInteger.TEN.negate();
    private boolean updateAfterPause;
    private int idx1, idx2;
    
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
    public SyntaxNode commit(Instance instance) throws ProgramError {
        BigInteger i1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger i2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        BigInteger size = arrayFrame.getArraySizeBigInt();
        Call call = instance.getCallNode();
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
        
        int cmp = arrayFrame.getArrayValue(idx1) - 
                arrayFrame.getArrayValue(idx2);
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
    
    @Override
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        int cmp = arrayFrame.getArrayValue(idx1) - 
                arrayFrame.getArrayValue(idx2);
        StringBuilder sb = new StringBuilder("Element o indeksie ")
                .append(idx1).append(" jest ");
        if (cmp == 0) {
            sb.append("równy elementowi");
        } else {
            sb.append(cmp < 0 ? "mniejszy" : "większy").append(" niż element");
        }
        sb.append(" o indeksie ").append(idx2)
                .append(cmp == 0 ? " (0)" : cmp < 0 ? " (-1)" : " (1)");
        
        return new SimpleLazyStringCreator(sb.toString());
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        StringBuilder sb = new StringBuilder("p(");
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0 ? "??" : value.toString());
        sb.append(",");
        value = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0 ? "??" : value.toString());
        return sb.append(")").toString();
    }
    
    public void pauseStartSelectionSort(int i) {
        arrayFrame.updateCompareSelectionSort(idx1, idx2, i);
    }
    public void pauseStartBubbleSort(int k, int range) {
        arrayFrame.updateCompareBubbleSort(idx1, k, range);
    }
    public void pauseStartInsertionSort(int i) {
        arrayFrame.updateCompareInsertionSort(idx1, i);
    }
    
    public void pauseStartUserCode() {
        updateAfterPause = true;
        arrayFrame.updateCompareUserCode(idx1, idx2);
    }
    public void pauseStop() {
        if (updateAfterPause) {
            arrayFrame.updateAfterPause(idx1, idx2);
            updateAfterPause = false;
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "porownaj";
    }
    //</editor-fold>
    
}
