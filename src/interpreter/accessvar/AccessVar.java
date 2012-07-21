package interpreter.accessvar;

public abstract class AccessVar {

    public abstract VariableType getVariableType();

    public abstract VariableScope getVariableScope();

    public final boolean isArray() {
        return VariableType.ARRAY == getVariableType();
    }

}
