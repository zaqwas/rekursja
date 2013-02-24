package interpreter.accessvar;

import interpreter.Instance;
import interpreter.arguments.ArgArray;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import parser.ProgramError;
import syntax.expression.SyntaxNodeExpr;

public abstract class AccessArray extends AccessVar {
    
    @Override
    public final VariableType getVariableType() {
        return VariableType.ARRAY;
    }

    public abstract BigInteger getValue(Instance instance, BigInteger index);

    public abstract BigInteger getValue(Instance instance, int index);

    public abstract void setValue(Instance instance, BigInteger index, BigInteger value);

    public abstract ArgReference getReference(Instance instance, BigInteger index);

    public abstract ArgArray getArrayPtr(Instance instance);

    public abstract BigInteger getSizeBigInt(Instance instance);

    public abstract int getSizeInteger(Instance instance);

    public final void checkIndex(Instance instance, BigInteger index, SyntaxNodeExpr arrayIndex)
            throws ProgramError {
        if (index.signum() < 0) {
            throw new ProgramError(Lang.indexNegativ, arrayIndex.getLeftIndex(), arrayIndex.getRightIndex());
        }
        if (index.compareTo(getSizeBigInt(instance)) >= 0) {
            String str = String.format(Lang.indexExceedSize, getSizeInteger(instance));
            throw new ProgramError(str, arrayIndex.getLeftIndex(), arrayIndex.getRightIndex());
        }
    }
    
    public final boolean checkIndex(Instance instance, BigInteger index) {
        return index.signum() >= 0 && index.compareTo(getSizeBigInt(instance)) < 0;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String indexNegativ = "Indeks tablicy jest ujemny";
        public static final String indexExceedSize = "Indeks tablicy przekracza jej rozmiar (%d)";
    }
    //</editor-fold>
    
}
