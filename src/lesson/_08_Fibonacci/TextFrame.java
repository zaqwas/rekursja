package lesson._08_Fibonacci;

import helpers.LessonHelper;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JInternalFrame;
import lesson._08_Fibonacci.FibonacciLesson.State;
import mainclass.MainClass;

class TextFrame {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private SimpleIntegerProperty selectedPart = new SimpleIntegerProperty(1);
    
    private SelectionModel<Tab> tabSelectionModels[] = new SelectionModel[4];
    private BooleanProperty hintTabDisabledProperties[] = new BooleanProperty[4];
    private WebEngine hintTabWebEngines[] = new WebEngine[4];
    
    private int shownHints[] = {0, 0, 0, 0};
    private int maxHints[] = {0, 2, 2, 1};
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
        if (selectedPart.get() == 1) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int part = selectedPart.get() - 1;
                tabSelectionModels[part].select(0);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoHint">
    public void gotoHint(final int nr) {
        assert selectedPart.get() > 1;
        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int part = selectedPart.get() - 1;
                if (shownHints[part] == 0) {
                    hintTabDisabledProperties[part].set(false);
                    shownHints[part] = 1;
                }
                if (nr > shownHints[part]) {
                    hintTabWebEngines[part].executeScript("showHint2()");
                    shownHints[part] = 2;
                }
                tabSelectionModels[part].select(1);
                if (maxHints[part] > 1) {
                    hintTabWebEngines[part].executeScript("gotoHint" + nr + "()");
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showAllHints">
    public void showAllHints() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int part = selectedPart.get() - 1;
                if (shownHints[part] == 0) {
                    hintTabDisabledProperties[part].set(false);
                    shownHints[part] = 1;
                }
                if (maxHints[part] > shownHints[part]) {
                    hintTabWebEngines[part].executeScript("showHint2()");
                    shownHints[part] = 2;
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoPrevPart">
    public void gotoPrevPart() {
        assert selectedPart.get() > 1;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                selectedPart.set(selectedPart.get() - 1);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoNextPart">
    public void gotoNextPart() {
        assert selectedPart.get() < 4;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int part = selectedPart.get() - 1;
                int maxHint = maxHints[part];
                if (maxHint > shownHints[part]) {
                    hintTabDisabledProperties[part].set(false);
                    if (maxHint > 1) {
                        hintTabWebEngines[part].executeScript("showHint2()");
                    }
                    shownHints[part] = maxHint;
                }
                selectedPart.set(selectedPart.get() + 1);
            }
        });
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initialize">
    public void initialize(final int selectedPart, final State state) {
        assert 1 <= selectedPart && selectedPart <= 4;
        assert state != null;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int shownPart = state.part - 1;
                for (int part = 1; part <= shownPart; part++) {
                    int hint = part == shownPart ? state.hint : maxHints[part];
                    if (hint > 0) {
                        hintTabDisabledProperties[part].set(false);
                        if (hint > 1) {
                            LessonHelper.initializeHintTab(hintTabWebEngines[part], hint);
                        }
                        shownHints[part] = hint;
                    }
                }
                
                TextFrame.this.selectedPart.set(selectedPart);
            }
        });
    }
    //</editor-fold>
    
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="initPart1TabPane">    
    private void initPart1TabPane(AnchorPane anchorPane) {
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part1_text.html").toString());
        web.visibleProperty().bind(selectedPart.isEqualTo(1));
        
        AnchorPane.setBottomAnchor(web, 0d);
        AnchorPane.setLeftAnchor(web, 0d);
        AnchorPane.setRightAnchor(web, 0d);
        AnchorPane.setTopAnchor(web, 0d);
        anchorPane.getChildren().add(web);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2TabPane">    
    private void initPart2TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinHeight(16d);
        tabPane.visibleProperty().bind(selectedPart.isEqualTo(2));
        tabSelectionModels[1] = tabPane.getSelectionModel();
        
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.textTabName);
        tabPane.getTabs().add(tab);
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_text.html").toString());
        tab.setContent(web);
        
        tab = new Tab(Lang.hintsTabName);
        tab.setDisable(true);
        hintTabDisabledProperties[1] = tab.disableProperty();
        tabPane.getTabs().add(tab);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_hint.html").toString());
        hintTabWebEngines[1] = web.getEngine();
        tab.setContent(web);
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart3TabPane">    
    private void initPart3TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinHeight(16d);
        tabPane.visibleProperty().bind(selectedPart.isEqualTo(3));
        tabSelectionModels[2] = tabPane.getSelectionModel();
        
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.textTabName);
        tabPane.getTabs().add(tab);
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part3_text.html").toString());
        tab.setContent(web);
        
        tab = new Tab(Lang.pseudocodeTabName);
        tab.setDisable(true);
        hintTabDisabledProperties[2] = tab.disableProperty();
        tabPane.getTabs().add(tab);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part3_hint.html").toString());
        hintTabWebEngines[2] = web.getEngine();
        tab.setContent(web);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2TabPane">    
    private void initPart4TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinHeight(16d);
        tabPane.visibleProperty().bind(selectedPart.isEqualTo(4));
        tabSelectionModels[3] = tabPane.getSelectionModel();
        
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.textTabName);
        tabPane.getTabs().add(tab);
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part4_text.html").toString());
        tab.setContent(web);
        
        tab = new Tab(Lang.pseudocodeTabName);
        tab.setDisable(true);
        hintTabDisabledProperties[3] = tab.disableProperty();
        tabPane.getTabs().add(tab);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part4_pseudocode.html").toString());
        tab.setContent(web);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="initFrame">
    private void initFrame()
    {
        frame = new JInternalFrame(Lang.frameTitle);
        final JFXPanel fxPanel = new JFXPanel();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AnchorPane anchorPane = new AnchorPane();
                
                initPart1TabPane(anchorPane);
                initPart2TabPane(anchorPane);
                initPart3TabPane(anchorPane);
                initPart4TabPane(anchorPane);
                
                Scene scene = new Scene(anchorPane);
                fxPanel.setScene(scene);
            }
        });
        
        frame.setContentPane(fxPanel);
        frame.setPreferredSize(new Dimension(600, 450));
        frame.setResizable(true);
        frame.setVisible(false);
        frame.pack();
        mainClass.addAddictionalLessonFrame(Lang.frameTitle, frame);
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
        
        public static final String textTabName = "Treść zadania";
        public static final String pseudocodeTabName = "Pseudokod";
        public static final String hintsTabName = "Wskazówki";
    }
    //</editor-fold>
    
}
