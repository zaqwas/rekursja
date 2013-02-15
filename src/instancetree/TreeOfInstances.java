package instancetree;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import interpreter.Instance;
import java.awt.Color;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeMap;
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
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;
import mainclass.MainClass;
import stringcreator.StringCreator;
import syntax.function.Function;
//</editor-fold>

public class TreeOfInstances {
    
    private final static String iconDir = "/instancetree/icons/";
    
    //<editor-fold defaultstate="collapsed" desc="Components">
    private JInternalFrame frame;
    
    private JPanel panel;
    private JLabel label;
    
    private JButton up1Button;
    private JButton up2Button;
    private JButton up3Button;
    private JButton jumpCurrentButton;
    
    private JButton left1Button;
    private JButton left2Button;
    private JButton left3Button;
    private JToggleButton leftJumpModeButton;
    
    private JButton right1Button;
    private JButton right2Button;
    private JButton right3Button;
    private JToggleButton rightJumpModeButton;
    
    private JButton down1Button;
    private JButton down2Button;
    private JButton down3Button;
    private JToggleButton downJumpModeButton;
    
    private JMenuBar menuBar;
    private JMenu optionsMenu;
    private JMenu observeMenu;
    private JRadioButtonMenuItem observeCurrentMenuItem;
    private JRadioButtonMenuItem observeSelectedMenuItem;
    private JMenu jumpLengthMenu;
    private JMenu jumpLengthHorizontalMenu;
    private JRadioButtonMenuItem jumpLengthHorizontalMenuItems[];
    private JMenu jumpLengthVectricalMenu;
    private JRadioButtonMenuItem jumpLengthVectricalMenuItems[];
    private JCheckBoxMenuItem showButtonsMenuItem;
    private JMenuItem saveAsPictureMenuItem;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private MainClass mainClass;
    
    private Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private FontMetrics statusLabelFontMetrics;
    
    private Font treeNodeFont = new java.awt.Font(Font.MONOSPACED, Font.PLAIN, 12);
    public int xShiftNodeLabel = 1, yShiftNodeLabel = 9;
    public int maxNodeLabelLength = 2;
    
    public int xRectSize = 16, yRectSize = 10;
    public int xSpace = 10, ySpace = 30, margin=4;
    
    //TODO popraw to!!!
    public void setTreeNodeMaxLetters(int maxLength) {
        maxNodeLabelLength = maxLength;
        xRectSize = 7*maxLength+2;
        xSpace = (xRectSize+5)/2;
    }
    
    private int maxNodesX, maxNodesY;
    private int displayXShift, displayYShift;
    private int displayXSize, displayYSize;
    
    private ArrayList<ArrayList<TreeNode>> nodesXY = new ArrayList<ArrayList<TreeNode>>();
    private TreeMap<Function, String> functionNumberMap = new TreeMap<Function, String>();

    private Instance mainInstance;
    private Instance currentInstance;
    private Instance selectedInstance;
    
    private TreeNode mainNode;
    private TreeNode currentNode;
    private TreeNode selectedNode;
    
    private boolean markCurrent;
    
    private boolean observeCurrent = true;
    private boolean jumpHorizontalModeOnlyChildren;
    private boolean jumpDownModeLeftChild;
    
    private int jumpLenghtsArray[] = {2, 5, 10, 20, 50};
    private int horizontalJumpLength = jumpLenghtsArray[0];
    private int vectricalJumpLength = jumpLenghtsArray[0];
    
    private Color colorNewInstance = new Color(159, 255, 255);
    private Color colorRemovedInstance = new Color(255, 255, 159);
    private Color colorOnStackInstance = new Color(159, 255, 159);
    private Color colorOutOfStackInstance = new Color(255, 159, 159);
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public TreeOfInstances(MainClass mainClass) {
        this.mainClass = mainClass;
        frame = new JInternalFrame(Lang.frameTitle);
        frame.setResizable(true);

        //<editor-fold defaultstate="collapsed" desc="Init label">
        label = new JLabel(Lang.emptyTree);
        label.setFont(labelFont);
        label.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLabel();
            }

        });
        statusLabelFontMetrics = label.getFontMetrics(labelFont);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init buttons">
        Insets noMargin = new Insets(0, 0, 0, 0);

        ImageIcon icon = new ImageIcon(getClass().getResource(iconDir + "up1.png"));
        up1Button = new JButton(icon);
        up1Button.setActionCommand("U\0");
        up1Button.setMargin(noMargin);
        up1Button.setFocusable(false);
        up1Button.setToolTipText(Lang.up1ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "up2.png"));
        up2Button = new JButton(icon);
        up2Button.setActionCommand("U\1");
        up2Button.setMargin(noMargin);
        up2Button.setFocusable(false);
        up2Button.setToolTipText(Lang.up2ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "up3.png"));
        up3Button = new JButton(icon);
        up3Button.setActionCommand("U\2");
        up3Button.setMargin(noMargin);
        up3Button.setFocusable(false);
        up3Button.setToolTipText(Lang.up3ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "jumpCurrent.png"));
        jumpCurrentButton = new JButton(icon);
        jumpCurrentButton.setActionCommand("C");
        jumpCurrentButton.setMargin(noMargin);
        jumpCurrentButton.setFocusable(false);
        jumpCurrentButton.setToolTipText(Lang.jumpCurrentToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "left1.png"));
        left1Button = new JButton(icon);
        left1Button.setActionCommand("L\0");
        left1Button.setMargin(noMargin);
        left1Button.setFocusable(false);
        left1Button.setToolTipText(Lang.left1ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "left2.png"));
        left2Button = new JButton(icon);
        left2Button.setActionCommand("L\1");
        left2Button.setMargin(noMargin);
        left2Button.setFocusable(false);
        left2Button.setToolTipText(Lang.left2ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "left3.png"));
        left3Button = new JButton(icon);
        left3Button.setActionCommand("L\2");
        left3Button.setMargin(noMargin);
        left3Button.setFocusable(false);
        left3Button.setToolTipText(Lang.left3ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "leftJumpMode.png"));
        leftJumpModeButton = new JToggleButton(icon);
        leftJumpModeButton.setActionCommand("H");
        leftJumpModeButton.setMargin(noMargin);
        leftJumpModeButton.setFocusable(false);
        leftJumpModeButton.setToolTipText(Lang.horizontalJumpModeToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "right1.png"));
        right1Button = new JButton(icon);
        right1Button.setActionCommand("R\0");
        right1Button.setMargin(noMargin);
        right1Button.setFocusable(false);
        right1Button.setToolTipText(Lang.right1ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "right2.png"));
        right2Button = new JButton(icon);
        right2Button.setActionCommand("R\1");
        right2Button.setMargin(noMargin);
        right2Button.setFocusable(false);
        right2Button.setToolTipText(Lang.right2ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "right3.png"));
        right3Button = new JButton(icon);
        right3Button.setActionCommand("R\2");
        right3Button.setMargin(noMargin);
        right3Button.setFocusable(false);
        right3Button.setToolTipText(Lang.right3ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "rightJumpMode.png"));
        rightJumpModeButton = new JToggleButton(icon);
        rightJumpModeButton.setActionCommand("H");
        rightJumpModeButton.setMargin(noMargin);
        rightJumpModeButton.setFocusable(false);
        rightJumpModeButton.setToolTipText(Lang.horizontalJumpModeToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "down1.png"));
        down1Button = new JButton(icon);
        down1Button.setActionCommand("D\0");
        down1Button.setMargin(noMargin);
        down1Button.setFocusable(false);
        down1Button.setToolTipText(Lang.down1ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "down2.png"));
        down2Button = new JButton(icon);
        down2Button.setActionCommand("D\1");
        down2Button.setMargin(noMargin);
        down2Button.setFocusable(false);
        down2Button.setToolTipText(Lang.down2ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "down3.png"));
        down3Button = new JButton(icon);
        down3Button.setActionCommand("D\2");
        down3Button.setMargin(noMargin);
        down3Button.setFocusable(false);
        down3Button.setToolTipText(Lang.down3ToolTip);

        icon = new ImageIcon(getClass().getResource(iconDir + "downJumpMode.png"));
        downJumpModeButton = new JToggleButton(icon);
        downJumpModeButton.setActionCommand("V");
        downJumpModeButton.setMargin(noMargin);
        downJumpModeButton.setFocusable(false);
        downJumpModeButton.setToolTipText(Lang.downJumpModeToolTip);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init Menu">
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        observeMenu = new JMenu(Lang.observe);
        menuBar.add(observeMenu);

        ButtonGroup buttonGroup = new ButtonGroup();
        observeCurrentMenuItem = new JRadioButtonMenuItem(Lang.observeCurrentInstance);
        observeCurrentMenuItem.setActionCommand("c");
        observeCurrentMenuItem.setSelected(true);
        buttonGroup.add(observeCurrentMenuItem);
        observeMenu.add(observeCurrentMenuItem);

        observeSelectedMenuItem = new JRadioButtonMenuItem(Lang.observeSelectedInstance);
        observeSelectedMenuItem.setActionCommand("s");
        buttonGroup.add(observeSelectedMenuItem);
        observeMenu.add(observeSelectedMenuItem);

        jumpLengthMenu = new JMenu(Lang.jumpLength);
        menuBar.add(jumpLengthMenu);

        jumpLengthHorizontalMenu = new JMenu(Lang.jumpLengthHorizontal);
        jumpLengthMenu.add(jumpLengthHorizontalMenu);

        buttonGroup = new ButtonGroup();
        jumpLengthHorizontalMenuItems = new JRadioButtonMenuItem[jumpLenghtsArray.length];
        for (int i = 0; i < jumpLenghtsArray.length; i++) {
            jumpLengthHorizontalMenuItems[i] = new JRadioButtonMenuItem(Integer.toString(jumpLenghtsArray[i]));
            jumpLengthHorizontalMenuItems[i].setActionCommand("h" + ((char) i));
            buttonGroup.add(jumpLengthHorizontalMenuItems[i]);
            jumpLengthHorizontalMenu.add(jumpLengthHorizontalMenuItems[i]);
        }
        jumpLengthHorizontalMenuItems[0].setSelected(true);

        jumpLengthVectricalMenu = new JMenu(Lang.jumpLengthVectrical);
        jumpLengthMenu.add(jumpLengthVectricalMenu);

        buttonGroup = new ButtonGroup();
        jumpLengthVectricalMenuItems = new JRadioButtonMenuItem[jumpLenghtsArray.length];
        for (int i = 0; i < jumpLenghtsArray.length; i++) {
            jumpLengthVectricalMenuItems[i] = new JRadioButtonMenuItem(Integer.toString(jumpLenghtsArray[i]));
            jumpLengthVectricalMenuItems[i].setActionCommand("v" + ((char) i));
            buttonGroup.add(jumpLengthVectricalMenuItems[i]);
            jumpLengthVectricalMenu.add(jumpLengthVectricalMenuItems[i]);
        }
        jumpLengthVectricalMenuItems[0].setSelected(true);

        optionsMenu = new JMenu(Lang.options);
        menuBar.add(optionsMenu);

        showButtonsMenuItem = new JCheckBoxMenuItem(Lang.showButtons);
        showButtonsMenuItem.setSelected(true);
        showButtonsMenuItem.setActionCommand("b");
        optionsMenu.add(showButtonsMenuItem);
        
        saveAsPictureMenuItem = new JMenuItem(Lang.saveAsPictureDots);
        saveAsPictureMenuItem.setActionCommand("p");
        optionsMenu.add(saveAsPictureMenuItem);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Button and Menu listeners">
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                char c = cmd.charAt(0);
                switch (c) {
                    case 'U':
                        jumpUp(cmd.charAt(1));
                        break;
                    case 'L':
                        jumpHorizontal(cmd.charAt(1), false);
                        break;
                    case 'R':
                        jumpHorizontal(cmd.charAt(1), true);
                        break;
                    case 'D':
                        jumpDown(cmd.charAt(1), jumpDownModeLeftChild);
                        break;
                    case 'H':
                        jumpHorizontalModeOnlyChildren = ((JToggleButton) e.getSource()).isSelected();
                        leftJumpModeButton.setSelected(jumpHorizontalModeOnlyChildren);
                        rightJumpModeButton.setSelected(jumpHorizontalModeOnlyChildren);
                        break;
                    case 'V':
                        jumpDownModeLeftChild = ((JToggleButton) e.getSource()).isSelected();
                        break;
                    case 'C':
                        jumpCurrentNode();
                        break;
                    case 'h':
                        horizontalJumpLength = jumpLenghtsArray[cmd.charAt(1)];
                    case 'v':
                        vectricalJumpLength = jumpLenghtsArray[cmd.charAt(1)];
                    case 'c':
                        observeCurrent = true;
                        break;
                    case 's':
                        observeCurrent = false;
                        break;
                    case 'b':
                        setVisibleButtons();
                        break;
                    case 'p':
                        saveAsPicture();
                        break;
                }
            }
        };

        up1Button.addActionListener(actionListener);
        up2Button.addActionListener(actionListener);
        up3Button.addActionListener(actionListener);
        jumpCurrentButton.addActionListener(actionListener);

        left1Button.addActionListener(actionListener);
        left2Button.addActionListener(actionListener);
        left3Button.addActionListener(actionListener);
        leftJumpModeButton.addActionListener(actionListener);

        right1Button.addActionListener(actionListener);
        right2Button.addActionListener(actionListener);
        right3Button.addActionListener(actionListener);
        rightJumpModeButton.addActionListener(actionListener);

        down1Button.addActionListener(actionListener);
        down2Button.addActionListener(actionListener);
        down3Button.addActionListener(actionListener);
        downJumpModeButton.addActionListener(actionListener);

        observeCurrentMenuItem.addActionListener(actionListener);
        observeSelectedMenuItem.addActionListener(actionListener);
        showButtonsMenuItem.addActionListener(actionListener);
        saveAsPictureMenuItem.addActionListener(actionListener);
        for (int i = 0; i < jumpLenghtsArray.length; i++) {
            jumpLengthHorizontalMenuItems[i].addActionListener(actionListener);
            jumpLengthVectricalMenuItems[i].addActionListener(actionListener);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Init panel">
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintTree(g);
            }
        };

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePanelView();
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int btn = e.getButton();
                mouseClickedOnPanel(e.getX(), e.getY(), btn != MouseEvent.BUTTON1);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMovedOnPanel(e.getX(), e.getY());
            }
        };
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        panel.setFocusable(true);
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
                        jumpUp(mode);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        jumpHorizontal(mode, false);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        jumpHorizontal(mode, true);
                        break;
                    case KeyEvent.VK_DOWN:
                        jumpDown(mode, jumpDownModeLeftChild);
                        break;
                    case KeyEvent.VK_Z:
                        jumpDown(mode, true);
                        break;
                    case KeyEvent.VK_X:
                        jumpDown(mode, false);
                        break;
                    case KeyEvent.VK_HOME:
                        jumpCurrentNode();
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_END:
                        jumpHorizontalModeOnlyChildren = !jumpHorizontalModeOnlyChildren;
                        leftJumpModeButton.setSelected(jumpHorizontalModeOnlyChildren);
                        rightJumpModeButton.setSelected(jumpHorizontalModeOnlyChildren);
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        jumpDownModeLeftChild = !jumpDownModeLeftChild;
                        downJumpModeButton.setSelected(jumpDownModeLeftChild);
                        break;
                    case KeyEvent.VK_DELETE:
                        showButtonsMenuItem.doClick();
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_ENTER:
                        if ( selectedInstance != null ) {
                            TreeOfInstances.this.mainClass.getInstanceFrame().select(selectedInstance);
                        }
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Layout">
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addGap(2)
                .addComponent(label, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addGap(2))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(left1Button)
                    .addComponent(left2Button)
                    .addComponent(left3Button)
                    .addComponent(leftJumpModeButton))
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(up1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(up2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(up3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(jumpCurrentButton, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addComponent(panel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(down1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(down2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(down3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(downJumpModeButton, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup()
                    .addComponent(right1Button)
                    .addComponent(right2Button)
                    .addComponent(right3Button)
                    .addComponent(rightJumpModeButton)))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGap(1)
            .addComponent(label)
            .addGap(1)
            .addGroup(layout.createParallelGroup()
                .addComponent(up1Button)
                .addComponent(up2Button)
                .addComponent(up3Button)
                .addComponent(jumpCurrentButton))
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(left1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(left2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(left3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(leftJumpModeButton, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addComponent(panel)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(right1Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(right2Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(right3Button, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(rightJumpModeButton, 24, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup()
                .addComponent(down1Button)
                .addComponent(down2Button)
                .addComponent(down3Button)
                .addComponent(downJumpModeButton))
        );
        frame.setLayout(layout);
        //</editor-fold>
        
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
        functionNumberMap.clear();
        this.mainInstance = mainInstance;
        currentInstance = mainInstance;
        selectedInstance = mainInstance;
        markCurrent = false;
        updateTree();
    }

    public synchronized void update(Instance currentInstance) {
        this.currentInstance = currentInstance;
        if ( observeCurrent ) {
            selectedInstance = currentInstance;
        }
        updateTree();
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
        mainInstance = null;
        selectedInstance = null;
        currentInstance = null;

        mainNode = null;
        selectedNode = null;
        currentNode = null;

        functionNumberMap.clear();
        nodesXY.clear();

        panel.repaint();
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void setVisibleButtons() {
        boolean shown = showButtonsMenuItem.isSelected();
        up1Button.setVisible(shown);
        up2Button.setVisible(shown);
        up3Button.setVisible(shown);
        jumpCurrentButton.setVisible(shown);

        left1Button.setVisible(shown);
        left2Button.setVisible(shown);
        left3Button.setVisible(shown);
        leftJumpModeButton.setVisible(shown);

        right1Button.setVisible(shown);
        right2Button.setVisible(shown);
        right3Button.setVisible(shown);
        rightJumpModeButton.setVisible(shown);

        down1Button.setVisible(shown);
        down2Button.setVisible(shown);
        down3Button.setVisible(shown);
        downJumpModeButton.setVisible(shown);
    }
    
    private void saveAsPicture() {
        JFileChooser chooser = new JFileChooser(mainClass.getSaveReportDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter( new FileFilter() {
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
            paintTree(image.getGraphics());
        }
        try {
            ImageIO.write(image, "png", file);
            mainClass.setSaveReportFile(file);
        } catch (Exception ex) {
        }
    }


    private void setNewSelectedNode(TreeNode node) {
        if (node == selectedNode) {
            return;
        }
        node.setLabelString();
        selectedInstance = node.getInstance();
        selectedNode = node;
        updatePanelView();
    }

    private TreeNode localizeNode(int xDisplay, int yDisplay) {
        if (selectedNode == null || xDisplay < margin || yDisplay < margin
                || xDisplay + margin >= panel.getWidth()
                || yDisplay + margin >= panel.getHeight()
                || (yDisplay - margin) % ySpace >= yRectSize) {
            return null;
        }

        int xPos = (xDisplay + displayXShift * xSpace - margin) / xSpace - 1;
        int yPos = (yDisplay + displayYShift * ySpace - margin) / ySpace;
        if (xPos >= maxNodesX || yPos >= maxNodesY) {
            return null;
        }
        
        ArrayList<TreeNode> nodesX = nodesXY.get(yPos);
        int index = binarySearchNodesX(nodesX, xPos);
        if (index >= nodesX.size()) {
            return null;
        }
        TreeNode node = nodesX.get(index);
        int nodeDisplayX = node.getDisplayX();
        return nodeDisplayX <= xDisplay && xDisplay < nodeDisplayX + xRectSize ? node : null;
    }

    private int binarySearchNodesX(ArrayList<TreeNode> nodesX, int xPosition) {
        int index = 0, indexR = nodesX.size();
        while (index != indexR) {
            int indexM = (indexR + index) / 2;
            int x = nodesX.get(indexM).getX();
            if (x == xPosition) {
                return indexM;
            } else if (x < xPosition) {
                index = indexM + 1;
            } else {
                indexR = indexM;
            }
        }
        return index;
    }

    
    private void updateTree() {
        mainNode = new TreeNode(mainInstance);

        TreeShape shape = new TreeShape();
        mainNode.makeTree(0, shape);

        nodesXY.clear();
        maxNodesX = shape.getMaxX();
        maxNodesY = shape.getMaxY();
        for (int y = 0; y < maxNodesY; y++) {
            nodesXY.add(new ArrayList<TreeNode>());
        }

        mainNode.setXPostion(0);

        updateLabel();
        updatePanelView();
    }
    
    
    private synchronized void jumpHorizontal(int mode, boolean jumpRight) {
        if (selectedNode == null) {
            return;
        }
        TreeNode node;
        if (jumpHorizontalModeOnlyChildren) {
            TreeNode parent = selectedNode.getParent();
            if (parent == null) {
                return;
            }
            if (mode == 2) {
                node = jumpRight ? parent.getRightChild() : parent.getLeftChild();
            } else {
                int index = selectedNode.getIndexParent();
                int diff = mode == 1 ? horizontalJumpLength : 1;
                index = jumpRight
                        ? Math.min(index + diff, parent.getChildrenSize() - 1)
                        : Math.max(index - diff, 0);
                node = parent.getChild(index);
            }
        } else {
            ArrayList<TreeNode> nodesX = nodesXY.get(selectedNode.getY());
            if (mode == 2) {
                node = nodesX.get(jumpRight ? nodesX.size() - 1 : 0);
            } else {
                int index = selectedNode.getIndexNodesX();
                int diff = mode == 1 ? horizontalJumpLength : 1;
                index = jumpRight
                        ? Math.min(index + diff, nodesX.size() - 1)
                        : Math.max(index - diff, 0);
                node = nodesX.get(index);
            }
        }
        setNewSelectedNode(node);
    }

    private synchronized void jumpUp(int mode) {
        if (selectedNode == null) {
            return;
        }
        TreeNode node;
        if (mode == 2) {
            node = mainNode;
        } else {
            int diff = mode == 1 ? vectricalJumpLength : 1;
            node = selectedNode;
            TreeNode parent;
            while (diff > 0 && (parent = node.getParent()) != null) {
                node = parent;
                diff--;
            }
        }
        setNewSelectedNode(node);
    }

    private synchronized void jumpDown(int mode, boolean jumpDownModeLeftChild) {
        if (selectedNode == null) {
            return;
        }
        int diff = mode == 2 ? Integer.MAX_VALUE
                : (mode == 1 ? vectricalJumpLength : 1);
        TreeNode node = selectedNode, child;
        while (diff > 0 && (child = jumpDownModeLeftChild
                ? node.getLeftChild() : node.getRightChild()) != null) {
            node = child;
            diff--;
        }
        setNewSelectedNode(node);
    }

    private synchronized void jumpCurrentNode() {
        if (currentNode == null) {
            return;
        }
        setNewSelectedNode(currentNode);
    }
    

    private synchronized void updatePanelView() {
        if (selectedNode == null) {
            return;
        }

        displayXSize = Math.min(maxNodesX, (panel.getWidth() - 2 * margin) / xSpace);
        displayXShift = selectedNode.xPosition - (displayXSize - 1) / 2;
        displayXShift -= Math.max(0, displayXShift + displayXSize - maxNodesX);
        displayXShift = Math.max(0, displayXShift);

        displayYSize = Math.min(maxNodesY, (panel.getHeight() - 2 * margin + ySpace - yRectSize) / ySpace);
        displayYShift = selectedNode.yPosition - (displayYSize - 1) / 2;
        displayYShift -= Math.max(0, displayYShift + displayYSize - maxNodesY);
        displayYShift = Math.max(0, displayYShift);

        panel.repaint();
    }

    private synchronized void updateLabel() {
        if (selectedNode == null) {
            return;
        }
        selectedNode.setLabelString();
    }

    private synchronized void paintTree(Graphics g) {
        if (selectedNode == null) {
            return;
        }
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        int xMax = displayXShift + displayXSize;
        int yMax = displayYShift + displayYSize;
        for (int y = displayYShift; y < yMax; y++) {
            ArrayList<TreeNode> nodesX = nodesXY.get(y);
            int index = binarySearchNodesX(nodesX, displayXShift);
            int size = nodesX.size();

            TreeNode node;
            while (index < size && (node = nodesX.get(index)).xPosition < xMax) {
                node.paint(g);
                index++;
            }
        }
    }

    private synchronized void mouseClickedOnPanel(int xDisplay, int yDisplay, boolean rightButton) {
        TreeNode node = localizeNode(xDisplay, yDisplay);
        if (node == null) {
            return;
        }
        if (rightButton) {
            mainClass.getInstanceFrame().select(node.instance);
        }
        setNewSelectedNode(node);
    }

    private synchronized void mouseMovedOnPanel(int xDisplay, int yDisplay) {
        TreeNode node = localizeNode(xDisplay, yDisplay);
        if (selectedNode == null) {
            return;
        }
        if (node == null) {
            selectedNode.setLabelString();
        } else {
            node.setLabelString();
        }
    }
    //</editor-fold>
     
    
    //<editor-fold defaultstate="collapsed" desc="Class TreeShape">
    private static class TreeShape {
        private LinkedList<Integer> shapeL = new LinkedList<Integer>();
        private LinkedList<Integer> shapeR = new LinkedList<Integer>();
        private int maxX = 1;
        
        public int getMaxY() {
            return shapeL.size();
        }
        
        public int getMaxX() {
            return maxX;
        }

        public void addHead(int xPosition) {
            shapeL.addFirst(xPosition);
            shapeR.addFirst(xPosition+2);
        }
        
        public ArrayList<Shift> join(TreeShape shape2, int number, 
                ArrayList<Integer> nrNodeShapeR) {
            ArrayList<Shift> shiftArray = new ArrayList<Shift>();
            ListIterator<Integer> itL1, itR1, itL2, itR2, itNrNode;
            
            int sh = Integer.MAX_VALUE, nr = -1;
            itR1 = shapeR.listIterator();
            itL2 = shape2.shapeL.listIterator();
            itNrNode = nrNodeShapeR.listIterator();
            
            while ( itR1.hasNext() && itL2.hasNext() ) {
                int sh2 = itL2.next() - itR1.next();
                int nr2 = itNrNode.next();
                if ( sh2<sh ) {
                    sh = sh2;
                    if ( nr2==nr ) {
                        shiftArray.get(shiftArray.size()-1).shift = sh;
                    } else {
                        nr = nr2;
                        shiftArray.add(new Shift(nr,sh));
                    }
                }
            }
            
            int shL = 0, shR = 0;
            if ( sh>0 ) {
                shL = sh;
            } else {
                shR = -sh;
            }
            
            itL1 = shapeL.listIterator();
            itR1 = shapeR.listIterator();
            itL2 = shape2.shapeL.listIterator();
            itR2 = shape2.shapeR.listIterator();
            itNrNode = nrNodeShapeR.listIterator();
            
            while ( itL1.hasNext() && itR2.hasNext() ) {
                itL1.set( itL1.next()+shL );
                itR1.next();
                itL2.next();
                itR2.set( itR2.next()+shR );
                
                itNrNode.next();
                itNrNode.set(number);
            }
            if ( itL1.hasNext() ) {
                while (itL1.hasNext()) {
                    itL1.set(itL1.next() + shL);
                    shape2.shapeR.addLast(itR1.next() + shL);
                }
            } else {
                while (itR2.hasNext()) {
                    itR2.set( itR2.next() + shR );
                    shapeL.addLast(itL2.next() + shR);
                    nrNodeShapeR.add(number);
                }
            }
            
            LinkedList<Integer> list = shapeR;
            shapeR = shape2.shapeR;
            shape2.shapeR = list;
            
            shape2.shapeL.clear();
            shape2.shapeR.clear();
            
            maxX = Math.max( maxX+shL , shape2.maxX+shR );
            shape2.maxX = 1;
            
            return shiftArray;
        }
        
        public static class Shift {
            public int nrNode;
            public int shift;
            
            public Shift(int nrNode, int shift) {
                this.nrNode = nrNode;
                this.shift = shift;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Class TreeNode">
    private class TreeNode {
        
        //<editor-fold defaultstate="collapsed" desc="Variables">
        private Instance instance;
        private boolean onStack;
        
        private TreeNode parent;
        private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
        
        private int indexNodesX;
        
        private int yPosition, xPosition, xShift;
        
        private StringCreator strCreator;
        private String nodeLabel;
        //</editor-fold>
        
        public TreeNode(Instance instance) {
            this.instance = instance;
            onStack = instance.isOnStack();
            strCreator = instance.getInstanceStringCreator("","");
            strCreator.setFontMetrics(statusLabelFontMetrics);

            nodeLabel = instance.getFunction().getTreeNodeLabel(maxNodeLabelLength, instance);
        }
        
        //<editor-fold defaultstate="collapsed" desc="Get metods">
        public Instance getInstance() {
            return instance;
        }

        public int getX() {
            return xPosition;
        }
        public int getY() {
            return yPosition;
        }

        public int getIndexParent() {
            return instance.getIndexParentInst();
        }
        public int getIndexNodesX() {
            return indexNodesX;
        }

        public int getDisplayX() {
            return (xPosition - displayXShift) * xSpace + margin;
        }
        public int getDisplayY() {
            return (yPosition - displayYShift) * ySpace + margin;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Neighbour nodes">
        public TreeNode getParent() {
            return parent;
        }

        public TreeNode getLeftBrother() {
            int index = getIndexParent();
            if (parent == null || index == 0) {
                return null;
            }
            return parent.children.get(index - 1);
        }
        public TreeNode getRightBrother() {
            int index = getIndexParent();
            if (parent == null || index + 1 == parent.children.size()) {
                return null;
            }
            return parent.children.get(index + 1);
        }

        public int getChildrenSize() {
            return children.size();
        }

        public TreeNode getChild(int number) {
            return children.get(number);
        }

        public TreeNode getRightChild() {
            if (children.isEmpty()) {
                return null;
            }
            return children.get(children.size() - 1);
        }
        public TreeNode getLeftChild() {
            if (children.isEmpty()) {
                return null;
            }
            return children.get(0);
        }
        //</editor-fold>

        public void makeTree(int yPosition, TreeShape shape) {
            this.yPosition = yPosition;
            
            if (instance == currentInstance) {
                currentNode = this;
            }
            if (instance == selectedInstance) {
                selectedNode = this;
            }

            if (!instance.hasChildrenInstances()) {
                xPosition = 0;
                shape.addHead(0);
                return;
            }

            ArrayList<Integer> nrNodeShapeR = null;
            TreeShape shapeR = null;
            for (int i = 0, size = instance.getChildrenInstancesSize(); i < size; i++) {
                Instance inst = instance.getChildInstance(i);
                TreeNode node = new TreeNode(inst);
                node.parent = this;
                children.add(node);

                if (i == 0) {
                    node.makeTree(yPosition + 1, shape);
                    continue;
                }
                if (i == 1) {
                    nrNodeShapeR = new ArrayList<Integer>();
                    for (int j = 0, s = shape.getMaxY(); j < s; j++) {
                        nrNodeShapeR.add(0);
                    }
                    shapeR = new TreeShape();
                }
                node.makeTree(yPosition + 1, shapeR);
                ArrayList<TreeShape.Shift> shiftArray = shape.join(shapeR, i, nrNodeShapeR);

                //<editor-fold defaultstate="collapsed" desc="Compute shift">
                TreeShape.Shift shift = shiftArray.get(shiftArray.size() - 1);
                int sh = shift.shift;
                int nr = shift.nrNode + 1;
                if (sh <= 0) {
                    children.get(i).xShift -= sh;
                } else {
                    for (int j = 0; j < nr; j++) {
                        children.get(j).xShift += sh;
                    }
                }
                int shTotal = 0;
                for (int j = shiftArray.size() - 2; j >= 0; j--) {
                    shift = shiftArray.get(0);
                    int nodes = shift.nrNode - nr + 1;
                    int shMax = shift.shift - sh;
                    shMax -= (shMax + nodes) / (nodes + 1);

                    for (int k = 1; k <= j; k++) {
                        shift = shiftArray.get(k);
                        int nodes2 = shift.nrNode - nr + 1;
                        int shMax2 = (shMax / nodes) * nodes2;
                        shMax2 += Math.max((shMax % nodes) - nodes2, 0);
                        shMax2 = Math.min(shift.shift - sh, shMax2);
                        nodes = nodes2;
                        shMax = shMax2;
                    }

                    sh += shMax;
                    while (nodes > 0) {
                        int div = shMax / nodes;
                        shTotal += div;
                        children.get(nr).xShift += shTotal;
                        shMax -= div;
                        nodes--;
                        nr++;
                    }
                }
                //</editor-fold>

            }

            int size = children.size(), s = 0;
            for (TreeNode node : children) {
                node.xPosition += node.xShift;
                s += node.xPosition;
            }
            xPosition = s / size;
            shape.addHead(xPosition);
        }

        public void setXPostion(int sh) {
            indexNodesX = nodesXY.get(yPosition).size();
            nodesXY.get(yPosition).add(this);

            xPosition += sh;
            sh += xShift;
            xShift = 0;
            for (TreeNode node : children) {
                node.setXPostion(sh);
            }
        }

        public void paint(Graphics g) {
            int x = getDisplayX();
            int y = getDisplayY();
            
            if ( markCurrent && this==currentNode ) {
                if (onStack) {
                    g.setColor(colorNewInstance);
                } else {
                    g.setColor(colorRemovedInstance);
                }
            } else {
                if (onStack) {
                    g.setColor(colorOnStackInstance);
                } else {
                    g.setColor(colorOutOfStackInstance);
                }
            }
            g.fillRect(x, y, xRectSize, yRectSize);

            g.setColor(Color.BLACK);
            if ( this==selectedNode ) {
            g.drawRect(x-1, y-1, xRectSize+1, yRectSize+1);
            g.drawRect(x-2, y-2, xRectSize+3, yRectSize+3);
            }
            
            g.setFont(treeNodeFont);
            g.drawString(nodeLabel, x + xShiftNodeLabel, y + yShiftNodeLabel);

            int xRectSize2 = xRectSize / 2;
            int x2 = x + xRectSize2;
            int y2 = y + yRectSize;
            if (parent != null) {
                g.drawLine(parent.getDisplayX() + xRectSize2,
                        parent.getDisplayY() + yRectSize, x2, y - 1);
            }
            for (TreeNode n : children) {
                g.drawLine(x2, y2,
                        n.getDisplayX() + xRectSize2, n.getDisplayY() - 1);
            }
        }

        public void setLabelString() {
            label.setText(strCreator.getString(label.getWidth()));
        }

    }
    //</editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Drzewo wywołań";
        public static final String emptyTree = "Drzewo jest puste";
        public static final String options = "Opcje";
        
        public static final String observe = "Obserwuj";
        public static final String observeCurrentInstance = "Obecnie wykonywaną instancję";
        public static final String observeSelectedInstance = "Wybraną instację";
        
        public static final String jumpLength = "Długość skoku";
        public static final String jumpLengthHorizontal = "W poziomie";
        public static final String jumpLengthVectrical = "W pionie";
        
        public static final String showButtons = "Pokaż przyciski";
        public static final String save = "Zapisz";
        public static final String saveAsPictureDots = "Zapisz jako obrazek...";
        
        public static final String up1ToolTip = "Skocz w górę o jedną pozycję (W, ↑)";
        public static final String up2ToolTip = "Skocz w górę o wybraną długość (Ctrl+W, Ctrl+↑)";
        public static final String up3ToolTip = "Skocz do korzenia drzewa (Shift+W, Shift+↑)";
        
        public static final String down1ToolTip = "Skocz w dół o jedną pozycję (↓)";
        public static final String down2ToolTip = "Skocz w dół o wybraną długość (Ctrl+↓)";
        public static final String down3ToolTip = "Skocz w dół najbardziej jak się da (Shift+↓)";
        
        public static final String left1ToolTip = "Skocz w lewo o jedną pozycję (A, ←)";
        public static final String left2ToolTip = "Skocz w lewo o wybraną długość (Ctrl+A, Ctrl+←)";
        public static final String left3ToolTip = "Skocz w lewo najbardziej jak się da (Shift+A, Shift+←)";
        
        public static final String right1ToolTip = "Skocz w prawo o jedną pozycję (D, →)";
        public static final String right2ToolTip = "Skocz w prawo o wybraną długość (Ctrl+D, Ctrl+→)";
        public static final String right3ToolTip = "Skocz w prawo najbardziej jak się da (Shift+D, Shift+→)";
        
        public static final String jumpCurrentToolTip = "Skocz do instancji będącej na szczycie stosu (Home)";
        public static final String horizontalJumpModeToolTip = "Pozwalaj/nie pozwalaj przeskakiwać na kuzyna (S, End)";
        public static final String downJumpModeToolTip = "Skacząc w dół wybieraj prawego/lewego syna (Page Down)";
    }
    // </editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Debug functions">
    private void debug_print(TreeNode node) {
        System.out.print(node.children.size());
        System.out.print(" ");
        System.out.print(node.xPosition);
        System.out.print(" ");
        System.out.print(node.yPosition);
        System.out.print("\n");
        
        for (TreeNode nd : node.children) {
            debug_print(nd);
        }
    }
    
    private int debug_count(TreeNode node) {
        int count = 1;
        for (TreeNode tn : node.children) {
            count += debug_count(tn);
        }
        return count;
    }
    
    private void debug_addInstance(Instance instance, int n) {
//        if (n == 8) {
//            return;
//        }
//        instance.add();
//        instance.add();
//        instance.add();
//        debug_addInstance(instance.childrenInst.get(0), n + 1);
//        debug_addInstance(instance.childrenInst.get(2), n + 1);

        
//        if (n <= 0) {
//            return;
//        }
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        instance.add();
//        debug_addInstance(instance.childrenInst.get(0), n - 1);
//        debug_addInstance(instance.childrenInst.get(2), n - 1);
//        debug_addInstance(instance.childrenInst.get(4), n - 1);
//        debug_addInstance(instance.childrenInst.get(6), n - 1);
        
    }
    
    private void debug_catalan(Instance instance, int n) {
        if (n <= 0) {
            
        } else {
            for (int i = 0, j = n - 1, index = 0; i <= j; i++, j--) {
                instance.add();
                debug_catalan(instance.childrenInst.get(index), i);
                index++;
                instance.add();
                debug_catalan(instance.childrenInst.get(index), j);
                index++;
            }
//            for (int i=0, index=0; i<=n-1; i++) {
//                instance.add();
//                debug_addInstance(instance.childrenInst.get(index), n-1-i);
//                index++;
//                instance.add();
//                debug_addInstance(instance.childrenInst.get(index), i);
//                index++;
//            }
        }
    }
    
    private void debug_t_instance(Instance instance, int h, int w) {
        if ( h==0 ) {
            for (int i=0; i<w; i++) {
                instance.add();
            }
            return;
        }
        instance.add();
        debug_t_instance(instance.childrenInst.get(0), h-1, w);
    }
    private void debug_tree(Instance main) {
//        main.add();//t
//        main.add();
//        main.add();
//        main.add();//t
//        main.add();
//        main.add();
//        main.add();//t
//        main.add();
//        main.add();
//        main.add();//
//        
//        debug_t_instance(main.getChild(0),3,51);
//        debug_t_instance(main.getChild(3),2,19);
//        debug_t_instance(main.getChild(6),1,7);
//        debug_t_instance(main.getChild(9),4,0);
        
        main.add();//t
        main.add();
        main.add();
        main.add();//t
        main.add();
        main.add();
        main.add();//t
        
        debug_t_instance(main.getChildInstance(0),2,21);
        debug_t_instance(main.getChildInstance(3),1,7);
        debug_t_instance(main.getChildInstance(6),3,0);
        
//        main.add();//t
//        main.add();
//        main.add();
//        main.add();//t
//        
//        debug_t_instance(main.getChild(0),1,9);
//        debug_t_instance(main.getChild(3),2,0);
    }
    
    private void debug_addTree() {
        mainInstance = new Instance();
        selectedInstance = mainInstance;
        currentInstance = mainInstance;

//        debug_addInstance(mainInstance, 3);
        debug_catalan(mainInstance, 10);
    }
    //</editor-fold>
    
}