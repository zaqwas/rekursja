package syntax.expression.operators;

import syntax.expression.SyntaxNodeExpr;

public abstract class Operation extends SyntaxNodeExpr {

    public OperationType operationType;

    public int getPriority() {
        return operationType.getPriority();
    }
    public OperationType getOperationType() {
        return operationType;
    }
    
    @Override
    public boolean isStatisticsNode() {
        return true;
    }

}
