package lesson._03A_Exponentiation;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._02B_ArithmeticSeries.*;
import helpers.ReadFileHelper;
import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import syntax.SyntaxNode;
//</editor-fold>

public class ExponentiationLesson implements Lesson {

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State 
    {
        NothingShown(0), Pseudocode1Shown(1), 
        Recursion1Shown(2), Iteration1Shown(3), Both1Shown(4), 
        Part2Shown(6), Hint1Shown(7), Hint2Shown(8), Hint3Shown(9), 
        Recursion2Shown(10), Iteration2Shown(11), Both2Shown(12), 
        SummaryShown(13);
        
        public final byte Id;

        State(int id) {
            Id = (byte) id;
        }
    }
    
    private static enum ChosenCode
    {
        UserRecursion(0),
        UserIteration(1),
        SolutionRecursion(2),
        SolutionIteration(3);
        
        public final byte Id;

        ChosenCode(int id) {
            Id = (byte) id;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private ChosenCode part1ChosenCode = ChosenCode.UserRecursion;
    private ChosenCode part2ChosenCode = ChosenCode.UserRecursion;
    private byte chosenPart;
    
    
    private MainClass mainClass;
    
    private JInternalFrame part1TextFrame;
    private SelectionModel<Tab> part1TabSelectionModel;
    private BooleanProperty part1PseudocodeTabDisabledProperty;
    
    private JInternalFrame part2TextFrame;
    private SelectionModel<Tab> part2TabSelectionModel;
    private BooleanProperty part2HintTabDisabledProperty;
    private BooleanProperty part2SummaryTabDisabledProperty;
    private WebEngine part2HintTabWebEngine;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part1UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionIterationCodeMenuItem;
    private JMenuItem part1gotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2Hint1MenuItem;
    private JMenuItem part2Hint2MenuItem;
    private JMenuItem part2Hint3MenuItem;
    private JRadioButtonMenuItem part2UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionIterationCodeMenuItem;
    private JMenuItem part2SummaryMenuItem;
    private JMenuItem part2gotoPart1MenuItem;
    
    private String oldCode;
    private String part1UserRecursionCode;
    private String part1UserIterationCode;
    private String part1SolutionRecursionCode;
    private String part1SolutionIterationCode;
    private String part2UserRecursionCode;
    private String part2UserIterationCode;
    private String part2SolutionRecursionCode;
    private String part2SolutionIterationCode;
    
    public ExponentiationLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    private void MemorizeCode() {
        if (chosenPart == 0) {
            if (part1ChosenCode == ChosenCode.UserRecursion) {
                part1UserRecursionCode = mainClass.getEditor().getCode();
            } else if (part1ChosenCode == ChosenCode.UserIteration) {
                part1UserIterationCode = mainClass.getEditor().getCode();
            }
        } else {
            if (part2ChosenCode == ChosenCode.UserRecursion) {
                part2UserRecursionCode = mainClass.getEditor().getCode();
            } else if (part2ChosenCode == ChosenCode.UserIteration) {
                part2UserIterationCode = mainClass.getEditor().getCode();
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="part1ShowSolutionConifrm">
    private boolean part1ShowSolutionConifrm() {
        if (state != State.NothingShown) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part1ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showPseudocode, Lang.cancel},
                Lang.showPseudocode);
        if (option != 0) {
            if (part1ChosenCode == ChosenCode.UserIteration) {
                part1UserIterationCodeMenuItem.setSelected(true);
            } else {
                part1UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                part1PseudocodeMenuItem.doClick();
            }
            return false;
        }
        part1PseudocodeTabDisabledProperty.set(false);
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part2ShowSolutionConifrm">
    private boolean part2ShowSolutionConifrm() {
        if (state.Id >= State.Hint3Shown.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part2ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            if (part2ChosenCode == ChosenCode.UserIteration) {
                part2UserIterationCodeMenuItem.setSelected(true);
            } else {
                part2UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                if (state == State.Hint2Shown ) {
                    part2Hint3MenuItem.doClick();
                } else if (state == State.Hint1Shown) {
                    part2Hint2MenuItem.doClick();
                } else {
                    part2Hint1MenuItem.doClick();
                }
            }
            return false;
        }
        part2showHint(3);
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showConifrmDialog">
    private boolean showConifrmDialog(String message, State state) {
        if (this.state.Id >= state.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, message, Lang.question,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.yes, Lang.no}, Lang.no);
        return option == 0;
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1TextFrame">
    private void initPart1TextFrame()
    {
        part1TextFrame = new JInternalFrame(Lang.part1TextFrameTitle);
        final JFXPanel fxPanel = new JFXPanel();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                tabPane.tabMinHeightProperty().set(0d);
                part1TabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.part1TextTabName);
                WebView web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part1_text.html").toString());
                tab.setContent(web);
                tabPane.getTabs().add(tab);

                tab = new Tab(Lang.part1PseudocodeTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part1_pseudocode.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part1PseudocodeTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                Scene scene = new Scene(tabPane);
                fxPanel.setScene(scene);
            }
        });
        
        part1TextFrame.setContentPane(fxPanel);
        part1TextFrame.setPreferredSize(new Dimension(700, 500));
        part1TextFrame.setResizable(true);
        part1TextFrame.setVisible(false);
        mainClass.getDesktop().add(part1TextFrame);
        part1TextFrame.pack();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2TextFrame">
    private void initPart2TextFrame()
    {
        part2TextFrame = new JInternalFrame(Lang.part2TextFrameTitle);
        final JFXPanel fxPanel = new JFXPanel();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                tabPane.tabMinHeightProperty().set(0d);
                part2TabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.part2TextTabName);
                WebView web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part2_text.html").toString());
                tab.setContent(web);
                tabPane.getTabs().add(tab);

                tab = new Tab(Lang.part2HintTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part2_hint.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part2HintTabWebEngine = web.getEngine();
                part2HintTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.part2SummaryTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part2_summary.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part2SummaryTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                Scene scene = new Scene(tabPane);
                fxPanel.setScene(scene);
            }
        });
        
        part2TextFrame.setContentPane(fxPanel);
        part2TextFrame.setPreferredSize(new Dimension(700, 500));
        part2TextFrame.setResizable(true);
        part2TextFrame.setVisible(false);
        mainClass.getDesktop().add(part2TextFrame);
        part2TextFrame.pack();
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="part1GotoPart2">
    private void part1GotoPart2()
    {
        part1TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2Hint1MenuItem);
        lessonMenu.add(part2Hint2MenuItem);
        lessonMenu.add(part2Hint3MenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2UserRecursionCodeMenuItem);
        lessonMenu.add(part2UserIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2SolutionRecursionCodeMenuItem);
        lessonMenu.add(part2SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2SummaryMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2gotoPart1MenuItem);
        
        switch (part2ChosenCode) {
            case UserIteration:
                mainClass.getEditor().setCode(part2UserIterationCode);
                part2UserIterationCodeMenuItem.setSelected(true);
                break;
            case UserRecursion:
                mainClass.getEditor().setCode(part2UserRecursionCode);
                part2UserRecursionCodeMenuItem.setSelected(true);
                break;
            case SolutionIteration:
                mainClass.getEditor().setCode(part2SolutionIterationCode);
                part2SolutionIterationCodeMenuItem.setSelected(true);
                break;
            case SolutionRecursion:
                mainClass.getEditor().setCode(part2SolutionRecursionCode);
                part2SolutionRecursionCodeMenuItem.setSelected(true);
                break;
        }
        
        if ( state.Id <= State.Part2Shown.Id ) {
            state = State.Part2Shown;
            part1PseudocodeTabDisabledProperty.set(false);
        }
        part2TextFrame.setVisible(true);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part2GotoPart1">
    private void part2GotoPart1()
    {
        part2TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1UserRecursionCodeMenuItem);
        lessonMenu.add(part1UserIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1SolutionRecursionCodeMenuItem);
        lessonMenu.add(part1SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1gotoPart2MenuItem);
        
        switch (part1ChosenCode) {
            case UserIteration:
                mainClass.getEditor().setCode(part1UserIterationCode);
                part1UserIterationCodeMenuItem.setSelected(true);
                break;
            case UserRecursion:
                mainClass.getEditor().setCode(part1UserRecursionCode);
                part1UserRecursionCodeMenuItem.setSelected(true);
                break;
            case SolutionIteration:
                mainClass.getEditor().setCode(part1SolutionIterationCode);
                part1SolutionIterationCodeMenuItem.setSelected(true);
                break;
            case SolutionRecursion:
                mainClass.getEditor().setCode(part1SolutionRecursionCode);
                part1SolutionRecursionCodeMenuItem.setSelected(true);
                break;
        }
        
        part1TextFrame.setVisible(true);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="part2gotoHint, part2showHint">
    private void part2gotoHint(final int nr) {
        if (nr < 1 || nr > 3) {
            throw new IllegalArgumentException();
        }
        part2TextFrame.setVisible(true);
        part2TextFrame.toFront();
        part2TabSelectionModel.select(1);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                part2HintTabWebEngine.executeScript("gotoHint" + nr + "()");
            }
        });
    }
    
    private void part2showHint(final int nr) {
        if (nr < 2 || nr > 3) {
            throw new IllegalArgumentException();
        }
        part2HintTabDisabledProperty.set(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                part2HintTabWebEngine.executeScript("showHint" + nr + "()");
            }
        });
    }
    //</editor-fold>
    
    public void start() {
        
        initPart1TextFrame();
        
        initPart2TextFrame();
        
        //<editor-fold defaultstate="collapsed" desc="Init codes">
        InputStream stream;
        stream = getClass().getResourceAsStream("part1_solution_recursion_code.txt");
        part1SolutionRecursionCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part1_solution_iteration_code.txt");
        part1SolutionIterationCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_recursion_code.txt");
        part2SolutionRecursionCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_iteration_code.txt");
        part2SolutionIterationCode = ReadFileHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part1_user_recursion_code.txt");
        part1UserRecursionCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part1_user_iteration_code.txt");
        part1UserIterationCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_recursion_code.txt");
        part2UserRecursionCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_iteration_code.txt");
        part2UserIterationCode = ReadFileHelper.readFile(stream);
        
        oldCode = mainClass.getEditor().getCode();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part1 menuItems">
        part1TextMenuItem = new JMenuItem(Lang.part1TextMenuItem);
        part1TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part1TextFrame.setVisible(true);
                part1TextFrame.toFront();
                part1TabSelectionModel.select(0);
            }
        });
        part1PseudocodeMenuItem = new JMenuItem(Lang.part1PseudocodeMenuItem);
        part1PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state == State.NothingShown ) {
                    state = State.Pseudocode1Shown;
                    part1PseudocodeTabDisabledProperty.set(false);
                }
                part1TextFrame.setVisible(true);
                part1TextFrame.toFront();
                part1TabSelectionModel.select(1);
            }
        });
        
        part1UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserRecursionCodeMenuItem);
        part1UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( part1ChosenCode == ChosenCode.UserRecursion ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1UserRecursionCode);
                part1ChosenCode = ChosenCode.UserRecursion;
            }
        });
        
        part1UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserIterationCodeMenuItem);
        part1UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( part1ChosenCode == ChosenCode.UserIteration ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1UserIterationCode);
                part1ChosenCode = ChosenCode.UserIteration;
            }
        });
        
        part1SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionRecursionCodeMenuItem);
        part1SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part1ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part1ChosenCode == ChosenCode.SolutionRecursion) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1SolutionRecursionCode);
                part1ChosenCode = ChosenCode.SolutionRecursion;

                if (state.Id <= State.Pseudocode1Shown.Id) {
                    state = State.Recursion1Shown;
                } else if (state == State.Iteration1Shown) {
                    state = State.Both1Shown;
                }
            }
        });
        
        part1SolutionIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionIterationCodeMenuItem);
        part1SolutionIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part1ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if ( part1ChosenCode == ChosenCode.SolutionIteration ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1SolutionIterationCode);
                part1ChosenCode = ChosenCode.SolutionIteration;
                
                if (state.Id <= State.Pseudocode1Shown.Id) {
                    state = State.Iteration1Shown;
                } else if (state == State.Recursion1Shown) {
                    state = State.Both1Shown;
                }
            }
        });
        
        part1gotoPart2MenuItem = new JMenuItem(Lang.part1gotoPart2MenuItem);
        part1gotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part1GotoPart2Conifrm, State.Both1Shown) ) {
                    return;
                }
                MemorizeCode();
                part1GotoPart2();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1UserRecursionCodeMenuItem);
        group.add(part1UserIterationCodeMenuItem);
        group.add(part1SolutionRecursionCodeMenuItem);
        group.add(part1SolutionIterationCodeMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part2 menuItems">
        part2TextMenuItem = new JMenuItem(Lang.part2TextMenuItem);
        part2TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
                part2TabSelectionModel.select(0);
            }
        });
        
        part2Hint1MenuItem = new JMenuItem(Lang.part2Hint1MenuItem);
        part2Hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Hint1Shown.Id ) {
                    part2HintTabDisabledProperty.set(false);
                    state = State.Hint1Shown;
                }
                part2gotoHint(1);
            }
        });
        
        part2Hint2MenuItem = new JMenuItem(Lang.part2Hint2MenuItem);
        part2Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowHintConifrm, State.Hint1Shown) ) {
                    return;
                }
                if ( state.Id < State.Hint2Shown.Id ) {
                    part2showHint(2);
                    state = State.Hint2Shown;
                }
                part2gotoHint(2);
            }
        });
        
        part2Hint3MenuItem = new JMenuItem(Lang.part2Hint3MenuItem);
        part2Hint3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowHintConifrm, State.Hint1Shown) ) {
                    return;
                }
                if ( state.Id < State.Hint3Shown.Id ) {
                    part2showHint(3);
                    state = State.Hint3Shown;
                }
                part2gotoHint(3);
            }
        });
        
        part2UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserRecursionCodeMenuItem);
        part2UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == ChosenCode.UserRecursion ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserRecursionCode);
                part2ChosenCode = ChosenCode.UserRecursion;
            }
        });
        
        part2UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserIterationCodeMenuItem);
        part2UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == ChosenCode.UserIteration ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserIterationCode);
                part2ChosenCode = ChosenCode.UserIteration;
            }
        });
        
        part2SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionRecursionCodeMenuItem);
        part2SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part2ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part2ChosenCode == ChosenCode.SolutionRecursion) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionRecursionCode);
                part2ChosenCode = ChosenCode.SolutionRecursion;
                if (state.Id <= State.Hint3Shown.Id) {
                    state = State.Recursion2Shown;
                } else if (state == State.Iteration2Shown) {
                    state = State.Both2Shown;
                }
            }
        });
        
        part2SolutionIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionIterationCodeMenuItem);
        part2SolutionIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part2ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part2ChosenCode == ChosenCode.SolutionIteration) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionIterationCode);
                part2ChosenCode = ChosenCode.SolutionIteration;
                if (state.Id <= State.Hint3Shown.Id) {
                    state = State.Recursion2Shown;
                } else if (state == State.Recursion2Shown) {
                    state = State.Both2Shown;
                }
            }
        });
        
        part2SummaryMenuItem = new JMenuItem(Lang.part2SummaryMenuItem);
        part2SummaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowSummaryConifrm, State.Both2Shown) ) {
                    return;
                }
                if ( state.Id < State.Hint3Shown.Id ) {
                    part2showHint(3);
                }
                if ( state != State.SummaryShown ) {
                    state = State.SummaryShown;
                    part2SummaryTabDisabledProperty.set(false);
                }
                part2TabSelectionModel.select(2);
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
            }
        });
        
        part2gotoPart1MenuItem = new JMenuItem(Lang.part2gotoPart1MenuItem);
        part2gotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MemorizeCode();
                part2GotoPart1();
            }
        });
        
        group = new ButtonGroup();
        group.add(part2UserRecursionCodeMenuItem);
        group.add(part2UserIterationCodeMenuItem);
        group.add(part2SolutionRecursionCodeMenuItem);
        group.add(part2SolutionIterationCodeMenuItem);
        //</editor-fold>
        
        mainClass.getLessonMenu().setEnabled(true);
        part2GotoPart1();
        
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        
    }
    
    @Override
    public void threadStop() {
    }
    
    @Override
    public boolean pauseStart(SyntaxNode node, final int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(SyntaxNode node) {
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String part1TextFrameTitle = "Potęgowanie";
        public static final String part2TextFrameTitle = "Szybkie obliczanie potęgi";
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String part2TextTabName = "Treść zadania";
        public static final String part2HintTabName = "Wskazówki";
        public static final String part2SummaryTabName = "Podsumowanie";
        
        public static final String part1TextMenuItem = "Treść zadania";
        public static final String part1PseudocodeMenuItem = "Wskazówaka: pseudokod";
        public static final String part1UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part1UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part1SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part1SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part1gotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2Hint1MenuItem = "Wskazówka I";
        public static final String part2Hint2MenuItem = "Wskazówka II";
        public static final String part2Hint3MenuItem = "Wskazówka III";
        public static final String part2UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part2UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part2SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part2SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part2SolutionConsoleCodeMenuItem = "Rozpisanie obliczanych potęg";
        public static final String part2SummaryMenuItem = "Podsumowanie";
        public static final String part2gotoPart1MenuItem = "<<< Część I";
        
        public static final String part1ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to dostępna jest wskazówka w postaci pseudokodów.";
        
        public static final String part2ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wskazówki.";
        
        public static final String part1GotoPart2Conifrm = 
                "Nie porównałeś jeszcze swoich rozwiązań z rozwiązaniami wzorcowymi.\n"
                + "Czy na pewno chcesz przejść do części II?";
        
        public static final String part2ShowHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
        public static final String part2ShowSummaryConifrm = 
            "Nie porównałeś jeszcze swoich rozwiązań z rozwiązaniami wzorcowymi.\n"
            + "Czy na pewno chcesz wyświetlić podsumowanie";
        
        public static final String showPseudocode = "Pokaż pseudokod";
        public static final String showHint = "Pokaż wskazówkę";
        public static final String showSolution = "Pokaż rozwiązanie";
        public static final String cancel = "Anuluj";
        
        public static final String yes = "Tak";
        public static final String no = "Nie";
        
        public static final String question = "Pytanie";
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
        
    }
    //</editor-fold>
    
}

