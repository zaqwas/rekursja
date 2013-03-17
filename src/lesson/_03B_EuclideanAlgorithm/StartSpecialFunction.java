package lesson._03B_EuclideanAlgorithm;

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
    private JTextField valueNTextField;
    private JTextField valueMTextField;
    private JLabel valueNLabel;
    private JLabel valueMLabel;
    
    private int valueN = 12;
    private int valueM = 18;
    private boolean started;
    private byte selectedPart;
    
    public StartSpecialFunction() {
        selectedPart = 1;
        
        valueNLabel = new JLabel(Lang.part1ValueNLabel);
        valueNTextField = new JTextField("12");
        
        valueMLabel = new JLabel(Lang.part1ValueMLabel);
        valueMTextField = new JTextField("18");
        
        inputs = new JComponent[] { valueNLabel, valueNTextField, valueMLabel, valueMTextField };
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
            String text = valueNTextField.getText();
            if (text.length() > 10) {
                valueNTextField.setText(text.substring(0, 10));
            }
            text = valueMTextField.getText();
            if (text.length() > 10) {
                valueMTextField.setText(text.substring(0, 10));
            }
            JOptionPane.showMessageDialog(null, inputs, "Wczytywanie danych", JOptionPane.PLAIN_MESSAGE);
            
            try {
                valueN = Integer.parseInt(valueNTextField.getText());
                valueM = Integer.parseInt(valueMTextField.getText());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (valueN < 1 || valueM < 1 || valueN > 999999 || valueM > 999999
                    || (selectedPart == 1 && (valueN > 99 || valueM > 99))) {
                continue;
            }
            break;
        }
        
        started = true;
        ((ArgReference) instance.getArgument(0)).setValue(BigInteger.valueOf(valueN));
        ((ArgReference) instance.getArgument(1)).setValue(BigInteger.valueOf(valueM));
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
        return "start(" + valueN + "," + valueM + ")";
    }
    
    
    public void setSelectedPart(byte selectedPart) {
        this.selectedPart = selectedPart;
        if (selectedPart == 1 && valueN > 99) {
            valueNTextField.setText("12");
            valueN = 12;
        }
        if (selectedPart == 1 && valueM > 99) {
            valueMTextField.setText("18");
            valueM = 18;
        }
        valueNLabel.setText(selectedPart == 1 ? Lang.part1ValueNLabel : Lang.part2ValueNLabel);
        valueMLabel.setText(selectedPart == 1 ? Lang.part1ValueMLabel : Lang.part2ValueMLabel);
    }
    
    public void threadStart() {
        this.started = false;
    }

    
    public int getValueN() {
        return valueN;
    }
    public int getValueM() {
        return valueM;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
        public static final String part1ValueNLabel = "<html>Podaj liczbę <i>n</i> (1 - 99):</html>";
        public static final String part2ValueNLabel = "<html>Podaj liczbę <i>n</i> (1 - 999 999):</html>";
        public static final String part1ValueMLabel = "<html>Podaj liczbę <i>m</i> (1 - 99):</html>";
        public static final String part2ValueMLabel = "<html>Podaj liczbę <i>m</i> (1 - 999 999):</html>";
    }
    //</editor-fold>
    
}
