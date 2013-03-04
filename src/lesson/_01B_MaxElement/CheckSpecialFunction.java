package lesson._01B_MaxElement;

import console.Console;
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

    private String error = "Funkcja „maxElement” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    private Console console;        
    private ArrayFrame arrayFrame;
    
    private int index;
    
    public CheckSpecialFunction(ArrayFrame arrayFrame, Console console) {
        this.arrayFrame = arrayFrame;
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
        
        BigInteger idx = ((ArgInteger) instance.getArgument(0)).getValue();
        BigInteger size = arrayFrame.getArraySizeBigInt();
        int properIdx = arrayFrame.getIndexMaxElement();
        
        if (idx.signum() < 0 || idx.compareTo(size) >= 0) {
            console.append("Funkcja „maxElement” zwróciła indeks spoza tablicy.\n"
                    + "Zwrócona wartość idxMax = " + idx + "\n"
                    + "Poprawna wartość idxMax = " + properIdx + "\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        
        index = idx.intValue(); 
        
        if (properIdx != index) {
            if (arrayFrame.getArrayValue(index) == arrayFrame.getArrayValue(properIdx)) {
                console.append("Funkcja „podziel” zwróciła indeks elementu,\n"
                        + "króry nie należy do elementu maksymalnego.\n"
                        + "Zwrócona wartość idxMax = " + index + "\n"
                        + "Poprawna wartość idxMax = " + properIdx + "\n");
            } else {
                console.append(
                        "Funkcja „podziel” zwróciła indeks elementu,\n"
                        + "króry należy do elementu maksymalnego.\n"
                        + "Jednak istnieje inny element\n"
                        + "maksymalny o mniejszym indeksie.\n"
                        + "Zwrócona wartość idxMax = " + index + "\n"
                        + "Poprawna wartość idxMax = " + properIdx + "\n");
            }

            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }

        console.append("Funkcja „maxElement” zwróciła poprawny indeks:\n"
                    + "idxMax = " + properIdx + "\n");
        
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
