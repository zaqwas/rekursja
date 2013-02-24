package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.math.BigInteger;
import parser.ProgramError;
import stringcreator.FlexibleStringCreator;
import stringcreator.StringCreator;
import sun.security.util.BigInt;
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
        FlexibleStringCreator strCreator = new FlexibleStringCreator();
        strCreator.addString("Przypisanie: ");
        strCreator.addStringToExtend(variable.getName(), 10, true, 2);

        if (variable.hasArrayIndex()) {
            BigInteger value = instance.popStack();
            BigInteger index = instance.peekStack();
            instance.pushStack(value);
            AccessArray access = (AccessArray) variable.getAccessVar();
            if (index == null || !access.checkIndex(instance, index)) {
                return strCreator;
            }
            strCreator.addString("[" + index.toString() + "] = ");

            if (operationType == OperationType.NOTHING) {
                strCreator.addBigIntegerToExtend(value, 1, 2);
            } else {
                BigInteger prevValue = access.getValue(instance, index);
                statusCreatorEvaluator(strCreator, prevValue, value);
            }
            return strCreator;
        }

        strCreator.addString(" = ");
        if (operationType == OperationType.NOTHING) {
            BigInteger value = instance.peekStack();
            strCreator.addBigIntegerToExtend(value, 1, 2);
        } else {
            BigInteger value = instance.peekStack();
            AccessInteger access = (AccessInteger) variable.getAccessVar();
            BigInteger prevValue = access.getValue(instance);
            statusCreatorEvaluator(strCreator, prevValue, value);
        }
        return strCreator;
    }
    
    private void statusCreatorEvaluator(FlexibleStringCreator strCreator,
            BigInteger prevValue, BigInteger value) {
        
        if (prevValue == null || value == null) {
            return;
        }
        
        BigInteger evalValue;
        try {
            evalValue = operationType.eval(prevValue, value);
        } catch (ProgramError pe) {
            return;
        }
        
        strCreator.addBigIntegerToExtend(evalValue, 1, 2);
        strCreator.addString(" (");
        strCreator.addBigIntegerToExtend(prevValue, 1, 1);
        strCreator.addString(" " + operationType.toString() + " ");
        strCreator.addBigIntegerToExtend(prevValue, 1, 1);
        strCreator.addString(")");
    }
    
    @Override
    public void printDebug() {
        System.out.println("Assigment: " + operationType);
        variable.printDebug();
        expresion.printDebug();
    }
}
