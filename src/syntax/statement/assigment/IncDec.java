package syntax.statement.assigment;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import java.awt.FontMetrics;
import java.math.BigInteger;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;
import syntax.expression.Variable;

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
    public SyntaxNode commit(Instance instance) {
        BigInteger result = eval(instance);
        if ( !variable.hasArrayIndex() ) {
            ((AccessInteger) variable.getAccessVar()).setValue(instance, result);
            return jump;
        }
        //unnecessery to check index
        BigInteger idx = instance.popStack();
        ((AccessArray) variable.getAccessVar()).setValue(instance, idx, result);
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
