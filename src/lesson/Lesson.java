package lesson;

import interpreter.InterpreterThread;
import syntax.SyntaxNode;

public interface Lesson {
    
    public void close();
    
    //return valye - If function returns false, it means thread shouldn't wait.
    public boolean pauseStart(SyntaxNode node, int delayTime);
    public void pauseStop(SyntaxNode node);
    
    public void threadStart(InterpreterThread thread);
    public void threadStop();
    
    public LessonLoader getLessonLoader();
    
}