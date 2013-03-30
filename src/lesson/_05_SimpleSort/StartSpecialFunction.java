package lesson._05_SimpleSort;

import lesson._01B_MaxElement.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class StartSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private int arraySize;
    
    public StartSpecialFunction(ArrayFrame arrayFrame) {
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
        return VariableType.REFERENCE;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            Call call = instance.getCallNode();
            throw new ProgramError("Funkcja „start” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        arraySize = arrayFrame.getArraySize();
        BigInteger n = arrayFrame.getArraySizeBigInt();
        ((ArgReference) instance.getArgument(0)).setValue(n);
        return null;
    }

    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        return "str(" + arraySize + ")";
    }
    
    @Override
    public boolean isStoppedBeforeCall() {
        return false;
    }

    @Override
    public boolean isStoppedAfterCall() {
        return false;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
