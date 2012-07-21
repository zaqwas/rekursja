package instanceframe;

//<editor-fold defaultstate="collapsed" desc="Import Classes">
import interpreter.Instance;
import interpreter.accessvar.AccessArray;
import interpreter.accessvar.AccessInteger;
import interpreter.accessvar.VariableScope;
import interpreter.accessvar.VariableType;
import interpreter.arguments.ArgInteger;
import interpreter.arguments.ArgReference;
import interpreter.arguments.Argument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import mainclass.MainClass;
import stringcreator.Extender;
import stringcreator.IntegerExtender;
import stringcreator.StringExtender;
import syntax.SyntaxTree;
import syntax.function.Function;
//</editor-fold>

public class InstanceFrame {
    
    //<editor-fold defaultstate="collapsed" desc="Components">
    private JInternalFrame frame;
    
    private JLabel functionNameTextLabel;
    private JLabel functionNameLabel;
    private JLabel returnedValueTextLabel;
    private JLabel returnedValueLabel;
    private JLabel onStackLabel;
    
    private JSplitPane splitPane;
    
    private JScrollPane varsScrollPane;
    private JPanel variablessPanel;
    private JPanel argumentsPanel;
    private JPanel localVarsPanel;
    private JPanel globalVarsPanel;
    
    private JPanel arrayPanel;
    private JPanel arrayValuesPanel;
    private JScrollBar arrayScrollBar;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private Font labelBoldFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
    private FontMetrics labelFontMetrics;
    private FontMetrics labelBoldFontMetrics;
    private Object textAntiAliasingHint;
    
    private Color argumentsColor = new Color(95, 0, 95);
    private Color localVarsColor = new Color(0, 95, 95);
    private Color globalVarsColor = new Color(0, 0, 0);
    
    private SyntaxTree syntaxTree;
    private Instance instance;
    private boolean onStack;
    private boolean observeCurrent = true;
    
    private AccessArray selectedArrayAccess;
    private String selectedArrayName;
    
    private int varsPanelsWidth;
    private boolean arrayIsUpdating;
    
    private int ySpaceBetweenPanels = 10;
    private int splitPanePrefWidth = 250;
    private int yGap = 0, xGap = 2;
    private int arrayIndexLabelWidth = 50;
    
    
    private int arguemtnsHeaderPosition;
    private ArrayList<String> argumentsString =  new ArrayList<String>();
    private ArrayList<Integer> argumentsPosition =  new ArrayList<Integer>();
    
    private int localVarsHeaderPosition;
    private ArrayList<String> localVarsString =  new ArrayList<String>();
    private ArrayList<Integer> localVarsPosition =  new ArrayList<Integer>();
    
    private int globalVarsHeaderPosition;
    private ArrayList<String> globalVarsString = new ArrayList<String>();
    private ArrayList<Integer> globalVarsPosition = new ArrayList<Integer>();
    
    private String arrayHeaderString;
    private int arrayHeaderPosition;
    private ArrayList<String> arrayValuesString =  new ArrayList<String>();
    private ArrayList<Integer> arrayValuesPosition =  new ArrayList<Integer>();
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public InstanceFrame(MainClass mainClass) {
        frame = new JInternalFrame(Lang.frameTitle);
        frame.setResizable(true);
        
        //<editor-fold defaultstate="collapsed" desc="Init top labels">
        functionNameTextLabel = new JLabel(Lang.functionName);
        functionNameTextLabel.setFont(labelFont);
        functionNameLabel = new JLabel("");
        functionNameLabel.setFont(labelBoldFont);
        functionNameLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFunctionName();
            }
        });
        
        returnedValueTextLabel = new JLabel(Lang.returnedValue);
        returnedValueTextLabel.setFont(labelFont);
        returnedValueTextLabel.setVisible(false);
        returnedValueLabel = new JLabel("");
        returnedValueLabel.setFont(labelBoldFont);
        returnedValueLabel.setVisible(false);
        returnedValueLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateReturnedValue();
            }
        });
        
        onStackLabel = new JLabel(Lang.onStack);
        onStackLabel.setFont(labelBoldFont);
        onStackLabel.setVisible(false);
        
        labelFontMetrics = functionNameTextLabel.getFontMetrics(labelFont);
        labelBoldFontMetrics = functionNameLabel.getFontMetrics(labelBoldFont);
        textAntiAliasingHint = labelFontMetrics.getFontRenderContext().getAntiAliasingHint();
        //</editor-fold>
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.75);
        splitPane.setBorder(null);
        
        //<editor-fold defaultstate="collapsed" desc="Init Variables Panels">
        varsScrollPane = new JScrollPane();
        varsScrollPane.setBorder(null);
        varsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        varsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        splitPane.setTopComponent(varsScrollPane);
        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int which = e.getComponent() == argumentsPanel ? 0
                        : (e.getComponent() == localVarsPanel ? 1 : 2);
                clickedOnVarsPanel(which, e.getY());
            }
        };
        argumentsPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintArgumentsPanel(g);
            }
        };
        argumentsPanel.setLayout(null);
        argumentsPanel.addMouseListener(mouseAdapter);
        localVarsPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintLocalVarsPanel(g);
            }
        };
        localVarsPanel.setLayout(null);
        localVarsPanel.addMouseListener(mouseAdapter);
        globalVarsPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintGlobalVarsPanel(g);
            }
        };
        globalVarsPanel.addMouseListener(mouseAdapter);
        
        variablessPanel = new JPanel();
        variablessPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                variablesPanelResized(e.getComponent().getWidth()-1);
            }
        });
        GroupLayout layout = new GroupLayout(variablessPanel);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGap(1) 
            .addGroup(
                layout.createParallelGroup()
                .addComponent(argumentsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(localVarsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(globalVarsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(1)
        );
        layout.setVerticalGroup (
            layout.createSequentialGroup()
            .addComponent(argumentsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(localVarsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(globalVarsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        variablessPanel.setLayout(layout);
        varsScrollPane.setViewportView(variablessPanel);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init Array Panels">
        arrayValuesPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintArrayValuesPanel(g);
            }
        };
        arrayValuesPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateArrayPanel();
            }
        });
        arrayScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
        arrayScrollBar.addAdjustmentListener( new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateArrayPanel();
            }
        });
        arrayPanel = new JPanel();
        layout = new GroupLayout(arrayPanel);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(1)
                .addComponent(arrayValuesPanel)
                .addGap(1)
                .addComponent(arrayScrollBar)
        );
        layout.setVerticalGroup (
            layout.createSequentialGroup()
            .addGap(2)
            .addGroup(layout.createParallelGroup()
                .addComponent(arrayValuesPanel, 0, 0, Short.MAX_VALUE)
                .addComponent(arrayScrollBar))
            .addGap(2)
        );
        arrayPanel.setLayout(layout);
        splitPane.setBottomComponent(arrayPanel);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Layout">
        JSeparator separator = new JSeparator();
        layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addGap(2)
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(functionNameTextLabel)
                        .addComponent(functionNameLabel, 100, 100, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(returnedValueTextLabel)
                        .addComponent(returnedValueLabel))
                    .addComponent(onStackLabel))
                .addGap(2))
            .addComponent(separator, 0, 0, Short.MAX_VALUE)
            .addComponent(splitPane, splitPanePrefWidth, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(functionNameTextLabel)
                .addComponent(functionNameLabel))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(returnedValueTextLabel)
                .addComponent(returnedValueLabel))
            .addComponent(onStackLabel)
            .addGap(1)
            .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(2)
            .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        frame.getContentPane().setLayout(layout);
        //</editor-fold>
        
        updateArgumentsPanel();
        updateLocalVarsPanel();
        updateGlobalVarsPanel();
        updateArrayPanel();
        frame.setSize(300, 300);
        
        
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public JInternalFrame getFrame() {
        return frame;
    }
    
    public void savePosition(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(frame.getX());
        dataOutputStream.writeInt(frame.getY());
        dataOutputStream.writeInt(frame.getWidth());
        dataOutputStream.writeInt(frame.getHeight());
    }
    
    public void loadPosition(DataInputStream dataInputStream) throws IOException {
        int x = dataInputStream.readInt();
        int y = dataInputStream.readInt();
        int w = dataInputStream.readInt();
        int h = dataInputStream.readInt();
        frame.setBounds(x, y, w, h);
    }

    public void start(SyntaxTree syntaxTree, Instance mainInstance) {
        updateAllPanels(syntaxTree, mainInstance, 2);
    }

    public void update(Instance currentInstance) {
        if (observeCurrent && currentInstance != instance) {
            updateAllPanels(syntaxTree, currentInstance, 1);
            return;
        }
        updateAllPanels(syntaxTree, instance, 0);
    }

    public void select(Instance selectedInstance) {
        if (selectedInstance != instance) {
            updateAllPanels(syntaxTree, selectedInstance, 1);
            return;
        }
        updateAllPanels(syntaxTree, selectedInstance, 0);
    }

    public void clear() {
        updateAllPanels(null, null, 2);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void updateAllPanels(SyntaxTree syntaxTree, Instance instance, int arrayPanelBehavior) {
        synchronized (this) {
            this.syntaxTree = syntaxTree;
            this.instance = instance;
            onStack = instance != null && instance.isOnStack();
            if (selectedArrayAccess != null && (arrayPanelBehavior == 2 || (arrayPanelBehavior == 1
                    && selectedArrayAccess.getVariableScope() != VariableScope.GLOBAL))) {
                selectedArrayAccess = null;
                selectedArrayName = null;
            }
            updateArgumentsPanel();
            updateLocalVarsPanel();
            updateGlobalVarsPanel();
            updateArrayPanel();
        }
        updateFunctionName();
        updateReturnedValue();
        onStackLabel.setVisible(instance != null);
        if (instance != null) {
            onStackLabel.setText(onStack ? Lang.onStack : Lang.notOnStack);
        }
        variablessPanel.revalidate();
        argumentsPanel.repaint();
        localVarsPanel.repaint();
        globalVarsPanel.repaint();
    }

    private void updateFunctionName() {
        String txt;
        synchronized (this) {
            if (instance == null) {
                txt = "";
            } else {
                int w = functionNameLabel.getWidth();
                Extender ext = new StringExtender(instance.getFunction().getName(), 1, labelBoldFontMetrics);
                ext.fastExtend(w);
                txt = ext.getString();
            }
        }
        functionNameLabel.setText(txt);
    }

    private void updateReturnedValue() {
        boolean returnedValueExist;
        String txt = null;
        synchronized (this) {
            returnedValueExist = instance != null && !onStack && instance.getReturnedValue() != null;
            if (returnedValueExist) {
                int w = returnedValueLabel.getWidth();
                Extender ext = new IntegerExtender(instance.getReturnedValue(), labelBoldFontMetrics);
                ext.fastExtend(w);
                txt = ext.getString();
            }
        }
        returnedValueTextLabel.setVisible(returnedValueExist);
        returnedValueLabel.setVisible(returnedValueExist);
        if (returnedValueExist) {
            returnedValueLabel.setText(txt);
        }
    }

    private synchronized void variablesPanelResized(int width) {
        if (width == varsPanelsWidth) {
            return;
        }
        varsPanelsWidth = width;
        if (argumentsPanel.isVisible()) {
            updateArgumentsPanel();
        }
        if (localVarsPanel.isVisible()) {
            updateLocalVarsPanel();
        }
        if (globalVarsPanel.isVisible()) {
            updateGlobalVarsPanel();
        }
    }

    private synchronized void clickedOnVarsPanel(int which, int yPosition) {
        if (syntaxTree==null || instance == null || (which < 2 && !onStack)) {
            return;
        }
        if (which > 0) {
            yPosition -= ySpaceBetweenPanels;
        }
        int ySize = 2 * yGap + labelFontMetrics.getHeight() + 1;
        yPosition -= 2 * (yGap + 1) + labelBoldFontMetrics.getHeight() + ySize;
        if (yPosition < 0 || yPosition % ySize == ySize - 1) {
            return;
        }
        int idx = yPosition / ySize;
        AccessArray access = null;
        String name = null;
        if (which == 0 && instance.getArgument(idx).isArray()) {
            access = (AccessArray) instance.getFunction().getArgumentAccessVar(idx);
            name = instance.getFunction().getArgumentName(idx);
        }
        if (which == 1 && instance.getFunction().getLocalVarType(idx) == VariableType.ARRAY) {
            access = (AccessArray) instance.getFunction().getLocalVarAccessVar(idx);
            name = instance.getFunction().getLocalVarName(idx);
        }
        if (which == 2 && syntaxTree.getGlobalVarType(idx) == VariableType.ARRAY) {
            access = (AccessArray) syntaxTree.getGlobalVarAccessVar(idx);
            name = syntaxTree.getGlobalVarName(idx);
        }
        if (access != null && access != selectedArrayAccess) {
            selectedArrayAccess = access;
            selectedArrayName = name;
            arrayScrollBar.setValue(0);
            updateArrayPanel();
        }
    }

    
    private void addArgumentString(Extender ext, int position, int maxWidth) {
        ext.fastExtend(maxWidth);
        argumentsString.add(ext.getString());
        argumentsPosition.add(position + Math.max(0, (maxWidth - ext.getWidth()) / 2));
    }
    private void addLocalVarString(Extender ext, int position, int maxWidth) {
        ext.fastExtend(maxWidth);
        localVarsString.add(ext.getString());
        localVarsPosition.add(position + Math.max(0, (maxWidth - ext.getWidth()) / 2));
    }
    private void addGlobalVarString(Extender ext, int position, int maxWidth) {
        ext.fastExtend(maxWidth);
        globalVarsString.add(ext.getString());
        globalVarsPosition.add(position + Math.max(0, (maxWidth - ext.getWidth()) / 2));
    }
    private void addArrayValueString(Extender ext, int position, int maxWidth) {
        ext.fastExtend(maxWidth);
        arrayValuesString.add(ext.getString());
        arrayValuesPosition.add(position + Math.max(0, (maxWidth - ext.getWidth()) / 2));
    }
    
    private synchronized void updateArgumentsPanel() {
        argumentsString.clear();
        argumentsPosition.clear();

        boolean noArgs = instance == null || instance.getArgumentsLength() == 0;
        int widthHeader = labelBoldFontMetrics.stringWidth(noArgs ? Lang.noArguments : Lang.arguments);
        arguemtnsHeaderPosition = 1 + xGap + Math.max(0, (varsPanelsWidth - 2 - 2 * xGap - widthHeader) / 2);

        int h = 2 * (yGap + 1) + labelBoldFontMetrics.getHeight();
        argumentsPanel.setPreferredSize(new Dimension(0, h));
        if (noArgs) {
            argumentsPanel.setPreferredSize(new Dimension(0, h));
            return;
        }
        h += (instance.getArgumentsLength() + 1) * (2 * yGap + labelFontMetrics.getHeight() + 1);
        argumentsPanel.setPreferredSize(new Dimension(0, h));
        int nameLabelPosition = xGap + 1;

        int nameLabelWidth = (varsPanelsWidth - 4 * xGap) / 3 - 1;
        int valueLabelPosition = nameLabelPosition + nameLabelWidth + 2 * xGap + 1;
        int valueLabelWidth = varsPanelsWidth - valueLabelPosition - xGap - 1;
        int firstValueLabelWidth = (valueLabelWidth - 2 * xGap) / 2;
        int secondValueLabelPosition = valueLabelPosition + firstValueLabelWidth + 2 * xGap + 1;
        int secondValueLabelWidth = varsPanelsWidth - secondValueLabelPosition - xGap - 1;

        Extender ext = new StringExtender(Lang.name, 1, labelFontMetrics);
        addArgumentString(ext, nameLabelPosition, nameLabelWidth);
        ext = new StringExtender(Lang.valueAtTheBegining, 1, labelFontMetrics);
        addArgumentString(ext, valueLabelPosition, firstValueLabelWidth);
        ext = new StringExtender(onStack ? Lang.currentValue : Lang.valueAtTheEnd, 1, labelFontMetrics);
        addArgumentString(ext, secondValueLabelPosition, secondValueLabelWidth);

        Function f = instance.getFunction();
        for (int i = 0, size = instance.getArgumentsLength(); i < size; i++) {
            ext = new StringExtender(f.getArgumentName(i), 1, labelFontMetrics);
            addArgumentString(ext, nameLabelPosition, nameLabelWidth);

            Argument arg = instance.getArgument(i);
            if (arg.isInteger()) {
                ArgInteger argI = (ArgInteger) arg;
                BigInteger bigInt = argI.getValueAtTheBeginning();
                ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                        : new IntegerExtender(bigInt, labelFontMetrics);
                if (onStack) {
                    addArgumentString(ext, valueLabelPosition, firstValueLabelWidth);
                    bigInt = argI.getValue();
                    ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                            : new IntegerExtender(bigInt, labelFontMetrics);
                    addArgumentString(ext, secondValueLabelPosition, secondValueLabelWidth);
                } else {
                    addArgumentString(ext, valueLabelPosition, valueLabelWidth);
                }
            } else if (arg.isReference()) {
                ArgReference argR = (ArgReference) arg;
                BigInteger bigInt = argR.getValueAtTheBeginning();
                ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                        : new IntegerExtender(bigInt, labelFontMetrics);
                addArgumentString(ext, valueLabelPosition, firstValueLabelWidth);
                bigInt = onStack ? argR.getValue() : argR.getValueAtTheEnd();
                ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                        : new IntegerExtender(bigInt, labelFontMetrics);
                addArgumentString(ext, secondValueLabelPosition, secondValueLabelWidth);
            } else {
                ext = new StringExtender(arg.isArray() ? Lang.array : Lang.string, 1, labelFontMetrics);
                addArgumentString(ext, valueLabelPosition, valueLabelWidth);
            }
        }
    }

    private synchronized void updateLocalVarsPanel() {
        localVarsString.clear();
        localVarsPosition.clear();

        boolean noLocalVars = instance == null || !onStack || instance.getFunction().getLocalVarsLength() == 0;
        int widthHeader = labelBoldFontMetrics.stringWidth(noLocalVars ? Lang.noLocalVars : Lang.localVars);
        localVarsHeaderPosition = 1 + xGap + Math.max(0, (varsPanelsWidth - 2 - 2 * xGap - widthHeader) / 2);

        int h = 2 * (yGap + 1) + labelBoldFontMetrics.getHeight() + ySpaceBetweenPanels;
        if (noLocalVars) {
            localVarsPanel.setPreferredSize(new Dimension(0, h));
            return;
        }
        Function f = instance.getFunction();
        h += (f.getLocalVarsLength() + 1) * (2 * yGap + labelFontMetrics.getHeight() + 1);
        localVarsPanel.setPreferredSize(new Dimension(0, h));

        int nameLabelPosition = xGap + 1;
        int nameLabelWidth = (varsPanelsWidth - 4 * xGap) / 3 - 1;
        int valueLabelPosition = nameLabelPosition + nameLabelWidth + 2 * xGap + 1;
        int valueLabelWidth = varsPanelsWidth - valueLabelPosition - xGap - 1;

        Extender ext = new StringExtender(Lang.name, 1, labelFontMetrics);
        addLocalVarString(ext, nameLabelPosition, nameLabelWidth);
        ext = new StringExtender(Lang.currentValue, 1, labelFontMetrics);
        addLocalVarString(ext, valueLabelPosition, valueLabelWidth);


        for (int i = 0, size = f.getLocalVarsLength(); i < size; i++) {
            ext = new StringExtender(f.getLocalVarName(i), 1, labelFontMetrics);
            addLocalVarString(ext, nameLabelPosition, nameLabelWidth);

            if (f.getLocalVarType(i) == VariableType.INTEGER) {
                AccessInteger access = (AccessInteger) f.getLocalVarAccessVar(i);
                BigInteger bigInt = access.getValue(instance);
                ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                        : new IntegerExtender(bigInt, labelFontMetrics);
                addLocalVarString(ext, valueLabelPosition, valueLabelWidth);
            } else {
                ext = new StringExtender(Lang.array, 1, labelFontMetrics);
                addLocalVarString(ext, valueLabelPosition, valueLabelWidth);
            }
        }
    }

    private synchronized void updateGlobalVarsPanel() {
        globalVarsString.clear();
        globalVarsPosition.clear();

        boolean noGlobalVars = syntaxTree == null || syntaxTree.getGlobalVarsSize() == 0;
        int widthHeader = labelBoldFontMetrics.stringWidth(noGlobalVars ? Lang.noGlobalVars : Lang.globalVars);
        globalVarsHeaderPosition = 1 + xGap + Math.max(0, (varsPanelsWidth - 2 - 2 * xGap - widthHeader) / 2);

        int h = 2 * (yGap + 1) + labelBoldFontMetrics.getHeight() + ySpaceBetweenPanels;
        if (noGlobalVars) {
            globalVarsPanel.setPreferredSize(new Dimension(0, h));
            return;
        }
        Function f = instance.getFunction();
        h += (syntaxTree.getGlobalVarsSize() + 1) * (2 * yGap + labelFontMetrics.getHeight() + 1);
        globalVarsPanel.setPreferredSize(new Dimension(0, h));

        int nameLabelPosition = xGap + 1;
        int nameLabelWidth = (varsPanelsWidth - 4 * xGap) / 3 - 1;
        int valueLabelPosition = nameLabelPosition + nameLabelWidth + 2 * xGap + 1;
        int valueLabelWidth = varsPanelsWidth - valueLabelPosition - xGap - 1;

        Extender ext = new StringExtender(Lang.name, 1, labelFontMetrics);
        addGlobalVarString(ext, nameLabelPosition, nameLabelWidth);
        ext = new StringExtender(Lang.currentValue, 1, labelFontMetrics);
        addGlobalVarString(ext, valueLabelPosition, valueLabelWidth);


        for (int i = 0, size = syntaxTree.getGlobalVarsSize(); i < size; i++) {
            ext = new StringExtender(syntaxTree.getGlobalVarName(i), 1, labelFontMetrics);
            addGlobalVarString(ext, nameLabelPosition, nameLabelWidth);

            if (syntaxTree.getGlobalVarType(i) == VariableType.INTEGER) {
                AccessInteger access = (AccessInteger) syntaxTree.getGlobalVarAccessVar(i);
                BigInteger bigInt = access.getValue(instance);
                ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                        : new IntegerExtender(bigInt, labelFontMetrics);
                addGlobalVarString(ext, valueLabelPosition, valueLabelWidth);
            } else {
                ext = new StringExtender(Lang.array, 1, labelFontMetrics);
                addGlobalVarString(ext, valueLabelPosition, valueLabelWidth);
            }
        }
    }

    private synchronized void updateArrayPanel() {
        if (arrayIsUpdating) {
            return;
        }
        arrayIsUpdating = true;
        arrayValuesString.clear();
        arrayValuesPosition.clear();
        int maxWidht = arrayValuesPanel.getWidth() - 2 - 2 * xGap;
        if (selectedArrayAccess == null) {
            arrayHeaderString = Lang.noArray;
            int widthHeader = labelBoldFontMetrics.stringWidth(arrayHeaderString);
            arrayHeaderPosition = 1 + xGap + Math.max(0, (maxWidht - widthHeader) / 2);
            arrayScrollBar.setMaximum(0);
            arrayValuesPanel.repaint();
            arrayIsUpdating = false;
            return;
        }
        int size = selectedArrayAccess.getSizeInteger(instance);
        arrayScrollBar.setMaximum(size);
        int width = labelBoldFontMetrics.stringWidth(" [ " + size + " ]");
        Extender ext = new StringExtender(selectedArrayName, 1, labelFontMetrics);
        ext.fastExtend(maxWidht - width);
        arrayHeaderPosition = 1 + xGap + Math.max(0, (maxWidht - width - ext.getWidth()) / 2);
        arrayHeaderString = ext.getString() + " [ " + size + " ]";

        int h = Math.max(0, arrayValuesPanel.getHeight() - 2 * (yGap + 1) - labelBoldFontMetrics.getHeight());
        int ySize = 2 * yGap + labelFontMetrics.getHeight() + 1;
        int visible = h / ySize;

        if (visible == 0) {
            arrayScrollBar.setVisibleAmount(0);
        }
        int arrayIndexLabelPosition = 1 + xGap;
        int arrayValueLabelPosition = arrayIndexLabelPosition + arrayIndexLabelWidth + 2 * xGap + 1;
        int arrayValueLabelWidth = varsPanelsWidth - arrayValueLabelPosition - xGap - 1;

        ext = new StringExtender(Lang.index, 1, labelFontMetrics);
        addArrayValueString(ext, arrayIndexLabelPosition, arrayIndexLabelWidth);
        ext = new StringExtender(Lang.currentValue, 1, labelFontMetrics);
        addArrayValueString(ext, arrayValueLabelPosition, arrayValueLabelWidth);

        visible--;
        arrayScrollBar.setVisibleAmount(visible);
        arrayScrollBar.setValue(Math.min(arrayScrollBar.getValue(), Math.max(0, size - visible)));
        for (int i = 0, idx = arrayScrollBar.getValue(); i < visible && idx < size; i++, idx++) {
            ext = new StringExtender(Integer.toString(idx), 1, labelFontMetrics);
            addArrayValueString(ext, arrayIndexLabelPosition, arrayIndexLabelWidth);
            BigInteger bigInt = selectedArrayAccess.getValue(instance, idx);
            ext = (bigInt == null) ? new StringExtender(Lang.nullString, 1, labelFontMetrics)
                    : new IntegerExtender(bigInt, labelFontMetrics);
            addArrayValueString(ext, arrayValueLabelPosition, arrayValueLabelWidth);
        }
        arrayValuesPanel.repaint();
        arrayIsUpdating = false;
    }

    
    private synchronized void paintArgumentsPanel(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        g.setColor(argumentsColor);

        int w = argumentsPanel.getWidth();
        int h = argumentsPanel.getHeight();
        int w1 = w - 1, h1 = h - 1, w2 = w - 2;

        g.drawLine(0, 0, 0, h1);
        g.drawLine(w1, 0, w1, h1);
        g.drawLine(0, 0, w1, 0);
        g.drawLine(0, h1, w1, h1);

        g.setFont(labelBoldFont);
        int yAscent = labelBoldFontMetrics.getAscent() + yGap + 1;
        boolean noArgs = instance == null || instance.getArgumentsLength() == 0;
        g.drawString(noArgs ? Lang.noArguments : Lang.arguments,
                arguemtnsHeaderPosition, yAscent);
        if (noArgs) {
            return;
        }
        g.setFont(labelFont);
        int firstSeparatorPosition = (varsPanelsWidth - 4 * xGap) / 3 + 2 * xGap;
        int secondSeparatorPosition = firstSeparatorPosition + 2 * xGap + 1
                + (varsPanelsWidth - firstSeparatorPosition - 2 * xGap) / 2;
        int ascent = labelFontMetrics.getAscent();
        int ySize = 2 * yGap + labelFontMetrics.getHeight();
        int y = 1 + 2 * yGap + labelBoldFontMetrics.getHeight();
        g.drawLine(firstSeparatorPosition, y, firstSeparatorPosition, h1);
        for (int i = -1, size = instance.getArgumentsLength(), index = 0; i < size; i++) {
            g.drawLine(1, y, w2, y++);
            yAscent = y + ascent + yGap;
            int number;
            if (i == -1) {
                number = 3;
            } else {
                Argument arg = instance.getArgument(i);
                number = (arg.isInteger() && onStack) || arg.isReference() ? 3 : 2;
            }
            if (number == 3) {
                g.drawLine(secondSeparatorPosition, y, secondSeparatorPosition, y + ySize);
            }
            for (int j = 0; j < number; j++) {
                g.drawString(argumentsString.get(index), argumentsPosition.get(index), yAscent);
                index++;
            }
            y += ySize;
        }
    }

    private synchronized void paintLocalVarsPanel(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        g.setColor(localVarsColor);

        int w = localVarsPanel.getWidth();
        int h = localVarsPanel.getHeight();
        int w1 = w - 1, h1 = h - 1, w2 = w - 2;

        g.drawLine(0, ySpaceBetweenPanels, 0, h1);
        g.drawLine(w1, ySpaceBetweenPanels, w1, h1);
        g.drawLine(0, ySpaceBetweenPanels, w1, ySpaceBetweenPanels);
        g.drawLine(0, h1, w1, h1);

        g.setFont(labelBoldFont);
        int yAscent = ySpaceBetweenPanels + labelBoldFontMetrics.getAscent() + yGap + 1;
        boolean noLocalVars = instance == null || !onStack || instance.getFunction().getLocalVarsLength() == 0;
        g.drawString(noLocalVars ? Lang.noLocalVars : Lang.localVars,
                localVarsHeaderPosition, yAscent);
        if (noLocalVars) {
            return;
        }
        g.setFont(labelFont);
        int firstSeparatorPosition = (varsPanelsWidth - 4 * xGap) / 3 + 2 * xGap;
        int ascent = labelFontMetrics.getAscent();
        int ySize = 2 * yGap + labelFontMetrics.getHeight();
        int y = ySpaceBetweenPanels + 1 + 2 * yGap + labelBoldFontMetrics.getHeight();
        g.drawLine(firstSeparatorPosition, y, firstSeparatorPosition, h1);
        for (int i = -1, size = instance.getFunction().getLocalVarsLength(), index = 0; i < size; i++) {
            g.drawLine(1, y, w2, y++);
            yAscent = y + ascent + yGap;
            g.drawString(localVarsString.get(index), localVarsPosition.get(index), yAscent);
            index++;
            g.drawString(localVarsString.get(index), localVarsPosition.get(index), yAscent);
            index++;
            y += ySize;
        }
    }

    private synchronized void paintGlobalVarsPanel(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        g.setColor(globalVarsColor);

        int w = globalVarsPanel.getWidth();
        int h = globalVarsPanel.getHeight();
        int w1 = w - 1, h1 = h - 1, w2 = w - 2;

        g.drawLine(0, ySpaceBetweenPanels, 0, h1);
        g.drawLine(w1, ySpaceBetweenPanels, w1, h1);
        g.drawLine(0, ySpaceBetweenPanels, w1, ySpaceBetweenPanels);
        g.drawLine(0, h1, w1, h1);

        g.setFont(labelBoldFont);
        int yAscent = ySpaceBetweenPanels + labelBoldFontMetrics.getAscent() + yGap + 1;
        boolean noGlobalVars = syntaxTree == null || syntaxTree.getGlobalVarsSize() == 0;
        g.drawString(noGlobalVars ? Lang.noGlobalVars : Lang.globalVars,
                globalVarsHeaderPosition, yAscent);
        if (noGlobalVars) {
            return;
        }
        g.setFont(labelFont);
        int firstSeparatorPosition = (varsPanelsWidth - 4 * xGap) / 3 + 2 * xGap;
        int ascent = labelFontMetrics.getAscent();
        int ySize = 2 * yGap + labelFontMetrics.getHeight();
        int y = ySpaceBetweenPanels + 1 + 2 * yGap + labelBoldFontMetrics.getHeight();
        g.drawLine(firstSeparatorPosition, y, firstSeparatorPosition, h1);
        for (int i = -1, size = syntaxTree.getGlobalVarsSize(), index = 0; i < size; i++) {
            g.drawLine(1, y, w2, y++);
            yAscent = y + ascent + yGap;
            g.drawString(globalVarsString.get(index), globalVarsPosition.get(index), yAscent);
            index++;
            g.drawString(globalVarsString.get(index), globalVarsPosition.get(index), yAscent);
            index++;
            y += ySize;
        }
    }

    private synchronized void paintArrayValuesPanel(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        if (selectedArrayAccess == null || selectedArrayAccess.getVariableScope() == VariableScope.GLOBAL) {
            g.setColor(globalVarsColor);
        } else if (selectedArrayAccess.getVariableScope() == VariableScope.LOCAL) {
            g.setColor(localVarsColor);
        } else {
            g.setColor(argumentsColor);
        }

        int w = arrayValuesPanel.getWidth();
        int w1 = w - 1, w2 = w - 2;

        g.setFont(labelBoldFont);
        int yAscent = labelBoldFontMetrics.getAscent() + yGap + 1;
        int y = 1 + 2 * yGap + labelBoldFontMetrics.getHeight();
        g.drawString(arrayHeaderString, arrayHeaderPosition, yAscent);
        g.drawLine(0, 0, w1, 0);
        g.drawLine(0, y, w1, y);
        if (arrayValuesString.isEmpty()) {
            g.drawLine(0, 0, 0, y);
            g.drawLine(w1, 0, w1, y);
            return;
        }
        int h = y++;
        g.setFont(labelFont);
        int firstSeparatorPosition = arrayIndexLabelWidth + 2 * xGap + 2;
        int ascent = labelFontMetrics.getAscent();
        int ySize = 2 * yGap + labelFontMetrics.getHeight();
        for (int i = 0, size = arrayValuesString.size() / 2, index = 0; i < size; i++) {
            yAscent = y + ascent + yGap;
            g.drawString(arrayValuesString.get(index), arrayValuesPosition.get(index), yAscent);
            index++;
            g.drawString(arrayValuesString.get(index), arrayValuesPosition.get(index), yAscent);
            index++;
            y += ySize;
            g.drawLine(1, y - 1, w2, y - 1);
        }
        y--;
        g.drawLine(0, 0, 0, y);
        g.drawLine(firstSeparatorPosition, h, firstSeparatorPosition, y);
        g.drawLine(w1, 0, w1, y);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Instancja funkcji";
        
        public static final String functionName = "Nazwa funkcji: ";
        public static final String returnedValue = "Zwrócona wartość: ";
        public static final String onStack = "Instancja znajduje się na stosie";
        public static final String notOnStack = "Instancja została zdjęta ze stosu";
        
        public static final String arrayName = "Nazwa tablicy: ";
        public static final String arraySize = "Rozmiar tablicy: ";
        public static final String index = "Indeks";
        
        public static final String arguments = "ARGUMENTY";
        public static final String localVars = "ZMIENNE LOKALNE";
        public static final String globalVars = "ZMIENNE GLOBALNE";
        public static final String noArguments = "BRAK ARGUMENTÓW";
        public static final String noLocalVars = "BRAK ZMIENNYCH LOKALNYCH";
        public static final String noGlobalVars = "BRAK ZMIENNYCH GLOBALNYCH";
        
        public static final String noArray = "Nie wybrano żadnej tablicy";
        
        public static final String name = "Nazwa";
        public static final String valueAtTheBegining = "Wartość przy wywołaniu";
        public static final String currentValue = "Obecna wartość";
        public static final String valueAtTheEnd = "Wartość końcowa";
        
        public static final String string = "<< Napis >>";
        public static final String array = "<< Tablica >>";
        public static final String nullString = "?????";
    }
    //</editor-fold>
    
}
   