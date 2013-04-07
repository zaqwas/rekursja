package lesson._03C_BinarySearch;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class BinarySearchLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "3c - Wyszukiwanie elementu";
    }

    @Override
    public String getLessonKey() {
        return "JSSm52xukI";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new BinarySearchLesson(mainClass, dataInputStream, this);
    }
    
}
