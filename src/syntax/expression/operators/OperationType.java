package syntax.expression.operators;

import java.math.BigInteger;

public enum OperationType {

    NOTHING,//in assigment
    NOT(0, 15, 12,
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.compareTo(BigInteger.ZERO) == 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    SUB_UNAR(0, 7, 4,
    new UnaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger expr) {
            return expr.negate();
        }
    }),
    MULT(1, 9, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.multiply(exprR);
        }
    }),
    DIV(1, 10, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.divide(exprR);
        }
    }),
    MOD(1, 11, 8,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.remainder(exprR);
        }
    }),
    PLUS(2, 5, 4,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.add(exprR);
        }
    }),
    SUB_BINAR(2, 6, 4,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.subtract(exprR);
        }
    }),
    GREATER(3, 19, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) > 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    LESS(3, 20, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) < 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    GREATER_EQ(3, 21, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) >= 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    LESS_EQ(3, 22, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) <= 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    EQUAL(4, 17, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) == 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    NOT_EQ(4, 18, 16,
    new BinaryEvaluator() {
        @Override
        public BigInteger eval(BigInteger exprL, BigInteger exprR) {
            return exprL.compareTo(exprR) != 0 ? BigInteger.ONE : BigInteger.ZERO;
        }
    }),
    AND(5, 13, 12,
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
    OR(6, 14, 12,
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
    BRACKET_TEMP(7);
    
    private int priority;
    private int statisticsOperationIndex;
    private int statisticsOperationGroupIndex;
    private UnaryEvaluator unary;
    private BinaryEvaluator binary;
    
    OperationType() {}
    OperationType(int priority) {
        this.priority = priority;
    }
    OperationType(int priority, int statisticsOperationIndex,
            int statisticsOperationGroupIndex, BinaryEvaluator binary) {
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.binary = binary;
    }
    OperationType(int priority, int statisticsOperationIndex,
            int statisticsOperationGroupIndex, UnaryEvaluator unary) {
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.unary = unary;
    }
    OperationType(int priority, int statisticsOperationIndex, int statisticsOperationGroupIndex, 
            BinaryEvaluator binary, UnaryEvaluator unary) {
        this.priority = priority;
        this.statisticsOperationIndex = statisticsOperationIndex;
        this.statisticsOperationGroupIndex = statisticsOperationGroupIndex;
        this.binary = binary;
        this.unary = unary;
    }
    
    public int getStatisticsOperationIndex() {
        return statisticsOperationIndex;
    }
    public int getStatisticsOperationGroupIndex() {
        return statisticsOperationGroupIndex;
    }
    
    public boolean isLogicOperation() {
        return this==OR || this==AND || this==NOT;
    }
    public int getPriority() {
        return priority;
    }
    public BigInteger eval(BigInteger leftValue, BigInteger rightValue) {
        return binary.eval(leftValue, rightValue);
    }
    public BigInteger eval(BigInteger value) {
        return unary.eval(value);
    }
    
    private interface BinaryEvaluator {
        public BigInteger eval(BigInteger leftValue, BigInteger rightValue);
    }
    private interface UnaryEvaluator {
        public BigInteger eval(BigInteger value);
    }
    
}