package syntax.function;

import interpreter.accessvar.VariableType;

public interface FunctionDeclaration {

    public String getName();

    public int getArgumentsLength();

    public VariableType getArgumentType(int index);

}
