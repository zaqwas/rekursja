package lesson._03A_Exponentiation;

//<editor-fold defaultstate="collapsed" desc="Import classes">
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

class ExponentiationLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public static enum State {
        NothingShown(0), 
        Part1PseudocodeShown(1), 
        Part1RecursionSolutionShown(2), Part1IterationSolutionShown(3), 
        Part1BothSolutionShown(4), 
        Part2Shown(5),
        Part2Hint1Shown(6), Part2Hint2Shown(7), Part2PseudocodeShown(8),
        Part2RecursionSolutionShown(9), Part2IterationSolutionShown(10),
        Part2BothSolutionShown(11), 
        SummaryShown(12);
        
        public final byte id;
        public final byte part;
        public final byte hint;

        State(int id) {
            this.id = (byte) id;

            part = (byte) (id < 5 ? 1 : 2);

            if (id < 5) {
                hint = (byte) (id == 0 ? 0 : 1);
            } else if (id >= 8) {
                hint = 3;
            } else {
                hint = (byte) (id - 5);
            }
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 12;
            
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
        UserRecursion(0),
        UserIteration(1),
        SolutionRecursion(2),
        SolutionIteration(3);
        
        public final byte Id;

        ChosenCode(int id) {
            Id = (byte) id;
        }
        
        public static ChosenCode getById(int id) {
            assert 0 <= id && id <= 3;
            return id < 2 ? (id == 0 ? UserRecursion : UserIteration)
                    : (id == 2 ? SolutionRecursion : SolutionIteration);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables and components">
    private State state = State.NothingShown;
    private ChosenCode part1ChosenCode = ChosenCode.UserRecursion;
    private ChosenCode part2ChosenCode = ChosenCode.UserRecursion;
    private byte selectedPart = 1;
    
    private MainClass mainClass;
    private ExponentiationLessonLoader loader;
    
    private TextFrame textFrame;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part1UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionIterationCodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2Hint1MenuItem;
    private JMenuItem part2Hint2MenuItem;
    private JMenuItem part2PseudocodeMenuItem;
    private JRadioButtonMenuItem part2UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionIterationCodeMenuItem;
    private JMenuItem part2SummaryMenuItem;
    private JMenuItem part2GotoPart1MenuItem;
    
    
    private String oldCode;
    private String part1UserRecursionCode;
    private String part1UserIterationCode;
    private String part1SolutionRecursionCode;
    private String part1SolutionIterationCode;
    private String part2UserRecursionCode;
    private String part2UserIterationCode;
    private String part2SolutionRecursionCode;
    private String part2SolutionIterationCode;
    
    private StartSpecialFunction startSpecialFunction;
    private ExponentiationSpecialFunction exponentiationSpecialFunction;
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ExponentiationLesson(MainClass mainClass, DataInputStream stream,
            ExponentiationLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        
        startSpecialFunction = new StartSpecialFunction();
        SpecialFunctions.add(startSpecialFunction);
        exponentiationSpecialFunction = new ExponentiationSpecialFunction();
        SpecialFunctions.add(exponentiationSpecialFunction);
        SpecialFunctions.add(
                new CheckSpecialFunction(startSpecialFunction, mainClass.getConsole()));
        
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
            
            part1ChosenCode = ChosenCode.getById(stream.readByte());
            part2ChosenCode = ChosenCode.getById(stream.readByte());
            
            part1UserRecursionCode = stream.readUTF();
            part1UserIterationCode = stream.readUTF();
            part2UserRecursionCode = stream.readUTF();
            part2UserIterationCode = stream.readUTF();
            
            if (selectedPart == 1) {
                initPart1();
            } else {
                initPart2();
            }
            
            textFrame.initialize(selectedPart, state);
            textFrame.gotoText();
        }
    }
    //</editor-fold>
    
    
    //private fucntions:
    
    //<editor-fold defaultstate="collapsed" desc="part1RememberCode">
    private void part1RememberCode() {
        if (part1ChosenCode == ChosenCode.UserRecursion) {
            part1UserRecursionCode = mainClass.getEditor().getCode();
        } else if (part1ChosenCode == ChosenCode.UserIteration) {
            part1UserIterationCode = mainClass.getEditor().getCode();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part1RememberCode">
    private void part2RememberCode() {
        if (part2ChosenCode == ChosenCode.UserRecursion) {
            part2UserRecursionCode = mainClass.getEditor().getCode();
        } else if (part2ChosenCode == ChosenCode.UserIteration) {
            part2UserIterationCode = mainClass.getEditor().getCode();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setButtonsEnabled">
    private void setButtonsEnabled(boolean enabled) {
        if (selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(enabled);
            part1UserRecursionCodeMenuItem.setEnabled(enabled);
            part1UserIterationCodeMenuItem.setEnabled(enabled);
            part1SolutionRecursionCodeMenuItem.setEnabled(enabled);
            part1SolutionIterationCodeMenuItem.setEnabled(enabled);
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
        textFrame.showAllHints();
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
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            if (part2ChosenCode == ChosenCode.UserIteration) {
                part2UserIterationCodeMenuItem.setSelected(true);
            } else {
                part2UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                if (state == State.Part2Hint2Shown ) {
                    part2PseudocodeMenuItem.doClick();
                } else if (state == State.Part2Hint1Shown) {
                    part2Hint2MenuItem.doClick();
                } else {
                    part2Hint1MenuItem.doClick();
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
    private void initCodes(boolean initUserCodes) {
        InputStream stream;
        
        stream = getClass().getResourceAsStream("part1_solution_recursion_code.txt");
        part1SolutionRecursionCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part1_solution_iteration_code.txt");
        part1SolutionIterationCode = LessonHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part2_solution_recursion_code.txt");
        part2SolutionRecursionCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part2_solution_iteration_code.txt");
        part2SolutionIterationCode = LessonHelper.readFile(stream);

        if (initUserCodes) {
            stream = getClass().getResourceAsStream("part1_user_recursion_code.txt");
            part1UserRecursionCode = LessonHelper.readFile(stream);
            
            stream = getClass().getResourceAsStream("part1_user_iteration_code.txt");
            part1UserIterationCode = LessonHelper.readFile(stream);

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
                if ( state.id < State.Part1PseudocodeShown.id ) {
                    state = State.Part1PseudocodeShown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part1UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserRecursionCodeMenuItem);
        part1UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == ChosenCode.UserRecursion) {
                    return;
                }
                part1RememberCode();
                mainClass.getEditor().setCode(part1UserRecursionCode);
                part1ChosenCode = ChosenCode.UserRecursion;
            }
        });
        
        part1UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part1UserIterationCodeMenuItem);
        part1UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == ChosenCode.UserIteration) {
                    return;
                }
                part1RememberCode();
                mainClass.getEditor().setCode(part1UserIterationCode);
                part1ChosenCode = ChosenCode.UserIteration;
            }
        });
        
        part1SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionRecursionCodeMenuItem);
        part1SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part1ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == ChosenCode.SolutionRecursion) {
                    return;
                }
                
                part1RememberCode();
                mainClass.getEditor().setCode(part1SolutionRecursionCode);
                part1ChosenCode = ChosenCode.SolutionRecursion;

                if (state.id <= State.Part1PseudocodeShown.id) {
                    state = State.Part1RecursionSolutionShown;
                } else if (state == State.Part1IterationSolutionShown) {
                    state = State.Part1BothSolutionShown;
                }
            }
        });
        
        part1SolutionIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part1SolutionIterationCodeMenuItem);
        part1SolutionIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part1ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == ChosenCode.SolutionIteration) {
                    return;
                }
                
                part1RememberCode();
                mainClass.getEditor().setCode(part1SolutionIterationCode);
                part1ChosenCode = ChosenCode.SolutionIteration;

                if (state.id <= State.Part1PseudocodeShown.id) {
                    state = State.Part1IterationSolutionShown;
                } else if (state == State.Part1RecursionSolutionShown) {
                    state = State.Part1BothSolutionShown;
                }
            }
        });
        
        part1GotoPart2MenuItem = new JMenuItem(Lang.part1GotoPart2MenuItem);
        part1GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showNextPartConifrm, State.Part1BothSolutionShown)) {
                    return;
                }
                part1RememberCode();
                textFrame.gotoPart(2);
                initPart2();
                if (state.id < State.Part2Shown.id) {
                    state = State.Part2Shown;
                    textFrame.gotoText();
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1UserRecursionCodeMenuItem);
        group.add(part1UserIterationCodeMenuItem);
        group.add(part1SolutionRecursionCodeMenuItem);
        group.add(part1SolutionIterationCodeMenuItem);
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
                if ( state.id < State.Part2Hint1Shown.id ) {
                    state = State.Part2Hint1Shown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part2Hint2MenuItem = new JMenuItem(Lang.part2Hint2MenuItem);
        part2Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part2Hint1Shown)) {
                    return;
                }
                if ( state.id < State.Part2Hint2Shown.id ) {
                    state = State.Part2Hint2Shown;
                }
                textFrame.gotoHint(2);
            }
        });
        
        part2PseudocodeMenuItem = new JMenuItem(Lang.part2PseudocodeMenuItem);
        part2PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part2Hint2Shown)) {
                    return;
                }
                if ( state.id < State.Part2PseudocodeShown.id ) {
                    state = State.Part2PseudocodeShown;
                }
                textFrame.gotoHint(3);
            }
        });
        

        part2UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserRecursionCodeMenuItem);
        part2UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == ChosenCode.UserRecursion) {
                    return;
                }
                part2RememberCode();
                mainClass.getEditor().setCode(part2UserRecursionCode);
                part2ChosenCode = ChosenCode.UserRecursion;
            }
        });
        
        part2UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserIterationCodeMenuItem);
        part2UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == ChosenCode.UserIteration) {
                    return;
                }
                part2RememberCode();
                mainClass.getEditor().setCode(part2UserIterationCode);
                part2ChosenCode = ChosenCode.UserIteration;
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
                if (part2ChosenCode == ChosenCode.SolutionRecursion) {
                    return;
                }
                
                part2RememberCode();
                mainClass.getEditor().setCode(part2SolutionRecursionCode);
                part2ChosenCode = ChosenCode.SolutionRecursion;

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
                if (part2ChosenCode == ChosenCode.SolutionIteration) {
                    return;
                }
                
                part2RememberCode();
                mainClass.getEditor().setCode(part2SolutionIterationCode);
                part2ChosenCode = ChosenCode.SolutionIteration;

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
        lessonMenu.add(part1UserRecursionCodeMenuItem);        
        lessonMenu.add(part1UserIterationCodeMenuItem);
        lessonMenu.add(part1SolutionRecursionCodeMenuItem);
        lessonMenu.add(part1SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1GotoPart2MenuItem);

        lessonMenu.setEnabled(true);
        
        if (part1ChosenCode == ChosenCode.UserRecursion) {
            mainClass.getEditor().setCode(part1UserRecursionCode);
            part1UserRecursionCodeMenuItem.setSelected(true);
        } else if (part1ChosenCode == ChosenCode.UserIteration) {
            mainClass.getEditor().setCode(part1UserIterationCode);
            part1UserIterationCodeMenuItem.setSelected(true);
        } else if (part1ChosenCode == ChosenCode.SolutionRecursion) {
            mainClass.getEditor().setCode(part1SolutionRecursionCode);
            part1SolutionRecursionCodeMenuItem.setSelected(true);
        } else if (part1ChosenCode == ChosenCode.SolutionIteration) {
            mainClass.getEditor().setCode(part1SolutionIterationCode);
            part1SolutionIterationCodeMenuItem.setSelected(true);
        }
        
        startSpecialFunction.setSelectedPart((byte)1);
        exponentiationSpecialFunction.setSelectedPart((byte)1);
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(13);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        selectedPart = 2;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2Hint1MenuItem);
        lessonMenu.add(part2Hint2MenuItem);
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
        
        if (part2ChosenCode == ChosenCode.UserRecursion) {
            mainClass.getEditor().setCode(part2UserRecursionCode);
            part2UserRecursionCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == ChosenCode.UserIteration) {
            mainClass.getEditor().setCode(part2UserIterationCode);
            part2UserIterationCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == ChosenCode.SolutionRecursion) {
            mainClass.getEditor().setCode(part2SolutionRecursionCode);
            part2SolutionRecursionCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == ChosenCode.SolutionIteration) {
            mainClass.getEditor().setCode(part2SolutionIterationCode);
            part2SolutionIterationCodeMenuItem.setSelected(true);
        }
        
        startSpecialFunction.setSelectedPart((byte)2);
        exponentiationSpecialFunction.setSelectedPart((byte)2);
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(14);
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
        
        stream.writeByte(part1ChosenCode.Id);
        stream.writeByte(part2ChosenCode.Id);
        
        if (selectedPart == 1) {
            part1RememberCode();
        } else if (selectedPart == 2) {
            part2RememberCode();
        }
        
        stream.writeUTF(part1UserRecursionCode);
        stream.writeUTF(part1UserIterationCode);
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
        startSpecialFunction.threadStart();
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
        public static final String part1UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part1UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part1SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part1SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part1GotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2Hint1MenuItem = "Wskazówka I";
        public static final String part2Hint2MenuItem = "Wskazówka II";
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
                "Nie wyświetliłeś jeszcze wszystkich rozwiązań wzorcowych.\n"
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

