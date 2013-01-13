package mainclass;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import codeeditor.CodeEditor;
import console.Console;
import instancetree.TreeOfInstances;
import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import lesson.Lesson;
import instanceframe.InstanceFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import lesson.EmptyLesson;
import lesson.LessonLoader;
import lesson._07A_PartitionFunction.PartitionFunctionLessonLoader;
import parser.ProgramError;
import stack.StackOfInstances;
import statistics.Statistics;
import stringcreator.StringCreator;
import syntax.SyntaxTree;
//</editor-fold>

public class MainClass {
    
    private final static String iconDir = "/mainclass/icons/";
    
    //<editor-fold defaultstate="collapsed" desc="Components">
    private JFrame mainFrame;
    
    private JLabel statusLabel;
    private JPanel statusPanel;
    private JDesktopPane desktop;
    private JMenuBar menuBar;
    
    private JMenu lessonMenu;
    
    private JMenu fileMenu;
    private JMenuItem loadFileMenuItem;
    private JMenuItem saveFileMenuItem;
    private JMenuItem loadLessonMenuItem;
    private JMenuItem closeLessonMenuItem;
    
    private JMenu programMenu;
    private JMenuItem runMenuItem;
    private JMenuItem pauseMenuItem;
    private JMenuItem stepIntoMenuItem;
    private JMenuItem stepOverMenuItem;
    private JMenuItem stepOutMenuItem;
    private JMenuItem fastRunMenuItem;
    private JMenuItem stopMenuItem;
    private JMenuItem slowerMenuItem;
    private JMenuItem fasterMenuItem;
    private JMenuItem checkSyntaxMenuItem;
    
    private JMenu chooseLessonMenu;
    private TreeMap<String, LessonLoader> lessonMap = new TreeMap<>();
    
    private JMenuItem chooseIntroductionLessonMenuItem;
    private JMenuItem chooseMinElementLessonMenuItem;
    private JMenuItem chooseRecursionIntroLessonMenuItem;
    private JMenuItem chooseArithmeticSeriesLessonMenuItem;
    private JMenuItem chooseExponentiationLessonMenuItem;
    private JMenuItem chooseEuclideanAlgorithmLessonMenuItem;
    private JMenuItem chooseHornerSchemaLessonMenuItem;
    private JMenuItem chooseBinarySearchLessonMenuItem;
    private JMenuItem chooseMergeFunctionLessonMenuItem;
    private JMenuItem choosePartitionFunctionLessonMenuItem;
    
    private JMenu helpMenu;
    private JMenuItem helpMenuItem;
    private JMenuItem aboutMenuItem;
    private JDialog aboutDialog;
    
    private JToolBar toolBar;
    private JButton runButton;
    private JButton pauseButton;
    private JButton stepIntoButton;
    private JButton stepOverButton;
    private JButton stepOutButton;
    private JButton fastRunButton;
    private JButton stopButton;
    
    private JLabel visualizationSpeedLabel;
    private JSlider visualizationSpeedSlider;
    private JPanel lessonPanel;
    
    private CodeEditor editor;
    private StackOfInstances stack;
    private TreeOfInstances tree;
    private InstanceFrame instanceFrame;
    private Console console;
    private Statistics statistics;
    
    private InterpreterThread thread;
    private Lesson emptyLesson = new EmptyLesson();
    private Lesson lesson = emptyLesson;
    
    private Font statusLabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private FontMetrics statusLabelFontMetrics;
    private Border redBorder = BorderFactory.createLineBorder(Color.RED);
    private Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
    private ProgramError error;
    private StringCreator statusCreator;
    //</editor-fold>

    private JMenu windowMenu;
    private ArrayList<JInternalFrame> windowFrame = new ArrayList<>();
    private ArrayList<InternalFrameListener> windowLessonFrameListener = new ArrayList<>();
    private ArrayList<JInternalFrame> windowLessonFrame = new ArrayList<>();
    private ArrayList<JMenu> windowLessonFrameMenu = new ArrayList<>();
    
    
    public MainClass() {    
        mainFrame = new JFrame(Lang.frameTitle);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveMainFramesPositions();
            }
        });
        mainFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        desktop = new JDesktopPane();
        
        lessonPanel = new JPanel();
        
        //<editor-fold defaultstate="collapsed" desc="Init statusLabel">
        statusLabel = new JLabel(Lang.statusDots);
        statusLabel.setFont(statusLabelFont);
        statusLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setStatusLabelText();
            }
        });
        statusLabelFontMetrics = statusLabel.getFontMetrics(statusLabelFont);
        
        statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(0, statusLabelFontMetrics.getHeight()+4));
        statusPanel.setBorder(blackBorder);
        
        statusPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new Insets(1, 2, 1, 2);
        statusPanel.add(statusLabel, constraints);
        statusPanel.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if ( error!=null ) {
                    editor.selectText(error.getLeftIndex(), error.getRightIndex());
                }
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init run-buttons">
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        Insets margin = new Insets(2, 4, 2, 4);
        ImageIcon icon = new ImageIcon(getClass().getResource(iconDir + "run.png"));
        runButton = new JButton(icon);
        runButton.setMargin(margin);
        runButton.setFocusable(false);
        runButton.setToolTipText(Lang.runHint);
        toolBar.add(runButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "pause.png"));
        pauseButton = new JButton(icon);
        pauseButton.setMargin(margin);
        pauseButton.setFocusable(false);
        pauseButton.setToolTipText(Lang.pauseHint);
        toolBar.add(pauseButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "step-into.png"));
        stepIntoButton = new JButton(icon);
        stepIntoButton.setMargin(margin);
        stepIntoButton.setFocusable(false);
        stepIntoButton.setToolTipText(Lang.stepIntoHint);
        toolBar.add(stepIntoButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "step-over.png"));
        stepOverButton = new JButton(icon);
        stepOverButton.setMargin(margin);
        stepOverButton.setFocusable(false);
        stepOverButton.setToolTipText(Lang.stepOverHint);
        toolBar.add(stepOverButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "step-out.png"));
        stepOutButton = new JButton(icon);
        stepOutButton.setMargin(margin);
        stepOutButton.setFocusable(false);
        stepOutButton.setToolTipText(Lang.stepOutHint);
        toolBar.add(stepOutButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "fast.png"));
        fastRunButton = new JButton(icon);
        fastRunButton.setMargin(margin);
        fastRunButton.setFocusable(false);
        fastRunButton.setToolTipText(Lang.fastRunHint);
        toolBar.add(fastRunButton);

        icon = new ImageIcon(getClass().getResource(iconDir + "stop.png"));
        stopButton = new JButton(icon);
        stopButton.setMargin(margin);
        stopButton.setFocusable(false);
        stopButton.setToolTipText(Lang.stopHint);
        toolBar.add(stopButton);
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(new Integer(2), new JLabel(Lang.slow));
        labelTable.put(new Integer(17), new JLabel(Lang.fast));
        visualizationSpeedLabel = new JLabel(Lang.visualisationSpeed);
        visualizationSpeedSlider = new JSlider(0, 19, 10);
        visualizationSpeedSlider.setMinorTickSpacing(1);
        visualizationSpeedSlider.setPaintTicks(true);
        visualizationSpeedSlider.setSnapToTicks(true);
        visualizationSpeedSlider.setLabelTable(labelTable);
        visualizationSpeedSlider.setPaintLabels(true);
        visualizationSpeedSlider.setPreferredSize(
                new Dimension(200, visualizationSpeedSlider.getPreferredSize().height));
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init Menu">
        menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        
        lessonMenu = new JMenu(Lang.lessonMenu);
        lessonMenu.setEnabled(false);
        menuBar.add(lessonMenu);

        fileMenu = new JMenu(Lang.fileMenu);
        menuBar.add(fileMenu);

        loadFileMenuItem = new JMenuItem(Lang.openFileDots);
        loadFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFile();
            }
        });
        fileMenu.add(loadFileMenuItem);

        saveFileMenuItem = new JMenuItem(Lang.saveFileDots);
        saveFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFile();
            }
        });
        fileMenu.add(saveFileMenuItem);
        
        loadLessonMenuItem = new JMenuItem(Lang.loadLessonDots);
        loadLessonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadLesson();
            }
        });
        fileMenu.add(loadLessonMenuItem);

        closeLessonMenuItem = new JMenuItem(Lang.closeLesson);
        closeLessonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeLesson();
            }
        });
        fileMenu.add(closeLessonMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init program menu">
        programMenu = new JMenu(Lang.programMenu);
        menuBar.add(programMenu);
        
        runMenuItem = new JMenuItem(Lang.run);
        runMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        programMenu.add(runMenuItem);
        
        pauseMenuItem = new JMenuItem(Lang.pause);
        pauseMenuItem.setAccelerator(KeyStroke.getKeyStroke("F6"));
        programMenu.add(pauseMenuItem);
        
        stepIntoMenuItem = new JMenuItem(Lang.stepInto);
        stepIntoMenuItem.setAccelerator(KeyStroke.getKeyStroke("F7"));
        programMenu.add(stepIntoMenuItem);
        
        stepOverMenuItem = new JMenuItem(Lang.stepOver);
        stepOverMenuItem.setAccelerator(KeyStroke.getKeyStroke("F8"));
        programMenu.add(stepOverMenuItem);
        
        stepOutMenuItem = new JMenuItem(Lang.stepOut);
        stepOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.ALT_DOWN_MASK));
        programMenu.add(stepOutMenuItem);
        
        fastRunMenuItem = new JMenuItem(Lang.fastRun);
        fastRunMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.ALT_DOWN_MASK));
        programMenu.add(fastRunMenuItem);
        
        stopMenuItem = new JMenuItem(Lang.stop);
        stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.ALT_DOWN_MASK));
        programMenu.add(stopMenuItem);
        
        programMenu.add(new JSeparator());
        
        slowerMenuItem = new JMenuItem(Lang.slower);
        slowerMenuItem.setAccelerator(KeyStroke.getKeyStroke('-',InputEvent.ALT_DOWN_MASK));
        programMenu.add(slowerMenuItem);
        
        fasterMenuItem = new JMenuItem(Lang.faster);
        fasterMenuItem.setAccelerator(KeyStroke.getKeyStroke('=',InputEvent.ALT_DOWN_MASK));
        programMenu.add(fasterMenuItem);
        
        programMenu.add(new JSeparator());
        
        checkSyntaxMenuItem = new JMenuItem(Lang.checkSyntax);
        checkSyntaxMenuItem.setAccelerator(KeyStroke.getKeyStroke("F4"));
        programMenu.add(checkSyntaxMenuItem);
        //</editor-fold>
        
        initChooseLessonMenu();
        //old
        //<editor-fold defaultstate="collapsed" desc="Init choose lesson menu">
//        chooseLessonMenu = new JMenu(Lang.chooseLesson);
//        menuBar.add(chooseLessonMenu);
//        
//        chooseIntroductionLessonMenuItem = new JMenuItem(Lang.chooseIntroductionLesson);
//        chooseIntroductionLessonMenuItem.addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if ( lesson!=null ) {
//                    lesson.close();
//                    lesson = null;
//                }
//                
//                lesson = new IntroductionLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseIntroductionLessonMenuItem);
//        
//        chooseMinElementLessonMenuItem = new JMenuItem(Lang.chooseMinElementLesson);
//        chooseMinElementLessonMenuItem.addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if ( lesson!=null ) {
//                    lesson.close();
//                    lesson = null;
//                }
//                
//                lesson = new MinElementLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseMinElementLessonMenuItem);
//        chooseLessonMenu.add(new JSeparator());
//        
//        chooseRecursionIntroLessonMenuItem = new JMenuItem(Lang.chooseRecursionIntroLesson);
//        chooseRecursionIntroLessonMenuItem.addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if ( lesson!=null ) {
//                    lesson.close();
//                    lesson = null;
//                }
//                
//                lesson = new RecursionIntroLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseRecursionIntroLessonMenuItem);
//        
//        
//        chooseArithmeticSeriesLessonMenuItem = new JMenuItem(Lang.chooseArithmeticSeriesLessonMenuItem);
//        chooseArithmeticSeriesLessonMenuItem.addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if ( lesson!=null ) {
//                    lesson.close();
//                    lesson = null;
//                }
//                
//                lesson = new ArithmeticSeriesLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseArithmeticSeriesLessonMenuItem);
//        chooseLessonMenu.add(new JSeparator());
//        
//        chooseExponentiationLessonMenuItem = new JMenuItem(Lang.chooseExponentiationLessonMenuItem);
//        chooseExponentiationLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new ExponentiationLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseExponentiationLessonMenuItem);
//
//        chooseEuclideanAlgorithmLessonMenuItem = new JMenuItem(Lang.chooseEuclideanAlgorithmLessonMenuItem);
//        chooseEuclideanAlgorithmLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new EuclideanAlgorithmLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseEuclideanAlgorithmLessonMenuItem);
//        
//        chooseHornerSchemaLessonMenuItem = new JMenuItem(Lang.chooseHornerSchemaLessonMenuItem);
//        chooseHornerSchemaLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new HornerSchemaLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseHornerSchemaLessonMenuItem);
//        
//        chooseBinarySearchLessonMenuItem = new JMenuItem(Lang.chooseBinarySearchLessonMenuItem);
//        chooseBinarySearchLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new BinarySearchLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseBinarySearchLessonMenuItem);
//        
//        chooseLessonMenu.add(new JSeparator());
//        
//        chooseMergeFunctionLessonMenuItem = new JMenuItem(Lang.chooseMergeFunctionLessonMenuItem);
//        chooseMergeFunctionLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new MergeFunctionLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(chooseMergeFunctionLessonMenuItem);
//
//        chooseLessonMenu.add(new JSeparator());
//        
//        choosePartitionFunctionLessonMenuItem = new JMenuItem(Lang.choosePartitionFunctionLessonMenuItem);
//        choosePartitionFunctionLessonMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                if (lesson != null) {
//                    lesson.close();
//                    lesson = null;
//                }
//
//                lesson = new PartitionFunctionLesson(MainClass.this);
//                lesson.start();
//            }
//        });
//        chooseLessonMenu.add(choosePartitionFunctionLessonMenuItem);
        //</editor-fold>
        
        windowMenu = new JMenu(Lang.windowMenu);
        menuBar.add(windowMenu);
        JMenuItem cascade = new JMenuItem(Lang.cascadeLayoutMenuItem);
        cascade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int offset = 0;
                for(JInternalFrame frame : windowFrame) {
                    frame.pack();
                    frame.setLocation(offset, offset);
                    frame.setVisible(true);
                    frame.toFront();
                    offset += 30;
                }
            }
        });
        windowMenu.add(cascade);
        windowMenu.add(new JSeparator());
        
        
        //<editor-fold defaultstate="collapsed" desc="Init help menu">
        aboutDialog = new AboutFrame();
        
        helpMenu = new JMenu(Lang.help);
        menuBar.add(helpMenu);
        
        helpMenuItem = new JMenuItem(Lang.showHelp);
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        helpMenu.add(helpMenuItem);
        
        aboutMenuItem = new JMenuItem(Lang.about);
        helpMenu.add(aboutMenuItem);
        
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO about
                aboutDialog.setVisible(true);
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Run-buttons and program menu listeners">
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButtonClick();
            }
        };
        runButton.addActionListener(actionListener);
        runMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButtonClick();
            }
        };
        pauseButton.addActionListener(actionListener);
        pauseMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepIntoButtonClick();
            }
        };
        stepIntoButton.addActionListener(actionListener);
        stepIntoMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepOverButtonClick();
            }
        };
        stepOverButton.addActionListener(actionListener);
        stepOverMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepOutButtonClick();
            }
        };
        stepOutButton.addActionListener(actionListener);
        stepOutMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fastRunButtonClick();
            }
        };
        fastRunButton.addActionListener(actionListener);
        fastRunMenuItem.addActionListener(actionListener);
        
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButtonClick();
            }
        };
        stopButton.addActionListener(actionListener);
        stopMenuItem.addActionListener(actionListener);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Visalisation speed listeners">
        slowerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreaseVisualistaionSpeed();
            }
        });
        
        fasterMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseVisualistaionSpeed();
            }
        });
        
        visualizationSpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                visulaisationSpeedChanged();
            }
        });
        
        checkSyntaxMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSyntaxClick();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(mainFrame.getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2)
                .addComponent(visualizationSpeedLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(visualizationSpeedSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2)
                .addComponent(lessonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(1)
                .addComponent(statusPanel, 0, 0, Short.MAX_VALUE)
                .addGap(1))
            .addComponent(desktop, 0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(visualizationSpeedLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(visualizationSpeedSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lessonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGap(1)
            .addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(2)
            .addComponent(desktop, 0, 600, Short.MAX_VALUE)
        );
        mainFrame.getContentPane().setLayout(layout);
        //</editor-fold>
        
        
        
        mainFrame.setSize(800, 600);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);
        
        //lesson = new SortLesson(this);
        //lesson = new IntroductionLesson(this);
        //lesson = new QuickSortPartitionLesson(this);
        //lesson = new SortLesson(this);
//        lesson = new SortTaskLesson(this);
//        lesson.start();
//        lesson = new BubbleSortLesson(this);
//        lesson.start();
//        
//        //<editor-fold defaultstate="collapsed" desc="Init Frames">
//        editor = new CodeEditor(this);
//        desktop.add(editor.getFrame());
//        editor.getFrame().show();
//
//        console = new Console();
//        desktop.add(console.getFrame());
//        console.getFrame().show();
//        
//        tree = new TreeOfInstances(this);
//        desktop.add(tree.getFrame());
//        tree.getFrame().show();
//        
//        instanceFrame = new InstanceFrame(this);
//        desktop.add(instanceFrame.getFrame());
//        instanceFrame.getFrame().show();
//
//        stack = new StackOfInstances(this, instanceFrame);
//        desktop.add(stack.getFrame());
//        stack.getFrame().show();
//        
//        statistics = new Statistics();
//        desktop.add(statistics.getFrame());
//        statistics.getFrame().show();
//        //</editor-fold>
//        
        
        editor = new CodeEditor(this);
        console = new Console();
        tree = new TreeOfInstances(this);
        instanceFrame = new InstanceFrame(this);
        stack = new StackOfInstances(this, instanceFrame);
        statistics = new Statistics();
        
        addFrame(Lang.codeEditorMenu, editor.getFrame(), false);
        addFrame(Lang.callStackMenu, stack.getFrame(), false);
        addFrame(Lang.callTreeMenu, tree.getFrame(), false);
        addFrame(Lang.instanceFrameMenu, instanceFrame.getFrame(), false);
        addFrame(Lang.consoleMenu, console.getFrame(), false);
        addFrame(Lang.statisticsMenu, statistics.getFrame(), false);
        cascade.doClick();
        
        loadMainFramesPositions();
    }
    
    private void initChooseLessonMenu() {
        chooseLessonMenu = new JMenu(Lang.chooseLesson);
        menuBar.add(chooseLessonMenu);
        addLessonLoader(new PartitionFunctionLessonLoader());
    }
    private void addLessonLoader(final LessonLoader loader) {
        lessonMap.put(loader.getLessonKey(), loader);
        
        JMenuItem menuItem = new JMenuItem(loader.getLessonName());
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lesson.close();
                lesson = loader.getLesson(MainClass.this, null);
            }
        });
        chooseLessonMenu.add(menuItem);
    }
    
    public void addAddictionalLessonFrame(String title, JInternalFrame frame) {
        addFrame(title, frame, true);
    }
    public void removeAddictionalLessonFrame(JInternalFrame frame) {
        frame.setVisible(false);
        desktop.remove(frame);
        
        int idx = windowLessonFrame.indexOf(frame);
        frame.removeInternalFrameListener(windowLessonFrameListener.get(idx));
        windowMenu.remove(9+idx);
        windowLessonFrameListener.remove(idx);
        windowLessonFrameMenu.remove(idx);
        windowLessonFrame.remove(idx);
        windowFrame.remove(frame);
        if ( windowLessonFrame.isEmpty() ) {
            windowMenu.remove(8);
        }
    }
    
    private void addFrame(String title, final JInternalFrame frame, boolean lessonFrame) {
        JMenu menu = new JMenu(title);

        final JCheckBoxMenuItem show = new JCheckBoxMenuItem(Lang.showHideMenu);
        show.setSelected(frame.isVisible());
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(!frame.isVisible());
            }
        });
        menu.add(show);

        JMenuItem restore = new JMenuItem(Lang.restoreMenu);
        restore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.pack();
                frame.setLocation(0, 0);
                frame.setVisible(true);
                frame.toFront();
            }
        });
        menu.add(restore);

        int modifiers;
        char key;
        if (lessonFrame) {
            key = (char) ('1' + windowLessonFrame.size());
            modifiers = InputEvent.CTRL_DOWN_MASK;
            windowLessonFrame.add(frame);
            if (windowLessonFrameMenu.isEmpty()) {
                windowMenu.add(new JSeparator());
            }
            windowLessonFrameMenu.add(menu);
        } else {
            key = (char) ('1' + windowFrame.size());
            modifiers = InputEvent.ALT_DOWN_MASK;
        }
        desktop.add(frame);
        windowFrame.add(frame);
        show.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        modifiers = modifiers | InputEvent.SHIFT_DOWN_MASK;
        restore.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));

        frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        frame.setClosable(true);
        InternalFrameListener listener = new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                show.setSelected(false);
            }
        };
        frame.addInternalFrameListener(listener);
        if (lessonFrame) {
            windowLessonFrameListener.add(listener);
        }
        windowMenu.add(menu);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Get main frames methods">
    public CodeEditor getEditor() {
        return editor;
    }
    public InstanceFrame getInstanceFrame() {
        return instanceFrame;
    }
    public Console getConsole() {
        return console;
    }
    public StackOfInstances getStackOfInstances() {
        return stack;
    }
    public TreeOfInstances getTreeOfInstances() {
        return tree;
    }
    public Statistics getStatistics() {
        return statistics;
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Desktop functions">
    public JDesktopPane getDesktop() {
        return desktop;
    }
    public void addToDesktop(JInternalFrame frame) {
        desktop.add(frame);
    }
    public void removeFromDesktop(JInternalFrame frame) {
        desktop.remove(frame);
    }
    //</editor-fold>
    
    
    private void loadMainFramesPositions(DataInputStream stream, boolean showMessage) throws IOException {
        byte buffor[] = new byte[10];
        stream.read(buffor);
        for (byte b : buffor) {
            if ((b < '0' || '9' < b) && (b < 'A' || 'Z' < b) && (b < 'a' || 'z' < b)) {
                if (showMessage) {
                    JOptionPane.showMessageDialog(null, Lang.notSaveLessonFile);
                }
                return;
            }
        }
        String key = new String(buffor);
        LessonLoader loader = lessonMap.get(key);
        if (loader == null) {
            if (showMessage) {
                JOptionPane.showMessageDialog(null, Lang.notSaveLessonFile);
            }
            return;
        }
    }
    
    public void saveMainFramesPositions(DataInputStream stream) {
        File dir = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (dir.isFile()) {
            dir = dir.getParentFile();
        }
        String path = dir.getAbsolutePath() + File.separator + "defaultSave";
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(
                    new FileOutputStream(path));
            editor.savePosition(dataOutputStream);
            instanceFrame.savePosition(dataOutputStream);
            console.savePosition(dataOutputStream);
            stack.savePosition(dataOutputStream);
            tree.savePosition(dataOutputStream);
            statistics.savePosition(dataOutputStream);
        } catch (IOException ex) {
        }
    }
    //<editor-fold defaultstate="collapsed" desc="Load/Save frames positions">
    public void loadMainFramesPositions() {
        File dir = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (dir.isFile()) {
            dir = dir.getParentFile();
        }
        String path = dir.getAbsolutePath() + File.separator + "defaultSave";
        try {
            DataInputStream dataInputStream = new DataInputStream(
                    new FileInputStream(path));
            editor.loadPosition(dataInputStream);
            instanceFrame.loadPosition(dataInputStream);
            console.loadPosition(dataInputStream);
            stack.loadPosition(dataInputStream);
            tree.loadPosition(dataInputStream);
            statistics.loadPosition(dataInputStream);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }
    
    public void saveMainFramesPositions() {
        File dir = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (dir.isFile()) {
            dir = dir.getParentFile();
        }
        String path = dir.getAbsolutePath() + File.separator + "defaultSave";
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(
                    new FileOutputStream(path));
            editor.savePosition(dataOutputStream);
            instanceFrame.savePosition(dataOutputStream);
            console.savePosition(dataOutputStream);
            stack.savePosition(dataOutputStream);
            tree.savePosition(dataOutputStream);
            statistics.savePosition(dataOutputStream);
        } catch (IOException ex) {
        }
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Public functions">
    public Lesson getLesson() {
        return lesson;
    }
    
    public JPanel getLessonPanel() {
        return lessonPanel;
    }
    
    public JMenu getLessonMenu() {
        return lessonMenu;
    }
    
    public void setStatus(StringCreator statusCreator) {
        this.statusCreator = statusCreator;
        if (statusCreator != null) {
            statusCreator.setFontMetrics(statusLabelFontMetrics);
        }
        if ( error!=null ) {
            statusPanel.setBorder(blackBorder);
            error = null;
        }
        setStatusLabelText();
    }
    
    public void setError(ProgramError error) {
        this.error = error;
        statusCreator = error.getStringCreator();
        statusPanel.setBorder(redBorder);
        setStatusLabelText();
    }
    
    public void clearThread() {
        thread = null;
    }
    
    public String getCurrentDir() {
        //TODO curr dir
        return ".";
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Run-buttons functions">
    private void tryStartThread() {
        if ( thread==null ) {
            SyntaxTree syntaxTree = editor.getSyntaxTree();
            if (syntaxTree != null) {
                thread = new InterpreterThread(syntaxTree, this);
                thread.start();
            }
        }
    }
    private void runButtonClick() {
        tryStartThread();
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestRun();
        }
    }
    private void pauseButtonClick() {
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestPause();
        }
    }
    private void stepIntoButtonClick() {
        tryStartThread();
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestStepInto();
        }
    }
    private void stepOverButtonClick() {
        tryStartThread();
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestStepOver();
        }
    }
    private void stepOutButtonClick() {
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestStepOut();
        }
    }
    private void fastRunButtonClick() {
        tryStartThread();
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestFast();
        }
    }
    private void stopButtonClick() {
        InterpreterThread th = thread;
        if ( th!=null ) {
            th.requestStop();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Visualisation speed and CheckSyntax functions">
    private void checkSyntaxClick()
    {
        editor.getSyntaxTree();
    }
    private void increaseVisualistaionSpeed()
    {
        int value = visualizationSpeedSlider.getValue();
        visualizationSpeedSlider.setValue(value+1);
    }
    private void decreaseVisualistaionSpeed()
    {
        int value = visualizationSpeedSlider.getValue();
        if ( value > visualizationSpeedSlider.getMinimum() )
        {
            visualizationSpeedSlider.setValue(value-1);
        }
    }
    private void visulaisationSpeedChanged()
    {
        //TODO
        System.out.println(visualizationSpeedSlider.getValue());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private functions">
    private void setStatusLabelText() {
        if ( statusCreator==null ) {
            statusLabel.setText(Lang.statusDots);
            return;
        }
        int maxWidth = statusLabel.getWidth();
        String str = statusCreator.getString(maxWidth);
        statusLabel.setText(str);
    }
    
    private void loadLesson() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setApproveButtonText(Lang.open);
        chooser.setDialogTitle(Lang.open);
        int returnVal = chooser.showOpenDialog(mainFrame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            URL urls[] = new URL[]{chooser.getSelectedFile().toURI().toURL()};
            ClassLoader cl = new URLClassLoader(urls);
            Class starter = cl.loadClass("Starter");
            Method start = starter.getMethod("start", MainClass.class);
            if (start != null && lesson!=null) {
                lesson.close();
                lesson = null;
            }
            lesson = (Lesson) start.invoke(null, new Object[]{this});
        } catch (Exception ex) {
        }
    }
    
    private void closeLesson() {
        if (lesson != null) {
            lesson.close();
            lesson = null;
        }
    }
    
    private void openFile() {  
        JFileChooser chooser = new JFileChooser(".");
        chooser.setApproveButtonText(Lang.open);
        chooser.setDialogTitle(Lang.open);
        int returnVal = chooser.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            editor.load(chooser.getSelectedFile().getAbsolutePath());
        }
    }                                            

    private void saveFile() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setApproveButtonText(Lang.save);
        chooser.setDialogTitle(Lang.save);
        int returnVal = chooser.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            editor.save(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="AboutFrame">
    private static class AboutFrame extends JDialog {

        private Font serifPlainFont = new Font(Font.SERIF, Font.PLAIN, 12);
        private Font serifBoldFont = new Font(Font.SERIF, Font.BOLD, 12);

        private ImageIcon icon = new ImageIcon(getClass().getResource(iconDir + "uni-wroc-logo.png"));
        private JLabel iconLabel = new JLabel(icon);

        private JLabel authorLeftLabel = new JLabel(Lang.authorLabel);
        private JLabel authorRightLabel = new JLabel(Lang.author);

        private JLabel emailLeftLabel = new JLabel(Lang.emailLabel);
        private JLabel emailRightLabel = new JLabel(Lang.email);

        private JLabel aboutLabel = new JLabel(Lang.aboutDescription);

        private JLabel wwwLeftLabel = new JLabel(Lang.wwwLabel);
        private JLabel wwwRightLabel = new JLabel(Lang.www);

        public AboutFrame() {
            icon = new ImageIcon(getClass().getResource(iconDir + "uni-wroc-logo.png"));
            iconLabel = new JLabel(icon);

            authorLeftLabel = new JLabel(Lang.authorLabel);
            authorLeftLabel.setFont(serifPlainFont);
            authorRightLabel = new JLabel(Lang.author);
            authorRightLabel.setFont(serifBoldFont);

            emailLeftLabel = new JLabel(Lang.emailLabel);
            emailLeftLabel.setFont(serifPlainFont);
            emailRightLabel = new JLabel(Lang.email);
            emailRightLabel.setFont(serifBoldFont);

            aboutLabel = new JLabel(Lang.aboutDescription);
            aboutLabel.setFont(serifPlainFont);

            wwwLeftLabel = new JLabel(Lang.wwwLabel);
            wwwLeftLabel.setFont(serifPlainFont);
            wwwRightLabel = new JLabel(Lang.www);
            wwwRightLabel.setFont(serifBoldFont);

            GroupLayout layout = new GroupLayout(getContentPane());
            layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addGap(10, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(aboutLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(authorLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(emailLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(wwwLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(authorRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(emailRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(wwwRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGap(10, 10, Short.MAX_VALUE)
                .addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addGroup(layout.createParallelGroup()
                    .addComponent(authorLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup()
                    .addComponent(emailLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5)
                .addComponent(aboutLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addGroup(layout.createParallelGroup()
                    .addComponent(wwwLeftLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(wwwRightLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, Short.MAX_VALUE)
            );
            getContentPane().setLayout(layout);


            setTitle(Lang.aboutTitle);
            setResizable(false);
            setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }
    //</editor-fold>
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static String frameTitle = "System REKURENCJA";
        public static String statusDots = "Status...";
        
        public static String chooseLesson = "Wybór lekcji";
        public static String chooseIntroductionLesson = "Wprowadzenie";
        public static String chooseMinElementLesson = "Minimalny element";
        public static String chooseRecursionIntroLesson = "Wprowadzenie do rekurencji";
        public static String chooseArithmeticSeriesLessonMenuItem = "Suma ciągu arytmetycznego";
        public static String chooseExponentiationLessonMenuItem = "Potęgowanie";
        public static String chooseEuclideanAlgorithmLessonMenuItem = "Największy wspólny dzielnik";
        public static String chooseHornerSchemaLessonMenuItem = "Wartość wielomianu";
        public static String chooseBinarySearchLessonMenuItem = "Wyszukiwanie elementu";
        public static String chooseMergeFunctionLessonMenuItem = "Scalanie posortowanych ciągów";
        public static String choosePartitionFunctionLessonMenuItem = "Podział tablicy";
        
        public static String lessonMenu = "Lekcja";
        
        public static String fileMenu = "Plik";
        public static String openFileDots = "Otwórz plik...";
        public static String saveFileDots = "Zapisz plik...";
        public static String open = "Otwórz";
        public static String save = "Zapisz";
        public static String loadLessonDots = "Wczytaj lekcję...";
        public static String closeLesson = "Zamknij lekcję";
        public static String notSaveLessonFile = "Wybrany plik nie jest zapisem stanu lekcji.";
        
        public static String windowMenu = "Okno";
        public static String cascadeLayoutMenuItem = "Ułóż okna kaskadowo";
        public static String showHideMenu = "Pokaż / Ukryj";
        public static String restoreMenu = "Przywróć";
        public static String codeEditorMenu = "Edytor kodu";
        public static String callStackMenu = "Stos wywołań";
        public static String callTreeMenu = "Drzewo wywołań";
        public static String instanceFrameMenu = "Okno instancji";
        public static String consoleMenu = "Konsola";
        public static String statisticsMenu = "Statystyka";
        
        public static String line = "[Linia: ";
        public static String position = "; Pozycja: ";
        
        public static String fast = "Szybko";
        public static String slow = "Wolno";
        public static String visualisationSpeed = "<html>Szybkość<br>wizualizacji:</html>";
        
        public static String help = "Pomoc";
        public static String showHelp = "Pokaż pomoc";
        public static String about = "O programie";
        
        public static String programMenu = "Program";
        public static String run = "Uruchom";
        public static String pause = "Pauza";
        public static String stepInto = "Skocz do";
        public static String stepOver = "Skocz nad";
        public static String stepOut = "Wyskocz";
        public static String fastRun = "Szybkie wykonanie";
        public static String stop = "Zatrzymaj";
        public static String slower = "Wolniej";
        public static String faster = "Szybciej";
        public static String checkSyntax = "Sprawdź składnię";
        
        public static String runHint = "Przycisk uruchamia program (F5)";
        public static String pauseHint = "Przycisk przerywa wykonanie programu (F6)";
        public static String stepIntoHint = "Przycisk powoduje zagłębienie się do wykonywanej funkcji (F7)";
        public static String stepOverHint = "Przycisk powoduje przeskoczenie nad wykonywaną funkcją (F8)";
        public static String stepOutHint = "Przycisk powoduje wyskoczenie z wykonywanej funkcji (F9)";
        public static String fastRunHint = "Przycisk powoduje szybkie wykonanie programu (F10)";
        public static String stopHint = "Przycisk zatrzymuje program (F11)";
        
        
        public static String aboutTitle = "O programie";
        public static String authorLabel = "autor:";
        public static String author = "Piotr Diduszko";
        public static String emailLabel = "e-mail:";
        public static String email = "piodid@gmail.com";
        public static String aboutDescription = 
                "<html><center>Program powstał w ramach pracy magisterskiej w<br/>"
                + "Instytucie Informatyki Uniwersytetu Wrocławskiego</center></html>";
        public static String wwwLabel = "www:";
        public static String www = "www.ii.uni.wroc.pl";
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Function main">
    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
            //UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            
//            Properties props = new Properties();
//            props.put("desktopColor", "223 223 255"); 
//            AcrylLookAndFeel.setCurrentTheme(props);
//            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
            
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            UIDefaults def = UIManager.getLookAndFeel().getDefaults();
            def.put("TextPane.contentMargins", new Insets(0,0,0,0));
            
            final Painter pBtn = (Painter)def.get("Button[Enabled].backgroundPainter");
            Painter<Component> p3 = new Painter<Component>() {
                @Override
                public void paint(Graphics2D g, Component c, int width, int height) {
                    g.translate(-2, -2);
                    pBtn.paint(g, c, width+4, height+4);
                    g.translate(2, 2);
                }
            };
            def.put("Button[Enabled].backgroundPainter", p3);
            def.put("Button.contentMargins", new Insets(2,4,2,4));
        } catch (Exception ex) {
            System.exit(1);
        }
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainClass();
            }
        }); 
    }
    //</editor-fold>
    
}
