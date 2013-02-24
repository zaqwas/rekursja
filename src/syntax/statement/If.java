package syntax.statement;

import java.util.ArrayList;
import syntax.SyntaxNode;

public class If extends SyntaxNodeCond {
    public ArrayList<SyntaxNode> statementsElse;
    
    public If () {
        //this.type = 10;
        this.statements = new ArrayList<SyntaxNode>();
        this.statementsElse = new ArrayList<SyntaxNode>();
    }
    
    @Override
    public void printDebug() {
        System.out.println("If");
        condition.printDebug();
        SyntaxNode.printDebugList(statements);
        SyntaxNode.printDebugList(statementsElse);
    }
    
    
    @Override
    protected String getNameOfInstruction() {
        return "Instrukcja warunkowa: ";
    }
}
