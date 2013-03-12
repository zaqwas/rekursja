package lesson._02B_ArithmeticSeries;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import syntax.function.SpecialFunctionBehavior;

class ArithmeticSeriesSpecialFunction extends SpecialFunctionBehavior {
    
    private byte selectedPart = 1;
    private BigInteger part1Max = BigInteger.TEN.pow(3);
    private BigInteger part1Min = BigInteger.TEN.pow(2).negate();
    private BigInteger part2Max = BigInteger.TEN.pow(11);
    private BigInteger part2Min = BigInteger.TEN.pow(10).negate();
    
    
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
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        BigInteger value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning();
        String valueStr;
        if (selectedPart == 1) {
            valueStr = value.compareTo(part1Max) >= 0 || value.compareTo(part1Min) <= 0 ? "???"
                    : value.toString();
        } else {
            valueStr = value.compareTo(part2Max) >= 0 || value.compareTo(part2Min) <= 0 ? "???????????"
                    : value.toString();
        }
        return "suma(" + valueStr + ")";
    }

    public void setSelectedPart(byte selectedPart) {
        this.selectedPart = selectedPart;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sumaCiagu";
    }
    //</editor-fold>
    
}
