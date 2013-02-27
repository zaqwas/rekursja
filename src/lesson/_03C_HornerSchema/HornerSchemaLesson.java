package lesson._03C_HornerSchema;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import helpers.LessonHelper;
import interpreter.Instance;
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

public class HornerSchemaLesson implements Lesson {

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
        Part1SolutionShown(4), Part2Shown(6),
        Part2PseudocodeShown(1), Part3Shown(6),
        Part3Hint1Shown(7), Part3Hint2Shown(8), Part3Hint3Shown(8),
        Part3RecursionSolutionShown(10), Part3IterationSolutionShown(11),
        Part3BothSolutionShown(12), SummaryShown(13);
        
        public final byte Id;
        State(int id) {
            Id = (byte) id;
        }
    }

    private static enum Part1ChosenCode {
        User(0), Solution(1);
        
        public final byte Id;
        Part1ChosenCode(int id) {
            Id = (byte) id;
        }
    }

    private static enum Part2ChosenCode {
        UserLeftToRight(0), UserRightToLeft(1),
        SolutionLeftToRight(2), SolutionRightToLeft(3);
        
        public final byte Id;
        Part2ChosenCode(int id) {
            Id = (byte) id;
        }
    }

    private static enum Part3ChosenCode {

        UserRecursion(0),
        UserIteration(1),
        SolutionRecursion(2),
        SolutionIteration(3);
        public final byte Id;

        Part3ChosenCode(int id) {
            Id = (byte) id;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private Part1ChosenCode part1ChosenCode = Part1ChosenCode.User;
    private Part2ChosenCode part2ChosenCode = Part2ChosenCode.UserLeftToRight;
    private Part3ChosenCode part3ChosenCode = Part3ChosenCode.UserRecursion;
    private byte chosenPart;
    
    
    private MainClass mainClass;
    
    private JInternalFrame part1TextFrame;
    private SelectionModel<Tab> part1TabSelectionModel;
    private BooleanProperty part1PseudocodeTabDisabledProperty;
    
    private JInternalFrame part2TextFrame;
    private SelectionModel<Tab> part2TabSelectionModel;
    private BooleanProperty part2PseudocodeTabDisabledProperty;
    
    private JInternalFrame part3TextFrame;
    private SelectionModel<Tab> part3TabSelectionModel;
    private BooleanProperty part3HintTabDisabledProperty;
    private BooleanProperty part3SummaryTabDisabledProperty;
    private WebEngine part3HintTabWebEngine;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionCodeMenuItem;
    private JMenuItem part1gotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2PseudocodeMenuItem;
    private JRadioButtonMenuItem part2UserLeftToRightCodeMenuItem;
    private JRadioButtonMenuItem part2UserRightToLeftCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionLeftToRightCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionRightToLeftCodeMenuItem;
    private JMenuItem part2gotoPart1MenuItem;
    private JMenuItem part2gotoPart3MenuItem;
    
    private JMenuItem part3TextMenuItem;
    private JMenuItem part3Hint1MenuItem;
    private JMenuItem part3Hint2MenuItem;
    private JMenuItem part3Hint3MenuItem;
    private JRadioButtonMenuItem part3UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part3UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part3SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part3SolutionIterationCodeMenuItem;
    private JMenuItem part3SummaryMenuItem;
    private JMenuItem part3gotoPart2MenuItem;
    
    private String oldCode;
    private String part1UserCode;
    private String part1SolutionCode;
    private String part2UserLeftToRightCode;
    private String part2UserRightToLeftCode;
    private String part2SolutionLeftToRightCode;
    private String part2SolutionRightToLeftCode;
    private String part3UserRecursionCode;
    private String part3UserIterationCode;
    private String part3SolutionRecursionCode;
    private String part3SolutionIterationCode;
    
    public HornerSchemaLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    //<editor-fold defaultstate="collapsed" desc="MemorizeCode">
    private void MemorizeCode() {
        if (chosenPart == 0) {
            if (part1ChosenCode == Part1ChosenCode.User) {
                part1UserCode = mainClass.getEditor().getCode();
            }
        } if (chosenPart == 1) {
            if (part2ChosenCode == Part2ChosenCode.UserLeftToRight) {
                part2UserLeftToRightCode = mainClass.getEditor().getCode();
            } else if (part2ChosenCode == Part2ChosenCode.UserRightToLeft) {
                part2UserRightToLeftCode = mainClass.getEditor().getCode();
            }
        } else {
            if (part3ChosenCode == Part3ChosenCode.UserRecursion) {
                part3UserRecursionCode = mainClass.getEditor().getCode();
            } else if (part3ChosenCode == Part3ChosenCode.UserIteration) {
                part3UserIterationCode = mainClass.getEditor().getCode();
            }
        }
    }
    //</editor-fold>
    
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
    
    //<editor-fold defaultstate="collapsed" desc="part3ShowSolutionConifrm">
    private boolean part3ShowSolutionConifrm() {
        if (state.Id >= State.Part3Hint3Shown.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part3ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            if (part3ChosenCode == Part3ChosenCode.UserIteration) {
                part3UserIterationCodeMenuItem.setSelected(true);
            } else {
                part3UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                if (state == State.Part3Hint1Shown) {
                    part3Hint2MenuItem.doClick();
                } else if (state == State.Part3Hint2Shown) {
                    part3Hint3MenuItem.doClick();
                } else {
                    part3Hint1MenuItem.doClick();
                }
            }
            return false;
        }
        part3showHint(3);
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

                tab = new Tab(Lang.part2PseudocodeTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part2_pseudocode.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part2PseudocodeTabDisabledProperty = tab.disableProperty();
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
    
    //<editor-fold defaultstate="collapsed" desc="initPart3TextFrame">
    private void initPart3TextFrame()
    {
        part3TextFrame = new JInternalFrame(Lang.part3TextFrameTitle);
        final JFXPanel fxPanel = new JFXPanel();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                tabPane.tabMinHeightProperty().set(0d);
                part3TabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.part3TextTabName);
                WebView web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part3_text.html").toString());
                tab.setContent(web);
                tabPane.getTabs().add(tab);

                tab = new Tab(Lang.part3HintTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part3_hint.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part3HintTabWebEngine = web.getEngine();
                part3HintTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.part3SummaryTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("part3_summary.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                part3SummaryTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                Scene scene = new Scene(tabPane);
                fxPanel.setScene(scene);
            }
        });
        
        part3TextFrame.setContentPane(fxPanel);
        part3TextFrame.setPreferredSize(new Dimension(700, 500));
        part3TextFrame.setResizable(true);
        part3TextFrame.setVisible(false);
        mainClass.getDesktop().add(part3TextFrame);
        part3TextFrame.pack();
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1()
    {
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
    private void initPart2()
    {
        part1TextFrame.setVisible(false);
        part3TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2UserLeftToRightCodeMenuItem);
        lessonMenu.add(part2UserRightToLeftCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2SolutionLeftToRightCodeMenuItem);
        lessonMenu.add(part2SolutionRightToLeftCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2gotoPart1MenuItem);
        lessonMenu.add(part2gotoPart3MenuItem);
        
        switch (part2ChosenCode) {
            case UserLeftToRight:
                mainClass.getEditor().setCode(part2UserLeftToRightCode);
                part2UserLeftToRightCodeMenuItem.setSelected(true);
                break;
            case UserRightToLeft:
                mainClass.getEditor().setCode(part2UserRightToLeftCode);
                part2UserRightToLeftCodeMenuItem.setSelected(true);
                break;
            case SolutionLeftToRight:
                mainClass.getEditor().setCode(part2SolutionLeftToRightCode);
                part2SolutionLeftToRightCodeMenuItem.setSelected(true);
                break;
            case SolutionRightToLeft:
                mainClass.getEditor().setCode(part2SolutionRightToLeftCode);
                part2SolutionRightToLeftCodeMenuItem.setSelected(true);
                break;
        }
        
        if ( state.Id < State.Part2Shown.Id ) {
            state = State.Part2Shown;
            part1PseudocodeTabDisabledProperty.set(false);
        }
        part2TextFrame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart3">
    private void initPart3()
    {
        part2TextFrame.setVisible(false);
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part3TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3Hint1MenuItem);
        lessonMenu.add(part3Hint2MenuItem);
        lessonMenu.add(part3Hint3MenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3UserRecursionCodeMenuItem);
        lessonMenu.add(part3UserIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3SolutionRecursionCodeMenuItem);
        lessonMenu.add(part3SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3SummaryMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3gotoPart2MenuItem);
        
        switch (part3ChosenCode) {
            case UserIteration:
                mainClass.getEditor().setCode(part3UserIterationCode);
                part3UserIterationCodeMenuItem.setSelected(true);
                break;
            case UserRecursion:
                mainClass.getEditor().setCode(part3UserRecursionCode);
                part3UserRecursionCodeMenuItem.setSelected(true);
                break;
            case SolutionIteration:
                mainClass.getEditor().setCode(part3SolutionIterationCode);
                part3SolutionIterationCodeMenuItem.setSelected(true);
                break;
            case SolutionRecursion:
                mainClass.getEditor().setCode(part3SolutionRecursionCode);
                part3SolutionRecursionCodeMenuItem.setSelected(true);
                break;
        }
        
        if ( state.Id < State.Part3Shown.Id ) {
            state = State.Part3Shown;
            part1PseudocodeTabDisabledProperty.set(false);
            part2PseudocodeTabDisabledProperty.set(false);
        }
        part3TextFrame.setVisible(true);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="part3gotoHint, part3showHint">
    private void part3gotoHint(final int nr) {
        if (nr < 1 || nr > 3) {
            throw new IllegalArgumentException();
        }
        part3TextFrame.setVisible(true);
        part3TextFrame.toFront();
        part3TabSelectionModel.select(1);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                part3HintTabWebEngine.executeScript("gotoHint" + nr + "()");
            }
        });
    }
    
    private void part3showHint(final int nr) {
        part3HintTabDisabledProperty.set(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                part3HintTabWebEngine.executeScript("showHint" + nr + "()");
            }
        });
    }
    //</editor-fold>
    
    public void start() {
        
        initPart1TextFrame();
        
        initPart2TextFrame();
        
        initPart3TextFrame();
        
        //<editor-fold defaultstate="collapsed" desc="Init codes">
        InputStream stream;
        stream = getClass().getResourceAsStream("part1_solution_code.txt");
        part1SolutionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_left_to_right_code.txt");
        part2SolutionLeftToRightCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_solution_right_to_left_code.txt");
        part2SolutionRightToLeftCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part3_solution_recursion_code.txt");
        part3SolutionRecursionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part3_solution_iteration_code.txt");
        part3SolutionIterationCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part1_user_code.txt");
        part1UserCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_left_to_right_code.txt");
        part2UserLeftToRightCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part2_user_right_to_left_code.txt");
        part2UserRightToLeftCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part3_user_recursion_code.txt");
        part3UserRecursionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part3_user_iteration_code.txt");
        part3UserIterationCode = LessonHelper.readFile(stream);
        
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
        
        part2PseudocodeMenuItem = new JMenuItem(Lang.part2PseudocodeMenuItem);
        part2PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    part2PseudocodeTabDisabledProperty.set(false);
                    state = State.Part2PseudocodeShown;
                }
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
                part2TabSelectionModel.select(1);
            }
        });
        
        part2UserLeftToRightCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserLeftToRightCodeMenuItem);
        part2UserLeftToRightCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.UserLeftToRight ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserLeftToRightCode);
                part2ChosenCode = Part2ChosenCode.UserLeftToRight;
            }
        });
        
        part2UserRightToLeftCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserRightToLeftCodeMenuItem);
        part2UserRightToLeftCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.UserRightToLeft ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2UserRightToLeftCode);
                part2ChosenCode = Part2ChosenCode.UserRightToLeft;
            }
        });
        
        part2SolutionLeftToRightCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionLeftToRightCodeMenuItem);
        part2SolutionLeftToRightCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.SolutionLeftToRight ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionLeftToRightCode);
                part2ChosenCode = Part2ChosenCode.SolutionLeftToRight;
            }
        });
        
        part2SolutionRightToLeftCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionRightToLeftCodeMenuItem);
        part2SolutionRightToLeftCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part2ChosenCode == Part2ChosenCode.SolutionRightToLeft ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part2SolutionRightToLeftCode);
                part2ChosenCode = Part2ChosenCode.SolutionRightToLeft;
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
        
        part2gotoPart3MenuItem = new JMenuItem(Lang.part2gotoPart3MenuItem);
        part2gotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MemorizeCode();
                initPart3();
                if ( state.Id < State.Part3Shown.Id ) {
                    state = State.Part3Shown;
                }
            }
        });
        
        
        group = new ButtonGroup();
        group.add(part2UserLeftToRightCodeMenuItem);
        group.add(part2UserRightToLeftCodeMenuItem);
        group.add(part2SolutionLeftToRightCodeMenuItem);
        group.add(part2SolutionRightToLeftCodeMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part3 menuItems">
        part3TextMenuItem = new JMenuItem(Lang.part3TextMenuItem);
        part3TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part3TextFrame.setVisible(true);
                part3TextFrame.toFront();
                part3TabSelectionModel.select(0);
            }
        });
        
        part3Hint1MenuItem = new JMenuItem(Lang.part3Hint1MenuItem);
        part3Hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part3Hint1Shown.Id ) {
                    part3HintTabDisabledProperty.set(false);
                    state = State.Part3Hint1Shown;
                }
                part3gotoHint(1);
            }
        });
        
        part3Hint2MenuItem = new JMenuItem(Lang.part3Hint2MenuItem);
        part3Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part3ShowHintConifrm, State.Part3Hint1Shown) ) {
                    return;
                }
                if ( state.Id < State.Part3Hint2Shown.Id ) {
                    part3showHint(2);
                    state = State.Part3Hint2Shown;
                }
                part3gotoHint(2);
            }
        });
        
        part3Hint3MenuItem = new JMenuItem(Lang.part3Hint3MenuItem);
        part3Hint3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part3ShowHintConifrm, State.Part3Hint2Shown) ) {
                    return;
                }
                if ( state.Id < State.Part3Hint3Shown.Id ) {
                    part3showHint(3);
                    state = State.Part3Hint3Shown;
                }
                part3gotoHint(3);
            }
        });
        
        part3UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part3UserRecursionCodeMenuItem);
        part3UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part3ChosenCode == Part3ChosenCode.UserRecursion ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part3UserRecursionCode);
                part3ChosenCode = Part3ChosenCode.UserRecursion;
            }
        });
        
        part3UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part3UserIterationCodeMenuItem);
        part3UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( part3ChosenCode == Part3ChosenCode.UserIteration ) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part3UserIterationCode);
                part3ChosenCode = Part3ChosenCode.UserIteration;
            }
        });
        
        part3SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part3SolutionRecursionCodeMenuItem);
        part3SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part3ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part3ChosenCode == Part3ChosenCode.SolutionRecursion) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part3SolutionRecursionCode);
                part3ChosenCode = Part3ChosenCode.SolutionRecursion;
                if (state.Id <= State.Part3Hint3Shown.Id) {
                    state = State.Part3RecursionSolutionShown;
                } else if (state == State.Part3IterationSolutionShown) {
                    state = State.Part3BothSolutionShown;
                }
            }
        });
        
        part3SolutionIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part3SolutionIterationCodeMenuItem);
        part3SolutionIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !part3ShowSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (part3ChosenCode == Part3ChosenCode.SolutionIteration) {
                    return;
                }
                MemorizeCode();
                mainClass.getEditor().setCode(part3SolutionIterationCode);
                part3ChosenCode = Part3ChosenCode.SolutionIteration;
                if (state.Id <= State.Part3Hint3Shown.Id) {
                    state = State.Part3RecursionSolutionShown;
                } else if (state == State.Part3RecursionSolutionShown) {
                    state = State.Part3BothSolutionShown;
                }
            }
        });
        
        part3SummaryMenuItem = new JMenuItem(Lang.part3SummaryMenuItem);
        part3SummaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showConifrmDialog(Lang.part3ShowSummaryConifrm, State.Part3BothSolutionShown) ) {
                    return;
                }
                if ( state.Id < State.Part3Hint3Shown.Id ) {
                    part3showHint(3);
                }
                if ( state != State.SummaryShown ) {
                    state = State.SummaryShown;
                    part3SummaryTabDisabledProperty.set(false);
                }
                part3TabSelectionModel.select(2);
                part3TextFrame.setVisible(true);
                part3TextFrame.toFront();
            }
        });
        
        part3gotoPart2MenuItem = new JMenuItem(Lang.part3gotoPart2MenuItem);
        part3gotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MemorizeCode();
                initPart2();
            }
        });
        
        group = new ButtonGroup();
        group.add(part3UserRecursionCodeMenuItem);
        group.add(part3UserIterationCodeMenuItem);
        group.add(part3SolutionRecursionCodeMenuItem);
        group.add(part3SolutionIterationCodeMenuItem);
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
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String part1TextFrameTitle = "Obliczanie wartości wielomianu";
        public static final String part2TextFrameTitle = "Obliczanie wartości wielomianu";
        public static final String part3TextFrameTitle = "Schemat Hornera";
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String part2TextTabName = "Treść zadania";
        public static final String part2PseudocodeTabName = "Pseudokod";
        
        public static final String part3TextTabName = "Treść zadania";
        public static final String part3HintTabName = "Wskazówki";
        public static final String part3SummaryTabName = "Podsumowanie";
        
        public static final String part1TextMenuItem = "Treść zadania";
        public static final String part1PseudocodeMenuItem = "Wskazówaka: pseudokod";
        public static final String part1UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part1SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part1gotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2PseudocodeMenuItem = "Wskazówaka: pseudokod";
        public static final String part2UserLeftToRightCodeMenuItem = "Rozwiązanie użytkownika LR";
        public static final String part2UserRightToLeftCodeMenuItem = "Rozwiązanie użytkownika RL";
        public static final String part2SolutionLeftToRightCodeMenuItem = "Rozwiązanie wzorcowe LR";
        public static final String part2SolutionRightToLeftCodeMenuItem = "Rozwiązanie wzorcowe RL";
        public static final String part2gotoPart1MenuItem = "<<< Część I";
        public static final String part2gotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania";
        public static final String part3Hint1MenuItem = "Wskazówka I";
        public static final String part3Hint2MenuItem = "Wskazówka II";
        public static final String part3Hint3MenuItem = "Wskazówka III";
        public static final String part3UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part3UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part3SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part3SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part3SolutionConsoleCodeMenuItem = "Rozpisanie obliczanych potęg";
        public static final String part3SummaryMenuItem = "Podsumowanie";
        public static final String part3gotoPart2MenuItem = "<<< Część II";
        
        public static final String part1ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to dostępna jest wskazówka w postaci pseudokodów.";
        
        public static final String part1GotoPart2Conifrm = 
                "Nie porównałeś jeszcze swoich rozwiązań z rozwiązaniami wzorcowymi.\n"
                + "Czy na pewno chcesz przejść do części II?";
        
        public static final String part3ShowHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
        public static final String part3ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wskazówki.";

        public static final String part3ShowSummaryConifrm = 
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

