package lesson._04_HanoiTower;

import lesson._03C_BinarySearch.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class HanoiSpecialFunction extends SpecialFunctionBehavior {
    
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
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        StringBuilder sb = new StringBuilder("h(");
        for (int i = 0; i < 3; i++) {
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

    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "hanoi";
    }
    //</editor-fold>
    
}
