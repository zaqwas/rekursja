package lesson._01A_Introduction;

import interpreter.InterpreterThread;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import syntax.SyntaxNode;

public class IntroductionLesson implements Lesson {
    
    private final static String resourcesDir = "/lesson/_01A_Introduction/";
    
    private MainClass mainClass;
    
    private JInternalFrame textFrame;
    private JScrollPane textScrollPane;
    private JTextPane textPane;
    
    private String oldCode;
    
    public IntroductionLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    public void start() {
        
        //<editor-fold defaultstate="collapsed" desc="Setting code in editor">
        oldCode = mainClass.getEditor().getCode();
        
        InputStream stream = getClass().getResourceAsStream(resourcesDir + "code.txt");
        byte bytes[] = new byte[8192];
        int bytesRead;
        try {
            bytesRead = stream.read(bytes);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        }
        if ( bytesRead >= 8192 ) {
            throw new RuntimeException("Array too short.");
        }
        String code = new String(bytes, 0, bytesRead);
        
        mainClass.getEditor().setCode(code);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init frame with introduction text">
        textFrame = new JInternalFrame(Lang.textFrameTitle);
        
        textScrollPane = new JScrollPane();
        textFrame.add(textScrollPane);

        textPane = new JTextPane();
        textPane.setEditable(false);
        try {
            textPane.setPage(getClass().getResource(resourcesDir + "text.html"));
        } catch (IOException ex) {
            textPane.setText(Lang.loadTabPaneError);
        }
        textScrollPane.setViewportView(textPane);

        
        textFrame.setResizable(true);
        textFrame.setPreferredSize(new Dimension(700, 500));
        textFrame.pack();
        
        mainClass.addToDesktop(textFrame);
        textFrame.setVisible(true);
        //</editor-fold>
    }

    @Override
    public void close() {
        mainClass.getEditor().setCode(oldCode);
    }

    @Override
    public boolean pauseStart(SyntaxNode node, int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(SyntaxNode node) {
    }

    @Override
    public void threadStart(InterpreterThread thread) {
    }

    @Override
    public void threadStop() {
    }

    @Override
    public LessonLoader getLessonLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textFrameTitle = "Wprowadzenie";
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
    }
    //</editor-fold>
    
}
