package lesson;

import interpreter.InterpreterThread;
import java.io.DataInputStream;
import mainclass.MainClass;
import syntax.SyntaxNode;

public class EmptyLesson implements Lesson {
    
    private LessonLoader loader;
    
    @Override
    public void close() {}

    @Override
    public boolean pauseStart(SyntaxNode node, int delayTime) {
        return true;
    }

    @Override
    public void pauseStop(SyntaxNode node) {}

    @Override
    public void threadStart(InterpreterThread thread) {}

    @Override
    public void threadStop() {}

    @Override
    public LessonLoader getLessonLoader() {
        if ( loader!= null ) {
            return loader;
        }
        loader = new LessonLoader() {
            @Override
            public String getLessonName() {
                return "Empty lesson";
            }
            @Override
            public String getLessonKey() {
                return "fs3cb3jXqD";
            }
            @Override
            public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) {
                throw new UnsupportedOperationException("Not supported operation.");
            }
        };
        return loader;
    }
}
