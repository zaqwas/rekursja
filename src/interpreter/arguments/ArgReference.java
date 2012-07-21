package interpreter.arguments;

import java.math.BigInteger;

public abstract class ArgReference extends ArgIntOrRef {

    protected BigInteger valueAtTheEnd;

    public BigInteger getValueAtTheEnd() {
        return valueAtTheEnd;
    }
}
