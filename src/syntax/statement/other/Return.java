package syntax.statement.other;

import interpreter.Instance;
import java.awt.FontMetrics;
import java.math.BigInteger;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;
import syntax.expression.SyntaxNodeExpr;

public class Return extends SyntaxNodeIdx {

    public SyntaxNodeExpr expresion;

    public Return(int indexL) {
        this.indexL = indexL;
        this.indexR = indexL + 6;
    }

    @Override
    public SyntaxNode commit(Instance instance) {
        if (expresion != null) {
            instance.setReturnedValue(instance.popStack());
        }
        return null;
    }
    
    @Override
    public boolean isStopNode() {
        return true;
    }
    
    @Override
    public StringCreator getStatusCreator(Instance instance) {
        //TODO array
        final BigInteger value = instance.stack.peek();
        
        return new StringCreator() {
            @Override
            public String getString(int maxWidth) {
                assert fontMetrics!=null : "FontMetrics not initialized.";
                return expresion == null
                        ? "Wyjdź z funkcji"
                        : "Zwróć:  " + value;
            }
        };
    }
    
    
    @Override
    public int getRightIndex() {
        if ( expresion==null ) {
            return indexL + 6;
        } else {
            return expresion.getRightIndex();
        }
    }
    
    @Override
    public void printDebug() {
        System.out.println("return");
        if ( expresion!=null ) expresion.printDebug();
    }
}
