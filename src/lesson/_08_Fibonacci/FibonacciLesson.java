package lesson._08_Fibonacci;

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

class FibonacciLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public static enum State {
        NothingShown(0),
        Part2Shown(1),
        Part2HintShown(2),
        Part2PseudocodeShown(3),
        Part2SolutionShown(4),
        Part3Shown(5),
        Part3HintShown(6),
        Part3PseudocodeShown(7),
        Part3SolutionShown(8), 
        Part4Shown(9),
        Part4PseudocodeShown(10),
        Part4RecursionSolutionShown(11), Part4IterationSolutionShown(12),
        Part2BothSolutionShown(13);
        
        public final int id;
        public final int part;
        public final int hint;

        State(int id) {
            this.id = id;

            part = (id < 5 ? (id == 0 ? 1 : 2) : (id < 9 ? 3 : 4));

            if (id == 0) {
                hint = 0;
            } else if (id < 5) {
                hint = id >= 3 ? 2 : id - 1;
            } else if (id < 9) {
                hint = id >= 7 ? 2 : id - 5;
            } else {
                hint = id == 9 ? 0 : 1;
            }
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 13;
            
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
    
    private static enum Part2ChosenCode {
        User(0), Solution(1);
        
        public final int id;

        Part2ChosenCode(int id) {
            this.id = id;
        }
        
        public static Part2ChosenCode getById(int id) {
            assert id == 0 || id == 1;
            return id == 0 ? User : Solution;
        }
    }
    
    private static enum Part4ChosenCode {
        UserRecursion(0),
        UserIteration(1),
        SolutionRecursion(2),
        SolutionIteration(3);
        
        public final int id;

        Part4ChosenCode(int id) {
            this.id = id;
        }
        
        public static Part4ChosenCode getById(int id) {
            assert 0 <= id && id <= 3;
            return id < 2 ? (id == 0 ? UserRecursion : UserIteration)
                    : (id == 2 ? SolutionRecursion : SolutionIteration);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables and components">
    private State state = State.NothingShown;
    private Part2ChosenCode part2ChosenCode = Part2ChosenCode.User;
    private Part2ChosenCode part3ChosenCode = Part2ChosenCode.User;
    private Part4ChosenCode part4ChosenCode = Part4ChosenCode.UserRecursion;
    private int selectedPart = 1;
    
    private MainClass mainClass;
    private FibonacciLessonLoader loader;
    
    private TextFrame textFrame;
    
    private JMenuItem part1TextMenuItem;
    private JRadioButtonMenuItem part1CodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2HintMenuItem;
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
    private JMenuItem part3GotoPart4MenuItem;
    
    private JMenuItem part4TextMenuItem;
    private JMenuItem part4PseudocodeMenuItem;
    private JRadioButtonMenuItem part4UserRecursionCodeMenuItem;
    private JRadioButtonMenuItem part4UserIterationCodeMenuItem;
    private JRadioButtonMenuItem part4SolutionRecursionCodeMenuItem;
    private JRadioButtonMenuItem part4SolutionIterationCodeMenuItem;
    private JMenuItem part4GotoPart3MenuItem;
    
    
    private String oldCode;
    private String part1Code;
    private String part2UserCode;
    private String part2SolutionCode;
    private String part3UserCode;
    private String part3SolutionCode;
    private String part4UserRecursionCode;
    private String part4UserIterationCode;
    private String part4SolutionRecursionCode;
    private String part4SolutionIterationCode;
    
    private StartSpecialFunction startSpecialFunction;
    private FibonacciSpecialFunction fibonacciSpecialFunction;
    private CheckSpecialFunction checkSpecialFunction;
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public FibonacciLesson(MainClass mainClass, DataInputStream stream,
            FibonacciLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        mainClass.getLessonMenu().setEnabled(true);
        
        startSpecialFunction = new StartSpecialFunction();
        SpecialFunctions.add(startSpecialFunction);
        fibonacciSpecialFunction = new FibonacciSpecialFunction();
        SpecialFunctions.add(fibonacciSpecialFunction);
        checkSpecialFunction = new CheckSpecialFunction(startSpecialFunction, mainClass.getConsole());
        SpecialFunctions.add(checkSpecialFunction);
        
        initPart1MenuItems();
        initPart2MenuItems();
        initPart3MenuItems();
        initPart4MenuItems();
        
        if (stream == null) {
            initCodes(true);
            initPart1();
            textFrame.gotoText();
        } else {
            initCodes(false);
            mainClass.loadFramesPositionAndSettnings(stream);
            
            selectedPart = stream.readByte();
            state = State.getById(stream.readByte());
            
            part2ChosenCode = Part2ChosenCode.getById(stream.readByte());
            part3ChosenCode = Part2ChosenCode.getById(stream.readByte());
            part4ChosenCode = Part4ChosenCode.getById(stream.readByte());
            
            part2UserCode = stream.readUTF();
            part3UserCode = stream.readUTF();
            part4UserRecursionCode = stream.readUTF();
            part4UserIterationCode = stream.readUTF();
            
            if (selectedPart == 1) {
                initPart1();
            } else if (selectedPart == 2) {
                initPart2();
            } else if (selectedPart == 3) {
                initPart3();
            } else {
                initPart4();
            }
            
            textFrame.initialize(selectedPart, state);
        }
    }
    //</editor-fold>
    
    
    //private fucntions:
    
    //<editor-fold defaultstate="collapsed" desc="part4RememberCode">
    private void part4RememberCode() {
        if (part4ChosenCode == Part4ChosenCode.UserRecursion) {
            part4UserRecursionCode = mainClass.getEditor().getCode();
        } else if (part4ChosenCode == Part4ChosenCode.UserIteration) {
            part4UserIterationCode = mainClass.getEditor().getCode();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setButtonsEnabled">
    private void setButtonsEnabled(boolean enabled) {
        if (selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(enabled);
            part1CodeMenuItem.setEnabled(enabled);
        } else if (selectedPart == 2) {
            part2GotoPart1MenuItem.setEnabled(enabled);
            part2GotoPart3MenuItem.setEnabled(enabled);
            part2UserCodeMenuItem.setEnabled(enabled);
            part2SolutionCodeMenuItem.setEnabled(enabled);
        } else if (selectedPart == 3) {
            part3GotoPart2MenuItem.setEnabled(enabled);
            part3GotoPart4MenuItem.setEnabled(enabled);
            part3UserCodeMenuItem.setEnabled(enabled);
            part3SolutionCodeMenuItem.setEnabled(enabled);
        } else {
            part4GotoPart3MenuItem.setEnabled(enabled);
            part4UserRecursionCodeMenuItem.setEnabled(enabled);
            part4UserIterationCodeMenuItem.setEnabled(enabled);
            part4SolutionRecursionCodeMenuItem.setEnabled(enabled);
            part4SolutionIterationCodeMenuItem.setEnabled(enabled);
        }
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
            part2UserCodeMenuItem.setSelected(true);
            if (option == 1) {
                if ( state.id < State.Part2HintShown.id) {
                    part2HintMenuItem.doClick();
                } else {
                    part2PseudocodeMenuItem.doClick();
                }
            }
            return false;
        }
        textFrame.showAllHints();
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part3ShowSolutionConifrm">
    private boolean part3ShowSolutionConifrm() {
        if (state.id >= State.Part3PseudocodeShown.id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part2ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            part3UserCodeMenuItem.setSelected(true);
            if (option == 1) {
                if ( state.id < State.Part3HintShown.id) {
                    part3HintMenuItem.doClick();;
                } else {
                    part3PseudocodeMenuItem.doClick();
                }
            }
            return false;
        }
        textFrame.showAllHints();
        return true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="part4ShowSolutionConifrm">
    private boolean part4ShowSolutionConifrm() {
        if (state.id >= State.Part4PseudocodeShown.id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.part4ShowSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showPseudocode, Lang.cancel},
                Lang.showPseudocode);
        if (option != 0) {
            if (part4ChosenCode == Part4ChosenCode.UserIteration) {
                part4UserIterationCodeMenuItem.setSelected(true);
            } else {
                part4UserRecursionCodeMenuItem.setSelected(true);
            }
            if (option == 1) {
                part4PseudocodeMenuItem.doClick();
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
        
        stream = getClass().getResourceAsStream("part1_code.txt");
        part1Code = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part2_solution_code.txt");
        part2SolutionCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part3_solution_code.txt");
        part3SolutionCode = LessonHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part4_solution_recursion_code.txt");
        part4SolutionRecursionCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part4_solution_iteration_code.txt");
        part4SolutionIterationCode = LessonHelper.readFile(stream);

        if (initUserCodes) {
            stream = getClass().getResourceAsStream("part2_user_code.txt");
            part2UserCode = LessonHelper.readFile(stream);
            
            stream = getClass().getResourceAsStream("part3_user_code.txt");
            part3UserCode = LessonHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part4_user_recursion_code.txt");
            part4UserRecursionCode = LessonHelper.readFile(stream);
            
            stream = getClass().getResourceAsStream("part4_user_iteration_code.txt");
            part4UserIterationCode = LessonHelper.readFile(stream);
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
        
        part1CodeMenuItem = new JRadioButtonMenuItem(Lang.part1CodeMenuItem);
        part1CodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
            }
        });
        
        part1GotoPart2MenuItem = new JMenuItem(Lang.part1GotoPart2MenuItem);
        part1GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoNextPart();
                initPart2();
                if (state.id < State.Part2Shown.id) {
                    state = State.Part2Shown;
                    textFrame.gotoText();
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1CodeMenuItem);
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
        
        part2HintMenuItem = new JMenuItem(Lang.part2HintMenuItem);
        part2HintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoHint(1);
                if (state.id < State.Part2HintShown.id) {
                    state = State.Part2HintShown;
                }
            }
        });
        
        part2PseudocodeMenuItem = new JMenuItem(Lang.part2PseudocodeMenuItem);
        part2PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part2HintShown)) {
                    return;
                }
                textFrame.gotoHint(2);
                if (state.id < State.Part2PseudocodeShown.id) {
                    state = State.Part2PseudocodeShown;
                }
            }
        });
        
        part2UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part2UserCodeMenuItem);
        part2UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.User) {
                    return;
                }
                mainClass.getEditor().setCode(part2UserCode);
                part2ChosenCode = Part2ChosenCode.User;
            }
        });
        
        part2SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part2SolutionCodeMenuItem);
        part2SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part2ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part2ChosenCode == Part2ChosenCode.Solution) {
                    return;
                }
                
                part2UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part2SolutionCode);
                part2ChosenCode = Part2ChosenCode.Solution;

                if (state.id < State.Part2SolutionShown.id) {
                    state = State.Part2SolutionShown;
                }
            }
        });
        
        part2GotoPart1MenuItem = new JMenuItem(Lang.part2GotoPart1MenuItem);
        part2GotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (part2ChosenCode == Part2ChosenCode.User) {
                    part2UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPrevPart();
                initPart1();
            }
        });
        
        part2GotoPart3MenuItem = new JMenuItem(Lang.part2GotoPart3MenuItem);
        part2GotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showNextPartConifrm, State.Part2SolutionShown)) {
                    return;
                }
                if (part2ChosenCode == Part2ChosenCode.User) {
                    part2UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoNextPart();
                initPart3();
                if (state.id < State.Part3Shown.id) {
                    state = State.Part3Shown;
                    textFrame.gotoText();
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
                textFrame.gotoHint(1);
                if (state.id < State.Part3HintShown.id) {
                    state = State.Part3HintShown;
                }
            }
        });
        
        part3PseudocodeMenuItem = new JMenuItem(Lang.part3PseudocodeMenuItem);
        part3PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part3HintShown)) {
                    return;
                }
                textFrame.gotoHint(2);
                if (state.id < State.Part3PseudocodeShown.id) {
                    state = State.Part3PseudocodeShown;
                }
            }
        });
        
        part3UserCodeMenuItem = new JRadioButtonMenuItem(Lang.part3UserCodeMenuItem);
        part3UserCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part3ChosenCode == Part2ChosenCode.User) {
                    return;
                }
                mainClass.getEditor().setCode(part3UserCode);
                part3ChosenCode = Part2ChosenCode.User;
            }
        });
        
        part3SolutionCodeMenuItem = new JRadioButtonMenuItem(Lang.part3SolutionCodeMenuItem);
        part3SolutionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part3ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part3ChosenCode == Part2ChosenCode.Solution) {
                    return;
                }
                
                part3UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part3SolutionCode);
                part3ChosenCode = Part2ChosenCode.Solution;

                if (state.id < State.Part3SolutionShown.id) {
                    state = State.Part3SolutionShown;
                }
            }
        });
        
        part3GotoPart2MenuItem = new JMenuItem(Lang.part3GotoPart2MenuItem);
        part3GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (part3ChosenCode == Part2ChosenCode.User) {
                    part3UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPrevPart();
                initPart2();
            }
        });
        
        part3GotoPart4MenuItem = new JMenuItem(Lang.part3GotoPart4MenuItem);
        part3GotoPart4MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showNextPartConifrm, State.Part3SolutionShown)) {
                    return;
                }
                if (part3ChosenCode == Part2ChosenCode.User) {
                    part3UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoNextPart();
                initPart4();
                if (state.id < State.Part4Shown.id) {
                    state = State.Part4Shown;
                    textFrame.gotoText();
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part3UserCodeMenuItem);
        group.add(part3SolutionCodeMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart4MenuItems">
    private void initPart4MenuItems() {
        part4TextMenuItem = new JMenuItem(Lang.part4TextMenuItem);
        part4TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        
        part4PseudocodeMenuItem = new JMenuItem(Lang.part4PseudocodeMenuItem);
        part4PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoHint(1);
                if (state.id < State.Part4PseudocodeShown.id) {
                    state = State.Part4PseudocodeShown;
                }
            }
        });
        
        part4UserRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part4UserRecursionCodeMenuItem);
        part4UserRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part4ChosenCode == Part4ChosenCode.UserRecursion) {
                    return;
                }
                part4RememberCode();
                mainClass.getEditor().setCode(part4UserRecursionCode);
                part4ChosenCode = Part4ChosenCode.UserRecursion;
            }
        });
        
        part4UserIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part4UserIterationCodeMenuItem);
        part4UserIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part4ChosenCode == Part4ChosenCode.UserIteration) {
                    return;
                }
                part4RememberCode();
                mainClass.getEditor().setCode(part4UserIterationCode);
                part4ChosenCode = Part4ChosenCode.UserIteration;
            }
        });
        
        part4SolutionRecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part4SolutionRecursionCodeMenuItem);
        part4SolutionRecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part4ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part4ChosenCode == Part4ChosenCode.SolutionRecursion) {
                    return;
                }
                
                part4RememberCode();
                mainClass.getEditor().setCode(part4SolutionRecursionCode);
                part4ChosenCode = Part4ChosenCode.SolutionRecursion;

                if (state.id <= State.Part4PseudocodeShown.id) {
                    state = State.Part4RecursionSolutionShown;
                } else if (state == State.Part4IterationSolutionShown) {
                    state = State.Part2BothSolutionShown;
                }
            }
        });
        
        part4SolutionIterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part4SolutionIterationCodeMenuItem);
        part4SolutionIterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!part4ShowSolutionConifrm()) {
                    return;
                }
                mainClass.getEditor().frameToFront();
                if (part4ChosenCode == Part4ChosenCode.SolutionIteration) {
                    return;
                }
                
                part4RememberCode();
                mainClass.getEditor().setCode(part4SolutionIterationCode);
                part4ChosenCode = Part4ChosenCode.SolutionIteration;

                if (state.id <= State.Part4PseudocodeShown.id) {
                    state = State.Part4IterationSolutionShown;
                } else if (state == State.Part4RecursionSolutionShown) {
                    state = State.Part2BothSolutionShown;
                }
            }
        });
        
        part4GotoPart3MenuItem = new JMenuItem(Lang.part4GotoPart3MenuItem);
        part4GotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part4RememberCode();
                textFrame.gotoPrevPart();
                initPart3();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part4UserRecursionCodeMenuItem);
        group.add(part4UserIterationCodeMenuItem);
        group.add(part4SolutionRecursionCodeMenuItem);
        group.add(part4SolutionIterationCodeMenuItem);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1() {
        selectedPart = 1;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1CodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1GotoPart2MenuItem);

        mainClass.getEditor().setCode(part1Code);
        part1CodeMenuItem.setSelected(true);
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(5);
        
        startSpecialFunction.setSelectedPart(1);
        fibonacciSpecialFunction.setSelectedPart(1);
        checkSpecialFunction.setSelectedPart(1);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        selectedPart = 2;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2HintMenuItem);
        lessonMenu.add(part2PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2UserCodeMenuItem);
        lessonMenu.add(part2SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2GotoPart1MenuItem);
        lessonMenu.add(part2GotoPart3MenuItem);

        if (part2ChosenCode == Part2ChosenCode.User) {
            mainClass.getEditor().setCode(part2UserCode);
            part2UserCodeMenuItem.setSelected(true);
        } else if (part2ChosenCode == Part2ChosenCode.Solution) {
            mainClass.getEditor().setCode(part2SolutionCode);
            part2SolutionCodeMenuItem.setSelected(true);
        }
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(5);
        
        startSpecialFunction.setSelectedPart(2);
        fibonacciSpecialFunction.setSelectedPart(2);
        checkSpecialFunction.setSelectedPart(2);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart3">
    private void initPart3() {
        selectedPart = 3;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part3TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3HintMenuItem);
        lessonMenu.add(part3PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3UserCodeMenuItem);
        lessonMenu.add(part3SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3GotoPart2MenuItem);
        lessonMenu.add(part3GotoPart4MenuItem);

        if (part3ChosenCode == Part2ChosenCode.User) {
            mainClass.getEditor().setCode(part3UserCode);
            part3UserCodeMenuItem.setSelected(true);
        } else if (part3ChosenCode == Part2ChosenCode.Solution) {
            mainClass.getEditor().setCode(part3SolutionCode);
            part3SolutionCodeMenuItem.setSelected(true);
        }
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(5);
        
        startSpecialFunction.setSelectedPart(3);
        fibonacciSpecialFunction.setSelectedPart(3);
        checkSpecialFunction.setSelectedPart(3);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart4">
    private void initPart4() {
        selectedPart = 4;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part4TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part4PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part4UserRecursionCodeMenuItem);
        lessonMenu.add(part4UserIterationCodeMenuItem);
        lessonMenu.add(part4SolutionRecursionCodeMenuItem);
        lessonMenu.add(part4SolutionIterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part4GotoPart3MenuItem);
        
        if (part4ChosenCode == Part4ChosenCode.UserRecursion) {
            mainClass.getEditor().setCode(part4UserRecursionCode);
            part4UserRecursionCodeMenuItem.setSelected(true);
        } else if (part4ChosenCode == Part4ChosenCode.UserIteration) {
            mainClass.getEditor().setCode(part4UserIterationCode);
            part4UserIterationCodeMenuItem.setSelected(true);
        } else if (part4ChosenCode == Part4ChosenCode.SolutionRecursion) {
            mainClass.getEditor().setCode(part4SolutionRecursionCode);
            part4SolutionRecursionCodeMenuItem.setSelected(true);
        } else if (part4ChosenCode == Part4ChosenCode.SolutionIteration) {
            mainClass.getEditor().setCode(part4SolutionIterationCode);
            part4SolutionIterationCodeMenuItem.setSelected(true);
        }
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(7);
        
        startSpecialFunction.setSelectedPart(4);
        fibonacciSpecialFunction.setSelectedPart(4);
        checkSpecialFunction.setSelectedPart(4);
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
        
        stream.writeByte(part2ChosenCode.id);
        stream.writeByte(part3ChosenCode.id);
        stream.writeByte(part4ChosenCode.id);

        if (selectedPart == 2) {
            if (part2ChosenCode == Part2ChosenCode.User) {
                part2UserCode = mainClass.getEditor().getCode();
            }
        } else if (selectedPart == 3) {
            if (part3ChosenCode == Part2ChosenCode.User) {
                part3UserCode = mainClass.getEditor().getCode();
            }
        } else if (selectedPart == 4) {
            part4RememberCode();
        }

        stream.writeUTF(part2UserCode);
        stream.writeUTF(part3UserCode);
        stream.writeUTF(part4UserRecursionCode);
        stream.writeUTF(part4UserIterationCode);
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
        fibonacciSpecialFunction.threadStart();
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
        public static final String part1CodeMenuItem = "Funkcja „int fibonacci(n)”";
        public static final String part1GotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2HintMenuItem = "Wskazówka";
        public static final String part2PseudocodeMenuItem = "Pseudokod";
        public static final String part2UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part2SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part2GotoPart1MenuItem = "<<< Część I";
        public static final String part2GotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania";
        public static final String part3HintMenuItem = "Wskazówka";
        public static final String part3PseudocodeMenuItem = "Pseudokod";
        public static final String part3UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part3SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part3GotoPart2MenuItem = "<<< Część II";
        public static final String part3GotoPart4MenuItem = ">>> Część IV";
        
        public static final String part4TextMenuItem = "Treść zadania";
        public static final String part4PseudocodeMenuItem = "Pseudokod";
        public static final String part4UserRecursionCodeMenuItem = "Rekurencyjne rozwiązanie użytkownika";
        public static final String part4UserIterationCodeMenuItem = "Iteracyjne rozwiązanie użytkownika";
        public static final String part4SolutionRecursionCodeMenuItem = "Rekurencyjne rozwiązanie wzorcowe";
        public static final String part4SolutionIterationCodeMenuItem = "Iteracyjne rozwiązanie wzorcowe";
        public static final String part4GotoPart3MenuItem = "<<< Część III";
        
        public static final String part2ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to najpierw powinieneś zobaczyć wszystkie wskazówki.";
        
        public static final String part4ShowSolutionConifrm = 
                "Czy na pewno chesz zobaczyć rozwiązanie?\n"
                + "Jeśli nie możesz poradzić sobie z rozwiązaniem tego zadania,\n"
                + "to dostępna jest wskazówka w postaci pseudokodów.";
        
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

