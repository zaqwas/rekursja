package console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import syntax.function.FunctionWrite;

public class Console {
    private JInternalFrame frame;
    private JTextArea textArea;
    
    public JInternalFrame getFrame () {
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
    
    public Console() {
        FunctionWrite.setConsole(this);
        frame = new JInternalFrame();
        //TODO ang
        frame.setTitle("Konsola");
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setResizable(true);
        frame.pack();
        frame.setSize(200, 100);
        
        frame.setLocation(210, 0);
        
        //-------------- POPUP MENU ----------------------------------------------
        ActionListener clearListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        };
        JMenuItem clearMenu = new JMenuItem("Clear");
        clearMenu.addActionListener(clearListener);
        final JPopupMenu popupMenu = new JPopupMenu("Menu");
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
    }
    
    public void clear() {
        textArea.setText("");
    }
    
    public void append(String str) {
        textArea.append(str);
    }
}
