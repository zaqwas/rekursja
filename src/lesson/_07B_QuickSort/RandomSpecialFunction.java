package lesson._07B_QuickSort;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import java.util.Random;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class RandomSpecialFunction extends SpecialFunctionBehavior {
    
    Random rand = new Random();
    
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
        return VariableType.INTEGER;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        int n = ((ArgInteger)instance.getArgument(0)).getValue().intValue();
        
        instance.setReturnedValue(BigInteger.valueOf(rand.nextInt(n)));
        
        return null;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        int idx = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning().intValue();
        BigInteger r = instance.getReturnedValue();
        StringBuilder sb = new StringBuilder();
        sb.append("Losuj(");
        sb.append(idx);
        if (idx < 10) {
            sb.append(" ");
        }
        sb.append("):");
        sb.append(r.toString());
        return sb.toString();
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
        public static final String functionName = "losuj";
    }
    //</editor-fold>
    
}
