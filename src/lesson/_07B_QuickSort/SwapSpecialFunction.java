package lesson._07B_QuickSort;

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

class SwapSpecialFunction extends SpecialFunctionBehavior {

    private ArrayFrame arrayFrame;
    private int lastIndex1, lastIndex2, lastIndex3;
    
    public SwapSpecialFunction(ArrayFrame arrayFrame) {
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
        lastIndex1 = ((ArgInteger)instance.getArgument(0)).getValue().intValue();
        lastIndex2 = ((ArgInteger)instance.getArgument(1)).getValue().intValue();
        lastIndex3 = ((ArgInteger)instance.getParentInstance().getArgument(1)).getValue().intValue();
        
        arrayFrame.swap(lastIndex1, lastIndex2);
        
        return null;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        int idx1 = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning().intValue();
        int idx2 = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning().intValue();
        StringBuilder sb = new StringBuilder();
        
        sb.append("Zamien(");
        sb.append(idx1);
        if (idx1 < 10) {
            sb.append(" ");
        }
        sb.append(",");
        sb.append(idx2);
        if (idx1 < 10) {
            sb.append(" ");
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
        String str = "Zamień elementy o indeksach: " + lastIndex1 + " oraz " + lastIndex2;
        return new SimpleLazyStringCreator(str);
    }
    
    public void pauseStart(int delayTime) {
        arrayFrame.animateSwap(lastIndex1, lastIndex2, lastIndex3, delayTime);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "zamien";
    }
    //</editor-fold>
    
}
