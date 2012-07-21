package lesson;

import interpreter.InterpreterThread;
import syntax.SyntaxNode;

public interface Lesson {
    
    public void start();
    public void close();
    
    public void pauseStart(SyntaxNode node, int delayTime);
    public void pauseStop(SyntaxNode node);
    
    public void threadStart(InterpreterThread thread);
    public void threadStop();
}