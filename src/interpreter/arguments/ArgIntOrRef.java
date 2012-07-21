package interpreter.arguments;

import java.math.BigInteger;

public abstract class ArgIntOrRef extends Argument {

    protected BigInteger valueAtTheBeginning;

    public abstract BigInteger getValue();

    public abstract void setValue(BigInteger value);

    public abstract ArgReference getReference();

    public BigInteger getValueAtTheBeginning() {
        return valueAtTheBeginning;
    }
}
