package syntax.statement;

import java.util.ArrayList;
import syntax.SyntaxNode;

public class For extends SyntaxNodeCond {
    public ArrayList<SyntaxNode> statementsInit;
    public ArrayList<SyntaxNode> statementsEnd;
    
    public For() {
        //this.type = 9;
        this.statementsInit = new ArrayList<SyntaxNode>();
        this.statementsEnd = new ArrayList<SyntaxNode>();
        this.statements = new ArrayList<SyntaxNode>();
    }
    
    @Override
    public void printDebug() {
        System.out.println("For");
        SyntaxNode.printDebugList(statementsInit);
        condition.printDebug();
        SyntaxNode.printDebugList(statementsEnd);
        SyntaxNode.printDebugList(statements);
    }

    @Override
    protected String getNameOfInstruction() {
        return "Pętla for: ";
    }
}
