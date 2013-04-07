package lesson._08_Fibonacci;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class FibonacciLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "8 - Ciąg Fibonacciego";
    }

    @Override
    public String getLessonKey() {
        return "x7KgN2c70C";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new FibonacciLesson(mainClass, dataInputStream, this);
    }
    
}
