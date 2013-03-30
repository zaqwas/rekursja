package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.math.BigInteger;
import parser.ProgramError;
import stringcreator.FlexibleStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;
import syntax.expression.Variable;
import syntax.expression.operators.OperationType;

public class IncDec extends SyntaxNodeIdx {
    public Variable variable;
    public boolean increasing;
    public IncDec(boolean inc, Variable variable, int indexL, int indexR) {
        this.increasing = inc;
        this.variable = variable;
        this.indexL = indexL;
        this.indexR = indexR;
    }
    
    public boolean isIncreasing() {
        return increasing;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        if (variable.hasArrayIndex()) {
            BigInteger index = instance.popStack();
            AccessArray access = (AccessArray) variable.getAccessVar();
            access.checkIndex(instance, index, variable.arrayIndex);
            BigInteger value = access.getValue(instance, index);
            variable.checkInitializedArray(value, index);
            value = increasing ? value.add(BigInteger.ONE) : value.subtract(BigInteger.ONE);
            checkProperValue(value);
            access.setValue(instance, index, value);
        } else {
            AccessInteger access = (AccessInteger) variable.getAccessVar();
            BigInteger value = access.getValue(instance);
            variable.checkInitialized(value);
            value = increasing ? value.add(BigInteger.ONE) : value.subtract(BigInteger.ONE);
            checkProperValue(value);
            access.setValue(instance, value);
        }
        return jump;
    }
    
    private void checkProperValue(BigInteger value) throws ProgramError {
        String error = null;
        if (increasing) {
            if (value.compareTo(MAX_VALUE) > 0) {
                error = Lang.exceedMaxValue;
            }
        } else {
            if (value.compareTo(MIN_VALUE) < 0) {
                error = Lang.exceedMinValue;
            }
        }
        if (error != null) {
            throw new ProgramError(error, getLeftIndex(), getRightIndex());
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
        strCreator.addString(increasing ? "Inkrementacja: " : "Dekrementacja: ");
        strCreator.addStringToExtend(variable.getName(), 10, true, 1);

        BigInteger value;
        if (variable.hasArrayIndex()) {
            BigInteger index = instance.peekStack();
            AccessArray access = (AccessArray) variable.getAccessVar();
            if (index == null || !access.checkIndex(instance, index)) {
                return strCreator;
            }
            strCreator.addString("[" + index.toString() + "] = ");
            value = access.getValue(instance, index);
        } else {
            strCreator.addString(" = ");
            AccessInteger access = (AccessInteger) variable.getAccessVar();
            value = access.getValue(instance);
        }
        
        if (value == null) {
            return strCreator;
        }
        BigInteger newValue = increasing ? value.add(BigInteger.ONE) : 
                value.subtract(BigInteger.ONE);
        strCreator.addBigIntegerToExtend(newValue, 1, 1);
        
        return strCreator;
    }
    
    @Override
    public void printDebug() {
        System.out.println(increasing?"inc":"dec");
        variable.printDebug();
    }
}
