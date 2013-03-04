package syntax.function;

import console.Console;
import interpreter.Instance;
import interpreter.arguments.ArgIntOrRef;
import interpreter.arguments.ArgString;
import interpreter.arguments.Argument;
import stringcreator.SimpleLazyStringCreator;
import stringcreator.StringCreator;
import syntax.SyntaxNode;

public class FunctionWrite extends Function {
    
    private static final FunctionWrite instance = new FunctionWrite(false);
    private static final FunctionWrite instanceLn = new FunctionWrite(true);
    
    public static FunctionWrite getInstance(boolean writeln) {
        return writeln ? instanceLn : instance;
    }
    
    private static Console console;
    public static void setConsole(Console console) {
        FunctionWrite.console = console;
    }
    
    
    private boolean writeln;
    
    private FunctionWrite(final boolean writeln) {
        this.name = writeln ? "writeln" : "write";
        this.writeln = writeln;
        this.returnValue = false;
        
        funcitonBehavior = new FunctionWriteBehavior();
    }
    
    @Override
    public String getArgumentName(int index) {
        return "param " + index;
    }
    
    @Override
    public int getLocalVarsLength() {
        return 0;
    }
    

    private class FunctionWriteBehavior extends FunctionBehavior {
        public FunctionWriteBehavior() {
            function = FunctionWrite.this;
        }

        @Override
        public boolean isStoppedBeforeCall() {
            return false;
        }

        @Override
        public StringCreator getStatusCreatorAfterCall(Instance instance) {
            return new SimpleLazyStringCreator("Wypisz tekst");
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
            if (instance.arrayArgs != null) {
                for (Argument arg : instance.arrayArgs) {
                    if (arg instanceof ArgIntOrRef) {
                        console.append(((ArgIntOrRef) arg).getValue().toString());
                    } else {
                        console.append(((ArgString) arg).getString());
                    }
                }
            }
            if (writeln) {
                console.append("\n");
            }
            return null;
        }
    }
    

}
