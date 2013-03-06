package lesson._02A_RecursionIntro;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._07A_PartitionFunction.*;
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

class RecursionIntroLesson2 implements Lesson {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        NothingShown(0), Part2Shown(1), Part3Shown(2);
        public final byte Id;

        State(int id) {
            Id = (byte) id;
        }
        
        public static State getById(int id) {
            assert 0 <= id && id <= 2;
            return id == 0 ? NothingShown : id == 1 ? Part2Shown : Part3Shown;
        }
    }
    
    private static enum Part1ChosenCode {
        Recursion(0), Iteration(1);
        public final byte Id;

        Part1ChosenCode(int id) {
            Id = (byte) id;
        }
        
        public static Part1ChosenCode getById(int id) {
            assert id == 0 || id == 1;
            return id == 0 ? Recursion : Iteration;
        }
    }
    
    private static enum Part3ChosenCode {
        WithoutStop(0), WithStop(1);
        public final byte Id;

        Part3ChosenCode(int id) {
            Id = (byte) id;
        }
        
        public static Part3ChosenCode getById(int id) {
            assert id == 0 || id == 1;
            return id == 0 ? WithoutStop : WithStop;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables and components">
    private State state = State.NothingShown;
    private Part1ChosenCode part1ChosenCode = Part1ChosenCode.Recursion;
    private Part3ChosenCode part3ChosenCode = Part3ChosenCode.WithoutStop;
    private byte selectedPart = 1;
    
    private MainClass mainClass;
    private RecursionIntroLessonLoader loader;
    
    private TextFrame textFrame;
    
    
    private JMenuItem part1TextMenuItem;
    private JRadioButtonMenuItem part1RecursionCodeMenuItem;
    private JRadioButtonMenuItem part1IterationCodeMenuItem;
    private JMenuItem part1GotoPart2MenuItem;
    
    private JMenuItem part2TextMenuItem;
    private JRadioButtonMenuItem part2CodeMenuItem;
    private JMenuItem part2GotoPart1MenuItem;
    private JMenuItem part2GotoPart3MenuItem;
    
    private JMenuItem part3TextMenuItem;
    private JRadioButtonMenuItem part3CodeWithoutStopMenuItem;
    private JRadioButtonMenuItem part3CodeWithStopMenuItem;
    private JMenuItem part3GotoPart2MenuItem;
    
    private String oldCode;
    private String part1RecursionCode;
    private String part1IterationCode;
    private String part2Code;
    private String part3WithoutStopCode;
    private String part3WithStopCode;
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public RecursionIntroLesson2(MainClass mainClass, DataInputStream stream,
            RecursionIntroLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.loader = loader;
        
        textFrame = new TextFrame(mainClass);
        
        mainClass.getTreeOfInstances().setTreeNodeMaxLetters(11);
        
        SpecialFunctions.addSpecialFunction(new StartSpecialFunction());
        SpecialFunctions.addSpecialFunction(new FactorialSpecialFunction());
        
        initPart1MenuItems();
        initPart2MenuItems();
        initPart3MenuItems();
        
        initCodes();
        
        mainClass.getLessonMenu().setEnabled(true);
        
        if (stream == null) {
            initPart1();
            textFrame.gotoText();
        } else {
            mainClass.loadFramesPositionAndSettnings(stream);
            
            selectedPart = stream.readByte();
            state = State.getById(stream.readByte());
            
            part1ChosenCode = Part1ChosenCode.getById(stream.readByte());
            part3ChosenCode = Part3ChosenCode.getById(stream.readByte());
            
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
            
            textFrame.gotoPart(selectedPart);
        }
    }
    //</editor-fold>
    
    
    //private fucntions:
    
    //<editor-fold defaultstate="collapsed" desc="initCodes">
    private void initCodes() {
        InputStream stream;
        
        stream = getClass().getResourceAsStream("part1_code_recursion.txt");
        part1RecursionCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part1_code_iteration.txt");
        part1IterationCode = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part2_code.txt");
        part2Code = LessonHelper.readFile(stream);
        
        stream = getClass().getResourceAsStream("part3_code_without_stop.txt");
        part3WithoutStopCode = LessonHelper.readFile(stream);
        stream = getClass().getResourceAsStream("part3_code_with_stop.txt");
        part3WithStopCode = LessonHelper.readFile(stream);
        
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
        
        part1RecursionCodeMenuItem = new JRadioButtonMenuItem(Lang.part1CodeRecursionMenuItem);
        part1RecursionCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == Part1ChosenCode.Recursion) {
                    return;
                }
                mainClass.getEditor().setCode(part1RecursionCode);
                part1ChosenCode = Part1ChosenCode.Recursion;
            }
        });
        
        part1IterationCodeMenuItem = new JRadioButtonMenuItem(Lang.part1CodeIterationMenuItem);
        part1IterationCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
                if (part1ChosenCode == Part1ChosenCode.Iteration) {
                    return;
                }
                mainClass.getEditor().setCode(part1IterationCode);
                part1ChosenCode = Part1ChosenCode.Iteration;
            }
        });
        
        part1GotoPart2MenuItem = new JMenuItem(Lang.part1gotoPart2MenuItem);
        part1GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initPart2();
                if (state == State.NothingShown) {
                    textFrame.gotoText();
                    state = State.Part2Shown;
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part1IterationCodeMenuItem);
        group.add(part1RecursionCodeMenuItem);
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
        
        part2CodeMenuItem = new JRadioButtonMenuItem(Lang.part2CodeMenuItem);
        part2CodeMenuItem.setSelected(true);
        part2CodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
            }
        });
        
        part2GotoPart1MenuItem = new JMenuItem(Lang.part2gotoPart1MenuItem);
        part2GotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initPart1();
            }
        });
        
        part2GotoPart3MenuItem = new JMenuItem(Lang.part2gotoPart3MenuItem);
        part2GotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initPart3();
                if (state != State.Part3Shown) {
                    textFrame.gotoText();
                    state = State.Part3Shown;
                }
            }
        });
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
        
        part3CodeWithoutStopMenuItem = new JRadioButtonMenuItem(Lang.part3CodeWithoutStopMenuItem);
        part3CodeWithoutStopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part3WithoutStopCode);
            }
        });
        
        part3CodeWithStopMenuItem = new JRadioButtonMenuItem(Lang.part3CodeWithStopMenuItem);
        part3CodeWithStopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part3WithStopCode);
            }
        });
        
        part3GotoPart2MenuItem = new JMenuItem(Lang.part3gotoPart2MenuItem);
        part3GotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initPart2();
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(part3CodeWithoutStopMenuItem);
        group.add(part3CodeWithStopMenuItem);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initPart1">
    private void initPart1() {
        selectedPart = 1;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1RecursionCodeMenuItem);
        lessonMenu.add(part1IterationCodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1GotoPart2MenuItem);
        
        if (part1ChosenCode == Part1ChosenCode.Recursion) {
            mainClass.getEditor().setCode(part1RecursionCode);
            part1RecursionCodeMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(part1IterationCode);
            part1IterationCodeMenuItem.setSelected(true);
        }
        
        textFrame.gotoPart(1);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2">
    private void initPart2() {
        selectedPart = 2;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part2TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2CodeMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part2GotoPart1MenuItem);
        lessonMenu.add(part2GotoPart3MenuItem);

        mainClass.getEditor().setCode(part2Code);
        textFrame.gotoPart(2);
    }
    //</editor-fold>    
    
    //<editor-fold defaultstate="collapsed" desc="initPart3">
    private void initPart3() {
        selectedPart = 3;
        
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();

        lessonMenu.add(part3TextMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3CodeWithoutStopMenuItem);
        lessonMenu.add(part3CodeWithStopMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part3GotoPart2MenuItem);

        mainClass.getEditor().setCode(part3WithoutStopCode);
        
        if (part3ChosenCode == Part3ChosenCode.WithoutStop) {
            mainClass.getEditor().setCode(part3WithoutStopCode);
            part3CodeWithoutStopMenuItem.setSelected(true);
        } else {
            mainClass.getEditor().setCode(part3WithStopCode);
            part3CodeWithStopMenuItem.setSelected(true);
        }
        
        textFrame.gotoPart(3);
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
        stream.writeByte(part3ChosenCode.Id);
    }
    
    @Override
    public void close() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();
        lessonMenu.setEnabled(false);
        
        mainClass.getTreeOfInstances().setDefaultTreeNodeMaxLetters();
        SpecialFunctions.clear();
        
        mainClass.removeAddictionalLessonFrame(textFrame.getFrame());
        mainClass.getEditor().setCode(oldCode);
    }
    
    private void setButtonsEnabled(boolean enabled) {
        if ( selectedPart == 1) {
            part1GotoPart2MenuItem.setEnabled(enabled);
            part1RecursionCodeMenuItem.setEnabled(enabled);
            part1IterationCodeMenuItem.setEnabled(enabled);
        } else if (selectedPart == 2) {
            part2GotoPart1MenuItem.setEnabled(enabled);
            part2GotoPart3MenuItem.setEnabled(enabled);
        } else {
            part3GotoPart2MenuItem.setEnabled(enabled);
            part3CodeWithoutStopMenuItem.setEnabled(enabled);
            part3CodeWithStopMenuItem.setEnabled(enabled);
        }
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
        public static final String part1CodeRecursionMenuItem = "Funkcja rekurencyjna";
        public static final String part1CodeIterationMenuItem = "Funkcja itaracyjna";
        public static final String part1gotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania";
        public static final String part2CodeMenuItem = "Przykładowa funkcja";
        public static final String part2gotoPart1MenuItem = "<<< Część I";
        public static final String part2gotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania";
        public static final String part3CodeWithoutStopMenuItem = "Funkcja bez warunku stopu";
        public static final String part3CodeWithStopMenuItem = "Funkcja z warunkiem stopu";
        public static final String part3gotoPart2MenuItem = "<<< Część II";
    }
    //</editor-fold>
    
}

