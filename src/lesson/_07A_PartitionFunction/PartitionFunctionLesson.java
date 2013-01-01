package lesson._07A_PartitionFunction;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._06A_MergeFunction.*;
import lesson._02B_ArithmeticSeries.*;
import helpers.ReadFileHelper;
import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import mainclass.MainClass;
import parser.SpecialFunctions;
import syntax.SyntaxNode;
//</editor-fold>

public class PartitionFunctionLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        NothingShown(0), 
        Part1PseudocodeShown(1), Part1SolutionShown(2), 
        Part2Shown(3),
        Part2Hint1Shown(4), Part2Hint2Shown(5),
        Part2PseudocodeShown(6), Part2SolutionShown(7), 
        Part3Shown(8),
        Part3HintShown(9), Part3PseudocodeShown(10), Part3SolutionShown(11), 
        SummaryShown(12);
        public final byte Id;

        State(int id) {
            Id = (byte) id;
        }
    }
    
    private static enum ChosenCode {
        User(0), Solution(1);
        public final byte Id;

        ChosenCode(int id) {
            Id = (byte) id;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private ChosenCode part1ChosenCode = ChosenCode.User;
    private ChosenCode part2ChosenCode = ChosenCode.User;
    private ChosenCode part3ChosenCode = ChosenCode.User;
    private int partSelected = 1;
    
    private MainClass mainClass;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionCodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2Hint1MenuItem;
    private JMenuItem part2Hint2MenuItem;
    private JMenuItem part2PseudocodeMenuItem;
    private JRadioButtonMenuItem part2UserCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionCodeMenuItem;
    private JMenuItem part2GotoPart1MenuItem;
    private JMenuItem part2GotoPart3MenuItem;
    
    private JMenuItem part3TextMenuItem;
    private JMenuItem part3HintMenuItem;
    private JMenuItem part3PseudocodeMenuItem;
    private JRadioButtonMenuItem part3UserCodeMenuItem;
    private JRadioButtonMenuItem part3SolutionCodeMenuItem;
    private JMenuItem part3GotoPart2MenuItem;
    private JMenuItem part3SummaryMenuItem;
    
    private String oldCode;
    private String part1UserCode;
    private String part1SolutionCode;
    private String part2UserCode;
    private String part2SolutionCode;
    private String part3UserCode;
    private String part3SolutionCode;
    
    public PartitionFunctionLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    //<editor-fold defaultstate="collapsed" desc="showNextHint">
    private void showNextHint() {
        if (partSelected == 1) {
            part1PseudocodeMenuItem.doClick();
        } else if (partSelected == 2) {
            if (state.Id < State.Part2Hint1Shown.Id) {
                part2Hint1MenuItem.doClick();
            } else if (state == State.Part2Hint1Shown) {
                part2Hint2MenuItem.doClick();
            } else {
                part2PseudocodeMenuItem.doClick();
            }
        } else {
            if (state.Id < State.Part3HintShown.Id) {
                part3HintMenuItem.doClick();
            } else {
                part3PseudocodeMenuItem.doClick();
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showSolutionConifrm">
    private boolean showSolutionConifrm(State state, JRadioButtonMenuItem menuItem, int showHintNr) {
        if (state.Id >= state.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.showSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            menuItem.setSelected(true);
            showNextHint();
            return false;
        }
        textFrame.showHint(showHintNr);
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

    
    //<editor-fold defaultstate="collapsed" desc="initCodes">
    private void initCodes(boolean onlySolutionCodes) {
        InputStream stream;
        stream = getClass().getResourceAsStream("part1_solution_code.txt");
        part1SolutionCode = ReadFileHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part2_solution_code.txt");
        part2SolutionCode = ReadFileHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part3_solution_code.txt");
        part3SolutionCode = ReadFileHelper.readFile(stream);

        if (!onlySolutionCodes) {
            stream = getClass().getResourceAsStream("part1_user_code.txt");
            part1UserCode = ReadFileHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part2_user_code.txt");
            part2UserCode = ReadFileHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part3_user_code.txt");
            part3UserCode = ReadFileHelper.readFile(stream);
        }
        oldCode = mainClass.getEditor().getCode();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart1MenuItems">
    private void initPart1MenuItems() {
        part1TextMenuItem = new JMenuItem(Lang.part1TextMenuItem);
        part1TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        
        part1PseudocodeMenuItem = new JMenuItem(Lang.part1PseudocodeMenuItem);
        part1PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part1PseudocodeShown.Id ) {
                    state = State.Part1PseudocodeShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part1UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserCodeMenuItem);
        part1UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO editor to front
                if ( part1ChosenCode == ChosenCode.User ) {
                    return;
                }
                mainClass.getEditor().setCode(part1UserCode);
                part1ChosenCode = ChosenCode.User;
            }
        });
        
        part1SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionCodeMenuItem);
        part1SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showSolutionConifrm(State.Part1PseudocodeShown, part1UserCodeMenuItem, 1)) {
                    return;
                }
                //TODO Editor to front
                if (part1ChosenCode == ChosenCode.Solution) {
                    return;
                }
                part1UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part1SolutionCode);
                part1ChosenCode = ChosenCode.Solution;
                if ( state.Id < State.Part1SolutionShown.Id ) {
                    state = State.Part1SolutionShown;
                }
            }
        });
        
        part1GotoPart2MenuItem = new JMenuItem(Lang.part1GotoPart2MenuItem);
        part1GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showNextPartConifrm, State.Part1SolutionShown)) {
                    return;
                }
                textFrame.showPart(2);
                if (state.Id < State.SummaryShown.Id) {
                    state = State.SummaryShown;
                    textFrame.gotoText();
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1UserCodeMenuItem);
        group.add(part1SolutionCodeMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2MenuItems">
    private void initPart2MenuItems() {
        part2TextMenuItem = new JMenuItem(Lang.part2TextMenuItem);
        part2TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        
        part2Hint1MenuItem = new JMenuItem(Lang.part2Hint1MenuItem);
        part2Hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part2Hint2MenuItem = new JMenuItem(Lang.part2Hint2MenuItem);
        part2Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part2PseudocodeMenuItem = new JMenuItem(Lang.part2PseudocodeMenuItem);
        part2PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(3);
            }
        });
        
        part2UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserCodeMenuItem);
        part2UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO editor to front
                if ( part2ChosenCode == ChosenCode.User ) {
                    return;
                }
                mainClass.getEditor().setCode(part2UserCode);
                part2ChosenCode = ChosenCode.User;
            }
        });
        
        part2SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionCodeMenuItem);
        part2SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showSolutionConifrm(State.Part2PseudocodeShown, part2UserCodeMenuItem, 3)) {
                    return;
                }
                //TODO editor to front
                if (part2ChosenCode == ChosenCode.Solution) {
                    return;
                }
                part2UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part2SolutionCode);
                part2ChosenCode = ChosenCode.Solution;
                if ( state.Id < State.Part2SolutionShown.Id ) {
                    state = State.Part2SolutionShown;
                }
            }
        });
        
        part2GotoPart1MenuItem = new JMenuItem(Lang.part2GotoPart1MenuItem);
        part2GotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.showPart(1);
            }
        });
        
        part2GotoPart3MenuItem = new JMenuItem(Lang.part2GotoPart3MenuItem);
        part2GotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showNextPartConifrm, State.Part2SolutionShown)) {
                    return;
                }
                textFrame.showPart(2);
                if (state.Id < State.Part3Shown.Id) {
                    textFrame.gotoText();
                    state = State.Part3Shown;
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part2UserCodeMenuItem);
        group.add(part2SolutionCodeMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart3MenuItems">
    private void initPart3MenuItems() {
        part3TextMenuItem = new JMenuItem(Lang.part3TextMenuItem);
        part3TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        
        part3HintMenuItem = new JMenuItem(Lang.part3HintMenuItem);
        part3HintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part3PseudocodeMenuItem = new JMenuItem(Lang.part3PseudocodeMenuItem);
        part3PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2PseudocodeShown.Id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(3);
            }
        });
        
        part3UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part3UserCodeMenuItem);
        part3UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO editor to front
                if ( part3ChosenCode == ChosenCode.User ) {
                    return;
                }
                mainClass.getEditor().setCode(part3UserCode);
                part3ChosenCode = ChosenCode.User;
            }
        });
        
        part3SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part3SolutionCodeMenuItem);
        part3SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showSolutionConifrm(State.Part3PseudocodeShown, part3UserCodeMenuItem, 2)) {
                    return;
                }
                //TODO editor to front
                if (part3ChosenCode == ChosenCode.Solution) {
                    return;
                }
                part3UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part3SolutionCode);
                part3ChosenCode = ChosenCode.Solution;
                if ( state.Id < State.Part2SolutionShown.Id ) {
                    state = State.Part2SolutionShown;
                }
            }
        });
        
        part3GotoPart2MenuItem = new JMenuItem(Lang.part3GotoPart2MenuItem);
        part3GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.showPart(2);
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part3UserCodeMenuItem);
        group.add(part3SolutionCodeMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initLesson">
    private void initLesson() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1UserCodeMenuItem);
        lessonMenu.add(part1SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1GotoPart2MenuItem);

        lessonMenu.setEnabled(true);
        
        if (part1ChosenCode == ChosenCode.User) {
            mainClass.getEditor().setCode(part1UserCode);
            part1UserCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(part1SolutionCode);
            part1SolutionCodeMenuItem.setSelected(true);
        }
    }
    //</editor-fold>
    
    
    @Override
    public void start() {
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initCodes(false);
        initPart1MenuItems();
        initPart2MenuItems();
        initPart3MenuItems();
        initLesson();
        
        SpecialFunctions.addSpecialFunction(new StartSpecialFunction(arrayFrame));
        SpecialFunctions.addSpecialFunction(new CompareSpecialFunction(arrayFrame));
        SpecialFunctions.addSpecialFunction(new MoveSpecialFunction(arrayFrame));
        
        arrayFrame.showFrame();
        //textFrame.showFrame();
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        arrayFrame.threadStart();
    }
    
    @Override
    public void threadStop() {
        arrayFrame.threadStop();
    }
    
    @Override
    public void pauseStart(SyntaxNode node, final int delayTime) {
        arrayFrame.updateResultValues();
    }

    @Override
    public void pauseStop(SyntaxNode node) {
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String part1TextMenuItem = "Treść zadania";
        public static final String part1PseudocodeMenuItem = "Pseudokod";
        public static final String part1UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part1SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part1GotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2Hint1MenuItem = "Wskazówka I";
        public static final String part2Hint2MenuItem = "Wskazówka II";
        public static final String part2PseudocodeMenuItem = "Wskazówka III";
        public static final String part2UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part2SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part2GotoPart1MenuItem = "<<< Część I";
        public static final String part2GotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania";
        public static final String part3HintMenuItem = "Wskazówka I";
        public static final String part3PseudocodeMenuItem = "Wskazówka II";
        public static final String part3UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part3SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part3SummaryMenuItem = "Podsumowanie";
        public static final String part3GotoPart2MenuItem = "<<< Część II";
        
        public static final String showSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wskazówki.";
        
        public static final String showHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
        public static final String showNextPartConifrm =
                "Nie wyświetliłeś jeszcze rozwiązania wzorcowego.\n"
                + "Czy na pewno chcesz przejść do następnej części?";
        
        public static final String showSummaryConifrm = 
                "Nie wyświetliłeś jeszcze rozwiązania wzorcowego.\n"
                + "Czy na pewno chcesz wyświetlić podsumowanie?";
        
        public static final String question = "Pytanie";
        public static final String showHint = "Pokaż wskazówkę";
        public static final String showSolution = "Pokaż rozwiązanie";
        public static final String cancel = "Anuluj";
        public static final String yes = "Tak";
        public static final String no = "Nie";
    }
    //</editor-fold>
    
}

