package lesson._08_Fibonacci;

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
    
    private boolean part4;
    
    private JComponent[] inputs;
    private JTextField valueNTextField;
    private JLabel valueNLabel;
    
    private String part1ValueNStr = "5";
    private String part4ValueNStr = "5";
    
    private int valueN = 5;
    
    public StartSpecialFunction() {
        valueNLabel = new JLabel(Lang.part1ValueNLabel);
        valueNTextField = new JTextField("5");
        
        inputs = new JComponent[] { valueNLabel, valueNTextField };
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
        
        while(true) {
            String text = valueNTextField.getText();
            if (text.length() > 10) {
                valueNTextField.setText(text.substring(0, 10));
            }
            int option = JOptionPane.showConfirmDialog(null, inputs, "Wczytywanie danych", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                throw new ProgramError("Anulowano wczytywanie danych",
                        call.getLeftIndex(), call.getRightIndex());
            }
            
            try {
                valueN = Integer.parseInt(valueNTextField.getText());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (valueN < 0 || valueN > 4000 || (!part4 && valueN > 98)) {
                continue;
            }
            break;
        }
        
        ((ArgReference) instance.getArgument(0)).setValue(BigInteger.valueOf(valueN));
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
            return "s(??)";
        }
        return "s(" + valueN + ")";
    }
    
    public int getValueN() {
        return valueN;
    }

    public void setSelectedPart(int part) {
        if (!part4 && part < 4 || part4 && part == 4) {
            return;
        }
        part4 = part == 4;
        if (part4) {
            part1ValueNStr = valueNTextField.getText();
            valueNTextField.setText(part4ValueNStr);
            valueNLabel.setText(Lang.part4ValueNLabel);
        } else {
            part4ValueNStr = valueNTextField.getText();
            valueNTextField.setText(part1ValueNStr);
            valueNLabel.setText(Lang.part1ValueNLabel);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String functionName = "start";
        
        public static final String part1ValueNLabel = "<html>Podaj liczbę <i>n</i> (0 - 98):</html>";
        public static final String part4ValueNLabel = "<html>Podaj liczbę <i>n</i> (0 - 4000):</html>";
    }
    //</editor-fold>
    
}
