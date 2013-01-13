package lesson;

import java.io.DataInputStream;
import mainclass.MainClass;

public interface LessonLoader {

    public String getLessonName();

    public String getLessonKey();

    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream);
    
}
