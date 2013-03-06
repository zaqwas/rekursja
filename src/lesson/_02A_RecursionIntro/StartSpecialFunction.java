package lesson._02A_RecursionIntro;

import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import syntax.SyntaxNode;
import syntax.function.SpecialFunctionBehavior;

class StartSpecialFunction extends SpecialFunctionBehavior {

    private int value;
    private JComponent[] inputs;
    private JTextField textField;
    
    public StartSpecialFunction() {
        textField = new JTextField();
        textField.setText("5");
        
        JLabel label = new JLabel("<html>Podaj liczbę <i>n</i> (0-99):</html>");
        inputs = new JComponent[] { label, textField };
    }
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return 1;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.REFERENCE;
    }

    @Override
    public SyntaxNode commit(Instance instance) {

        while(true) {
            String text = textField.getText();
            if (text.length() > 10) {
                textField.setText(text.substring(0, 10));
            }
            JOptionPane.showMessageDialog(null, inputs, "Wczytywanie danych", JOptionPane.PLAIN_MESSAGE);
            try {
                value = Integer.parseInt(textField.getText());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (value < 0 || value >= 100) {
                continue;
            }
            break;
        }
        
        ((ArgReference) instance.getArgument(0)).setValue(BigInteger.valueOf(value));
        return null;
    }

    @Override
    public boolean isStoppedBeforeCall() {
        return false;
    }

    @Override
    public boolean isStoppedAfterCall() {
        return false;
    }
    
    @Override
    public String getTreeNodeLabel(int maxLength, Instance instance) {
        return "start(" + value + ")";
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
    }
    //</editor-fold>
    
}
