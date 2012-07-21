package interpreter.arguments;

import java.math.BigInteger;

public class ArgArray extends Argument {

    protected BigInteger[] array;
    protected BigInteger sizeBigInt;
    protected int sizeInteger;
    protected int indexFirst;
    
    public ArgArray(BigInteger[] array, BigInteger size, int indexFirst) {
        this.array = array;
        sizeBigInt = size;
        sizeInteger = size.intValue();
        this.indexFirst = indexFirst;
    }
    
    @Override
    public void clear() {
        array = null;
        sizeBigInt = null;
    }

    public BigInteger getValue(BigInteger index) {
        int indexInt = index.intValue();
        return array[indexFirst + indexInt];
    }

    public BigInteger getValue(int index) {
        BigInteger[] arr = array;
        return arr == null ? null : arr[indexFirst + index];
    }

    public void setValue(BigInteger index, BigInteger value) {
        int indexInt = index.intValue();
        array[indexFirst + indexInt] = value;
    }

    public ArgReference getReference(BigInteger index) {
        int indexInt = index.intValue();
        return new ArgRefNormal(array, indexFirst + indexInt);
    }

    public ArgArray getArrayPtr() {
        return this;
    }

    public BigInteger getSizeBigInt() {
        return sizeBigInt;
    }

    public int getSizeInteger() {
        return sizeInteger;
    }

}
