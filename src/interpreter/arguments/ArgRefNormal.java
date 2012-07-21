package interpreter.arguments;

import java.math.BigInteger;

public class ArgRefNormal extends ArgReference {

    protected BigInteger[] array;
    protected int index;

    public ArgRefNormal(BigInteger[] array, int index) {
        this.array = array;
        this.index = index;
        this.valueAtTheBeginning = array[index];
    }
    
    @Override
    public void clear() {
        valueAtTheEnd = array[index];
        array = null;
    }

    @Override
    public BigInteger getValue() {
        BigInteger[] arr = array;
        return arr == null ? null : arr[index];
    }

    @Override
    public void setValue(BigInteger value) {
        array[index] = value;
    }

    @Override
    public ArgReference getReference() {
        return new ArgRefNormal(array, index);
    }
}
