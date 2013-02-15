package statistics;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import instanceframe.InstanceFrame;
import interpreter.accessvar.VariableType;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import mainclass.MainClass;
import syntax.SyntaxNode;
import syntax.expression.operators.Operation;
import syntax.expression.operators.OperationType;
import syntax.function.FunctionComparator;
import syntax.function.FunctionDeclaration;
import syntax.statement.assigment.Assigment;
import syntax.statement.assigment.IncDec;
//</editor-fold>

public class Statistics {
    
    private final static String iconDir = "/instanceframe/icons/";
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private MainClass mainClass;
    
    private ImageIcon rememberIcon = new ImageIcon(getClass().getResource(iconDir + "save.png"));
    private ImageIcon deleteIcon = new ImageIcon(getClass().getResource(iconDir + "delete.png"));
    
    private JInternalFrame frame;
    private JScrollPane scrollPane;
    private JPanel panel;
    
    private JMenu optionsMenu;
    private JMenuItem saveAsTextMenuItem;
    private JMenuItem saveAsPictureMenuItem;
    
    private JButton extendGroupButton[] = new JButton[6];
    private boolean extendGroup[] = new boolean[6];
    
    private Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private FontMetrics labelFontMetrics;
    private Object textAntiAliasingHint;
    
    private int xGap = 2, yGap = 0;
    private Insets noMargin = new Insets(0, 0, 0, 0);
    private FunctionComparator functionComparator = new FunctionComparator();
    
    private StatNode currentStatNode, displayedStatNode;
    private ArrayList<StatNode> rememberedStatNodes = new ArrayList<StatNode>();
    
    int labelsWidth;
    private String operationsString[] = {
        Lang.assigmentGroup, Lang.assigment, Lang.inc, Lang.dec,
        Lang.addGroup, Lang.add, Lang.sub, Lang.minus,
        Lang.multGroup, Lang.mult, Lang.div, Lang.mod,
        Lang.logicGroup, Lang.and, Lang.or, Lang.not,
        Lang.compGroup, Lang.eq, Lang.notEq, Lang.greater, Lang.less, Lang.greaterEq, Lang.lessEq,
        Lang.callGroup
    };
    private int operationsPosition[] = new int[24];
    private TreeSet<FunctionDeclaration> functionsSet = new TreeSet<>(functionComparator);
    private String functionsString[];
    private int functionsPosition[];
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public Statistics(MainClass mainClass) {
        this.mainClass = mainClass;
        
        frame = new JInternalFrame(Lang.frameTitle);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        frame.setComponentPopupMenu(null);

        labelFontMetrics = frame.getFontMetrics(labelFont);
        textAntiAliasingHint = labelFontMetrics.getFontRenderContext().getAntiAliasingHint();
        
        //<editor-fold defaultstate="collapsed" desc="Init menu">
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        optionsMenu = new JMenu(Lang.optionsMenu);
        menuBar.add(optionsMenu);
        saveAsTextMenuItem = new JMenuItem(Lang.saveAsTextMenuItem);
        optionsMenu.add(saveAsTextMenuItem);
        saveAsPictureMenuItem = new JMenuItem(Lang.saveAsPictureMenuItem);
        optionsMenu.add(saveAsPictureMenuItem);
        
        saveAsTextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsText();
            }
        });
        saveAsPictureMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsPicture();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init scrollPane and panel">
        scrollPane = new JScrollPane();
        frame.add(scrollPane);

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                synchronized (frame.getTreeLock()) {
                    paintPanel(g);
                }
            }
        };
        panel.setLayout(null);
        scrollPane.setViewportView(panel);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init extendGroupButtons">
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (frame.getTreeLock()) {
                    extendOrNarrowGroup(e.getActionCommand().charAt(0));
                }
            }
        };
        for (int i = 0; i < 6; i++) {
            extendGroupButton[i] = new JButton("+");
            extendGroupButton[i].setFont(labelFont);
            extendGroupButton[i].setMargin(noMargin);
            extendGroupButton[i].setFocusable(false);
            extendGroupButton[i].setToolTipText(Lang.extend);
            extendGroupButton[i].setActionCommand(Character.toString((char) i));
            extendGroupButton[i].setSize(extendGroupButton[i].getPreferredSize().width,
                    labelFontMetrics.getHeight() + 2 * yGap);
            extendGroupButton[i].addActionListener(actionListener);
            panel.add(extendGroupButton[i]);
        }
        //</editor-fold>

        currentStatNode = new StatNode(0);
        displayedStatNode = new StatNode(1);

        updateExtendGroupButtons();
        updateFunctionsSet();
        updatePanelPrefSize();
        updateStatNodeButtons();
        //frame.setSize(200, 200);
        frame.pack();
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

    public void start() {
        synchronized (frame.getTreeLock()) {
            currentStatNode.clear();
            displayedStatNode.clear();
            updateFunctionsSet();
            updateStatNodeButtons();
            updatePanelPrefSize();

            panel.revalidate();
            panel.repaint();
        }
    }
    public void update() {
        synchronized (frame.getTreeLock()) {
            displayedStatNode.copyValues(currentStatNode);
            updateFunctionsSet();
            updateStatNodeButtons();
            updatePanelPrefSize();

            panel.revalidate();
            panel.repaint();
        }
    }

    public void addSyntaxNode(SyntaxNode node) {
        if (node instanceof Operation) {
            OperationType operation = ((Operation) node).getOperationType();
            currentStatNode.increaseOperationCounter(
                    operation.getStatisticsOperationIndex());
            currentStatNode.increaseOperationCounter(
                    operation.getStatisticsOperationGroupIndex());
        } else if (node instanceof Assigment) {
            OperationType operation = ((Assigment) node).getOperationType();
            if (operation != OperationType.NOTHING) {
                currentStatNode.increaseOperationCounter(
                        operation.getStatisticsOperationIndex());
                currentStatNode.increaseOperationCounter(
                        operation.getStatisticsOperationGroupIndex());
            }
            currentStatNode.increaseOperationCounter(1);
            currentStatNode.increaseOperationCounter(0);
        } else if (node instanceof IncDec) {
            if (((IncDec) node).increasing) {
                currentStatNode.increaseOperationCounter(2);
            } else {
                currentStatNode.increaseOperationCounter(3);
            }
            currentStatNode.increaseOperationCounter(0);
        } else {
            throw new AssertionError();
        }
    }
    public void addFunciton(FunctionDeclaration f) {
        currentStatNode.increaseFunctionCounter(f);
        currentStatNode.increaseOperationCounter(23);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void drawOperationsCounters(Graphics g, int index, int y) {
        g.drawString(operationsString[index], xGap + operationsPosition[index], y);
        int x = labelsWidth + 2;
        for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
            StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
            String str = Long.toString(node.getOperationsCounter(index));
            int position = (node.getWidth() - labelFontMetrics.stringWidth(str)) / 2;
            g.drawString(str, x + position, y);
            x += node.getWidth() + 2;
        }
    }
    private void drawFunctionsCounters(Graphics g, FunctionDeclaration f, int index, int y) {
        g.drawString(functionsString[index], xGap + functionsPosition[index], y);
        int x = labelsWidth + 2;
        for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
            StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
            String str = Long.toString(node.getFunctionCounter(f));
            int position = (node.getWidth() - labelFontMetrics.stringWidth(str)) / 2;
            g.drawString(str, x + position, y);
            x += node.getWidth() + 2;
        }
    }
    private void paintPanel(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        g.setFont(labelFont);
        int w = panel.getPreferredSize().width - 1;
        int h = panel.getPreferredSize().height - 1;

        int x = labelsWidth;
        g.drawLine(x, 0, x++, h);
        g.drawLine(x, 0, x++, h);
        for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
            StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
            x += node.getWidth();
            g.drawLine(x, 0, x++, h);
            g.drawLine(x, 0, x++, h);
        }

        int y = displayedStatNode.getButtonPreferredHeight();
        g.drawLine(0, y, w, y++);
        g.drawLine(0, y, w, y++);
        int ySize = labelFontMetrics.getHeight() + 2 * yGap;
        int yAscent = yGap + labelFontMetrics.getAscent();
        for (int i = 0, index = 0; i < 6; i++) {
            drawOperationsCounters(g, index++, y + yAscent);
            y += ySize;
            if (extendGroup[i]) {
                if (i == 5) {
                    index = 0;
                    for (FunctionDeclaration f : functionsSet) {
                        g.drawLine(0, y, w, y++);
                        drawFunctionsCounters(g, f, index++, y + yAscent);
                        y += ySize;
                    }
                } else {
                    int number = i < 4 ? 3 : 6;
                    for (int j = 0; j < number; j++) {
                        g.drawLine(0, y, w, y++);
                        drawOperationsCounters(g, index++, y + yAscent);
                        y += ySize;
                    }
                }
            } else {
                index += i < 4 ? 3 : 6;
            }
            g.drawLine(0, y, w, y++);
            g.drawLine(0, y, w, y++);
        }
    }
    
    
    private void updateExtendGroupButtons() {
        int y = displayedStatNode.getButtonPreferredHeight() + 2;
        int ySize = labelFontMetrics.getHeight() + 2 * yGap;
        for (int i = 0; i < 6; i++) {
            extendGroupButton[i].setLocation(0, y);
            y += ySize + 2;
            if (extendGroup[i]) {
                int number = i < 4 ? 3 : (i == 4 ? 6 : functionsSet.size());
                y += (1 + ySize) * number;
            }
        }
        Dimension d = panel.getPreferredSize();
        d.height = y;
        panel.setPreferredSize(d);
    }
    private void extendOrNarrowGroup(int index) {
        extendGroup[index] = !extendGroup[index];
        extendGroupButton[index].setText(extendGroup[index] ? "-" : "+");
        extendGroupButton[index].setToolTipText(
                extendGroup[index] ? Lang.narrow : Lang.extend);
        updateExtendGroupButtons();
        panel.repaint();
    }
    
    
    private void updateFunctionsSet() {
        int maxWidth = 0, btnWidth = extendGroupButton[0].getWidth();
        for (int i = 0; i < 24; i++) {
            operationsPosition[i] = labelFontMetrics.stringWidth(operationsString[i]);
            if ((i < 17 && i % 4 == 0) || i == 23) {
                operationsPosition[i] += btnWidth;
            }
            maxWidth = Math.max(maxWidth, operationsPosition[i]);
        }
        functionsSet.clear();
        displayedStatNode.addFunctions(functionsSet);
        for (int i = 0, size = rememberedStatNodes.size(); i < size; i++) {
            rememberedStatNodes.get(i).addFunctions(functionsSet);
        }
        functionsString = new String[functionsSet.size()];
        functionsPosition = new int[functionsSet.size()];
        int index = 0;
        for (FunctionDeclaration f : functionsSet) {
            if (f.getName().equals("write") || f.getName().equals("writeln")) {
                functionsString[index] = "write()";
            } else {
                StringBuilder sb = new StringBuilder(f.getName());
                sb.append('(');
                for (int i = 0, size = f.getArgumentsLength(); i < size; i++) {
                    if (i != 0) {
                        sb.append(',');
                    }
                    if (f.getArgumentType(i) == VariableType.INTEGER) {
                        sb.append('i');
                    } else if (f.getArgumentType(i) == VariableType.REFERENCE) {
                        sb.append('&');
                    } else {
                        sb.append("[]");
                    }
                }
                sb.append(')');
                functionsString[index] = sb.toString();
            }
            functionsPosition[index] = labelFontMetrics.stringWidth(functionsString[index]);
            maxWidth = Math.max(maxWidth, functionsPosition[index]);
            index++;
        }
        for (int i = 0; i < 24; i++) {
            operationsPosition[i] = (maxWidth - operationsPosition[i]) / 2;
            if ((i < 17 && i % 4 == 0) || i == 23) {
                operationsPosition[i] += btnWidth;
            }
        }
        for (int i = 0; i < functionsSet.size(); i++) {
            functionsPosition[i] = (maxWidth - functionsPosition[i]) / 2;
        }
        labelsWidth = maxWidth + 2 * xGap;
    }

    private void updatePanelPrefSize() {
        int w = labelsWidth + displayedStatNode.getWidth() + 4;
        for (int i = 0, size = rememberedStatNodes.size(); i < size; i++) {
            w += rememberedStatNodes.get(i).getWidth() + 2;
        }
        int h = displayedStatNode.getButtonPreferredHeight() + 2;
        int ySize = labelFontMetrics.getHeight() + 2 * yGap;
        for (int i = 0; i < 6; i++) {
            h += ySize + 2;
            if (extendGroup[i]) {
                int number = i < 4 ? 3 : (i == 4 ? 6 : functionsSet.size());
                h += (1 + ySize) * number;
            }
        }
        panel.setPreferredSize(new Dimension(w, h));
    }

    private void updateStatNodeButtons() {
        int x = labelsWidth + 2;
        for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
            StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
            node.updateButtonPosition(x);
            x += node.getWidth() + 2;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="save functions">
    private void saveAsPicture() {
        JFileChooser chooser = new JFileChooser(mainClass.getSaveReportDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".png");
            }
            @Override
            public String getDescription() {
                return "Obraz PNG (*.png)";
            }
        });
        chooser.setApproveButtonText(Lang.save);
        chooser.setDialogTitle(Lang.save);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if ( !file.getName().endsWith(".png") ) {
            file = new File(file.getPath()+".png");
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException | SecurityException ex) {
                return;
            }
        }
        if (!file.canWrite()) {
            return;
        }
        BufferedImage image;
        synchronized (this) {
            image = new BufferedImage(panel.getWidth(),
                    panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            panel.paint(image.getGraphics());
        }
        try {
            ImageIO.write(image, "png", file);
            mainClass.setSaveReportFile(file);
        } catch (Exception ex) {
        }
    }
    
    private void saveAsText() {
        JFileChooser chooser = new JFileChooser(mainClass.getSaveReportDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter( new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".txt");
            }
            @Override
            public String getDescription() {
                return "Plik tekstowy (*.txt)";
            }
        });
        chooser.setApproveButtonText(Lang.save);
        chooser.setDialogTitle(Lang.save);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if ( !file.getName().endsWith(".txt") ) {
            file = new File(file.getPath()+".txt");
        }
        try {
            FileWriter writer = new FileWriter(file);
            String separator = System.getProperty("line.separator");
            int operationsStringLength = operationsString.length;
            for (int idx = 0; idx < operationsStringLength; idx++) {
                if ((4 <= idx && idx <= 16 && idx % 4 == 0) || idx + 1 == operationsStringLength) {
                    writer.write(separator);
                }
                writer.write(operationsString[idx]);
                writer.write(" : ");
                for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
                    StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
                    if (i != -1) {
                        writer.write(", ");
                    }
                    writer.write(Long.toString(node.getOperationsCounter(idx)));
                }
                writer.write(separator);
            }
            int funcIdx = 0;
            for (FunctionDeclaration f : functionsSet) {
                writer.write(functionsString[funcIdx++]);
                writer.write(" : ");
                for (int i = -1, size = rememberedStatNodes.size(); i < size; i++) {
                    StatNode node = i == -1 ? displayedStatNode : rememberedStatNodes.get(i);
                    if (i != -1) {
                        writer.write(", ");
                    }
                    writer.write(Long.toString(node.getFunctionCounter(f)));
                }
                writer.write(separator);
            }
            writer.flush();
            writer.close();
            mainClass.setSaveReportFile(file);
        } catch (IOException ex) {
        }
    }
    //</editor-fold>
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Class StatNode">
    private class StatNode {
        private long operationsCounter[] = new long[24];
        private TreeMap<FunctionDeclaration, Long> functionsCounter = new TreeMap<>(functionComparator);
        private long maxValue;
        private int width;
        private JButton button;
        
        //<editor-fold defaultstate="collapsed" desc="Constructor">
        public StatNode(int buttonType) {
            width = labelFontMetrics.charWidth('0') + 2 * xGap;
            if ( buttonType==0 ) {
                return;
            }
            button = new JButton(buttonType == 1 ? rememberIcon : deleteIcon);
            button.setMargin(noMargin);
            button.setFocusable(false);
            button.setToolTipText(buttonType == 1 ? Lang.remember : Lang.delete);
            button.addActionListener(buttonType == 1 
                    ? new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    synchronized (frame.getTreeLock()) {
                        addStatNode();
                    }
                }
            }
                    : new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    synchronized (frame.getTreeLock()) {
                        removeStatNode();
                    }
                }

            });
            panel.add(button);
            width = Math.max(width, button.getPreferredSize().width);
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Get methods">
        public int getWidth() {
            return width;
        }
        public long getFunctionCounter(FunctionDeclaration f) {
            Long value = functionsCounter.get(f);
            return value == null ? 0 : value;
        }
        public long getOperationsCounter(int index) {
            return operationsCounter[index];
        }
        public int getButtonPreferredHeight() {
            return button.getPreferredSize().height;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Increase counter and clear methods">
        public void increaseOperationCounter(int index) {
            operationsCounter[index]++;
            if (operationsCounter[index] > maxValue) {
                maxValue = operationsCounter[index];
            }
        }
        public void increaseFunctionCounter(FunctionDeclaration f) {
            Long counter = functionsCounter.get(f);
            counter = counter == null ? (long) 1 : counter + 1;
            functionsCounter.put(f, counter);
            if (counter > maxValue) {
                maxValue = counter;
            }
        }
        public void clear() {
            Arrays.fill(operationsCounter, 0);
            functionsCounter.clear();
            maxValue = 0;
            width = labelFontMetrics.charWidth('0') + 2 * xGap;
            if (button != null && width < button.getPreferredSize().width) {
                width = button.getPreferredSize().width;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Other methods">
        public void addFunctions(TreeSet<FunctionDeclaration> set) {
            set.addAll(functionsCounter.keySet());
        }
        public void copyValues(StatNode statNode) {
            System.arraycopy(statNode.operationsCounter, 0, operationsCounter, 0, 24);
            functionsCounter.clear();
            functionsCounter.putAll(statNode.functionsCounter);
            maxValue = statNode.maxValue;
            width = Math.max(labelFontMetrics.stringWidth(Long.toString(maxValue)) + 2 * xGap,
                    button.getPreferredSize().width);
        }
        public void updateButtonPosition(int x) {
            button.setSize(width, button.getPreferredSize().height);
            button.setLocation(x, 0);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Private Add and Remove StatNode methods">
        private void addStatNode() {
            StatNode node = new StatNode(2);
            node.copyValues(this);
            rememberedStatNodes.add(node);
            updatePanelPrefSize();
            updateStatNodeButtons();
            panel.revalidate();
            panel.repaint();
        }
        private void removeStatNode() {
            panel.remove(button);
            rememberedStatNodes.remove(this);
            updateFunctionsSet();
            updatePanelPrefSize();
            updateStatNodeButtons();
            panel.revalidate();
            panel.repaint();
        }
        //</editor-fold>
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Statystyka";
        
        public static final String optionsMenu = "Opcje";
        public static final String saveAsTextMenuItem = "Zapisz jako tekst...";
        public static final String saveAsPictureMenuItem = "Zapisz jako obraz...";
        
        public static final String assigmentGroup = "Przypisania...";
        public static final String assigment = "Przypisanie";
        public static final String inc = "Inkrementacja (++)";
        public static final String dec = "Dekrementacja (--)";
        
        public static final String addGroup = "Operacje dodawania...";
        public static final String add = "Dodawanie";
        public static final String sub = "Odejmowanie";
        public static final String minus = "Minus unarny";
        
        public static final String multGroup = "Operacje mnożenia...";
        public static final String mult = "Mnożenie";
        public static final String div = "Dzielenie";
        public static final String mod = "Modulo";
        
        public static final String logicGroup = "Operacje logiczne...";
        public static final String and = "Koniunkcja (&&)";
        public static final String or = "Alternatywa (||)";
        public static final String not = "Negacja (!)";
        
        public static final String compGroup = "Porównania...";
        public static final String eq = "Równe (==)";
        public static final String notEq = "Różne (!=)";
        public static final String greater = "Większe (>)";
        public static final String less = "Mniejsze (<)";
        public static final String greaterEq = "Większe lub równe (>=)";
        public static final String lessEq = "Mniejsze lub równe (<=)";
        
        public static final String callGroup = "Wywołania funkcji...";
        
        public static final String remember = "Zapamiętaj";
        public static final String delete = "Usuń";
        
        public static final String extend = "Rozwiń";
        public static final String narrow = "Zwiń";
        
        public static final String save = "Zapisz";
    }
    //</editor-fold>
    
}
