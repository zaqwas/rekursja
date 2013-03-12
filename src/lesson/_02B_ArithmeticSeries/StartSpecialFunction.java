package lesson._02B_ArithmeticSeries;

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
    private JTextField textField;
    private JLabel label;
    
    private int value = 5;
    private boolean started;
    private byte selectedPart;
    
    public StartSpecialFunction() {
        selectedPart = 1;
        
        label = new JLabel(Lang.part1Label);
        textField = new JTextField("5");
        
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
            if (value < 0 || value > 1000000000 || (selectedPart == 1 && value > 98)) {
                continue;
            }
            break;
        }
        
        started = true;
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
    
    
    public void setSelectedPart(byte selectedPart) {
        this.selectedPart = selectedPart;
        if (selectedPart == 1 && value > 98) {
            textField.setText("5");
        }
        label.setText(selectedPart == 1 ? Lang.part1Label : Lang.part2Label);
    }
    
    public void threadStart() {
        this.started = false;
    }

    public int getValue() {
        return value;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
        public static final String part1Label = "<html>Podaj liczbę <i>n</i> (0 - 98):</html>";
        public static final String part2Label = "<html>Podaj liczbę <i>n</i> (0 - 1 000 000 000):</html>";
    }
    //</editor-fold>
    
}
