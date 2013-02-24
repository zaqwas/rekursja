package syntax.expression.operators;

import java.math.BigInteger;
import parser.ProgramError;

public enum OperationType {

    NOTHING,//in assigment
    
    //<editor-fold defaultstate="collapsed" desc="NOT, SUB_UNAR">
    NOT("!", 0, 15, 12,
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.signum() == 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    SUB_UNAR("-", 0, 7, 4,
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.negate();
        }
    }),
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MULT, DIV, MOD">
    MULT("*", 1, 9, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.multiply(exprR);
        }
    }),
    DIV("/", 1, 10, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) throws ProgramError {
            if (exprR.signum() == 0) {
                throw new ProgramError(Lang.dividedByZero);
            }
            return exprL.divide(exprR);
        }
    }),
    MOD("%", 1, 11, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) throws ProgramError {
            if (exprR.signum() == 0) {
                throw new ProgramError(Lang.reminderOfDisionByZero);
            }
            return exprL.remainder(exprR);
        }
    }),
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PLUS, SUB_BINAR">
    PLUS("+", 2, 5, 4,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.add(exprR);
        }
    }),
    SUB_BINAR("-", 2, 6, 4,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.subtract(exprR);
        }
    }),
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="COMPARE ">
    GREATER(">", 3, 19, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) > 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    LESS("<", 3, 20, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) < 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    GREATER_EQ(">=", 3, 21, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) >= 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    LESS_EQ("<=", 3, 22, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) <= 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    EQUAL("==", 4, 17, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) == 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    NOT_EQ("!=", 4, 18, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) != 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AND, OR">
    AND("&&", 5, 13, 12,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(BigInteger.ZERO) != 0 && exprR.compareTo(BigInteger.ZERO) != 0
                        ? BigInteger.ONE : BigInteger.ZERO;
        }
    },
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.compareTo(BigInteger.ZERO) != 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    OR("||", 6, 14, 12,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(BigInteger.ZERO) != 0 || exprR.compareTo(BigInteger.ZERO) != 0
                        ? BigInteger.ONE : BigInteger.ZERO;
        }
    },
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.compareTo(BigInteger.ZERO) != 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    //</editor-fold>
    
    BRACKET_TEMP(7);
    
    //<editor-fold defaultstate="collapsed" desc="Consturctos and variables">
    private int priority;
    private int statisticsOperationIndex;
    private int statisticsOperationGroupIndex;
    private UnaryEvaluator unary;
    private BinaryEvaluator binary;
    private String operatorString;
    
    OperationType() {}
    OperationType(int priority) {
        this.priority = priority;
    }
    OperationType(String operatorString, int priority, int statisticsOperationIndex,
            int statisticsOperationGroupIndex, BinaryEvaluator binary) {
        this.operatorString = operatorString;
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.binary = binary;
    }
    OperationType(String operatorString, int priority, int statisticsOperationIndex,
            int statisticsOperationGroupIndex, UnaryEvaluator unary) {
        this.operatorString = operatorString;
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.unary = unary;
    }
    OperationType(String operatorString, int priority, int statisticsOperationIndex, 
            int statisticsOperationGroupIndex, 
            BinaryEvaluator binary, UnaryEvaluator unary) {
        this.operatorString = operatorString;
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.binary = binary;
        this.unary = unary;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="public functions">
    public BigInteger eval(BigInteger leftValue, BigInteger rightValue)
            throws ProgramError {
        return binary.eval(leftValue, rightValue);
    }
    public BigInteger eval(BigInteger value) {
        return unary.eval(value);
    }
    
    public boolean isLogicOperation() {
        return this == OR || this == AND || this == NOT;
    }
    public int getPriority() {
        return priority;
    }
    
    public int getStatisticsOperationIndex() {
        return statisticsOperationIndex;
    }
    public int getStatisticsOperationGroupIndex() {
        return statisticsOperationGroupIndex;
    }
    
    @Override
    public String toString() {
        return operatorString;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Evaluator interfaces">
    private interface BinaryEvaluator {
        public BigInteger eval(BigInteger leftValue, BigInteger rightValue) throws ProgramError;
    }
    private interface UnaryEvaluator {
        public BigInteger eval(BigInteger value);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String dividedByZero = "Dzielenie przez zero";
        public static final String reminderOfDisionByZero = "Obliczanie reszty z dzielenia przez zero";
    }
    //</editor-fold>
    
}