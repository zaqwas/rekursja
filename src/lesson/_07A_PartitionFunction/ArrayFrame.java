package lesson._07A_PartitionFunction;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class ArrayFrame {

    private MainClass mainClass;
    private JInternalFrame frame;
    
    private int selectedPart = 1;
    private boolean duringJavafx;
    private int animationSemaphore;
    private boolean noCheckStringProperty;
    private SimpleIntegerProperty randomMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty arraySize = new SimpleIntegerProperty();
    private DoubleProperty arrayLayoutYProperty;
    private DoubleProperty arrayResultLayoutYProperty;
    private BooleanProperty arraySizeSliderDisableProperty;
    private BooleanProperty randomValuesButtonDisableProperty;
    private BooleanProperty randomIncOrderValuesButtonDisableProperty;
    private BooleanProperty randomDecOrderValuesButtonDisableProperty;
    private TextField arrayTextField[] = new TextField[32];
    private TextField arrayResultTextField[] = new TextField[32];
    private TextField flyingTextField1;
    private TextField flyingTextField2;
    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;
    private StringProperty arrayLabelTextProperty;
    private StringProperty arrayResultLabelTextProperty;
    
    private BigInteger arraySizeBigInt;
    private int pivot;
    private int arrayValue[] = new int[32];
    private int arrayResultValue[] = new int[32];
    private String arrayResultText[] = new String[32];
    private boolean removedValue[] = new boolean[32];
    private boolean comparedValue[] = new boolean[32];

    
    //<editor-fold defaultstate="collapsed" desc="showFrame">
    public void showFrame() {
        frame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setSelectedPart">
    public void setSelectedPart(int nr) {
        selectedPart = nr;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getArrayValue, getArraySizeBigInt">
    public int getArrayValue(int index) {
        return selectedPart == 3 ? arrayResultValue[index] : arrayValue[index];
    }
    
    public BigInteger getArraySizeBigInt() {
        return arraySizeBigInt;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        for (int i = 0; i < 32; i++) {
            String text = arrayTextField[i].getText();
            arrayResultValue[i] = selectedPart == 3 ? arrayValue[i] : -1;
            arrayResultText[i] = selectedPart == 3 ? text : null;
        }
        Arrays.fill(removedValue, false);
        Arrays.fill(comparedValue, false);
        if (selectedPart > 1) {
            comparedValue[0] = true;
        }
        arraySizeBigInt = BigInteger.valueOf(arraySize.get());
        
        duringJavafx = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arraySizeSliderDisableProperty.set(true);
                randomIncOrderValuesButtonDisableProperty.set(true);
                randomDecOrderValuesButtonDisableProperty.set(true);
                randomValuesButtonDisableProperty.set(true);
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(false);
                    arrayResultTextField[i].setText("");
                    arrayResultTextField[i].setId("empty");
                }                
                if (selectedPart == 3) {
                    arrayResultTextField[0].setId("equal");
                }
                duringJavafx = false;
            }
        });
        while (duringJavafx) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        Arrays.fill(removedValue, false);
        if (selectedPart == 2) {
            Arrays.fill(comparedValue, false);
        }
        duringJavafx = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayTextField[i].setEditable(true);
                }
                
                arraySizeSliderDisableProperty.set(false);
                randomValuesButtonDisableProperty.set(false);
                randomIncOrderValuesButtonDisableProperty.set(false);
                randomDecOrderValuesButtonDisableProperty.set(false);
                
                updateAllTextFields(selectedPart != 3, true);
                duringJavafx = false;
            }
        });
        while (duringJavafx) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateArrays">
    public void updateArrays() {
        duringJavafx = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateAllTextFields(selectedPart != 3, true);
                duringJavafx = false;
            }
        });
        while (duringJavafx) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="move functions">
    public void moveValue(int idxSrc, int idxDest) {
        arrayResultText[idxDest] = arrayTextField[idxSrc].getText();
        arrayResultValue[idxDest] = arrayValue[idxSrc];
        removedValue[idxSrc] = true;
    }

    public void undoMoveValue(int idxSrc, int idxDest) {
        arrayResultText[idxDest] = null;
        arrayResultValue[idxDest] = -1;
        removedValue[idxSrc] = false;
    }
    
    public boolean isValueRemoved(int idxSrc) {
        return removedValue[idxSrc];
    }
    
    public boolean isResultValueSet(int idxDest) {
        return arrayResultText[idxDest] != null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="compare functions">
    public void setValueCompared(int idx, boolean compared) {
        comparedValue[idx] = compared;
    }
    
    public boolean isValueCompared(int idx) {
        return comparedValue[idx];
    }
    
    public void compareStart(final int idx1, final int idx2) {
        assert selectedPart < 3 && idx1 == 0;
        
        duringJavafx = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if ( selectedPart == 3 ) {
                    arrayResultTextField[idx1].setId("comparing");
                    arrayResultTextField[idx2].setId("comparing");
                } else {
                    String id = selectedPart == 2 ? "comparingCompared" : "comparing";
                    arrayTextField[idx1].setId(id);
                    arrayTextField[idx2].setId("comparing");
                }
                duringJavafx = false;
            }
        });
        while (duringJavafx) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    public void compareStop(final int idx1, final int idx2) {
        if (selectedPart > 1) {
            comparedValue[idx1] = true;
            comparedValue[idx2] = true;
        }
        duringJavafx = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if ( selectedPart == 3 ) {
                    updateResultTextField(idx1);
                    updateResultTextField(idx2);
                } else {
                    updateTextField(idx1);
                    updateTextField(idx2);
                }
                duringJavafx = false;
            }
        });
        while (duringJavafx) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="swap functions">
    public void swapValue(int idxSrc, int idxDest) {
        String text = arrayResultText[idxDest];
        arrayResultText[idxDest] = arrayResultText[idxSrc];
        arrayResultText[idxSrc] = text;
        int value = arrayResultValue[idxDest];
        arrayResultValue[idxDest] = arrayResultValue[idxSrc];
        arrayResultValue[idxSrc] = value;
        if ( selectedPart == 3) {
            boolean compared = comparedValue[idxDest];
            comparedValue[idxDest] = comparedValue[idxSrc];
            comparedValue[idxSrc] = compared;    
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="animations">
    public boolean isAnimating() {
        return animationSemaphore > 0;
    }
    
    public void animateMove(final int idxSrc, final int idxDest) {
        animationSemaphore = 1;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double xSrc, ySrc, xDest, yDest;

                TextField testSrc = arrayTextField[idxSrc];
                xSrc = testSrc.getLayoutX();
                ySrc = testSrc.getLayoutY();
                ySrc += arrayLayoutYProperty.get();

                TextField testDest = arrayResultTextField[idxDest];
                xDest = testDest.getLayoutX();
                yDest = testDest.getLayoutY();
                yDest += arrayResultLayoutYProperty.get();
                
                String idSrc = selectedPart == 2 && comparedValue[idxSrc] ? 
                        "emptyCompared" : "empty";
                arrayTextField[idxSrc].setId(idSrc);
                removedValue[idxSrc] = true;
                final String text = arrayTextField[idxSrc].getText();
                final int value = arrayValue[idxSrc];
                final String id = value == pivot ? "equal" : 
                        value < pivot ? "less" : "greater";
                
                flyingTextField1.setText(text);
                flyingTextField1.setId(id);
                flyingTextField1.setVisible(true);

                EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        flyingTextField1.setVisible(false);
                        arrayResultText[idxDest] = text;
                        arrayResultValue[idxDest] = value;
                        arrayResultTextField[idxDest].setText(text);
                        arrayResultTextField[idxDest].setId(id);
                        animationSemaphore = 0;
                    }
                };

                TranslateTransition transition = TranslateTransitionBuilder.create()
                        .duration(Duration.millis(2000))
                        .node(flyingTextField1)
                        .fromX(xSrc)
                        .toX(xDest)
                        .fromY(ySrc)
                        .toY(yDest)
                        .onFinished(event)
                        .build();
                transition.play();
            }
        });
    }
    
    public void animateSwap(final int idx1, final int idx2) {
        animationSemaphore = 2;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double x1, y1, x2, y2;
                
                final TextField field1 = arrayResultTextField[idx1];
                x1 = field1.getLayoutX();
                y1 = field1.getLayoutY();
                y1 += arrayResultLayoutYProperty.get();

                final TextField field2 = arrayResultTextField[idx2];
                x2 = field2.getLayoutX();
                y2 = field2.getLayoutY();
                y2 += arrayResultLayoutYProperty.get();
                
                final String text1 = arrayResultText[idx1];
                final String text2 = arrayResultText[idx2];
                arrayResultText[idx2] = text1;
                arrayResultText[idx1] = text2;
                int value = arrayResultValue[idx2];
                arrayResultValue[idx2] = arrayResultValue[idx1];
                arrayResultValue[idx1] = value;
                if ( selectedPart == 3 ) {
                    boolean compared = comparedValue[idx2];
                    comparedValue[idx2] = comparedValue[idx1];
                    comparedValue[idx1] = compared;
                }
                final String id1 = field1.getId();
                final String id2 = field2.getId();
                
                
                flyingTextField1.setText(text1);
                flyingTextField1.setId(id1);
                flyingTextField1.setTranslateX(x1);
                flyingTextField1.setTranslateY(y1);
                flyingTextField1.setVisible(true);
                field1.setVisible(false);
                field1.setText(text2);
                field1.setId(id2);
                
                flyingTextField2.setText(text2);
                flyingTextField2.setId(id2);
                flyingTextField2.setTranslateX(x2);
                flyingTextField2.setTranslateY(y2);
                flyingTextField2.setVisible(true);
                field2.setVisible(false);
                field2.setText(text1);
                field2.setId(id1);
                
                EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        field2.setVisible(true);
                        flyingTextField1.setVisible(false);
                        animationSemaphore--;
                    }
                };
                
                EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        field1.setVisible(true);
                        flyingTextField2.setVisible(false);
                        animationSemaphore--;
                    }
                };
                
                TranslateTransition transition1 = TranslateTransitionBuilder.create()
                        .duration(Duration.millis(2000))
                        .node(flyingTextField1)
                        .fromX(x1)
                        .toX(x2)
                        .fromY(y1)
                        .toY(y2)
                        .onFinished(event1)
                        .build();
                
                TranslateTransition transition2 = TranslateTransitionBuilder.create()
                        .duration(Duration.millis(2000))
                        .node(flyingTextField2)
                        .fromX(x2)
                        .toX(x1)
                        .fromY(y2)
                        .toY(y1)
                        .onFinished(event2)
                        .build();
                
                transition1.play();
                transition2.play();
            }
        });
    }
    //</editor-fold>
    
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="update textFields">
    private void updateAllTextFields(boolean updateTopArray, boolean updateBelowArray) {
        if (updateTopArray) {
            for (int i = 0; i < 32; i++) {
                updateTextField(i);
            }
        }
        if (updateBelowArray) {
            for (int i = 0; i < 32; i++) {
                updateResultTextField(i);
            }
        }
    }
    
    private void updateTextField(int idx) {
        int value = arrayValue[idx];
        String id;
        if (selectedPart == 2 && comparedValue[idx]) {
            id = removedValue[idx] ? "emptyCompared" : 
                    value == pivot ? "equalCompared" : 
                    value < pivot ? "lessCompared" : "greaterCompared";
        } else {
            id = removedValue[idx] ? "empty" : 
                    value == pivot ? "equal" : 
                    value < pivot ? "less" : "greater";
        }
        arrayTextField[idx].setId(id);
    }
    
    private void updateResultTextField(int idx) {
        arrayResultTextField[idx].setText(arrayResultText[idx]);
        
        int value = arrayResultValue[idx];
        String id;
        
        if (value == -1 || (selectedPart == 3 && !comparedValue[idx]) ) {
            id = "empty";
        } else {
            id = value == pivot ? "equal" : 
                    value < pivot ? "less" : "greater";
        }
        arrayResultTextField[idx].setId(id);
    }
    //</editor-fold>
    
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
            
            int value = Integer.parseInt(t1);
            arrayValue[index] = value;
            
            if (index==0) {
                pivot = value;
                updateAllTextFields(true, false);
            } else {
                updateTextField(index);
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
                    arrayValue[i] = array[i];
                }
                pivot = arrayValue[0];
            } finally {
                noCheckStringProperty = false;
            }
            updateAllTextFields(true, false);
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
    public void initFXPanel(JFXPanel fxPanel) {

        AnchorPane anchorPane = new AnchorPane();
        VBox vbox = new VBox();
        AnchorPane.setLeftAnchor(vbox, 0d);
        AnchorPane.setRightAnchor(vbox, 0d);
        AnchorPane.setTopAnchor(vbox, 0d);
        AnchorPane.setBottomAnchor(vbox, 0d);
        
        flyingTextField1 = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .visible(false)
                .editable(false)
                .build();
        flyingTextField2 = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .visible(false)
                .editable(false)
                .build();
        
        anchorPane.getChildren().addAll(vbox, flyingTextField1, flyingTextField2);
        

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

        label = LabelBuilder.create()
                .text("Tablica pierwotna")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        arrayLabelTextProperty = label.textProperty();
        GridPane grid = new GridPane();
        arrayLayoutYProperty = grid.layoutYProperty();
        for (int i = 0; i < 32; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .build();            
            arrayTextField[i] = text;
            text.textProperty().addListener(new TextFieldStringChangeListener(i));
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);
        
        label = LabelBuilder.create()
                .text("Tablica wynikowa")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        arrayResultLabelTextProperty = label.textProperty();
        grid = new GridPane();
        arrayResultLayoutYProperty = grid.layoutYProperty();
        for (int i = 0; i < 32; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .editable(false)
                    .focusTraversable(false)
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
    
    public ArrayFrame(MainClass mainClass) {
        this.mainClass = mainClass;
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
        ArrayFrame.this.mainClass.getDesktop().add(frame);
        
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
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Tablica liczb";
    }
    //</editor-fold>
    
}
