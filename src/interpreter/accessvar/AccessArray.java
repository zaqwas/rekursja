package interpreter.accessvar;

import interpreter.Instance;
import interpreter.arguments.ArgArray;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;

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

    public final int checkIndex(Instance instance, BigInteger index) {
        return index.compareTo(BigInteger.ZERO) < 0 ? -1
                : (index.compareTo(getSizeBigInt(instance)) >= 0 ? 1 : 0);
    }

}
