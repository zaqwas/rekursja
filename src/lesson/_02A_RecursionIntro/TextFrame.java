package lesson._02A_RecursionIntro;

import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javax.swing.JInternalFrame;
import mainclass.MainClass;

class TextFrame {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    private byte currentPart = 1;
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private BooleanProperty part1WebViewVisibleProperty;
    private BooleanProperty part2WebViewVisibleProperty;
    private BooleanProperty part3WebViewVisibleProperty;
    
    private BooleanProperty currentPartWebViewVisibleProperty;
    //</editor-fold>
    
    
    //public functions:
    
    //<editor-fold defaultstate="collapsed" desc="getFrame">
    public JInternalFrame getFrame() {
        return frame;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoText">
    public void gotoText() {
        frame.setVisible(true);
        frame.toFront();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoPart">
    public void gotoPart(final int part) {
        assert 1 <= part && part <= 3;
        if (part == currentPart) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentPartWebViewVisibleProperty.set(false);
                switch (part) {
                    case 1:
                        currentPartWebViewVisibleProperty = part1WebViewVisibleProperty;
                        break;
                    case 2:
                        currentPartWebViewVisibleProperty = part2WebViewVisibleProperty;
                        break;
                    case 3:
                        currentPartWebViewVisibleProperty = part3WebViewVisibleProperty;
                        break;
                }
                currentPartWebViewVisibleProperty.set(true);
                currentPart = (byte) part;
            }
        });
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="initFrame">
    private void initFrame()
    {
        frame = new JInternalFrame(Lang.frameTitle);
        final JFXPanel fxPanel = new JFXPanel();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AnchorPane anchorPane = new AnchorPane();
                
                WebView web = new WebView();
                web.setContextMenuEnabled(false);
                part1WebViewVisibleProperty = web.visibleProperty();
                currentPartWebViewVisibleProperty = part1WebViewVisibleProperty;
                web.getEngine().load(getClass().getResource("part1_text.html").toExternalForm());
                
                AnchorPane.setBottomAnchor(web, 0d);
                AnchorPane.setLeftAnchor(web, 0d);
                AnchorPane.setRightAnchor(web, 0d);
                AnchorPane.setTopAnchor(web, 0d);
                anchorPane.getChildren().add(web);
                
                web = new WebView();
                web.setContextMenuEnabled(false);
                web.setVisible(false);
                part2WebViewVisibleProperty = web.visibleProperty();
                web.getEngine().load(getClass().getResource("part2_text.html").toExternalForm());
                
                AnchorPane.setBottomAnchor(web, 0d);
                AnchorPane.setLeftAnchor(web, 0d);
                AnchorPane.setRightAnchor(web, 0d);
                AnchorPane.setTopAnchor(web, 0d);
                anchorPane.getChildren().add(web);
                
                web = new WebView();
                web.setContextMenuEnabled(false);
                web.setVisible(false);
                part3WebViewVisibleProperty = web.visibleProperty();
                web.getEngine().load(getClass().getResource("part3_text.html").toExternalForm());
                
                AnchorPane.setBottomAnchor(web, 0d);
                AnchorPane.setLeftAnchor(web, 0d);
                AnchorPane.setRightAnchor(web, 0d);
                AnchorPane.setTopAnchor(web, 0d);
                anchorPane.getChildren().add(web);
                
                Scene scene = new Scene(anchorPane);
                fxPanel.setScene(scene);
            }
        });
        
        frame.setContentPane(fxPanel);
        frame.setPreferredSize(new Dimension(600, 450));
        frame.setResizable(true);
        frame.setVisible(false);
        frame.pack();
        mainClass.addAddictionalLessonFrame(Lang.frameMenuDescription, frame);
    }
    //</editor-fold>
    
    //constructor:
    public TextFrame(MainClass mainClass) {
        this.mainClass = mainClass;
        initFrame();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Treść zadania";
        public static final String frameMenuDescription = "Treść zadania";
    }
    //</editor-fold>
    
}
