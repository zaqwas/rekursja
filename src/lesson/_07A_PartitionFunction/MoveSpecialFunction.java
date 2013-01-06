package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public SyntaxNode commit(Instance instance) {
//        BigInteger n = arrayFrame.getArraySizeBigInt(1);
//        BigInteger m = arrayFrame.getArraySizeBigInt(2);
        BigInteger idxSrc = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger idxDest = ((ArgInteger)instance.getArgument(1)).getValue();
        
        //TODO throw error
        
        lastIdxSrc = idxSrc.intValue();
        lastIdxDest = idxDest.intValue();
        
        
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
    
    public void undo(SyntaxNode node) {
        if (!(node instanceof Call) || ((Call) node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.undoMoveValue(lastIdxSrc, lastIdxDest);
    }
    
    public void pauseStart(SyntaxNode node, int time) {
        if (!(node instanceof Call) || ((Call)node).getFunction().getFunctionBehavior() != this) {
            return;
        }
        arrayFrame.animateMove(lastIdxSrc, lastIdxDest);
        
    }
    public void pauseStop() {
        while (arrayFrame.isAnimating()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "przenies";
    }
    //</editor-fold>
    
}
