package lesson._03A_Exponentiation;

import console.Console;
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

class CheckSpecialFunction extends SpecialFunctionBehavior {

    private String error = "Funkcja „potega” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    private StartSpecialFunction startSpecialFunction;
    private Console console;        
    
    public CheckSpecialFunction(StartSpecialFunction startSpecialFunction, Console console) {
        this.startSpecialFunction = startSpecialFunction;
        this.console = console;
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
        return VariableType.INTEGER;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „sprawdz” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        int valueA = startSpecialFunction.getValueA();
        int valueN = startSpecialFunction.getValueN();
        BigInteger properValueBigInt = BigInteger.valueOf(valueA).pow(valueN);
        BigInteger userValueBigInteger = ((ArgInteger) instance.getArgument(0)).getValue();
        
        if (userValueBigInteger.compareTo(properValueBigInt) != 0) {
            console.append("Funkcja „potega” zwróciła nie poprawny wynik.\n"
                    + "Zwrócona wartość potega(" + valueA + ", " + valueN + ") = " + userValueBigInteger + "\n"
                    + "Poprawna wartość potega(" + valueA + ", " + valueN + ") = " + properValueBigInt + "\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        
        console.append("Funkcja „potega” zwróciła poprawny wynik.\n"
                    + "potega(" + valueA + ", " + valueN + ") = " + properValueBigInt + "\n");
        
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
        String str = "Funkcja „potega” zwróciła poprawny wynik";
        return new SimpleLazyStringCreator(str);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
