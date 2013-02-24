package parser;


import interpreter.accessvar.VariableType;
import java.util.ArrayList;
import syntax.SyntaxNode;
import syntax.SyntaxTree;
import syntax.expression.Call;
import syntax.expression.SyntaxNodeExpr;
import syntax.expression.Variable;
import syntax.expression.operators.BinaryOperation;
import syntax.expression.operators.OperationType;
import syntax.expression.operators.UnaryOperation;
import syntax.function.Function;
import syntax.statement.For;
import syntax.statement.If;
import syntax.statement.SyntaxNodeCond;
import syntax.statement.While;
import syntax.statement.assigment.Assigment;
import syntax.statement.assigment.IncDec;
import syntax.statement.other.BreakContinue;
import syntax.statement.other.Return;

class JumpLinker {

    private static class PrevNode {

        public SyntaxNode sn;
        public boolean elseInCond;

        public PrevNode(SyntaxNode sn, boolean elseInCond) {
            this.sn = sn;
            this.elseInCond = elseInCond;
        }
    }

    private static void setJump(SyntaxNode sn, ArrayList<PrevNode> prev) {
        for (PrevNode p : prev) {
            if (p.elseInCond) {
                ((SyntaxNodeCond) p.sn).jumpElse = sn;
            } else {
                p.sn.jump = sn;
            }
        }
        prev.clear();
    }

    private static void setJumpAndAdd(SyntaxNode sn, boolean elseInCond, ArrayList<PrevNode> prev) {
        setJump(sn, prev);
        prev.add(new PrevNode(sn, elseInCond));
    }
    
    private static void statmentlist(ArrayList<SyntaxNode> list, ArrayList<PrevNode> prev,
            ArrayList<PrevNode> continuePrev, ArrayList<PrevNode> breakPrev) {
        for (SyntaxNode sn : list) {
            statment(sn, prev, continuePrev, breakPrev);
        }
    }

    private static void expr(SyntaxNodeExpr sn, ArrayList<PrevNode> prev) {
        if (sn instanceof UnaryOperation) {
            UnaryOperation uo = (UnaryOperation) sn;
            expr(uo.expresion, prev);
        } else if (sn instanceof BinaryOperation) {
            BinaryOperation bo = (BinaryOperation) sn;
            expr(bo.expresionL, prev);
            expr(bo.expresionR, prev);
            if (bo.operationType == OperationType.AND) {
                bo.expresionL.parentIsLogicOperation(bo, true);
            } else if (bo.operationType == OperationType.OR) {
                bo.expresionL.parentIsLogicOperation(bo, false);
            }
        } else if (sn instanceof Call) {
            Call call = (Call) sn;
            int paramIdx = 0;
            if ( call.name.equals("write") || call.name.equals("writeln") ) {
                for (SyntaxNodeExpr param : call.params) {
                    expr(param, prev);
                    int idx = paramIdx == 0 ? -call.getArgumentsLength() : paramIdx;
                    param.thisNodeIsParameter(idx, VariableType.INTEGER);
                    paramIdx++;
                }
            } else {
                for (SyntaxNodeExpr param : call.params) {
                    expr(param, prev);
                    int idx = paramIdx == 0 ? -call.getArgumentsLength() : paramIdx;
                    param.thisNodeIsParameter(idx, call.getFunction().getArgumentType(paramIdx));
                    paramIdx++;
                }
            }
        } else if (sn instanceof Variable) {
            Variable v = (Variable) sn;
            if (v.arrayIndex != null) {
                expr(v.arrayIndex, prev);
            }

        }
        setJumpAndAdd(sn, false, prev);
    }

    private static void statment(SyntaxNode sn, ArrayList<PrevNode> prev,
            ArrayList<PrevNode> continuePrev, ArrayList<PrevNode> breakPrev) {
        if (sn instanceof Assigment) {
            Assigment a = (Assigment) sn;
            if (a.variable.arrayIndex != null) {
                expr(a.variable.arrayIndex, prev);
            }
            expr(a.expresion, prev);
            setJumpAndAdd(sn, false, prev);
        } else if (sn instanceof IncDec) {
            IncDec inc = (IncDec) sn;
            if (inc.variable.arrayIndex != null) {
                expr(inc.variable.arrayIndex, prev);
            }
            setJumpAndAdd(sn, false, prev);
        } else if (sn instanceof For) {
            For fr = (For) sn;
            ArrayList<PrevNode> continueNew = new ArrayList<>();
            ArrayList<PrevNode> breakNew = new ArrayList<>();
            statmentlist(fr.statementsInit, prev, null, null);
            prev.add(new PrevNode(sn, true));//jumpElse=first node in condition, jumpElse as Tmp
            expr(fr.condition, prev);
            setJumpAndAdd(sn, false, prev);
            statmentlist(fr.statements, prev, continueNew, breakNew);
            prev.addAll(continueNew);
            statmentlist(fr.statementsEnd, prev, null, null);
            setJump(fr.jumpElse, prev);
            fr.jumpElse = null;
            prev.add(new PrevNode(sn, true));
            prev.addAll(breakNew);
        } else if (sn instanceof If) {
            If iif = (If) sn;
            expr(iif.condition, prev);
            setJumpAndAdd(sn, false, prev);
            statmentlist(iif.statements, prev, continuePrev, breakPrev);
            ArrayList<PrevNode> prevNew = new ArrayList<>();
            prevNew.add(new PrevNode(sn, true));
            statmentlist(iif.statementsElse, prevNew, continuePrev, breakPrev);
            prev.addAll(prevNew);
        } else if (sn instanceof While) {
            While wh = (While) sn;
            ArrayList<PrevNode> continueNew = new ArrayList<>();
            ArrayList<PrevNode> breakNew = new ArrayList<>();
            if (wh.doWhile) {
                prev.add(new PrevNode(sn, false));
                statmentlist(wh.statements, prev, continueNew, breakNew);
                prev.addAll(continueNew);
                expr(wh.condition, prev);
                setJumpAndAdd(sn, true, prev);
                prev.addAll(breakNew);
            } else {
                prev.add(new PrevNode(sn, true));//jumpElse=first node in condition, jumpElse as Tmp
                expr(wh.condition, prev);
                setJumpAndAdd(sn, false, prev);
                statmentlist(wh.statements, prev, continueNew, breakNew);
                setJump(wh.jumpElse, prev);
                setJump(wh.jumpElse, continueNew);
                wh.jumpElse = null;
                prev.add(new PrevNode(sn, true));
                prev.addAll(breakNew);
            }
        } else if (sn instanceof Return) {
            Return r = (Return) sn;
            if (r.expresion != null) {
                expr(r.expresion, prev);
                setJump(sn, prev);
                prev.add(new PrevNode(sn, false));
            }
            setJump(sn, prev);
        } else if (sn instanceof BreakContinue) {
            BreakContinue bc = (BreakContinue) sn;
            if (bc.breakBool) {
                breakPrev.add(new PrevNode(sn, false));
            } else {
                continuePrev.add(new PrevNode(sn, false));
            }
            setJump(sn, prev);
        } else if (sn instanceof Call) {
            expr((Call) sn,prev);
        } else {
            Function f = (Function) sn;
            prev.add(new PrevNode(sn, false));
            statmentlist(f.statements, prev, null, null);
        }
    }

    public static void doJumpLinks(SyntaxTree syntaxTree) {
        ArrayList<PrevNode> prev = new ArrayList<>();
        for (int i = 0, size = syntaxTree.getFunctionsSize(); i < size; i++) {
            Function f = syntaxTree.getFunction(i);
            prev.clear();
            prev.add(new PrevNode(f, false));
            statmentlist(f.statements, prev, null, null);
        }
    }
}
