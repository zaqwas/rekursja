package lesson._03C_BinarySearch;

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

    private String error = "Funkcja „wyszukaj” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    private ArrayFrame arrayFrame;
    private int selectedPart = 1;
    private Console console;     
    
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
        
        int size = arrayFrame.getArraySize();
        int searchingValue = arrayFrame.getSearchingValue();
        
        String arguments;
        if (selectedPart == 1) {
            arguments = Integer.toString(size);
        } else {
            arguments = "0, " + size;
        }
        
        int properValue = -1;
        for(int i=0; i<size; i++) {
            if (searchingValue == arrayFrame.getArrayValue(i)) {
                properValue = i;
                break;
            }
        }
        
        BigInteger properBigInt = BigInteger.valueOf(properValue);
        BigInteger userValueBigInt = ((ArgInteger) instance.getArgument(0)).getValue();
        if (selectedPart == 2 && properValue != -1) {
            int idx = properValue + 1;
            while (idx < size && searchingValue == arrayFrame.getArrayValue(idx)) {
                idx++;
            }
            int properValue2 = idx - 1;
            BigInteger properBigInt2 = BigInteger.valueOf(properValue2);
            
            if (userValueBigInt.compareTo(properBigInt) < 0
                    || userValueBigInt.compareTo(properBigInt2) > 0) {

                console.append("Funkcja „wyszukaj” zwróciła niepoprawny wynik.\n"
                        + "Zwrócona wartość wyszukaj(" + arguments + ") = " + userValueBigInt + "\n"
                        + "Funkcja „wyszukaj” powinna zwrócić wartość z przedziału ["
                        + properBigInt + ", " + properBigInt2 + "]\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }            
        } else {
            if (properBigInt.compareTo(userValueBigInt) != 0) {
                console.append("Funkcja „wyszukaj” zwróciła niepoprawny wynik.\n"
                        + "Zwrócona wartość wyszukaj(" + arguments + ") = " + userValueBigInt + "\n"
                        + "Poprawna wartość wyszukaj(" + arguments + ") = " + properValue + "\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
        }
        
        console.append("Funkcja „wyszukaj” zwróciła poprawny wynik.\n"
                    + "wyszukaj(" + arguments + ") = " + properValue + "\n");
        
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
        String str = "Funkcja „wyszukaj” zwróciła poprawny wynik";
        return new SimpleLazyStringCreator(str);
    }

    
    public void setSelectedPart(int selectedPart) {
        this.selectedPart = selectedPart;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
