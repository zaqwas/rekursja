package lesson._01B_MaxElement;

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
    
    @Override
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        int cmp = arrayFrame.getArrayValue(lastIndex1) - 
                arrayFrame.getArrayValue(lastIndex2);
        String value = cmp == 0 ? " (0)"
                : cmp < 0 ? " (-1)" : " (1)";
        String cmpString = " jest " + (cmp == 0 ? "równy elementowi"
                : (cmp < 0 ? "mniejszy" : "większy") + " niż element")
                + " o indeksie ";
        String str = "Element o indeksie " + lastIndex1 + cmpString + lastIndex2 + value;
        return new SimpleLazyStringCreator(str);
    }
    
    public void pauseStart(boolean userCode) {
        started = userCode;
        arrayFrame.compareStart(lastIndex1, lastIndex2);
    }

    public void pauseStop() {
        if (started) {
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
