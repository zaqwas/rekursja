package interpreter.arguments;

import java.math.BigInteger;

public class ArgInteger extends ArgIntOrRef {

    protected BigInteger current;

    public ArgInteger(BigInteger value) {
        this.current = value;
        this.valueAtTheBeginning = value;
    }
    
    @Override
    public void clear() {
        current = null;
    }

    @Override
    public BigInteger getValue() {
        return current;
    }

    @Override
    public void setValue(BigInteger value) {
        this.current = value;
    }

    @Override
    public ArgReference getReference() {
        return new ArgRef2ArgInt(this);
    }
}
