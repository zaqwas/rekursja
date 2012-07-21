package interpreter.arguments;

public abstract class Argument {
    
    public boolean isInteger() {
        return this instanceof ArgInteger;
    }
    public boolean isReference() {
        return this instanceof ArgReference;
    }
    public boolean isArray() {
        return this instanceof ArgArray;
    }
    public boolean isString() {
        return this instanceof ArgString;
    }
    public boolean isIntOrRef() {
        return this instanceof ArgIntOrRef;
    }
    
    public abstract void clear();

}
