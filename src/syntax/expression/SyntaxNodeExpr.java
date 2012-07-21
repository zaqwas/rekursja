package syntax.expression;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.Argument;
import java.math.BigInteger;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;

public abstract class SyntaxNodeExpr extends SyntaxNodeIdx {

    public SyntaxNodeIdx jumpAndOr;
    public int paramIndex; //if positiv: index of parameter, if negatif: quantity of parameters;
    //public ExprSpecialTask specialTask = ExprSpecialTask.NOTHING;
    private Committer committer;
    
    @Override
    public final SyntaxNode commit(Instance instance) {
        return committer.commit(instance);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Special task functions">
    public void thisNodeIsParameter(final int paramIndex, VariableType type) {
        if ( paramIndex<0 ) {
            committer = new Committer() {
                private int size = - paramIndex;
                @Override
                public SyntaxNode commit(Instance instance) {
                    BigInteger value = getValue(instance);
                    instance.pushArgsArray(size);
                    instance.setArgument(0, new ArgInteger(value));
                    return jump;
                }
            };
            return;
        }
        committer = new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                BigInteger value = getValue(instance);
                instance.setArgument(paramIndex, new ArgInteger(value));
                return jump;
            }
        };
    }
    
    public void parentIsLogicOperation(final SyntaxNode parent, boolean parentIsAnd) {
        if (parentIsAnd) {
            committer = new Committer() {
                @Override
                public SyntaxNode commit(Instance instance) {
                    BigInteger value = getValue(instance);
                    if ( value.compareTo(BigInteger.ZERO)==0 ) {
                        instance.pushStack(value);
                        return parent;
                    }
                    return jump;
                }
            };
            return;
        }
        committer = new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                BigInteger value = getValue(instance);
                if (value.compareTo(BigInteger.ZERO) != 0) {
                    instance.pushStack(value);
                    return parent;
                }
                return jump;
            }
        };
    }
    
    public void putValueOnStackTwice() {
        committer = new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                BigInteger value = getValue(instance);
                instance.pushStack(value);
                instance.pushStack(value);
                return jump;
            }
        };
    };
    //</editor-fold>
    
    protected abstract BigInteger getValue(Instance instance);
    
    protected final void setCommitter(Committer committer) {
        if ( committer==null ) {
            this.committer = new Committer() {
                @Override
                public SyntaxNode commit(Instance instance) {
                    instance.pushStack( getValue(instance) );
                    return jump;
                }
            };
        } else {
            this.committer = committer;
        }
    }
}
