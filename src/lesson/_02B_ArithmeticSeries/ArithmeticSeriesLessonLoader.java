package lesson._02B_ArithmeticSeries;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class ArithmeticSeriesLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "2b - Suma ciągu arytmetycznego";
    }

    @Override
    public String getLessonKey() {
        return "COC2AntX7X";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new ArithmeticSeriesLesson(mainClass, dataInputStream, this);
    }
    
}
