
package syntax.function;

import interpreter.Instance;
import stringcreator.StringCreator;
import syntax.SyntaxNode;

public abstract class FunctionBehavior {
    protected Function function;
    
    public void setFunction(Function function) {
        this.function = function;
    }
    
    
    public SyntaxNode commit(Instance instance) {
        return function.getJumpNode();
    }
    
    public boolean isStoppedBeforeCall() {
        return true;
    }
    public boolean isStoppedAfterCall() {
        return true;
    }
    public boolean isAddedToHistory() {
        return true;
    }
    public boolean isAddedToStatistics() {
        return true;
    }
    
    public StringCreator getStatusCreatorBeforeCall(Instance instance) {
        return instance.getInstanceStringCreator(Lang.putOntoStack, "");
    }
    public StringCreator getStatusCreatorAfterCall(Instance instance) {
        return instance.getInstanceStringCreator(Lang.removeFromStack, "");
    }
    
    public StringCreator getLabelCreator(Instance instance) {
        return instance.getInstanceStringCreator("", "");
    }
    
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        String name = function.getName();
        if ( name.length() <= maxLength ) {
            return name;
        }
        return name.substring(0, maxLength);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static String putOntoStack = "Połóż na stosie:  ";
        public static String removeFromStack = "Zdejmij ze stosu:  ";
    }
    //</editor-fold>
}
