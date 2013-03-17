package lesson._03C_BinarySearch;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class SearchSpecialFunction extends SpecialFunctionBehavior {
    
    private int selectedPart = 1;
    private BigInteger max = BigInteger.TEN.pow(2);
    private BigInteger min = BigInteger.TEN.negate();
    
    @Override
    public String getName() {
        return Lang.functionName;
    }
    @Override
    public int getArgumentsLength() {
        return selectedPart;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.INTEGER;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        int argsLength = instance.getArgumentsLength();
        StringBuilder sb = new StringBuilder(argsLength == 1 ? "wys(" : "w(");
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        sb.append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0
                ? "??" : value.toString());
        if (argsLength == 2) {
            value = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
            sb.append(",").append(value.compareTo(max) >= 0 || value.compareTo(min) <= 0
                    ? "??" : value.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    
    public void setSelectedPart(int selectedPart) {
        this.selectedPart = selectedPart;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "wyszukaj";
    }
    //</editor-fold>
    
}
