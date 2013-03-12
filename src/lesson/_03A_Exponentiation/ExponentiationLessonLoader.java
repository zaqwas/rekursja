package lesson._03A_Exponentiation;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class ExponentiationLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Potęgowanie";
    }

    @Override
    public String getLessonKey() {
        return "9Cj2l54x8r";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new ExponentiationLesson(mainClass, dataInputStream, this);
    }
    
}
