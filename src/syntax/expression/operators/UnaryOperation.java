package syntax.expression.operators;

import interpreter.Instance;
import java.math.BigInteger;
import syntax.expression.SyntaxNodeExpr;

public class UnaryOperation extends Operation {

    public SyntaxNodeExpr expresion;

    public UnaryOperation(OperationType operationType, int indexL) {
        this.operationType = operationType;
        this.indexL = indexL;
        
        setCommitter(null);
    }
    
    @Override
    public void printDebug() {
        System.out.println("UnarOper: " + operationType);
        expresion.printDebug();
    }
    
    @Override
    protected BigInteger getValue(Instance instance) {
        BigInteger value = instance.popStack();
        return operationType.eval(value);
    }
}
