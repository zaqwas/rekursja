package syntax.function;

import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessVar;
import interpreter.accessvar.VariableType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.TreeMap;
import parser.ProgramError;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIdx;
import syntax.VarDeclaration;

public class Function extends SyntaxNodeIdx implements FunctionDeclaration {
    public String name;
    //TODO rename to isVoid
    public boolean returnValue;
    
    public ArrayList<SyntaxNode> statements;
    
    protected ArrayList<VarDeclaration> arguments;
    protected ArrayList<VarDeclaration> localVars;
    protected TreeMap<String, VarDeclaration> variablesMap;
    public int localVarsArraySize;
    
    protected FunctionBehavior funcitonBehavior;
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Function(boolean integer, String name, int indexL) {
        this.returnValue = integer;
        this.name = name;
        this.indexL = indexL;
        
        //this.varsList = new ArrayList<DeclarationArgVar>();
        //this.argsList = new ArrayList<DeclarationArgVar>();
        //this.argsVarsTree = new TreeSet<DeclarationArgVar>();
        
        arguments = new ArrayList<>();
        localVars = new ArrayList<>();
        variablesMap = new TreeMap<>();
        
        this.statements = new ArrayList<>();
        
        funcitonBehavior = new FunctionBehavior() {
            {
                function = Function.this;
            }
        };
    }

    public Function(String name) {
        this.name = name;
    }
    public Function() {}
    //</editor-fold>    
    
    
    public SyntaxNode getJumpNode() {
        return jump;
    }
    public int getLocalVarsArraySize() {
        return localVarsArraySize;
    }
    public boolean isVoid() {
        return !returnValue;
    }
    
    public void setFuncitonBehavior(FunctionBehavior funcitonBehavior) {
        this.funcitonBehavior = funcitonBehavior;
        funcitonBehavior.setFunction(this);
    }
    
    public FunctionBehavior getFunctionBehavior() {
        return funcitonBehavior;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getArgumentsLength() {
        return arguments.size();
    }
    public String getArgumentName(int index) {
        return arguments.get(index).getName();
    }
    public AccessVar getArgumentAccessVar(int index) {
        return arguments.get(index).getAccessVar();
    }
    @Override
    public VariableType getArgumentType(int index) {
        return arguments.get(index).getAccessVar().getVariableType();
    }
    
    public int getLocalVarsLength() {
        return localVars.size();
    }
    public String getLocalVarName(int index) {
        return localVars.get(index).getName();
    }
    public AccessVar getLocalVarAccessVar(int index) {
        return localVars.get(index).getAccessVar();
    }
    public VariableType getLocalVarType(int index) {
        return localVars.get(index).getAccessVar().getVariableType();
    }
    
    public AccessVar getAccessVarByName(String name) {
        VarDeclaration declaration = variablesMap.get(name);
        return declaration==null ? null : declaration.getAccessVar();
    }
    public boolean checkDeclaration(String name) {
        return variablesMap.containsKey(name);
    }
    public void addArgument(String name, VariableType type) {
        AccessVar accessVar = Instance.genereArgumentAccessVar(arguments.size(), type);
        VarDeclaration declaration = new VarDeclaration(name, accessVar);
        arguments.add(declaration);
        variablesMap.put(name, declaration);
    }
    public void addLocalVar(String name, BigInteger size) {
        AccessVar accessVar = Instance.genereLocalVarAccessVar(localVarsArraySize, size);
        localVarsArraySize += size == null ? 1 : ((AccessArray) accessVar).getSizeInteger(null);
        VarDeclaration declaration = new VarDeclaration(name, accessVar);
        localVars.add(declaration);
        variablesMap.put(name, declaration);
    }
    
    
    

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        return funcitonBehavior.commit(instance);
    }
    
    public boolean isStopedBeforeCall() {
        return funcitonBehavior.isStoppedBeforeCall();
    }
    public boolean isStoppedAfterCall() {
        return funcitonBehavior.isStoppedAfterCall();
    }
    public boolean isAddedToHistory() {
        return funcitonBehavior.isAddedToHistory();
    }
    public boolean isAddedToStatistics() {
        return funcitonBehavior.isAddedToStatistics();
    }
    
    public StringCreator getStatusCreatorBeforeCall(Instance instance) {
        return funcitonBehavior.getStatusCreatorBeforeCall(instance);
    }
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        return funcitonBehavior.getStatusCreatorAfterCall(instance);
    }
    
    public StringCreator getLabelCreator(Instance instance) {
        return funcitonBehavior.getLabelCreator(instance);
    }
    
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        return funcitonBehavior.getTreeNodeLabel(maxLength, instance);
    }
    
    
    @Override
    public void printDebug() {
        System.out.println("Function: " + name);
        SyntaxNode.printDebugList(statements);
    }
}





//@Override
//    public int compareTo(Function o) {
//        int comp = name.compareTo(o.name);
//        if ( comp != 0 ) {
//            return comp;
//        }
//        comp = argsList.size() - o.argsList.size();
//        if ( comp != 0 ) {
//            return comp<0 ? -1 : 1;
//        }
//        int size = argsList.size();
//        
//        for (int i=0; i<size; i++) {
//            boolean t1 = argsList.get(i).varType != VariableType.ARRAY;
//            boolean t2 = o.argsList.get(i).varType != VariableType.ARRAY;
//            
//            if ( t1 && !t2 ) return -1;
//            if ( !t1 && t2 ) return 1;
//        }
//        
//        return 0;
//    }