package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.web.WebEngine;

public final class LessonHelper {

    private LessonHelper() {}
    

    //<editor-fold defaultstate="collapsed" desc="readFile">
    public static String readFile(InputStream stream) {
        try {
            byte[] bytes = new byte[0];
            byte[] buffer = new byte[4096];
            int byteRead = 0, bufferRead;
            while ((bufferRead = stream.read(buffer)) != -1) {
                if (bufferRead == 0) {
                    continue;
                }
                bytes = Arrays.copyOf(bytes, byteRead + bufferRead);
                System.arraycopy(buffer, 0, bytes, byteRead, bufferRead);
                byteRead += bufferRead;
            }
            return new String(bytes, "UTF-8");
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="initializeHintTab">
    public static void initializeHintTab(final WebEngine webEngine, final int hint) {
        assert hint >= 2;
        assert webEngine != null;
        assert Platform.isFxApplicationThread();

        final ReadOnlyBooleanProperty runningProperty =
                webEngine.getLoadWorker().runningProperty();

        if (runningProperty.get()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runningProperty.get()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            webEngine.executeScript("showHint" + hint + "()");
                        }
                    });
                }
            }).start();
        } else {
            webEngine.executeScript("showHint" + hint + "()");
        }
    }
    //</editor-fold>
    
}
