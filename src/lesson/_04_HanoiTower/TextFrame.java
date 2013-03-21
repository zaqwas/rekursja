package lesson._04_HanoiTower;

import helpers.LessonHelper;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JInternalFrame;
import lesson._04_HanoiTower.HanoiTowerLesson.State;
import mainclass.MainClass;

class TextFrame {
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private int hintShown = 0;
    
    private SelectionModel<Tab> tabSelectionModel;
    private BooleanProperty hintTabDisabledProperty;
    private WebEngine hintTabWebEngine;
    private BooleanProperty summaryTabDisabledProperty;
    
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabSelectionModel.select(0);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoFunctions">
    public void gotoFunctions() {
        frame.setVisible(true);
        frame.toFront();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabSelectionModel.select(1);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoHint">
    public void gotoHint(final int nr) {
        assert nr >= 1 && nr <= 4;
        frame.setVisible(true);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (hintTabDisabledProperty.get()) {
                    hintTabDisabledProperty.set(false);
                    hintShown = 1;
                }
                if (nr > hintShown) {
                    hintTabWebEngine.executeScript("showHint" + nr + "()");
                    hintShown = nr;
                }
                tabSelectionModel.select(2);
                hintTabWebEngine.executeScript("gotoHint" + nr + "()");
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showAllHints">
    public void showAllHints() {
        if (hintShown == 4) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hintTabDisabledProperty.set(false);
                hintTabWebEngine.executeScript("showHint4()");
                hintShown = 4;
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoSummary">
    public void gotoSummary() {
        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (hintTabDisabledProperty.get()) {
                    hintTabDisabledProperty.set(false);
                }
                if (hintShown < 4) {
                    hintTabWebEngine.executeScript("showHint4()");
                    hintShown = 4;
                }
                if (summaryTabDisabledProperty.get()) {
                    summaryTabDisabledProperty.set(false);
                }
                tabSelectionModel.select(3);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initialize">
    public void initialize(final State state) {
        assert state != null;
        
        if (state == State.NothingShown) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hintTabDisabledProperty.set(false);
                if (state.hint > 1) {
                    LessonHelper.initializeHintTab(hintTabWebEngine, state.hint);
                }
                hintShown = state.hint;
                
                if (state == State.SummaryShown) {
                    summaryTabDisabledProperty.set(false);
                }
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
                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                tabPane.setStyle("-fx-tab-min-height: 16px");
                tabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.textTabName);
                WebView web = new WebView();
                web.setContextMenuEnabled(false);
                web.getEngine().load(getClass().getResource("text.html").toExternalForm());
                tab.setContent(web);
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.functionsTabName);
                web = new WebView();
                web.setContextMenuEnabled(false);
                web.getEngine().load(getClass().getResource("functions.html").toExternalForm());
                tab.setContent(web);
                tabPane.getTabs().add(tab);

                tab = new Tab(Lang.hintTabName);
                web = new WebView();
                web.setContextMenuEnabled(false);
                web.getEngine().load(getClass().getResource("hint.html").toExternalForm());
                tab.setContent(web);
                tab.setDisable(true);
                hintTabDisabledProperty = tab.disableProperty();
                hintTabWebEngine = web.getEngine();
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.summaryTabName);
                web = new WebView();
                web.setContextMenuEnabled(false);
                web.getEngine().load(getClass().getResource("summary.html").toExternalForm());
                tab.setContent(web);
                tab.setDisable(true);
                summaryTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                Scene scene = new Scene(tabPane);
                fxPanel.setScene(scene);
            }
        });
        
        frame.setContentPane(fxPanel);
        frame.setPreferredSize(new Dimension(700, 500));
        frame.setResizable(true);
        frame.setVisible(false);
        frame.pack();
        mainClass.addAddictionalLessonFrame(Lang.frameMenuDescription, frame);
    }
    //</editor-fold>

    public TextFrame(MainClass mainClass) {
        this.mainClass = mainClass;
        initFrame();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Treść zadania";
        public static final String frameMenuDescription = "Treść zadania";
        
        public static final String textTabName = "Treść zadania";
        public static final String functionsTabName = "Funkcje specjalne";
        public static final String hintTabName = "Wskazówki";
        public static final String summaryTabName = "Podsumowanie";
    }
    //</editor-fold>
    
}
