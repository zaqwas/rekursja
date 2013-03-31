package lesson._02C_Logarithm;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._03B_EuclideanAlgorithm.*;
import helpers.LessonHelper;
import interpreter.Instance;
import interpreter.InterpreterThread;
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
//</editor-fold>

class LogarithmLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public static enum State {
        NothingShown(0), 
        Part1PseudocodeShown(1), 
        Part1SolutionShown(2), 
        Part2Shown(3),
        Part2PseudocodeShown(4),
        Part2RecursionSolutionShown(5), Part2IterationSolutionShown(6),
        Part2BothSolutionShown(7), 
        SummaryShown(8);
        
        public final byte id;
        public final byte part;
        public final byte hint;

        State(int id) {
            this.id = (byte) id;

            part = (byte) (id < 3 ? 1 : 2);

            if (id < 3) {
                hint = (byte) (id == 0 ? 0 : 1);
            } else {
                hint = (byte) (id == 3 ? 0 : 1);;
            }
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 8;
            
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
    
    private static enum Part1ChosenCode {
        User(0), Solution(1);
        
        public final byte id;

        Part1ChosenCode(int id) {
            this.id = (byte) id;
        }
        
        public static Part1ChosenCode getById(int id) {
            assert id == 0 || id == 1;
            return id == 0 ? User : Solution;
        }
    }
    
    private static enum Part2ChosenCode {
        UserRecursion(0),
        UserIteration(1),
        SolutionRecursion(2),
        SolutionIteration(3);
        
        public final byte id;

        Part2ChosenCode(int id) {
            this.id = (byte) id;
        }
        
        public static Part2ChosenCode getById(int id) {
            assert 0 <= id && id <= 3;
            return id < 2 ? (id == 0 ? UserRecursion : UserIteration)
                    : (id == 2 ? SolutionRecursion : SolutionIteration);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables and components">
    private State state = State.NothingShown;
    private Part1ChosenCode part1ChosenCode = Part1ChosenCode.User;
    private Part2ChosenCode part2ChosenCode = Part2ChosenCode.UserRecursion;
    private byte selectedPart = 1;
    
    private MainClass mainClass;
    private LogarithmLessonLoader loader;
    
    private TextFrame textFrame;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionCodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2PseudocodeMenuItem;
    private JRadioButtonMenuItem part2UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionIterationCodeMenuItem;
    private JMenuItem part2SummaryMenuItem;
    private JMenuItem part2GotoPart1MenuItem;
    
    
    private String oldCode;
    private String part1UserCode;
    private String part1SolutionCode;
    private String part2UserRecursionCode;
    private String part2UserIterationCode;
    private String part2SolutionRecursionCode;
    private String part2SolutionIterationCode;
    
    private StartSpecialFunction startSpecialFunction;
    private LogarithmSpecialFunction gcdSpecialFunction;
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public LogarithmLesson(MainClass mainClass, DataInputStream stream,
            LogarithmLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(17);
        
        startSpecialFunction = new StartSpecialFunction();
        SpecialFunctions.add(startSpecialFunction);
        gcdSpecialFunction = new LogarithmSpecialFunction();
        SpecialFunctions.add(gcdSpecialFunction);
        SpecialFunctions.add(new CheckSpecialFunction(startSpecialFunction, mainClass.getConsole()));
        
        initPart1MenuItems();
        initPart2MenuItems();
        
        if (stream == null) {
            initCodes(true);
            initPart1();
            textFrame.gotoText();
        } else {
            initCodes(false);
            mainClass.loadFramesPositionAndSettnings(stream);
            
            selectedPart = stream.readByte();
            state = State.getById(stream.readByte());
            
            part1ChosenCode = Part1ChosenCode.getById(stream.readByte());
            part2ChosenCode = Part2ChosenCode.getById(stream.readByte());
            
            part1UserCode = stream.readUTF();
            part2UserRecursionCode = stream.readUTF();
            part2UserIterationCode = stream.readUTF();
            
            if (selectedPart == 1) {
                initPart1();
            } else {
                initPart2();
            }
            
            textFrame.initialize(selectedPart, state);
        }
    }
    //</editor-fold>
    
    
    //private fucntions:
    
    //<editor-fold defaultstate="collapsed" desc="part2RememberCode">
    private void part2RememberCode() {
        if (part2ChosenCode == Part2ChosenCode.UserRecursion) {
            part2UserRecursionCode = mainClass.getEditor().getCode();
        } else if (part2ChosenCode == Part2ChosenCode.UserIteration) {
            part2UserIterationCode = mainClass.getEditor().getCode();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setButtonsEnabled">
    private void setButtonsEnabled(boolean enabled) {
        if (selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(enabled);
            part1UserCodeMenuItem.setEnabled(enabled);
            part1SolutionCodeMenuItem.setEnabled(enabled);
        } else {
            part2GotoPart1MenuItem.setEnabled(enabled);
            part2UserRecursionCodeMenuItem.setEnabled(enabled);
            part2UserIterationCodeMenuItem.setEnabled(enabled);
            part2SolutionRecursionCodeMenuItem.setEnabled(enabled);
            part2SolutionIterationCodeMenuItem.setEnabled(enabled);
            part2SummaryMenuItem.setEnabled(enabled);
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
        textFrame.showPseudocode();
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part2ShowSolutionConifrm">
    private boolean part2ShowSolutionConifrm() {
        if (state.id >= State.Part2PseudocodeShown.id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part2ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showPseudocode, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            if (part2ChosenCode == Part2ChosenCode.UserIteration) {
                part2UserIterationCodeMenuItem.setSelected(true);
            } else {
                part2UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                part2PseudocodeMenuItem.doClick();
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
    private void initCodes(boolean initUserCodes) {
        InputStream stream;
        
        stream = getClass().getResourceAsStream("part1_solution_code.txt");
        part1SolutionCode = LessonHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part2_solution_recursion_code.txt");
        part2SolutionRecursionCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part2_solution_iteration_code.txt");
        part2SolutionIterationCode = LessonHelper.readFile(stream);

        if (initUserCodes) {
            stream = getClass().getResourceAsStream("part1_user_code.txt");
            part1UserCode = LessonHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part2_user_recursion_code.txt");
            part2UserRecursionCode = LessonHelper.readFile(stream);
            
            stream = getClass().getResourceAsStream("part2_user_iteration_code.txt");
            part2UserIterationCode = LessonHelper.readFile(stream);
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
                textFrame.gotoPseudocode();
                if (state.id < State.Part1PseudocodeShown.id) {
                    state = State.Part1PseudocodeShown;
                }
            }
        });
        
        part1UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserCodeMenuItem);
        part1UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == Part1ChosenCode.User) {
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
                if (!part1ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == Part1ChosenCode.Solution) {
                    return;
                }
                
                part1UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part1SolutionCode);
                part1ChosenCode = Part1ChosenCode.Solution;

                if (state.id < State.Part1SolutionShown.id) {
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
                if (part1ChosenCode == Part1ChosenCode.User) {
                    part1UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPart(2);
                initPart2();
                if (state.id < State.Part2Shown.id) {
                    state = State.Part2Shown;
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
        
        part2PseudocodeMenuItem = new JMenuItem(Lang.part2PseudocodeMenuItem);
        part2PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoPseudocode();
                if (state.id < State.Part2PseudocodeShown.id) {
                    state = State.Part2PseudocodeShown;
                }
            }
        });
        

        part2UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserRecursionCodeMenuItem);
        part2UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.UserRecursion) {
                    return;
                }
                part2RememberCode();
                mainClass.getEditor().setCode(part2UserRecursionCode);
                part2ChosenCode = Part2ChosenCode.UserRecursion;
            }
        });
        
        part2UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserIterationCodeMenuItem);
        part2UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.UserIteration) {
                    return;
                }
                part2RememberCode();
                mainClass.getEditor().setCode(part2UserIterationCode);
                part2ChosenCode = Part2ChosenCode.UserIteration;
            }
        });
        
        part2SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionRecursionCodeMenuItem);
        part2SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part2ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.SolutionRecursion) {
                    return;
                }
                
                part2RememberCode();
                mainClass.getEditor().setCode(part2SolutionRecursionCode);
                part2ChosenCode = Part2ChosenCode.SolutionRecursion;

                if (state.id <= State.Part2PseudocodeShown.id) {
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
                if (!part2ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.SolutionIteration) {
                    return;
                }
                
                part2RememberCode();
                mainClass.getEditor().setCode(part2SolutionIterationCode);
                part2ChosenCode = Part2ChosenCode.SolutionIteration;

                if (state.id <= State.Part2PseudocodeShown.id) {
                    state = State.Part2IterationSolutionShown;
                } else if (state == State.Part2RecursionSolutionShown) {
                    state = State.Part2BothSolutionShown;
                }
            }
        });
        

        
        part2SummaryMenuItem = new JMenuItem(Lang.part2SummaryMenuItem);
        part2SummaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showSummaryConifrm, State.Part2BothSolutionShown)) {
                    return;
                }
                textFrame.gotoSummary();
                if (state.id < State.SummaryShown.id) {
                    state = State.SummaryShown;
                }
            }
        });
        
        part2GotoPart1MenuItem = new JMenuItem(Lang.part2GotoPart1MenuItem);
        part2GotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part2RememberCode();
                textFrame.gotoPart(1);
                initPart1();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part2UserRecursionCodeMenuItem);
        group.add(part2UserIterationCodeMenuItem);
        group.add(part2SolutionRecursionCodeMenuItem);
        group.add(part2SolutionIterationCodeMenuItem);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1() {
        selectedPart = 1;
        
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
        
        if (part1ChosenCode == Part1ChosenCode.User) {
            mainClass.getEditor().setCode(part1UserCode);
            part1UserCodeMenuItem.setSelected(true);
        } else if (part1ChosenCode == Part1ChosenCode.Solution) {
            mainClass.getEditor().setCode(part1SolutionCode);
            part1SolutionCodeMenuItem.setSelected(true);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        selectedPart = 2;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2UserRecursionCodeMenuItem);
        lessonMenu.add(part2UserIterationCodeMenuItem);
        lessonMenu.add(part2SolutionRecursionCodeMenuItem);
        lessonMenu.add(part2SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2SummaryMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2GotoPart1MenuItem);
        
        lessonMenu.setEnabled(true);
        
        if (part2ChosenCode == Part2ChosenCode.UserRecursion) {
            mainClass.getEditor().setCode(part2UserRecursionCode);
            part2UserRecursionCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == Part2ChosenCode.UserIteration) {
            mainClass.getEditor().setCode(part2UserIterationCode);
            part2UserIterationCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == Part2ChosenCode.SolutionRecursion) {
            mainClass.getEditor().setCode(part2SolutionRecursionCode);
            part2SolutionRecursionCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == Part2ChosenCode.SolutionIteration) {
            mainClass.getEditor().setCode(part2SolutionIterationCode);
            part2SolutionIterationCodeMenuItem.setSelected(true);
        }
    }
    //</editor-fold>    
    
    
    
    //public fucntions:
    
    @Override
    public LessonLoader getLessonLoader() {
        return loader;
    }
    
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
        stream.writeByte(selectedPart);
        stream.writeByte(state.id);
        
        stream.writeByte(part1ChosenCode.id);
        stream.writeByte(part2ChosenCode.id);
        
        if (selectedPart == 1) {
            if (part1ChosenCode == Part1ChosenCode.User) {
                part1UserCode = mainClass.getEditor().getCode();
            }
        } else if (selectedPart == 2) {
            part2RememberCode();
        }
        
        stream.writeUTF(part1UserCode);
        stream.writeUTF(part2UserRecursionCode);
        stream.writeUTF(part2UserIterationCode);
    }
    
    @Override
    public void close() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();
        lessonMenu.setEnabled(false);
        
        SpecialFunctions.clear();
        mainClass.getTreeOfInstances().setDefaultTreeNodeMaxLetters();
        
        mainClass.removeAddictionalLessonFrame(textFrame.getFrame());
        mainClass.getEditor().setCode(oldCode);
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        setButtonsEnabled(false);
    }
    
    @Override
    public void threadStop() {
        setButtonsEnabled(true);
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
        public static final String part1TextMenuItem = "Treść zadania";
        public static final String part1PseudocodeMenuItem = "Wskazówaka: pseudokod";
        public static final String part1UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part1SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part1GotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2Hint1MenuItem = "Wskazówka";
        public static final String part2PseudocodeMenuItem = "Pseudokod";
        public static final String part2UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part2UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part2SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part2SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part2SummaryMenuItem = "Podsumowanie";
        public static final String part2GotoPart1MenuItem = "<<< Część I";
        
        public static final String part1ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to dostępna jest wskazówka w postaci pseudokodów.";
        
        public static final String part2ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wszystkie wskazówki.";
        
        public static final String showHintConifrm = 
                "Nie wyświetliłeś jeszcze poprzednich wskazówek.\n"
                + "Czy na pewno chcesz wyświetlić tę wskazówkę?";
        
        public static final String showNextPartConifrm =
                "Nie wyświetliłeś jeszcze rozwiązania wzorcowego.\n"
                + "Czy na pewno chcesz przejść do następnej części?";
        
        public static final String showSummaryConifrm = 
                "Nie wyświetliłeś jeszcze wszystkich rozwiązań wzorcowych.\n"
                + "Czy na pewno chcesz wyświetlić podsumowanie?";
        
        public static final String question = "Pytanie";
        public static final String showHint = "Pokaż wskazówkę";
        public static final String showPseudocode = "Pokaż pseudokod";
        public static final String showSolution = "Pokaż rozwiązanie";
        public static final String cancel = "Anuluj";
        public static final String yes = "Tak";
        public static final String no = "Nie";
    }
    //</editor-fold>
    
}

