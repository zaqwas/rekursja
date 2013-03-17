package lesson._06B_MergeSort;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import helpers.LessonHelper;
import interpreter.Instance;
import interpreter.InterpreterThread;
import interpreter.arguments.ArgInteger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import syntax.expression.Call;
import syntax.statement.assigment.Assigment;
//</editor-fold>

class MergeSortLesson implements Lesson {

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
        NothingShown(0),PseudocodeShown(1),
        SolutionShown(2), SummaryShown(3);
        
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
    private MergeSortLessonLoader loader;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem textMenuItem;
    private JMenuItem functionsMenuItem;
    private JMenuItem pseudocodeMenuItem;
    private JRadioButtonMenuItem userCodeMenuItem;
    private JRadioButtonMenuItem solutionCodeMenuItem;
    private JMenuItem summaryMenuItem;
    
    private String oldCode;
    private String userCode;
    private String solutionCode;
    
    private MergeSpecialFunction moveSpecialFunction;
    
    public MergeSortLesson(MainClass mainClass, DataInputStream stream, 
            MergeSortLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(11);
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initMenuItems();
        
        SpecialFunctions.add(new StartSpecialFunction(arrayFrame));
        SpecialFunctions.add(new CheckSpecialFunction(mainClass.getConsole(), arrayFrame));
        SpecialFunctions.add(new SortSpecialFunction());
        moveSpecialFunction = new MergeSpecialFunction(arrayFrame);
        SpecialFunctions.add(moveSpecialFunction);
        
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
            //textFrame.gotoText();
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
                pseudocodeMenuItem.doClick();
            }
            return false;
        }
        textFrame.showPseudocode();
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
        
        pseudocodeMenuItem = new JMenuItem(Lang.pseudocodeMenuItem);
        pseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.id < State.PseudocodeShown.id ) {
                    state = State.PseudocodeShown;
                }
                textFrame.gotoPseudocode();
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
                chosenCode = ChosenCode.User;
                
                mainClass.getEditor().frameToFront();
            }
        });
        
        solutionCodeMenuItem = new JRadioButtonMenuItem(Lang.solutionCodeMenuItem);
        solutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !showSolutionConifrm() ) {
                    return;
                }
                if (chosenCode == ChosenCode.Solution) {
                    return;
                }
                userCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(solutionCode);
                chosenCode = ChosenCode.Solution;
                if ( state.id < State.SolutionShown.id ) {
                    state = State.SolutionShown;
                }
                
                mainClass.getEditor().frameToFront();
            }
        });
        
        summaryMenuItem = new JMenuItem(Lang.summaryMenuItem);
        summaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showSummaryConifrm, State.SolutionShown)) {
                    return;
                }
                if (state.id < State.SummaryShown.id) {
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
        lessonMenu.add(functionsMenuItem);
        lessonMenu.add(new JSeparator());
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
    public void close() {
        mainClass.getTreeOfInstances().setDefaultTreeNodeMaxLetters();
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
        moveSpecialFunction.undo(node);
        
        if ( chosenCode == ChosenCode.User ) {
            arrayFrame.updateArray();
        } else {
            int mode = 0, argIdx = 1;
            if (node instanceof Call) {
                if (instance.getParentInstance().getParentInstance() == null) {
                    mode = (afterCall ? 1 : 2);
                } else if (instance.getFunction().getFunctionBehavior() == moveSpecialFunction) {
                    argIdx = 2;
                } else {
                    instance = instance.getParentInstance();
                }
            }
            else {
                mode = node instanceof Assigment ? 0 : 2;
            }
            int idx1 = ((ArgInteger) instance.getArgument(0)).getValueAtTheBeginning().intValue();
            int idx2 = ((ArgInteger) instance.getArgument(argIdx)).getValueAtTheBeginning().intValue();
            arrayFrame.updateArray(idx1, idx2, mode);
        }
        
        return moveSpecialFunction.pauseStart(node, delayTime);
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
        if (chosenCode == ChosenCode.User) {
            moveSpecialFunction.pauseStop();
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textMenuItem = "Treść zadania";
        public static final String functionsMenuItem = "Funkcje specjalne";
        public static final String pseudocodeMenuItem = "Pseudokod";
        
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

