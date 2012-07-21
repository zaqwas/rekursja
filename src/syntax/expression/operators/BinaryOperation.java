package syntax.expression.operators;

import interpreter.Instance;
import java.math.BigInteger;
import syntax.expression.SyntaxNodeExpr;

public class BinaryOperation extends Operation {
    
    public SyntaxNodeExpr expresionL;
    public SyntaxNodeExpr expresionR;
    
    public BinaryOperation(OperationType operationType) {
        this.operationType = operationType;
        
        setCommitter(null);
    }
    
    @Override
    public void printDebug() {
        System.out.println("BinarOper: " + operationType);
        expresionL.printDebug();
        expresionR.printDebug();
    }
    
    @Override
    protected BigInteger getValue(Instance instance) {
        if ( operationType.isLogicOperation() ) {
            BigInteger value = instance.popStack();
            return operationType.eval(value);
        }
        BigInteger rightValue = instance.popStack();
        BigInteger leftValue = instance.popStack();
        return operationType.eval(leftValue, rightValue);
    }
}
