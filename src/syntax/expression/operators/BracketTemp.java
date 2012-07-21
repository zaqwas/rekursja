package syntax.expression.operators;

import interpreter.Instance;
import java.math.BigInteger;
import syntax.SyntaxNode;

public class BracketTemp extends Operation {
    
    public BracketTemp( int indexL ) {
        //this.type = 2;
        this.operationType = OperationType.BRACKET_TEMP;
        this.indexL = indexL;
    }

//    @Override
//    public SyntaxNode commit(Instance instance) {
//        throw new UnsupportedOperationException("Not supported");
//    }
    
    @Override
    protected BigInteger getValue(Instance instance) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public void printDebug() {
        System.out.println("BracketTemp");
    }
    
}
