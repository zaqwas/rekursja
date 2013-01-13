package lesson._07A_PartitionFunction;

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
import mainclass.MainClass;

class TextFrame {

    //<editor-fold defaultstate="collapsed" desc="Enums">
    private static enum State {
        Nothing(0), 
        Hint1(1), Hint2(2), Hint3(3),
        Summary(4);
        
        public final byte Id;
        
        public static State getHint(int nr) {
            assert 1 <= nr && nr <= 3;
            return nr == 1 ? Hint1 : (nr == 2 ? Hint2 : Hint3);
        }

        State(int id) {
            Id = (byte) id;
        }
    }
    //</editor-fold>
    
    private State shownPartState = State.Nothing;
    private byte shownPart = 1;
    private byte currentPart = 1;
    
    private SelectionModel<Tab> currentPartTabSelectionModel;
    private BooleanProperty currentPartTabPaneVisibleProperty;
    private BooleanProperty currentPartHintTabDisabledProperty;
    private WebEngine currentPartHintTabWebEngine;
    
    private MainClass mainClass;
    private JInternalFrame frame;
    
    
    private SelectionModel<Tab> part1TabSelectionModel;
    private BooleanProperty part1TabPaneVisibleProperty;
    private BooleanProperty part1PseudocodeTabDisabledProperty;
    
    private SelectionModel<Tab> part2TabSelectionModel;
    private BooleanProperty part2TabPaneVisibleProperty;
    private BooleanProperty part2HintTabDisabledProperty;
    private WebEngine part2HintTabWebEngine;
    
    private SelectionModel<Tab> part3TabSelectionModel;
    private BooleanProperty part3TabPaneVisibleProperty;
    private BooleanProperty part3HintTabDisabledProperty;
    private BooleanProperty part3SummaryTabDisabledProperty;
    private WebEngine part3HintTabWebEngine;
    
    //public functions:
    
    //<editor-fold defaultstate="collapsed" desc="showFrame">
    public void showFrame() {
        frame.setVisible(true);
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
    public void gotoHint(final int nr) {
        assert 1 <= nr && nr <= 3;

        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (currentPart == shownPart && shownPartState == State.Nothing) {
                    currentPartHintTabDisabledProperty.set(false);
                    shownPartState = State.Hint1;
                }
                currentPartTabSelectionModel.select(2);
                
                if (currentPart == shownPart && shownPartState.Id < nr) {
                    currentPartHintTabWebEngine.executeScript("showHint" + nr + "()");
                    shownPartState = State.getHint(nr);
                }
                if (currentPart != 1) {
                    currentPartHintTabWebEngine.executeScript("gotoHint" + nr + "()");
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showHint">
    public void showHint(final int nr) {
        assert 1 <= nr && nr <= 3;

        if ( currentPart < shownPart || nr <= shownPartState.Id ) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (currentPart == shownPart && shownPartState == State.Nothing) {
                    currentPartHintTabDisabledProperty.set(false);
                    shownPartState = State.Hint1;
                }
                if (currentPart == shownPart && shownPartState.Id < nr) {
                    currentPartHintTabWebEngine.executeScript("showHint" + nr + "()");
                    shownPartState = State.getHint(nr);
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gotoSummary">
    public void gotoSummary() {
        assert currentPart == 3;
        
        frame.setVisible(true);
        frame.toFront();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (shownPartState != State.Summary) {
                    if (shownPartState.Id <= State.Hint3.Id) {
                        currentPartHintTabDisabledProperty.set(false);
                        currentPartHintTabWebEngine.executeScript("showHint3()");
                    }
                    part3SummaryTabDisabledProperty.set(false);
                    shownPartState = State.Summary;
                }
                currentPartTabSelectionModel.select(3);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showSummary">
    public void showSummary() {
        assert currentPart == 3;

        if (shownPartState != State.Summary) {
            return;
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (shownPartState != State.Summary) {
                    if (shownPartState.Id <= State.Hint3.Id) {
                        currentPartHintTabDisabledProperty.set(false);
                        currentPartHintTabWebEngine.executeScript("showHint3()");
                    }
                    part3SummaryTabDisabledProperty.set(false);
                    shownPartState = State.Summary;
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showPart">
    public void showPart(final int nr) {
        assert 1 <= nr && nr <= 3;
        if (nr == currentPart) {
            return;
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                while (shownPart < nr)
                {
                    if (shownPart == 1) {
                        if (shownPartState == State.Nothing) {
                            part1PseudocodeTabDisabledProperty.set(false);
                        }
                    } else if (shownPart == 2) {
                        if (shownPartState == State.Nothing) {
                            part2HintTabDisabledProperty.set(false);
                        }
                        if ( shownPartState != State.Hint3 ) {
                            part2HintTabWebEngine.executeScript("showHint3()");
                        }
                    }
                    shownPartState = State.Nothing;
                    shownPart++;
                } 
                currentPartTabPaneVisibleProperty.set(false);
                
                switch (nr) {
                    case 1:
                        currentPartTabSelectionModel = part1TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part1TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part1PseudocodeTabDisabledProperty;
                        currentPartHintTabWebEngine = null;
                        break;
                    case 2:
                        currentPartTabSelectionModel = part2TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part2TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part2HintTabDisabledProperty;
                        currentPartHintTabWebEngine = part2HintTabWebEngine;
                        break;
                    case 3:
                        currentPartTabSelectionModel = part3TabSelectionModel;
                        currentPartTabPaneVisibleProperty = part3TabPaneVisibleProperty;
                        currentPartHintTabDisabledProperty = part3HintTabDisabledProperty;
                        currentPartHintTabWebEngine = part3HintTabWebEngine;
                        break;
                }
                currentPartTabPaneVisibleProperty.set(true);
                currentPart = (byte)nr;
            }
        });
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="initPart1TabPane">    
    private void initPart1TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 1.2em");
        //tabPane.setTabMinHeight(12d);
        part1TabSelectionModel = tabPane.getSelectionModel();
        part1TabPaneVisibleProperty = tabPane.visibleProperty();
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);
        //tabPane.setSide(Side.BOTTOM);

        Tab tab = new Tab(Lang.part1TextTabName);
        WebView web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part1_text.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part1FunctionsTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part1_functions.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);

        tab = new Tab(Lang.part1PseudocodeTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part1_pseudocode.html").toString());
        tab.setContent(web);
        tab.disableProperty().set(true);
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
        tabPane.setStyle("-fx-tab-min-height: 1.2em");
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
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part2_text.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part2FunctionsTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part2_functions.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);

        tab = new Tab(Lang.part2HintTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part2_hint.html").toString());
        tab.setContent(web);
        tab.disableProperty().set(true);
        part2HintTabDisabledProperty = tab.disableProperty();
        part2HintTabWebEngine = web.getEngine();
        tabPane.getTabs().add(tab);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initPart3TabPane">    
    private void initPart3TabPane(AnchorPane anchorPane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 1.2em");
        tabPane.setVisible(false);
        part3TabSelectionModel = tabPane.getSelectionModel();
        part3TabPaneVisibleProperty = tabPane.visibleProperty();
        AnchorPane.setBottomAnchor(tabPane, 0d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);
        anchorPane.getChildren().add(tabPane);

        Tab tab = new Tab(Lang.part3TextTabName);
        WebView web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part3_text.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part3FunctionsTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part3_functions.html").toString());
        tab.setContent(web);
        tabPane.getTabs().add(tab);

        tab = new Tab(Lang.part3HintTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part3_hint.html").toString());
        tab.setContent(web);
        tab.disableProperty().set(true);
        part3HintTabDisabledProperty = tab.disableProperty();
        part3HintTabWebEngine = web.getEngine();
        tabPane.getTabs().add(tab);
        
        tab = new Tab(Lang.part3SummaryTabName);
        web = new WebView();
        web.contextMenuEnabledProperty().set(false);
        web.getEngine().load(getClass().getResource("part3_summary.html").toString());
        tab.setContent(web);
        tab.disableProperty().set(true);
        part3SummaryTabDisabledProperty = tab.disableProperty();
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
                initPart3TabPane(anchorPane);
                
                Scene scene = new Scene(anchorPane);
                fxPanel.setScene(scene);
            }
        });
        
        frame.setContentPane(fxPanel);
        frame.setPreferredSize(new Dimension(600, 450));
        frame.setResizable(true);
        frame.setVisible(false);
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
        public static final String frameTitle = "Scalanie posortowanych ciągów";
        public static final String frameMenuDescription = "Treść zadania";
        
        public static final String part1TextTabName = "Treść zadania";
        public static final String part1FunctionsTabName = "Funkcje specjalne";
        public static final String part1PseudocodeTabName = "Pseudokod";
        
        public static final String part2TextTabName = "Treść zadania";
        public static final String part2FunctionsTabName = "Funkcje specjalne";
        public static final String part2HintTabName = "Wskazówki";
        
        public static final String part3TextTabName = "Treść zadania";
        public static final String part3FunctionsTabName = "Funkcje specjalne";
        public static final String part3HintTabName = "Wskazówki";
        public static final String part3SummaryTabName = "Podsumowanie";
        
        public static final String loadTabPaneError = "Nie można załadować tej zakładki.";
    }
    //</editor-fold>
    
}
