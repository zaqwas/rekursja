package syntax.statement.other;

import interpreter.Instance;
import java.math.BigInteger;
import stringcreator.FlexibleStringCreator;
import stringcreator.SimpleLazyStringCreator;
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
        if (expresion == null) {
            return new SimpleLazyStringCreator("Przerwij działanie funkcji");
        }
        FlexibleStringCreator strCreator = new FlexibleStringCreator();
        strCreator.addString("Przerwij działanie funkcji i zwróć ");
        BigInteger value = instance.stack.peek();
        strCreator.addBigIntegerToExtend(value, 1, 1);
        return strCreator;
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
