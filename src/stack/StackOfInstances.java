package stack;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import instanceframe.InstanceFrame;
import interpreter.Instance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import mainclass.MainClass;
import stringcreator.StringCreator;
//</editor-fold>

public class StackOfInstances {

    private final static String iconDir = "/stack/icons/";

    // <editor-fold defaultstate="collapsed" desc="Components">
    private JInternalFrame frame;

    private JButton up1Button;
    private JButton up2Button;
    private JButton up3Button;
    
    private JButton down1Button;
    private JButton down2Button;
    private JButton down3Button;

    private JPanel panel;
    private JLabel emptyLabel;
    
    private JMenuBar menuBar;
    private JMenu observeMenu;
    private JRadioButtonMenuItem observeTopMenuItem;
    private JRadioButtonMenuItem observeSelectedMenuItem;
    private JMenu jumpLengthMenu;
    private JRadioButtonMenuItem jumpLengthMenuItems[];
    private JMenu optionsMenu;
    private JCheckBoxMenuItem showButtonsMenuItem;
    private JMenuItem saveAsPictureMenuItem;
    private JMenuItem saveAsTextMenuItem;
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Variables">
    private MainClass mainClass;
    private InstanceFrame instanceFrame;

    private Font nodeLabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private FontMetrics nodeLabelFontMetrics;
    private Object textAntiAliasingHint;    
    
    private int nodeLabelFontAscent, yPositionBottom;
    private int xRectSize, yRectSize;
    private int margin = 3, yGap = 3;
    
    private ArrayList<StackNode> nodes = new ArrayList<StackNode>();
    private int nodesSize;
    
    private boolean markCurrent;
    private boolean currentOnStack;
    
    private int bottomIndex, topIndex, selectedIndex;
    
    private Instance mainInstance;
    private Instance currentInstance;
    private Instance selectedInstance;
    
    private boolean observeTop = true;
    private int jumpLenghtsArray[] = {2, 5, 10, 20, 50};
    private int jumpLength = jumpLenghtsArray[0];
    
    private Color colorNewInstance = new Color(191, 255, 255);
    private Color colorRemovedInstance = new Color(255, 255, 191);
    private Color colorOnStackInstance = new Color(191, 255, 191);
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public StackOfInstances(MainClass mainClass, InstanceFrame instanceFrame) {
        this.mainClass = mainClass;
        this.instanceFrame = instanceFrame;
        frame = new JInternalFrame(Lang.frameTitle);

        //<editor-fold defaultstate="collapsed" desc="Init buttons">
        Insets noMargin = new Insets(0, 0, 0, 0);
        
        ImageIcon icon = new ImageIcon(getClass().getResource(iconDir + "up1.png"));
        up1Button = new JButton(icon);
        up1Button.setMargin(noMargin);
        up1Button.setFocusable(false);
        up1Button.setActionCommand("U\0");
        up1Button.setToolTipText(Lang.up1ToolTip);
        
        icon = new ImageIcon(getClass().getResource(iconDir + "up2.png"));
        up2Button = new JButton(icon);
        up2Button.setMargin(noMargin);
        up2Button.setFocusable(false);
        up2Button.setActionCommand("U\1");
        up2Button.setToolTipText(Lang.up2ToolTip);
        
        icon = new ImageIcon(getClass().getResource(iconDir + "up3.png"));
        up3Button = new JButton(icon);
        up3Button.setMargin(noMargin);
        up3Button.setFocusable(false);
        up3Button.setActionCommand("U\2");
        up3Button.setToolTipText(Lang.up3ToolTip);
        
        icon = new ImageIcon(getClass().getResource(iconDir + "down1.png"));
        down1Button = new JButton(icon);
        down1Button.setMargin(noMargin);
        down1Button.setFocusable(false);
        down1Button.setActionCommand("D\0");
        down1Button.setToolTipText(Lang.down1ToolTip);
        
        icon = new ImageIcon(getClass().getResource(iconDir + "down2.png"));
        down2Button = new JButton(icon);
        down2Button.setMargin(noMargin);
        down2Button.setFocusable(false);
        down2Button.setActionCommand("D\1");
        down2Button.setToolTipText(Lang.down2ToolTip);
        
        icon = new ImageIcon(getClass().getResource(iconDir + "down3.png"));
        down3Button = new JButton(icon);
        down3Button.setMargin(noMargin);
        down3Button.setFocusable(false);
        down3Button.setActionCommand("D\2");
        down3Button.setToolTipText(Lang.down3ToolTip);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Init menu">
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        observeMenu = new JMenu(Lang.observe);
        menuBar.add(observeMenu);
        
        ButtonGroup buttonGroup = new ButtonGroup();
        observeTopMenuItem = new JRadioButtonMenuItem(Lang.observeTopStack);
        observeTopMenuItem.setActionCommand("t");
        observeTopMenuItem.setSelected(true);
        buttonGroup.add(observeTopMenuItem);
        observeMenu.add(observeTopMenuItem);
        
        observeSelectedMenuItem = new JRadioButtonMenuItem(Lang.observeSelected);
        observeSelectedMenuItem.setActionCommand("s");
        buttonGroup.add(observeSelectedMenuItem);
        observeMenu.add(observeSelectedMenuItem);
        
        jumpLengthMenu = new JMenu(Lang.jumpLength);
        menuBar.add(jumpLengthMenu);
        
        buttonGroup = new ButtonGroup();
        jumpLengthMenuItems = new JRadioButtonMenuItem[jumpLenghtsArray.length];
        for (int i = 0; i < jumpLenghtsArray.length; i++) {
            jumpLengthMenuItems[i] = new JRadioButtonMenuItem(Integer.toString(jumpLenghtsArray[i]));
            jumpLengthMenuItems[i].setActionCommand("j"+((char) i));
            buttonGroup.add(jumpLengthMenuItems[i]);
            jumpLengthMenu.add(jumpLengthMenuItems[i]);
        }
        jumpLengthMenuItems[0].setSelected(true);
        
        optionsMenu = new JMenu(Lang.options);
        menuBar.add(optionsMenu);

        showButtonsMenuItem = new JCheckBoxMenuItem(Lang.showButtons);
        showButtonsMenuItem.setSelected(true);
        showButtonsMenuItem.setActionCommand("b");
        optionsMenu.add(showButtonsMenuItem);

        saveAsPictureMenuItem = new JMenuItem(Lang.saveAsPictureDots);
        saveAsPictureMenuItem.setActionCommand("p");
        optionsMenu.add(saveAsPictureMenuItem);
        
        saveAsTextMenuItem = new JMenuItem(Lang.saveAsTextDots);
        saveAsTextMenuItem.setActionCommand("x");
        optionsMenu.add(saveAsTextMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Button and Menu listeners">
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                char c = cmd.charAt(0);
                switch (c) {
                    case 'U':
                        jump(cmd.charAt(1), true);
                        break;
                    case 'D':
                        jump(cmd.charAt(1), false);
                        break;
                    case 'j':
                        jumpLength = jumpLenghtsArray[cmd.charAt(1)];
                        break;
                    case 't':
                        observeTop = true;
                        break;
                    case 's':
                        observeTop = false;
                        break;
                    case 'b':
                        setVisibleButtons();
                        break;
                    case 'p':
                        saveAsPicture();
                        break;
                    case 'x':
                        saveAsText();
                        break;
                }
            }
        };

        up1Button.addActionListener(actionListener);
        up2Button.addActionListener(actionListener);
        up3Button.addActionListener(actionListener);

        down1Button.addActionListener(actionListener);
        down2Button.addActionListener(actionListener);
        down3Button.addActionListener(actionListener);

        observeTopMenuItem.addActionListener(actionListener);
        observeSelectedMenuItem.addActionListener(actionListener);
        
        for (int i = 0; i < jumpLenghtsArray.length; i++) {
            jumpLengthMenuItems[i].addActionListener(actionListener);
        }
        
        showButtonsMenuItem.addActionListener(actionListener);
        saveAsPictureMenuItem.addActionListener(actionListener);
        saveAsTextMenuItem.addActionListener(actionListener);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="InitPanel">
        panel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintStack(g);
            }
        };
        panel.setLayout(null);
        panel.setFocusable(true);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePanelView();
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseClickedOnPanel(e.getX(), e.getY());
            }
        });
        nodeLabelFontMetrics = panel.getFontMetrics(nodeLabelFont);
        textAntiAliasingHint = nodeLabelFontMetrics.getFontRenderContext().getAntiAliasingHint();
        yRectSize = nodeLabelFontMetrics.getHeight();
        nodeLabelFontAscent = nodeLabelFontMetrics.getAscent();
        panel.setMinimumSize(new Dimension(40, 2 * margin + 3 * (yRectSize + yGap) - yGap));
        
        emptyLabel = new JLabel(Lang.emptyStack);
        emptyLabel.setFont(nodeLabelFont);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setLocation(0,0);
        panel.add(emptyLabel);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="KeyListener">
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int mode = e.isShiftDown() ? 2
                        : (e.isControlDown() ? 1 : 0);
                int keyCode = e.getKeyCode();
                
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        jump(mode, true);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        jump(mode, true);
                        break;
                    case KeyEvent.VK_DELETE:
                        showButtonsMenuItem.doClick();
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(up1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(up2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(up3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
            .addComponent(panel)
            .addGroup(layout.createSequentialGroup()
                .addComponent(down1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(down2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(down3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(up1Button)
                .addComponent(up2Button)
                .addComponent(up3Button))
            .addComponent(panel)
            .addGroup(layout.createParallelGroup()
                .addComponent(down1Button)
                .addComponent(down2Button)
                .addComponent(down3Button)));
        frame.getContentPane().setLayout(layout);
        //</editor-fold>

        frame.setLocation(420, 0);
        frame.setResizable(true);
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

    public synchronized void start(Instance mainInstance) {
        emptyLabel.setVisible(false);
        this.mainInstance = mainInstance;
        currentInstance = mainInstance;
        selectedInstance = mainInstance;
        updateStack();
    }

    public synchronized void update(Instance currentInstance) {
        this.currentInstance = currentInstance;
        currentOnStack = currentInstance.isOnStack();
        if (observeTop) {
            selectedInstance = currentInstance;
        }
        updateStack();
    }

    public synchronized void mark() {
        markCurrent = true;
        panel.repaint();
    }

    public synchronized void unmark() {
        markCurrent = false;
        panel.repaint();
    }

    public synchronized void clear() {
        emptyLabel.setSize(panel.getWidth(), emptyLabel.getPreferredSize().height);
        emptyLabel.setVisible(true);

        mainInstance = null;
        currentInstance = null;
        selectedInstance = null;

        panel.repaint();
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void setVisibleButtons() {
        boolean shown = showButtonsMenuItem.isSelected();
        up1Button.setVisible(shown);
        up2Button.setVisible(shown);
        up3Button.setVisible(shown);

        down1Button.setVisible(shown);
        down2Button.setVisible(shown);
        down3Button.setVisible(shown);
    }
    
    private void saveAsPicture() {
        JFileChooser chooser = new JFileChooser(mainClass.getCurrentDir());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter( new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".png");
            }
            @Override
            public String getDescription() {
                return "PNG (*.png)";
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
        BufferedImage image;
        synchronized (this) {
            image = new BufferedImage(panel.getWidth(),
                    panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            paintStack(image.getGraphics());
        }
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException ex) {
        }
    }
    
    private void saveAsText() {
        //TODO text
        JFileChooser chooser = new JFileChooser(mainClass.getCurrentDir());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter( new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".png");
            }
            @Override
            public String getDescription() {
                return "TXT (*.txt)";
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
            synchronized (this) {
                if ( selectedInstance!=null ) {
                    for (int i=topIndex; i>=bottomIndex; i--) {
                        nodes.get(i).write(writer);
                    }
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }
    }
    
    
    private void updateStack() {
        if ( selectedInstance != currentInstance ) {
            while (!selectedInstance.isOnStack()) {
                selectedInstance = selectedInstance.getParentInstance();
            }
        }
        
        nodes.clear();
        nodesSize = 0;
        Instance instance = mainInstance;
        do {
            if ( instance==selectedInstance ) {
                selectedIndex = nodesSize;
            }
            StackNode node = new StackNode(instance);
            nodes.add(node);
            nodesSize++;
            if ( instance==currentInstance ) {
                break;
            }
            instance = instance.getRightChildInstance();
        } while ( instance!=null && (instance.isOnStack() || instance==currentInstance) );
        
        updatePanelView();
    }
    
    
    private synchronized void mouseClickedOnPanel(int xDisplay, int yDisplay) {
        int ySize = yRectSize + yGap;
        int yFirst = yPositionBottom - (topIndex - bottomIndex) * ySize;
        int yDiff = yDisplay - yFirst;
        int index = topIndex - yDiff/ySize;
        if (selectedInstance == null || xDisplay < margin || yDisplay < yFirst
                || xDisplay + margin >= panel.getWidth()
                || yDisplay + margin >= panel.getHeight()
                || yDiff % ySize >= yRectSize
                || index==selectedIndex) {
            return;
        }
        selectedInstance = nodes.get(index).getInstance();
        selectedIndex = index;
        updatePanelView();
    }
    
    private synchronized void jump(int mode, boolean up) {
        int index;
        if (selectedInstance == null) {
            return;
        }
        if (mode == 2) {
            index = up ? nodesSize - 1 : 0;
        } else {
            int diff = mode == 1 ? jumpLength : 1;
            index = up ? Math.min(nodesSize - 1, selectedIndex + diff)
                    : Math.max(0, selectedIndex - diff);
        }
        if (index == selectedIndex) {
            return;
        }
        selectedInstance = nodes.get(index).getInstance();
        selectedIndex = index;
        updatePanelView();
    }
    
    private synchronized void updatePanelView() {
        int h = panel.getHeight();
        int w = panel.getWidth();
        if (selectedInstance == null) {
            emptyLabel.setSize(w, emptyLabel.getPreferredSize().height);
            return;
        }
        xRectSize = w - 2 * margin;
        yPositionBottom = h - margin - yRectSize;
        
        int nodesMax = (h - 2 * margin + yGap) / (yRectSize + yGap);
        int bottom = Math.min(selectedIndex, nodesMax/2);
        bottomIndex = selectedIndex - bottom;
        int top = Math.min(nodesSize-selectedIndex, nodesMax-bottom)-1;
        topIndex = selectedIndex + top;
        bottomIndex = Math.max(0, bottomIndex - (nodesMax - (top + bottom + 1)));
        
        for (int y=0, index=bottomIndex; index<=topIndex; y++, index++) {
            nodes.get(index).updateLabelStr();
        }
        
        panel.repaint();
    }
    
    public synchronized void paintStack(Graphics g) {
        if (selectedInstance == null) {
            return;
        }
        
        g.setFont(nodeLabelFont);
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    textAntiAliasingHint);
        }
        
        for (int y=0, index=bottomIndex; index<=topIndex; y++, index++) {
            nodes.get(index).paint(g, y);
        }
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Stack Node">
    private class StackNode {
        private Instance instance;
        private StringCreator strCreator;
        private String labelStr;
        
        public StackNode(Instance instance) {
            this.instance = instance;
            strCreator = instance.getFunction().getLabelCreator(instance);
            strCreator.setFontMetrics(nodeLabelFontMetrics); 
        }
        
        public Instance getInstance() {
            return instance;
        }
        
        public void updateLabelStr() {
            labelStr = strCreator.getString(xRectSize);
        }
        
        public void paint(Graphics g, int yPosition) {
            int y = yPositionBottom - yPosition*(yRectSize + yGap);
            
            if ( markCurrent && instance==currentInstance) {
                if ( currentOnStack ) {
                    g.setColor(colorNewInstance);
                } else {
                    g.setColor(colorRemovedInstance);
                }
            } else {
                g.setColor(colorOnStackInstance);
            }
            g.fillRect(margin, y, xRectSize, yRectSize);
            
            g.setColor(Color.BLACK);
            if ( instance==selectedInstance ) {
                g.drawRect(margin-1, y-1, xRectSize+1, yRectSize+1);
                g.drawRect(margin-2, y-2, xRectSize+3, yRectSize+3);
                g.drawRect(margin-3, y-3, xRectSize+5, yRectSize+5);
            }
            g.drawString(labelStr, margin, y + nodeLabelFontAscent);
        } 
        
        public void write(FileWriter writer) throws IOException {
            writer.write(labelStr + System.getProperty("line.separator"));
        }
    }
    //</editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Stos wywołań";
        public static final String emptyStack = "Stos pusty";
        public static final String options = "Opcje";
        public static final String jumpLength = "Długość skoku";
        
        public static final String observe = "Obserwuj";
        public static final String observeTopStack = "Szczyt stosu";
        public static final String observeSelected = "Wybraną instację";
        
        public static final String showButtons = "Pokaż przyciski";
        public static final String save = "Zapisz";
        public static final String saveAsPictureDots = "Zapisz jako obrazek...";
        public static final String saveAsTextDots = "Zapisz jako text...";
        
        public static final String up1ToolTip = "Skocz w górę o jedną pozycję (W, ↑)";
        public static final String up2ToolTip = "Skocz w górę o wybraną długość (Ctrl+W, Ctrl+↑)";
        public static final String up3ToolTip = "Skocz na szczyt stosu (Shift+W, Shift+↑)";
        
        public static final String down1ToolTip = "Skocz w dół o jedną pozycję (S, ↓)";
        public static final String down2ToolTip = "Skocz w dół o wybraną długość (Ctrl+S, Ctrl+↓)";
        public static final String down3ToolTip = "Skocz na dno stosu (Shift+S, Shift+↓)";
    }
    // </editor-fold>
    
}
