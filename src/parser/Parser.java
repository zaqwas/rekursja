package parser;

import interpreter.accessvar.VariableScope;
import interpreter.accessvar.VariableType;
import syntax.statement.other.BreakContinue;
import syntax.function.Function;
import syntax.SyntaxNode;
import syntax.statement.If;
import syntax.statement.other.Return;
import syntax.SyntaxNodeIdx;
import syntax.statement.assigment.Assigment;
import syntax.expression.operators.BinaryOperation;
import syntax.expression.operators.BracketTemp;
import syntax.expression.Call;
import syntax.expression.Const;
import syntax.expression.operators.Operation;
import syntax.expression.StrParam;
import syntax.expression.operators.UnaryOperation;
import syntax.expression.Variable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;
import syntax.SyntaxTree;
import syntax.expression.SyntaxNodeExpr;
import syntax.expression.operators.OperationType;
import syntax.statement.assigment.IncDec;
import syntax.statement.For;
import syntax.statement.While;

public final class Parser {

    private static final BigInteger maxTab = new BigInteger("1000");
    private static String str;
    private static int index;
    private static ArrayList<Bracket> brackets;

    private static boolean keyWord(String s) {
        return s.equals("break") || s.equals("continue")
                || s.equals("do") || s.equals("if") || s.equals("else")
                || s.equals("for") || s.equals("int") || s.equals("return")
                || s.equals("void") || s.equals("while") || s.equals("var")
                || s.equals("read") || s.equals("write");
    }

    private static void skipBlank() throws ParseError {
        while (true) {
            char c = str.charAt(index);
            index++;
            if (c == '/') {
                c = str.charAt(index);
                index++;
                if (c == '/') {
                    while (index < str.length() && str.charAt(index) != '\n') {
                        index++;
                    }
                    if (index == str.length()) {
                        index--;
                    }
                } else if (c == '*') {
                    int indexBegin = index - 2;
                    int state = 0;
                    while (state != 2) {
                        c = str.charAt(index);
                        index++;
                        if (c == '*') {
                            state = 1;
                        } else if (c == '/' && state == 1) {
                            state = 2;
                        } else if (index == str.length()) {
                            index--;
                            break;
                        } else {
                            state = 0;
                        }
                    }
                    if (state != 2) {
                        throw new ParseError(1, indexBegin);
                    }
                } else {
                    index -= 2;
                    break;
                }
            } else if (Character.isWhitespace(c)) {
                continue;
            } else {
                index--;
                break;
            }
        }
    }

    private static String getStr() {
        int indexBegin = index;
        char c;
        boolean first = true;
        while ((c = str.charAt(index)) == '_' || (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z') || (!first && (c >= '0' && c <= '9'))) {
            index++;
            first = false;
        }
        return str.substring(indexBegin, index);
    }

    private static BigInteger parseInt() throws ParseError {
        int indexBegin = index;
        char c = str.charAt(index);
        index++;
        if (c == '0') {
            if (str.charAt(index) == 'x') {
                index++;
                indexBegin = index;
                while (((c = str.charAt(index)) >= '0' && c <= '9')
                        || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
                    index++;
                }
                if (index == indexBegin) {
                    throw new ParseError(2, index);
                }
                return new BigInteger(str.substring(indexBegin, index), 16);
            } else {
                while ((c = str.charAt(index)) >= '0' && c <= '7') {
                    index++;
                }
                if ((c = str.charAt(index)) == '8' || c == '9') {
                    throw new ParseError(3, index);
                }
                return new BigInteger(str.substring(indexBegin, index), 8);
            }
        } else if (c >= '1' && c <= '9') {
            while ((c = str.charAt(index)) >= '0' && c <= '9') {
                index++;
            }
            return new BigInteger(str.substring(indexBegin, index));
        } else {
            throw new ParseError(4, index - 1);
        }
    }

    private static void parseVar(Function function, SyntaxTree syntaxTree) throws ParseError {
        while (true) {
            skipBlank();
            int prevIndex = index;
            String name = getStr();
            if (name.equals("")) {
                throw new ParseError(5, index);
            }
            if (keyWord(name)) {
                throw new ParseError(6, prevIndex);
            }
            boolean alreadyDeclarated = function != null
                    ? function.checkDeclaration(name)
                    : syntaxTree.checkDeclaration(name);
            if (alreadyDeclarated) {
                throw new ParseError(7, prevIndex);
            }
            skipBlank();
            BigInteger size = null;
            char c = str.charAt(index++);
            if (c == '[') {
                skipBlank();
                prevIndex = index;
                size = parseInt();
                if (size.compareTo(BigInteger.ZERO) <= 0) {
                    throw new ParseError(8, prevIndex);
                }
                if (size.compareTo(maxTab) > 0) {
                    throw new ParseError(9, prevIndex);
                }
                skipBlank();
                if (str.charAt(index++) != ']') {
                    throw new ParseError(10, index - 1);
                }
                skipBlank();
                c = str.charAt(index++);
            }
            if ( function!=null ) {
                function.addLocalVar(name, size);
            } else {
                syntaxTree.addGlobalVar(name, size);
            }
            if (c == ';') {
                skipBlank();
                break;
            } else if (c != ',') {
                throw new ParseError(11, index - 1);
            }
        }
    }

    private static Function parseFunction(boolean integer) throws ParseError {
        skipBlank();
        int indexBegin = index;
        String name = getStr();
        if (name.equals("")) {
            throw new ParseError(12, index);
        }
        if (keyWord(name)) {
            throw new ParseError(13, indexBegin);
        }
        Function f = new Function(integer, name, indexBegin);

        skipBlank();
        Bracket br;
        if (str.charAt(index) != '(') {
            throw new ParseError(14, index);
        }
        br = new Bracket(index);
        index++;
        skipBlank();
        if (str.charAt(index) != ')') {
            while (true) {
                skipBlank();
                indexBegin = index;
                name = getStr();
                if (name.equals("")) {
                    throw new ParseError(15, index);
                }
                if (keyWord(name)) {
                    throw new ParseError(16, indexBegin);
                }
                if (f.checkDeclaration(name)) {
                    throw new ParseError(17, indexBegin);
                }
                VariableType type = VariableType.INTEGER;
                skipBlank();
                char c = str.charAt(index++);
                if (c == '&' || c == '[') {
                    if (c == '&') {
                        type = VariableType.REFERENCE;
                    } else {
                        skipBlank();
                        if (str.charAt(index++) != ']') {
                            throw new ParseError(10, index-1);
                        }
                        type = VariableType.ARRAY;
                    }
                    skipBlank();
                    c = str.charAt(index++);
                }
                f.addArgument(name, type);
                if (c == ')') {
                    break;
                } else if (c != ',') {
                    throw new ParseError(18, index - 1);
                }
            }
        } else {
            index++;
        }
        f.indexR = index;
        br.index2 = index - 1;
        brackets.add(br);

        skipBlank();
        indexBegin = index;
        String s = getStr();
        if (s.equals("var")) {
            parseVar(f, null);
        } else {
            if (!s.equals("")) {
                throw new ParseError(43, indexBegin);
            }
        }
        if (str.charAt(index) != '{') {
            throw new ParseError(21, index);
        }
        index++;
        parseBlock(f.statements, f.returnValue, false);
        return f;
    }

    private static void parseBlock(ArrayList<SyntaxNode> statements, boolean integer, boolean loop) throws ParseError {
        skipBlank();
        while (true) {
            if (str.charAt(index) == '}') {
                index++;
                skipBlank();
                return;
            }
            parseBlockOrStatement(statements, integer, true);
        }
    }

    private static SyntaxNode parseStatement(boolean integer, boolean loop) throws ParseError {
        int idx = index;
        String s = getStr();
        if (s.equals("")) {
            throw new ParseError(22, index);
        }

        if (s.equals("break") || s.equals("continue")) {
            if (!loop) {
                if (s.equals("break")) {
                    throw new ParseError(24, idx);
                } else {
                    throw new ParseError(25, idx);
                }
            }
            skipBlank();
            if (str.charAt(index) != ';') {
                throw new ParseError(23, index);
            }
            index++;
            skipBlank();
            if (s.equals("break")) {
                return new BreakContinue(true, idx);
            } else {
                return new BreakContinue(false, idx);
            }
        } else if (s.equals("return")) {
            Return ret = new Return(idx);
            if (integer) {
                ret.expresion = parseExpr();
            } else {
                skipBlank();
            }
            if (str.charAt(index) != ';') {
                throw new ParseError(23, index);
            }
            index++;
            skipBlank();
            return ret;
        } else if (s.equals("for")) {
            return parseFor(integer);
        } else if (s.equals("while")) {
            return parseWhile(integer);
        } else if (s.equals("do")) {
            return parseDoWhile(integer);
        } else if (s.equals("if")) {
            return parseIf(integer);
        } else {
            SyntaxNodeIdx sn = parseAssigmentOrCall(s, idx);
            if (str.charAt(index) != ';') {
                throw new ParseError(23, index);
            }
            index++;
            skipBlank();
            return sn;
        }
    }

    private static void parseBlockOrStatement(ArrayList<SyntaxNode> statements,
            boolean integer, boolean loop) throws ParseError {
        if (str.charAt(index) != ';') {
            if (str.charAt(index) == '{') {
                index++;
                parseBlock(statements, integer, true);
            } else {
                statements.add(parseStatement(integer, true));
            }
        } else {
            index++;
            skipBlank();
        }
    }

    private static SyntaxNodeExpr parseCond() throws ParseError {
        SyntaxNodeExpr cond;
        skipBlank();
        if (str.charAt(index) != '(') {
            throw new ParseError(14, index);
        }
        index++;
        cond = parseExpr();
        if (str.charAt(index) != ')') {
            throw new ParseError(41, index);
        }
        index++;
        skipBlank();
        return cond;
    }

    private static For parseFor(boolean integer) throws ParseError {
        For fr = new For();
        skipBlank();
        if (str.charAt(index) != '(') {
            throw new ParseError(14, index);
        }
        index++;
        skipBlank();
        if (str.charAt(index) != ';') {
            while (true) {
                fr.statementsInit.add(parseAssigmentOrCall(null, 0));
                char c = str.charAt(index);
                if (c == ',') {
                    index++;
                    skipBlank();
                } else if (c == ';') {
                    break;
                } else {
                    throw new ParseError(11, index);
                }
            }
        }
        index++;
        fr.condition = parseExpr();
        if (str.charAt(index) != ';') {
            throw new ParseError(23, index);
        }
        index++;
        skipBlank();
        if (str.charAt(index) != ')') {
            while (true) {
                fr.statementsEnd.add(parseAssigmentOrCall(null, 0));
                char c = str.charAt(index);
                if (c == ',') {
                    skipBlank();
                    index++;
                } else if (c == ')') {
                    break;
                } else {
                    throw new ParseError(18, index);
                }
            }
        }
        index++;
        skipBlank();
        parseBlockOrStatement(fr.statements, integer, true);

        return fr;
    }

    private static While parseWhile(boolean integer) throws ParseError {
        While wh = new While(false);
        wh.condition = parseCond();
        parseBlockOrStatement(wh.statements, integer, true);
        return wh;
    }

    private static While parseDoWhile(boolean integer) throws ParseError {
        While wh = new While(true);
        skipBlank();
        parseBlockOrStatement(wh.statements, integer, true);
        String s = getStr();
        if (!s.equals("while")) {
            throw new ParseError(42, index);
        }
        wh.condition = parseCond();
        if (str.charAt(index) != ';') {
            throw new ParseError(23, index);
        }
        index++;
        skipBlank();
        return wh;
    }

    private static If parseIf(boolean integer) throws ParseError {
        If iif = new If();
        iif.condition = parseCond();
        parseBlockOrStatement(iif.statements, integer, true);
        int idx = index;
        String s = getStr();
        if (s.equals("else")) {
            skipBlank();
            parseBlockOrStatement(iif.statementsElse, integer, true);
        } else {
            index = idx;
        }
        return iif;
    }

    private static SyntaxNodeExpr parseExpr() throws ParseError {
        Stack<Operation> sOper = new Stack<Operation>();
        Stack<SyntaxNodeExpr> sVar = new Stack<SyntaxNodeExpr>();
        boolean nextOper = false;

        while (true) {
            skipBlank();
            char c = str.charAt(index);
            index++;
            if (nextOper) {
                Operation op = null;
                if (c == '*') {
                    op = new BinaryOperation(OperationType.MULT);
                } else if (c == '/') {
                    op = new BinaryOperation(OperationType.DIV);
                } else if (c == '%') {
                    op = new BinaryOperation(OperationType.MOD);
                } else if (c == '+') {
                    op = new BinaryOperation(OperationType.PLUS);
                } else if (c == '-') {
                    op = new BinaryOperation(OperationType.SUB_BINAR);
                } else if (c == '<') {
                    if (str.charAt(index) == '=') {
                        index++;
                        op = new BinaryOperation(OperationType.LESS_EQ);
                    } else {
                        op = new BinaryOperation(OperationType.LESS);
                    }
                } else if (c == '>') {
                    if (str.charAt(index) == '=') {
                        index++;
                        op = new BinaryOperation(OperationType.GREATER_EQ);
                    } else {
                        op = new BinaryOperation(OperationType.GREATER);
                    }
                } else if (c == '=') {
                    if (str.charAt(index) != '=') {
                        throw new ParseError(27, index);
                    }
                    index++;
                    op = new BinaryOperation(OperationType.EQUAL);
                } else if (c == '!') {
                    if (str.charAt(index) != '=') {
                        throw new ParseError(27, index);
                    }
                    index++;
                    op = new BinaryOperation(OperationType.NOT_EQ);
                } else if (c == '&') {
                    if (str.charAt(index) != '&') {
                        throw new ParseError(28, index);
                    }
                    index++;
                    op = new BinaryOperation(OperationType.AND);
                } else if (c == '|') {
                    if (str.charAt(index) != '|') {
                        throw new ParseError(29, index);
                    }
                    index++;
                    op = new BinaryOperation(OperationType.OR);
                }

                int priority = 6;
                if (op != null) {
                    priority = op.getPriority();
                }

                SyntaxNodeExpr right = null;
                BinaryOperation binarOp;
                while (!sOper.empty() && sOper.peek().getPriority() <= priority) {
                    if (right == null) {
                        right = sVar.pop();
                    }
                    binarOp = (BinaryOperation) sOper.pop();
                    SyntaxNodeExpr left = sVar.pop();
                    binarOp.expresionL = left;
                    binarOp.expresionR = right;
                    binarOp.indexL = left.indexL;
                    binarOp.indexR = right.indexR;
                    right = binarOp;
                }

                if (c == ')' && !sOper.empty()) {
                    if (right == null) {
                        right = sVar.pop();
                    }
                    BracketTemp brTmp = (BracketTemp) sOper.pop();
                    right.indexL = brTmp.indexL;
                    right.indexR = index;

                    while (!sOper.empty() && sOper.peek().getPriority() == 0) {
                        UnaryOperation uo = (UnaryOperation) sOper.pop();
                        uo.expresion = right;
                        uo.indexR = right.indexR;
                        right = uo;
                    }

                    sVar.add(right);
                } else if (op != null) {
                    if (right != null) {
                        sVar.add(right);
                    }
                    sOper.add(op);
                    nextOper = false;
                } else {
                    index--;
                    if (!sOper.empty()) {
                        throw new ParseError(30, index);
                    }
                    if (right == null) {
                        return sVar.pop();
                    }
                    return right;
                }
            } else {
                if (c == '(') {
                    sOper.push(new BracketTemp(index - 1));
                } else if (c == '-') {
                    if (str.charAt(index - 2) == '-') {
                        throw new ParseError(31, index);
                    }
                    sOper.push(new UnaryOperation(OperationType.SUB_UNAR, index - 1));
                } else if (c == '!') {
                    sOper.push(new UnaryOperation(OperationType.NOT, index - 1));
                } else {
                    SyntaxNodeExpr newVar;
                    if (c >= '0' && c <= '9') {
                        index--;
                        int indexBegin = index;
                        BigInteger value = parseInt();
                        newVar = new Const(value, indexBegin, index);
                    } else {
                        index--;
                        newVar = parseVarOrCall(null, 0, true);
                    }
                    while (!sOper.empty() && sOper.peek().getPriority() == 0) {
                        UnaryOperation uo = (UnaryOperation) sOper.pop();
                        uo.expresion = newVar;
                        uo.indexR = newVar.indexR;
                        newVar = uo;
                    }
                    sVar.add(newVar);
                    nextOper = true;
                }
            }
        }
    }

    private static String parseStrParam() throws ParseError {
        int indexBegin = index - 1;
        StringBuilder sb = new StringBuilder();
        boolean slash = false;
        while (index < str.length()) {
            char c = str.charAt(index);
            index++;
            if (slash) {
                if (c == 'n') {
                    sb.append('\n');
                } else if (c == 't') {
                    sb.append("   ");
                } else if (c == '"' || c == '\\') {
                    sb.append(c);
                } else {
                    throw new ParseError(32, index);
                }
                slash = false;
            } else {
                if (c == '\\') {
                    slash = true;
                } else if (c == '"') {
                    return sb.toString();
                } else {
                    sb.append(c);
                }
            }
        }
        throw new ParseError(33, indexBegin);
    }

    private static SyntaxNodeExpr parseVarOrCall(String s, int idx, boolean callInExpr) throws ParseError {
        if (s == null) {
            idx = index;
            s = getStr();
            if (s.equals("")) {
                if (callInExpr) {
                    throw new ParseError(26, index);
                } else {
                    throw new ParseError(40, index);
                }
            }
        }

        boolean writeOrRead = false, isCall = false, write = false;
        int indexTemp = index;
        skipBlank();
        char c = str.charAt(index);
        index++;

        if (s.equals("write") || s.equals("read")) {
            if (callInExpr) {
                throw new ParseError(34, index);
            }
            skipBlank();
            if (c != '(') {
                throw new ParseError(14, index);
            }
            write = s.equals("write");
            writeOrRead = true;
        } else {
            if (keyWord(s)) {
                throw new ParseError(35, idx);
            }
            if (c == '(') {
                isCall = true;
            }
        }

        if (isCall || writeOrRead) {
            Call cl = new Call(s, idx, callInExpr);
            skipBlank();
            if (str.charAt(index) != ')') {
                while (true) {
                    if (write && str.charAt(index) == '"') {
                        int indexBegin = index;
                        index++;
                        String strParam = parseStrParam();
                        cl.params.add(new StrParam(strParam, indexBegin, index));
                        skipBlank();
                    } else {
                        cl.params.add(parseExpr());
                    }

                    c = str.charAt(index);
                    index++;
                    if (c == ')') {
                        cl.indexR = index;
                        return cl;
                    } else if (c != ',') {
                        throw new ParseError(18, index - 1);
                    }
                    skipBlank();
                }
            } else {
                if (writeOrRead) {
                    throw new ParseError(39, index - 1);
                }
                index++;
                cl.indexR = index;
                return cl;
            }
        } else {
            Variable v = new Variable(s, idx, indexTemp);
            if (c == '[') {
                v.arrayIndex = parseExpr();
                if (str.charAt(index) != ']') {
                    throw new ParseError(10, index);
                }
                index++;
                v.indexR = index;
            } else {
                index--;
            }
            return v;
        }
    }

    private static SyntaxNodeIdx parseAssigmentOrCall(String s, int idx) throws ParseError {
        SyntaxNodeIdx sn = parseVarOrCall(s, idx, false);
        if (sn instanceof Call) {//is call
            skipBlank();
            return sn;
        } else {
            skipBlank();
            char c = str.charAt(index++);

            OperationType operType;
            if (c == '=') {
                operType = OperationType.NOTHING;
            } else {
                if (c == '+' || c == '-') {
                    char c2 = str.charAt(index++);
                    boolean inc = c == '+';
                    if (c2 == c) {
                        IncDec incDec = new IncDec(inc, (Variable) sn, sn.indexL, index);
                        skipBlank();
                        return incDec;
                    } else if (c2 == '=') {
                        operType = inc ? OperationType.PLUS : OperationType.SUB_BINAR;
                    } else {
                        throw new ParseError(inc ? 36 : 37, index - 1);
                    }
                } else {
                    if (c == '*') {
                        operType = OperationType.MULT;
                    } else if (c == '/') {
                        operType = OperationType.DIV;
                    } else if (c == '%') {
                        operType = OperationType.MULT;
                    } else if (c == '&') {
                        operType = OperationType.AND;
                    } else if (c == '|') {
                        operType = OperationType.OR;
                    } else {
                        throw new ParseError(38, index - 1);
                    }
                    if (str.charAt(index++) != '=') {
                        throw new ParseError(27, index - 1);
                    }
                }
            }
            SyntaxNodeExpr expr = parseExpr();
            return new Assigment(operType, (Variable) sn, expr, sn.indexL, expr.indexR);
        }
    }

    public static SyntaxTree parse(String str) throws ParseError {
        try {
            boolean nothingDeclared = true;
            int indexBegin;

            Parser.str = str + ((char) 0);
            index = 0;

            SyntaxTree syntaxTree = new SyntaxTree();

            brackets = new ArrayList<Bracket>();

            skipBlank();
            indexBegin = index;
            String s = getStr();
            if (s.equals("var")) {
                parseVar(null, syntaxTree);
                nothingDeclared = false;
                skipBlank();
                indexBegin = index;
                s = getStr();
            }

            while (true) {
                if (s.equals("") || (!s.equals("int") && !s.equals("void"))) {
                    if (s.equals("") && index >= str.length() - 1) {
                        break;
                    }
                    if (nothingDeclared) {
                        throw new ParseError(19, indexBegin);
                    } else {
                        throw new ParseError(20, indexBegin);
                    }
                } else {
                    Function f = parseFunction(s.equals("int"));
                    syntaxTree.addFunction(f);
                    nothingDeclared = false;
                    indexBegin = index;
                    s = getStr();
                }
            }
            SpecialFunctions.checkSyntaxTree(syntaxTree);
            
            CheckConstraints.check(syntaxTree);
            JumpLinker.doJumpLinks(syntaxTree);
            return syntaxTree;
        } finally {
            Parser.str = null;
            index = 0;
        }
    }
}
