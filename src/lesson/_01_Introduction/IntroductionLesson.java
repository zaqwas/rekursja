package lesson._01_introduction;

import interpreter.InterpreterThread;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;
import lesson.Lesson;
import mainclass.MainClass;
import syntax.SyntaxNode;

public class IntroductionLesson implements Lesson {
    
    private MainClass mainClass;
    private JInternalFrame textFrame;
    private JTextPane textPane;
    
    public IntroductionLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    @Override
    public void start() {
        textFrame = new JInternalFrame();

        textPane = new JTextPane();
        textPane.setText("Tutaj będzie wprowadzenie.\nBla bla bla");
        textPane.setEditable(false);

        textFrame.add(textPane);
        
        textFrame.pack();
        
        mainClass.addToDesktop(textFrame);
        textFrame.setVisible(true);
        
    }

    @Override
    public void close() {
        
    }

    @Override
    public void pauseStart(SyntaxNode node, int delayTime) {
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
    
}
