package syntax.statement;

import interpreter.Instance;
import java.awt.FontMetrics;
import java.math.BigInteger;
import java.util.ArrayList;
import stringcreator.FlexibleStringCreator;
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
    
    public SyntaxNode getStatement(int index) {
        return statements.get(index);
    }

    public SyntaxNode getJumpElse() {
        return jumpElse;
    }
    
    
    protected abstract String getNameOfInstruction();
    
    @Override
    public StringCreator getStatusCreator(Instance instance) {
        FlexibleStringCreator strCreator = new FlexibleStringCreator();
        strCreator.addString(getNameOfInstruction());
        
        final BigInteger value = instance.stack.peek();
        if (value == null) {
            return strCreator;
        }
        
        if (value.compareTo(BigInteger.ZERO) == 0) {
            strCreator.addString("warunek niespełniony (0)");
        } else 
        {
            strCreator.addString("warunek spełniony (");
            strCreator.addBigIntegerToExtend(value, 1, 1);
            strCreator.addString(")");
        }
        return strCreator;
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
