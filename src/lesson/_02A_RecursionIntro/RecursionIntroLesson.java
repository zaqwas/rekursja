package lesson._02A_RecursionIntro;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import helpers.ReadFileHelper;
import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import lesson.Lesson;
import mainclass.MainClass;
import syntax.SyntaxNode;
//</editor-fold>

public class RecursionIntroLesson implements Lesson {
    
    private final static String resourcesDir = "/lesson/_02A_RecursionIntro/";
   
    private MainClass mainClass;
    
    private JInternalFrame partOneTextFrame;
    private JEditorPane partOneTextEditorPane;
    private JScrollPane partOneTaxtScrollPane;
    
    private JInternalFrame partTwoTextFrame;
    private JEditorPane partTwoTextEditorPane;
    private JScrollPane partTwoTaxtScrollPane;
    
    private JInternalFrame partThreeTextFrame;
    private JEditorPane partThreeTextEditorPane;
    private JScrollPane partThreeTaxtScrollPane;
    
    private JMenuItem partOneTextLessonMenuItem;
    private JMenuItem partOneCodeLessonMenuItem;
    private JMenuItem partTwoTextLessonMenuItem;
    private JMenuItem partTwoCodeLessonMenuItem;
    private JMenuItem partThreeTextLessonMenuItem;
    private JMenuItem partThreeCodeLessonMenuItem;
    
    private String oldCode;
    private String partOneCodeRecursion;
    private String partOneCodeIteration;
    private String partTwoCode;
    private String partThreeCode;
    
    public RecursionIntroLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    @Override
    public void start() {
        
        //<editor-fold defaultstate="collapsed" desc="PartOneTextFrame">
        partOneTextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        partOneTaxtScrollPane = new JScrollPane();
        partOneTaxtScrollPane.setBorder(null);
        
        partOneTextEditorPane = new JEditorPane();
        partOneTextEditorPane.setEditable(false);
        try {
            partOneTextEditorPane.setPage(getClass().getResource(resourcesDir + "part_one_text.html"));
        } catch (IOException ex) {
            partOneTextEditorPane.setText(Lang.loadTabPaneError);
        }
        partOneTaxtScrollPane.setViewportView(partOneTextEditorPane);
        partOneTextFrame.add(partOneTaxtScrollPane);
        
        partOneTaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        partOneTextFrame.setResizable(true);
        partOneTextFrame.pack();
        
        mainClass.addToDesktop(partOneTextFrame);
        partOneTextFrame.setVisible(true);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="PartTwoTextFrame">
        partTwoTextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        partTwoTaxtScrollPane = new JScrollPane();
        partTwoTaxtScrollPane.setBorder(null);
        
        partTwoTextEditorPane = new JEditorPane();
        partTwoTextEditorPane.setEditable(false);
        try {
            partTwoTextEditorPane.setPage(getClass().getResource(resourcesDir + "part_two_text.html"));
        } catch (IOException ex) {
            partTwoTextEditorPane.setText(Lang.loadTabPaneError);
        }
        partTwoTaxtScrollPane.setViewportView(partTwoTextEditorPane);
        partTwoTextFrame.add(partTwoTaxtScrollPane);
        
        partTwoTaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        partTwoTextFrame.setResizable(true);
        partTwoTextFrame.pack();
        
        mainClass.addToDesktop(partTwoTextFrame);
        partTwoTextFrame.setVisible(false);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="PartThreeTextFrame">
        partThreeTextFrame = new JInternalFrame(Lang.textFrameTitle);
        
        partThreeTaxtScrollPane = new JScrollPane();
        partThreeTaxtScrollPane.setBorder(null);
        
        partThreeTextEditorPane = new JEditorPane();
        partThreeTextEditorPane.setEditable(false);
        try {
            partThreeTextEditorPane.setPage(getClass().getResource(resourcesDir + "part_three_text.html"));
        } catch (IOException ex) {
            partThreeTextEditorPane.setText(Lang.loadTabPaneError);
        }
        partThreeTaxtScrollPane.setViewportView(partThreeTextEditorPane);
        partThreeTextFrame.add(partThreeTaxtScrollPane);
        
        partThreeTaxtScrollPane.setPreferredSize(new Dimension(700, 500));
        partThreeTextFrame.setResizable(true);
        partThreeTextFrame.pack();
        
        mainClass.addToDesktop(partThreeTextFrame);
        partThreeTextFrame.setVisible(false);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init codeEditor">
        InputStream stream;
        stream = getClass().getResourceAsStream(resourcesDir + "part_one_code_recursion.txt");
        partOneCodeRecursion = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part_one_code_iteration.txt");
        partOneCodeIteration = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part_two_code.txt");
        partTwoCode = ReadFileHelper.readFile(stream);
        stream = getClass().getResourceAsStream(resourcesDir + "part_three_code.txt");
        partThreeCode = ReadFileHelper.readFile(stream);
        
        oldCode = mainClass.getEditor().getCode();
        mainClass.getEditor().setCode(partOneCodeRecursion);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="InitMenu">
        JMenu lessonMenu = mainClass.getLessonMenu();
        
        partOneTextLessonMenuItem = new JMenuItem(Lang.partOneTextLessonMenuItem);
        lessonMenu.add(partOneTextLessonMenuItem);
        partOneCodeLessonMenuItem = new JMenuItem(Lang.partOneCodeLessonMenuItem);
        lessonMenu.add(partOneCodeLessonMenuItem);
        lessonMenu.add(new JSeparator());
        
        partTwoTextLessonMenuItem = new JMenuItem(Lang.partTwoTextLessonMenuItem);
        lessonMenu.add(partTwoTextLessonMenuItem);
        partTwoCodeLessonMenuItem = new JMenuItem(Lang.partTwoCodeLessonMenuItem);
        lessonMenu.add(partTwoCodeLessonMenuItem);
        lessonMenu.add(new JSeparator());
        
        partThreeTextLessonMenuItem = new JMenuItem(Lang.partThreeTextLessonMenuItem);
        lessonMenu.add(partThreeTextLessonMenuItem);
        partThreeCodeLessonMenuItem = new JMenuItem(Lang.partThreeCodeLessonMenuItem);
        lessonMenu.add(partThreeCodeLessonMenuItem);
        
        lessonMenu.setEnabled(true);
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
    public void pauseStart(SyntaxNode node, final int delayTime) {
    }

    @Override
    public void pauseStop(SyntaxNode node) {
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textFrameTitle = "Tablica liczb";
        
        public static final String partOneTextLessonMenuItem = "Treść zadania";
        public static final String partOneCodeLessonMenuItem = "Kod funkcji";
        public static final String partTwoTextLessonMenuItem = "Treść zadania";
        public static final String partTwoCodeLessonMenuItem = "Kod funkcji";
        public static final String partThreeTextLessonMenuItem = "Treść zadania";
        public static final String partThreeCodeLessonMenuItem = "Kod funkcji";
        
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
        
        
    }
    //</editor-fold>
}

