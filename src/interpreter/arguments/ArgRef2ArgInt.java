package interpreter.arguments;

import java.math.BigInteger;

public class ArgRef2ArgInt extends ArgReference {

    protected ArgInteger argInt;

    public ArgRef2ArgInt(ArgInteger argInt) {
        this.argInt = argInt;
        this.valueAtTheBeginning = argInt.getValue();
    }
    
    @Override
    public void clear() {
        valueAtTheEnd = argInt.getValue();
        argInt = null;
    }

    @Override
    public BigInteger getValue() {
        ArgInteger arg = argInt;
        return arg == null ? null : arg.getValue();
    }

    @Override
    public void setValue(BigInteger value) {
        argInt.setValue(value);
    }

    @Override
    public ArgReference getReference() {
        return new ArgRef2ArgInt(argInt);
    }
}
