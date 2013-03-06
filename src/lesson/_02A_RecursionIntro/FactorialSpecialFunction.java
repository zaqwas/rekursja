package lesson._02A_RecursionIntro;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import syntax.function.SpecialFunctionBehavior;

class FactorialSpecialFunction extends SpecialFunctionBehavior {
    
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
        int value = ((ArgInteger)instance.getArgument(0)).getValueAtTheBeginning().intValue();
        return "silnia(" + value + ")";
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "silnia";
    }
    //</editor-fold>
    
}
