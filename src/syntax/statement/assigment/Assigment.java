package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.awt.FontMetrics;
import java.math.BigInteger;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;
import syntax.expression.SyntaxNodeExpr;
import syntax.expression.Variable;
import syntax.expression.operators.OperationType;

public class Assigment extends SyntaxNodeIdx {

    public Variable variable;
    public SyntaxNodeExpr expresion;
    public OperationType operationType;

    public Assigment(OperationType operationType, Variable variable,
            SyntaxNodeExpr expresion, int indexL, int indexR) {
        this.operationType = operationType;
        this.variable = variable;
        this.expresion = expresion;
        this.indexL = indexL;
        this.indexR = indexR;
    }
    
    public OperationType getOperationType() {
        return operationType;
    }
    
    @Override
    public SyntaxNode commit(Instance instance) {
        BigInteger result = eval(instance);
        if ( !variable.hasArrayIndex() ) {
            ((AccessInteger) variable.getAccessVar()).setValue(instance, result);
            return jump;
        }
        BigInteger idx = instance.popStack();
        AccessArray arr = (AccessArray) variable.getAccessVar();
        int idxErr = arr.checkIndex(instance, idx);
        if ( idxErr!=0 ) {
            //TODO check size
            throw new RuntimeException();
        }
        arr.setValue(instance, idx, result);
        return jump;
    }

    @Override
    public boolean isStopNode() {
        return true;
    }
    
    @Override
    public boolean isStatisticsNode() {
        return true;
    }
    
    @Override
    public StringCreator getStatusCreator(Instance instance) {
        //TODO array
//        BigInteger value = instance.stack.pop(), result = value;
//        if (operationType != OperationType.NOTHING) {
//            if (operationType.compareTo(OperationType.AND) >= 0) {
//                result = operationEval.eval(value);
//            } else {
//                result = operationEval.eval(instance.stack.peek(), value);
//            }
//        }
//        instance.stack.push(value);
//        final BigInteger val = result;
        return new StringCreator() {
            @Override
            public String getString(int maxWidth) {
                assert fontMetrics!=null : "FontMetrics not initialized.";
                //return "Przypisanie:  " + variable.name + " = " + val;
                return "Przypisanie";
            }
        };
    }
    
    private BigInteger eval(Instance instance) {
        if ( operationType==OperationType.NOTHING ) {
            return instance.popStack();
        }
        if ( operationType.isLogicOperation() ) {
            BigInteger value = instance.popStack();
            return operationType.eval(value);
        }
        BigInteger rightValue = instance.popStack();
        BigInteger leftValue = instance.popStack();
        return operationType.eval(leftValue, rightValue);
    }

    @Override
    public void printDebug() {
        System.out.println("Assigment: " + operationType);
        variable.printDebug();
        expresion.printDebug();
    }
}
