package lesson;

import interpreter.Instance;
import interpreter.InterpreterThread;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mainclass.MainClass;
import syntax.SyntaxNode;

public class EmptyLesson implements Lesson, LessonLoader {
    
    private MainClass mainClass;
    
    public EmptyLesson(MainClass mainClass) {
        this.mainClass = mainClass;
    }
    
    @Override
    public LessonLoader getLessonLoader() {
        return this;
    }
    
    @Override
    public void save(DataOutputStream stream) throws IOException {
        mainClass.saveFramesPositionAndSettnings(stream);
        String code = mainClass.getEditor().getCode();
        stream.writeUTF(code);
    }
    
    @Override
    public void close() {}

    @Override
    public boolean pauseStart(Instance instance, SyntaxNode node, boolean afterCall, int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(Instance instance, SyntaxNode node, boolean afterCall) {}

    @Override
    public void threadStart(InterpreterThread thread) {}

    @Override
    public void threadStop() {}
    
    
    
    @Override
    public String getLessonName() {
        return "Empty lesson";
    }

    @Override
    public String getLessonKey() {
        return "fs3cb3jXqD";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream stream) throws IOException {
        mainClass.loadFramesPositionAndSettnings(stream);
        String code = stream.readUTF();
        mainClass.getEditor().setCode(code);
        return this;
    }
}
