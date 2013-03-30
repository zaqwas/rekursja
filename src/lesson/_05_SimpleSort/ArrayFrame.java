package lesson._05_SimpleSort;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.InterpreterThread;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ParallelTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    
    private SimpleBooleanProperty componentsDisabledProperty = new SimpleBooleanProperty(false);
    
    private Menu arraySizeMenu;
    private Menu randomValuesMenu;
    private Menu animationMenu;
    
    private BooleanProperty animationSynchronizedProperty;
    private SimpleIntegerProperty animationTime = new SimpleIntegerProperty();
    private TextField flyingTextField1, flyingTextField2;
    private DoubleProperty gridLayoutYProperty;
    
    private SimpleIntegerProperty randomMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty arraySize = new SimpleIntegerProperty();
    
    private TextField inputArayValueTextField[] = new TextField[32];
    private TextField arrayValueTextField[] = new TextField[32];

    private BigInteger arraySizeBigInt;
    private int arrayValueInt[] = new int[32];
    private String arrayValueString[] = new String[32];
    
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
    public int isSorted() {
        int size = arraySize.get();
        for (int i = 1; i < size; i++) {
            if (arrayValueInt[i - 1] > arrayValueInt[i]) {
                return i - 1;
            }
        }
        return -1;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="swapValue">
    public void swapValue(int idx1, int idx2) {
        String text = arrayValueString[idx2];
        arrayValueString[idx2] = arrayValueString[idx1];
        arrayValueString[idx1] = text;
        
        int value = arrayValueInt[idx2];
        arrayValueInt[idx2] = arrayValueInt[idx1];
        arrayValueInt[idx1] = value;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getArray Value/Size">
    public int getArrayValue(int index) {
        return arrayValueInt[index];
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
            String text = inputArayValueTextField[i].getText();
            arrayValueString[i] = text;
            arrayValueInt[i] = Integer.parseInt(text);
        }
        arraySizeBigInt = BigInteger.valueOf(arraySize.get());

        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                componentsDisabledProperty.set(true);
                
                for (int i = 0; i < 32; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
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
                    arrayValueTextField[i].setText(arrayValueString[i]);
                    arrayValueTextField[i].setId("empty");
                }
                componentsDisabledProperty.set(false);
                
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
    
    
    //<editor-fold defaultstate="collapsed" desc="updateAllSorted">
    public void updateAllSorted() {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
                    arrayValueTextField[i].setId("sorted");
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SelectionSort">
    public void updateSelectionSort(final int i, final int j, final int min) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int k = 0; k < size; k++) {
                    arrayValueTextField[k].setText(arrayValueString[k]);
                    String id = k < i ? "sorted" : k == min ? "selected"
                            : k == j ? "marked" : "empty";
                    arrayValueTextField[k].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void updateCompareSelectionSort(final int idx1, final int idx2, final int i) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int k = 0; k < size; k++) {
                    arrayValueTextField[k].setText(arrayValueString[k]);
                    String id = k < i ? "sorted" : k == idx1 || k == idx2 ? "comparing" : "empty";
                    arrayValueTextField[k].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void animateSwapSelectionSort(final int idx1, final int idx2, final int delayTime) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
                    String id = i <= idx1 ? (i == idx1 ? "selectedSwapping" : "sorted")
                            : (i == idx2 ? "emptySwapping" : "empty");
                    arrayValueTextField[i].setId(id);
                }

                animateSwap(idx1, idx2, "emptySwapping", "selectedSwapping", delayTime);
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="BubbleSort">
    public void updateBubbleSort(final int i, final int k, final int range) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q > range ? "sorted" : q == k ? "selected"
                            : i < range && (q == i || q == i + 1) ? "marked" : "empty";
                    arrayValueTextField[q].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    

    public void updateCompareBubbleSort(final int idx, final int k, final int range) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q > range ? "sorted" : q == k ? "selected"
                            : q == idx || q == idx + 1 ? "comparing" : "empty";
                    arrayValueTextField[q].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void animateSwapBubbleSort(final int idx, final int k, final int range, final int delayTime) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q > range ? "sorted" : q == k ? "selected"
                            : q == idx || q == idx + 1 ? "markedSwapping" : "empty";
                    arrayValueTextField[q].setId(id);
                }

                animateSwap(idx, idx + 1, "markedSwapping", "markedSwapping", delayTime);
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="InsertionSort">
    public void updateInsertionSort(final int i, final int j) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q == j ? "selected" : q < i ? "sorted" : "empty";
                    arrayValueTextField[q].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void updateCompareInsertionSort(final int idx, final int i) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q == idx || q == idx + 1 ? "comparing" : q < i ? "sorted" : "empty";
                    arrayValueTextField[q].setId(id);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void animateSwapInsertionSort(final int idx, final int i, final int delayTime) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int q = 0; q < size; q++) {
                    arrayValueTextField[q].setText(arrayValueString[q]);
                    String id = q == idx ? "selectedSwapping" : q == idx + 1 ? "sortedSwapping"
                            : q < i ? "sorted" : "empty";
                    arrayValueTextField[q].setId(id);
                }

                animateSwap(idx, idx + 1, "sortedSwapping", "selectedSwapping", delayTime);
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UserCode">
    
    public void updateUserCode() {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
                }
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void updateCompareUserCode(final int idx1, final int idx2) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
                }
                arrayValueTextField[idx1].setId("comparing");
                arrayValueTextField[idx2].setId("comparing");
                
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void updateAfterPause(final int idx1, final int idx2) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arrayValueTextField[idx1].setId("empty");
                arrayValueTextField[idx2].setId("empty");
                
                duringJavafxThread = false;
            }
        });
        waitForJavaFX();
    }
    
    public void animateSwapUserCode(final int idx1, final int idx2, final int delayTime) {
        duringJavafxThread = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayValueTextField[i].setText(arrayValueString[i]);
                    if (i == idx1 || i == idx2) {
                        arrayValueTextField[i].setId("emptySwapping");
                    }
                }
                
                animateSwap(idx1, idx2, "emptySwapping", "emptySwapping", delayTime);
            }
        });
        waitForJavaFX();
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="waitForJavaFX">
    private void waitForJavaFX() {
        while (duringJavafxThread) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="animateSwap">
    private void animateSwap(final int idx1, final int idx2, String id1, String id2, final int delayTime) {

        final int delayMillis = animationSynchronizedProperty.get()
                ? delayTime : InterpreterThread.getDelayTime(animationTime.get());

        if (idx1 == idx2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ex) {
                    }
                    duringJavafxThread = false;
                }
            }).start();
            return;
        }

        double x1, y1, x2, y2;

        final TextField field1 = arrayValueTextField[idx1];
        x1 = field1.getLayoutX();
        y1 = field1.getLayoutY();
        y1 += gridLayoutYProperty.get();

        final TextField field2 = arrayValueTextField[idx2];
        x2 = field2.getLayoutX();
        y2 = field2.getLayoutY();
        y2 += gridLayoutYProperty.get();

        flyingTextField1.setText(arrayValueString[idx2]);
        flyingTextField1.setId(id1);
        flyingTextField1.setTranslateX(x1);
        flyingTextField1.setTranslateY(y1);
        flyingTextField1.setVisible(true);
        field1.setVisible(false);

        flyingTextField2.setText(arrayValueString[idx1]);
        flyingTextField2.setId(id2);
        flyingTextField2.setTranslateX(x2);
        flyingTextField2.setTranslateY(y2);
        flyingTextField2.setVisible(true);
        field2.setVisible(false);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                field1.setVisible(true);
                field2.setVisible(true);
                flyingTextField1.setVisible(false);
                flyingTextField2.setVisible(false);
                duringJavafxThread = false;
            }
        };

        Duration delay = Duration.millis(delayMillis);

        TranslateTransition transition1 = TranslateTransitionBuilder.create()
                .node(flyingTextField1).duration(delay)
                .fromX(x1).toX(x2).fromY(y1).toY(y2).build();

        TranslateTransition transition2 = TranslateTransitionBuilder.create()
                .node(flyingTextField2).duration(delay)
                .fromX(x2).toX(x1).fromY(y2).toY(y1).build();

        ParallelTransitionBuilder.create().children(transition1, transition2)
                .onFinished(event).build().play();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TextFieldStringChangeListener">
    private class TextFieldStringChangeListener implements ChangeListener<String> {

        private final int index;

        public TextFieldStringChangeListener(int index) {
            this.index = index;
        }

        @Override
        public void changed(ObservableValue<? extends String> ov, String t, String t1) {
            if (noCheckStringProperty) {
                return;
            }
            try {
                noCheckStringProperty = true;
                TextField text = inputArayValueTextField[index];
                
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
                    text.setText(t);
                    text.forward();
                    return;
                }

                String str = null;
                if (t1.length() == 2) {
                    if (t1.charAt(0) == '0') {
                        str = t1.substring(1);
                    }
                } else if (t1.isEmpty()) {
                    str = "0";
                }
                if (str != null) {
                    text.setText(str);
                }
                text.forward();
            } finally {
                noCheckStringProperty = false;
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
                    inputArayValueTextField[i].setText(strValue);
                }
            } finally {
                noCheckStringProperty = false;
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
                .prefColumnCount(2)
                .visible(false)
                .editable(false)
                .focusTraversable(false)
                .contextMenu(null);
        
        flyingTextField1 = textFieldBuilder.build();
        flyingTextField2 = textFieldBuilder.build();
        anchorPane.getChildren().addAll(flyingTextField1, flyingTextField2);
        
        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init arrays sizes menu">
        arraySizeMenu = new Menu("Rozmiar");

        Slider slider = SliderBuilder.create()
                .min(1).max(32).value(32)
                .majorTickUnit(31)
                .minorTickCount(30)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(200d)
                .build();
        slider.valueProperty().bindBidirectional(arraySize);
        arraySize.set(32);
        slider.disableProperty().bind(componentsDisabledProperty);

        Label label = new Label();
        StringExpression strExpression = Bindings.format("Rozmiar tablicy: %02d", arraySize);
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraySizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(arraySizeMenu);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init random values menu">
        randomValuesMenu = new Menu("Wypełnij");

        MenuItem randomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami")
                .onAction(new RandomValuesAction(false, false))
                .build();
        randomMenuItem.disableProperty().bind(componentsDisabledProperty);
        randomValuesMenu.getItems().add(randomMenuItem);

        MenuItem orderRandomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami\nuporządkowanymi niemalejąco")
                .onAction(new RandomValuesAction(true, true))
                .build();
        orderRandomMenuItem.disableProperty().bind(componentsDisabledProperty);
        randomValuesMenu.getItems().add(orderRandomMenuItem);
        
        orderRandomMenuItem = MenuItemBuilder.create()
                .text("Wypełnij losowymi wartościami\nuporządkowanymi nierosnąco")
                .onAction(new RandomValuesAction(true, false))
                .build();
        orderRandomMenuItem.disableProperty().bind(componentsDisabledProperty);
        randomValuesMenu.getItems().add(orderRandomMenuItem);

        slider = SliderBuilder.create()
                .min(0).max(99).value(10)
                .majorTickUnit(9)
                .minorTickCount(8)
                .snapToTicks(true)
                .showTickMarks(true)
                .prefWidth(200d)
                .build();
        slider.valueProperty().bindBidirectional(randomMaxValue);
        randomMaxValue.set(20);

        label = new Label();
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

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        animationMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(animationMenu);
        //</editor-fold>

        
        LabelBuilder headerBuilder = LabelBuilder.create()
                .id("arrayHeader").alignment(Pos.CENTER).maxWidth(Double.MAX_VALUE);
        
        TextFieldBuilder textBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER).prefColumnCount(2).contextMenu(null);
        
        LabelBuilder indexBuilder = LabelBuilder.create().id("arrayIndex");
        
        //<editor-fold defaultstate="collapsed" desc="input array">
        headerBuilder.text("Tablica przed uruchomieniem programu");
        vbox.getChildren().add(headerBuilder.build());
        
        GridPane grid = new GridPane();
        vbox.getChildren().addAll(grid);
        
        BooleanBinding editable = componentsDisabledProperty.not();
        for (int i = 0; i < 32; i++) {
            TextField text = textBuilder.build();
            text.editableProperty().bind(editable);
            text.textProperty().addListener(new TextFieldStringChangeListener(i));
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            grid.add(text, i % 8 + 1, i / 8);
            inputArayValueTextField[i] = text;
        }
        for (int i = 0; i < 4; i++) {
            label = indexBuilder.build();
            label.setText((i * 8) + "-" + ((i + 1) * 8 - 1) + ":");
            GridPane.setHalignment(label, HPos.RIGHT);
            grid.add(label, 0, i);
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="output array">
        
        headerBuilder.text("Tablica w trakcie działania programu");
        vbox.getChildren().add(headerBuilder.build());
        
        grid = new GridPane();
        vbox.getChildren().add(grid);
        
        gridLayoutYProperty = grid.layoutYProperty();
        textBuilder.editable(false).focusTraversable(false);
        for (int i = 0; i < 32; i++) {
            TextField text = textBuilder.build();
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            grid.add(text, i % 8 + 1, i / 8);
            arrayValueTextField[i] = text;
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
