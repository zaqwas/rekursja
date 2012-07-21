package syntax.statement.other;

import interpreter.Instance;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;

public class BreakContinue extends SyntaxNodeIdx {
    public boolean breakBool;
    public BreakContinue(boolean breakBool, int indexL) {
        this.breakBool = breakBool;
        this.indexL = indexL;
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
    public void printDebug() {
        System.out.println(breakBool?"break":"continue");
    }
}
