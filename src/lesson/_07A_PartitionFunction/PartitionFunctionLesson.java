package lesson._07A_PartitionFunction;

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

class PartitionFunctionLesson implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        NothingShown(0), 
        Part1PseudocodeShown(1), Part1SolutionShown(2), 
        Part2Shown(3),
        Part2Hint1Shown(4), Part2Hint2Shown(5),
        Part2PseudocodeShown(6), Part2SolutionShown(7), 
        Part3Shown(8),
        Part3Hint1Shown(9), Part3Hint2Shown(10), 
        Part3PseudocodeShown(11), Part3SolutionShown(12), 
        SummaryShown(13);
        public final byte Id;

        State(int id) {
            Id = (byte) id;
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 13;
            
            State[] values = values();
            if ( values[id].Id == id ) {
                return values[id];
            }
            for (State state : values) {
                if ( state.Id == id ) {
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
    
    //<editor-fold defaultstate="collapsed" desc="Variables and components">
    private State state = State.NothingShown;
    private ChosenCode part1ChosenCode = ChosenCode.User;
    private ChosenCode part2ChosenCode = ChosenCode.User;
    private ChosenCode part3ChosenCode = ChosenCode.User;
    private byte selectedPart = 1;
    
    private MainClass mainClass;
    private PartitionFunctionLessonLoader loader;
    
    private TextFrame textFrame;
    private ArrayFrame arrayFrame;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1FunctionsMenuItem;
    private JMenuItem part1PseudocodeMenuItem;
    private JRadioButtonMenuItem part1UserCodeMenuItem;
    private JRadioButtonMenuItem part1SolutionCodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2FunctionsMenuItem;
    private JMenuItem part2Hint1MenuItem;
    private JMenuItem part2Hint2MenuItem;
    private JMenuItem part2PseudocodeMenuItem;
    private JRadioButtonMenuItem part2UserCodeMenuItem;
    private JRadioButtonMenuItem part2SolutionCodeMenuItem;
    private JMenuItem part2GotoPart1MenuItem;
    private JMenuItem part2GotoPart3MenuItem;
    
    private JMenuItem part3TextMenuItem;
    private JMenuItem part3FunctionsMenuItem;
    private JMenuItem part3Hint1MenuItem;
    private JMenuItem part3Hint2MenuItem;
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
    
    private StartSpecialFunction startSpecialFunction;
    private CheckSpecialFunction checkSpecialFunction;
    private CompareOneSpecialFunction compareOneSpecialFunction;
    private CompareTwoSpecialFunction compareTwoSpecialFunction;
    private MoveSpecialFunction moveSpecialFunction;
    private SwapSpecialFunction swapSpecialFunction;
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public PartitionFunctionLesson(MainClass mainClass, DataInputStream stream,
            PartitionFunctionLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        arrayFrame = new ArrayFrame(mainClass);
        
        initSpecialFunctions();
        
        initPart1MenuItems();
        initPart2MenuItems();
        initPart3MenuItems();
        
        if (stream == null) {
            initCodes(true);
            initPart1();
            
            textFrame.gotoText();
            arrayFrame.frameToFront();
        } else {
            initCodes(false);
            mainClass.loadFramesPositionAndSettnings(stream);
            
            selectedPart = stream.readByte();
            state = State.getById(stream.readByte());
            
            part1ChosenCode = ChosenCode.getById(stream.readByte());
            part2ChosenCode = ChosenCode.getById(stream.readByte());
            part3ChosenCode = ChosenCode.getById(stream.readByte());
            
            part1UserCode = stream.readUTF();
            part2UserCode = stream.readUTF();
            part3UserCode = stream.readUTF();
            
            switch (selectedPart) {
                case 1:
                    initPart1();
                    break;
                case 2:
                    initPart2();
                    break;
                case 3:
                    initPart3();
                    break;
                default:
                    throw new AssertionError();
            }
            
            TextFrame.State textState = TextFrame.State.Nothing;
            byte shownPart;
            if (state.Id < State.Part2Shown.Id) {
                shownPart = 1;
                if (state.Id >= State.Part1PseudocodeShown.Id) {
                    textState = TextFrame.State.Hint1;
                }
            } else if (state.Id < State.Part3Shown.Id) {
                shownPart = 2;
                if (state.Id >= State.Part2PseudocodeShown.Id) {
                    textState = TextFrame.State.Hint3;
                } else if (state == State.Part2Hint2Shown) {
                    textState = TextFrame.State.Hint2;
                } else if (state == State.Part2Hint1Shown) {
                    textState = TextFrame.State.Hint1;
                }
            } else {
                shownPart = 3;
                if (state == State.SummaryShown) {
                    textState = TextFrame.State.Summary;
                } else if (state.Id >= State.Part3PseudocodeShown.Id) {
                    textState = TextFrame.State.Hint3;
                } else if (state == State.Part3Hint2Shown) {
                    textState = TextFrame.State.Hint2;
                } else if (state == State.Part3Hint1Shown) {
                    textState = TextFrame.State.Hint1;
                }
            }
            textFrame.initialize(selectedPart, shownPart, textState);
            textFrame.gotoText();
        }
    }
    //</editor-fold>
    
    
    //private fucntions:
    
    //<editor-fold defaultstate="collapsed" desc="showNextHint">
    private void showNextHint() {
        if (selectedPart == 1) {
            part1PseudocodeMenuItem.doClick();
        } else if (selectedPart == 2) {
            if (state.Id < State.Part2Hint1Shown.Id) {
                part2Hint1MenuItem.doClick();
            } else if (state == State.Part2Hint1Shown) {
                part2Hint2MenuItem.doClick();
            } else {
                part2PseudocodeMenuItem.doClick();
            }
        } else {
            if (state.Id < State.Part3Hint1Shown.Id) {
                part3Hint1MenuItem.doClick();
            } else if (state == State.Part3Hint1Shown) {
                part3Hint2MenuItem.doClick();
            } else {
                part3PseudocodeMenuItem.doClick();
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showSolutionConifrm">
    private boolean showSolutionConifrm(State state, JRadioButtonMenuItem menuItem, int showHintNr) {
        if (this.state.Id >= state.Id) {
            return true;
        }
        int option = JOptionPane.showOptionDialog(
                null, Lang.showSolutionConifrm, Lang.question,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{Lang.showSolution, Lang.showHint, Lang.cancel},
                Lang.showHint);
        if (option != 0) {
            menuItem.setSelected(true);
            if ( option == 1 ) {
                showNextHint();
            }
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
    private void initCodes(boolean initUserCodes) {
        InputStream stream;
        stream = getClass().getResourceAsStream("part1_solution_code.txt");
        part1SolutionCode = LessonHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part2_solution_code.txt");
        part2SolutionCode = LessonHelper.readFile(stream);

        stream = getClass().getResourceAsStream("part3_solution_code.txt");
        part3SolutionCode = LessonHelper.readFile(stream);

        if (initUserCodes) {
            stream = getClass().getResourceAsStream("part1_user_code.txt");
            part1UserCode = LessonHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part2_user_code.txt");
            part2UserCode = LessonHelper.readFile(stream);

            stream = getClass().getResourceAsStream("part3_user_code.txt");
            part3UserCode = LessonHelper.readFile(stream);
        }
        oldCode = mainClass.getEditor().getCode();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initSpecialFunctions">
    private void initSpecialFunctions() {
        startSpecialFunction = new StartSpecialFunction(arrayFrame);
        checkSpecialFunction = new CheckSpecialFunction(mainClass, arrayFrame);
        compareOneSpecialFunction = new CompareOneSpecialFunction(arrayFrame);
        compareTwoSpecialFunction = new CompareTwoSpecialFunction(arrayFrame);
        moveSpecialFunction = new MoveSpecialFunction(arrayFrame);
        swapSpecialFunction = new SwapSpecialFunction(arrayFrame);
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
        
        part1FunctionsMenuItem = new JMenuItem(Lang.part1FunctionsMenuItem);
        part1FunctionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoFunctions();
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
                if ( part1ChosenCode == ChosenCode.User ) {
                    part1UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPart(2);
                initPart2();
                if (state.Id < State.Part2Shown.Id) {
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
        
        part2FunctionsMenuItem = new JMenuItem(Lang.part2FunctionsMenuItem);
        part2FunctionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoFunctions();
            }
        });
        
        part2Hint1MenuItem = new JMenuItem(Lang.part2Hint1MenuItem);
        part2Hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part2Hint1Shown.Id ) {
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
                if ( state.Id < State.Part2Hint2Shown.Id ) {
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
                if ( part2ChosenCode == ChosenCode.User ) {
                    part2UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPart(1);
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
                if ( part2ChosenCode == ChosenCode.User ) {
                    part2UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPart(3);
                initPart3();
                if (state.Id < State.Part3Shown.Id) {
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
        
        part3FunctionsMenuItem = new JMenuItem(Lang.part3FunctionsMenuItem);
        part3FunctionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoFunctions();
            }
        });
        
        part3Hint1MenuItem = new JMenuItem(Lang.part3Hint1MenuItem);
        part3Hint1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( state.Id < State.Part3Hint1Shown.Id ) {
                    state = State.Part3Hint1Shown;
                }
                textFrame.gotoHint(1);
            }
        });
        
        part3Hint2MenuItem = new JMenuItem(Lang.part3Hint2MenuItem);
        part3Hint2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part3Hint1Shown)) {
                    return;
                }
                if ( state.Id < State.Part3Hint2Shown.Id ) {
                    state = State.Part3Hint2Shown;
                }
                textFrame.gotoHint(2);
            }
        });
        
        part3PseudocodeMenuItem = new JMenuItem(Lang.part3PseudocodeMenuItem);
        part3PseudocodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showHintConifrm, State.Part3Hint2Shown)) {
                    return;
                }
                if ( state.Id < State.Part3PseudocodeShown.Id ) {
                    state = State.Part3PseudocodeShown;
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
                if (!showSolutionConifrm(State.Part3PseudocodeShown, part3UserCodeMenuItem, 3)) {
                    return;
                }
                //TODO editor to front
                if (part3ChosenCode == ChosenCode.Solution) {
                    return;
                }
                part3UserCode = mainClass.getEditor().getCode();
                mainClass.getEditor().setCode(part3SolutionCode);
                part3ChosenCode = ChosenCode.Solution;
                if ( state.Id < State.Part3SolutionShown.Id ) {
                    state = State.Part3SolutionShown;
                }
            }
        });
        
        part3SummaryMenuItem = new JMenuItem(Lang.part3SummaryMenuItem);
        part3SummaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showConifrmDialog(Lang.showSummaryConifrm, State.Part3SolutionShown)) {
                    return;
                }
                if ( state.Id < State.SummaryShown.Id ) {
                    state = State.SummaryShown;
                }
                textFrame.gotoSummary();
            }
        });
        
        part3GotoPart2MenuItem = new JMenuItem(Lang.part3GotoPart2MenuItem);
        part3GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( part3ChosenCode == ChosenCode.User ) {
                    part3UserCode = mainClass.getEditor().getCode();
                }
                textFrame.gotoPart(2);
                initPart2();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part3UserCodeMenuItem);
        group.add(part3SolutionCodeMenuItem);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1() {
        selectedPart = 1;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(part1FunctionsMenuItem);
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
        
        arrayFrame.setSelectedPart(1);
        
        SpecialFunctions.clear();
        compareOneSpecialFunction.setSelectedPart(1);
        SpecialFunctions.addSpecialFunction(startSpecialFunction);
        SpecialFunctions.addSpecialFunction(checkSpecialFunction);
        SpecialFunctions.addSpecialFunction(compareOneSpecialFunction);
        SpecialFunctions.addSpecialFunction(moveSpecialFunction);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        selectedPart = 2;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(part2FunctionsMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2Hint1MenuItem);
        lessonMenu.add(part2Hint2MenuItem);
        lessonMenu.add(part2PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2UserCodeMenuItem);
        lessonMenu.add(part2SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2GotoPart1MenuItem);
        lessonMenu.add(part2GotoPart3MenuItem);

        lessonMenu.setEnabled(true);
        
        if (part2ChosenCode == ChosenCode.User) {
            mainClass.getEditor().setCode(part2UserCode);
            part2UserCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(part2SolutionCode);
            part2SolutionCodeMenuItem.setSelected(true);
        }
        
        arrayFrame.setSelectedPart(2);
        
        SpecialFunctions.clear();
        compareOneSpecialFunction.setSelectedPart(2);
        SpecialFunctions.addSpecialFunction(startSpecialFunction);
        SpecialFunctions.addSpecialFunction(checkSpecialFunction);
        SpecialFunctions.addSpecialFunction(compareOneSpecialFunction);
        SpecialFunctions.addSpecialFunction(moveSpecialFunction);
        SpecialFunctions.addSpecialFunction(swapSpecialFunction);
    }
    //</editor-fold>    
    
    //<editor-fold defaultstate="collapsed" desc="initPart3">
    private void initPart3() {
        selectedPart = 3;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part3TextMenuItem);
        lessonMenu.add(part3FunctionsMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3Hint1MenuItem);
        lessonMenu.add(part3Hint2MenuItem);
        lessonMenu.add(part3PseudocodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3UserCodeMenuItem);
        lessonMenu.add(part3SolutionCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3SummaryMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3GotoPart2MenuItem);

        lessonMenu.setEnabled(true);
        
        if (part3ChosenCode == ChosenCode.User) {
            mainClass.getEditor().setCode(part3UserCode);
            part3UserCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(part3SolutionCode);
            part3SolutionCodeMenuItem.setSelected(true);
        }
        
        arrayFrame.setSelectedPart(3);
        
        SpecialFunctions.clear();
        SpecialFunctions.addSpecialFunction(startSpecialFunction);
        SpecialFunctions.addSpecialFunction(checkSpecialFunction);
        SpecialFunctions.addSpecialFunction(compareTwoSpecialFunction);
        SpecialFunctions.addSpecialFunction(swapSpecialFunction);
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
        stream.writeByte(state.Id);
        
        stream.writeByte(part1ChosenCode.Id);
        stream.writeByte(part2ChosenCode.Id);
        stream.writeByte(part3ChosenCode.Id);
        
        if (selectedPart == 1) {
            if (part1ChosenCode == ChosenCode.User) {
                part1UserCode = mainClass.getEditor().getCode();
            }
        } else if (selectedPart == 2) {
            if (part2ChosenCode == ChosenCode.User) {
                part2UserCode = mainClass.getEditor().getCode();
            }
        } else {
            if (part3ChosenCode == ChosenCode.User) {
                part3UserCode = mainClass.getEditor().getCode();
            }
        }
        
        stream.writeUTF(part1UserCode);
        stream.writeUTF(part2UserCode);
        stream.writeUTF(part3UserCode);
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
        arrayFrame.threadStart();
        
        if ( selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(false);
            part1UserCodeMenuItem.setEnabled(false);
            part1SolutionCodeMenuItem.setEnabled(false);
        } else if (selectedPart == 2) {
            part2GotoPart1MenuItem.setEnabled(false);
            part2GotoPart3MenuItem.setEnabled(false);
            part2UserCodeMenuItem.setEnabled(false);
            part2SolutionCodeMenuItem.setEnabled(false);
        } else {
            part3GotoPart2MenuItem.setEnabled(false);
            part3UserCodeMenuItem.setEnabled(false);
            part3SolutionCodeMenuItem.setEnabled(false);
        }
    }
    
    @Override
    public void threadStop() {
        arrayFrame.threadStop();
        
        if (selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(true);
            part1UserCodeMenuItem.setEnabled(true);
            part1SolutionCodeMenuItem.setEnabled(true);
        } else if (selectedPart == 2) {
            part2GotoPart1MenuItem.setEnabled(true);
            part2GotoPart3MenuItem.setEnabled(true);
            part2UserCodeMenuItem.setEnabled(true);
            part2SolutionCodeMenuItem.setEnabled(true);
        } else {
            part3GotoPart2MenuItem.setEnabled(true);
            part3UserCodeMenuItem.setEnabled(true);
            part3SolutionCodeMenuItem.setEnabled(true);
        }
    }
    
    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, final int delayTime) {
        compareTwoSpecialFunction.undo(node);
        moveSpecialFunction.undo(node);
        compareOneSpecialFunction.undo(node);
        swapSpecialFunction.undo(node);
        
        arrayFrame.updateArrays();
        
        boolean wait = moveSpecialFunction.pauseStart(node, delayTime);
        wait = wait && swapSpecialFunction.pauseStart(node, delayTime);
        if ( !wait ) {
            return false;
        }
        compareOneSpecialFunction.pauseStart(node);
        compareTwoSpecialFunction.pauseStart(node);
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
        compareOneSpecialFunction.pauseStop();
        compareTwoSpecialFunction.pauseStop();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String part1TextMenuItem = "Treść zadania";
        public static final String part1FunctionsMenuItem = "Funkcje specjalne";
        public static final String part1PseudocodeMenuItem = "Pseudokod";
        public static final String part1UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part1SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part1GotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2FunctionsMenuItem = "Funkcje specjalne";
        public static final String part2Hint1MenuItem = "Wskazówka I";
        public static final String part2Hint2MenuItem = "Wskazówka II";
        public static final String part2PseudocodeMenuItem = "Wskazówka III: pseudokod";
        public static final String part2UserCodeMenuItem = "Rozwiązanie użytkownika";
        public static final String part2SolutionCodeMenuItem = "Rozwiązanie wzorcowe";
        public static final String part2GotoPart1MenuItem = "<<< Część I";
        public static final String part2GotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania";
        public static final String part3FunctionsMenuItem = "Funkcje specjalne";
        public static final String part3Hint1MenuItem = "Wskazówka I";
        public static final String part3Hint2MenuItem = "Wskazówka II";
        public static final String part3PseudocodeMenuItem = "Wskazówka III: pseudokod";
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

