package lesson._01B_MaxElement;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class MaxElementLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Maksymalny element";
    }

    @Override
    public String getLessonKey() {
        return "Uo18983Yji";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new MaxElementLesson(mainClass, dataInputStream, this);
    }
    
}
