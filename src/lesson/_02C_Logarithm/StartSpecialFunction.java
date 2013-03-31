package lesson._02C_Logarithm;

import lesson._03B_EuclideanAlgorithm.*;
import interpreter.Instance;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgReference;
import java.math.BigInteger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import parser.ProgramError;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;

class StartSpecialFunction extends SpecialFunctionBehavior {
    
    private JComponent[] inputs;
    private JTextField valueATextField;
    private JTextField valueNTextField;
    private JLabel valueNLabel;
    private JLabel valueMLabel;
    
    private int valueA = 2;
    private int valueN = 31;
    
    public StartSpecialFunction() {
        
        valueNLabel = new JLabel(Lang.valueALabel);
        valueATextField = new JTextField("2");
        
        valueMLabel = new JLabel(Lang.valueNLabel);
        valueNTextField = new JTextField("31");
        
        inputs = new JComponent[] { valueNLabel, valueATextField, valueMLabel, valueNTextField };
    }
    
    @Override
    public String getName() {
        return Lang.functionName;
    }

    @Override
    public int getArgumentsLength() {
        return 2;
    }
    @Override
    public VariableType getArgumentType(int index) {
        return VariableType.REFERENCE;
    }

    @Override
    public SyntaxNode commit(Instance instance) throws ProgramError {
        Call call = instance.getCallNode();
        Instance parentInst = instance.getParentInstance().getParentInstance();
        if (parentInst != null) {
            throw new ProgramError("Funkcja „start” powinna być wywoływana tylko w funkcji „main”",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        while(true) {
            String text = valueATextField.getText();
            if (text.length() > 10) {
                valueATextField.setText(text.substring(0, 10));
            }
            text = valueNTextField.getText();
            if (text.length() > 15) {
                valueNTextField.setText(text.substring(0, 15));
            }
            int option = JOptionPane.showConfirmDialog(null, inputs, "Wczytywanie danych", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                throw new ProgramError("Anulowano wczytywanie danych",
                        call.getLeftIndex(), call.getRightIndex());
            }
            
            try {
                valueA = Integer.parseInt(valueATextField.getText());
                valueN = Integer.parseInt(valueNTextField.getText());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (valueA < 2 || valueN < 1 || valueA > 99 || valueN > 999999999) {
                continue;
            }
            break;
        }
        
        ((ArgReference) instance.getArgument(0)).setValue(BigInteger.valueOf(valueA));
        ((ArgReference) instance.getArgument(1)).setValue(BigInteger.valueOf(valueN));
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
        BigInteger bigInt = ((ArgReference)instance.getArgument(0)).getValueAtTheEnd();
        if (bigInt == null) {
            return "str(??,??)";
        }
        return "str(" + valueA + "," + valueN + ")";
    }
    
    
    public int getValueA() {
        return valueA;
    }
    public int getValueN() {
        return valueN;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
        
        public static final String valueALabel = "<html>Podaj liczbę <i>a</i> (2 - 99):</html>";
        public static final String valueNLabel = "<html>Podaj liczbę <i>n</i> (1 - 999 999 999):</html>";
    }
    //</editor-fold>
    
}
