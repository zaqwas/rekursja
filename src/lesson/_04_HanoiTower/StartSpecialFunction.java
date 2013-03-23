package lesson._04_HanoiTower;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class StartSpecialFunction extends SpecialFunctionBehavior {

    private HanoiFrame hanoiFrame;
    private boolean called;
    private int diskNumber, startRod, finishRod;
    
    public StartSpecialFunction(HanoiFrame arrayFrame) {
        this.hanoiFrame = arrayFrame;
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
        if (called) {
            throw new ProgramError("Funkcja „start” może być wywoływana tylko raz.",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        diskNumber = hanoiFrame.getDiskNumber();
        startRod = hanoiFrame.getStartRod() + 1;
        finishRod = hanoiFrame.getFinishRod() + 1;

        BigInteger diskNumberBigInt = hanoiFrame.getDiskNumberBigInt();
        BigInteger startRodBigInt = BigInteger.valueOf(startRod);
        BigInteger finishRodBigInt = BigInteger.valueOf(finishRod);
        
        ((ArgReference) instance.getArgument(0)).setValue(diskNumberBigInt);
        ((ArgReference) instance.getArgument(1)).setValue(startRodBigInt);
        ((ArgReference) instance.getArgument(2)).setValue(finishRodBigInt);
        
        called = true;
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
        return "s(" + diskNumber + "," + startRod + "," + finishRod + ")";
    }
    
    public void threadStart() {
        this.called = false;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
