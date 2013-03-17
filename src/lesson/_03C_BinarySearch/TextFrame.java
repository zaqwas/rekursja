package lesson._03C_BinarySearch;

import helpers.LessonHelper;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JInternalFrame;
import lesson._03C_BinarySearch.BinarySearchLesson.State;
import mainclass.MainClass;

class TextFrame {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    private byte selectedPart = 1;
    private int part2ShowHints = 0;
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    
    private SelectionModel<Tab> currentPartTabSelectionModel;
    private BooleanProperty currentPartTabPaneVisibleProperty;
    private BooleanProperty currentPartHintTabDisabledProperty;
    
    private SelectionModel<Tab> part1TabSelectionModel;
    private BooleanProperty part1TabPaneVisibleProperty;
    private BooleanProperty part1PseudocodeTabDisabledProperty;
    
    private SelectionModel<Tab> part2TabSelectionModel;
    private BooleanProperty part2TabPaneVisibleProperty;
    private BooleanProperty part2HintTabDisabledProperty;
    private BooleanProperty part2SummaryTabDisabledProperty;
    private WebEngine part2HintTabWebEngine;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentPartTabSelectionModel.select(0);
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
                currentPartTabSelectionModel.select(1);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoHint">
    public void gotoHint(final int hint) {
        assert 1 <= hint && hint <= 3;

        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (currentPartHintTabDisabledProperty.get()) {
                    currentPartHintTabDisabledProperty.set(false);
                    if (selectedPart == 2) {
                        part2ShowHints = 1;
                    }
                }
                if (selectedPart == 2 && hint > part2ShowHints) {
                    part2HintTabWebEngine.executeScript("showHint" + hint + "()");
                    part2ShowHints = hint;
                }
                
                currentPartTabSelectionModel.select(2);
                if (selectedPart == 2) {
                    part2HintTabWebEngine.executeScript("gotoHint" + hint + "()");
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showAllHints">
    public void showAllHints() {
        if (!currentPartHintTabDisabledProperty.get()) {
            if (selectedPart == 1 || part2ShowHints == 3) {
                return;
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (currentPartHintTabDisabledProperty.get()) {
                    currentPartHintTabDisabledProperty.set(false);
                }
                if (selectedPart == 2) {
                    part2HintTabWebEngine.executeScript("showHint3()");
                    part2ShowHints = 3;
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoSummary">
    public void gotoSummary() {
        assert selectedPart == 2;
        
        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (part2HintTabDisabledProperty.get()) {
                    part2HintTabDisabledProperty.set(false);
                }
                if (part2ShowHints < 3) {
                    part2HintTabWebEngine.executeScript("showHint3()");
                    part2ShowHints = 3;
                }
                if (part2SummaryTabDisabledProperty.get()) {
                    part2SummaryTabDisabledProperty.set(false);
                }
                part2TabSelectionModel.select(3);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoPart">
    public void gotoPart(final int part) {
        assert 1 <= part && part <= 2;
        if (part == selectedPart) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (part1PseudocodeTabDisabledProperty.get()) {
                    part1PseudocodeTabDisabledProperty.set(false);
                }
                
                currentPartTabPaneVisibleProperty.set(false);
                switch (part) {
                    case 1:
                        currentPartTabSelectionModel = part1TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part1TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part1PseudocodeTabDisabledProperty;
                        break;
                    case 2:
                        currentPartTabSelectionModel = part2TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part2TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part2HintTabDisabledProperty;
                        break;
                }
                currentPartTabPaneVisibleProperty.set(true);
                selectedPart = (byte) part;
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initialize">
    public void initialize(final byte selectedPart, final State state) {
        assert 1 <= selectedPart && selectedPart <= 2;
        assert state != null;
        
        this.selectedPart = selectedPart;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (state.part == 1) {
                    if (state.hint > 0) {
                        part1PseudocodeTabDisabledProperty.set(false);
                    }
                } else {
                    part1PseudocodeTabDisabledProperty.set(false);
                    part2ShowHints = state.hint;
                    if (part2ShowHints > 0) {
                        part2HintTabDisabledProperty.set(false);
                    }
                    if (part2ShowHints > 1) {
                        LessonHelper.initializeHintTab(part2HintTabWebEngine, part2ShowHints);
                    }
                }
                if (state == State.SummaryShown) {
                    part2SummaryTabDisabledProperty.set(false);
                }
                
                part1TabPaneVisibleProperty.set(false);
                switch (selectedPart) {
                    case 1:
                        currentPartTabSelectionModel = part1TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part1TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part1PseudocodeTabDisabledProperty;
                        break;
                    case 2:
                        currentPartTabSelectionModel = part2TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part2TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part2HintTabDisabledProperty;
                        break;
                }
                currentPartTabPaneVisibleProperty.set(true);
            }
        });
    }
    //</editor-fold>
    
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="initPart1TabPane">    
    private void initPart1TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 14px");
        
        part1TabSelectionModel = tabPane.getSelectionModel();
        part1TabPaneVisibleProperty = tabPane.visibleProperty();
        
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.part1TextTabName);
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part1_text.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part1FunctionsTabName);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part1_functions.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);

        tab = new Tab(Lang.part1PseudocodeTabName);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part1_pseudocode.html").toString());
        tab.setContent(web);
        tab.setDisable(true);
        part1PseudocodeTabDisabledProperty = tab.disableProperty();
        tabPane.getTabs().add(tab);
        
        currentPartTabSelectionModel = part1TabSelectionModel;
        currentPartTabPaneVisibleProperty = part1TabPaneVisibleProperty;
        currentPartHintTabDisabledProperty = part1PseudocodeTabDisabledProperty;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart2TabPane">    
    private void initPart2TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 16px");
        tabPane.setVisible(false);
        
        part2TabSelectionModel = tabPane.getSelectionModel();
        part2TabPaneVisibleProperty = tabPane.visibleProperty();
        
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.part2TextTabName);
        WebView web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_text.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part2FunctionsTabName);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_functions.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part2HintTabName);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_hint.html").toString());
        tab.setContent(web);
        tab.setDisable(true);
        part2HintTabDisabledProperty = tab.disableProperty();
        part2HintTabWebEngine = web.getEngine();
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part2SummaryTabName);
        web = new WebView();
        web.setContextMenuEnabled(false);
        web.getEngine().load(getClass().getResource("part2_summary.html").toString());
        tab.setContent(web);
        tab.setDisable(true);
        part2SummaryTabDisabledProperty = tab.disableProperty();
        tabPane.getTabs().add(tab);
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
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1FunctionsTabName = "Funkcje specjalne";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String part2TextTabName = "Treść zadania";
        public static final String part2FunctionsTabName = "Funkcje specjalne";
        public static final String part2HintTabName = "Wskazówki";
        public static final String part2SummaryTabName = "Podsumowanie";
    }
    //</editor-fold>
    
}
