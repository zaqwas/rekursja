package lesson._06A_MergeFunction;

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
    private boolean started;
    private int lastIndex1, lastIndex2;
    
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
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValue();
        
        BigInteger size1 = arrayFrame.getArraySizeBigInt(true);
        BigInteger size2 = arrayFrame.getArraySizeBigInt(false);
        Call call = instance.getCallNode();
        if (idx1.signum() < 0) {
            throw new ProgramError("Agrument idx1 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx1.compareTo(size1) >= 0) {
            throw new ProgramError("Agrument idx1 powinien być mniejszy niż rozmiar pierwszej tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx2.signum() < 0) {
            throw new ProgramError("Agrument idx2 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx2.compareTo(size2) >= 0) {
            throw new ProgramError("Agrument idx2 powinien być mniejszy niż rozmiar drugiej tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        lastIndex1 = idx1.intValue();
        lastIndex2 = idx2.intValue();
        
        int cmp = arrayFrame.getArrayValue(lastIndex1, true) - 
                arrayFrame.getArrayValue(lastIndex2, false);
        BigInteger value;
        if (cmp == 0) {
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
    
    @Override
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        int cmp = arrayFrame.getArrayValue(lastIndex1, true) - 
                arrayFrame.getArrayValue(lastIndex2, false);
        String value = cmp == 0 ? " (0)"
                : cmp < 0 ? " (-1)" : " (1)";
        String cmpString = " jest " + (cmp == 0 ? "równy elementowi"
                : (cmp < 0 ? "mniejszy" : "większy") + " niż element")
                + " drugiej tablicy o indeksie ";
        String str = "Element pierwszej tablicy o indeksie " + lastIndex1 + cmpString + lastIndex2 + value;
        return new SimpleLazyStringCreator(str);
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
