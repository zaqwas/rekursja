package lesson._03A_Exponentiation;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class ExponentiationSpecialFunction extends SpecialFunctionBehavior {
    
    private byte selectedPart = 1;
    private BigInteger part1Max = BigInteger.TEN.pow(2);
    private BigInteger part1Min = BigInteger.TEN.pow(1).negate();
    private BigInteger part2Max = BigInteger.TEN.pow(3);
    private BigInteger part2Min = BigInteger.TEN.pow(2).negate();
    
    
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
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger valueA = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        BigInteger valueN = ((ArgInteger)instance.getArgument(1)).getValueAtTheBeginning();
        String valueAStr = valueA.compareTo(part1Max) >= 0 || valueA.compareTo(part1Min) <= 0 ? "??"
                    : valueA.toString();
        String valueNStr;
        if (selectedPart == 1) {
            valueNStr = valueN.compareTo(part1Max) >= 0 || valueN.compareTo(part1Min) <= 0 ? "??"
                    : valueN.toString();
        } else {
            valueNStr = valueN.compareTo(part2Max) >= 0 || valueN.compareTo(part2Min) <= 0 ? "???"
                    : valueN.toString();
        }
        return "potega(" + valueAStr + "," + valueNStr + ")";
    }

    public void setSelectedPart(byte selectedPart) {
        this.selectedPart = selectedPart;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "potega";
    }
    //</editor-fold>
    
}
