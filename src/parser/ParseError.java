package parser;

public class ParseError extends Exception {
    public int code;
    public int index;
    public ParseError(int code, int index) {
        this.code = code;
        this.index = index;
    }
}
