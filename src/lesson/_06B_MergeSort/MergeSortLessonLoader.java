package lesson._06B_MergeSort;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class MergeSortLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "6b - Porządkowanie przez scalanie";
    }

    @Override
    public String getLessonKey() {
        return "EhGeQz3sCt";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new MergeSortLesson(mainClass, dataInputStream, this);
    }
    
}
