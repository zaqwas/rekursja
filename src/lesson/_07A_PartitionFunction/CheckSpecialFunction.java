package lesson._07A_PartitionFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import java.math.BigInteger;
import mainclass.MainClass;
import parser.ProgramError;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class CheckSpecialFunction extends SpecialFunctionBehavior {

    private MainClass mainClass;
    private ArrayFrame arrayFrame;
    private String error = "Funkcja „podziel” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    public CheckSpecialFunction(MainClass mainClass, ArrayFrame arrayFrame) {
        this.arrayFrame = arrayFrame;
        this.mainClass = mainClass;
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
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „sprawdz” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        int size = arrayFrame.getArraySize();
        for (int i = 0; i < size; i++) {
            if (!arrayFrame.isResultValueSet(i)) {
                mainClass.getConsole().append("Tablica wynikowa nie została w całości wypełniona.\n"
                        + "Nie został wypełniony element o indeksie " + i + ".\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
        }

        int idx = 0, pivot = arrayFrame.getPivot();
        while (idx < size && arrayFrame.getArrayResultValue(idx) < pivot) {
            idx++;
        }
        int properIdx1 = idx;
        BigInteger properIdx1Big = BigInteger.valueOf(idx);
        while (idx < size && arrayFrame.getArrayResultValue(idx) <= pivot) {
            if (arrayFrame.getArrayResultValue(idx) < pivot) {
                mainClass.getConsole().append("Tablica liczb nie została poprawnie podzielona.\n"
                        + "Element o indeksie " + idx + " jest mniejszy od „wartości osiowej”, jednak\n"
                        + "znajduje się za elementami równymi „wartości osiowej”.\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
            idx++;
        }
        int properIdx2 = idx;
        BigInteger properIdx2Big = BigInteger.valueOf(idx);
        while (idx < size) {
            if (arrayFrame.getArrayResultValue(idx) <= pivot) {
                String cmpStr = arrayFrame.getArrayResultValue(idx) == pivot ? "równy" : "mniejszy od";
                mainClass.getConsole().append("Tablica liczb nie została poprawnie podzielona\n"
                        + "Element o indeksie " + idx + " jest " + cmpStr + " „wartości osiowej”, jednak\n"
                        + "znajduje się za elementami większymi od „wartości osiowej”.\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
            idx++;
        }

        BigInteger idx1 = ((ArgInteger) instance.getArgument(0)).getValue();
        BigInteger idx2 = ((ArgInteger) instance.getArgument(1)).getValue();

        if (properIdx1Big.compareTo(idx1) == 0 && properIdx2Big.compareTo(idx2) == 0) {
            mainClass.getConsole().append("Tablica liczb została poprawnie podzielona\n"
                    + "i funkcja „podziel” zwróciła poprawne indeksy:\n"
                    + "idx1 = " + properIdx1 + "\n"
                    + "idx2 = " + properIdx2 + "\n");
        } else {
            mainClass.getConsole().append("Tablica liczb została poprawnie podzielona, jednak\n"
                    + "funkcja „podziel” zwróciła niepoprawne indeksy:\n"
                    + "Zwrócona wartość idx1 = " + idx1 + "\n"
                    + "Poprawna wartość idx1 = " + properIdx1 + "\n"
                    + "Zwrócona wartość idx2 = " + idx2 + "\n"
                    + "Poprawna wartość idx2 = " + properIdx2 + "\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
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
        String str = "Tablica liczb została poprawnie podzielona "
                + "i funkcja „podziel” zwróciła poprawne indeksy";
        return new SimpleLazyStringCreator(str);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
