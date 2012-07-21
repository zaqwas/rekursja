package syntax.expression;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import interpreter.accessvar.AccessVar;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.Argument;
import java.math.BigInteger;
import syntax.SyntaxNode;

public class Variable extends SyntaxNodeExpr {

    public String name;
    public SyntaxNodeExpr arrayIndex;
    protected AccessVar accessVar;
    
    //private AccessVar accessVar;

    public Variable(String name, int indexL, int indexR) {
        this.name = name;
        this.indexL = indexL;
        this.indexR = indexR;
        
        setCommitter(null);
    }
    
    
    public String getName() {
        return name;
    }
    
    public AccessVar getAccessVar() {
        return accessVar;
    }
    public void setAccessVar(AccessVar accessVar) {
        this.accessVar = accessVar;
    }
    
    public boolean hasArrayIndex() {
        return arrayIndex!=null;
    }
    
    public VariableType getPossiblyType() {
        return accessVar.getVariableType() == VariableType.ARRAY && arrayIndex == null
                ? VariableType.ARRAY : VariableType.REFERENCE;
    }
    
    //<editor-fold defaultstate="collapsed" desc="thisNodeIsParameter function">
    @Override
    public void thisNodeIsParameter(final int paramIndex, VariableType type) {
        final ArgumentCreator argCreator;
        switch (type) {
            case INTEGER:
                argCreator = new ArgumentCreator() {
                    @Override
                    public Argument getArgument(Instance instance) {
                        return new ArgInteger(getValue(instance));
                    }
                };
                break;
            case ARRAY:
                argCreator = new ArgumentCreator() {
                    @Override
                    public Argument getArgument(Instance instance) {
                        return ((AccessArray) accessVar).getArrayPtr(instance);
                    }
                };
                break;
            case REFERENCE:
                if ( arrayIndex==null ) {
                    argCreator = new ArgumentCreator() {
                        @Override
                        public Argument getArgument(Instance instance) {
                            return ((AccessInteger) accessVar).getReference(instance);
                        }
                    };
                    break;
                }
                argCreator = new ArgumentCreator() {
                    @Override
                    public Argument getArgument(Instance instance) {
                        BigInteger idx = instance.popStack();
                        AccessArray arr = (AccessArray) accessVar;
                        int idxErr = arr.checkIndex(instance, idx);
                        if (idxErr != 0) {
                            //TODO check size
                            throw new RuntimeException();
                        }
                        return arr.getReference(instance, idx);
                    }
                };
                break;
            default:
                throw new AssertionError();
        }
        
        if (paramIndex < 0) {
            setCommitter(new Committer() {
                private int size = -paramIndex;
                @Override
                public SyntaxNode commit(Instance instance) {
                    instance.pushArgsArray(size);
                    instance.setArgument(0, argCreator.getArgument(instance));
                    return jump;
                }
            });
            return;
        }
        setCommitter(new Committer() {
            @Override
            public SyntaxNode commit(Instance instance) {
                instance.setArgument(paramIndex, argCreator.getArgument(instance));
                return jump;
            }
        });
    }
    //</editor-fold>
    
    @Override
    protected BigInteger getValue(Instance instance) {
        if ( arrayIndex==null ) {
            return ((AccessInteger) accessVar).getValue(instance);
        }
        BigInteger idx = instance.popStack();
        AccessArray arr = (AccessArray) accessVar;
        int idxErr = arr.checkIndex(instance, idx);
        if ( idxErr!=0 ) {
            //TODO check size
            throw new RuntimeException();
        }
        return arr.getValue(instance, idx);
    }
    
    private interface ArgumentCreator {
        public Argument getArgument(Instance instance);
    }
    
    @Override
    public void printDebug() {
        System.out.println("Variable: " + name);
        if ( arrayIndex!=null ) {
            arrayIndex.printDebug();
        }
    }
    
}
