package lesson._08_Fibonacci;

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
    
    //<editor-fold defaultstate="collapsed" desc="Fibonacci functions">
    private static class FibonacciPair {

        public final BigInteger f1;
        public final BigInteger f2;

        public FibonacciPair(BigInteger f1, BigInteger f2) {
            this.f1 = f1;
            this.f2 = f2;
        }
    }
    
    private static FibonacciPair fibonacci(int n) {
        BigInteger f1 = BigInteger.ZERO;
        BigInteger f2 = BigInteger.ONE;
        BigInteger a1 = BigInteger.ONE;
        BigInteger a2 = BigInteger.ZERO;
        while (n > 0) {
            if ((n & 1) != 0) {
                BigInteger tmp = f1.multiply(a1).add(f2.multiply(a2));
                f1 = f1.multiply(a1.add(a2)).add(f2.multiply(a1));
                f2 = tmp;
            }
            BigInteger tmp = a1.multiply(a1).add(a2.multiply(a2));
            a1 = a1.multiply(a1.add(a2)).add(a2.multiply(a1));
            a2 = tmp;
            n >>= 1;
        }
        return new FibonacciPair(f1, f2);
    }
    //</editor-fold>

    private String error = "Funkcja „fibonacci” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    private boolean part4;
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
        return part4 ? 2 : 1;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.INTEGER;
    }
    
    //<editor-fold defaultstate="collapsed" desc="check">
    private void check(Instance instance, Call call, FibonacciPair pair, int n) throws ProgramError {
        BigInteger userValue = ((ArgInteger) instance.getArgument(0)).getValue();

        if (userValue.compareTo(pair.f1) != 0) {
            StringBuilder sb = new StringBuilder()
                    .append("Funkcja „fibonacci” zwróciła niepoprawny wynik.\n")
                    .append("Zwrócona wartość: fibonacci(").append(n)
                    .append(") = ").append(userValue).append("\n")
                    .append("Poprawna wartość: fibonacci(").append(n)
                    .append(") = ").append(pair.f1).append("\n");

            console.append(sb.toString());
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }

        StringBuilder sb = new StringBuilder()
                    .append("Funkcja „fibonacci” zwróciła poprawny wynik.\n")
                    .append("fibonacci(").append(n)
                    .append(") = ").append(pair.f1).append("\n");

        console.append(sb.toString());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="checkPart4">
    private void checkPart4(Instance instance, Call call, FibonacciPair pair, int n) throws ProgramError {
        BigInteger userValueF1 = ((ArgInteger) instance.getArgument(0)).getValue();
        BigInteger userValueF2 = ((ArgInteger) instance.getArgument(1)).getValue();

        if (userValueF1.compareTo(pair.f1) != 0) {
            StringBuilder sb = new StringBuilder()
                    .append("Funkcja „fibonacci” zwróciła niepoprawny wynik.\n")
                    .append("Zwrócona wartość: f1 = F(").append(n)
                    .append(") = ").append(userValueF1).append("\n")
                    .append("Poprawna wartość: f1 = F(").append(n)
                    .append(") = ").append(pair.f1).append("\n");

            sb.append("Zwrócona wartość: f2 = ");
            if (n > 0) {
                sb.append("F(").append(n - 1).append(") = ");
            }
            sb.append(userValueF2).append("\n").append("Poprawna wartość: f2 = ");
            if (n > 0) {
                sb.append("F(").append(n - 1).append(") = ");
            }
            sb.append(pair.f2).append("\n");

            console.append(sb.toString());
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }

        StringBuilder sb = new StringBuilder()
                .append("Funkcja „fibonacci” zwróciła poprawny wynik.\n")
                .append("f1 = F(").append(n).append(") = ").append(userValueF1).append("\n");

        sb.append("f2 = ");
        if (n > 0) {
            sb.append("F(").append(n - 1).append(") = ");
        }
        sb.append(userValueF2).append("\n");

        console.append(sb.toString());
    }
    //</editor-fold>

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „sprawdz” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        int n = startSpecialFunction.getValueN();
        FibonacciPair pair = fibonacci(n);
        
        if (part4) {
            checkPart4(instance, call, pair, n);
        } else {
            check(instance, call, pair, n);
        }
        
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
        String str = "Funkcja „fibonacci” zwróciła poprawny wynik";
        return new SimpleLazyStringCreator(str);
    }
    
    public void setSelectedPart(int part) {
        this.part4 = part == 4;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
