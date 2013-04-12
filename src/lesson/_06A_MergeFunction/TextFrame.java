package lesson._06A_MergeFunction;

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
import lesson._06A_MergeFunction.MergeFunctionLesson.State;
import mainclass.MainClass;

class TextFrame {
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private int hintShown = 0;
    private boolean summaryShown = false;
    
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
        assert 1 <= nr && nr <= 4;
        
        frame.setVisible(true);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (hintShown == 0) {
                    hintTabDisabledProperty.set(false);
                    hintShown = 1;
                }
                tabSelectionModel.select(2);
                
                if (hintShown < nr) {
                    hintTabWebEngine.executeScript("showHint" + nr + "()");
                    hintShown = nr;
                }
                hintTabWebEngine.executeScript("gotoHint" + nr + "()");
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showHint">
    public void showHint(final int nr) {
        assert 1 <= nr && nr <= 4;

        if (hintShown >= nr) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (hintShown == 0) {
                    hintTabDisabledProperty.set(false);
                    hintShown = 1;
                }
                if (hintShown < nr) {
                    hintTabWebEngine.executeScript("showHint" + nr + "()");
                    hintShown = nr;
                }
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
                if (!summaryShown) {
                    if (hintShown < 4) {
                        hintTabDisabledProperty.set(false);
                        hintTabWebEngine.executeScript("showHint4()");
                    }
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
                
                hintShown = state.hint;
                if ( hintShown >= 2 ) {
                    LessonHelper.initializeHintTab(hintTabWebEngine, hintShown);
                }
                
                if (state == State.SummaryShown) {
                    summaryTabDisabledProperty.set(false);
                    summaryShown = true;
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
                tabPane.tabMinHeightProperty().set(16d);
                tabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.textTabName);
                tabPane.getTabs().add(tab);
                WebView web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("text.html").toString());
                tab.setContent(web);
                
                tab = new Tab(Lang.functionsTabName);
                tabPane.getTabs().add(tab);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("functions.html").toString());
                tab.setContent(web);

                tab = new Tab(Lang.hintTabName);
                tab.disableProperty().set(true);
                tabPane.getTabs().add(tab);
                hintTabDisabledProperty = tab.disableProperty();
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("hint.html").toString());
                tab.setContent(web);
                hintTabWebEngine = web.getEngine();
                
                tab = new Tab(Lang.summaryTabName);
                tab.disableProperty().set(true);
                summaryTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("summary.html").toString());
                tab.setContent(web);
                
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
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String textTabName = "Treść zadania";
        public static final String functionsTabName = "Funkcje specjalne";
        public static final String hintTabName = "Wskazówki";
        public static final String summaryTabName = "Podsumowanie";
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
    }
    //</editor-fold>
    
}
