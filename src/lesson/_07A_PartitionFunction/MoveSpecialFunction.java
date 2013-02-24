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

class MoveSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private int lastIdxSrc, lastIdxDest;
    
    public MoveSpecialFunction(ArrayFrame arrayFrame) {
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
        BigInteger idxSrc = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idxDest = ((ArgInteger)instance.getArgument(1)).getValue();
        
        Call call = instance.getCallNode();
        BigInteger size = arrayFrame.getArraySizeBigInt();
        
        if (idxSrc.signum() < 0) {
            throw new ProgramError("Agrument idxSkad powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxSrc.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idxSkad powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxDest.signum() < 0) {
            throw new ProgramError("Agrument idxDokad powinien być nieujemny", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxDest.compareTo(size) >= 0) {
            throw new ProgramError("Agrument idxDokad powinien być mniejszy niż rozmiar tablicy",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        lastIdxSrc = idxSrc.intValue();
        lastIdxDest = idxDest.intValue();
        
        if (arrayFrame.isValueRemoved(lastIdxSrc)) {
            throw new ProgramError("Element tablicy pierwotnej o indeksie " + lastIdxSrc + " został już przeniesiony",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (arrayFrame.isResultValueSet(lastIdxDest)) {
            throw new ProgramError("Element tablicy wynikowej o indeksie " + lastIdxDest + " został już wypełniony",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        arrayFrame.moveValue(idxSrc.intValue(), idxDest.intValue());
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
        String str = "Przenieś element o indeksie " + lastIdxSrc + " na miejsce o indeksie " + lastIdxDest;
        return new SimpleLazyStringCreator(str);
    }
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call) node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.undoMoveValue(lastIdxSrc, lastIdxDest);
    }
    
    public boolean pauseStart(SyntaxNode node, int delayTime) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return true;
        }
        arrayFrame.animateMove(lastIdxSrc, lastIdxDest, delayTime);
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "przenies";
    }
    //</editor-fold>
    
}
