package lesson._03B_EuclideanAlgorithm;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class EuclideanAlgorithmLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "3b - Największy wspólny dzielnik";
    }

    @Override
    public String getLessonKey() {
        return "nIG10VK0Bb";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new EuclideanAlgorithmLesson(mainClass, dataInputStream, this);
    }
    
}
