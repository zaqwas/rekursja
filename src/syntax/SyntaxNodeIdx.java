package syntax;

public abstract class SyntaxNodeIdx extends SyntaxNode implements SyntaxNodeIndexed {
    public int indexL;
    public int indexR;
    
    @Override
    public int getLeftIndex() {
        return indexL;
    }
    
    @Override
    public int getRightIndex() {
        return indexR;
    }
    
}
