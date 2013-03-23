package lesson._04_HanoiTower;

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

    private HanoiFrame hanoiFrame;
    private int src, dest;
    private boolean pauseStarted;
    
    public MoveSpecialFunction(HanoiFrame arrayFrame) {
        this.hanoiFrame = arrayFrame;
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
        BigInteger size = hanoiFrame.getDiskNumberBigInt();
        
        if (idxSrc.signum() <= 0) {
            throw new ProgramError("Agrument skad powinien być dodatni", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxSrc.compareTo(size) >= 0) {
            throw new ProgramError("Agrument skad powinien być mniejszy niż 4",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxDest.signum() <= 0) {
            throw new ProgramError("Agrument dokad powinien być dodatni", 
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxDest.compareTo(size) >= 0) {
            throw new ProgramError("Agrument dokad powinien być mniejszy niż 4",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (idxSrc.compareTo(idxDest) == 0) {
            throw new ProgramError("Agrument skad powinien być różny od argumentu dokad",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        src = idxSrc.intValue();
        dest = idxDest.intValue();
        
        if (hanoiFrame.isStackEmpty(src - 1)) {
            throw new ProgramError("Na słupku " + src + " nie ma żadnego krążka",
                    call.getLeftIndex(), call.getRightIndex());
        }
        if (hanoiFrame.isBiggerDisk(src - 1, dest - 1)) {
            throw new ProgramError("Krążek na słupku " + src + " jest większy niż krążek na słupku " + dest,
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        hanoiFrame.moveDisk(src - 1, dest - 1);
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
        String str = "Przenieś krążek ze słupka " + src + " na słupek " + dest;
        return new SimpleLazyStringCreator(str);
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        StringBuilder sb = new StringBuilder("prz(");
        for (int i = 0; i < 2; i++) {
            if (i > 0) {
                sb.append(",");
            }
            BigInteger value = ((ArgInteger) instance.getArgument(i)).getValueAtTheBeginning();
            if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(BigInteger.TEN) >= 0) {
                sb.append("?");
            } else {
                sb.append(value);
            }
        }
        return sb.append(")").toString();
    }
    
    public void pauseStart(boolean userCode, boolean paintFreeRod, int delayTime) {
        hanoiFrame.animateMove(src - 1, dest - 1, !userCode, paintFreeRod, delayTime);
        pauseStarted = userCode;
    }
    
    public void pauseStop() {
        if (pauseStarted) {
            hanoiFrame.updateAfterAnimate(dest - 1);
            pauseStarted = false;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "przenies";
    }
    //</editor-fold>
    
}
