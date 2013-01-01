package lesson._06A_MergeFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class MoveSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    
    public MoveSpecialFunction(ArrayFrame arrayFrame) {
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
    public SyntaxNode commit(Instance instance) {
//        BigInteger n = arrayFrame.getArraySizeBigInt(1);
//        BigInteger m = arrayFrame.getArraySizeBigInt(2);
        BigInteger idxSrc = ((ArgInteger)instance.getArgument(0)).getValue();
        BigInteger tab = ((ArgInteger)instance.getArgument(1)).getValue();
        BigInteger idxDest = ((ArgInteger)instance.getArgument(2)).getValue();
        
        //TODO throw error
        arrayFrame.moveValueToResultArray(idxSrc.intValue(), tab.intValue()+1, idxDest.intValue());
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
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "przenies";
    }
    //</editor-fold>
    
}
