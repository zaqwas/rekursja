package lesson._07A_PartitionFunction;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class PartitionFunctionLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "7a - Funkcja dzieląca tablicę";
    }

    @Override
    public String getLessonKey() {
        return "3ESlhQXc3d";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new PartitionFunctionLesson(mainClass, dataInputStream, this);
        
//        if (dataInputStream != null) {;
//            mainClass.loadFramesPositionAndSettnings(dataInputStream);
//        }
//        return lesson;
    }
    
}
