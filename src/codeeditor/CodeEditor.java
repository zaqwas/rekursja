package codeeditor;

//<editor-fold defaultstate="collapsed" desc="Import classes">
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import mainclass.MainClass;
import parser.ParseError;
import parser.Parser;
import parser.ProgramError;
import syntax.SyntaxTree;
//</editor-fold>

public class CodeEditor {
    
    //<editor-fold defaultstate="collapsed" desc="Components">
    private JInternalFrame frame;
    
    private JTextPane editorTextPane;
    private JLabel lineNumbersLabel;
    private JPanel textAndNumbersPanel;
    private JScrollPane scrollPane;
    
    private JLabel lineLabel;
    private JLabel positionLabel;
    private JLabel statusLabel;
    
    private JMenuBar menuBar;
    
    private JMenu editMenu;
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;
    private JMenuItem cutMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    
    private JMenu optionsMenu;
    private JMenuItem checkSyntaxMenuItem;
    private JMenuItem saveToFileMenuItem;
    
    private JPopupMenu editPopupMenu;
    private JMenuItem undoPopupMenuItem;
    private JMenuItem redoPopupMenuItem;
    private JMenuItem cutPopupMenuItem;
    private JMenuItem copyPopupMenuItem;
    private JMenuItem pastePopupMenuItem;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private MainClass mainClass;
    private SyntaxTree syntaxTree;
    
    private int numberOfLines = 1;
    private ArrayList<Integer> indexesOfNewLines = new ArrayList<>();
    
    private DefaultStyledDocument doc;
    
    private Color unmodifyingRangeColor = new Color(223, 223, 223);
    private ArrayList<Range> unmodifyingLines = new ArrayList<>();
    
    private Color statmentStrongColor = new Color(189, 230, 170);
    private Color statmentWeakColor = new Color(233, 255, 230);
    private Range paintedStatment;
    
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private FontMetrics monospacedFontMetrics;
    private int monospacedFontHeight;
    
    private SimpleAttributeSet emptyAttributeSet;
    private SimpleAttributeSet keyWordAttributeSet;
    private Color keyWordColor = new Color(0, 0, 230);
    private SimpleAttributeSet stringAttributeSet;
    private Color stringColor = new Color(206, 123, 0);
    private SimpleAttributeSet commentAttributeSet;
    private Color commentColor = new Color(150, 150, 150);
    
    private String tabString = "  ";
    private int tabSize = 2;
    
    private Insets margin = new Insets(0, 2, 0, 2);
    
    private int undoLimit = 100;
    private LinkedList<UndoInfo> undoStack = new LinkedList<>();
    private Stack<UndoInfo> redoStack = new Stack<>();
    private boolean addUndoToStack = true;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private static classes">
    private static class UndoInfo {
        public int offset;
        public String removedString;
        public String insertedString;
        
        public UndoInfo(int offset, String removedString, String insertedString) {
            this.offset = offset;
            this.removedString = removedString;
            this.insertedString = insertedString;
        }
    }
    
    private static class Range {
        public int start, end;
        public Range(int startLine, int endLine) {
            this.start = startLine;
            this.end = endLine;
        }
        public Range(int startLine) {
            this.start = startLine;
        }
        public boolean contains(int idxL, int idxR) {
            return this.start <= idxR && idxL <= this.end;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private functions">
    private boolean isKeyWord(String str) {
        return str.equals("var") 
                || str.equals("int") || str.equals("void")
                || str.equals("break") || str.equals("continue") 
                || str.equals("return") 
                || str.equals("if") || str.equals("else") 
                || str.equals("while") || str.equals("do") || str.equals("for");
    }
    
    private boolean canModify(int lineL, int lineR) {
        if (unmodifyingLines==null || unmodifyingLines.isEmpty()){
            return true;
        }
        int left = 0;
        int right = unmodifyingLines.size() - 1;
        while ( left <= right ) {
            int middle = (left + right) / 2;
            Range range = unmodifyingLines.get(middle);
            if ( lineR < range.start) {
                right = middle - 1;
            } else if ( range.end < lineL ) {
                left = middle + 1;
            } else {
                return false;
            }
        }
        return true;
    }
    
    private void shiftUnmodmodifyRanges(int offset, int shiftLines) {
        if ( shiftLines==0 || unmodifyingLines==null || unmodifyingLines.isEmpty() ) {
            return;
        }
        int line = getLine(offset);
        int right = unmodifyingLines.size() - 1;
        Range range = unmodifyingLines.get(right);
        if ( range.start < line ) {
            return;
        }
        int left = 0;
        while ( left < right ) {
            int middle = (left + right) / 2;
            if ( line < unmodifyingLines.get(middle).start ) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        for (int i = left; i < unmodifyingLines.size(); i++) {
            range = unmodifyingLines.get(i);
            range.start += shiftLines;
            range.end += shiftLines;
        }
    }
    
    private int getLine(int index) {
        int left = 0;
        int right = indexesOfNewLines.size();
        while (left < right) {
            int middle = (left + right) / 2;
            int indexMiddle = indexesOfNewLines.get(middle);
            if (index == indexMiddle) {
                left = middle;
                break;
            } else if (index < indexMiddle) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        return left;
    }
    
    private void setLineAndPosition(int caretPosition) {
        int line = getLine(caretPosition) + 1;
        int position = caretPosition - (line == 1 ? -1 : indexesOfNewLines.get(line - 2));
        lineLabel.setText(Integer.toString(line));
        positionLabel.setText(Integer.toString(position));
    }
    
    private void setProperLineNumbers() {
        int newNumberOfLines = indexesOfNewLines.size() + 1;
        if (numberOfLines == newNumberOfLines) {
            return;
        }
        numberOfLines = newNumberOfLines;
        StringBuilder sb = new StringBuilder("<html><p align=\"right\">");
        for (int i = 0; i < numberOfLines; i++) {
            sb.append(i + 1).append("<br/>");
        }
        sb.append("</p></html>");
        lineNumbersLabel.setText(sb.toString());
    }
    
    private void paintHighlights(Graphics g) {
        int width = editorTextPane.getWidth();
        g.setColor(unmodifyingRangeColor);
        for (Range range : unmodifyingLines) {
            int y = range.start * monospacedFontHeight + margin.top;
            int h = (range.end - range.start + 1) * monospacedFontHeight;
            g.fillRect(0, y, width, h);
        }
        Range range = paintedStatment;
        if (range != null) {
            String str = editorTextPane.getText();
            int lineL = getLine(range.start);
            int lineR = getLine(range.end);
            int widthL = monospacedFontMetrics.stringWidth(str.substring(
                    lineL == 0 ? 0 : indexesOfNewLines.get(lineL - 1), range.start));
            int widthR = monospacedFontMetrics.stringWidth(str.substring(
                    lineR == 0 ? 0 : indexesOfNewLines.get(lineR - 1), range.end));

            int y = lineL * monospacedFontHeight + margin.top;
            int h = (lineR - lineL + 1) * monospacedFontHeight;
            g.setColor(statmentWeakColor);
            g.fillRect(0, y, width, h);

            g.setColor(statmentStrongColor);
            y = lineL * monospacedFontHeight + margin.top;
            if (lineL == lineR) {
                g.fillRect(margin.left + widthL, y, widthR - widthL, monospacedFontHeight);
            } else {
                int x = margin.left + widthL;
                g.fillRect(x, y, width - x, monospacedFontHeight);
                y = lineR * monospacedFontHeight + margin.top;
                g.fillRect(0, y, margin.left + widthR, monospacedFontHeight);
                if (lineR - lineL > 1) {
                    y = (lineL + 1) * monospacedFontHeight + margin.top;
                    h = (lineR - lineL - 1) * monospacedFontHeight;
                    g.fillRect(0, y, width, h);
                }
            }
        }
    }
    
    private void updateUndoButtons() {
        undoMenuItem.setEnabled(!undoStack.isEmpty());
        undoPopupMenuItem.setEnabled(!undoStack.isEmpty());
        redoMenuItem.setEnabled(!redoStack.isEmpty());
        redoPopupMenuItem.setEnabled(!redoStack.isEmpty());
    }
    
    private void saveToFile() {
        //TODO saveToFile
    }
    
    private void undoAction() {
        if ( undoStack.isEmpty() ) {
            return;
        }
        UndoInfo info = undoStack.removeLast();
        addUndoToStack = false;
        try {
            doc.replace(info.offset, info.insertedString.length(),
                    info.removedString, emptyAttributeSet);
        } catch (BadLocationException ex) {}
        redoStack.add(info);
        addUndoToStack = true;
        updateUndoButtons();
    }
    
    private void redoAction() {
        if ( redoStack.isEmpty() ) {
            return;
        }
        UndoInfo info = redoStack.pop();
        addUndoToStack = false;
        try {
            doc.replace(info.offset, info.removedString.length(),
                    info.insertedString, emptyAttributeSet);
        } catch (BadLocationException ex) {}
        undoStack.addLast(info);
        addUndoToStack = true;
        updateUndoButtons();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="docFilterReplaceFunction">
    private void docFilterReplaceFunction(DocumentFilter.FilterBypass fb,
            int offset, int length, String string) 
            throws BadLocationException {
        
        int lineL = getLine(offset);
        int lineR = length > 0 ? getLine(offset + length) : lineL;
        if (!canModify(lineL, lineR)) {
            return;
        }
        int shiftLines = lineL - lineR;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                shiftLines++;
            }
        }
        shiftUnmodmodifyRanges(offset, shiftLines);

        boolean afterOffset = false;
        int index = 0, prevIndex = 0, strLength = doc.getLength();
        int positionInLine = 0, idxPrevVisibleChar = -1;
        int bracketBalance = 0, startPositionOfComment = 0;
        int state = 0;//0:normal, 1:abc, 2:/, 3://, 4:/*, 5:/* *, 6:", 7:"\
        String str = doc.getText(0, strLength);

        indexesOfNewLines.clear();
        doc.setCharacterAttributes(0, strLength, emptyAttributeSet, true);
        while (true) {

            //<editor-fold defaultstate="collapsed" desc="Parser loop">
            while (index < strLength && (afterOffset || index < offset)) {
                char c = str.charAt(index);
                if (c == '\n') {
                    indexesOfNewLines.add(index);
                    positionInLine = 0;
                } else {
                    positionInLine++;
                }
                if (!Character.isWhitespace(c)) {
                    idxPrevVisibleChar = index;
                }

                if (state <= 2) {

                    //<editor-fold defaultstate="collapsed" desc="Normal state">
                    if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || c == '_'
                            || (state == 1 && '0' <= c && c <= '9')) {
                        if (state != 1) {
                            state = 1;
                            prevIndex = index;
                        }
                        index++;
                        continue;
                    }
                    if (state == 1) {
                        if (isKeyWord(str.substring(prevIndex, index))) {
                            doc.setCharacterAttributes(prevIndex,
                                    index - prevIndex, keyWordAttributeSet, false);
                        }
                    }
                    if (state == 2 && c == '*') {
                        state = 4;
                        startPositionOfComment = positionInLine - 2;
                        prevIndex = index - 1;
                    } else if (c == '/') {
                        if (state == 2) {
                            state = 3;
                            prevIndex = index - 1;
                        } else {
                            state = 2;
                        }
                    } else if (c == '"') {
                        state = 6;
                        prevIndex = index;
                    } else {
                        state = 0;
                        if (c == '{') {
                            bracketBalance++;
                        } else if (c == '}') {
                            bracketBalance--;
                        }
                    }
                    //</editor-fold>

                } else if (state == 3) {

                    //<editor-fold defaultstate="collapsed" desc="Inside comment:\\">
                    if (c == '\n') {
                        state = 0;
                        doc.setCharacterAttributes(prevIndex, index - prevIndex,
                                commentAttributeSet, false);
                    }
                    //</editor-fold>

                } else if (state <= 5) {

                    //<editor-fold defaultstate="collapsed" desc="Inside comment:\* *\">
                    if (c == '*') {
                        state = 5;
                    } else if (state == 5 && c == '/') {
                        state = 0;
                        doc.setCharacterAttributes(prevIndex, index - prevIndex + 1,
                                commentAttributeSet, false);
                    } else {
                        state = 4;
                    }
                    //</editor-fold>

                } else {

                    //<editor-fold defaultstate="collapsed" desc="Inside string">
                    if (c == '\n') {
                        state = 0;
                        doc.setCharacterAttributes(prevIndex, index - prevIndex,
                                stringAttributeSet, false);
                    } else if (state == 6 && c == '"') {
                        state = 0;
                        doc.setCharacterAttributes(prevIndex, index - prevIndex + 1,
                                stringAttributeSet, false);
                    } else if (c == '\\') {
                        state = state == 6 ? 7 : 6;
                    } else {
                        state = 6;
                    }
                    //</editor-fold>

                }
                index++;
            }
            //</editor-fold>

            if (afterOffset) {

                //<editor-fold defaultstate="collapsed" desc="End of string reached">
                //state - 0:normal, 1:abc, 2:/, 3://, 4:/*, 5:/* *, 6:", 7:"\
                if (state >= 6) {
                    doc.setCharacterAttributes(prevIndex, index - prevIndex,
                            stringAttributeSet, false);
                } else if (state >= 3) {
                    doc.setCharacterAttributes(prevIndex, index - prevIndex,
                            commentAttributeSet, false);
                } else if (state == 1) {
                    if (isKeyWord(str.substring(prevIndex, index))) {
                        doc.setCharacterAttributes(prevIndex,
                                index - prevIndex, keyWordAttributeSet, false);
                    }
                }
                setProperLineNumbers();
                syntaxTree = null;
                mainClass.clearError();
                statusLabel.setText(Lang.modified);
                return;
                //</editor-fold>

            }
            
            //<editor-fold defaultstate="collapsed" desc="Offset reached">
            //state - 0:normal, 1:abc, 2:/, 3://, 4:/*, 5:/* *, 6:", 7:"\
            switch (string) {
                case "\t":
                    string = tabString;
                    break;
                case "\n":
                    int size = state == 4 || state == 5
                            ? startPositionOfComment : bracketBalance * tabSize;
                    StringBuilder sb = new StringBuilder(string);
                    for (int i = 0; i < size; i++) {
                        sb.append(" ");
                    }
                    string = sb.toString();
                    break;
                case "}":
                    int cut = 0;
                    if (state <= 2) {
                        int properSize = tabSize * (bracketBalance - 1);
                        if (properSize < 0) {
                            properSize = 0;
                        }
                        cut = Math.min(offset - idxPrevVisibleChar - 1, positionInLine - properSize);
                        if (cut <= 0) {
                            cut = 0;
                        }
                    }
                    if (cut > 0) {
                        offset -= cut;
                        length += cut;
                        index -= cut;
                    }
                    break;
            }
            if ( addUndoToStack ) {
                if ( undoStack.size()>=undoLimit ) {
                    undoStack.removeFirst();
                }
                undoStack.addLast(new UndoInfo(offset, doc.getText(offset, length), string));
                redoStack.clear();
                updateUndoButtons();
            }
            fb.replace(offset, length, string, emptyAttributeSet);
            str = doc.getText(0, doc.getLength());
            strLength = str.length();
            afterOffset = true;
            //</editor-fold>
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public functions">
    public JInternalFrame getFrame() {
        return frame;
    }
    
    public void selectText(int indexL, int indexR) {
        //TODO potrzeba poprawić widok
        editorTextPane.getCaret().setDot(indexR);
        if ( indexR != indexL ) {
            editorTextPane.getCaret().moveDot(indexL);
        }
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ex) {}
    }
        
    public void paintStatement(int indexL, int indexR) {
        paintedStatment = new Range(indexL, indexR);
        editorTextPane.getCaret().setDot(indexL);
        editorTextPane.repaint();
    }
    
    public void clrearStatement() {
        paintedStatment = null;
        editorTextPane.repaint();
    }
    
    public void setCode(String text) {
        ArrayList<Range> unmodLines = unmodifyingLines;
        unmodifyingLines = null;
        unmodLines.clear();
        if ( text.isEmpty() ) {
            editorTextPane.setText(text);
            unmodifyingLines = unmodLines;
            return;
        }
        Range range = text.charAt(0) == '~' ? null : new Range(0);
        StringBuilder sb = new StringBuilder();
        int line = 0;
        for (int i = range == null ? 1 : 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ( c=='\r') {
                continue;
            }
            if (c=='~' && text.charAt(i-1)=='\n') {
                if ( range==null ) {
                    range = new Range(line);
                } else {
                    range.end = line-1;
                    unmodLines.add(range);
                    range = null;
                }
            } else {
                if ( c=='\n') {
                    line++;
                }
                sb.append(c);
            }
        }
        if ( range!=null ) {
            range.end = line;
            unmodLines.add(range);
        }
        editorTextPane.setText(sb.toString());
        unmodifyingLines = unmodLines;
        undoStack.clear();
        redoStack.clear();
        updateUndoButtons();
    }
    
    public String getCode() {
        String str = editorTextPane.getText();
        if ( unmodifyingLines==null || unmodifyingLines.isEmpty() ) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int offset = 0;
        boolean skipFirst = true;
        if (unmodifyingLines.get(0).start !=0 )
        {
            sb.insert(0, '~');
            offset++;
            skipFirst = false;
        }
        for(Range r : unmodifyingLines)
        {
            int newLine;
            if ( !skipFirst ) {
                newLine = indexesOfNewLines.get(r.start-1);
                sb.insert(newLine+offset+1, '~');
                offset++;
            }
            skipFirst = false;
            if (r.end+1 == numberOfLines) {
                break;
            }
            newLine = indexesOfNewLines.get(r.end);
            sb.insert(newLine+offset+1, '~');
            offset++;
        }
        return sb.toString();
    }
    
    public SyntaxTree getSyntaxTree() {
        try {
            syntaxTree = Parser.parse(editorTextPane.getText());
            statusLabel.setText(Lang.syntaxOK);
        } catch (ParseError ex) {
            //TODO należy wyeliminować parserError zastępując go ProgramError
            syntaxTree = null;
            ProgramError pe = new ProgramError(Langs.getParseError(ex.code), ex.index, ex.index);
            int line = getLine(ex.index) + 1;
            int position = ex.index - (line == 1 ? -1 : indexesOfNewLines.get(line - 2));
            pe.setLineAndPosition(line, position);
            selectText(ex.index, ex.index);
            mainClass.setError(pe);
            statusLabel.setText(Lang.syntaxError);
        }
        return syntaxTree;
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
    
    public void save(String strFile) {
        try {
            FileWriter fWriter = new FileWriter(strFile);
            String text = editorTextPane.getText();
            fWriter.write(text, 0, text.length());
            fWriter.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public void load(String strFile) {
        try {
            FileReader fReader = new FileReader(strFile);
            char buffer[] = new char[4], all[] = new char[0];
            int bRead;
            while ( (bRead=fReader.read(buffer))>0 ) {
                int oldLength = all.length;
                all = Arrays.copyOf(all, oldLength + bRead);
                System.arraycopy(buffer, 0, all, oldLength, bRead);
                //System.out.println(all[0]);
            }
            String text = new String(all);
            editorTextPane.setText(text);
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public CodeEditor (MainClass mainClass) {
        this.mainClass = mainClass;
        frame = new JInternalFrame(Lang.frameTitle);
        
        //<editor-fold defaultstate="collapsed" desc="Attributes">
        emptyAttributeSet = new SimpleAttributeSet();
        keyWordAttributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWordAttributeSet, keyWordColor);
        commentAttributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(commentAttributeSet, commentColor);
        stringAttributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(stringAttributeSet, stringColor);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="EditorTextPane and NumbersLabel - Init">
        editorTextPane = new JTextPane();
        editorTextPane.setFont(monospacedFont);
        editorTextPane.setForeground(Color.BLACK);
        editorTextPane.setMargin(margin);

        monospacedFontMetrics = editorTextPane.getFontMetrics(monospacedFont);
        monospacedFontHeight = monospacedFontMetrics.getHeight();

        lineNumbersLabel = new JLabel("<html><p align=\"right\">1<br></p></html>");
        lineNumbersLabel.setFont(monospacedFont);
        
        textAndNumbersPanel = new JPanel();
        GroupLayout layout = new GroupLayout(textAndNumbersPanel);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addComponent(lineNumbersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(1)
            .addComponent(editorTextPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(lineNumbersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(editorTextPane, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );
        textAndNumbersPanel.setLayout(layout);

        scrollPane = new JScrollPane(textAndNumbersPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Document filter">
        doc = (DefaultStyledDocument) editorTextPane.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
                    AttributeSet attrs) throws BadLocationException {
                docFilterReplaceFunction(fb, offset, 0, string);
            }
            @Override
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                docFilterReplaceFunction(fb, offset, length, "");
            }
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string,
                    AttributeSet attrs) throws BadLocationException {
                docFilterReplaceFunction(fb, offset, length, string);
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="CaretListener and Highlighter">
        editorTextPane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                setLineAndPosition(e.getDot());
            }
        });
        
        editorTextPane.setHighlighter(new DefaultHighlighter() {
            @Override
            public void paint(Graphics g) {
                paintHighlights(g);
                super.paint(g);
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Line and position labels">
        statusLabel = new JLabel();

        lineLabel = new JLabel("1");
        lineLabel.setFont(monospacedFont);
        lineLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        Dimension d = lineLabel.getMinimumSize();
        d.width += monospacedFontMetrics.charWidth('0');
        lineLabel.setMinimumSize(d);
        lineLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        positionLabel = new JLabel("1");
        positionLabel.setFont(monospacedFont);
        positionLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        positionLabel.setMinimumSize(d);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Frame layout">
        layout = new GroupLayout(frame.getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(scrollPane, 0, 250, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(2)
                .addComponent(lineLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2)
                .addComponent(positionLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2)
                .addComponent(statusLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(scrollPane, 0, 400, Short.MAX_VALUE)
            .addGap(1)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lineLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(positionLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(statusLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGap(1)
        );
        frame.setLayout(layout);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Menu Items - Init">
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        //<editor-fold defaultstate="collapsed" desc="editPopupMenu">
        editPopupMenu = new JPopupMenu(Lang.edit);
        
        undoPopupMenuItem = new JMenuItem(Lang.undo);
        undoPopupMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK));
        editPopupMenu.add(undoPopupMenuItem);
        
        redoPopupMenuItem = new JMenuItem(Lang.redo);
        redoPopupMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK));
        editPopupMenu.add(redoPopupMenuItem);
        
        editPopupMenu.add(new JSeparator());
        
        cutPopupMenuItem = new JMenuItem(Lang.cut);
        cutPopupMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        editPopupMenu.add(cutPopupMenuItem);
        
        copyPopupMenuItem = new JMenuItem(Lang.copy);
        copyPopupMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        editPopupMenu.add(copyPopupMenuItem);
        
        pastePopupMenuItem = new JMenuItem(Lang.paste);
        pastePopupMenuItem.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        editPopupMenu.add(pastePopupMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="editMenu">
        editMenu = new JMenu(Lang.edit);
        menuBar.add(editMenu);
        
        undoMenuItem = new JMenuItem(Lang.undo);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK));
        editMenu.add(undoMenuItem);
        
        redoMenuItem = new JMenuItem(Lang.redo);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK));
        editMenu.add(redoMenuItem);
        
        editMenu.add(new JSeparator());
        
        cutMenuItem = new JMenuItem(Lang.cut);
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        editMenu.add(cutMenuItem);
        
        copyMenuItem = new JMenuItem(Lang.copy);
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        editMenu.add(copyMenuItem);
        
        pasteMenuItem = new JMenuItem(Lang.paste);
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        editMenu.add(pasteMenuItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="optionsMenu">
        optionsMenu = new JMenu(Lang.options);
        menuBar.add(optionsMenu);
        
        checkSyntaxMenuItem = new JMenuItem(Lang.checkSyntax);
        checkSyntaxMenuItem.setAccelerator(KeyStroke.getKeyStroke("F4"));
        optionsMenu.add(checkSyntaxMenuItem);
        
        optionsMenu.add(new JSeparator());
        
        saveToFileMenuItem = new JMenuItem(Lang.saveToFile);
        optionsMenu.add(saveToFileMenuItem);
        //</editor-fold>
        
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Menu Items - Listeners">
        
        //<editor-fold defaultstate="collapsed" desc="Show popup">
        editorTextPane.addMouseListener(new MouseAdapter() {
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
                    editPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Undo, redo">
        ActionListener undoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoAction();
            }
        };
        undoPopupMenuItem.addActionListener(undoActionListener);
        undoMenuItem.addActionListener(undoActionListener);
        
        ActionListener redoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redoAction();
            }
        };
        redoPopupMenuItem.addActionListener(redoActionListener);
        redoMenuItem.addActionListener(redoActionListener);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Cut, copy, paste">
        ActionListener cutListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorTextPane.cut();
            }
        };
        cutPopupMenuItem.addActionListener(cutListener);
        cutMenuItem.addActionListener(cutListener);
        
        ActionListener copyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorTextPane.copy();
            }
        };
        copyPopupMenuItem.addActionListener(copyListener);
        copyMenuItem.addActionListener(copyListener);
        
        ActionListener pasteListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorTextPane.paste();
            }
        };
        pastePopupMenuItem.addActionListener(pasteListener);
        pasteMenuItem.addActionListener(pasteListener);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="CheckSyntax, SaveToFile">
        checkSyntaxMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                getSyntaxTree();
            }
        });
        saveToFileMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });
        //</editor-fold>
        
        //</editor-fold>
        
        frame.pack();
        frame.setResizable(true);
        
        //<editor-fold defaultstate="collapsed" desc="SetCode - delete this">
        //TODO SetCode delete this
//        docMgr.getCodeTextPane().setText(
//                "int silnia(n) {\n"
//                + "   if (n<=0) return 1;\n"
//                + "   else return n * silnia(n-1);\n"
//                + "}\n"
//                + "void main() {\n"
//                + "   silnia(20);\n"
//                + "}\n");
        
//        setCode(
//                "\0var a;\n"
//                + "//aaa\n"
//                + "\" aa {} \"\n"
//                + "/*\n"
//                + "aaa\n"
//                + "bbb\n"
//                + "*/\n"
//                );
        
//        setCode(
//                "\0/*\n"
//                + "a\n"
//                + "b\n"
//                + "*/\n"
//                + "\"c\"\n"
//                + "d"
//                );
        
        //setCode("\0_int\n\09int\n\0int9\n\0int");
        
        setCode(
                "var qw[20], zx;\n"
                + "\n"
                + "~void swap1(a&,b&)\n"
                + "var q, w[3], e;\n"
                + "{\n"
                + "  q = a;\n"
                + "  a = b;\n"
                + "  b = q;\n"
                + "}\n"
                + "\n"
                + "~void swap2(a,tab[],b&)\n"
                + "var q, w[3], e;\n"
                + "{\n"
                + "  e = tab[a];\n"
                + "  tab[a] = tab[b];\n"
                + "  tab[b] = e;\n"
                + "}\n"
                + "~\n"
                + "void main()\n"
                + "var g[3], t, b;\n"
                + "{\n"
                + "  t=1; b=2;\n"
                + "  swap1(t,b);\n"
                + "  write(t,\" \",b,\"\\n\");\n"
                + "  g[t]=2; g[b]=3;\n"
                + "  swap2(t, g, b);\n"
                + "  write(g[t],\" \",g[b],\"\\n\");\n"
                + "}"
                );
        //</editor-fold>
        
    }
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="Language">
    private static class Lang {
        public static String frameTitle = "Edytor kodu";
        
        public static String edit = "Edycja";
        public static String cut = "Wytnij";
        public static String copy = "Kopiuj";
        public static String paste = "Wklej";
        public static String undo = "Cofnij";
        public static String redo = "Powtórz";
        public static String insertSpecialFunction = "Wstaw funkcję specjalną";
        public static String closeLesson = "Zamknij lekcję";
        
        public static String options = "Opcje";
        public static String checkSyntax = "Sprawdź składnię";
        public static String saveToFile = "Zapisz do pliku...";
        
        public static String modified = "Zmodyfikowano.";
        public static String syntaxOK = "Składnia poprawna.";
        public static String syntaxError = "Błąd w składni programu.";
    }
    //</editor-fold>
    
}
//TODO save to file