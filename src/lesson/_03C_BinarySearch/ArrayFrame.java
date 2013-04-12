package lesson._03C_BinarySearch;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import mainclass.MainClass;
//</editor-fold>

public class ArrayFrame {
    
    public static final int NOTHING = 0;
    public static final int MARK = 1;
    public static final int COMPARING = 2;

    //<editor-fold defaultstate="collapsed" desc="Components and variables">
    private MainClass mainClass;
    private JInternalFrame frame;

    private boolean noCheckTextFieldFlag;
    private boolean semaphoreJavaFX;
    
    private SimpleBooleanProperty componentsDisabledProperty = new SimpleBooleanProperty(false);
    
    private int selectedPart = 1;

    private SimpleIntegerProperty randomMaxValue = new SimpleIntegerProperty();
    private SimpleIntegerProperty arraySize = new SimpleIntegerProperty();

    private TextField searchingTextField;
    private TextField arrayTextField[] = new TextField[32];

    private Menu arraysSizeMenu;
    private Menu randomValuesMenu;

    private BigInteger arraySizeBigInt;
    private int arrayValue[] = new int[32];
    private int searchingValue;
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
    
    public int getSearchingValue() {
        return searchingValue;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startThread">
    public void threadStart() {
        semaphoreJavaFX = true;
        arraySizeBigInt = BigInteger.valueOf(arraySize.get());
        searchingValue = Integer.parseInt(searchingTextField.getText());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                componentsDisabledProperty.set(true);
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
                    arrayTextField[i].setId("empty");
                }
                searchingTextField.setId("empty");
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

    //<editor-fold defaultstate="collapsed" desc="compare fuction">
    
    public void compareStart(final int idx) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arrayTextField[idx].setId("comparing");
                searchingTextField.setId("comparing");
                semaphoreJavaFX = false;
            }
        });
        while (semaphoreJavaFX) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }

    public void compareStop(final int idx) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                arrayTextField[idx].setId("empty");
                searchingTextField.setId("empty");
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
    
    //<editor-fold defaultstate="collapsed" desc="setSelectedPart">
    public void setSelectedPart(int selectedPart) {
        this.selectedPart = selectedPart;
        if (selectedPart == 1) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                Arrays.sort(arrayValue, 0, size);
                
                try {
                    noCheckTextFieldFlag = true;
                    for (int i = 0; i < size; i++) {
                        String str = String.valueOf(arrayValue[i]);
                        arrayTextField[i].setText(str);
                    }
                } finally {
                    noCheckTextFieldFlag = false;
                }
            }
        });
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="paintRejectedArrayBeginning">
    public void paintRejectedArrayBeginning(final int idx, final boolean comparing) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    String id = "empty";
                    if (i == idx) {
                        id = comparing ? "comparing" : "rejected";
                    } else if (i < idx) {
                        id = "rejected";
                    }
                    arrayTextField[i].setId(id);
                }
                searchingTextField.setId(comparing ? "comparing" : "empty");
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
    
    //<editor-fold defaultstate="collapsed" desc="paintFound">
    public void paintFound(final int idxFound) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    String id = i == idxFound ? "found" : "empty";
                    arrayTextField[i].setId(id);
                }
                searchingTextField.setId("empty");
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
    
    public void paintFoundAndMark(final int idx1, final int idx2, final int idxFound) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    String id = "empty";
                    if (idx1 <= i && i < idx2) {
                        if (i == idxFound) {
                            id = "foundMarked";
                        } else {
                            id = "emptyMarked";
                        }
                    }
                    arrayTextField[i].setId(id);
                }
                searchingTextField.setId("empty");
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
    
    //<editor-fold defaultstate="collapsed" desc="paintAllRejected">
    public void paintAllRejected() {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    arrayTextField[i].setId("rejected");
                }
                searchingTextField.setId("empty");
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
    
    public void paintAllRejectedAndMark(final int idx1, final int idx2) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                for (int i = 0; i < size; i++) {
                    String id = "rejected";
                    if (idx1 <= i && i < idx2) {
                        id = "rejectedMarked";
                    }
                    arrayTextField[i].setId(id);
                }
                searchingTextField.setId("empty");
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
    
    //<editor-fold defaultstate="collapsed" desc="paintRange">
    public void paintRange(final int idx1, final int idx2, final int middle) {
        semaphoreJavaFX = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = arraySize.get();
                int idxSr = (idx1 + idx2) >> 1;
                for (int i = 0; i < size; i++) {
                    String id = "rejected";
                    if (idx1 <= i && i < idx2) {
                        if (i == idxSr) {
                            if (middle == NOTHING) {
                                id = "potential";
                            } else if (middle == MARK) {
                                id = "middle";
                            } else {
                                id = "comparing";
                            }
                        } else {
                            id = "potential";
                        }
                    }
                    arrayTextField[i].setId(id);
                }
                searchingTextField.setId(middle == COMPARING ? "comparing" : "empty");
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
    
    //private functions:
    
    //<editor-fold defaultstate="collapsed" desc="swapTextFields">
    private void swapTextFields(int idx1, int idx2) {
        int value = arrayValue[idx2];
        arrayValue[idx2] = arrayValue[idx2 - 1];
        arrayValue[idx2 - 1] = value;
        
        TextField t1 = arrayTextField[idx1];
        TextField t2 = arrayTextField[idx2];

        String str = t1.getText();
        t1.setText(t2.getText());
        t2.setText(str);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ArraySizeChangeListener">
    private class ArraySizeChangeListener implements ChangeListener<Number> {
        
        @Override
        public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
            int idxFrom = (int)oldValue;
            int idxTo = (int)newValue;
            if (selectedPart == 1 || idxFrom >= idxTo) {
                return;
            }
            
            try {
                noCheckTextFieldFlag = true;
                for (int i = idxFrom; i < idxTo; i++) {
                    int j = i;
                    while (j > 0 && arrayValue[j - 1] > arrayValue[j]) {
                        swapTextFields(j-1, j);
                        j--;
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

        private final int index;

        public TextFieldStringChangeListener(int index) {
            this.index = index;
        }

        @Override
        public void changed(ObservableValue<? extends String> ov, String oldStr, String newStr) {
            if (noCheckTextFieldFlag) {
                return;
            }
            try {
                noCheckTextFieldFlag = true;
                TextField text = index == -1 ? searchingTextField : arrayTextField[index];
                
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
                    text.setText(changeStr);
                    newStr = changeStr;
                }
                if (index != -1) {
                    arrayValue[index] = Integer.parseInt(newStr);
                }
                
                if (selectedPart == 1 || index == -1) {
                    text.forward();
                    return;
                }
                
                int idx = index;
                while (idx > 0 && arrayValue[idx - 1] > arrayValue[idx]) {
                    swapTextFields(idx - 1, idx);
                    idx--;

                }
                if (idx == index) {
                    int size = arraySize.get() - 1;
                    while (idx < size && arrayValue[idx] > arrayValue[idx + 1]) {
                        swapTextFields(idx, idx + 1);
                        idx++;
                    }
                }
                if (idx != index) {
                    text = arrayTextField[idx];
                    text.requestFocus();
                }
                text.forward();
            } finally {
                noCheckTextFieldFlag = false;
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RandomValuesAction">
    private class RandomValuesAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            Random rand = new Random();
            int maxValue = randomMaxValue.get() + 1;
            int size = arraySize.get();
            
            for (int i = 0; i < size; i++) {
                arrayValue[i] = rand.nextInt(maxValue);
            }
            if (selectedPart == 2) {
                Arrays.sort(arrayValue, 0, size);
            }
            try {
                noCheckTextFieldFlag = true;
                String str = String.valueOf(rand.nextInt(maxValue));
                searchingTextField.setText(str);
                for (int i = 0; i < size; i++) {
                    str = String.valueOf(arrayValue[i]);
                    arrayTextField[i].setText(str);
                }
            } finally {
                noCheckTextFieldFlag = false;
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
        arraySize.addListener(new ArraySizeChangeListener());
        slider.disableProperty().bind(componentsDisabledProperty);

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
                .onAction(new RandomValuesAction())
                .build();
        randomValuesMenu.getItems().add(randomMenuItem);
        randomMenuItem.disableProperty().bind(componentsDisabledProperty);
        
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
        
        TextFieldBuilder textFieldBuilder = TextFieldBuilder.create()
                .alignment(Pos.CENTER)
                .style("-fx-font: 12 Monospaced;")
                .prefColumnCount(2)
                .contextMenu(null);
        
        BooleanBinding editable = componentsDisabledProperty.not();
        
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(hbox);
        
        label = new Label("Wyszukiwany element:");
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        HBox.setMargin(label, new Insets(0d, 2d, 0d, 0d));
        searchingTextField = textFieldBuilder.build();
        searchingTextField.textProperty().addListener(new TextFieldStringChangeListener(-1));
        searchingTextField.editableProperty().bind(editable);
        hbox.getChildren().addAll(label, searchingTextField);
                
        GridPane grid = new GridPane();
        for (int i = 0; i < 32; i++) {
            TextField text = textFieldBuilder.build();
            arrayTextField[i] = text;
            text.textProperty().addListener(new TextFieldStringChangeListener(i));
            text.disableProperty().bind(arraySize.lessThanOrEqualTo(i));
            text.editableProperty().bind(editable);
            grid.add(text, i % 8 + 1, i / 8);
        }
        for (int i = 0; i < 4; i++) {
            label = new Label();
            label.setStyle("-fx-font-size: 8px;");
            label.setText((i * 8) + "-" + ((i + 1) * 8 - 1) + ":");
            GridPane.setHalignment(label, HPos.RIGHT);
            grid.add(label, 0, i);
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
