package lesson._06A_MergeFunction;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class MergeFunctionLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "6a - Scalanie uporządkowanych tablic";
    }

    @Override
    public String getLessonKey() {
        return "5QWmJz4Yw2";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new MergeFunctionLesson(mainClass, dataInputStream, this);
    }
    
}
