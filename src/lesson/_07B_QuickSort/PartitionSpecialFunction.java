package lesson._07B_QuickSort;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import parser.ProgramError;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class PartitionSpecialFunction extends SpecialFunctionBehavior {
    
    private ArrayFrame arrayFrame;
    private boolean started;
    
    private int lastIdx1, lastIdx2;
    private int lastOutIdx1, lastOutIdx2;
    private int prevArrayInt[] = new int[32];
    private String prevArrayStr[] = new String[32];
    
    private BigInteger minusTen = BigInteger.TEN.negate();
    private BigInteger hundred = BigInteger.TEN.pow(2);
    
    
    public PartitionSpecialFunction(ArrayFrame arrayFrame) {
        this.arrayFrame = arrayFrame;
    }
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return 4;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return index < 2 ? VariableType.INTEGER : VariableType.REFERENCE;
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
        if (idx2.compareTo(size) > 0) {
            throw new ProgramError("Agrument idx2 powinien być mniejszy lub równy rozmiarowi tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx1.compareTo(idx2) >= 0) {
            throw new ProgramError("Agrument idx1 powinien być mniejszy niż argument idx2",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        lastIdx1 = idx1.intValue();
        lastIdx2 = idx2.intValue();
        
        arrayFrame.rememberValues(lastIdx1, lastIdx2, prevArrayInt, prevArrayStr);
        
        int tab[] = arrayFrame.partition(lastIdx1, lastIdx2);
        
        lastOutIdx1 = tab[0];
        lastOutIdx2 = tab[1];
        
        ((ArgReference)instance.getArgument(2)).setValue(BigInteger.valueOf(tab[0]));
        ((ArgReference)instance.getArgument(3)).setValue(BigInteger.valueOf(tab[1]));
        return null;
    }

    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        BigInteger idx3 = ((ArgReference)instance.getArgument(2)).getValueAtTheEnd();
        BigInteger idx4 = ((ArgReference)instance.getArgument(3)).getValueAtTheEnd();
        StringBuilder sb = new StringBuilder();
        sb.append("P(");
        if ( idx1.compareTo(minusTen) <= 0 || idx1.compareTo(hundred) >= 0 ) {
            sb.append("??");
        } else {
            int value = idx1.intValue();
            sb.append(value);
            if (value >=0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(",");
        if ( idx2.compareTo(minusTen) <= 0 || idx2.compareTo(hundred) >= 0 ) {
            sb.append("??");
        } else {
            int value = idx2.intValue();
            sb.append(value);
            if (value >=0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(",");
        if (idx3 == null || idx3.compareTo(minusTen) <= 0 || idx3.compareTo(hundred) >= 0) {
            sb.append("??");
        } else {
            int value = idx3.intValue();
            sb.append(value);
            if (value >= 0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(",");
        if (idx4 == null || idx4.compareTo(minusTen) <= 0 || idx4.compareTo(hundred) >= 0) {
            sb.append("??");
        } else {
            int value = idx4.intValue();
            sb.append(value);
            if (value >= 0 && value < 10) {
                sb.append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
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
        StringBuilder sb = new StringBuilder();
        sb.append("Podziel fragment tablicy [")
                .append(lastIdx1).append(',').append(lastIdx2)
                .append(") na trzy części: [")
                .append(lastIdx1).append(',').append(lastOutIdx1)
                .append("), [")
                .append(lastOutIdx1).append(',').append(lastOutIdx2)
                .append("), [")
                .append(lastOutIdx2).append(',').append(lastIdx2)
                .append(")");
        return new SimpleLazyStringCreator(sb.toString());
    }
    
    public void pauseStart(int delayTime, boolean userCode) {
        arrayFrame.animatePartition(lastIdx1, lastIdx2, prevArrayInt, prevArrayStr, delayTime);
        started = userCode;
    }
    
    public void pauseStop() {
        if (started) {
            arrayFrame.paintArrayRange(lastIdx1, lastIdx2, "empty");
            started = false;
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "podziel";
    }
    //</editor-fold>
    
}
