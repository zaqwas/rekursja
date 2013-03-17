package lesson._07B_QuickSort;

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
import syntax.function.FunctionBehavior;
import syntax.statement.assigment.Assigment;
//</editor-fold>

class QuickSortLesson implements Lesson {

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
        HintShown(1), PseudocodeShown(2),
        SolutionShown(3), SummaryShown(4);
        
        public final byte id;

        State(int id) {
            this.id = (byte) id;
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 4;
            
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
        User(0), Solution(1), Randomized(2);
        public final byte Id;

        ChosenCode(int id) {
            Id = (byte) id;
        }
        
        public static ChosenCode getById(int id) {
            assert id == 0 || id == 1 || id == 2;
            return id == 0 ? User : id == 1 ? Solution : Randomized;
        }
    }
    //</editor-fold>
    
    private State state = State.NothingShown;
    private ChosenCode chosenCode = ChosenCode.User;
    
    private boolean threadStarted;
    
    private MainClass mainClass;
    private QuickSortLessonLoader loader;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem textMenuItem;
    private JMenuItem functionsMenuItem;
    private JMenuItem hintMenuItem;
    private JMenuItem pseudocodeMenuItem;
    private JRadioButtonMenuItem userCodeMenuItem;
    private JRadioButtonMenuItem solutionCodeMenuItem;
    private JRadioButtonMenuItem randomizedCodeMenuItem;
    private JMenuItem summaryMenuItem;
    
    private String oldCode;
    private String userCode;
    private String solutionCode;
    private String randomizedCode;
    
    private PartitionSpecialFunction partitionSpecialFunction;
    private RandomSpecialFunction randomSpecialFunction;
    private SwapSpecialFunction swapSpecialFunction;
    
    public QuickSortLesson(MainClass mainClass, DataInputStream stream, 
            QuickSortLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(14);
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initMenuItems();
        
        SpecialFunctions.add(new StartSpecialFunction(arrayFrame));
        SpecialFunctions.add(new CheckSpecialFunction(mainClass.getConsole(), arrayFrame));
        SpecialFunctions.add(new SortSpecialFunction());
        partitionSpecialFunction = new PartitionSpecialFunction(arrayFrame);
        SpecialFunctions.add(partitionSpecialFunction);
        randomSpecialFunction = new RandomSpecialFunction();
        SpecialFunctions.add(randomSpecialFunction);
        swapSpecialFunction = new SwapSpecialFunction(arrayFrame);
        SpecialFunctions.add(swapSpecialFunction);
        
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
            
            if (state == State.SummaryShown) {
                randomizedCodeMenuItem.setEnabled(true);
            }
            
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
        
        stream = getClass().getResourceAsStream("randomized_code.txt");
        randomizedCode = LessonHelper.readFile(stream);
        
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
                if (!showConifrmDialog(Lang.showHint, State.HintShown)) {
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
                if ( chosenCode == ChosenCode.User ) {
                    userCode = mainClass.getEditor().getCode();
                }
                mainClass.getEditor().setCode(solutionCode);
                chosenCode = ChosenCode.Solution;
                if ( state.id < State.SolutionShown.id ) {
                    state = State.SolutionShown;
                }
                
                mainClass.getEditor().frameToFront();
            }
        });
        
        randomizedCodeMenuItem = new JRadioButtonMenuItem(Lang.randomizedCodeMenuItem);
        randomizedCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chosenCode == ChosenCode.Randomized) {
                    return;
                }
                if ( chosenCode == ChosenCode.User ) {
                    userCode = mainClass.getEditor().getCode();
                }
                mainClass.getEditor().setCode(randomizedCode);
                chosenCode = ChosenCode.Randomized;
                
                mainClass.getEditor().frameToFront();
            }
        });
        randomizedCodeMenuItem.setEnabled(false);
        
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
                if (!threadStarted) {
                    randomizedCodeMenuItem.setEnabled(true);
                }
                textFrame.gotoSummary();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(userCodeMenuItem);
        group.add(solutionCodeMenuItem);
        group.add(randomizedCodeMenuItem);
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
        lessonMenu.add(randomizedCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(summaryMenuItem);

        lessonMenu.setEnabled(true);
        
        if (chosenCode == ChosenCode.User) {
            mainClass.getEditor().setCode(userCode);
            userCodeMenuItem.setSelected(true);
        } else if (chosenCode == ChosenCode.Solution) {
            mainClass.getEditor().setCode(solutionCode);
            solutionCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(randomizedCode);
            randomizedCodeMenuItem.setSelected(true);
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
        threadStarted = true;
        arrayFrame.threadStart();
        userCodeMenuItem.setEnabled(false);
        solutionCodeMenuItem.setEnabled(false);
        randomizedCodeMenuItem.setEnabled(false);
    }
    
    @Override
    public void threadStop() {
        arrayFrame.threadStop();
        userCodeMenuItem.setEnabled(true);
        solutionCodeMenuItem.setEnabled(true);
        if (state == State.SummaryShown) {
            randomizedCodeMenuItem.setEnabled(true);
        }
        threadStarted = false;
    }
    
    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
        FunctionBehavior behavior = instance.getFunction().getFunctionBehavior();
        if (behavior == partitionSpecialFunction) {
            partitionSpecialFunction.pauseStart(delayTime, chosenCode == ChosenCode.User);
            return false;
        }
        if (behavior == swapSpecialFunction) {
            swapSpecialFunction.pauseStart(delayTime);
            return false;
        }
        
        if (chosenCode == ChosenCode.User) {
            arrayFrame.updateArray();
            return true;
        }
        
        int idx1 = ((ArgInteger) instance.getArgument(0)).getValueAtTheBeginning().intValue();
        int idx2 = ((ArgInteger) instance.getArgument(1)).getValueAtTheBeginning().intValue();
        
        if (!(node instanceof Call)) {
            arrayFrame.updateBeforePartition(idx1, idx2);
        } else if (instance.getParentInstance().getParentInstance() == null) {
            if (afterCall) {
                arrayFrame.updateArrayAndPaint("equal");
            } else {
                arrayFrame.updateBeforePartition(0, idx2);
            }
        } else {
            int idx3 = ((ArgInteger) instance.getParentInstance().getArgument(0)).getValueAtTheBeginning().intValue();
            int idx4 = ((ArgInteger) instance.getParentInstance().getArgument(1)).getValueAtTheBeginning().intValue();

            if (idx1 == idx3) {
                arrayFrame.updateAfterPartition(idx3, idx4, idx2, true, afterCall);
            } else {
                arrayFrame.updateAfterPartition(idx3, idx4, idx1 - 1, false, afterCall);
            }
        }
        
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
        partitionSpecialFunction.pauseStop();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textMenuItem = "Treść zadania";
        public static final String functionsMenuItem = "Funkcje specjalne";
        public static final String hintMenuItem = "Wskazówka";
        public static final String pseudocodeMenuItem = "Pseudokod";
        
        public static final String userCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String solutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String randomizedCodeMenuItem = "Rozwiązanie zrandomizowane";
        
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

