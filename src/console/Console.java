package console;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import mainclass.MainClass;
import syntax.function.FunctionWrite;

public class Console {
    
    private static final int maxLength = 1000000;
    
    private JInternalFrame frame;
    private MainClass mainClass;
    
    private JTextArea textArea;
    private JRadioButtonMenuItem clearTextMenuItem;
    private JRadioButtonMenuItem keepTextMenuItem;
    
    private FileFilter textFileFilter;
    
    
    public Console(MainClass mainClass) {
        this.mainClass = mainClass;
        FunctionWrite.setConsole(this);
        
        frame = new JInternalFrame(Lang.frameTitle);
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        frame.setContentPane(scrollPane);
        frame.setResizable(true);
        frame.setPreferredSize(new Dimension(300, 200));
        
        //<editor-fold defaultstate="collapsed" desc="Menu bar">
        JMenuBar menuBar = new JMenuBar();
        
        JMenu clearingMenu = new JMenu(Lang.clearingMenu);
        menuBar.add(clearingMenu);
        clearTextMenuItem = new JRadioButtonMenuItem(Lang.clearMenuItem);
        clearingMenu.add(clearTextMenuItem);
        keepTextMenuItem = new JRadioButtonMenuItem(Lang.keepMenuItem);
        clearingMenu.add(keepTextMenuItem);
        
        ButtonGroup group = new ButtonGroup();
        group.add(clearTextMenuItem);
        group.add(keepTextMenuItem);
        clearTextMenuItem.setSelected(true);
        
        JMenu optionsMenu = new JMenu(Lang.optionsMenu);
        menuBar.add(optionsMenu);
        JMenuItem saveMenuItem = new JMenuItem(Lang.saveMenuItem);
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFileAction();
            }
        });
        optionsMenu.add(saveMenuItem);
        
        frame.setJMenuBar(menuBar);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Popup menu">
        JMenuItem clearMenu = new JMenuItem(Lang.clear);
        clearMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(clearMenu);
        
        textArea.add(popupMenu);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="textFileFilter">
        textFileFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return Lang.textFileDescription;
            }
        };
        //</editor-fold>
    }
    
    //<editor-fold defaultstate="collapsed" desc="saveToFileAction">
    private void saveToFileAction() {
        JFileChooser chooser = new JFileChooser(mainClass.getSaveReportDirectory());
        chooser.setApproveButtonText(Lang.save);
        chooser.setDialogTitle(Lang.save);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(textFileFilter);
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if ( !file.getName().endsWith(".txt") ) {
                file = new File(file.getParentFile(), file.getName()+".txt");
            }
            try {    
                FileOutputStream fileStream = new FileOutputStream(file);
                fileStream.write(textArea.getText().getBytes("UTF-8"));
                fileStream.close();
                mainClass.setSaveReportFile(file);
            } catch (IOException ex) {}
        }
    }
    //</editor-fold>
    
    
    public JInternalFrame getFrame() {
        return frame;
    }

    public void saveSettnings(DataOutputStream stream) throws IOException {
        stream.writeBoolean(clearTextMenuItem.isSelected());
    }

    public void loadSettnings(DataInputStream stream) throws IOException {
        boolean selected = stream.readBoolean();
        if (selected) {
            clearTextMenuItem.setSelected(true);
        } else {
            keepTextMenuItem.setSelected(true);
        }
    }

    public void start() {
        if (clearTextMenuItem.isSelected()) {
            textArea.setText("");
        }
    }

    public void append(String str) {
        int len = str.length() + textArea.getText().length();
        if (len <= maxLength) {
            textArea.append(str);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(textArea.getText());
        sb.append(str);
        sb.delete(0, len - maxLength);
        textArea.setText(sb.toString());
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static final String frameTitle = "Konsola";
        public static final String clear = "Wyczyść";
        
        public static final String clearingMenu = "Kasowanie";
        public static final String clearMenuItem = "Wyczyść konsolę przed uruchomieniem";
        public static final String keepMenuItem = "Zostaw nieskasowany tekst";
        
        public static final String optionsMenu = "Opcje";
        public static final String saveMenuItem = "Zapisz do pliku...";
        
        public static final String save = "Zapisz";
        public static final String textFileDescription = "Plik tekstowy (*.txt)";
    }
    // </editor-fold>
    
}
