package lesson._04_HanoiTower;

import console.Console;
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

    private Console console;
    private HanoiFrame hanoiFrame;
    private String error = "Funkcja „hanoi” nie została zaimlementowana prawidłowo "
            + "(więcej informacji zostało wyświetlone w konsoli)";
    
    public CheckSpecialFunction(HanoiFrame hanoiFrame, Console console) {
        this.hanoiFrame = hanoiFrame;
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
        
        int src = hanoiFrame.getStartRod();
        int dest = hanoiFrame.getFinishRod();
        int free = 3 - src - dest;
        
        if (!hanoiFrame.isStackEmpty(src) && !hanoiFrame.isStackEmpty(free)) {
            console.append("Na słupkach " + (src + 1) + " oraz " + (free + 1) + 
                    "\nznadjują się nieprzeniesione krążki.\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        if (!hanoiFrame.isStackEmpty(src)) {
            console.append("Na słupku " + (src + 1) + " znadjują się nieprzeniesione krążki.\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        if (!hanoiFrame.isStackEmpty(free)) {
            console.append("Na słupku " + (free + 1) + " znadjują się nieprzeniesione krążki.\n");
            throw new ProgramError(error, call.getLeftIndex(), call.getRightIndex());
        }
        
        console.append("Wieże Hanoi zostały poprawnie przeniesione.\n");
        
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
        String str = "Wieże Hanoi zostały poprawnie przeniesione";
        return new SimpleLazyStringCreator(str);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "sprawdz";
    }
    //</editor-fold>
    
}
