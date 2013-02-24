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
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    
    private int semaphore;
    private boolean notCheckStringProperty = false;
    
    private SimpleIntegerProperty randMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty array1Size = new SimpleIntegerProperty();
    private SimpleIntegerProperty array2Size = new SimpleIntegerProperty();
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();
    private DoubleProperty array1LayoutYProperty;
    private DoubleProperty array2LayoutYProperty;
    private DoubleProperty arrayResultLayoutYProperty;
    private BooleanProperty array1SizeSliderDisableProperty;
    private BooleanProperty array2SizeSliderDisableProperty;
    private BooleanProperty randomValuesButtonDisableProperty;
    private BooleanProperty animationSynchronizedProperty;
    
    private TextField array1TextField[] = new TextField[16];
    private TextField array2TextField[] = new TextField[16];
    private TextField arrayResultTextField[] = new TextField[32];
    private TextField flyingTextField;
    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;
    private Menu animationMenu;
    
    private BigInteger array1SizeBigInt;
    private BigInteger array2SizeBigInt;
    private BigInteger arrayReslutSizeBigInt;
    private int array1Value[] = new int[16];
    private int array2Value[] = new int[16];
    private boolean array1Removed[] = new boolean[16];
    private boolean array2Removed[] = new boolean[16];
    private String arrayResultText[] = new String[32];
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
        semaphore = 1;
        
        Arrays.fill(array1Removed, false);
        Arrays.fill(array2Removed, false);
        Arrays.fill(arrayResultText, null);
        Arrays.fill(arrayResultValue, -1);
        for (int i = 0; i < 16; i++) {
            array1Value[i] = Integer.parseInt(array1TextField[i].getText());
            array2Value[i] = Integer.parseInt(array2TextField[i].getText());
        }
        
        array1SizeBigInt = BigInteger.valueOf(array1Size.get());
        array2SizeBigInt = BigInteger.valueOf(array2Size.get());
        arrayReslutSizeBigInt = BigInteger.valueOf(array1Size.get() + array2Size.get());
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                array1SizeSliderDisableProperty.set(true);
                array2SizeSliderDisableProperty.set(true);
                randomValuesButtonDisableProperty.set(true);
                
                for (int i = 0; i < 16; i++) {
                    array1TextField[i].setEditable(false);
                    array2TextField[i].setEditable(false);
                    arrayResultTextField[i].setText("");
                    arrayResultTextField[i + 16].setText("");
                    arrayResultTextField[i].setId("empty");
                    arrayResultTextField[i + 16].setId("empty");
                }
                
                semaphore = 0;
            }
        });
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        semaphore = 1;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 16; i++) {
                    array1TextField[i].setEditable(true);
                    array2TextField[i].setEditable(true);
                    array1TextField[i].setId("first");
                    array2TextField[i].setId("second");
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    arrayResultTextField[i + 16].setText(arrayResultText[i + 16]);
                }
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    if (arrayResultText[i] != null) {
                        arrayResultTextField[i].setId("result");
                    }
                }
                array1SizeSliderDisableProperty.set(false);
                array2SizeSliderDisableProperty.set(false);
                randomValuesButtonDisableProperty.set(false);
                
                semaphore = 0;
            }
        });
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateResultValues">
    public void updateResultValues() {
        semaphore = 1;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    if (arrayResultText[i] != null) {
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
                semaphore = 0;
            }
        });
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="compare functions">
    
    public void compareStart(final int idx1, final int idx2) {
        semaphore = 1;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                array1TextField[idx1].setId("comparing");
                array2TextField[idx2].setId("comparing");
                semaphore = 0;
            }
        });
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void compareStop(final int idx1, final int idx2) {
        semaphore = 1;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String id = array1Removed[idx1] ? "empty" : "first";
                array1TextField[idx1].setId(id);

                id = array2Removed[idx2] ? "empty" : "second";
                array2TextField[idx2].setId(id);

                semaphore = 0;
            }
        });
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="moveValue fuctions">
    
    public void moveValue(int idxSrc, boolean firstTab, int idxDest) {
        boolean removedArray[] = firstTab ? array1Removed : array2Removed;
        removedArray[idxSrc] = true;
        
        TextField textArray[] = firstTab ? array1TextField : array2TextField;
        arrayResultText[idxDest] = textArray[idxSrc].getText();
        
        int array[] = firstTab ? array1Value : array2Value;
        arrayResultValue[idxDest] = array[idxSrc];
    }
    
    public void undoMoveValue(int idxSrc, boolean firstTab, int idxDest) {
        boolean removedArray[] = firstTab ? array1Removed : array2Removed;
        removedArray[idxSrc] = false;
        
        arrayResultText[idxDest] = null;
    }
    
    public boolean isValueRemoved(int index, boolean firstTab) {
        return firstTab ? array1Removed[index] : array2Removed[index];
    }
    public boolean isResultValueSet(int index) {
        return arrayResultText[index] != null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="animateMove">
    
    public void animateMove(final int idxSrc, final boolean firstTab, final int idxDest, final int delayTime) {
        semaphore = 1;
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
                        arrayResultText[idxDest] = text;
                        arrayResultTextField[idxDest].setText(text);
                        arrayResultTextField[idxDest].setId("result");
                        semaphore = 0;
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
        while (semaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="RandomValuesAction">
    private class RandomValuesAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent t) {
            try {
                notCheckStringProperty = true;

                Random rand = new Random();
                int maxValue = randMaxValue.get() + 1;
                int size, array[];

                size = array1Size.get();
                array = new int[size];
                for (int i = 0; i < size; i++) {
                    array[i] = rand.nextInt(maxValue);
                }
                Arrays.sort(array);
                for (int i = 0; i < size; i++) {
                    String strValue = Integer.toString(array[i]);
                    array1TextField[i].setText(strValue);
                }

                size = array2Size.get();
                array = new int[size];
                for (int i = 0; i < size; i++) {
                    array[i] = rand.nextInt(maxValue);
                }
                Arrays.sort(array);
                for (int i = 0; i < size; i++) {
                    String strValue = Integer.toString(array[i]);
                    array2TextField[i].setText(strValue);
                }
            } finally {
                notCheckStringProperty = false;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TextFieldDisableChangeListener">
    private class TextFieldDisableChangeListener implements ChangeListener<Boolean> {
        
        private final TextField arrayTextField[];
        private final int index;

        public TextFieldDisableChangeListener(int index, boolean first) {
            this.index = index;
            arrayTextField = first ? array1TextField : array2TextField;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
            if (t1.booleanValue()) {
                return;
            }
            try {
                notCheckStringProperty = true;

                TextField prev = arrayTextField[index];
                String strText = prev.getText();
                int intText = Integer.parseInt(strText);
                boolean change = false;
                int idx = index;

                while (idx > 0) {
                    TextField next = arrayTextField[idx - 1];
                    String str = next.getText();
                    int int2 = Integer.parseInt(str);
                    if (int2 <= intText) {
                        break;
                    }
                    prev.setText(str);
                    if (next.isFocused()) {
                        prev.requestFocus();
                        prev.forward();
                    }
                    change = true;
                    prev = next;
                    idx--;
                }
                if (change) {
                    arrayTextField[idx].setText(strText);
                }
            } finally {
                notCheckStringProperty = false;
            }
        }
    };
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TextFieldStringChangeListener">
    private class TextFieldStringChangeListener implements ChangeListener<String> {
        
        private final TextField arrayTextField[];
        private final SimpleIntegerProperty arraySize;
        private final int index;

        public TextFieldStringChangeListener(int index, boolean first) {
            this.index = index;
            arrayTextField = first ? array1TextField : array2TextField;
            arraySize = first ? array1Size : array2Size;
        }

        @Override
        public void changed(ObservableValue<? extends String> ov, String t, String t1) {
            if (notCheckStringProperty) {
                return;
            }
            TextField text = arrayTextField[index];
            boolean oldValue = false;
            if (t1.length() > 2) {
                oldValue = true;
            } else {
                for (char c : t1.toCharArray()) {
                    if (c < '0' || '9' < c) {
                        oldValue = true;
                        break;
                    }
                }
            }
            if (oldValue) {
                try {
                    notCheckStringProperty = true;
                    text.setText(t);
                } finally {
                    notCheckStringProperty = false;
                }
                return;
            }

            boolean change = false;
            if (t1.length() == 2) {
                if (t1.charAt(0) == '0') {
                    t1 = t1.substring(1);
                    change = true;
                }
            } else if (t1.isEmpty()) {
                t1 = "0";
                change = true;
            }

            int idx = index;
            int intText = Integer.parseInt(t1);

            try {
                notCheckStringProperty = true;
                
                while (idx > 0) {
                    String str = arrayTextField[idx - 1].getText();
                    int int2 = Integer.parseInt(str);
                    if (int2 <= intText) {
                        break;
                    }
                    arrayTextField[idx].setText(str);
                    change = true;
                    idx--;
                }
                
                int size = arraySize.get() - 1;
                while (idx < size) {
                    String str = arrayTextField[idx + 1].getText();
                    int int2 = Integer.parseInt(str);
                    if (intText <= int2) {
                        break;
                    }
                    arrayTextField[idx].setText(str);
                    change = true;
                    idx++;
                }
                if (change) {
                    arrayTextField[idx].setText(t1);
                    arrayTextField[idx].requestFocus();
                    arrayTextField[idx].forward();
                }
            } finally {
                notCheckStringProperty = false;
            }
        }
    };
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="hideMenu">
    private void hideMenu() {
        if (arraysSizeMenu == null || randomValuesMenu == null) {
            return;
        }
        Runnable run = null;
        if (arraysSizeMenu.isShowing()) {
            run = new Runnable() {
                @Override
                public void run() {
                    arraysSizeMenu.hide();
                }
            };
        } else if (randomValuesMenu.isShowing()) {
            run = new Runnable() {
                @Override
                public void run() {
                    randomValuesMenu.hide();
                }
            };
        } else if (animationMenu.isShowing()) {
            run = new Runnable() {
                @Override
                public void run() {
                    animationMenu.hide();
                }
            };
        }
        if (run != null) {
            Platform.runLater(run);
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
        
        TextFieldBuilder textFieldBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2);
        
        flyingTextField = textFieldBuilder.build();
        flyingTextField.setVisible(false);
        flyingTextField.setEditable(false);
        flyingTextField.setFocusTraversable(false);
        
        anchorPane.getChildren().addAll(vbox, flyingTextField);
        

        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init arrays sizes menu">
        arraysSizeMenu = new Menu("Rozmiar");
        
        SliderBuilder sliderBuilder = SliderBuilder.create()
                .min(1).max(16).value(16)
                .majorTickUnit(15)
                .minorTickCount(14)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(200d);

        Slider slider = sliderBuilder.build();
        slider.valueProperty().bindBidirectional(array1Size);
        array1Size.set(16);
        array1SizeSliderDisableProperty = slider.disableProperty();

        Label label = new Label();
        StringExpression strExpression = Bindings.format("Rozmiar pierwszej tablicy: %02d", array1Size);
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraysSizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        
        
        slider = sliderBuilder.build();
        slider.valueProperty().bindBidirectional(array2Size);
        array2Size.set(16);
        array2SizeSliderDisableProperty = slider.disableProperty();

        label = new Label();
        strExpression = Bindings.format("Rozmiar drugiej tablicy: %02d", array2Size);
        label.textProperty().bind(strExpression);

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraysSizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(arraysSizeMenu);
        //</editor-fold>  

        //<editor-fold defaultstate="collapsed" desc="Init random values menu">
        randomValuesMenu = new Menu("Wypełnij");

        MenuItem randomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami")
                .onAction(new RandomValuesAction())
                .build();
        randomValuesMenu.getItems().add(randomMenuItem);
        randomValuesButtonDisableProperty = randomMenuItem.disableProperty();

        slider = SliderBuilder.create()
                .min(0).max(99).value(10)
                .majorTickUnit(9)
                .minorTickCount(8)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(200d)
                .build();
        slider.valueProperty().bindBidirectional(randMaxValue);
        randMaxValue.set(10);

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

        
        label = LabelBuilder.create()
                .text("Pierwsza tablica")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        textFieldBuilder.id("first");
        GridPane grid = new GridPane();
        array1LayoutYProperty = grid.layoutYProperty();
        for (int i = 0; i < 16; i++) {
            TextField text = textFieldBuilder.build();
            array1TextField[i] = text;
            
            text.disableProperty().bind(array1Size.lessThanOrEqualTo(i));
            text.disableProperty().addListener(new TextFieldDisableChangeListener(i, true));
            text.textProperty().addListener(new TextFieldStringChangeListener(i, true));
            
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);

        label = LabelBuilder.create()
                .text("Druga tablica")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        grid = new GridPane();
        array2LayoutYProperty = grid.layoutYProperty();
        textFieldBuilder.id("second");
        for (int i = 0; i < 16; i++) {
            TextField text = textFieldBuilder.build();
            array2TextField[i] = text;
            
            text.disableProperty().bind(array2Size.lessThanOrEqualTo(i));
            text.disableProperty().addListener(new TextFieldDisableChangeListener(i, false));
            text.textProperty().addListener(new TextFieldStringChangeListener(i, false));
            
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);

        label = new Label("Tablica wynikowa");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        grid = new GridPane();
        arrayResultLayoutYProperty = grid.layoutYProperty();
        NumberBinding sizes = array1Size.add(array2Size);
        textFieldBuilder.editable(false).focusTraversable(false).id("empty");
        for (int i = 0; i < 32; i++) {
            TextField text = textFieldBuilder.build();
            arrayResultTextField[i] = text;
            
            text.disableProperty().bind( sizes.lessThanOrEqualTo(i));
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);

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
