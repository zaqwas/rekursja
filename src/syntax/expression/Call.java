package syntax.expression;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.Argument;
import java.math.BigInteger;
import java.util.ArrayList;
import syntax.function.Function;
import syntax.SyntaxNode;
import syntax.function.FunctionDeclaration;

public class Call extends SyntaxNodeExpr implements FunctionDeclaration {

    public String name;
    public boolean callInExpr;
    public ArrayList<SyntaxNodeExpr> params;
    int paramsLength;
    public Function function;

    public Call(String name, int indexL, boolean callInExpr) {
        this.params = new ArrayList<SyntaxNodeExpr>();
        this.name = name;
        this.indexL = indexL;
        this.callInExpr = callInExpr;
        
        setCommitter(new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                return jump;
            }
        });
    }
    
    public Function getFunction() {
        return function;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Interface FunctionDeclaration">
    @Override
    public String getName() {
        return name;
    }
    @Override
    public int getArgumentsLength() {
        return params.size();
    }
    @Override
    //Returns possibly type
    public VariableType getArgumentType(int index) {
        SyntaxNode param = params.get(index);
        if ( param instanceof Variable ) {
            return ((Variable) param).getPossiblyType();
        }
        return VariableType.INTEGER;
    }
    //</editor-fold>
    
    @Override
    protected BigInteger getValue(Instance instance) {
        return instance.popStack();
    }
    
    @Override
    public void printDebug() {
        System.out.println("Call: " + name);
        for (SyntaxNodeExpr sn:params) {
            sn.printDebug();
        }
    }
}
