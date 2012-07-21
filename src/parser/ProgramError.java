package parser;

public class ProgramError extends Exception {
    
    private int leftIndex;
    private int rightIndex;
    private int line;
    private int position;
    
    public ProgramError(String message, int leftIndex, int rightIndex) {
        super(message);
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }

    public int getLeftIndex() {
        return leftIndex;
    }
    public int getRightIndex() {
        return rightIndex;
    }
    public int getPosition() {
        return position;
    }
    public int getLine() {
        return line;
    }
    
    public void setLineAndPosition(int line, int position)
    {
        this.line = line;
        this.position = position;
    }
}
