package syntax.expression.operators;

import interpreter.Instance;
import java.math.BigInteger;
import parser.ProgramError;
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
    protected BigInteger getValue(Instance instance) throws ProgramError {
        if ( operationType.isLogicOperation() ) {
            BigInteger value = instance.popStack();
            return operationType.eval(value);
        }
        BigInteger rightValue = instance.popStack();
        BigInteger leftValue = instance.popStack();
        BigInteger value;
        try {
            value = operationType.eval(leftValue, rightValue);
        } catch (ProgramError pe) {
            pe.setIndexes(indexL, indexR);
            throw pe;
        }
        return value;
    }
}
