package syntax.expression;

import interpreter.Instance;
import java.math.BigInteger;

public class Const extends SyntaxNodeExpr {

    public BigInteger value;

    public Const(BigInteger value, int indexL, int indexR) {
        this.value = value;
        this.indexL = indexL;
        this.indexR = indexR;
        
        setCommitter(null);
    }
    
    @Override
    protected BigInteger getValue(Instance instance) {
        return value;
    }
    
    
    @Override
    public void printDebug() {
        System.out.println("Const: " + value);
    }

}
