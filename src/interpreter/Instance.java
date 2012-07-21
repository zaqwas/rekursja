package interpreter;

import interpreter.accessvar.*;
import interpreter.arguments.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;
import stringcreator.StringExtender;
import stringcreator.Extender;
import stringcreator.IntegerExtender;
import stringcreator.StringCreator;
import syntax.function.Function;
import syntax.expression.Call;

public class Instance {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    public Function function;
    private Call callNode;
    
    public BigInteger returnedValue;
    public boolean onStack;
    
    public BigInteger arrayLocal[];
    public Argument arrayArgs[];
    
    public Stack<Argument[]> argsStack = new Stack<Argument[]>();
    public Stack<BigInteger> stack = new Stack<BigInteger>();

    private Instance parentInst;
    private int indexParentInst;
    public ArrayList<Instance> childrenInst = new ArrayList<Instance>();
    //</editor-fold>
    
    
    public Instance (Function main) {
        function = main;
        
        arrayLocal = new BigInteger[function.localVarsArraySize];
        onStack = true;
    }
    
    public Instance (Instance parent, Call call, boolean addToChildren) {
        callNode = call;
        function = call.getFunction();
        
        arrayLocal = new BigInteger[function.getLocalVarsArraySize()];
        if ( call.getArgumentsLength()>0 ) {
            arrayArgs = parent.argsStack.pop();
        }
        
        parentInst = parent;
        if ( addToChildren ) {
            indexParentInst = parent.childrenInst.size();
            parent.childrenInst.add(this);
        }
        onStack = true;
    }
    
    
    public Function getFunction() {
        return function;
    }
    public Call getCallNode() {
        return callNode;
    }
    public BigInteger getReturnedValue() {
        return returnedValue;
    }
    public void setReturnedValue(BigInteger returnedValue) {
        this.returnedValue = returnedValue;
    }
    
    public boolean isOnStack() {
        return onStack;
    }
    public void removeFromStack() {
        onStack = false;
    }
    
    
    

    //<editor-fold defaultstate="collapsed" desc="Args, local and global array access">
    public int getArgumentsLength() {
        if ( arrayArgs == null ) {
            return 0;
        }
        return arrayArgs.length;
    }
    public Argument getArgument(int index) {
        return arrayArgs[index];
    }
    
//    public BigInteger getLocalInt(int index) {
//        return arrayLocal[index]; 
//    }
//    public void setLocalInt(int index, BigInteger value) {
//        arrayLocal[index] = value;
//    }
//    
//    public BigInteger getGlobalInt(int index) {
//        return arrayGlobal[index]; 
//    }
//    public void setGlobalInt(int index, BigInteger value) {
//        arrayGlobal[index] = value;
//    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Stack and ArgsStack functions">
    public void pushStack(BigInteger value) {
        stack.push(value);
    }
    public BigInteger popStack() {
        return stack.pop();
    }
    public BigInteger peekStack() {
        return stack.peek();
    }
    
    public void pushArgsArray(int size) {
        argsStack.push(new Argument[size]);
    }
    public void setArgument(int index, Argument arg) {
        argsStack.peek()[index] = arg;
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Neighbour instances">
    public boolean hasParentInstance() {
        return parentInst != null;
    }
    public Instance getParentInstance() {
        return parentInst;
    }
    public int getIndexParentInst() {
        return indexParentInst;
    }
    
    public boolean hasLeftBrotherInstance() {
        return parentInst != null && indexParentInst > 0;
    }
    public Instance getLeftBrotherInstance() {
        if (hasLeftBrotherInstance()) {
            return parentInst.childrenInst.get(indexParentInst - 1);
        }
        return null;
    }
    
    public boolean hasRightBrotherInstance() {
        return parentInst != null && indexParentInst + 1 < parentInst.childrenInst.size();
    }
    public Instance getRightBrotherInstance() {
        if (hasRightBrotherInstance()) {
            return parentInst.childrenInst.get(indexParentInst + 1);
        }
        return null;
    }
    
    public boolean hasChildrenInstances() {
        return !childrenInst.isEmpty();
    }
    public int getChildrenInstancesSize() {
        return childrenInst.size();
    }
    public Instance getChildInstance(int number) {
        return childrenInst.get(number);
    }
    public Instance getRightChildInstance() {
        if ( childrenInst.isEmpty() ) {
            return null;
        }
        return childrenInst.get(childrenInst.size()-1);
    }
    public Instance getLeftChildInstance() {
        if ( childrenInst.isEmpty() ) {
            return null;
        }
        return childrenInst.get(0);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="InstanceStringCreator">
    public StringCreator getInstanceStringCreator(final String prefix, final String sufix) {
        
        return new StringCreator() {

            private boolean onStack;

            @Override
            public String getString(int maxWidth) {
                assert fontMetrics!=null : "FontMetrics not initialized.";
                int roundBracketsWidth = fontMetrics.stringWidth("()");
                int squereBracketsWidth = fontMetrics.stringWidth("[]");
                int invertedCommasWidht = fontMetrics.stringWidth("\"\"");
                int commaWidht = fontMetrics.charWidth(',');
                int colonWidth = fontMetrics.charWidth(':');

                maxWidth -= fontMetrics.stringWidth(prefix);
                maxWidth -= fontMetrics.stringWidth(sufix);

                ArrayList<Extender> extArray = new ArrayList<Extender>();
                Extender extender = new StringExtender(function.getName(), 10, fontMetrics);
                maxWidth -= extender.getWidth();
                extArray.add(extender);
                maxWidth -= roundBracketsWidth;
                if (arrayArgs != null && arrayArgs.length > 0) {
                    for (Argument arg : arrayArgs) {
                        if (arg.isIntOrRef()) {
                            extender = new IntegerExtender(((ArgIntOrRef) arg)
                                    .getValueAtTheBeginning(), fontMetrics);
                            maxWidth -= extender.getWidth();
                            extArray.add(extender);
                            if (!onStack && arg.isReference()) {
                                maxWidth -= colonWidth;
                                extender = new IntegerExtender(((ArgReference) arg)
                                        .getValueAtTheEnd(), fontMetrics);
                                maxWidth -= extender.getWidth();
                                extArray.add(extender);
                            }
                        } else if (arg.isArray()) {
                            maxWidth -= squereBracketsWidth;
                        } else {
                            maxWidth -= invertedCommasWidht;
                        }
                        maxWidth -= commaWidht;
                    }
                    maxWidth += commaWidht;
                }
                if (!onStack && returnedValue != null) {
                    maxWidth -= colonWidth;
                    extender = new IntegerExtender(returnedValue, fontMetrics);
                    maxWidth -= extender.getWidth();
                    extArray.add(extender);
                }

                while (true) {
                    boolean canExtend = false;
                    for (Extender ext : extArray) {
                        int width = ext.tryExtend(maxWidth);
                        if (width > 0) {
                            maxWidth -= width;
                            canExtend = true;
                        }

                    }
                    if (!canExtend) {
                        break;
                    }
                }

                StringBuilder sb = new StringBuilder(prefix);
                sb.append(extArray.get(0).getString());
                sb.append('(');
                if (arrayArgs != null && arrayArgs.length > 0) {
                    int index = 1;
                    boolean first = true;
                    for (Argument arg : arrayArgs) {
                        if (!first) {
                            sb.append(',');
                        }
                        first = false;
                        if (arg.isIntOrRef()) {
                            sb.append(extArray.get(index++).getString());
                            if (!onStack && arg.isReference()) {
                                sb.append(':');
                                sb.append(extArray.get(index++).getString());
                            }
                        } else if (arg.isArray()) {
                            sb.append("[]");
                        } else {
                            sb.append("\"\"");
                        }
                    }
                }
                sb.append(')');
                if (!onStack && returnedValue != null) {
                    sb.append(':');
                    sb.append(extArray.get(extArray.size() - 1).getString());
                }
                sb.append(sufix);
                return sb.toString();
            }
        };
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Genere accessVar">
    public static AccessVar genereArgumentAccessVar(final int index, final VariableType type) {
        if (type != VariableType.ARRAY) {
            return new AccessInteger() {
                @Override
                public VariableType getVariableType() {
                    return type;
                }
                @Override
                public VariableScope getVariableScope() {
                    return VariableScope.ARGUMENT;
                }
                @Override
                public BigInteger getValue(Instance instance) {
                    return ((ArgIntOrRef) instance.arrayArgs[index]).getValue();
                }
                @Override
                public void setValue(Instance instance, BigInteger value) {
                    ((ArgIntOrRef) instance.arrayArgs[index]).setValue(value);
                }
                @Override
                public ArgReference getReference(Instance instance) {
                    return ((ArgIntOrRef) instance.arrayArgs[index]).getReference();
                }
            };
        }
        return new AccessArray() {
            @Override
            public VariableScope getVariableScope() {
                return VariableScope.ARGUMENT;
            }
            @Override
            public BigInteger getValue(Instance instance, BigInteger idx) {
                return ((ArgArray) instance.arrayArgs[index]).getValue(idx);
            }
            @Override
            public BigInteger getValue(Instance instance, int idx) {
                return ((ArgArray) instance.arrayArgs[index]).getValue(idx);
            }
            @Override
            public void setValue(Instance instance, BigInteger idx, BigInteger value) {
                ((ArgArray) instance.arrayArgs[index]).setValue(idx, value);
            }
            @Override
            public ArgReference getReference(Instance instance, BigInteger idx) {
                return ((ArgArray) instance.arrayArgs[index]).getReference(idx);
            }
            @Override
            public ArgArray getArrayPtr(Instance instance) {
                return ((ArgArray) instance.arrayArgs[index]).getArrayPtr();
            }
            @Override
            public BigInteger getSizeBigInt(Instance instance) {
                return ((ArgArray) instance.arrayArgs[index]).getSizeBigInt();
            }
            @Override
            public int getSizeInteger(Instance instance) {
                return ((ArgArray) instance.arrayArgs[index]).getSizeInteger();
            }
        };
    }
    
    public static AccessVar genereLocalVarAccessVar(final int index, final BigInteger size) {
        if (size == null) {
            return new AccessInteger() {
                @Override
                public VariableScope getVariableScope() {
                    return VariableScope.LOCAL;
                }
                @Override
                public BigInteger getValue(Instance instance) {
                    BigInteger[] arr = instance.arrayLocal;
                    return arr == null ? null : instance.arrayLocal[index];
                }
                @Override
                public void setValue(Instance instance, BigInteger value) {
                    instance.arrayLocal[index] = value;
                }
                @Override
                public ArgReference getReference(Instance instance) {
                    return new ArgRefNormal(instance.arrayLocal, index);
                }
            };
        }
        return new AccessArray() {
            private int sizeInteger = size.intValue();
            @Override
            public VariableScope getVariableScope() {
                return VariableScope.LOCAL;
            }
            @Override
            public BigInteger getValue(Instance instance, BigInteger idx) {
                int idxInt = idx.intValue();
                return instance.arrayLocal[index + idxInt];
            }
            @Override
            public BigInteger getValue(Instance instance, int idx) {
                BigInteger[] arr = instance.arrayLocal;
                return arr == null ? null : arr[index + idx];
            }
            @Override
            public void setValue(Instance instance, BigInteger idx, BigInteger value) {
                int idxInt = idx.intValue();
                instance.arrayLocal[index + idxInt] = value;
            }
            @Override
            public ArgReference getReference(Instance instance, BigInteger idx) {
                int idxInt = idx.intValue();
                return new ArgRefNormal(instance.arrayLocal, index + idxInt);
            }
            @Override
            public ArgArray getArrayPtr(Instance instance) {
                return new ArgArray(instance.arrayLocal, size, index);
            }
            @Override
            public BigInteger getSizeBigInt(Instance instance) {
                return size;
            }
            @Override
            public int getSizeInteger(Instance instance) {
                return sizeInteger;
            }
        };
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="debug">
    private static int count = 0;
    public Instance() {
        //function = new Function("ABC_abc" + count);
        count++;
    }
    public void add() {
        Instance newInst = new Instance();
        newInst.parentInst = this;
        newInst.indexParentInst = childrenInst.size();
        childrenInst.add(newInst);
    }
    
    public void debug_print() {
        System.out.println(function.name);
        System.out.println("returnInt: " + returnedValue);
        System.out.println("arrayLocal.length: " + arrayLocal.length);
        System.out.println("arrayArgs.length: " + arrayArgs.length);
    }
    //</editor-fold>
}
