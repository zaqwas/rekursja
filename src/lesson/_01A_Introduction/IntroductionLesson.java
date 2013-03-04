package lesson._01A_Introduction;

import helpers.LessonHelper;
import interpreter.Instance;
import interpreter.InterpreterThread;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;
import syntax.SyntaxNode;

public class IntroductionLesson implements Lesson {
    
    private MainClass mainClass;
    private IntroductionLessonLoader lessonLoader;
    private TextFrame textFrame;
    private String oldCode;
    
    
    public IntroductionLesson(MainClass mainClass, DataInputStream stream, 
            IntroductionLessonLoader loader) throws IOException {
        this.mainClass = mainClass;
        this.lessonLoader = loader;
        
        textFrame = new TextFrame(mainClass);
        
        oldCode = mainClass.getEditor().getCode();
        InputStream inputStream = getClass().getResourceAsStream("code.txt");
        String code = LessonHelper.readFile(inputStream);
        mainClass.getEditor().setCode(code);
        
        initMenu();
        
        if (stream == null) {
            textFrame.gotoText();
        } else {
            mainClass.loadFramesPositionAndSettnings(stream);
        }
    }
    
    private void initMenu() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        
        JMenuItem textMenuItem = new JMenuItem(Lang.textMenuItem);
        textMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.gotoText();
            }
        });
        lessonMenu.add(textMenuItem);
        
        lessonMenu.add(new JSeparator());
        
        JMenuItem programExampleMenuItem = new JMenuItem(Lang.programExampleMenuItem);
        programExampleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainClass.getEditor().frameToFront();
            }
        });
        lessonMenu.add(programExampleMenuItem);
        
        lessonMenu.setEnabled(true);
    }

    @Override
    public void close() {
        JMenu lessonMenu = mainClass.getLessonMenu();
        lessonMenu.removeAll();
        lessonMenu.setEnabled(false);
        
        mainClass.removeAddictionalLessonFrame(textFrame.getFrame());
        
        mainClass.getEditor().setCode(oldCode);
    }

    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {
    }

    @Override
    public void threadStart(InterpreterThread thread) {
    }

    @Override
    public void threadStop() {
    }

    @Override
    public LessonLoader getLessonLoader() {
        return lessonLoader;
    }
    
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String textMenuItem = "Treść zadania";
        public static final String programExampleMenuItem = "Przykładowy program";
    }
    //</editor-fold>
    
}
