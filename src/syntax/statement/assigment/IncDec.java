package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.math.BigInteger;
import parser.ProgramError;
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
        //TODO array
//        BigInteger value = variable.accessVar.getVar(instance);
//        if ( value==null ) {
//            return null;
//        }
//        if ( increasing ) {
//            value = value.add( BigInteger.ONE );
//        } else {
//            value = value.subtract( BigInteger.ONE );
//        }
//        final BigInteger val = value;
//        return new StringCreator() {
//            @Override
//            public String getString(int maxWidth, FontMetrics fontMetrics) {
//                return (increasing?"Inkrementacja:  ":"Dekrementacja:  ") + variable.name + " = " + val;
//            }
//        };
        return new StringCreator() {
            @Override
            public String getString(int maxWidth) {
                assert fontMetrics!=null : "FontMetrics not initialized.";
                return increasing?"Inkrementacja":"Dekrementacja";
            }
        };
    }
    
    private BigInteger eval(Instance instance) {
        BigInteger value = instance.popStack();
        return increasing ? value.add(BigInteger.ONE)
                : value.subtract(BigInteger.ONE);
    }
    
    @Override
    public void printDebug() {
        System.out.println(increasing?"inc":"dec");
        variable.printDebug();
    }
}
