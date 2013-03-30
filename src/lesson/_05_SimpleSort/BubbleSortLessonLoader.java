package lesson._05_SimpleSort;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import lesson._05_SimpleSort.SimpleSortLesson.Algorithm;
import mainclass.MainClass;

public class BubbleSortLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Porządkowanie bąbelkowe";
    }

    @Override
    public String getLessonKey() {
        return "soIWHsoQ85";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new SimpleSortLesson(Algorithm.BubbleSort, mainClass, dataInputStream, this);
    }
}
