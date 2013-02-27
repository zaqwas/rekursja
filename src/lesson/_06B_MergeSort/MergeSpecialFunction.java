package lesson._06B_MergeSort;

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

class MergeSpecialFunction extends SpecialFunctionBehavior {
    
    private ArrayFrame arrayFrame;
    private boolean started;
    
    private int lastIdx1, lastIdx2, lastIdx3;
    private int prevArrayInt[] = new int[32];
    private String prevArrayStr[] = new String[32];
    
    private BigInteger minusTen = BigInteger.TEN.negate();
    private BigInteger hundred = BigInteger.TEN.pow(2);
    
    
    public MergeSpecialFunction(ArrayFrame arrayFrame) {
        this.arrayFrame = arrayFrame;
    }
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return 3;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.INTEGER;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValue();
        BigInteger idx3 = ((ArgInteger)instance.getArgument(2)).getValue();
        
        BigInteger size = arrayFrame.getArraySizeBigInt();
        Call call = instance.getCallNode();
        
        if (idx1.signum() < 0) {
            throw new ProgramError("Agrument idx1 powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx3.compareTo(size) > 0) {
            throw new ProgramError("Agrument idx3 powinien być mniejszy lub równy rozmiarowi tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx1.compareTo(idx2) >= 0) {
            throw new ProgramError("Agrument idx1 powinien być mniejszy niż argument idx2",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idx2.compareTo(idx3) >= 0) {
            throw new ProgramError("Agrument idx2 powinien być mniejszy niż argument idx3",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        lastIdx1 = idx1.intValue();
        lastIdx2 = idx2.intValue();
        lastIdx3 = idx3.intValue();
        
        arrayFrame.rememberValues(lastIdx1, lastIdx3, prevArrayInt, prevArrayStr);
        
        arrayFrame.merge(lastIdx1, lastIdx2, lastIdx3);
        return null;
    }

    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger idx1 = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        BigInteger idx2 = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        BigInteger idx3 = ((ArgInteger)instance.getArgument(2)).getValueAtTheBeginning();
        StringBuilder sb = new StringBuilder();
        sb.append("S(");
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
        if ( idx3.compareTo(minusTen) <= 0 || idx3.compareTo(hundred) >= 0 ) {
            sb.append("??");
        } else {
            int value = idx3.intValue();
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
        String str = "Scalanie";
        return new SimpleLazyStringCreator(str);
    }
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call) node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.restoreValues(lastIdx1, lastIdx3, prevArrayInt, prevArrayStr);
    }
    
    public boolean pauseStart(SyntaxNode node, int delayTime) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            started = false;
            return true;
        }
        arrayFrame.animateMerge(lastIdx1, lastIdx2, lastIdx3, delayTime);
        started = true;
        return false;
    }
    
    public void pauseStop() {
        if (started) {
            arrayFrame.updateArray();
            started = false;
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "scal";
    }
    //</editor-fold>
    
}
