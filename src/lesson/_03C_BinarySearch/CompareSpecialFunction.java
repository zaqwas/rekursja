package lesson._03C_BinarySearch;

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
    private boolean comparingStarted;
    private boolean found;
    private int index;
    
    
    
    public CompareSpecialFunction(ArrayFrame arrayFrame) {
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
    public SyntaxNode commit(Instance instance) throws ProgramError {
        BigInteger idx = ((ArgInteger)instance.getArgument(0)).getValue();
        
        BigInteger size = arrayFrame.getArraySizeBigInt();
        Call call = instance.getCallNode();
        if (idx.signum() < 0) {
            throw new ProgramError("Agrument idx powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idx powinien być mniejszy niż rozmiar pierwszej tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        index = idx.intValue();
        int cmp = arrayFrame.getArrayValue(index) - 
                arrayFrame.getSearchingValue();
        BigInteger value;
        if (cmp == 0) {
            value = BigInteger.ZERO;
            found = true;
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
        int cmp = arrayFrame.getArrayValue(index) - 
                arrayFrame.getSearchingValue();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Element tablicy o indeksie ").append(index).append(" jest ");
        if (cmp == 0){
            sb.append("równy wyszukiwanemu elementowi (0)");
        } else {
            sb.append(cmp < 0 ? "mniejszy" : "większy").append(" niż wyszukiwany element ")
                    .append(cmp < 0 ? "(-1)" : "(1)");
        }
        return new SimpleLazyStringCreator(sb.toString());
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        String valueStr = value.compareTo(max) >= 0 || value.compareTo(min) <= 0
                ? "??" : value.toString();
        return "por(" + valueStr + ")";
    }
    
    public void pauseStart(boolean userCode) {
        comparingStarted = userCode;
        arrayFrame.compareStart(index);
    }
    
    public void pauseStop() {
        if ( comparingStarted ) { 
            arrayFrame.compareStop(index);
            comparingStarted = false;
        }
    }
    
    public void threadStart() {
        this.found = false;
        this.index = -1;
    }
    
    public boolean isValueFound() {
        return found;
    }

    public int getIdxLastCompared() {
        return index;
    }

    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "porownaj";
    }
    //</editor-fold>
    
}
