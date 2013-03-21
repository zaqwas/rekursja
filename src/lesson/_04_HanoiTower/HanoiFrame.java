package lesson._04_HanoiTower;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CheckMenuItemBuilder;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontBuilder;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import lesson._06A_MergeFunction.ArrayFrame;
import mainclass.MainClass;
//</editor-fold>

class HanoiFrame {

    //<editor-fold defaultstate="collapsed" desc="Components and variables">
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private boolean noUpdateProperty;
    private boolean semaphoreJavaFX;

    
    private SimpleIntegerProperty diskNumber = new SimpleIntegerProperty(6);
    private SimpleIntegerProperty startRod = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty finishRod = new SimpleIntegerProperty(2);
    
    private SimpleBooleanProperty componentsDisabledProperty = new SimpleBooleanProperty(false);
    
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();
    private BooleanProperty animationSynchronizedProperty;

    private Menu diskNumberMenu;
    private Menu rodMenu;
    private Menu animationMenu;

    private BigInteger arraySizeBigInt;
    private int arrayValue[] = new int[32];
    
    private Line rod[] = new Line[3];
    private Rectangle disk[] = new Rectangle[6];
    private Rectangle diskRed[] = new Rectangle[6];
    private Rectangle diskGreen[] = new Rectangle[6];
    private Text rodNumber[] = new Text[3];
    //</editor-fold>


    public HanoiFrame(MainClass mainClass) {
        this.mainClass = mainClass;
        initFrame();
    }


    //<editor-fold defaultstate="collapsed" desc="getFrame">
    public JInternalFrame getFrame() {
        return frame;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="frameToFront">
    public void frameToFront() {
        frame.setVisible(true);
        frame.toFront();
    }
    //</editor-fold>
    
    
    //private functions:
    
    private double getDiskPositionX(int disk, int stack) {
        return 32d + 83 * stack - 6d * disk;
    }
    
    private double getDiskPositionY(int yPosition) {
        return 20d + yPosition * 12d;
    }
    
    private void updateTower() {
        int size = diskNumber.get();
        int idxSrc = startRod.get();
        int idxDest = finishRod.get();
        
        for (int i = 0; i < 6; i++) {
            if ( i<size ) {
                disk[i].setTranslateX(getDiskPositionX(i, idxSrc));
                disk[i].setTranslateY(getDiskPositionY(6 + i - size));
                disk[i].setVisible(true);
            } else {
                disk[i].setVisible(false);
            }
            
            if ( i<size ) {
                diskGreen[i].setTranslateX(getDiskPositionX(i, idxDest));
                diskGreen[i].setTranslateY(getDiskPositionY(6 + i - size));
                diskGreen[i].setVisible(true);
            } else {
                diskGreen[i].setVisible(false);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="hideMenu">
    private void hideMenu() {
        if (diskNumberMenu == null || rodMenu == null) {
            return;
        }
        Runnable run = null;
        if (diskNumberMenu.isShowing()) {
            run = new Runnable() {
                @Override
                public void run() {
                    diskNumberMenu.hide();
                }
            };
        } else if (rodMenu.isShowing()) {
            run = new Runnable() {
                @Override
                public void run() {
                    rodMenu.hide();
                }
            };
        }
        if (run != null) {
            Platform.runLater(run);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="initFXPanel">
    private void initFXPanel(JFXPanel fxPanel) {
        VBox vbox = new VBox();
        
        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init disk number menu">
        diskNumberMenu = new Menu("Rozmiar");

        Slider slider = SliderBuilder.create()
                .min(1).max(6).value(6)
                .majorTickUnit(1)
                .minorTickCount(0)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(150d)
                .build();
        slider.disableProperty().bind(componentsDisabledProperty);
        slider.valueProperty().bindBidirectional(diskNumber);
        diskNumber.set(6);
        diskNumber.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                updateTower();
            }
        });
        

        StringExpression strExpression = Bindings.format("Liczba krążków: %2d", diskNumber);
        Label label = new Label();
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        diskNumberMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(diskNumberMenu);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init rod menu">
        rodMenu = new Menu("Słupek");
        
        final ToggleGroup startToggleGroup = new ToggleGroup();
        final ToggleGroup finishToggleGroup = new ToggleGroup();
        
        vboxMenu = new VBox();
        label = new Label("Słupek początkowy:");
        vboxMenu.getChildren().add(label);
        HBox hBoxMenu = new HBox();
        vboxMenu.getChildren().add(hBoxMenu);
        for (int i = 0; i < 3; i++) {
            RadioButton radioButton = new RadioButton(String.valueOf(i+1));
            radioButton.setSelected(i == 0);
            radioButton.setUserData(i);
            radioButton.disableProperty().bind(componentsDisabledProperty);
            hBoxMenu.getChildren().add(radioButton);
            startToggleGroup.getToggles().add(radioButton);
        }
        startToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                if (noUpdateProperty) {
                    return;
                }
                int idx = (Integer)t1.getUserData();
                startRod.set(idx);
                if (finishRod.get() == idx) {
                    int idx2 = (Integer)t.getUserData();
                    finishRod.set(idx2);
                    noUpdateProperty = true;
                    finishToggleGroup.getToggles().get(idx2).setSelected(true);
                    noUpdateProperty = false;
                }
                updateTower();
            }
        });
        rodMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        
        rodMenu.getItems().add(new SeparatorMenuItem());
        
        vboxMenu = new VBox();
        label = new Label("Słupek docelowy:");
        vboxMenu.getChildren().add(label);
        hBoxMenu = new HBox();
        vboxMenu.getChildren().add(hBoxMenu);
        for (int i = 0; i < 3; i++) {
            RadioButton radioButton = new RadioButton(String.valueOf(i+1));
            radioButton.setSelected(i == 2);
            radioButton.setUserData(i);
            radioButton.disableProperty().bind(componentsDisabledProperty);
            hBoxMenu.getChildren().add(radioButton);
            finishToggleGroup.getToggles().add(radioButton);
        }
        finishToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                if (noUpdateProperty) {
                    return;
                }
                int idx = (Integer)t1.getUserData();
                finishRod.set(idx);
                if (startRod.get() == idx) {
                    int idx2 = (Integer)t.getUserData();
                    startRod.set(idx2);
                    noUpdateProperty = true;
                    startToggleGroup.getToggles().get(idx2).setSelected(true);
                    noUpdateProperty = false;
                }
                updateTower();
            }
        });
        rodMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        
        //rodMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        menuBar.getMenus().add(rodMenu);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init animation menu">
        animationMenu = new Menu("Animacja");

        CheckMenuItem synchronizeMenuItem = CheckMenuItemBuilder.create()
                .text("Synchronizuj czas animacji")
                .selected(true)
                .build();
        animationSynchronizedProperty = synchronizeMenuItem.selectedProperty();
        animationMenu.getItems().add(synchronizeMenuItem);
        
        slider = SliderBuilder.create()
                .min(0).max(19).value(10)
                .majorTickUnit(1)
                .minorTickCount(0)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(200d)
                .build();
        slider.valueProperty().bindBidirectional(animationTime);
        slider.disableProperty().bind(synchronizeMenuItem.selectedProperty());
        animationTime.set(10);
        
        label = new Label("Czas animacji:");
        label.setMaxWidth(Double.MAX_VALUE);

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        animationMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(animationMenu);
        //</editor-fold>

        
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(250d);
        anchorPane.setPrefHeight(100d);
        vbox.getChildren().add(anchorPane);
        
        LineBuilder lineBuilder = LineBuilder.create()
                .endY(76d).strokeWidth(6d).strokeLineCap(StrokeLineCap.ROUND)
                .layoutY(16d);
        for (int i = 0; i < 3; i++) {
            rod[i] = lineBuilder.build();
            rod[i].setLayoutX(42d + 83d * i);
            anchorPane.getChildren().add(rod[i]);
        }
        
        lineBuilder = LineBuilder.create()
                .endX(246d).strokeWidth(6d).strokeLineCap(StrokeLineCap.BUTT)
                .layoutY(95d).layoutX(2d);
        anchorPane.getChildren().add(lineBuilder.build());
        
        TextBuilder textBuilder = TextBuilder.create().layoutY(28d)
                .font(Font.font(null, FontWeight.BOLD, 32d));
        for (int i = 0; i < 3; i++) {
            rodNumber[i] = textBuilder.build();
            rodNumber[i].setText(String.valueOf(i+1));
            rodNumber[i].setLayoutX(6d + 83d * i);
            
            anchorPane.getChildren().add(rodNumber[i]);
        }
        
        RectangleBuilder rectangleBuilder = RectangleBuilder.create()
                .height(10d).fill(Color.rgb(0, 0, 0, 0d))
                .strokeType(StrokeType.INSIDE).strokeWidth(2d)
                .arcHeight(10d).arcWidth(10d)
                .strokeDashArray(4d, 2d).strokeLineCap(StrokeLineCap.BUTT)
                .stroke(Color.ORANGERED).visible(false);
        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = rectangleBuilder.build();
            rectangle.setWidth(20d + 12d * i);
            
            anchorPane.getChildren().add(rectangle);
            diskRed[i] = rectangle;
        }
        rectangleBuilder.stroke(Color.FORESTGREEN).visible(true);
        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = rectangleBuilder.build();
            rectangle.setWidth(20d + 12d * i);
            rectangle.setTranslateX(getDiskPositionX(i, 2));
            rectangle.setTranslateY(getDiskPositionY(i));
            
            anchorPane.getChildren().add(rectangle);
            diskGreen[i] = rectangle;
        }
        
        
        rectangleBuilder = RectangleBuilder.create()
                .height(10d).fill(Color.LIGHTGREY)
                .arcHeight(10d).arcWidth(10d)
                .stroke(Color.BLACK).strokeType(StrokeType.INSIDE).strokeWidth(2d);
        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = rectangleBuilder.build();
            rectangle.setWidth(20d + 12d * i);
            rectangle.setTranslateX(getDiskPositionX(i, 0));
            rectangle.setTranslateY(getDiskPositionY(i));
            
            anchorPane.getChildren().add(rectangle);
            disk[i] = rectangle;
        }
        
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        fxPanel.setScene(scene);
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
                initFXPanel(fxPanel);
                frame.pack();
            }
        });
        frame.setContentPane(fxPanel);
        frame.setResizable(false);
        frame.setVisible(false);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                hideMenu();
            }
        });
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                hideMenu();
            }
        });
        mainClass.addAddictionalLessonFrame(Lang.frameTitle, frame);
    }
    //</editor-fold>


    //Langugage class:

    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Wieże Hanoi";
    }
    //</editor-fold>

}
