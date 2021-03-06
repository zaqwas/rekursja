package lesson._01B_MaxElement;

//<editor-fold defaultstate="collapsed" desc="Import classes">
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

class MaxElementLesson implements Lesson {

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
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public static enum State {
        NothingShown(0),
        HintShown(1), 
        PseudocodeShown(2),
        SolutionShown(3);
        
        public final byte id;

        State(int id) {
            this.id = (byte) id;
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
    private MaxElementLessonLoader loader;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem textMenuItem;
    private JMenuItem functionsMenuItem;
    private JMenuItem hintMenuItem;
    private JMenuItem pseudocodeMenuItem;
    private JRadioButtonMenuItem userCodeMenuItem;
    private JRadioButtonMenuItem solutionCodeMenuItem;
    
    private String oldCode;
    private String userCode;
    private String solutionCode;
    
    private CompareSpecialFunction compareSpecialFunction;
    
    public MaxElementLesson(MainClass mainClass, DataInputStream stream, 
            MaxElementLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initMenuItems();
        
        SpecialFunctions.add(new StartSpecialFunction(arrayFrame));
        SpecialFunctions.add(new CheckSpecialFunction(arrayFrame, mainClass.getConsole()));
        compareSpecialFunction = new CompareSpecialFunction(arrayFrame);
        SpecialFunctions.add(compareSpecialFunction);
        
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
                    hintMenuItem.doClick();
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
        
        hintMenuItem = new JMenuItem(Lang.hintMenuItem);
        hintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.id < State.HintShown.id ) {
                    state = State.HintShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        pseudocodeMenuItem = new JMenuItem(Lang.pseudocodeMenuItem);
        pseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.HintShown)) {
                    return;
                }
                if ( state.id < State.PseudocodeShown.id ) {
                    state = State.PseudocodeShown;
                }
                textFrame.gotoHint(2);
            }
        });
        
        userCodeMenuItem = new JRadioButtonMenuItem(Lang.userCodeMenuItem);
        userCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( chosenCode == ChosenCode.User ) {
                    return;
                }
                mainClass.getEditor().setCode(userCode);
                mainClass.getEditor().frameToFront();
                chosenCode = ChosenCode.User;
            }
        });
        
        solutionCodeMenuItem = new JRadioButtonMenuItem(Lang.solutionCodeMenuItem);
        solutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chosenCode == ChosenCode.Solution) {
                    return;
                }
                if ( !showSolutionConifrm() ) {
                    return;
                }
                if ( chosenCode == ChosenCode.User ) {
                    userCode = mainClass.getEditor().getCode();
                }
                mainClass.getEditor().setCode(solutionCode);
                mainClass.getEditor().frameToFront();
                
                chosenCode = ChosenCode.Solution;
                if ( state.id < State.SolutionShown.id ) {
                    state = State.SolutionShown;
                }
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
        lessonMenu.add(hintMenuItem);
        lessonMenu.add(pseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(userCodeMenuItem);
        lessonMenu.add(solutionCodeMenuItem);

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
        arrayFrame.threadStart();
        userCodeMenuItem.setEnabled(false);
        solutionCodeMenuItem.setEnabled(false);
    }
    
    @Override
    public void threadStop() {
        arrayFrame.threadStop();
        userCodeMenuItem.setEnabled(true);
        solutionCodeMenuItem.setEnabled(true);
    }
    
    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
        FunctionBehavior behavior = instance.getFunction().getFunctionBehavior();
        boolean userCodeChosen = chosenCode == ChosenCode.User;
        if (behavior == compareSpecialFunction) {
            compareSpecialFunction.pauseStart(userCodeChosen);
            return true;
        }
        if (userCodeChosen) {
            return true;
        }
        
        if (instance.getFunction().getName().equals("idxMaxElement")) {
            AccessInteger access = (AccessInteger) instance.getFunction().getAccessVarByName("idxMax");
            BigInteger value = access.getValue(instance);
            if (value != null) {
                arrayFrame.paintMax(value.intValue());
            }
        } else {
            arrayFrame.paintMax();
        }
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
        compareSpecialFunction.pauseStop();
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textMenuItem = "Treść zadania";
        public static final String functionsMenuItem = "Funkcje specjalne";
        public static final String hintMenuItem = "Wskazówka";
        public static final String pseudocodeMenuItem = "Pseudokod";
        
        public static final String userCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String solutionCodeMenuItem = "Rozwiązanie wzorcowe";
        
        
        public static final String showSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wskazówki.";
        
        public static final String showHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
        public static final String question = "Pytanie";
        public static final String showHint = "Pokaż wskazówkę";
        public static final String showSolution = "Pokaż rozwiązanie";
        public static final String cancel = "Anuluj";
        public static final String yes = "Tak";
        public static final String no = "Nie";
    }
    //</editor-fold>
    
}

