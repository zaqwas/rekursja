package syntax.function;

import console.Console;
import interpreter.Instance;
import interpreter.arguments.ArgIntOrRef;
import interpreter.arguments.ArgString;
import interpreter.arguments.Argument;
import syntax.SyntaxNode;

public class FunctionWrite extends Function {
    public FunctionWrite() {
        this.name = "write";
        this.returnValue = false;
        
        funcitonBehavior = new FunctionBehavior() {
            {
                function = FunctionWrite.this;
            }
        };
    }
    
    
    @Override
    public String getArgumentName(int index) {
        return "param " + index;
    }
    
    @Override
    public int getLocalVarsLength() {
        return 0;
    }
    
    private static Console console;
    public static void setConsole(Console console) {
        FunctionWrite.console = console;
    }
    
    private static final FunctionWrite instance = new FunctionWrite();
    public static FunctionWrite getInstance() {
        return instance;
    }
    
    @Override
    public SyntaxNode commit(Instance instance) {
        //instance.debug_print();
        for (Argument arg : instance.arrayArgs) {
            if (arg instanceof ArgIntOrRef) {
                console.append( ((ArgIntOrRef) arg).getValue().toString() );
            } else {
                console.append( ((ArgString) arg).getString() );
            }
        }
        
        return jump;
    }
}
