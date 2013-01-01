package lesson._07A_PartitionFunction;

import lesson._06A_MergeFunction.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.TranslateTransition;
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
    
    private boolean notCheckStringProperty = false;
    private SimpleIntegerProperty randMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty array1Size = new SimpleIntegerProperty();
    private SimpleIntegerProperty array2Size = new SimpleIntegerProperty();
    private DoubleProperty array2LayoutYProperty;
    private DoubleProperty arrayResultLayoutYProperty;
    private BooleanProperty array1SizeSliderDisableProperty;
    private BooleanProperty array2SizeSliderDisableProperty;
    private BooleanProperty randomValuesButtonDisableProperty;
    private TextField array1TextField[] = new TextField[16];
    private TextField array2TextField[] = new TextField[16];
    private TextField arrayResultTextField[] = new TextField[32];
    private TextField flyingTextField;
    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;
    
    private BigInteger array1SizeBigInt;
    private BigInteger array2SizeBigInt;
    private int array1Values[] = new int[16];
    private int array2Values[] = new int[16];
    private String arrayResultTextValues[] = new String[32];

    
    //<editor-fold defaultstate="collapsed" desc="showFrame">
    public void showFrame() {
        frame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        for (int i = 0; i < 16; i++) {
            arrayResultTextValues[i] = null;
            arrayResultTextValues[i+16] = null;
            array1Values[i] = Integer.parseInt(array1TextField[i].getText());
            array2Values[i] = Integer.parseInt(array2TextField[i].getText());
        }
        array1SizeBigInt = BigInteger.valueOf(array1Size.get());
        array2SizeBigInt = BigInteger.valueOf(array2Size.get());
        
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
                }
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="stopThread">
    public void threadStop() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 16; i++) {
                    array1TextField[i].setEditable(true);
                    array2TextField[i].setEditable(true);
                }
                array1SizeSliderDisableProperty.set(false);
                array2SizeSliderDisableProperty.set(false);
                randomValuesButtonDisableProperty.set(false);
            }
        });
        updateResultValues();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getArraySize">
    public int getArraySize(int nr) {
        assert nr == 1 || nr == 2;
        return nr == 1 ? array1Size.get() : array2Size.get();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getArraySizeBigInt">
    public BigInteger getArraySizeBigInt(int nr) {
        assert nr == 1 || nr == 2;
        return nr == 1 ? array1SizeBigInt : array2SizeBigInt;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getArrayValue">
    public int getArrayValue(int index, int nr) {
        assert nr == 1 || nr == 2;
        return nr == 1 ? array1Values[index] : array2Values[index];
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="moveValueToResultArray">
    public void moveValueToResultArray(int idxSrc, int nr, int idxDest) {
        assert nr == 1 || nr == 2;
        TextField[] array = nr == 1 ? array1TextField : array2TextField;
        arrayResultTextValues[idxDest] = array[idxSrc].getText();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateResultValues">
    public void updateResultValues() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 32; i++) {
                    arrayResultTextField[i].setText(arrayResultTextValues[i]);
                }
            }
        });
    }
    //</editor-fold>
    
    
    

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
    
    //<editor-fold defaultstate="collapsed" desc="animateMove">
    private void animateMove(int idxSrc, boolean first, int idxDest) {
        double xSrc = 0, ySrc = 0, xDest = 0, yDest = 0;
        
        TextField arrayTextField[] = first ? array1TextField : array2TextField;
        TextField testSrc = arrayTextField[idxSrc];
        xSrc = testSrc.getLayoutX();
        ySrc = testSrc.getLayoutY();
        if (!first) {
            ySrc += array2LayoutYProperty.get();
        }
        
        TextField testDest = arrayResultTextField[idxDest];
        xDest = testDest.getLayoutX();
        yDest = testDest.getLayoutY();
        yDest += arrayResultLayoutYProperty.get();
        
        flyingTextField.setText("WW");
        flyingTextField.setVisible(true);
        
        TranslateTransition transition = TranslateTransitionBuilder.create()
                .duration(Duration.millis(500))
                .node(flyingTextField)
                .fromX(xSrc)
                .toX(xDest)
                .fromY(ySrc)
                .toY(yDest)
                .build();
        transition.play();
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
        
        flyingTextField = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .visible(false)
                .editable(false)
                .build();
        
        anchorPane.getChildren().addAll(vbox, flyingTextField);
        

        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);

        //<editor-fold defaultstate="collapsed" desc="Init arrays sizes menu">
        arraysSizeMenu = new Menu("Rozmiar");

        Slider slider = SliderBuilder.create()
                .min(1).max(16).value(16)
                .majorTickUnit(15)
                .minorTickCount(14)
                .snapToTicks(true)
                .showTickMarks(true)
                .build();
        slider.valueProperty().bindBidirectional(array1Size);
        array1Size.set(16);
        array1SizeSliderDisableProperty = slider.disableProperty();

        Label label = new Label();
        StringExpression strExpression = Bindings.format("Rozmiar pierwszej tablicy: %02d", array1Size);
        label.textProperty().bind(strExpression);

        VBox vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        arraysSizeMenu.getItems().add(new CustomMenuItem(vboxMenu, false));
        
        
        slider = SliderBuilder.create()
                .min(1).max(16).value(16)
                .majorTickUnit(15)
                .minorTickCount(14)
                .snapToTicks(true)
                .showTickMarks(true)
                .build();
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
                .build();
        slider.valueProperty().bindBidirectional(randMaxValue);
        randMaxValue.set(10);

        label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        strExpression = Bindings.format("Przedział losowania: [0,%02d]", randMaxValue);
        label.textProperty().bind(strExpression);

        vboxMenu = new VBox();
        vboxMenu.getChildren().addAll(label, slider);
        randomValuesMenu.getItems().add(new CustomMenuItem(vboxMenu, false));

        menuBar.getMenus().add(randomValuesMenu);
        //</editor-fold>

//        Button button = new Button("START");
//        button.setMaxWidth(Double.MAX_VALUE);
//        //button.setDisable(true);
//        button.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent t) {
//                animateMove(2,false,4);
//            }
//        });
//        VBox.setMargin(button, new Insets(3, 6, 0, 6));
//        vbox.getChildren().add(button);

        label = LabelBuilder.create()
                .text("Pierwsza tablica")
                .alignment(Pos.CENTER)
                .maxWidth(Double.MAX_VALUE)
                .build();
        GridPane grid = new GridPane();
        for (int i = 0; i < 16; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .build();
            
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
        for (int i = 0; i < 16; i++) {
            final TextField text = TextFieldBuilder.create()
                    .alignment(Pos.CENTER)
                    .style("-fx-font: 12 Monospaced;")
                    .prefColumnCount(2)
                    .build();
            
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
        for (int i = 0; i < 32; i++) {
            TextField text = new TextField();
            text.setEditable(false);
            text.setStyle("-fx-font: 12 Monospaced; ");
            text.setPrefColumnCount(2);
            text.disableProperty().bind( sizes.lessThanOrEqualTo(i));
            arrayResultTextField[i] = text;
            grid.add(text, i % 8, i / 8);
        }
        vbox.getChildren().addAll(label, grid);

        randomMenuItem.getOnAction().handle(null);
        Scene scene = new Scene(anchorPane);
        
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
