package syntax.statement;

import java.util.ArrayList;
import syntax.SyntaxNode;

public class While extends SyntaxNodeCond {
    public boolean doWhile;
    
    public While ( boolean doWhile ) {
        //this.type = 11;
        this.doWhile = doWhile;
        this.statements = new ArrayList<SyntaxNode>();
    }
    
    @Override
    public void printDebug() {
        if (doWhile) {
            System.out.println("do while");
            SyntaxNode.printDebugList(statements);
            condition.printDebug();
        } else {
            System.out.println("while");
            condition.printDebug();
            SyntaxNode.printDebugList(statements);
        }
    }
    
    @Override
    protected String getNameOfInstruction() {
        return doWhile ? "Pętla do...while: " : "Pętla while: ";
    }
}
