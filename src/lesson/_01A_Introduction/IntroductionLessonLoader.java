package lesson._01A_Introduction;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class IntroductionLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "1a - Wprowadzenie do systemu";
    }

    @Override
    public String getLessonKey() {
        return "9nb68I2S5A";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new IntroductionLesson(mainClass, dataInputStream, this);
    }
    
}
