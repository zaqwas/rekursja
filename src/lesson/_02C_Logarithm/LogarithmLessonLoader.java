package lesson._02C_Logarithm;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class LogarithmLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "2c - Logarytm";
    }

    @Override
    public String getLessonKey() {
        return "8aaMP298V6";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new LogarithmLesson(mainClass, dataInputStream, this);
    }
    
}
