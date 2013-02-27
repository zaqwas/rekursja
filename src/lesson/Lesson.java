package lesson;

import interpreter.Instance;
import interpreter.InterpreterThread;
import java.io.DataOutputStream;
import java.io.IOException;
import syntax.SyntaxNode;

public interface Lesson {
    
    public LessonLoader getLessonLoader();
    
    public void save(DataOutputStream stream) throws IOException;
    public void close();
    
    //return value: If function returns false, it means thread shouldn't wait.
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, int delayTime);
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall);
    
    public void threadStart(InterpreterThread thread);
    public void threadStop();
    
}