package syntax;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import interpreter.accessvar.AccessVar;
import interpreter.accessvar.VariableScope;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgArray;
import interpreter.arguments.ArgRefNormal;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import syntax.function.Function;
import java.util.ArrayList;
import java.util.TreeMap;
import syntax.function.FunctionComparator;
import syntax.function.FunctionDeclaration;
import syntax.function.SpecialFunctionBehavior;

public class SyntaxTree {
    public Function main;
    private ArrayList<Function> functions = new ArrayList<Function>();
    private TreeMap<FunctionDeclaration, Integer> functionsMap =
            new TreeMap<FunctionDeclaration, Integer>(new FunctionComparator());
    
    private ArrayList<VarDeclaration> globalVars = new ArrayList<VarDeclaration>();
    private TreeMap<String, VarDeclaration> variablesMap = new TreeMap<String, VarDeclaration>();
    
    private int arrayGlobalSize;
    private BigInteger arrayGlobal[];
    
    
    
    public int getFunctionsSize() {
        return functions.size();
    }
    public Function getFunction(int index) {
        return functions.get(index);
    }
    
    public void initGlobalVars() {
        arrayGlobal = new BigInteger[arrayGlobalSize];
        for (int i = 0; i < arrayGlobalSize; i++) {
            arrayGlobal[i] = BigInteger.ZERO;
        }
    }
    
    public Function getFunciton(FunctionDeclaration functionDeclaration) {
        Integer integer = functionsMap.get(functionDeclaration);
        return integer==null ? null : functions.get(integer);
    }
    public void setSpecialFunctionBehavior(SpecialFunctionBehavior function) {
        Integer index = functionsMap.get(function);
        if ( index==null ) {
            return;
        }
        functions.get(index).setFuncitonBehavior(function);
    }
    public boolean addFunction(Function function) {
        if ( functionsMap.containsKey(function) ) {
            return false;
        }
        functionsMap.put(function, functions.size());
        functions.add(function);
        return true;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Global Vars functions">
    public int getGlobalVarsSize() {
        return globalVars.size();
    }
    public String getGlobalVarName(int index) {
        return globalVars.get(index).getName();
    }
    public AccessVar getGlobalVarAccessVar(int index) {
        return globalVars.get(index).getAccessVar();
    }
    public VariableType getGlobalVarType(int index) {
        return globalVars.get(index).getAccessVar().getVariableType();
    }
    
    public AccessVar getAccessVarByName(String name) {
        VarDeclaration declaration = variablesMap.get(name);
        return declaration==null ? null : declaration.getAccessVar();
    }
    public boolean checkDeclaration(String name) {
        return variablesMap.containsKey(name);
    }
    public void addGlobalVar(String name, BigInteger size) {
        AccessVar accessVar = genereGlobalVarAccessVar(arrayGlobalSize, size);
        arrayGlobalSize += size == null ? 1 : ((AccessArray) accessVar).getSizeInteger(null);
        VarDeclaration declaration = new VarDeclaration(name, accessVar);
        globalVars.add(declaration);
        variablesMap.put(name, declaration);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Genere Access Var">
    public AccessVar genereGlobalVarAccessVar(final int index, final BigInteger size) {
        if (size == null) {
            return new AccessInteger() {
                @Override
                public VariableScope getVariableScope() {
                    return VariableScope.GLOBAL;
                }
                @Override
                public BigInteger getValue(Instance instance) {
                    return arrayGlobal[index];
                }
                @Override
                public void setValue(Instance instance, BigInteger value) {
                    arrayGlobal[index] = value;
                }
                @Override
                public ArgReference getReference(Instance instance) {
                    return new ArgRefNormal(arrayGlobal, index);
                }
            };
        }
        return new AccessArray() {
            private int sizeInteger = size.intValue();
            @Override
            public VariableScope getVariableScope() {
                return VariableScope.GLOBAL;
            }
            @Override
            public BigInteger getValue(Instance instance, BigInteger idx) {
                int idxInt = idx.intValue();
                return arrayGlobal[index + idxInt];
            }
            @Override
            public BigInteger getValue(Instance instance, int idx) {
                return arrayGlobal[index + idx];
            }
            @Override
            public void setValue(Instance instance, BigInteger idx, BigInteger value) {
                int idxInt = idx.intValue();
                arrayGlobal[index + idxInt] = value;
            }
            @Override
            public ArgReference getReference(Instance instance, BigInteger idx) {
                int idxInt = idx.intValue();
                return new ArgRefNormal(arrayGlobal, index + idxInt);
            }
            @Override
            public ArgArray getArrayPtr(Instance instance) {
                return new ArgArray(arrayGlobal, size, index);
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
    
    public void printDebug() { //debug
        for ( Function f:functions) {
            f.printDebug();
        }
    }
}
