package interpreter.arguments;

public class ArgString extends Argument {

    protected String string;
    
    public ArgString(String str) {
        this.string = str;
    }
    
    @Override
    public void clear() {
        string = null;
    }

    public String getString() {
        return string;
    }
}
