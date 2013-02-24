package syntax.statement.other;

import interpreter.Instance;
import stringcreator.FlexibleStringCreator;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;

public class BreakContinue extends SyntaxNodeIdx {
    public boolean breakBool;
    public BreakContinue(boolean breakBool, int indexL, int indexR) {
        this.breakBool = breakBool;
        this.indexL = indexL;
        this.indexR = indexR;
    }

    @Override
    public SyntaxNode commit(Instance instance) {
        return jump;
    }
    
    @Override
    public boolean isStopNode() {
        return true;
    }
    
    @Override
    public StringCreator getStatusCreator(Instance instance) {
        return new SimpleLazyStringCreator(
                breakBool ? "Przerwij działanie pętli" : "Kontunuuj działanie pętli");
    }

    @Override
    public void printDebug() {
        System.out.println(breakBool ? "break" : "continue");
    }
}
