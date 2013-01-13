package lesson._11_FindElement;

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
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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

public class FindElementLesson implements Lesson {
    
   private final static String resourcesDir = "/lesson/_03_FindElement/resources/";
   
    //<editor-fold defaultstate="collapsed" desc="Components and variables">
    private JInternalFrame frame;
    private JPanel valuesPanel;
    private JLabel valuesIndexLabel[] = new JLabel[32];
    private JTextField valuesTextField[] = new JTextField[32];
    
    private JPanel buttonsPanel;
    private JButton startButton;
    private JLabel sizeLabel;
    private JSlider sizeSlider;
    private JButton randomValuesButton;
    private JLabel randomValuesLabel;
    private JSpinner randomValuesSpinner;
    
    private Insets noMargin = new Insets(0, 0, 0, 0);
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    private Font sansSerifPlain11Font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private Font sansSerifPlain9Font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
    private Font sansSerifBold11Font = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    private Font sansSerifBold16Font = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    
    private MainClass mainClass;
    
    private Call callNode;
    private int prevIdx1, prevIdx2;
    
    private boolean swapAnimation = true;
    private volatile boolean duringAnimation;
    
    private JButton taskTextButton;
    private JButton secondLessonButton;
    
    private JInternalFrame textFrame;
    private JTabbedPane tabbedPane;
    private JEditorPane taskEditorPane;
    private JScrollPane taskScrollPane;
    private JEditorPane specialFunctionEditorPane;
    private JScrollPane specialFunctionScrollPane;
    private JEditorPane specificationEditorPane;
    private JScrollPane specificationScrollPane;
    private JEditorPane pseudocodeEditorPane;
    private JScrollPane pseudocodeScrollPane;
    //</editor-fold>
    
    private JMenuItem userSolutionMenuItem;
    private JMenuItem taskMenuItem;
    private JMenuItem hintMenuItem;
    private JMenuItem solutionMenuItem;
    
    private JLabel searchedElementLabel;
    private JTextField searchedElementTextField;
    
    private String userSolutionString;
    
    public FindElementLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Button actions">
    private void randomValuesAction() {
        Random rand = new Random();
        int max = ((Integer) randomValuesSpinner.getValue()) + 1;
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            valuesTextField[i].setText(Integer.toString(rand.nextInt(max)));
        }
    }
    private void incSortValuesAction() {
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            valuesTextField[i].setText(Integer.toString(i));
        }
    }
    private void decSortValuesAction() {
        for (int i = 0, size = sizeSlider.getValue(); i < size; i++) {
            valuesTextField[i].setText(Integer.toString(size - i - 1));
        }
    }
    private void startButtonAction() {
        for (int i = 0; i < 32; i++) {
            valuesTextField[i].setEditable(false);
        }
        buttonsPanel.setVisible(false);
        frame.pack();
        startButton.setEnabled(false);
    }
    
    private void userSolutionMenuItemAction() {
        mainClass.getEditor().setCode(userSolutionString);
        userSolutionMenuItem.setEnabled(false);
        solutionMenuItem.setEnabled(true);
        secondLessonButton.setText(Lang.solution);
        secondLessonButton.setActionCommand("s");
        JInternalFrame codeFrame = mainClass.getEditor().getFrame();
        codeFrame.setVisible(true);
        try {
            codeFrame.setSelected(true);
        } catch (PropertyVetoException ex) {}
    }
    private void taskMenuItemAction() {
        tabbedPane.setSelectedIndex(0);
        textFrame.setVisible(true);
        try {
            textFrame.setSelected(true);
        } catch (PropertyVetoException ex) {}
    }
    private void hintMenuItemAction() {
        if ( !tabbedPane.isEnabledAt(3) ) {
            tabbedPane.setEnabledAt(3, true);
            secondLessonButton.setText(Lang.solution);
            secondLessonButton.setActionCommand("s");
        }
        tabbedPane.setSelectedIndex(3);
        textFrame.setVisible(true);
        try {
            textFrame.setSelected(true);
        } catch (PropertyVetoException ex) {}
    }
    private void solutionMenuItemAction() {
        if ( !tabbedPane.isEnabledAt(3) && JOptionPane.showOptionDialog(null, 
                Lang.showSolutionConfirmation, Lang.showSolutionConfirmationTitle, 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                null, new Object[] { Lang.yes, Lang.no }, Lang.no)
                !=JOptionPane.YES_OPTION ) {
            return;
        }
        tabbedPane.setEnabledAt(3, true);
        secondLessonButton.setText(Lang.user);
        secondLessonButton.setActionCommand("u");
        userSolutionString = mainClass.getEditor().getCode();
        mainClass.getEditor().setCode(Lang.solutionCode);
        userSolutionMenuItem.setEnabled(true);
        solutionMenuItem.setEnabled(false);
        JInternalFrame codeFrame = mainClass.getEditor().getFrame();
        codeFrame.setVisible(true);
        try {
            codeFrame.setSelected(true);
        } catch (PropertyVetoException ex) {}
    }
    //</editor-fold>
    
    public void start() {
        frame = new JInternalFrame(Lang.arrayFrameTitle);
        
        //<editor-fold defaultstate="collapsed" desc="Document filter">
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
        //</editor-fold>
        
        searchedElementLabel = new JLabel(Lang.searchedElement);
        searchedElementLabel.setFont(sansSerifBold11Font);
        
        searchedElementTextField = new JTextField("0");
        searchedElementTextField.setFont(monospacedFont);
        searchedElementTextField.setMargin(noMargin);
        searchedElementTextField.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument) searchedElementTextField.getDocument()).setDocumentFilter(documentFilter);
        searchedElementTextField.setColumns(3);
        
        //<editor-fold defaultstate="collapsed" desc="valuesPanel">
        valuesPanel = new JPanel();
        valuesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        for (int i=0; i<32; i++) {
            valuesIndexLabel[i] = new JLabel(Integer.toString(i)+":");
            valuesIndexLabel[i].setFont(sansSerifPlain9Font);
        }
        
        for (int i=0; i<32; i++) {
            valuesTextField[i] = new JTextField("0");
            valuesTextField[i].setFont(monospacedFont);
            valuesTextField[i].setMargin(noMargin);
            valuesTextField[i].setHorizontalAlignment(JTextField.CENTER);
            ((AbstractDocument) valuesTextField[i].getDocument()).setDocumentFilter(documentFilter);
            valuesTextField[i].setColumns(3);
        }
        GroupLayout layout = new GroupLayout(valuesPanel);
        Group group = layout.createSequentialGroup().addGap(2);
        for (int i = 0; i < 8; i++) {
            Group gr1 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
            Group gr2 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
            for (int j = 0; j < 4; j++) {
                gr1 = gr1.addComponent(valuesIndexLabel[8*j+i]);
                gr2 = gr2.addComponent(valuesTextField[8*j+i]);
            }
            group = group.addGroup(gr1).addGroup(gr2).addGap(i==7?2:1);
        }
        layout.setHorizontalGroup(group);
        group = layout.createSequentialGroup().addGap(2);
        for (int i = 0; i < 4; i++) {
            Group gr = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
            for (int j = 0; j < 8; j++) {
                gr = gr.addComponent(valuesIndexLabel[8*i+j]);
                gr = gr.addComponent(valuesTextField[8*i+j]);
            }
            group = group.addGroup(gr).addGap(i==3?2:1);
        }
        layout.setVerticalGroup(group);
        valuesPanel.setLayout(layout);
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
                    case 'u':
                        userSolutionMenuItemAction();
                        break;
                    case 't':
                        taskMenuItemAction();
                        break;
                    case 'h':
                        hintMenuItemAction();
                        break;
                    case 's':
                        solutionMenuItemAction();
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
                for (int i=0; i<32; i++) {
                    valuesTextField[i].setEnabled(i<v);
                }
            }
        };
        buttonsPanel = new JPanel();
        
        startButton = new JButton(Lang.startButton);
        startButton.setFont(sansSerifBold16Font);
        startButton.setMargin(noMargin);
        startButton.setActionCommand("S");
        startButton.setEnabled(false);
        startButton.addActionListener(actionListener);
        
        sizeLabel = new JLabel(Lang.sizeLabel);
        sizeLabel.setFont(sansSerifPlain11Font);
        sizeSlider = new JSlider();
        sizeSlider.setMinimum(1);
        sizeSlider.setMaximum(32);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.addChangeListener(changeListener);
        
        randomValuesButton = new JButton(Lang.randomValuesButton);
        randomValuesButton.setFont(sansSerifBold11Font);
        randomValuesButton.setMargin(noMargin);
        randomValuesButton.setActionCommand("R");
        randomValuesButton.addActionListener(actionListener);
        randomValuesLabel = new JLabel(Lang.randomValuesLabel);
        randomValuesLabel.setFont(sansSerifPlain11Font);
        randomValuesSpinner = new JSpinner();
        randomValuesSpinner.setFont(sansSerifBold11Font);
        randomValuesSpinner.setModel(new SpinnerNumberModel(10, 0, 99, 1));

        JSeparator vSep = new JSeparator();
        vSep.setOrientation(SwingConstants.VERTICAL);
        JSeparator hSep1 = new JSeparator();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="buttonsPanel Layout">
        layout = new javax.swing.GroupLayout(buttonsPanel);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(startButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addGap(2)
                .addComponent(vSep, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2)
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sizeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sizeSlider, 0, 0, Short.MAX_VALUE))
                    .addComponent(hSep1, 0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(randomValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(randomValuesLabel)
                        .addComponent(randomValuesSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(startButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(vSep, 0, 0, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(sizeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sizeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(2)
                    .addComponent(hSep1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(2)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(randomValuesButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(randomValuesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(randomValuesSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))))
        );
        buttonsPanel.setLayout(layout);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Layout">
        layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGap(2)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(searchedElementLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchedElementTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(valuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGap(2)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGap(2)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(searchedElementLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchedElementTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGap(2)
            .addComponent(valuesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(2)
            .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(2)
        );
        frame.getContentPane().setLayout(layout);
        //</editor-fold>
       
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.setEnabled(true);
        
        userSolutionMenuItem = new JMenuItem(Lang.user);
        userSolutionMenuItem.setEnabled(false);
        userSolutionMenuItem.setActionCommand("u");
        lessonMenu.add(userSolutionMenuItem);

        taskMenuItem = new JMenuItem(Lang.task);
        taskMenuItem.setActionCommand("t");
        lessonMenu.add(taskMenuItem);

        hintMenuItem = new JMenuItem(Lang.hint);
        hintMenuItem.setActionCommand("h");
        lessonMenu.add(hintMenuItem);

        solutionMenuItem = new JMenuItem(Lang.solution);
        solutionMenuItem.setActionCommand("s");
        lessonMenu.add(solutionMenuItem);

        userSolutionMenuItem.addActionListener(actionListener);
        taskMenuItem.addActionListener(actionListener);
        hintMenuItem.addActionListener(actionListener);
        solutionMenuItem.addActionListener(actionListener);
        
        
        //<editor-fold defaultstate="collapsed" desc="Lesson panel">
        taskTextButton = new JButton(Lang.task);
        taskTextButton.setMargin(noMargin);
        taskTextButton.setFont(sansSerifBold11Font);
        taskTextButton.setFocusable(false);
        
        secondLessonButton = new JButton(Lang.hint);
        secondLessonButton.setMargin(noMargin);
        secondLessonButton.setFont(sansSerifBold11Font);
        secondLessonButton.setFocusable(false);
        
        taskTextButton.addActionListener(actionListener);
        taskTextButton.setActionCommand("t");
        secondLessonButton.addActionListener(actionListener);
        secondLessonButton.setActionCommand("h");
        
        layout = new GroupLayout(mainClass.getLessonPanel());
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addComponent(taskTextButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(4)
            .addComponent(secondLessonButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(taskTextButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(secondLessonButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            
        );
        mainClass.getLessonPanel().setLayout(layout);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="TextFrame">
        textFrame = new JInternalFrame();
        tabbedPane = new JTabbedPane();
        
        
        taskScrollPane = new JScrollPane();
        taskScrollPane.setBorder(null);
        tabbedPane.addTab(Lang.taskTabName, taskScrollPane);
        
        taskEditorPane = new JEditorPane();
        taskEditorPane.setEditable(false);
        try {
            taskEditorPane.setPage(getClass().getResource(resourcesDir + "task.html"));
        } catch (IOException ex) {
            taskEditorPane.setText(Lang.loadTabPaneError);
        }
        taskScrollPane.setViewportView(taskEditorPane);
        
        
        specialFunctionScrollPane = new JScrollPane();
        specialFunctionScrollPane.setBorder(null);
        tabbedPane.addTab(Lang.specialFunctionsTabName, specialFunctionScrollPane);
        
        specialFunctionEditorPane = new JEditorPane();
        specialFunctionEditorPane.setEditable(false);
        try {
            specialFunctionEditorPane.setPage(getClass().getResource(resourcesDir + "specialFunctions.html"));
        } catch (IOException ex) {
            specialFunctionEditorPane.setText(Lang.loadTabPaneError);
        }
        specialFunctionScrollPane.setViewportView(specialFunctionEditorPane);
        
        
        specificationScrollPane = new JScrollPane();
        specificationScrollPane.setBorder(null);
        tabbedPane.addTab(Lang.specificationTabName, specificationScrollPane);
        
        specificationEditorPane = new JEditorPane();
        specificationEditorPane.setEditable(false);
        try {
            specificationEditorPane.setPage(getClass().getResource(resourcesDir + "specification.html"));
        } catch (IOException ex) {
            specificationEditorPane.setText(Lang.loadTabPaneError);
        }
        specificationScrollPane.setViewportView(specificationEditorPane);
        
        
        pseudocodeScrollPane = new JScrollPane();
        pseudocodeScrollPane.setBorder(null);
        tabbedPane.addTab(Lang.pseudocodeTabName, pseudocodeScrollPane);
        tabbedPane.setEnabledAt(3, false);
        
        pseudocodeEditorPane = new JEditorPane();
        pseudocodeEditorPane.setEditable(false);
        try {
            pseudocodeEditorPane.setPage(getClass().getResource(resourcesDir + "pseudocode.html"));
        } catch (IOException ex) {
            pseudocodeEditorPane.setText(Lang.loadTabPaneError);
        }
        pseudocodeScrollPane.setViewportView(pseudocodeEditorPane);
        
        
        textFrame.add(tabbedPane);
        tabbedPane.setPreferredSize(new Dimension(500, 400));
        textFrame.setResizable(true);
        textFrame.pack();
        //</editor-fold>
        
        mainClass.addToDesktop(textFrame);
        textFrame.setVisible(true);
        
        
        
        frame.pack();
        sizeSlider.setValue(8);
        randomValuesButton.doClick();
        
        mainClass.addToDesktop(frame);
        frame.setVisible(true);
        
        mainClass.getEditor().setCode(Lang.startCode);
        
        SpecialFunctions.addSpecialFunction(new Check());
        SpecialFunctions.addSpecialFunction(new Compare());
        SpecialFunctions.addSpecialFunction(new Start());
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        
    }
    
    @Override
    public void threadStop() {
        for (int i = 0; i < 32; i++) {
            valuesTextField[i].setEditable(true);
        }
        buttonsPanel.setVisible(true);
        frame.pack();
    }
    
    @Override
    public boolean pauseStart(SyntaxNode node, final int delayTime) {
        return true;
//        if (node == null || node != callNode) {
//            updateTextFieldsPosition();
//            return;
//        }
//        currValuesTextField[prevIdx1].setForeground(Color.RED);
//        currValuesTextField[prevIdx2].setForeground(Color.RED);
//        if (callNode.getName().equals(Lang.swapFunciton) && swapAnimation) {
//            swapFields(prevIdx1, prevIdx2);
//            updateTextFieldsPosition();
//            duringAnimation = true;
//            (new Thread() {
//                @Override
//                public void run() {
//                    JTextField v1 = currValuesTextField[prevIdx1];
//                    JTextField v2 = currValuesTextField[prevIdx2];
//                    currValuesInnerPanel.setComponentZOrder(v1, 0);
//                    currValuesInnerPanel.setComponentZOrder(v2, 0);
//                    int x1 = v1.getX(), y1 = v1.getY();
//                    int x2 = v2.getX(), y2 = v2.getY();
//                    int numberOfSlices = delayTime / 10;
//                    for (int i = 1; i <= numberOfSlices; i++) {
//                        v1.setLocation(x1 + (x2 - x1) * i / numberOfSlices, y1 + (y2 - y1) * i / numberOfSlices);
//                        v2.setLocation(x2 + (x1 - x2) * i / numberOfSlices, y2 + (y1 - y2) * i / numberOfSlices);
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException ex) {
//                        }
//                    }
//                    currValuesTextField[prevIdx1] = v2;
//                    currValuesTextField[prevIdx2] = v1;
//                    duringAnimation = false;
//                }
//            }).start();
//        }
//        updateTextFieldsPosition();
    }

    @Override
    public void pauseStop(SyntaxNode node) {
//        if (node == callNode) {
//            while ( duringAnimation ) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) {
//                }
//            }
//            currValuesTextField[prevIdx1].setForeground(Color.BLACK);
//            currValuesTextField[prevIdx2].setForeground(Color.BLACK);
//        }
//        callNode = null;
    }

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
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
            return 1;
        }
        @Override
        public VariableType getArgumentType(int index) {
            return VariableType.INTEGER;
        }
        
        @Override
        public SyntaxNode commit(Instance instance) {
//            int n = sizeSlider.getValue(), idx = -1;
//            for (int i=0; i<n-1; i++) {
//                if ( v1>v2 ) {
//                    idx = i;
//                    break;
//                }
//            }
//            //TODO 
//            System.out.println(idx);
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
            callNode = instance.getCallNode();
            int v1 = Integer.parseInt(valuesTextField[prevIdx1].getText());
            int v2 = Integer.parseInt(valuesTextField[prevIdx2].getText());
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
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String arrayFrameTitle = "Tablica liczb";
        
        public static final String searchedElement = "Szukany element: ";
        
        //public static final String startButton = "<html>S<br/>T<br/>A<br/>R<br/>T</html>";
        public static final String startButton = "START";
        public static final String sizeLabel = "Rozmiar tablicy: ";
        public static final String sortValuesLabel = "Ustaw wartości posortowane w sposób: ";
        public static final String incSortValuesButton = "rosnący";
        public static final String decSortValuesButton = "malejący";
        public static final String randomValuesButton = "Losuj";
        public static final String randomValuesLabel = " wartości z przedziału od 0 do ";
        
        public static final String startFunciton = "start";
        public static final String checkFunciton = "sprawdz";
        public static final String swapFunciton = "zamien";
        public static final String compareFunciton = "porownaj";
        public static final String colorFunciton = "pokoloruj";
        
        public static final String taskTabName = "Treść zadania";
        public static final String specialFunctionsTabName = "Funkcje specjalne";
        public static final String specificationTabName = "Specyfikacja";
        public static final String pseudocodeTabName = "Pseudokod";
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
        
        public static final String showTaskText = "Wyświetl treść zadania";
        public static final String showUserSolution = "Pokaż własne rozwiązanie";
        public static final String showSolution = "Wyświetl poprawne rozwiazanie dla sortowania:";
        public static final String selectionSort = "przez wybór";
        public static final String insertionSort = "przez wstawienie";
        public static final String bubbleSort = "bąbelkowego";
        
        public static final String user = "Pokaż własne rozwiązanie";
        public static final String task = "Pokaż treść zadania";
        public static final String hint = "Pokaż wskazówkę (pseudokod)";
        public static final String solution = "Pokaż rozwiązanie";
        
        public static final String yes = "Tak";
        public static final String no = "Nie";
        
        public static final String showSolutionConfirmationTitle = "Potwierdzenie";
        public static final String showSolutionConfirmation = 
                "Jeśli masz problemy z rozwiązaniem tego zadania,\n"
                + "spróbuj nejpierw skorzystać ze wskazwóski.\n"
                + "Czy na pewno chcesz zobaczyć rozwiązanie tego zadania?";
        
        public static final String solutionCode = 
                "void start(n&) {}\n"
                + "void sprawdz(idx) {}\n"
                + "int porownaj(idx1, idx2) {}\n"
                + "\n"
                + "int idxMaxElement(n) \n"
                + "var idx, i;\n"
                + "{\n"
                + "  idx = 0;\n"
                + "  for (i = 1; i < n; i++) {\n"
                + "    if (porownaj(i, idx) > 0) {\n"
                + "      idx = i;\n"
                + "    }\n"
                + "  }\n"
                + "  return idx;\n"
                + "}\n"
                + "\n"
                + "void main()\n"
                + "var n, idx;\n"
                + "{\n"
                + "   start(n);\n"
                + "   idx = idxMaxElement(n);\n"
                + "   write(idx,\"\\n\");\n"
                + "   sprawdz(idx);\n"
                + "}";
        public static final String startCode = 
                "void start(n&) {}\n"
                + "void sprawdz(idx) {}\n"
                + "int porownaj(idx1, idx2) {}\n"
                + "\n"
                + "int idxMaxElement(n) \n"
                + "var idx, i;\n"
                + "{\n"
                + "   \n"
                + "}\n"
                + "\n"
                + "void main()\n"
                + "var n, idx;\n"
                + "{\n"
                + "   start(n);\n"
                + "   idx = idxMaxElement(n);\n"
                + "   write(idx,\"\\n\");\n"
                + "   sprawdz(idx);\n"
                + "}";
    }
    //</editor-fold>
}

