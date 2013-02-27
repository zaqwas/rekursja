package lesson._06B_MergeSort;

import helpers.LessonHelper;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javax.swing.JInternalFrame;
import lesson._06B_MergeSort.MergeSortLesson.State;
import mainclass.MainClass;

class TextFrame {
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private SelectionModel<Tab> tabSelectionModel;
    private BooleanProperty pseudocodeTabDisabledProperty;
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
    
    //<editor-fold defaultstate="collapsed" desc="gotoPseudocode">
    public void gotoPseudocode() {
        frame.setVisible(true);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (pseudocodeTabDisabledProperty.get()) {
                    pseudocodeTabDisabledProperty.set(false);
                }
                tabSelectionModel.select(2);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showPseudocode">
    public void showPseudocode() {
        if (!pseudocodeTabDisabledProperty.get()) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pseudocodeTabDisabledProperty.set(false);
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
                if (pseudocodeTabDisabledProperty.get()) {
                    pseudocodeTabDisabledProperty.set(false);
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
                pseudocodeTabDisabledProperty.set(false);
                
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
                tabPane.tabMinHeightProperty().set(0d);
                tabSelectionModel = tabPane.getSelectionModel();
                
                Tab tab = new Tab(Lang.textTabName);
                WebView web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("text.html").toString());
                tab.setContent(web);
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.functionsTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("functions.html").toString());
                tab.setContent(web);
                tabPane.getTabs().add(tab);

                tab = new Tab(Lang.pseudocodeTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("pseudocode.html").toString());
                tab.setContent(web);
                tab.setDisable(true);
                pseudocodeTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.summaryTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("summary.html").toString());
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
        public static final String pseudocodeTabName = "Pseudokod";
        public static final String summaryTabName = "Podsumowanie";
    }
    //</editor-fold>
    
}
