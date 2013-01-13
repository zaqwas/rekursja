package lesson._07A_PartitionFunction;

import java.io.DataInputStream;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class PartitionFunctionLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Funkcja dzieląca tablicę";
    }

    @Override
    public String getLessonKey() {
        return "3ESlhQXc3d";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) {
        PartitionFunctionLesson lesson = new PartitionFunctionLesson(mainClass);
        lesson.start();
        return lesson;
    }
    
}
