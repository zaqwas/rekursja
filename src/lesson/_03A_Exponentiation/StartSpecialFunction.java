package lesson._03A_Exponentiation;

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
    
    private int valueA = 2;
    private int valueN = 5;
    private boolean started;
    private byte selectedPart;
    
    public StartSpecialFunction() {
        selectedPart = 1;
        
        JLabel valueALabel = new JLabel(Lang.valueALabel);
        valueATextField = new JTextField("2");
        
        valueNLabel = new JLabel(Lang.part1ValueNLabel);
        valueNTextField = new JTextField("5");
        
        inputs = new JComponent[] { valueALabel, valueATextField, valueNLabel, valueNTextField };
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
        if (started) {
            throw new ProgramError("Funkcja „start” może być wywoływana tylko raz.",
                    call.getLeftIndex(), call.getRightIndex());
        }
        
        while(true) {
            String text = valueATextField.getText();
            if (text.length() > 10) {
                valueATextField.setText(text.substring(0, 10));
            }
            text = valueNTextField.getText();
            if (text.length() > 10) {
                valueNTextField.setText(text.substring(0, 10));
            }
            JOptionPane.showMessageDialog(null, inputs, "Wczytywanie danych", JOptionPane.PLAIN_MESSAGE);
            
            try {
                valueA = Integer.parseInt(valueATextField.getText());
                valueN = Integer.parseInt(valueNTextField.getText());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (valueA < 1 || valueA > 10 || valueN < 0 || valueN > 500
                    || (selectedPart == 1 && valueN > 98)) {
                continue;
            }
            break;
        }
        
        started = true;
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
        return "start(" + valueA + "," + valueN + ")";
    }
    
    
    public void setSelectedPart(byte selectedPart) {
        this.selectedPart = selectedPart;
        if (selectedPart == 1 && valueN > 98) {
            valueNTextField.setText("5");
            valueN = 5;
        }
        valueNLabel.setText(selectedPart == 1 ? Lang.part1ValueNLabel : Lang.part2ValueNLabel);
    }
    
    public void threadStart() {
        this.started = false;
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
        public static final String valueALabel = "<html>Podaj liczbę <i>a</i> (1 - 10):</html>";
        public static final String part1ValueNLabel = "<html>Podaj liczbę <i>n</i> (0 - 98):</html>";
        public static final String part2ValueNLabel = "<html>Podaj liczbę <i>n</i> (0 - 500):</html>";
    }
    //</editor-fold>
    
}
