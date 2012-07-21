package syntax.expression;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.ArgString;
import java.math.BigInteger;
import syntax.SyntaxNode;

public class StrParam extends SyntaxNodeExpr {
    
    private String string;    
    
    public StrParam(String string, int indexL, int indexR) {
        //this.type = 6;
        this.string = string;
        this.indexL = indexL;
        this.indexR = indexR;
    }
    
    
    
    @Override
    protected BigInteger getValue(Instance instance) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public void thisNodeIsParameter(final int paramIndex, VariableType type) {
        if ( paramIndex<0 ) {
            setCommitter(new Committer() {
                private int size = - paramIndex;
                @Override
                public SyntaxNode commit(Instance instance) {
                    instance.pushArgsArray(size);
                    instance.setArgument(0, new ArgString(string));
                    return jump;
                }
            });
            return;
        }
        setCommitter(new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                instance.setArgument(paramIndex, new ArgString(string));
                return jump;
            }
        });
    }
    
    
    @Override
    public void printDebug() {
        System.out.println("String param: " + string);
    }

}
