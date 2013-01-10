package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.math.BigInteger;
import parser.ProgramError;
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
    public SyntaxNode commit(Instance instance) throws ProgramError {
        if (variable.hasArrayIndex()) {
            BigInteger value = instance.popStack();
            BigInteger index = instance.popStack();
            AccessArray access = (AccessArray) variable.getAccessVar();
            access.checkIndex(instance, index, variable.arrayIndex);
            if (operationType != OperationType.NOTHING) {
                BigInteger val = access.getValue(instance, index);
                variable.checkInitializedArray(val, index);
                value = compute(val, value);
            }
            checkProperValue(value);
            access.setValue(instance, index, value);
        } else {
            BigInteger value = instance.popStack();
            AccessInteger access = (AccessInteger) variable.getAccessVar();
            if (operationType != OperationType.NOTHING) {
                BigInteger val = access.getValue(instance);
                variable.checkInitialized(val);
                value = compute(val, value);
            }
            checkProperValue(value);
            access.setValue(instance, value);
        }
        return jump;
    }
    
    private BigInteger compute(BigInteger v1, BigInteger v2) throws ProgramError {
        try {
            return operationType.eval(v1, v2);
        } catch (ProgramError pe) {
            pe.setIndexes(getLeftIndex(), getRightIndex());
            throw pe;
        }
    }
    private void checkProperValue(BigInteger value) throws ProgramError {
        String error = null;
        if (value.compareTo(MAX_VALUE) > 0) {
            error = Lang.exceedMaxValue;
        }
        if (value.compareTo(MIN_VALUE) < 0) {
            error = Lang.exceedMinValue;
        }
        if (error != null) {
            SyntaxNodeIdx sn = operationType == OperationType.NOTHING ? expresion : this;
            throw new ProgramError(error, sn.getLeftIndex(), sn.getRightIndex());
        }
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
    
    @Override
    public void printDebug() {
        System.out.println("Assigment: " + operationType);
        variable.printDebug();
        expresion.printDebug();
    }
}
