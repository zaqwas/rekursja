package parser;

import interpreter.accessvar.AccessVar;
import interpreter.accessvar.VariableType;
import java.util.ArrayList;
import syntax.function.Function;
import syntax.function.FunctionWrite;
import syntax.SyntaxNode;
import syntax.SyntaxTree;
import syntax.expression.Call;
import syntax.expression.SyntaxNodeExpr;
import syntax.expression.Variable;
import syntax.expression.operators.BinaryOperation;
import syntax.expression.operators.UnaryOperation;
import syntax.function.FunctionDeclaration;
import syntax.statement.For;
import syntax.statement.If;
import syntax.statement.While;
import syntax.statement.assigment.Assigment;
import syntax.statement.assigment.IncDec;
import syntax.statement.other.Return;

public class CheckConstraints {
    
    //private static TreeSet<Function> functionsTree;
    private static SyntaxTree syntaxTree;
    private static Function currentFunction;
    
    //public static TreeSet<DeclarationArgVar> globalVarsTree;
    //public static TreeSet<DeclarationArgVar> argsVarsInCheckingFunc;
    
    private static void checkStatement(SyntaxNode sn) throws ParseError {
        if (sn instanceof Assigment) {
            Assigment a = (Assigment) sn;
            checkExpr(a.variable, false);
            checkExpr(a.expresion, false);
        } else if (sn instanceof IncDec) {
            IncDec inc = (IncDec) sn;
            checkExpr(inc.variable, false);
        } else if (sn instanceof For) {
            For fr = (For) sn;
            checkStatementList(fr.statementsInit);
            checkExpr(fr.condition, false);
            checkStatementList(fr.statementsEnd);
            checkStatementList(fr.statements);
        } else if (sn instanceof If) {
            If iif = (If) sn;
            checkExpr(iif.condition, false);
            checkStatementList(iif.statements);
            checkStatementList(iif.statementsElse);
        } else if (sn instanceof While) {
            While wh = (While) sn;
            if (wh.doWhile) {
                checkStatementList(wh.statements);
                checkExpr(wh.condition, false);
            } else {
                checkExpr(wh.condition, false);
                checkStatementList(wh.statements);
            }
        } else if (sn instanceof Return) {
            Return r = (Return) sn;
            if (r.expresion != null) {
                checkExpr(r.expresion, false);
            }
        } else if (sn instanceof Call) {
            checkExpr((Call) sn, false);
        }
    }

    private static void checkStatementList(ArrayList<SyntaxNode> snList) throws ParseError {
        for (SyntaxNode sn : snList) {
            checkStatement(sn);
        }
    }

    private static VariableType checkExpr(SyntaxNodeExpr sn, boolean ptrArray) throws ParseError {
        if (sn instanceof UnaryOperation) {
            UnaryOperation uo = (UnaryOperation) sn;
            checkExpr(uo.expresion, false);
            return VariableType.INTEGER;
        }
        if (sn instanceof BinaryOperation) {
            BinaryOperation uo = (BinaryOperation) sn;
            checkExpr(uo.expresionL, false);
            checkExpr(uo.expresionR, false);
            return VariableType.REFERENCE;
        }

        if (sn instanceof Call) {
            Call cl = (Call) sn;
            if (cl.name.equals("read")) {
                for (SyntaxNodeExpr param : cl.params) {
                    if (!(sn instanceof Variable)) {
                        throw new ParseError(81, param.indexL);
                    } else {
                        checkExpr(param, false);
                    }
                }
            } else if (cl.name.equals("write") || cl.name.equals("writeln")) {
                for (SyntaxNodeExpr param : cl.params) {
                    checkExpr(param, false);
                }
                cl.function = FunctionWrite.getInstance(cl.name.equals("writeln"));
            } else {
                for (SyntaxNodeExpr param : cl.params) {
                    checkExpr(param, true);
                }
                Function f = syntaxTree.getFunciton(cl);
                if (f == null) {
                    throw new ParseError(82, cl.indexL);
                }
                cl.function = f;
                for (int i = 0, size = f.getArgumentsLength(); i < size; i++) {
                    if (f.getArgumentType(i) == VariableType.REFERENCE
                            && cl.getArgumentType(i) == VariableType.INTEGER) {
                        throw new ParseError(82, cl.indexL);
                    }
                }
                if (cl.callInExpr && !f.returnValue) {
                    throw new ParseError(83, cl.indexL);
                }
            }
            return VariableType.INTEGER;
        }

        if (sn instanceof Variable) {
            Variable v = (Variable) sn;
            AccessVar accessVar = currentFunction.getAccessVarByName(v.getName());
            if (accessVar == null) {
                if (accessVar == null) {
                    throw new ParseError(84, v.indexL);
                }
            }
            v.setAccessVar(accessVar);
            if (accessVar.isArray()) {
                if (v.hasArrayIndex()) {
                    checkExpr(v.arrayIndex, false);
                    return VariableType.REFERENCE;
                } else {
                    if (ptrArray) {
                        return VariableType.ARRAY;
                    }
                    throw new ParseError(85, v.indexL);
                }
            } else {
                if (v.hasArrayIndex()) {
                    throw new ParseError(86, v.indexL);
                }
                return VariableType.REFERENCE;
            }
        }
        return VariableType.INTEGER;
    }
    

    
    public static void check(SyntaxTree syntaxTree) throws ParseError {
        try {
            CheckConstraints.syntaxTree = syntaxTree;
            for (int i = 0, size = syntaxTree.getFunctionsSize(); i < size; i++) {
                Function f = syntaxTree.getFunction(i);
                currentFunction = f;
                checkStatementList(f.statements);
            }
            
            FunctionDeclaration mainFunction = new FunctionDeclaration() {
                @Override
                public String getName() {
                    return "main";
                }
                @Override
                public int getArgumentsLength() {
                    return 0;
                }
                @Override
                public VariableType getArgumentType(int index) {
                    return null;
                }
            };
            Function main = syntaxTree.getFunciton(mainFunction);
            if ( main==null ) {
                throw new ParseError(87, -1);
            }
            if ( main.returnValue ) {
                throw new ParseError(88, main.indexL);
            }
            syntaxTree.main = main;
            
        } finally {
            CheckConstraints.syntaxTree = null;
            currentFunction = null;
        }
    }
}

//    public static int countSize(ArrayList<DeclarationArgVar> varsList) {
//        BigInteger size = BigInteger.ZERO;
//        for (DeclarationArgVar v : varsList) {
//            if (v.varType == VariableType.ARRAY) {
//                size = size.add(v.accessVar.size);
//            } else {
//                size = size.add(BigInteger.ONE);
//            }
//        }
//        return size.intValue();
//    }