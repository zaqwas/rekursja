package lesson._06B_MergeSort;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.InterpreterThread;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import javafx.animation.ParallelTransition;
import javafx.animation.ParallelTransitionBuilder;
import javafx.animation.PauseTransitionBuilder;
import javafx.animation.SequentialTransition;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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

    private boolean noCheckStringProperty;
    private boolean duringJavafxThread;
    private int animationSemaphore;

    private SimpleIntegerProperty randomMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty arraySize = new SimpleIntegerProperty();
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();

    private DoubleProperty arrayLayoutYProperty;
    private DoubleProperty arrayResultLayoutYProperty;

    private BooleanProperty arraySizeSliderDisableProperty;
    private BooleanProperty randomValuesButtonDisableProperty;
    private BooleanProperty randomIncOrderValuesButtonDisableProperty;
    private BooleanProperty randomDecOrderValuesButtonDisableProperty;
    private BooleanProperty animationSynchronizedProperty;

    private TextField arrayTextField[] = new TextField[32];
    private TextField arrayResultTextField[] = new TextField[32];
    private TextField flyingTextField[] = new TextField[32];

    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;
    private Menu animationMenu;

    private BigInteger arraySizeBigInt;
    private int arrayResultValue[] = new int[32];
    private String arrayResultText[] = new String[32];
    private int arrayTempValue[] = new int[32];
    private String arrayTempText[] = new String[32];
    private int arrayTempIndex[] = new int[32];
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

    //<editor-fold defaultstate="collapsed" desc="isSorted">
    public int isSorted(int idx1, int idx2) {
        for (int i = idx1; i < idx2 - 1; i++) {
            if (arrayResultValue[i] > arrayResultValue[i + 1]) {
                return i;
            }
        }
        return -1;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getArray Value/Size,">
    public int getArrayResultValue(int index) {
        return arrayResultValue[index];
    }

    public BigInteger getArraySizeBigInt() {
        return arraySizeBigInt;
    }

    public int getArraySize() {
        return arraySize.get();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        for (int i = 0; i < 32; i++) {
            String text = arrayTextField[i].getText();
            arrayResultText[i] = text;
            arrayResultValue[i] = Integer.parseInt(text);
        }
        arraySizeBigInt = BigInteger.valueOf(arraySize.get());

        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arraySizeSliderDisableProperty.set(true);
                randomValuesButtonDisableProperty.set(true);
                randomIncOrderValuesButtonDisableProperty.set(true);
                randomDecOrderValuesButtonDisableProperty.set(true);
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(false);
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    arrayResultTextField[i].setId("empty");
                }
                duringJavafxThread = false;
            }
        });
        while (duringJavafxThread) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(true);
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    arrayResultTextField[i].setId("empty");
                }
                arraySizeSliderDisableProperty.set(false);
                randomValuesButtonDisableProperty.set(false);
                randomIncOrderValuesButtonDisableProperty.set(false);
                randomDecOrderValuesButtonDisableProperty.set(false);
                
                duringJavafxThread = false;
            }
        });
        while (duringJavafxThread) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateArrays">
    public void updateArray() {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    arrayResultTextField[i].setId("empty");
                }
                duringJavafxThread = false;
            }
        });
        while (duringJavafxThread) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    
    public void updateArray(final int idx1, final int idx2, final int mode) {
        assert 0 <= mode && mode <= 2;
        final int idxSr = (idx1 + idx2) >> 1;
        
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultText[i]);
                    if ( idx1 <= i && i<idx2 ) {
                        String id = mode == 0 ? (i < idxSr ? "first" : "second") :
                                mode == 1 ? "result" : "marked";
                        arrayResultTextField[i].setId(id);
                    } else {
                        arrayResultTextField[i].setId("empty");
                    }
                }
                duringJavafxThread = false;
            }
        });
        while (duringJavafxThread) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="merge functions">
    public void rememberValues(int idx1, int idx2, int[] arrayVal, String[] arrayStr) {
        for (int i = idx1; i < idx2; i++) {
            arrayVal[i] = arrayResultValue[i];
            arrayStr[i] = arrayResultText[i];
        }
    }
    public void restoreValues(int idx1, int idx2, int[] arrayVal, String[] arrayStr) {
        for (int i = idx1; i < idx2; i++) {
            arrayResultValue[i] = arrayVal[i];
            arrayResultText[i] = arrayStr[i];
        }
    }

    public void merge(int idx1, int idx2, int idx3) {
        int i1 = idx1, i2 = idx2, i3 = 0;
        
        while (i1 < idx2 && i2 < idx3) {
            if (arrayResultValue[i1] <= arrayResultValue[i2]) {
                arrayTempValue[i3] = arrayResultValue[i1];
                arrayTempText[i3++] = arrayResultText[i1++];
            } else {
                arrayTempValue[i3] = arrayResultValue[i2];
                arrayTempText[i3++] = arrayResultText[i2++];
            }
        }
        while (i1 < idx2) {
            arrayTempValue[i3] = arrayResultValue[i1];
            arrayTempText[i3++] = arrayResultText[i1++];
        }
        while (i2 < idx3) {
            arrayTempValue[i3] = arrayResultValue[i2];
            arrayTempText[i3++] = arrayResultText[i2++];
        }

        i1 = idx3;
        while (i3 > 0) {
            arrayResultValue[--i1] = arrayTempValue[--i3];
            arrayResultText[i1] = arrayTempText[i3];
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="animations">
    
    public void animateMerge(final int idx1, final int idx2, final int idx3, 
            final int delayTime) {
        
        
        int i1 = idx1, i2 = idx2, i3 = 0;
        while (i1 < idx2 && i2 < idx3) {
            if (arrayResultValue[i1] <= arrayResultValue[i2]) {
                arrayTempIndex[i3] = i1;
                arrayTempValue[i3] = arrayResultValue[i1];
                arrayTempText[i3++] = arrayResultText[i1++];
            } else {
                arrayTempIndex[i3] = i2;
                arrayTempValue[i3] = arrayResultValue[i2];
                arrayTempText[i3++] = arrayResultText[i2++];
            }
        }
        while (i1 < idx2) {
            arrayTempIndex[i3] = i1;
            arrayTempValue[i3] = arrayResultValue[i1];
            arrayTempText[i3++] = arrayResultText[i1++];
        }
        while (i2 < idx3) {
            arrayTempIndex[i3] = i2;
            arrayTempValue[i3] = arrayResultValue[i2];
            arrayTempText[i3++] = arrayResultText[i2++];
        }
        
        animationSemaphore = 2;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                final int idxEnd = idx3 - idx1;
                
                final int delay = animationSynchronizedProperty.get() ? delayTime
                        : InterpreterThread.getDelayTime(animationTime.get());
                Duration moveDuration = Duration.millis(2 * delay / 5);
                Duration duration = Duration.millis(delay / 5);
                
                TranslateTransition firstTransition[] = new TranslateTransition[idxEnd];
                TranslateTransition secondTransition[] = new TranslateTransition[idxEnd];
                
                TranslateTransitionBuilder translateBuilder = 
                        TranslateTransitionBuilder.create();
                
                for (int i = 0; i < idxEnd; i++) {
                    flyingTextField[i].setText(arrayTempText[i]);
                    flyingTextField[i].setId(arrayTempIndex[i] < idx2 ? "first" : "second");
                    
                    double x1, y1, x2, y2;
                    
                    TextField field1 = arrayResultTextField[arrayTempIndex[i]];
                    x1 = field1.getLayoutX();
                    y1 = field1.getLayoutY();
                    y1 += arrayResultLayoutYProperty.get();
                    
                    flyingTextField[i].setTranslateX(x1);
                    flyingTextField[i].setTranslateY(y1);
                    
                    TextField field2 = arrayResultTextField[i + idx1];
                    x2 = field2.getLayoutX();
                    y2 = field2.getLayoutY();
                    y2 += arrayResultLayoutYProperty.get();
                    
                    double prefHight = arrayResultTextField[0].getHeight() / 2d;
                    
                    firstTransition[i] = translateBuilder
                            .node(flyingTextField[i])
                            .duration(moveDuration)
                            .fromX(x1)
                            .fromY(y1)
                            .toX(x2)
                            .toY(y2 - prefHight)
                            .build();
                    secondTransition[i] = translateBuilder
                            .node(flyingTextField[i])
                            .duration(duration)
                            .fromX(x2)
                            .fromY(y2 - prefHight)
                            .toX(x2)
                            .toY(y2)
                            .build();
                }
                
                EventHandler eventChangeIdToResult = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        for (int i = 0; i < idxEnd; i++) {
                            flyingTextField[i].setId("result");
                        }
                    }
                };
                
                EventHandler eventOnFinished = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        for (int i = 0, j = idx1; i < idxEnd; i++, j++) {
                            arrayResultTextField[j].setVisible(true);
                            flyingTextField[i].setVisible(false);
                        }
                        animationSemaphore = 0;
                    }
                };
                
                ParallelTransitionBuilder parallelBuilder = ParallelTransitionBuilder.create();
                PauseTransitionBuilder pauseBuilder = PauseTransitionBuilder.create()
                        .duration(duration);
                        
                
                SequentialTransition transition = SequentialTransitionBuilder.create()
                        .children(
                            pauseBuilder.build(),
                            parallelBuilder.children(firstTransition).build(),
                            pauseBuilder.onFinished(eventChangeIdToResult).build(),
                            parallelBuilder.children(secondTransition).build()
                        )
                        .onFinished(eventOnFinished)
                        .build();
                
                for (int i = 0, j = idx1; i < idxEnd; i++, j++) {
                    flyingTextField[i].setVisible(true);
                    TextField field = arrayResultTextField[j];
                    field.setVisible(false);
                    field.setText(arrayTempText[i]);
                    field.setId("result");
                    arrayResultValue[j] = arrayTempValue[i];
                    arrayResultText[j] = arrayTempText[i];
                }
                
                transition.play();
            }
        });
        while (animationSemaphore > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }
    //</editor-fold>


    //private functions:

    //<editor-fold defaultstate="collapsed" desc="TextFieldStringChangeListener">
    private class TextFieldStringChangeListener implements ChangeListener<String> {

        private final int index;

        public TextFieldStringChangeListener(int index) {
            this.index = index;;
        }

        @Override
        public void changed(ObservableValue<? extends String> ov, String t, String t1) {
            if (noCheckStringProperty) {
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
                    noCheckStringProperty = true;
                    text.setText(t);
                } finally {
                    noCheckStringProperty = false;
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
            if (change) {
                try {
                    noCheckStringProperty = true;
                    text.setText(t1);
                } finally {
                    noCheckStringProperty = false;
                }
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RandomValuesAction">
    private class RandomValuesAction implements EventHandler<ActionEvent> {

        private final boolean sorting;
        private final boolean increasing;

        public RandomValuesAction(boolean sorting, boolean increasing) {
            this.sorting = sorting;
            this.increasing = increasing;
        }

        @Override
        public void handle(ActionEvent t) {
            Random rand = new Random();
            int maxValue = randomMaxValue.get() + 1, size;
            Integer array[];

            size = arraySize.get();
            array = new Integer[size];
            for (int i = 0; i < size; i++) {
                array[i] = rand.nextInt(maxValue);
            }
            if (sorting) {
                if (increasing) {
                    Arrays.sort(array);
                } else {
                    Comparator<Integer> comp = new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o2 - o1;
                        }
                    };
                    Arrays.sort(array, comp);
                }
            }

            try {
                noCheckStringProperty = true;
                for (int i = 0; i < size; i++) {
                    String strValue = array[i].toString();
                    arrayTextField[i].setText(strValue);
                }
            } finally {
                noCheckStringProperty = false;
            }
        }
    }
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
    private void initFXPanel(JFXPanel fxPanel) {

        AnchorPane anchorPane = new AnchorPane();
        VBox vbox = new VBox();
        AnchorPane.setLeftAnchor(vbox, 0d);
        AnchorPane.setRightAnchor(vbox, 0d);
        AnchorPane.setTopAnchor(vbox, 0d);
        AnchorPane.setBottomAnchor(vbox, 0d);
        anchorPane.getChildren().addAll(vbox);
        
        TextFieldBuilder textFieldBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .visible(false)
                .editable(false)
                .focusTraversable(false)
                .contextMenu(null);
        
        for (int i = 0; i < 32; i++) {
            flyingTextField[i] = textFieldBuilder.build();
        }

        anchorPane.getChildren().addAll(flyingTextField);


        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init arrays sizes menu">
        arraysSizeMenu = new Menu("Rozmiar");

        Slider slider = SliderBuilder.create()
                .min(1).max(32).value(32)
                .majorTickUnit(31)
                .minorTickCount(30)
                .snapToTicks(true)
                .showTickMarks(true)
                .build();
        slider.valueProperty().bindBidirectional(arraySize);
        arraySize.set(32);
        arraySizeSliderDisableProperty = slider.disableProperty();

        Label label = new Label();
        StringExpression strExpression = Bindings.format("Rozmiar tablicy: %02d", arraySize);
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraysSizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(arraysSizeMenu);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init random values menu">
        randomValuesMenu = new Menu("Wypełnij");

        MenuItem randomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami")
                .onAction(new RandomValuesAction(false, false))
                .build();
        randomValuesMenu.getItems().add(randomMenuItem);
        randomValuesButtonDisableProperty = randomMenuItem.disableProperty();

        MenuItem orderRandomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami\nposortowanymi niemalejąco")
                .onAction(new RandomValuesAction(true, true))
                .build();
        randomValuesMenu.getItems().add(orderRandomMenuItem);
        randomIncOrderValuesButtonDisableProperty = orderRandomMenuItem.disableProperty();

        orderRandomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami\nposortowanymi nierosnąco")
                .onAction(new RandomValuesAction(true, false))
                .build();
        randomValuesMenu.getItems().add(orderRandomMenuItem);
        randomDecOrderValuesButtonDisableProperty = orderRandomMenuItem.disableProperty();

        slider = SliderBuilder.create()
                .min(0).max(99).value(10)
                .majorTickUnit(9)
                .minorTickCount(8)
                .snapToTicks(true)
                .showTickMarks(true)
                .build();
        slider.valueProperty().bindBidirectional(randomMaxValue);
        randomMaxValue.set(10);

        label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        strExpression = Bindings.format("Przedział losowania: [0,%02d]", randomMaxValue);
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
                .style("-fx-font-size: 12;")
                .text("Przed uruchomieniem programu")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        GridPane grid = new GridPane();
        arrayLayoutYProperty = grid.layoutYProperty();
        for (int i = 0; i < 32; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .contextMenu(null)
                    .build();
            arrayTextField[i] = text;
            text.textProperty().addListener(new TextFieldStringChangeListener(i));
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);

        label = LabelBuilder.create()
                .style("-fx-font-size: 12;")
                .text("W trakcie działania programu")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        grid = new GridPane();
        arrayResultLayoutYProperty = grid.layoutYProperty();
        for (int i = 0; i < 32; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .editable(false)
                    .focusTraversable(false)
                    .contextMenu(null)
                    .build();
            arrayResultTextField[i] = text;
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
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
        public static final String frameTitle = "Tablica liczb";
    }
    //</editor-fold>

}
