package lesson._07A_PartitionFunction;

import lesson._06A_MergeFunction.*;
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
    
    //<editor-fold defaultstate="collapsed" desc="showFrame">
    public void showFrame() {
        frame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoHint">
    public void gotoHint(final int nr) {
        assert 1 <= nr && nr <= 3;

        if (hintShown==0) {
            hintTabDisabledProperty.set(false);
            hintShown = 1;
        }
        frame.setVisible(true);
        frame.toFront();
        tabSelectionModel.select(1);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
        assert 1 <= nr && nr <= 3;

        if (hintShown == 0) {
            hintTabDisabledProperty.set(false);
            hintShown = 1;
        }
        if (hintShown < nr) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    hintTabWebEngine.executeScript("showHint" + nr + "()");
                }
            });
            hintShown = nr;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoText">
    public void gotoText() {
        frame.setVisible(true);
        frame.toFront();
        tabSelectionModel.select(0);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoSummary">
    public void gotoSummary() {
        if (hintShown != 3) {
            showHint(3);
        }
        summaryTabDisabledProperty.set(false);
        
        frame.setVisible(true);
        frame.toFront();
        tabSelectionModel.select(2);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showSummary">
    public void showSummary() {
        if (hintShown != 3) {
            showHint(3);
        }
        summaryTabDisabledProperty.set(false);
    }
    //</editor-fold>
    
    public void showPart(int nr) {
        
    }
    
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

                tab = new Tab(Lang.hintTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("hint.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
                hintTabWebEngine = web.getEngine();
                hintTabDisabledProperty = tab.disableProperty();
                tabPane.getTabs().add(tab);
                
                tab = new Tab(Lang.summaryTabName);
                web = new WebView();
                web.contextMenuEnabledProperty().set(false);
                web.getEngine().load(getClass().getResource("summary.html").toString());
                tab.setContent(web);
                tab.disableProperty().set(true);
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
        mainClass.getDesktop().add(frame);
        frame.pack();
    }
    //</editor-fold>

    public TextFrame(MainClass mainClass) {
        this.mainClass = mainClass;
        initFrame();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Funkcja scal posortowane ciągi";
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String textTabName = "Treść zadania";
        public static final String hintTabName = "Wskazówki";
        public static final String summaryTabName = "Podsumowanie";
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
    }
    //</editor-fold>
    
}
