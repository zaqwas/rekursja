package lesson._06A_MergeFunction;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.InterpreterThread;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CheckMenuItemBuilder;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import mainclass.MainClass;
//</editor-fold>

public class ArrayFrame {

    //<editor-fold defaultstate="collapsed" desc="Components and variables">
    private MainClass mainClass;
    private JInternalFrame frame;
    
    private boolean semaphoreJavaFX;
    private boolean noCheckTextFieldFlag = false;
    
    private SimpleBooleanProperty componentsDisabledProperty = new SimpleBooleanProperty(false);
    
    private SimpleIntegerProperty randMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty array1Size = new SimpleIntegerProperty();
    private SimpleIntegerProperty array2Size = new SimpleIntegerProperty();
    
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();
    private BooleanProperty animationSynchronizedProperty;
    
    private Menu arraySizeMenu;
    private Menu randomValuesMenu;
    private Menu animationMenu;
    
    private DoubleProperty array1LayoutYProperty;
    private DoubleProperty array2LayoutYProperty;
    private DoubleProperty arrayResultLayoutYProperty;
    
    private TextField flyingTextField;
    private TextField array1TextField[] = new TextField[16];
    private TextField array2TextField[] = new TextField[16];
    private TextField arrayResultTextField[] = new TextField[32];
    
    private BigInteger array1SizeBigInt;
    private BigInteger array2SizeBigInt;
    private BigInteger arrayReslutSizeBigInt;
    
    private int array1Value[] = new int[16];
    private int array2Value[] = new int[16];
    private boolean array1Removed[] = new boolean[16];
    private boolean array2Removed[] = new boolean[16];
    private String arrayResultString[] = new String[32];
    private int arrayResultValue[] = new int[32];
    //</editor-fold>

    
    public ArrayFrame(MainClass mainClass) {
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
    
    //<editor-fold defaultstate="collapsed" desc="arrays size and values fuctions">
    
    public int getArraySize(boolean firstTab) {
        return firstTab ? array1Size.get() : array2Size.get();
    }
    public BigInteger getArraySizeBigInt(boolean firstTab) {
        return firstTab ? array1SizeBigInt : array2SizeBigInt;
    }
    
    public int getArrayResultSize() {
        return array1Size.get() + array2Size.get();
    }
    public BigInteger getArrayReslutSizeBigInt(){
        return arrayReslutSizeBigInt;
    }
    
    
    public int getArrayValue(int index, boolean firstTab) {
        return firstTab ? array1Value[index] : array2Value[index];
    }
    public int getArrayReslutValue(int index) {
        return arrayResultValue[index];
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        semaphoreJavaFX = true;
        
        Arrays.fill(array1Removed, false);
        Arrays.fill(array2Removed, false);
        Arrays.fill(arrayResultString, null);
        Arrays.fill(arrayResultValue, -1);
        
        array1SizeBigInt = BigInteger.valueOf(array1Size.get());
        array2SizeBigInt = BigInteger.valueOf(array2Size.get());
        arrayReslutSizeBigInt = BigInteger.valueOf(array1Size.get() + array2Size.get());
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                componentsDisabledProperty.set(true);
                
                for (int i = 0; i < 16; i++) {
                    arrayResultTextField[i].setText("");
                    arrayResultTextField[i + 16].setText("");
                    arrayResultTextField[i].setId("empty");
                    arrayResultTextField[i + 16].setId("empty");
                }
                
                semaphoreJavaFX = false;
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        semaphoreJavaFX = true;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 16; i++) {
                    array1TextField[i].setId("first");
                    array2TextField[i].setId("second");
                    arrayResultTextField[i].setText(arrayResultString[i]);
                    arrayResultTextField[i + 16].setText(arrayResultString[i + 16]);
                }
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultString[i]);
                    if (arrayResultString[i] != null) {
                        arrayResultTextField[i].setId("result");
                    }
                }
                componentsDisabledProperty.set(false);
                
                semaphoreJavaFX = false;
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateResultValues">
    public void updateResultValues() {
        semaphoreJavaFX = true;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultString[i]);
                    if (arrayResultString[i] != null) {
                        arrayResultTextField[i].setId("result");
                    }
                }
                for (int i = 0; i < 16; i++) {
                    if (array1Removed[i]) {
                        array1TextField[i].setId("empty");
                    }
                    if (array2Removed[i]) {
                        array2TextField[i].setId("empty");
                    }
                }
                semaphoreJavaFX = false;
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="compare functions">
    
    public void compareStart(final int idx1, final int idx2) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                array1TextField[idx1].setId("comparing");
                array2TextField[idx2].setId("comparing");
                semaphoreJavaFX = false;
            }
        });
        waitForJavaFX();
    }

    public void compareStop(final int idx1, final int idx2) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String id = array1Removed[idx1] ? "empty" : "first";
                array1TextField[idx1].setId(id);

                id = array2Removed[idx2] ? "empty" : "second";
                array2TextField[idx2].setId(id);

                semaphoreJavaFX = false;
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="moveValue fuctions">
    
    public void moveValue(int idxSrc, boolean firstTab, int idxDest) {
        boolean removedArray[] = firstTab ? array1Removed : array2Removed;
        removedArray[idxSrc] = true;
        
        TextField textArray[] = firstTab ? array1TextField : array2TextField;
        arrayResultString[idxDest] = textArray[idxSrc].getText();
        
        int array[] = firstTab ? array1Value : array2Value;
        arrayResultValue[idxDest] = array[idxSrc];
    }
    
    public void undoMoveValue(int idxSrc, boolean firstTab, int idxDest) {
        boolean removedArray[] = firstTab ? array1Removed : array2Removed;
        removedArray[idxSrc] = false;
        
        arrayResultString[idxDest] = null;
    }
    
    public boolean isValueRemoved(int index, boolean firstTab) {
        return firstTab ? array1Removed[index] : array2Removed[index];
    }
    public boolean isResultValueSet(int index) {
        return arrayResultString[index] != null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="animateMove">
    
    public void animateMove(final int idxSrc, final boolean firstTab, final int idxDest, final int delayTime) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double xSrc, ySrc, xDest, yDest;

                TextField arrayTextField[] = firstTab ? array1TextField : array2TextField;
                boolean arrayRemoved[] = firstTab ? array1Removed : array2Removed;
                DoubleProperty arrayLayoutYProperty = firstTab ? array1LayoutYProperty : array2LayoutYProperty;
                
                TextField testSrc = arrayTextField[idxSrc];
                xSrc = testSrc.getLayoutX();
                ySrc = testSrc.getLayoutY();
                ySrc += arrayLayoutYProperty.get();

                TextField testDest = arrayResultTextField[idxDest];
                xDest = testDest.getLayoutX();
                yDest = testDest.getLayoutY();
                yDest += arrayResultLayoutYProperty.get();
                
                arrayTextField[idxSrc].setId("empty");
                arrayRemoved[idxSrc] = true;
                
                final String text = arrayTextField[idxSrc].getText();
                
                flyingTextField.setText(text);
                flyingTextField.setId(firstTab ? "first" : "second");
                flyingTextField.setVisible(true);
                
                EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        flyingTextField.setVisible(false);
                        arrayResultString[idxDest] = text;
                        arrayResultTextField[idxDest].setText(text);
                        arrayResultTextField[idxDest].setId("result");
                        semaphoreJavaFX = false;
                    }
                };

                int delay = animationSynchronizedProperty.get() ? delayTime
                        : InterpreterThread.getDelayTime(animationTime.get());
                
                TranslateTransitionBuilder.create()
                        .duration(Duration.millis(delay))
                        .node(flyingTextField)
                        .fromX(xSrc).toX(xDest)
                        .fromY(ySrc).toY(yDest)
                        .onFinished(event)
                        .build()
                        .play();
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="RandomValuesAction">
    private class RandomValuesAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent t) {
            try {
                noCheckTextFieldFlag = true;

                Random rand = new Random();
                int maxValue = randMaxValue.get() + 1;
                
                int size = array1Size.get();
                for (int i = 0; i < size; i++) {
                    array1Value[i] = rand.nextInt(maxValue);
                }
                Arrays.sort(array1Value, 0, size);
                for (int i = 0; i < size; i++) {
                    String strValue = Integer.toString(array1Value[i]);
                    array1TextField[i].setText(strValue);
                }

                size = array2Size.get();
                for (int i = 0; i < size; i++) {
                    array2Value[i] = rand.nextInt(maxValue);
                }
                Arrays.sort(array2Value, 0, size);
                for (int i = 0; i < size; i++) {
                    String strValue = Integer.toString(array2Value[i]);
                    array2TextField[i].setText(strValue);
                }
            } finally {
                noCheckTextFieldFlag = false;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ArraySizeChangeListener">
    private class ArraySizeChangeListener implements ChangeListener<Number> {
        
        private final TextField arrayTextField[];
        private final int arrayValue[];
        
        public ArraySizeChangeListener(boolean firstArray) {
            arrayTextField = firstArray ? array1TextField : array2TextField;
            arrayValue = firstArray ? array1Value : array2Value;
        }
        
        @Override
        public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
            int idxFrom = (int)oldValue;
            int idxTo = (int)newValue;
            
            if (idxFrom >= idxTo) {
                return;
            }
            
            try {
                noCheckTextFieldFlag = true;
                for (int i = idxFrom; i < idxTo; i++) {
                    int idx = i;
                    int value = arrayValue[idx];
                    String str = arrayTextField[idx].getText();
                    while (idx > 0 && arrayValue[idx - 1] > value) {
                        arrayValue[idx] = arrayValue[idx - 1];
                        arrayTextField[idx].setText(arrayTextField[idx - 1].getText());
                        idx--;
                    }
                    if (i != idx) {
                        arrayValue[idx] = value;
                        arrayTextField[idx].setText(str);
                    }
                }
            } finally {
                noCheckTextFieldFlag = false;
            }
        }
    };
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TextFieldStringChangeListener">
    private class TextFieldStringChangeListener implements ChangeListener<String> {
        
        private final SimpleIntegerProperty arraySize;
        private final TextField arrayTextField[];
        private final int arrayValue[];
        private final int index;

        public TextFieldStringChangeListener(int index, boolean firstArray) {
            this.index = index;
            arraySize = firstArray ? array1Size : array2Size;
            arrayTextField = firstArray ? array1TextField : array2TextField;
            arrayValue = firstArray ? array1Value : array2Value;
        }

        @Override
        public void changed(ObservableValue<? extends String> ov, String oldStr, String newStr) {
            if (noCheckTextFieldFlag) {
                return;
            }
            try {
                noCheckTextFieldFlag = true;
                TextField text = arrayTextField[index];

                boolean oldValue = false;
                if (newStr.length() > 2) {
                    oldValue = true;
                } else {
                    for (char c : newStr.toCharArray()) {
                        if (c < '0' || '9' < c) {
                            oldValue = true;
                            break;
                        }
                    }
                }
                if (oldValue) {
                    text.setText(oldStr);
                    return;
                }

                String changeStr = null;
                if (newStr.length() == 2) {
                    if (newStr.charAt(0) == '0') {
                        changeStr = newStr.substring(1);
                    }
                } else if (newStr.isEmpty()) {
                    changeStr = "0";
                }
                if (changeStr != null) {
                    newStr = changeStr;
                }

                int idx = index;
                int value = Integer.parseInt(newStr);

                while (idx > 0 && arrayValue[idx - 1] > value) {
                    arrayValue[idx] = arrayValue[idx - 1];
                    arrayTextField[idx].setText(arrayTextField[idx - 1].getText());
                    idx--;
                }
                int size = arraySize.get() - 1;
                while (idx < size && value > arrayValue[idx + 1]) {
                    arrayValue[idx] = arrayValue[idx + 1];
                    arrayTextField[idx].setText(arrayTextField[idx + 1].getText());
                    idx++;
                }
                
                if (idx != index) {
                    arrayTextField[idx].setText(newStr);
                    arrayTextField[idx].requestFocus();
                } else if (changeStr != null) {
                    arrayTextField[idx].setText(newStr);
                }
                arrayValue[idx] = value;
                arrayTextField[idx].forward();
            } finally {
                noCheckTextFieldFlag = false;
            }
        }
    };
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="waitForJavaFX">
    private void waitForJavaFX() {
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="hideMenu">
    private void hideMenu() {
        if (arraySizeMenu == null || randomValuesMenu == null || animationMenu == null) {
            return;
        }
        Menu menu = null;

        if (arraySizeMenu.isShowing()) {
            menu = arraySizeMenu;
        } else if (randomValuesMenu.isShowing()) {
            menu = randomValuesMenu;
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
    public void initFXPanel(JFXPanel fxPanel) {

        AnchorPane anchorPane = new AnchorPane();
        VBox vbox = new VBox();
        AnchorPane.setLeftAnchor(vbox, 0d);
        AnchorPane.setRightAnchor(vbox, 0d);
        AnchorPane.setTopAnchor(vbox, 0d);
        AnchorPane.setBottomAnchor(vbox, 0d);
        
        flyingTextField = TextFieldBuilder.create()
                .alignment(Pos.CENTER).prefColumnCount(2)
                .visible(false).editable(false)
                .focusTraversable(false).build();
        
        anchorPane.getChildren().addAll(vbox, flyingTextField);
        

        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init arrays sizes menu">
        arraySizeMenu = new Menu("Rozmiar");
        
        SliderBuilder sliderBuilder = SliderBuilder.create()
                .min(1).max(16).value(16)
                .majorTickUnit(15).minorTickCount(14)
                .snapToTicks(true).showTickMarks(true)
                .prefWidth(200d);
        
        Slider slider = sliderBuilder.build();
        slider.disableProperty().bind(componentsDisabledProperty);
        slider.valueProperty().bindBidirectional(array1Size);
        array1Size.set(16);
        array1Size.addListener(new ArraySizeChangeListener(true));

        Label label = new Label();
        StringExpression strExpression = Bindings.format("Rozmiar pierwszej tablicy: %02d", array1Size);
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraySizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        
        
        slider = sliderBuilder.build();
        slider.disableProperty().bind(componentsDisabledProperty);
        slider.valueProperty().bindBidirectional(array2Size);
        array2Size.set(16);
        array2Size.addListener(new ArraySizeChangeListener(false));

        label = new Label();
        strExpression = Bindings.format("Rozmiar drugiej tablicy: %02d", array2Size);
        label.textProperty().bind(strExpression);

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraySizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(arraySizeMenu);
        //</editor-fold>  

        //<editor-fold defaultstate="collapsed" desc="Init random values menu">
        randomValuesMenu = new Menu("Wypełnij");

        MenuItem randomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami")
                .onAction(new RandomValuesAction())
                .build();
        randomValuesMenu.getItems().add(randomMenuItem);
        randomMenuItem.disableProperty().bind(componentsDisabledProperty);

        slider = SliderBuilder.create()
                .min(0).max(99).value(10)
                .majorTickUnit(9).minorTickCount(8)
                .snapToTicks(true).showTickMarks(true)
                .prefWidth(200d).build();
        slider.valueProperty().bindBidirectional(randMaxValue);
        randMaxValue.set(20);

        label = new Label();
        strExpression = Bindings.format("Przedział losowania: [0,%02d]", randMaxValue);
        label.textProperty().bind(strExpression);

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        randomValuesMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(randomValuesMenu);
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
                .majorTickUnit(1).minorTickCount(0)
                .snapToTicks(true).showTickMarks(true)
                .prefWidth(200d)
                .build();
        slider.valueProperty().bindBidirectional(animationTime);
        slider.disableProperty().bind(synchronizeMenuItem.selectedProperty());
        animationTime.set(10);
        
        label = new Label("Czas animacji:");

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        animationMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(animationMenu);
        //</editor-fold>
        
        TextFieldBuilder textFieldBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER).prefColumnCount(2);

        LabelBuilder labelBuilder = LabelBuilder.create()
                .alignment(Pos.CENTER).maxWidth(Double.MAX_VALUE).id("arrayHeader");
        
        LabelBuilder indexBuilder = LabelBuilder.create().id("arrayIndex");
        
        BooleanBinding editable = componentsDisabledProperty.not();
        
        //<editor-fold defaultstate="collapsed" desc="First array">
        labelBuilder.text("Pierwsza tablica");
        vbox.getChildren().add(labelBuilder.build());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_RIGHT);
        vbox.getChildren().add(grid);
        array1LayoutYProperty = grid.layoutYProperty();
        
        textFieldBuilder.id("first");
        for (int i = 0; i < 16; i++) {
            TextField text = textFieldBuilder.build();
            text.disableProperty().bind(array1Size.lessThanOrEqualTo(i));
            text.editableProperty().bind(editable);
            text.textProperty().addListener(new TextFieldStringChangeListener(i, true));
            grid.add(text, i % 8 + 1, i / 8);
            array1TextField[i] = text;
        }
        for (int i = 0; i < 2; i++) {
            label = indexBuilder.build();
            label.setText((i * 8) + "-" + ((i + 1) * 8 - 1) + ":");
            GridPane.setHalignment(label, HPos.RIGHT);
            grid.add(label, 0, i);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Second array">
        labelBuilder.text("Druga tablica");
        vbox.getChildren().add(labelBuilder.build());
        
        grid = new GridPane();
        grid.setAlignment(Pos.TOP_RIGHT);
        vbox.getChildren().add(grid);
        array2LayoutYProperty = grid.layoutYProperty();
        
        textFieldBuilder.id("second");
        for (int i = 0; i < 16; i++) {
            TextField text = textFieldBuilder.build();
            text.disableProperty().bind(array2Size.lessThanOrEqualTo(i));
            text.editableProperty().bind(editable);
            text.textProperty().addListener(new TextFieldStringChangeListener(i, false));
            grid.add(text, i % 8 + 1, i / 8);
            array2TextField[i] = text;
        }
        for (int i = 0; i < 2; i++) {
            label = indexBuilder.build();
            label.setText((i * 8) + "-" + ((i + 1) * 8 - 1) + ":");
            GridPane.setHalignment(label, HPos.RIGHT);
            grid.add(label, 0, i);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Result array">
        labelBuilder.text("Tablica wynikowa");
        vbox.getChildren().add(labelBuilder.build());
        
        grid = new GridPane();
        vbox.getChildren().add(grid);
        arrayResultLayoutYProperty = grid.layoutYProperty();
        
        NumberBinding sizes = array1Size.add(array2Size);
        textFieldBuilder.editable(false).focusTraversable(false).id("empty");
        for (int i = 0; i < 32; i++) {
            TextField text = textFieldBuilder.build();
            text.disableProperty().bind(sizes.lessThanOrEqualTo(i));
            grid.add(text, i % 8 + 1, i / 8);
            arrayResultTextField[i] = text;
        }
        for (int i = 0; i < 4; i++) {
            label = indexBuilder.build();
            label.setText((i * 8) + "-" + ((i + 1) * 8 - 1) + ":");
            GridPane.setHalignment(label, HPos.RIGHT);
            grid.add(label, 0, i);
        }
        //</editor-fold>

        randomMenuItem.getOnAction().handle(null);
        
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        fxPanel.setScene(scene);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initFrame">
    private void initFrame() {
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
        mainClass.addAddictionalLessonFrame(Lang.frameMenuDescription, frame);

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
    }
    //</editor-fold>
    
    
    //Langugage class:
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Tablica liczb";
        public static final String frameMenuDescription = "Tablica liczb";
    }
    //</editor-fold>
    
}
