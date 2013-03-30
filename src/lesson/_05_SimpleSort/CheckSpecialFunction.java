package lesson._05_SimpleSort;

import console.Console;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import parser.ProgramError;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class CheckSpecialFunction extends SpecialFunctionBehavior {

    private Console console;
    private ArrayFrame arrayFrame;
    private String error = "Tablica liczb nie została prawidłowo uporządkowana "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
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
        return 0;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return null;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „sprawdz” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        int idx = arrayFrame.isSorted();
        if (idx != -1) {
            console.append("Tablica liczb nie została prawidłowo uporządkowana.\n"
                    + "Element o indeksie " + idx + " jest\n"
                    + "większy niż element o indeksie " + (idx+1) + ".\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        
        console.append("Tablica liczb została poprawnie uporządkowana.\n");
        
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
        String str = "Tablica liczb została poprawnie uporządkowana";
        return new SimpleLazyStringCreator(str);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
