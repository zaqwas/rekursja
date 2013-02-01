package lesson._02A_RecursionIntro;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import helpers.ReadFileHelper;
import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import syntax.SyntaxNode;
//</editor-fold>

public class RecursionIntroLesson implements Lesson {
    
    private final static String resourcesDir = "/lesson/_02A_RecursionIntro/";
   
    private MainClass mainClass;
    
    private JInternalFrame part1TextFrame;
    private JEditorPane part1TextEditorPane;
    private JScrollPane part1TaxtScrollPane;
    
    private JInternalFrame part2TextFrame;
    private JEditorPane part2TextEditorPane;
    private JScrollPane part2TaxtScrollPane;
    
    private JInternalFrame part3TextFrame;
    private JEditorPane part3TextEditorPane;
    private JScrollPane part3TaxtScrollPane;
    
    private JMenuItem part1TextMenuItem;
    private JMenuItem part1CodeRecursionMenuItem;
    private JMenuItem part1CodeIterationMenuItem;
    private JMenuItem part2TextMenuItem;
    private JMenuItem part2CodeMenuItem;
    private JMenuItem part3TextMenuItem;
    private JMenuItem part3CodeWithoutStopMenuItem;
    private JMenuItem part3CodeWithStopMenuItem;
    
    private JMenuItem part1gotoPart2MenuItem;
    private JMenuItem part2gotoPart1MenuItem;
    private JMenuItem part2gotoPart3MenuItem;
    private JMenuItem part3gotoPart2MenuItem;
    
    private String oldCode;
    private String part1CodeRecursion;
    private String part1CodeIteration;
    private String part2Code;
    private String part3CodeWithoutStop;
    private String part3CodeWithStop;
    
    public RecursionIntroLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    public void start() {
        
        //<editor-fold defaultstate="collapsed" desc="Part1TextFrame">
        part1TextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        part1TaxtScrollPane = new JScrollPane();
        part1TaxtScrollPane.setBorder(null);
        
        part1TextEditorPane = new JEditorPane();
        part1TextEditorPane.setEditable(false);
        try {
            part1TextEditorPane.setPage(getClass().getResource(resourcesDir + "part1_text.html"));
        } catch (IOException ex) {
            part1TextEditorPane.setText(Lang.loadTabPaneError);
        }
        part1TaxtScrollPane.setViewportView(part1TextEditorPane);
        part1TextFrame.add(part1TaxtScrollPane);
        
        part1TaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        part1TextFrame.setResizable(true);
        part1TextFrame.pack();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Part2TextFrame">
        part2TextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        part2TaxtScrollPane = new JScrollPane();
        part2TaxtScrollPane.setBorder(null);
        
        part2TextEditorPane = new JEditorPane();
        part2TextEditorPane.setEditable(false);
        try {
            part2TextEditorPane.setPage(getClass().getResource(resourcesDir + "part2_text.html"));
        } catch (IOException ex) {
            part2TextEditorPane.setText(Lang.loadTabPaneError);
        }
        part2TaxtScrollPane.setViewportView(part2TextEditorPane);
        part2TextFrame.add(part2TaxtScrollPane);
        
        part2TaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        part2TextFrame.setResizable(true);
        part2TextFrame.pack();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Part3TextFrame">
        part3TextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        part3TaxtScrollPane = new JScrollPane();
        part3TaxtScrollPane.setBorder(null);
        
        part3TextEditorPane = new JEditorPane();
        part3TextEditorPane.setEditable(false);
        try {
            part3TextEditorPane.setPage(getClass().getResource(resourcesDir + "part3_text.html"));
        } catch (IOException ex) {
            part3TextEditorPane.setText(Lang.loadTabPaneError);
        }
        part3TaxtScrollPane.setViewportView(part3TextEditorPane);
        part3TextFrame.add(part3TaxtScrollPane);
        
        part3TaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        part3TextFrame.setResizable(true);
        part3TextFrame.pack();
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init codes">
        InputStream stream;
        stream = getClass().getResourceAsStream(resourcesDir + "part1_code_recursion.txt");
        part1CodeRecursion = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part1_code_iteration.txt");
        part1CodeIteration = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part2_code.txt");
        part2Code = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part3_code_without_stop.txt");
        part3CodeWithoutStop = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part3_code_with_stop.txt");
        part3CodeWithStop = ReadFileHelper.readFile(stream);
        
        oldCode = mainClass.getEditor().getCode();
        mainClass.getEditor().setCode(part1CodeRecursion);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part1 menuItems">
        part1TextMenuItem = new JMenuItem(Lang.part1TextMenuItem);
        part1TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part1TextFrame.setVisible(true);
                part1TextFrame.toFront();
            }
        });
        
        part1CodeRecursionMenuItem = new JMenuItem(Lang.part1CodeRecursionMenuItem);
        part1CodeRecursionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part1CodeRecursion);
            }
        });
        
        part1CodeIterationMenuItem = new JMenuItem(Lang.part1CodeIterationMenuItem);
        part1CodeIterationMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part1CodeIteration);
            }
        });
        
        part1gotoPart2MenuItem = new JMenuItem(Lang.part1gotoPart2MenuItem);
        part1gotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu lessonMenu = mainClass.getLessonMenu();
                lessonMenu.removeAll();
                mainClass.getDesktop().remove(part1TextFrame);
                
                lessonMenu.add(part2TextMenuItem);
                lessonMenu.add(part2CodeMenuItem);
                lessonMenu.add(new JSeparator());
                lessonMenu.add(part2gotoPart1MenuItem);
                lessonMenu.add(part2gotoPart3MenuItem);
                
                mainClass.getEditor().setCode(part2Code);
                mainClass.getDesktop().add(part2TextFrame);
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
                mainClass.getDesktop().repaint();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part2 menuItems">
        part2TextMenuItem = new JMenuItem(Lang.part2TextMenuItem);
        part2TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
            }
        });
        
        part2CodeMenuItem = new JMenuItem(Lang.part2CodeMenuItem);
        part2CodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part2Code);
            }
        });
        
        part2gotoPart1MenuItem = new JMenuItem(Lang.part2gotoPart1MenuItem);
        part2gotoPart1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu lessonMenu = mainClass.getLessonMenu();
                lessonMenu.removeAll();
                mainClass.getDesktop().remove(part2TextFrame);
                
                lessonMenu.add(part1TextMenuItem);
                lessonMenu.add(part1CodeRecursionMenuItem);
                lessonMenu.add(part1CodeIterationMenuItem);
                lessonMenu.add(new JSeparator());
                lessonMenu.add(part1gotoPart2MenuItem);
                
                mainClass.getEditor().setCode(part1CodeRecursion);
                mainClass.getDesktop().add(part1TextFrame);
                part1TextFrame.setVisible(true);
                part1TextFrame.toFront();
                mainClass.getDesktop().repaint();
            }
        });
        
        part2gotoPart3MenuItem = new JMenuItem(Lang.part2gotoPart3MenuItem);
        part2gotoPart3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu lessonMenu = mainClass.getLessonMenu();
                lessonMenu.removeAll();
                mainClass.getDesktop().remove(part2TextFrame);
                
                lessonMenu.add(part3TextMenuItem);
                lessonMenu.add(part3CodeWithoutStopMenuItem);
                lessonMenu.add(part3CodeWithStopMenuItem);
                lessonMenu.add(new JSeparator());
                lessonMenu.add(part3gotoPart2MenuItem);
                
                mainClass.getEditor().setCode(part3CodeWithoutStop);
                mainClass.getDesktop().add(part3TextFrame);
                part3TextFrame.setVisible(true);
                part3TextFrame.toFront();
                mainClass.getDesktop().repaint();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init part3 menuItems">
        part3TextMenuItem = new JMenuItem(Lang.part3TextMenuItem);
        part3TextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                part3TextFrame.setVisible(true);
                part3TextFrame.toFront();
            }
        });
        
        part3CodeWithoutStopMenuItem = new JMenuItem(Lang.part3CodeWithoutStopMenuItem);
        part3CodeWithoutStopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part3CodeWithoutStop);
            }
        });
        
        part3CodeWithStopMenuItem = new JMenuItem(Lang.part3CodeWithStopMenuItem);
        part3CodeWithStopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().setCode(part3CodeWithStop);
            }
        });
        
        part3gotoPart2MenuItem = new JMenuItem(Lang.part3gotoPart2MenuItem);
        part3gotoPart2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu lessonMenu = mainClass.getLessonMenu();
                lessonMenu.removeAll();
                mainClass.getDesktop().remove(part3TextFrame);
                
                lessonMenu.add(part2TextMenuItem);
                lessonMenu.add(part2CodeMenuItem);
                lessonMenu.add(new JSeparator());
                lessonMenu.add(part2gotoPart1MenuItem);
                lessonMenu.add(part2gotoPart3MenuItem);
                
                mainClass.getEditor().setCode(part2Code);
                mainClass.getDesktop().add(part2TextFrame);
                part2TextFrame.setVisible(true);
                part2TextFrame.toFront();
                mainClass.getDesktop().repaint();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="InitMenu">
        JMenu lessonMenu = mainClass.getLessonMenu();

        lessonMenu.add(part1TextMenuItem);
        lessonMenu.add(part1CodeRecursionMenuItem);
        lessonMenu.add(part1CodeIterationMenuItem);
        lessonMenu.add(new JSeparator());
        lessonMenu.add(part1gotoPart2MenuItem);
        
        lessonMenu.setEnabled(true);
        
        mainClass.addToDesktop(part1TextFrame);
        part1TextFrame.setVisible(true);
        //</editor-fold>
        
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void threadStart(InterpreterThread thread) {
        
    }
    
    @Override
    public void threadStop() {
    }
    
    @Override
    public boolean pauseStart(SyntaxNode node, final int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(SyntaxNode node) {
    }

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textFrameTitle = "Tablica liczb";
        
        public static final String part1TextMenuItem = "Treść zadania (o rekurncji)";
        public static final String part1CodeRecursionMenuItem = "Funkcja rekurencyjna";
        public static final String part1CodeIterationMenuItem = "Funkcja itaracyjna";
        public static final String part1gotoPart2MenuItem = ">>> Część II";
        
        public static final String part2TextMenuItem = "Treść zadania (o stosie)";
        public static final String part2CodeMenuItem = "Funkcja przykładowa";
        public static final String part2gotoPart1MenuItem = "<<< Część I";
        public static final String part2gotoPart3MenuItem = ">>> Część III";
        
        public static final String part3TextMenuItem = "Treść zadania (o warunku stopu)";
        public static final String part3CodeWithoutStopMenuItem = "Funkcja bez warunku stopu";
        public static final String part3CodeWithStopMenuItem = "Funkcja z warunkiem stopu";
        public static final String part3gotoPart2MenuItem = "<<< Część II";
        
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
        
        
    }
    //</editor-fold>
}

