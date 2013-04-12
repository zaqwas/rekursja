package lesson._04_HanoiTower;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.InterpreterThread;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Stack;
import javafx.animation.ParallelTransitionBuilder;
import javafx.animation.PathTransition;
import javafx.animation.PathTransitionBuilder;
import javafx.animation.StrokeTransition;
import javafx.animation.StrokeTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CheckMenuItemBuilder;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.Duration;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import mainclass.MainClass;
//</editor-fold>

class HanoiFrame {

    //<editor-fold defaultstate="collapsed" desc="Components and variables">
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private boolean noUpdateFlag;
    private boolean semaphoreJavaFX;

    private SimpleIntegerProperty diskNumber = new SimpleIntegerProperty(6);
    private SimpleIntegerProperty startRod = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty finishRod = new SimpleIntegerProperty(2);
    
    private SimpleBooleanProperty componentsDisabledProperty = new SimpleBooleanProperty(false);
    
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();
    private BooleanProperty animationSynchronizedProperty;
    
    private Rectangle disk[] = new Rectangle[6];
    private Rectangle diskRed[] = new Rectangle[6];
    private Rectangle diskGreen[] = new Rectangle[6];
    private Rectangle diskBlue[] = new Rectangle[6];
    private Text rodNumber[] = new Text[3];

    private Menu diskNumberMenu;
    private Menu rodMenu;
    private Menu animationMenu;

    private BigInteger diskNumberBigInt;
    private Stack<Integer>[] stack = new Stack[3];
    //</editor-fold>
    
    public HanoiFrame(MainClass mainClass) {
        this.mainClass = mainClass;
        for (int i = 0; i < 3; i++) {
            stack[i] = new Stack<>();
        }
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

    //<editor-fold defaultstate="collapsed" desc="get diskNumber, start/finish Rod">
    public int getDiskNumber() {
        return diskNumber.get();
    }
    
    public BigInteger getDiskNumberBigInt() {
        return diskNumberBigInt;
    }

    public int getStartRod() {
        return startRod.get();
    }

    public int getFinishRod() {
        return finishRod.get();
    }
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {

        final int start = startRod.get();
        final int size = diskNumber.get();
        for (int i = 0; i < 3; i++) {
            stack[i].clear();
            if (i == start) {
                for (int j = size - 1; j >= 0; j--) {
                    stack[i].push(j);
                }
            }
        }
        diskNumberBigInt = BigInteger.valueOf(size);

        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                componentsDisabledProperty.set(true);
                for (int i = 0; i < size; i++) {
                    translateDisk(disk[i], i, start, size - i - 1);
                    diskGreen[i].setVisible(false);
                }
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    rodNumber[i].setFill(Color.BLACK);
                }
                
                for (int i = 0; i < 3; i++) {
                    int size = stack[i].size();
                    for (int j = 0; j < size; j++) {
                        int d = stack[i].get(j);
                        translateDisk(disk[d], d, i, j);
                    }
                }
                
                int size = diskNumber.get();
                for (int i = 0; i < size; i++) {
                    disk[i].setStroke(Color.BLACK);
                    diskRed[i].setVisible(false);
                    diskGreen[i].setVisible(false);
                    diskBlue[i].setVisible(false);
                }
                
                componentsDisabledProperty.set(false);
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="update">
    public void update() {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    int size = stack[i].size();
                    for (int j = 0; j < size; j++) {
                        int d = stack[i].get(j);
                        translateDisk(disk[d], d, i, j);
                    }
                }
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    
    public void updateAtCall(final int n, final int src, final int dest,
            final boolean afterMove, final boolean afterCall) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    Paint paint = i == src ? Color.DARKRED : i == dest ? Color.DARKGREEN
                            : Color.DARKBLUE;
                    rodNumber[i].setFill(paint);
                }
                
                for (int i = 0; i < 3; i++) {
                    int size = stack[i].size();
                    for (int j = 0; j < size; j++) {
                        int d = stack[i].get(j);
                        translateDisk(disk[d], d, i, j);
                    }
                }
                
                Paint paint;
                int size = diskNumber.get(), stackIdx;
                Rectangle[] diskVisible, diskHide1, diskHide2;
                
                if (!afterMove && !afterCall || afterMove && afterCall) {
                    stackIdx = 3 - src - dest;
                    paint = afterMove ? Color.MEDIUMSEAGREEN : Color.ORANGERED;
                    diskVisible = diskBlue;
                    diskHide1 = diskRed;
                    diskHide2 = diskGreen;
                } else {
                    paint = Color.BLUE;
                    diskHide2 = diskBlue;
                    
                    if ( afterMove ) {
                        stackIdx = dest;
                        diskVisible = diskGreen;
                        diskHide1 = diskRed;
                    } else {
                        stackIdx = src;
                        diskVisible = diskRed;
                        diskHide1 = diskGreen;
                    }
                }
                int stackSize = stack[stackIdx].size();
                
                for (int i = 0; i < size; i++) {
                    if (i < n) {
                        disk[i].setStroke(paint);
                        translateDisk(diskVisible[i], i, stackIdx, n - i - 1 + stackSize);
                        diskVisible[i].setVisible(true);
                    } else {
                        Paint paintDiskCol = i != n ? Color.BLACK
                                : afterMove ? Color.DARKGREEN : Color.FIREBRICK;
                        disk[i].setStroke(paintDiskCol);
                        diskVisible[i].setVisible(false);
                    }
                    diskHide1[i].setVisible(false);
                    diskHide2[i].setVisible(false);
                }
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    
    public void update(final int n, final int src, final int dest, final boolean afterMove,
            final boolean paintRodes, final boolean paintFreeRod) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    Paint paint = Color.BLACK;
                    if (paintRodes) {
                        paint = i == src ? Color.DARKRED : i == dest ? Color.DARKGREEN
                                : paintFreeRod ? Color.DARKBLUE : Color.BLACK;
                    }
                    rodNumber[i].setFill(paint);
                }
                
                for (int i = 0; i < 3; i++) {
                    int size = stack[i].size();
                    for (int j = 0; j < size; j++) {
                        int d = stack[i].get(j);
                        translateDisk(disk[d], d, i, j);
                    }
                }
                
                int size = diskNumber.get();
                int stackIdx = afterMove ? src : dest;
                int stackSize = stack[stackIdx].size() - 1;
                Paint paint = afterMove ? Color.MEDIUMSEAGREEN : Color.ORANGERED;
                Rectangle diskVisible[] = afterMove ? diskRed : diskGreen;
                Rectangle diskNotVisible[] = !afterMove ? diskRed : diskGreen;
                
                for (int i = 0; i < size; i++) {
                    if (i < n) {
                        disk[i].setStroke(paint);
                        translateDisk(diskVisible[i], i, stackIdx, n - i + stackSize);
                        diskVisible[i].setVisible(true);    
                    } else {
                        disk[i].setStroke(Color.BLACK);
                        diskVisible[i].setVisible(false);
                    }
                    diskNotVisible[i].setVisible(false);
                    diskBlue[i].setVisible(false);
                }
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="moveValue fuctions">
    
    public void moveDisk(int src, int dest) {
        int movingDisk = stack[src].pop();
        stack[dest].push(movingDisk);
    }
    
    public boolean isStackEmpty(int stackIdx) {
        return stack[stackIdx].empty();
    }
    
    public boolean isBiggerDisk(int src, int dest) {
        return !stack[dest].empty() && stack[dest].peek() < stack[src].peek();
    }
    
    public void updateAfterAnimate(int dest) {
        int movingDisk = stack[dest].peek();
        disk[movingDisk].setStroke(Color.BLACK);
        diskGreen[movingDisk].setVisible(false);
        diskRed[movingDisk].setVisible(false);
    }
    
    public void animateMove(final int src, final int dest, final boolean paint, 
            final boolean paintFreeRod, final int delayTime) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                final int movingDisk = stack[dest].pop();
                stack[src].push(movingDisk);
                
                for (int i = 0; i < 3; i++) {
                    int size = stack[i].size();
                    for (int j = 0; j < size; j++) {
                        int d = stack[i].get(j);
                        translateDisk(disk[d], d, i, j);
                    }
                }
                
                stack[src].pop();
                stack[dest].push(movingDisk);
                
                disk[movingDisk].setStroke(Color.ORANGERED);
                translateDisk(diskGreen[movingDisk], movingDisk, dest, stack[dest].size() - 1);
                diskGreen[movingDisk].setVisible(true);
                translateDisk(diskRed[movingDisk], movingDisk, src, stack[src].size());
                diskRed[movingDisk].setVisible(true);
        
                if (paint) {
                    for (int i = 0; i < 3; i++) {
                        Paint paint = i == src ? Color.DARKRED : i == dest ? Color.DARKGREEN
                                : paintFreeRod ? Color.DARKBLUE : Color.BLACK;
                        rodNumber[i].setFill(paint);
                    }
                    
                    int size = diskNumber.get();
                    for (int i = 0; i < size; i++) {
                        diskBlue[i].setVisible(false);
                        if (i == movingDisk) {
                            continue;
                        }

                        if (i < movingDisk) {
                            disk[i].setStroke(Color.DARKBLUE);
                        } else {
                            disk[i].setStroke(Color.BLACK);
                        }
                        diskGreen[i].setVisible(false);
                        diskRed[i].setVisible(false);
                    }
                }
                
                double xSrc, ySrc, xDest, yDest;
                
                xSrc = getDiskPositionX(src);
                ySrc = getDiskPositionY(stack[src].size());
                
                xDest = getDiskPositionX(dest);
                yDest = getDiskPositionY(stack[dest].size() - 1);

                EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        semaphoreJavaFX = false;
                    }
                };

                int delay = animationSynchronizedProperty.get() ? delayTime
                        : InterpreterThread.getDelayTime(animationTime.get());
                Duration durationAnimation = Duration.millis(delay);
                
                Path path = PathBuilder.create()
                        .elements(new MoveTo(xSrc,ySrc), new VLineTo(7), 
                            new HLineTo(xDest), new VLineTo(yDest)
                        ).build();
                
                PathTransition pathTransition = PathTransitionBuilder.create()
                        .duration(durationAnimation)
                        .path(path)
                        .orientation(PathTransition.OrientationType.NONE)
                        .build();
                
                StrokeTransition strokeTransition = StrokeTransitionBuilder.create()
                        .duration(durationAnimation)
                        .fromValue(Color.ORANGERED)
                        .toValue(Color.MEDIUMSEAGREEN)
                        .build();
                
                ParallelTransitionBuilder.create()
                        .node(disk[movingDisk])
                        .children(pathTransition, strokeTransition)
                        .onFinished(event)
                        .build().play();
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }
    
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="getPosition / translate">
    private int getDiskPositionX(int rod) {
        return 42 + 83 * rod;
    }
    
    private int getDiskPositionY(int yPosition) {
        return 85 - yPosition * 12;
    }
    
    private void translateDisk(Rectangle node, int disk, int rod, int yPosition) {
        node.setTranslateX(32 + 83 * rod - 6 * disk);
        node.setTranslateY(80 - yPosition * 12);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateTowerAfterSettningsChanged">
    private void updateTowerAfterSettningsChanged() {
        int size = diskNumber.get();
        int idxSrc = startRod.get();
        int idxDest = finishRod.get();
        
        for (int i = 0; i < 6; i++) {
            if (i < size) {
                translateDisk(disk[i], i, idxSrc, size - i - 1);
                disk[i].setVisible(true);
            } else {
                disk[i].setVisible(false);
            }
            
            if (i < size) {
                translateDisk(diskGreen[i], i, idxDest, size - i - 1);
                diskGreen[i].setVisible(true);
            } else {
                diskGreen[i].setVisible(false);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hideMenu">
    private void hideMenu() {
        if (diskNumberMenu == null || rodMenu == null || animationMenu == null) {
            return;
        }
        Menu menu = null;
        if (diskNumberMenu.isShowing()) {
            menu = diskNumberMenu;
        } else if (rodMenu.isShowing()) {
            menu = rodMenu;
        } else if (animationMenu.isShowing()) {
            menu = animationMenu;
        }
        if (menu != null) {
            final Menu menuThread = menu;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    menuThread.hide();
                }
            });
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
                updateTowerAfterSettningsChanged();
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
                if (noUpdateFlag) {
                    return;
                }
                int idx = (Integer)t1.getUserData();
                startRod.set(idx);
                if (finishRod.get() == idx) {
                    int idx2 = (Integer)t.getUserData();
                    finishRod.set(idx2);
                    noUpdateFlag = true;
                    finishToggleGroup.getToggles().get(idx2).setSelected(true);
                    noUpdateFlag = false;
                }
                updateTowerAfterSettningsChanged();
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
                if (noUpdateFlag) {
                    return;
                }
                int idx = (Integer)t1.getUserData();
                finishRod.set(idx);
                if (startRod.get() == idx) {
                    int idx2 = (Integer)t.getUserData();
                    startRod.set(idx2);
                    noUpdateFlag = true;
                    startToggleGroup.getToggles().get(idx2).setSelected(true);
                    noUpdateFlag = false;
                }
                updateTowerAfterSettningsChanged();
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
            Line line = lineBuilder.build();
            line.setLayoutX(42d + 83d * i);
            anchorPane.getChildren().add(line);
        }
        
        lineBuilder = LineBuilder.create()
                .endX(246d).strokeWidth(6d).strokeLineCap(StrokeLineCap.BUTT)
                .layoutY(95d).layoutX(2d);
        anchorPane.getChildren().add(lineBuilder.build());
        
        TextBuilder textBuilder = TextBuilder.create().layoutY(28d)
                .font(Font.font(null, FontWeight.BOLD, 32d));
        for (int i = 0; i < 3; i++) {
            Text text = textBuilder.build();
            text.setText(String.valueOf(i+1));
            text.setLayoutX(6d + 83d * i);
            
            anchorPane.getChildren().add(text);
            rodNumber[i] = text;
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
        
        rectangleBuilder.stroke(Color.BLUE);
        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = rectangleBuilder.build();
            rectangle.setWidth(20d + 12d * i);
            
            anchorPane.getChildren().add(rectangle);
            diskBlue[i] = rectangle;
        }
        
        rectangleBuilder.stroke(Color.MEDIUMSEAGREEN).visible(true);
        for (int i = 0; i < 6; i++) {
            Rectangle rectangle = rectangleBuilder.build();
            rectangle.setWidth(20d + 12d * i);
            translateDisk(rectangle, i, 2, 5 - i);
            
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
            translateDisk(rectangle, i, 0, 5 - i);
            
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
