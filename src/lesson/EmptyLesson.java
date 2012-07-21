package lesson;

import interpreter.InterpreterThread;
import syntax.SyntaxNode;

public class EmptyLesson implements Lesson {
    @Override
    public void start() {}

    @Override
    public void close() {}

    @Override
    public void pauseStart(SyntaxNode node, int delayTime) {}

    @Override
    public void pauseStop(SyntaxNode node) {}

    @Override
    public void threadStart(InterpreterThread thread) {}

    @Override
    public void threadStop() {}
}
