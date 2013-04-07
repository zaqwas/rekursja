package lesson._07B_QuickSort;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class QuickSortLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "7b - Szybki algorytm porządkowania";
    }

    @Override
    public String getLessonKey() {
        return "YBYXvp9XH8";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new QuickSortLesson(mainClass, dataInputStream, this);
    }
    
}
