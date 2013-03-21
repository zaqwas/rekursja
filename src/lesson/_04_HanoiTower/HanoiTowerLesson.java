package lesson._04_HanoiTower;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._01B_MaxElement.*;
import helpers.LessonHelper;
import interpreter.Instance;
import interpreter.InterpreterThread;
import interpreter.accessvar.AccessInteger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import parser.SpecialFunctions;
import syntax.SyntaxNode;
import syntax.function.FunctionBehavior;
//</editor-fold>

class HanoiTowerLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public static enum State {
        NothingShown(0),
        Hint1Shown(1),
        Hint2Shown(2),
        Hint3Shown(3),
        PseudocodeShown(4),
        SolutionShown(5),
        SummaryShown(6);
        
        public final byte id;
        public final byte hint;

        State(int id) {
            this.id = (byte) id;
            hint = id < 5 ? (byte)id : 4;
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 3;
            
            State[] values = values();
            if ( values[id].id == id ) {
                return values[id];
            }
            for (State state : values) {
                if ( state.id == id ) {
                    return state;
                }
            }
            return null;
        }
    }
    
    private static enum ChosenCode {
        User(0), Solution(1);
        public final byte Id;

        ChosenCode(int id) {
            Id = (byte) id;
        }
        
        public static ChosenCode getById(int id) {
            assert id == 0 || id == 1;
            return id == 0 ? User : Solution;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private ChosenCode chosenCode = ChosenCode.User;
    
    private MainClass mainClass;
    private HanoiTowerLessonLoader loader;
    
    private TextFrame textFrame;
    private HanoiFrame arrayFrame;
    
    private JMenuItem textMenuItem;
    private JMenuItem functionsMenuItem;
    private JMenuItem hint1MenuItem;
    private JMenuItem hint2MenuItem;
    private JMenuItem hint3MenuItem;
    private JMenuItem pseudocodeMenuItem;
    private JRadioButtonMenuItem userCodeMenuItem;
    private JRadioButtonMenuItem solutionCodeMenuItem;
    private JMenuItem summaryMenuItem;
    
    private String oldCode;
    private String userCode;
    private String solutionCode;
    
//    private CompareSpecialFunction compareSpecialFunction;
    
    
    public HanoiTowerLesson(MainClass mainClass, DataInputStream stream, 
            HanoiTowerLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        arrayFrame = new HanoiFrame(mainClass);
        
        initMenuItems();
        
//        SpecialFunctions.add(new StartSpecialFunction(arrayFrame));
//        SpecialFunctions.add(new CheckSpecialFunction(arrayFrame, mainClass.getConsole()));
//        compareSpecialFunction = new CompareSpecialFunction(arrayFrame);
//        SpecialFunctions.add(compareSpecialFunction);
        
        if (stream == null) {
            initCodes(true);
            textFrame.gotoText();
            arrayFrame.frameToFront();
        } else {
            initCodes(false);
            mainClass.loadFramesPositionAndSettnings(stream);
            
            state = State.getById(stream.readByte());
            chosenCode = ChosenCode.getById(stream.readByte());
            
            userCode = stream.readUTF();
            
            textFrame.initialize(state);
        }
        
        initLesson();
    }
    
    //<editor-fold defaultstate="collapsed" desc="showSolutionConifrm">
    private boolean showSolutionConifrm() {
        if (state.id >= State.PseudocodeShown.id) {
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
                if (state == State.NothingShown) {
                    hint1MenuItem.doClick();
                } else if (state == State.Hint1Shown) {
                    hint2MenuItem.doClick();
                } else if (state == State.Hint1Shown) {
                    hint3MenuItem.doClick();
                } else {
                    pseudocodeMenuItem.doClick();
                }
            }
            return false;
        }
        textFrame.showAllHints();
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showConifrmDialog">
    private boolean showConifrmDialog(String message, State state) {
        if (this.state.id >= state.id) {
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
    private void initCodes(boolean initUserCode) {
        InputStream stream;
        stream = getClass().getResourceAsStream("solution_code.txt");
        solutionCode = LessonHelper.readFile(stream);
        
        if (initUserCode) {
            stream = getClass().getResourceAsStream("user_code.txt");
            userCode = LessonHelper.readFile(stream);
        }

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
        
        functionsMenuItem = new JMenuItem(Lang.functionsMenuItem);
        functionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoFunctions();
            }
        });
        
        hint1MenuItem = new JMenuItem(Lang.hint1MenuItem);
        hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoHint(1);
                if ( state.id < State.Hint1Shown.id ) {
                    state = State.Hint1Shown;
                }
            }
        });
        
        hint2MenuItem = new JMenuItem(Lang.hint2MenuItem);
        hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Hint1Shown)) {
                    return;
                }
                textFrame.gotoHint(2);
                if ( state.id < State.Hint2Shown.id ) {
                    state = State.Hint2Shown;
                }
            }
        });
        
        hint3MenuItem = new JMenuItem(Lang.hint3MenuItem);
        hint3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Hint2Shown)) {
                    return;
                }                
                textFrame.gotoHint(3);
                if ( state.id < State.Hint3Shown.id ) {
                    state = State.Hint3Shown;
                }
            }
        });
        
        pseudocodeMenuItem = new JMenuItem(Lang.pseudocodeMenuItem);
        pseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Hint3Shown)) {
                    return;
                }
                textFrame.gotoHint(4);
                if ( state.id < State.PseudocodeShown.id ) {
                    state = State.PseudocodeShown;
                }
            }
        });
        
        userCodeMenuItem = new JRadioButtonMenuItem(Lang.userCodeMenuItem);
        userCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
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
                if (!showSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (chosenCode == ChosenCode.Solution) {
                    return;
                }
                userCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(solutionCode);
                
                chosenCode = ChosenCode.Solution;
                if (state.id < State.SolutionShown.id) {
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
                textFrame.gotoSummary();
                state = State.SummaryShown;
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
        lessonMenu.add(functionsMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(hint1MenuItem);
        lessonMenu.add(hint2MenuItem);
        lessonMenu.add(hint3MenuItem);
        lessonMenu.add(pseudocodeMenuItem);
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
    }
    //</editor-fold>
    
    
    @Override
    public LessonLoader getLessonLoader() {
        return loader;
    }
    
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
        
        stream.writeByte(state.id);
        stream.writeByte(chosenCode.Id);
        
        if (chosenCode == ChosenCode.User) {
            userCode = mainClass.getEditor().getCode();
        }
        stream.writeUTF(userCode);
    }
    
    @Override
    public void close() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();
        lessonMenu.setEnabled(false);
        
        SpecialFunctions.clear();
        
        mainClass.removeAddictionalLessonFrame(textFrame.getFrame());
        mainClass.removeAddictionalLessonFrame(arrayFrame.getFrame());
        mainClass.getEditor().setCode(oldCode);
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        userCodeMenuItem.setEnabled(false);
        solutionCodeMenuItem.setEnabled(false);
    }
    
    @Override
    public void threadStop() {
        userCodeMenuItem.setEnabled(true);
        solutionCodeMenuItem.setEnabled(true);
    }
    
    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
//        FunctionBehavior behavior = instance.getFunction().getFunctionBehavior();
//        boolean userCodeChosen = chosenCode == ChosenCode.User;
//        if (behavior == compareSpecialFunction) {
//            compareSpecialFunction.pauseStart(userCodeChosen);
//            return true;
//        }
//        if (userCodeChosen) {
//            return true;
//        }
//        
//        if (instance.getFunction().getName().equals("idxMaxElement")) {
//            AccessInteger access = (AccessInteger) instance.getFunction().getAccessVarByName("idxMax");
//            BigInteger value = access.getValue(instance);
//            if (value != null) {
//                arrayFrame.paintMax(value.intValue());
//            }
//        } else {
//            arrayFrame.paintMax();
//        }
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
//        compareSpecialFunction.pauseStop();
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textMenuItem = "Treść zadania";
        public static final String functionsMenuItem = "Funkcje specjalne";
        public static final String hint1MenuItem = "Wskazówka I";
        public static final String hint2MenuItem = "Wskazówka II";
        public static final String hint3MenuItem = "Wskazówka III";
        public static final String pseudocodeMenuItem = "Pseudokod";
        public static final String summaryMenuItem = "Podsumowanie";
        
        public static final String userCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String solutionCodeMenuItem = "Rozwiązanie wzorcowe";
        
        
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

