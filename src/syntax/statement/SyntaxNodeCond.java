package syntax.statement;

import interpreter.Instance;
import java.awt.FontMetrics;
import java.math.BigInteger;
import java.util.ArrayList;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIndexed;
import syntax.expression.SyntaxNodeExpr;

public abstract class SyntaxNodeCond extends SyntaxNode implements SyntaxNodeIndexed {
    public SyntaxNodeExpr condition;
    public ArrayList<SyntaxNode> statements;
    public SyntaxNode jumpElse;
    
    @Override
    public SyntaxNode commit(Instance instance) {
        BigInteger value = instance.stack.pop();
        return value.compareTo(BigInteger.ZERO) != 0 ? jump : jumpElse;
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
                return value.compareTo(BigInteger.ZERO)!=0
                        ? "Prawda"
                        : "Fałsz";
            }
        };
    }
    
    @Override
    public int getLeftIndex() {
        return ((SyntaxNodeIndexed) condition).getLeftIndex();
    }

    @Override
    public int getRightIndex() {
        return ((SyntaxNodeIndexed) condition).getRightIndex();
    }
    
    
}
