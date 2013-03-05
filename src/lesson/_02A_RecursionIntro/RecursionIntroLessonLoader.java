package lesson._02A_RecursionIntro;

import java.io.DataInputStream;
import java.io.IOException;
import lesson.Lesson;
import lesson.LessonLoader;
import mainclass.MainClass;

public class RecursionIntroLessonLoader implements LessonLoader {

    @Override
    public String getLessonName() {
        return "Wprowadzenie do rekurencji";
    }

    @Override
    public String getLessonKey() {
        return "aFa0gd13i3";
    }

    @Override
    public Lesson getLesson(MainClass mainClass, DataInputStream dataInputStream) throws IOException {
        //return new MaxElementLesson(mainClass, dataInputStream, this);
        return null;
    }
    
}
