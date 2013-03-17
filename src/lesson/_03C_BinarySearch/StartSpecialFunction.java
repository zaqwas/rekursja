package lesson._03C_BinarySearch;

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
    private boolean started;
    private int size;
    
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
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „start” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (started) {
            throw new ProgramError("Funkcja „start” może być wywoływana tylko raz.",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        size = arrayFrame.getArraySize();
        BigInteger n = arrayFrame.getArraySizeBigInt();
        ((ArgReference) instance.getArgument(0)).setValue(n);
        started = true;
        return null;
    }

    @Override
    public boolean isStoppedBeforeCall() {
        return false;
    }

    @Override
    public boolean isStoppedAfterCall() {
        return false;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        return "str(" + size + ")";
    }
    
    public void threadStart() {
        this.started = false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
