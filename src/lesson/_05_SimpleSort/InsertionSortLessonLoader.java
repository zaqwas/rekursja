package lesson._05_SimpleSort;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import lesson._05_SimpleSort.SimpleSortLesson.Algorithm;
import mainclass.MainClass;

public class InsertionSortLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Porządkowanie przez wstawianie";
    }

    @Override
    public String getLessonKey() {
        return "7AfB4G73hP";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new SimpleSortLesson(Algorithm.InsertionSort, mainClass, dataInputStream, this);
    }
}
