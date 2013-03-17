package lesson._91_sortFrame;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.Instance;
import interpreter.InterpreterThread;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.ArgReference;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import parser.SpecialFunctions;
import syntax.SyntaxNode;
import syntax.expression.Call;
import syntax.function.SpecialFunctionBehavior;
//</editor-fold>

public class SortLesson implements Lesson {
    
        @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
    }
    
    private JInternalFrame frame;
    private JPanel startValuesPanel;
    private JLabel startValuesIndexLabel[] = new JLabel[30];
    private JTextField startValuesTextField[] = new JTextField[30];
    
    private JPanel currValuesPanel;
    private JPanel currValuesInnerPanel;
    private JLabel currValuesIndexLabel[] = new JLabel[30];
    private JTextField currValuesTextField[] = new JTextField[30];
    private Color currColorField[] = new Color[30];
    private Color defaultColor = UIManager.getDefaults().getColor("TextField.inactiveBackground");
    
    private JPanel buttonsPanel;
    private JButton startButton;
    private JLabel sizeLabel;
    private JSlider sizeSlider;
    private JLabel sortValuesLabel;
    private JButton incSortValuesButton;
    private JButton decSortValuesButton;
    private JButton randomValuesButton;
    private JLabel randomValuesLabel;
    private JSpinner randomValuesSpinner;
    
    private Insets noMargin = new Insets(0, 0, 0, 0);
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    private Font sansSerifPlainFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private Font sansSerifBoldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    
    private MainClass mainClass;
    private InterpreterThread thread;
    
    private Call callNode;
    private int prevIdx1, prevIdx2, prevIdx3, prevIdx4, prevIdx5;

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private enum PrevOperation { COMPARE, SWAP, COLORS }
    private PrevOperation prevOperation;
    
    private boolean swapAnimation = true;
    private boolean duringAnimation;
    
    
    //<editor-fold defaultstate="collapsed" desc="Button actions">
    private void randomValuesAction() {
        Random rand = new Random();
        int max = ((Integer) randomValuesSpinner.getValue()) + 1;
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            startValuesTextField[i].setText(Integer.toString(rand.nextInt(max)));
        }
    }
    private void incSortValuesAction() {
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            startValuesTextField[i].setText(Integer.toString(i));
        }
    }
    private void decSortValuesAction() {
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            startValuesTextField[i].setText(Integer.toString(size - i - 1));
        }
    }
    private void startButtonAction() {
        for (int i = 0; i < 30; i++) {
            startValuesTextField[i].setEditable(false);
            currValuesTextField[i].setText(startValuesTextField[i].getText());
        }
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            currValuesTextField[i].setEnabled(true);
        }
        buttonsPanel.setVisible(false);
        frame.pack();
        startButton.setEnabled(false);
    }
    //</editor-fold>
    
    private void updateTextFieldsPosition() {
        int ySizeTextField = currValuesTextField[0].getHeight();
        int xSizeTextField = currValuesTextField[0].getWidth();
        int yTextField = 0;
        int xTextField1 = currValuesIndexLabel[0].getWidth();
        int xTextField2 = xTextField1 + xSizeTextField + 4 + currValuesIndexLabel[10].getWidth();
        int xTextField3 = xTextField2 + xSizeTextField + 4 + currValuesIndexLabel[20].getWidth();
        for (int i = 0; i < 10; i++) {
            currValuesTextField[i].setLocation(xTextField1, yTextField);
            currValuesTextField[i + 10].setLocation(xTextField2, yTextField);
            currValuesTextField[i + 20].setLocation(xTextField3, yTextField);
            yTextField += ySizeTextField;
        }
        for (int i = 0; i < 30; i++) {
            currValuesTextField[i].setBackground(currColorField[i]);
        }
    }
    
    private void swapFields(int idx1, int idx2) {
        JTextField tmp = currValuesTextField[idx1];
        currValuesTextField[idx1] = currValuesTextField[idx2];
        currValuesTextField[idx2] = tmp;
        Color tmpCol = currColorField[idx1];
        currColorField[idx1] = currColorField[idx2];
        currColorField[idx2] = tmpCol;
    }
    
    public SortLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    
    
    public void start() {
        for (int i = 0; i < 30; i++) {
            currColorField[i] = defaultColor;
        }
        
        frame = new JInternalFrame();
        
        //<editor-fold defaultstate="collapsed" desc="startValuesPanel">
        startValuesPanel = new JPanel();
        startValuesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), Lang.valuesAtTheBegining, 
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        for (int i=0; i<30; i++) {
            startValuesIndexLabel[i] = new JLabel(Integer.toString(i)+":");
            startValuesIndexLabel[i].setFont(monospacedFont);
        }
        DocumentFilter documentFilter = new DocumentFilter () {
            @Override
            public void insertString(FilterBypass fb, int offset,
                    String string, AttributeSet attr) throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                replace(fb, offset, length, "", null);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int docLength = fb.getDocument().getLength();
                StringBuilder sb = new StringBuilder();
                sb.append(fb.getDocument().getText(0, offset));
                sb.append(text);
                sb.append(fb.getDocument().getText(offset + length, docLength - offset - length));
                String str = sb.toString();
                char c1;
                if (str.length() == 0 || str.length() > 2 || (c1 = str.charAt(0)) < '0' || c1 > '9') {
                    return;
                }
                if (str.length() == 2) {
                    char c2 = str.charAt(1);
                    if (c2 < '0' || c2 > '9') {
                        return;
                    }
                    if (c1 == '0') {
                        str = Character.toString(c2);
                    }
                }
                fb.replace(0, docLength, str, attrs);
            }
        };
        for (int i=0; i<30; i++) {
            startValuesTextField[i] = new JTextField("0");
            startValuesTextField[i].setFont(monospacedFont);
            startValuesTextField[i].setColumns(3);
            startValuesTextField[i].setMargin(noMargin);
            startValuesTextField[i].setHorizontalAlignment(JTextField.CENTER);
            ((AbstractDocument) startValuesTextField[i].getDocument()).setDocumentFilter(documentFilter);
        }
        GroupLayout layout = new GroupLayout(startValuesPanel);
        Group group = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        for (int i = 0; i < 10; i++) {
            group = group.addGroup(layout.createSequentialGroup()
                .addComponent(startValuesIndexLabel[i])
                .addComponent(startValuesTextField[i])
                .addGap(4)
                .addComponent(startValuesIndexLabel[i+10])
                .addComponent(startValuesTextField[i+10])
                .addGap(4)
                .addComponent(startValuesIndexLabel[i+20])
                .addComponent(startValuesTextField[i+20])
            );
        }
        layout.setHorizontalGroup(group);
        group = layout.createSequentialGroup();
        for (int i = 0; i < 10; i++) {
            group = group.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(startValuesIndexLabel[i])
                .addComponent(startValuesTextField[i])
                .addComponent(startValuesIndexLabel[i+10])
                .addComponent(startValuesTextField[i+10])
                .addComponent(startValuesIndexLabel[i+20])
                .addComponent(startValuesTextField[i+20])
            );
        }
        layout.setVerticalGroup(group);
        startValuesPanel.setLayout(layout);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="currValuesPanel">
        currValuesPanel = new JPanel();
        currValuesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), Lang.currentValuse, 
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        currValuesInnerPanel = new JPanel();
        currValuesInnerPanel.setLayout(null);
        
        layout = new GroupLayout(currValuesPanel);
        layout.setHorizontalGroup(
                layout.createSequentialGroup().addComponent(currValuesInnerPanel));
        layout.setVerticalGroup(
                layout.createSequentialGroup().addComponent(currValuesInnerPanel));
        currValuesPanel.setLayout(layout);
        
        for (int i=0; i<30; i++) {
            currValuesIndexLabel[i] = new JLabel(Integer.toString(i)+":");
            currValuesIndexLabel[i].setFont(monospacedFont);
            currValuesInnerPanel.add(currValuesIndexLabel[i]);
            currValuesIndexLabel[i].setSize(currValuesIndexLabel[i].getPreferredSize());
        }
        for (int i=0; i<30; i++) {
            currValuesTextField[i] = new JTextField("0");
            currValuesTextField[i].setFont(monospacedFont);
            currValuesTextField[i].setColumns(3);
            currValuesTextField[i].setMargin(noMargin);
            currValuesTextField[i].setHorizontalAlignment(JTextField.CENTER);
            currValuesTextField[i].setFocusable(false);
            currValuesTextField[i].setEditable(false);
            currValuesTextField[i].setEnabled(false);
            currValuesInnerPanel.add(currValuesTextField[i]);
            currValuesTextField[i].setSize(currValuesTextField[i].getPreferredSize());
        }
        int ySizeTextField = currValuesTextField[0].getHeight();
        int xSizeTextField = currValuesTextField[0].getWidth();
        int yTextField = 0;
        int yLabel = (ySizeTextField - currValuesIndexLabel[0].getHeight()) / 2;
        int xLabel1 = 0;
        int xTextField1 = xLabel1 + currValuesIndexLabel[0].getWidth();
        int xLabel2 = xTextField1 + xSizeTextField + 4;
        int xTextField2 = xLabel2 + currValuesIndexLabel[10].getWidth();
        int xLabel3 = xTextField2 + xSizeTextField + 4;
        int xTextField3 = xLabel3 + currValuesIndexLabel[20].getWidth();
        for (int i = 0; i < 10; i++) {
            currValuesIndexLabel[i].setLocation(xLabel1, yLabel);
            currValuesTextField[i].setLocation(xTextField1, yTextField);
            currValuesIndexLabel[i + 10].setLocation(xLabel2, yLabel);
            currValuesTextField[i + 10].setLocation(xTextField2, yTextField);
            currValuesIndexLabel[i + 20].setLocation(xLabel3, yLabel);
            currValuesTextField[i + 20].setLocation(xTextField3, yTextField);
            yLabel += ySizeTextField;
            yTextField += ySizeTextField;
        }
        currValuesInnerPanel.setPreferredSize(
                new Dimension(xTextField3 + xSizeTextField, 10 * ySizeTextField));
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="buttonsPanel">
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char c = e.getActionCommand().charAt(0);
                switch (c) {
                    case 'S':
                        startButtonAction();
                        break;
                    case 'R':
                        randomValuesAction();
                        break;
                    case 'I':
                        incSortValuesAction();
                        break;
                    case 'D':
                        decSortValuesAction();
                        break;
                    default:
                        throw new AssertionError();
                }
            }  
        };
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v = sizeSlider.getValue();
                for (int i=0; i<30; i++) {
                    startValuesTextField[i].setEnabled(i<v);
                }
            }
        };
        buttonsPanel = new JPanel();
        
        startButton = new JButton();
        startButton.setText(Lang.startButton);
        startButton.setFont(this.sansSerifBoldFont);
        startButton.setMargin(noMargin);
        startButton.setActionCommand("S");
        startButton.setEnabled(false);
        startButton.addActionListener(actionListener);
        
        sizeLabel = new JLabel();
        sizeLabel.setFont(this.sansSerifPlainFont);
        sizeLabel.setText(Lang.sizeLabel);
        sizeSlider = new JSlider();
        sizeSlider.setMinimum(1);
        sizeSlider.setMaximum(30);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.addChangeListener(changeListener);
        
        sortValuesLabel = new JLabel();
        sortValuesLabel.setFont(this.sansSerifPlainFont);
        sortValuesLabel.setText(Lang.sortValuesLabel);
        sortValuesLabel.setHorizontalAlignment(JLabel.CENTER);
        incSortValuesButton = new JButton();
        incSortValuesButton.setFont(this.sansSerifBoldFont);
        incSortValuesButton.setText(Lang.incSortValuesButton);
        incSortValuesButton.setMargin(noMargin);
        incSortValuesButton.setActionCommand("I");
        incSortValuesButton.addActionListener(actionListener);
        decSortValuesButton = new JButton();
        decSortValuesButton.setFont(this.sansSerifBoldFont);
        decSortValuesButton.setText(Lang.decSortValuesButton);
        decSortValuesButton.setMargin(noMargin);
        decSortValuesButton.setActionCommand("D");
        decSortValuesButton.addActionListener(actionListener);
        
        randomValuesButton = new JButton();
        randomValuesButton.setFont(this.sansSerifBoldFont);
        randomValuesButton.setMargin(noMargin);
        randomValuesButton.setText(Lang.randomValuesButton);
        randomValuesButton.setActionCommand("R");
        randomValuesButton.addActionListener(actionListener);
        randomValuesLabel = new JLabel();
        randomValuesLabel.setText(Lang.randomValuesLabel);
        randomValuesLabel.setFont(sansSerifPlainFont);
        randomValuesSpinner = new JSpinner();
        randomValuesSpinner.setModel(new SpinnerNumberModel(10, 0, 99, 1));

        JSeparator vSep = new JSeparator();
        vSep.setOrientation(SwingConstants.VERTICAL);
        JSeparator hSep1 = new JSeparator();
        JSeparator hSep2 = new JSeparator();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="buttonsPanel Layout">
        layout = new javax.swing.GroupLayout(buttonsPanel);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addComponent(startButton, 30, 30, 30)
            .addGap(2)
            .addComponent(vSep, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(2)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(sizeLabel)
                    .addComponent(sizeSlider, 0, 0, Short.MAX_VALUE))
                .addComponent(hSep1, 0, 0, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(sortValuesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(4)
                    .addComponent(incSortValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(4)
                    .addComponent(decSortValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addComponent(hSep2, 0, 0, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(randomValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(4)
                    .addComponent(randomValuesLabel)
                    .addComponent(randomValuesSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(startButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addComponent(vSep, 0, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(sizeLabel)
                    .addComponent(sizeSlider))
                .addGap(1)
                .addComponent(hSep1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(1)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(sortValuesLabel)
                    .addComponent(incSortValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(decSortValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addGap(1)
                .addComponent(hSep2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(1)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(randomValuesButton)
                    .addComponent(randomValuesLabel)
                    .addComponent(randomValuesSpinner)))
        );
        buttonsPanel.setLayout(layout);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Layout">
        layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGap(2)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(startValuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(2)
                    .addComponent(currValuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(buttonsPanel))
            .addGap(2)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(startValuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(currValuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(buttonsPanel)
            .addGap(2)
        );
        frame.getContentPane().setLayout(layout);
        //</editor-fold>
        
        frame.pack();
        sizeSlider.setValue(10);
        randomValuesButton.doClick();
        
        mainClass.addToDesktop(frame);
        frame.setVisible(true);
        
        mainClass.getEditor().setCode(
                "void start(n&) {}\n"
                + "void sprawdz() {}\n"
                + "int porownaj(idx1, idx2) {}\n"
                + "void zamien(idx1, idx2) {}\n"
                + "\n"
                + "void sortuj(n) \n"
                + "var i,j;\n"
                + "{\n"
                + "  for (i=n-1; i>0; i--) {\n"
                + "    for (j=0; j<i; j++) {\n"
                + "      if (porownaj(j, j+1)>0) {\n"
                + "        zamien(j, j+1);\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n"
                + "\n"
                + "void main()\n"
                + "var n;\n"
                + "{\n"
                + "   start(n);\n"
                + "   sortuj(n);\n"
                + "   sprawdz();\n"
                + "}");
        mainClass.getEditor().setCode(
                "void start(n&) {}\n"
                + "void sprawdz() {}\n"
                + "int porownaj(idx1, idx2) {}\n"
                + "void zamien(idx1, idx2) {}\n"
                + "void pokoloruj(idx1, idx2, i1, i2, i3) {}\n"
                + "\n"
                + "void podziel(idx1, idx2, i1&, i2&)\n"
                + "var i3, tmp;\n"
                + "{\n"
                + "   i1 = idx1;\n"
                + "   i2 = i1 + 1;\n"
                + "   i3 = idx2;\n"
                + "   while ( i2 < i3 ) {\n"
                + "      pokoloruj(idx1, idx2, i1, i2, i3);\n"
                + "      tmp = porownaj(i2, i1);\n"
                + "      if ( tmp == 0 ) {\n"
                + "         i2++;\n"
                + "      } else if ( tmp < 0 ) {\n"
                + "         zamien(i1, i2);\n"
                + "         i1++;\n"
                + "         i2++;\n"
                + "      } else {\n"
                + "         i3--;\n"
                + "         zamien(i2, i3);\n"
                + "      }\n"
                + "   }\n"
                + "}\n"
                + "\n"
                + "void quicksort(idx1, idx2)\n"
                + "var i1, i2, idxPivot;\n"
                + "{\n"
                + "   if ( idx2 - idx1 <= 2 ) {\n"
                + "      if ( idx1 != idx2 && \n"
                + "            porownaj(idx1, idx2) > 0 ) {\n"
                + "         zamien(idx1, idx2);\n"
                + "      }\n"
                + "      return;\n"
                + "   }\n"
                + "   podziel(idx1, idx2, i1, i2);\n"
                + "   if ( i1 > idx1 ) {\n"
                + "      quicksort(idx1, i1);\n"
                + "   }\n"
                + "   if ( i2 < idx2 ) {\n"
                + "      quicksort(i2, idx2);\n"
                + "   }\n"
                + "}\n"
                + "\n"
                + "void main()\n"
                + "var n;\n"
                + "{\n"
                + "   start(n);\n"
                + "   quicksort(0, n);\n"
                + "   sprawdz();\n"
                + "}\n");
        
        SpecialFunctions.add(new Check());
        SpecialFunctions.add(new Compare());
        SpecialFunctions.add(new Start());
        SpecialFunctions.add(new Swap());
        SpecialFunctions.add(new Colors());
    }
    
    @Override
    public void close() {
    }
    @Override
    public void threadStart(InterpreterThread thread) {
        
    }
    
    @Override
    public void threadStop() {
        updateTextFieldsPosition();
        for (int i = 0; i < 30; i++) {
            startValuesTextField[i].setEditable(true);
        }
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            currValuesTextField[i].setEnabled(false);
        }
        buttonsPanel.setVisible(true);
        frame.pack();
    }
    
    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
        if ( node != callNode ) {
            updateTextFieldsPosition();
            return true;
        }
        currValuesTextField[prevIdx1].setForeground(Color.RED);
        currValuesTextField[prevIdx2].setForeground(Color.RED);
        if (prevOperation == PrevOperation.SWAP && swapAnimation) {
            swapFields(prevIdx1, prevIdx2);
            updateTextFieldsPosition();
            duringAnimation = true;
            (new Thread() {
                @Override
                public void run() {
                    JTextField v1 = currValuesTextField[prevIdx1];
                    JTextField v2 = currValuesTextField[prevIdx2];
                    currValuesInnerPanel.setComponentZOrder(v1, 0);
                    currValuesInnerPanel.setComponentZOrder(v2, 0);
                    int x1 = v1.getX(), y1 = v1.getY();
                    int x2 = v2.getX(), y2 = v2.getY();
                    int numberOfSlices = delayTime / 10;
                    for (int i = 1; i <= numberOfSlices; i++) {
                        v1.setLocation(x1 + (x2 - x1) * i / numberOfSlices, y1 + (y2 - y1) * i / numberOfSlices);
                        v2.setLocation(x2 + (x1 - x2) * i / numberOfSlices, y2 + (y1 - y2) * i / numberOfSlices);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                        }
                    }
                    currValuesTextField[prevIdx1] = v2;
                    currValuesTextField[prevIdx2] = v1;
                    duringAnimation = false;
                }
            }).start();
        }
        updateTextFieldsPosition();
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
        if (node == callNode) {
            while ( duringAnimation ) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
            }
            currValuesTextField[prevIdx1].setForeground(Color.BLACK);
            currValuesTextField[prevIdx2].setForeground(Color.BLACK);
        }
        prevOperation = null;
        callNode = null;
    }
    
    
    
    public JInternalFrame getFrame() {
        return frame;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Special functions">
    public class Start extends SpecialFunctionBehavior {
        @Override
        public String getName() {
            return Lang.startFunciton;
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
            startButton.setEnabled(true);
            while ( startButton.isEnabled() ) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {}
            }
            ((ArgReference) instance.getArgument(0)).setValue(
                    new BigInteger(Integer.toString(sizeSlider.getValue())));
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
    }
    
    public class Check extends SpecialFunctionBehavior {
        @Override
        public String getName() {
            return Lang.checkFunciton;
        }
        @Override
        public int getArgumentsLength() {
            return 0;
        }
        @Override
        public VariableType getArgumentType(int index) {
            return null;
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
            int n = sizeSlider.getValue(), idx = -1;
            for (int i=0; i<n-1; i++) {
                int v1 = Integer.parseInt(currValuesTextField[i].getText());
                int v2 = Integer.parseInt(currValuesTextField[i].getText());
                if ( v1>v2 ) {
                    idx = i;
                    break;
                }
            }
            //TODO 
            System.out.println(idx);
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
    }
    
    public class Compare extends SpecialFunctionBehavior {
        @Override
        public String getName() {
            return Lang.compareFunciton;
        }
        @Override
        public int getArgumentsLength() {
            return 2;
        }
        @Override
        public VariableType getArgumentType(int index) {
            return VariableType.INTEGER;
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
            BigInteger idx1 = ((ArgInteger) instance.getArgument(0)).getValue();
            BigInteger idx2 = ((ArgInteger) instance.getArgument(1)).getValue();
            //TODO check size
            
            prevIdx1 = idx1.intValue();
            prevIdx2 = idx2.intValue();
            prevOperation = PrevOperation.COMPARE;
            callNode = instance.getCallNode();
            int v1 = Integer.parseInt(currValuesTextField[prevIdx1].getText());
            int v2 = Integer.parseInt(currValuesTextField[prevIdx2].getText());
            if ( v1==v2 ) {
                instance.setReturnedValue(BigInteger.ZERO);
            } else if ( v1<v2 ) {
                instance.setReturnedValue(BigInteger.ONE.negate());
            } else {
                instance.setReturnedValue(BigInteger.ONE);
            }
            return null;
        }
        
        @Override
        public boolean isAddedToHistory() {
            return false;
        }
        @Override
        public boolean isStoppedBeforeCall() {
            return false;
        }
        @Override
        public boolean isStoppedAfterCall() {
            return true;
        }
    }
    
    public class Swap extends SpecialFunctionBehavior {
        @Override
        public String getName() {
            return Lang.swapFunciton;
        }
        @Override
        public int getArgumentsLength() {
            return 2;
        }
        @Override
        public VariableType getArgumentType(int index) {
            return VariableType.INTEGER;
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
            BigInteger idx1 = ((ArgInteger) instance.getArgument(0)).getValue();
            BigInteger idx2 = ((ArgInteger) instance.getArgument(1)).getValue();
            //TODO chaeck idx
            prevIdx1 = idx1.intValue();
            prevIdx2 = idx2.intValue();
            
            prevOperation = PrevOperation.SWAP;
            callNode = instance.getCallNode();
            
            swapFields(prevIdx1, prevIdx2);
            
            return null;
        }
        
        @Override
        public boolean isAddedToHistory() {
            return false;
        }
        @Override
        public boolean isStoppedBeforeCall() {
            return false;
        }
        @Override
        public boolean isStoppedAfterCall() {
            return true;
        }
    }
    
    public class Colors extends SpecialFunctionBehavior {
        @Override
        public String getName() {
            return Lang.colorFunciton;
        }

        @Override
        public int getArgumentsLength() {
            return 5;
        }

        @Override
        public VariableType getArgumentType(int index) {
            return VariableType.INTEGER;
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
            int idx1 = ((ArgInteger) instance.getArgument(0)).getValue().intValue();
            int idx2 = ((ArgInteger) instance.getArgument(1)).getValue().intValue();
            int idx3 = ((ArgInteger) instance.getArgument(2)).getValue().intValue();
            int idx4 = ((ArgInteger) instance.getArgument(3)).getValue().intValue();
            int idx5 = ((ArgInteger) instance.getArgument(4)).getValue().intValue();
            
            for (int i = idx1; i < idx3; i++) {
                currColorField[i] = Color.BLUE;
            }
            for (int i = idx3; i < idx4; i++) {
                currColorField[i] = Color.GREEN;
            }
            for (int i = idx4; i < idx5; i++) {
                currColorField[i] = Color.RED;
            }
            for (int i = idx5; i < idx2; i++) {
                currColorField[i] = Color.YELLOW;
            }
            return null;
        }
    }
    //</editor-fold>
    
    
    
    private static class Lang {
        public static final String valuesAtTheBegining = "<html><center>Wartości<br>początkowe:</center></html>";
        public static final String currentValuse = "<html><center>Obecne<br>wartości:</center></html>";
        
        public static final String startButton = "<html>S<br>T<br>A<br>R<br>T</html>";
        public static final String sizeLabel = "Rozmiar tablicy: ";
        public static final String sortValuesLabel = "<html>Ustaw wartości posor-<br>towane w sposób: </html>";
        public static final String incSortValuesButton = "malejący";
        public static final String decSortValuesButton = "rosnący";
        public static final String randomValuesButton = "Losuj";
        public static final String randomValuesLabel = "wartości z przedziału od 0 do ";
        
        
        public static final String startFunciton = "start";
        public static final String checkFunciton = "sprawdz";
        public static final String swapFunciton = "zamien";
        public static final String compareFunciton = "porownaj";
        public static final String colorFunciton = "pokoloruj";
    }
    
}
