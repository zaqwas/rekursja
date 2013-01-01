package lesson._06A_MergeFunction;

//<editor-fold defaultstate="collapsed" desc="Import classes">
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

public class MergeFunctionLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        NothingShown(0),Hint1Shown(1), Hint2Shown(2), Hint3Shown(3),
        SolutionShown(4), SummaryShown(5);
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
    private ChosenCode chosenCode = ChosenCode.User;
    
    private MainClass mainClass;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem textMenuItem;
    private JMenuItem hint1MenuItem;
    private JMenuItem hint2MenuItem;
    private JMenuItem hint3MenuItem;
    private JRadioButtonMenuItem userCodeMenuItem;
    private JRadioButtonMenuItem solutionCodeMenuItem;
    private JMenuItem summaryMenuItem;
    
    private String oldCode;
    private String userCode;
    private String solutionCode;
    
    public MergeFunctionLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    //<editor-fold defaultstate="collapsed" desc="showSolutionConifrm">
    private boolean showSolutionConifrm() {
        if (state.Id >= State.Hint3Shown.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.showSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            userCodeMenuItem.setSelected(true);
            if (option == 1) {
                if (state == State.Hint2Shown ) {
                    hint3MenuItem.doClick();
                } else if (state == State.Hint1Shown) {
                    hint2MenuItem.doClick();
                } else {
                    hint1MenuItem.doClick();
                }
            }
            return false;
        }
        textFrame.showHint(3);
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
    private void initCodes() {
        InputStream stream;
        stream = getClass().getResourceAsStream("solution_code.txt");
        solutionCode = ReadFileHelper.readFile(stream);

        stream = getClass().getResourceAsStream("user_code.txt");
        userCode = ReadFileHelper.readFile(stream);

        oldCode = mainClass.getEditor().getCode();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initMenuItems">
    private void initMenuItems() {
        textMenuItem = new JMenuItem(Lang.textMenuItem);
        textMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        
        hint1MenuItem = new JMenuItem(Lang.hint1MenuItem);
        hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Hint1Shown.Id ) {
                    state = State.Hint1Shown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        hint2MenuItem = new JMenuItem(Lang.hint2MenuItem);
        hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHint, State.Hint1Shown)) {
                    return;
                }
                if ( state.Id < State.Hint2Shown.Id ) {
                    state = State.Hint2Shown;
                }
                textFrame.gotoHint(2);
            }
        });
        
        hint3MenuItem = new JMenuItem(Lang.hint3MenuItem);
        hint3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHint, State.Hint2Shown)) {
                    return;
                }
                if ( state.Id < State.Hint3Shown.Id ) {
                    state = State.Hint3Shown;
                }
                textFrame.gotoHint(3);
            }
        });
        
        userCodeMenuItem = new JRadioButtonMenuItem(Lang.userCodeMenuItem);
        userCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //editor to front
                if ( chosenCode == ChosenCode.User ) {
                    return;
                }
                mainClass.getEditor().setCode(userCode);
                chosenCode = ChosenCode.User;
            }
        });
        
        solutionCodeMenuItem = new JRadioButtonMenuItem(Lang.solutionCodeMenuItem);
        solutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showSolutionConifrm() ) {
                    return;
                }
                //editor to front
                if (chosenCode == ChosenCode.Solution) {
                    return;
                }
                userCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(solutionCode);
                chosenCode = ChosenCode.Solution;
                if ( state.Id < State.SolutionShown.Id ) {
                    state = State.SolutionShown;
                }
            }
        });
        
        summaryMenuItem = new JMenuItem(Lang.summaryMenuItem);
        summaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showSummaryConifrm, State.SolutionShown)) {
                    return;
                }
                if (state.Id < State.SummaryShown.Id) {
                    state = State.SummaryShown;
                }
                textFrame.gotoSummary();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(userCodeMenuItem);
        group.add(solutionCodeMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initLesson">
    private void initLesson() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(textMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(hint1MenuItem);
        lessonMenu.add(hint2MenuItem);
        lessonMenu.add(hint3MenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(userCodeMenuItem);
        lessonMenu.add(solutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(summaryMenuItem);

        lessonMenu.setEnabled(true);
        
        if (chosenCode == ChosenCode.User) {
            mainClass.getEditor().setCode(userCode);
            userCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(solutionCode);
            solutionCodeMenuItem.setSelected(true);
        }

        arrayFrame.showFrame();
        //textFrame.showFrame();
    }
    //</editor-fold>
    
    
    @Override
    public void start() {
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initCodes();
        initMenuItems();
        initLesson();
        
        SpecialFunctions.addSpecialFunction(new StartSpecialFunction(arrayFrame));
        SpecialFunctions.addSpecialFunction(new CompareSpecialFunction(arrayFrame));
        SpecialFunctions.addSpecialFunction(new MoveSpecialFunction(arrayFrame));
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
        public static final String textMenuItem = "Treść zadania";
        public static final String hint1MenuItem = "Wskazówka I";
        public static final String hint2MenuItem = "Wskazówka II";
        public static final String hint3MenuItem = "Wskazówka III";
        public static final String userCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String solutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String summaryMenuItem = "Podsumowanie";
        
        public static final String showSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wskazówki.";
        
        public static final String showHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
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

