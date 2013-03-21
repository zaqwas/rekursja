package lesson._04_HanoiTower;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class HanoiTowerLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Wieże Hanoi";
    }

    @Override
    public String getLessonKey() {
        return "se6vBi09at";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        return new HanoiTowerLesson(mainClass, dataInputStream, this);
    }
    
}
