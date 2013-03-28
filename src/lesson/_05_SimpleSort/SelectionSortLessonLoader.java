package lesson._05_SimpleSort;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import lesson._05_SimpleSort.SimpleSortLesson.Algorithm;
import mainclass.MainClass;

public class SelectionSortLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Porządkowanie przez wybór";
    }

    @Override
    public String getLessonKey() {
        return "9sX6haLj0C";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new SimpleSortLesson(Algorithm.SelectionSort, mainClass, dataInputStream, this);
    }
}
