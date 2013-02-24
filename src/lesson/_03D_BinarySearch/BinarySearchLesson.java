package lesson._03D_BinarySearch;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import helpers.LessonHelper;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import syntax.SyntaxNode;
//</editor-fold>

public class BinarySearchLesson implements Lesson {

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        NothingShown(0), Part1PseudocodeShown(1),
        Part1SolutionShown(2), Part2Shown(3),
        Part2Hint1Shown(4), Part2Hint2Shown(5), Part2Hint3Shown(6),
        Part2RecursionSolutionShown(7), Part2IterationSolutionShown(8), 
        Part2BothSolutionShown(9), SummaryShown(10);
        
        public final byte Id;
        State(int id) {
            Id = (byte) id;
        }
    }

    private static enum Part1ChosenCode {
        User(0),Solution(1);
        
        public final byte Id;
        Part1ChosenCode(int id) {
            Id = (byte) id;
        }
    }

    private static enum Part2ChosenCode {
        UserRecursion(0), UserIteration(1),
        SolutionRecursion(2), SolutionIteration(3);
        
        public final byte Id;
        Part2ChosenCode(int id) {
            Id = (byte) id;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private Part1ChosenCode part1ChosenCode = Part1ChosenCode.User;
    private Part2ChosenCode part2ChosenCode = Part2ChosenCode.UserRecursion;
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
    private JRadioButtonMenuItem part1UserCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionCodeMenuItem;
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
    private String part1UserCode;
    private String part1SolutionCode;
    private String part2UserRecursionCode;
    private String part2UserIterationCode;
    private String part2SolutionRecursionCode;
    private String part2SolutionIterationCode;
    
    public BinarySearchLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    private void MemorizeCode() {
        if (chosenPart == 0) {
            if (part1ChosenCode == Part1ChosenCode.User) {
                part1UserCode = mainClass.getEditor().getCode();
            }
        } else {
            if (part2ChosenCode == Part2ChosenCode.UserRecursion) {
                part2UserRecursionCode = mainClass.getEditor().getCode();
            } else if (part2ChosenCode == Part2ChosenCode.UserIteration) {
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
            part1UserCodeMenuItem.setSelected(true);
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
        if (state.Id >= State.Part2Hint2Shown.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part2ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            if (part2ChosenCode == Part2ChosenCode.UserIteration) {
                part2UserIterationCodeMenuItem.setSelected(true);
            } else {
                part2UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                if (state == State.Part2Hint1Shown) {
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
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1() {
        part2TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1UserCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1gotoPart2MenuItem);

        switch (part1ChosenCode) {
            case User:
                mainClass.getEditor().setCode(part1UserCode);
                part1UserCodeMenuItem.setSelected(true);
                break;
            case Solution:
                mainClass.getEditor().setCode(part1SolutionCode);
                part1SolutionCodeMenuItem.setSelected(true);
                break;
        }

        part1TextFrame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        part1TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2Hint1MenuItem);
        lessonMenu.add(part2Hint2MenuItem);
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

        if (state.Id <= State.Part2Shown.Id) {
            state = State.Part2Shown;
            part1PseudocodeTabDisabledProperty.set(false);
        }
        part2TextFrame.setVisible(true);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="part2gotoHint, part2showHint">
    private void part2gotoHint(final int nr) {
        assert 1 <= nr && nr <= 3;
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
        assert nr == 2 || nr == 3;
        part2HintTabDisabledProperty.set(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                part2HintTabWebEngine.executeScript("showHint"+nr+"()");
            }
        });
    }
    //</editor-fold>
    
    public void start() {
        
        initPart1TextFrame();
        
        initPart2TextFrame();
        
        //<editor-fold defaultstate="collapsed" desc="Init codes">
        InputStream stream;
        stream = getClass().getResourceAsStream("part1_solution_code.txt");
        part1SolutionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_recursion_code.txt");
        part2SolutionRecursionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_iteration_code.txt");
        part2SolutionIterationCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part1_user_code.txt");
        part1UserCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_recursion_code.txt");
        part2UserRecursionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_iteration_code.txt");
        part2UserIterationCode = LessonHelper.readFile(stream);
        
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
                    state = State.Part1PseudocodeShown;
                    part1PseudocodeTabDisabledProperty.set(false);
                }
                part1TextFrame.setVisible(true);
                part1TextFrame.toFront();
                part1TabSelectionModel.select(1);
            }
        });
        
        part1UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserCodeMenuItem);
        part1UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( part1ChosenCode == Part1ChosenCode.User ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1UserCode);
                part1ChosenCode = Part1ChosenCode.User;
            }
        });
        
        part1SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionCodeMenuItem);
        part1SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part1ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part1ChosenCode == Part1ChosenCode.Solution) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part1SolutionCode);
                part1ChosenCode = Part1ChosenCode.Solution;

                if (state.Id < State.Part1SolutionShown.Id) {
                    state = State.Part1SolutionShown;
                }
            }
        });
        
        part1gotoPart2MenuItem = new JMenuItem(Lang.part1gotoPart2MenuItem);
        part1gotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part1GotoPart2Conifrm, State.Part1SolutionShown) ) {
                    return;
                }
                MemorizeCode();
                initPart2();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1UserCodeMenuItem);
        group.add(part1SolutionCodeMenuItem);
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
                if ( state.Id < State.Part2Hint1Shown.Id ) {
                    part2HintTabDisabledProperty.set(false);
                    state = State.Part2Hint1Shown;
                }
                part2gotoHint(1);
            }
        });
        
        part2Hint2MenuItem = new JMenuItem(Lang.part2Hint2MenuItem);
        part2Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowHintConifrm, State.Part2Hint1Shown) ) {
                    return;
                }
                if ( state.Id < State.Part2Hint2Shown.Id ) {
                    part2showHint(2);
                    state = State.Part2Hint2Shown;
                }
                part2gotoHint(2);
            }
        });
        
        part2Hint3MenuItem = new JMenuItem(Lang.part2Hint3MenuItem);
        part2Hint3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowHintConifrm, State.Part2Hint2Shown) ) {
                    return;
                }
                if ( state.Id < State.Part2Hint3Shown.Id ) {
                    part2showHint(3);
                    state = State.Part2Hint3Shown;
                }
                part2gotoHint(3);
            }
        });
        
        part2UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserRecursionCodeMenuItem);
        part2UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.UserRecursion ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserRecursionCode);
                part2ChosenCode = Part2ChosenCode.UserRecursion;
            }
        });
        
        part2UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserIterationCodeMenuItem);
        part2UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.UserIteration ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserIterationCode);
                part2ChosenCode = Part2ChosenCode.UserIteration;
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
                if (part2ChosenCode == Part2ChosenCode.SolutionRecursion) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionRecursionCode);
                part2ChosenCode = Part2ChosenCode.SolutionRecursion;
                if (state.Id <= State.Part2Hint3Shown.Id) {
                    state = State.Part2RecursionSolutionShown;
                } else if (state == State.Part2IterationSolutionShown) {
                    state = State.Part2BothSolutionShown;
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
                if (part2ChosenCode == Part2ChosenCode.SolutionIteration) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionIterationCode);
                part2ChosenCode = Part2ChosenCode.SolutionIteration;
                if (state.Id <= State.Part2Hint3Shown.Id) {
                    state = State.Part2RecursionSolutionShown;
                } else if (state == State.Part2RecursionSolutionShown) {
                    state = State.Part2BothSolutionShown;
                }
            }
        });
        
        part2SummaryMenuItem = new JMenuItem(Lang.part2SummaryMenuItem);
        part2SummaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part2ShowSummaryConifrm, State.Part2BothSolutionShown) ) {
                    return;
                }
                if ( state.Id < State.Part2Hint3Shown.Id ) {
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
                initPart1();
            }
        });
        
        group = new ButtonGroup();
        group.add(part2UserRecursionCodeMenuItem);
        group.add(part2UserIterationCodeMenuItem);
        group.add(part2SolutionRecursionCodeMenuItem);
        group.add(part2SolutionIterationCodeMenuItem);
        //</editor-fold>
        
        mainClass.getLessonMenu().setEnabled(true);
        initPart1();
        
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
        public static final String part1UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part1SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
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

