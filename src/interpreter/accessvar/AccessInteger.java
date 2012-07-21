package interpreter.accessvar;

import interpreter.Instance;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;

public abstract class AccessInteger extends AccessVar {
    
    @Override
    public VariableType getVariableType() {
        return VariableType.INTEGER;
    }
    
    public abstract BigInteger getValue(Instance instance);
    
    public abstract void setValue(Instance instance, BigInteger value);
    
    public abstract ArgReference getReference(Instance instance);
    
}
