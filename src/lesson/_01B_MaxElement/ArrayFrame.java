package lesson._01B_MaxElement;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import lesson._07B_QuickSort.*;
import com.sun.deploy.util.ArrayUtil;
import lesson._06B_MergeSort.*;
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
    private boolean semaphoreJavaFX;

    private SimpleIntegerProperty randomMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty arraySize = new SimpleIntegerProperty();

    private BooleanProperty arraySizeSliderDisableProperty;
    private BooleanProperty randomValuesButtonDisableProperty;
    private BooleanProperty randomIncOrderValuesButtonDisableProperty;
    private BooleanProperty randomDecOrderValuesButtonDisableProperty;

    private TextField arrayTextField[] = new TextField[32];

    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;

    private BigInteger arraySizeBigInt;
    private int arrayValue[] = new int[32];
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

    //<editor-fold defaultstate="collapsed" desc="getArray Value/Size,">
    public int getArrayValue(int index) {
        return arrayValue[index];
    }

    public BigInteger getArraySizeBigInt() {
        return arraySizeBigInt;
    }

    public int getArraySize() {
        return arraySize.get();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getIndexMaxElement">
    public int getIndexMaxElement() {
        int max = 0, size = arraySize.get();
        for (int i = 1; i < size; i++) {
            if (arrayValue[i] > arrayValue[max]) {
                max = i;
            }
        }
        return max;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        for (int i = 0; i < 32; i++) {
            String text = arrayTextField[i].getText();
            arrayValue[i] = Integer.parseInt(text);
        }
        arraySizeBigInt = BigInteger.valueOf(arraySize.get());

        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arraySizeSliderDisableProperty.set(true);
                randomValuesButtonDisableProperty.set(true);
                randomIncOrderValuesButtonDisableProperty.set(true);
                randomDecOrderValuesButtonDisableProperty.set(true);
                
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(false);
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
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(true);
                    arrayTextField[i].setId("empty");
                }
                
                arraySizeSliderDisableProperty.set(false);
                randomValuesButtonDisableProperty.set(false);
                randomIncOrderValuesButtonDisableProperty.set(false);
                randomDecOrderValuesButtonDisableProperty.set(false);
                
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

    //<editor-fold defaultstate="collapsed" desc="compare fuction">
    
    public void compareStart(final int idx1, final int idx2) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arrayTextField[idx1].setId("comparing");
                arrayTextField[idx2].setId("comparing");
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }

    public void compareStop(final int idx1, final int idx2) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arrayTextField[idx1].setId("empty");
                arrayTextField[idx2].setId("empty");
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="paintMax">
    public void paintMax(int idxMax) {
        int size = arraySize.get();
        for (int i = 0; i < size; i++) {
            arrayTextField[i].setId(i == idxMax ? "maxElement" : "empty");
        }
    }
    
    public void paintMax() {
        int idxMax = 0, size = arraySize.get();
        for (int i = 1; i < size; i++) {
            if (arrayValue[i] > arrayValue[idxMax]) {
                idxMax = i;
            }
        }
        for (int i = 0; i < size; i++) {
            arrayTextField[i].setId(i == idxMax ? "maxElement" : "empty");
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
                .prefWidth(200d)
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
                .prefWidth(200d)
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
        
        GridPane grid = new GridPane();
        TextFieldBuilder textFieldBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .contextMenu(null);
        for (int i = 0; i < 32; i++) {
            TextField text = textFieldBuilder.build();
            arrayTextField[i] = text;
            text.textProperty().addListener(new TextFieldStringChangeListener(i));
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(grid);

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
