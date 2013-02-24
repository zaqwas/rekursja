package lesson._06A_MergeFunction;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
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
    private String error = "Funkcja „scal” nie została zaimlementowana prawidłowo "
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
        
        int size = arrayFrame.getArrayResultSize();
        for (int i = 0; i < size; i++) {
            if (!arrayFrame.isResultValueSet(i)) {
                mainClass.getConsole().append("Tablica wynikowa nie została w całości wypełniona.\n"
                        + "Nie został wypełniony element o indeksie " + i + ".\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
        }

        for (int i = 0; i < size-1; i++) {
            if (arrayFrame.getArrayReslutValue(i) > arrayFrame.getArrayReslutValue(i+1)) {
                mainClass.getConsole().append("Tablica wynikowa nie jest właściwie posortowana.\n"
                        + "Element o indeksie " + i + " jest\n"
                        + "większy niż element o indeksie " + (i+1) + ".\n");
                throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
            }
        }
        
        mainClass.getConsole().append("Tablice liczb zostały poprawnie scalone.\n");
        
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
        String str = "Tablice liczb zostały poprawnie scalone";
        return new SimpleLazyStringCreator(str);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
