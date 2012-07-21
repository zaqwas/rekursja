package parser;


import interpreter.accessvar.VariableType;
import java.util.ArrayList;
import syntax.function.Function;
import syntax.SyntaxNode;
import syntax.SyntaxTree;
import syntax.statement.assigment.Assigment;
import syntax.expression.Call;
import syntax.expression.ExprSpecialTask;
import syntax.expression.SyntaxNodeExpr;
import syntax.expression.Variable;
import syntax.expression.operators.BinaryOperation;
import syntax.expression.operators.OperationType;
import syntax.expression.operators.UnaryOperation;
import syntax.statement.For;
import syntax.statement.If;
import syntax.statement.SyntaxNodeCond;
import syntax.statement.While;
import syntax.statement.assigment.IncDec;
import syntax.statement.other.BreakContinue;
import syntax.statement.other.Return;

public class JumpLinker {

    private static class Prev {

        public SyntaxNode sn;
        public int which;

        public Prev(SyntaxNode sn, int which) {
            this.sn = sn;
            this.which = which;
        }
    }

    private static void setJump(SyntaxNode sn, ArrayList<Prev> prev) {
        for (Prev p : prev) {
            if (p.which == 0) {
                p.sn.jump = sn;
            } else {
                ((SyntaxNodeCond) p.sn).jumpElse = sn;
            }
        }
        prev.clear();
    }

    private static void setJumpAndAdd(SyntaxNode sn, int which, ArrayList<Prev> prev) {
        setJump(sn, prev);
        prev.add(new Prev(sn, which));
    }
    
    private static void statmentlist(ArrayList<SyntaxNode> list, ArrayList<Prev> prev,
            ArrayList<Prev> continuePrev, ArrayList<Prev> breakPrev) {
        for (SyntaxNode sn : list) {
            statment(sn, prev, continuePrev, breakPrev);
        }
    }

    private static void expr(SyntaxNodeExpr sn, ArrayList<Prev> prev) {
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
            if ( call.name.equals("write") ) {
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
        setJumpAndAdd(sn, 0, prev);
    }

    private static void statment(SyntaxNode sn, ArrayList<Prev> prev,
            ArrayList<Prev> continuePrev, ArrayList<Prev> breakPrev) {
        if (sn instanceof Assigment) {
            Assigment a = (Assigment) sn;
            if (a.variable.arrayIndex != null) {
                expr(a.variable.arrayIndex, prev);
                if (a.operationType != OperationType.NOTHING) {
                    a.variable.arrayIndex.putValueOnStackTwice();
                }
            }
            if (a.operationType != OperationType.NOTHING) {
                setJumpAndAdd(a.variable, 0, prev);
                if (a.operationType == OperationType.AND) {
                    a.variable.parentIsLogicOperation(a, true);
                } else if (a.operationType == OperationType.OR) {
                    a.variable.parentIsLogicOperation(a, false);
                }
            }
            expr(a.expresion, prev);
            setJumpAndAdd(sn, 0, prev);
        } else if (sn instanceof IncDec) {
            IncDec inc = (IncDec) sn;
            if (inc.variable.arrayIndex != null) {
                expr(inc.variable.arrayIndex, prev);
                inc.variable.arrayIndex.putValueOnStackTwice();
            }
            setJumpAndAdd(inc.variable, 0, prev);
            setJumpAndAdd(sn, 0, prev);
        } else if (sn instanceof For) {
            For fr = (For) sn;
            ArrayList<Prev> continueNew = new ArrayList<Prev>();
            ArrayList<Prev> breakNew = new ArrayList<Prev>();
            statmentlist(fr.statementsInit, prev, null, null);
            prev.add(new Prev(sn, 1));//jumpElse=first node in condition, jumpElse as Tmp
            expr(fr.condition, prev);
            setJumpAndAdd(sn, 0, prev);
            statmentlist(fr.statements, prev, continueNew, breakNew);
            prev.addAll(continueNew);
            statmentlist(fr.statementsEnd, prev, null, null);
            setJump(fr.jumpElse, prev);
            fr.jumpElse = null;
            prev.add(new Prev(sn, 1));
            prev.addAll(breakNew);
        } else if (sn instanceof If) {
            If iif = (If) sn;
            expr(iif.condition, prev);
            setJumpAndAdd(sn, 0, prev);
            statmentlist(iif.statements, prev, continuePrev, breakPrev);
            ArrayList<Prev> prevNew = new ArrayList<Prev>();
            prevNew.add(new Prev(sn, 1));
            statmentlist(iif.statementsElse, prevNew, continuePrev, breakPrev);
            prev.addAll(prevNew);
        } else if (sn instanceof While) {
            While wh = (While) sn;
            ArrayList<Prev> continueNew = new ArrayList<Prev>();
            ArrayList<Prev> breakNew = new ArrayList<Prev>();
            if (wh.doWhile) {
                prev.add(new Prev(sn, 0));
                statmentlist(wh.statements, prev, continueNew, breakNew);
                prev.addAll(continueNew);
                expr(wh.condition, prev);
                setJumpAndAdd(sn, 1, prev);
                prev.addAll(breakNew);
            } else {
                prev.add(new Prev(sn, 1));//jumpElse=first node in condition, jumpElse as Tmp
                expr(wh.condition, prev);
                setJumpAndAdd(sn, 0, prev);
                statmentlist(wh.statements, prev, continueNew, breakNew);
                setJump(wh.jumpElse, prev);
                setJump(wh.jumpElse, continueNew);
                wh.jumpElse = null;
                prev.add(new Prev(sn, 1));
                prev.addAll(breakNew);
            }
        } else if (sn instanceof Return) {
            Return r = (Return) sn;
            if (r.expresion != null) {
                expr(r.expresion, prev);
                setJump(sn, prev);
                prev.add(new Prev(sn, 0));
            }
            setJump(sn, prev);
        } else if (sn instanceof BreakContinue) {
            BreakContinue bc = (BreakContinue) sn;
            if (bc.breakBool) {
                breakPrev.add(new Prev(sn, 0));
            } else {
                continuePrev.add(new Prev(sn, 0));
            }
            setJump(sn, prev);
        } else if (sn instanceof Call) {
            expr((Call) sn,prev);
        } else {
            Function f = (Function) sn;
            prev.add(new Prev(sn, 0));
            statmentlist(f.statements, prev, null, null);
        }
    }

    public static void doJumpLinks(SyntaxTree syntaxTree) {
        ArrayList<Prev> prev = new ArrayList<Prev>();
        for (int i = 0, size = syntaxTree.getFunctionsSize(); i < size; i++) {
            Function f = syntaxTree.getFunction(i);
            prev.clear();
            prev.add(new Prev(f, 0));
            statmentlist(f.statements, prev, null, null);
        }
    }
}
