import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.w3c.dom.Node;

public class MarkdownNoteApp {
    private JFrame frame;
    private JTextArea markdownArea;
    private JEditorPane previewPane;
    private boolean darkMode = false;
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarkdownNoteApp::new);
    }

    public MarkdownNoteApp() {
        frame = new JFrame("Markdown Note App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        markdownArea = new JTextArea();
        attachDocumentListener();
        previewPane = new JEditorPane("text/html", "");
        previewPane.setEditable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(markdownArea), new JScrollPane(previewPane));
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);

        markdownArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem toggleTheme = new JMenuItem("Toggle Theme");
        toggleTheme.addActionListener(e -> toggleTheme());
        viewMenu.add(toggleTheme);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        frame.setJMenuBar(menuBar);
        frame.add(splitPane);
        frame.setVisible(true);

        updatePreview();
    }

    private void updatePreview() {
        String markdown = markdownArea.getText();
        org.commonmark.node.Node document = parser.parse(markdown);
        String html = renderer.render(document);
        previewPane.setText("<html><body style='font-family:sans-serif;''>" + html + "</body></html>");
    }

    private void attachDocumentListener() {
        markdownArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
    }
    

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Replace the document
                markdownArea.setText(""); // Clear before read to ensure change
                markdownArea.read(reader, null);
                attachDocumentListener(); // 👈 Reattach listener here
                updatePreview();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to open file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void saveFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                markdownArea.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;
        markdownArea.setBackground(bg);
        markdownArea.setForeground(fg);
        previewPane.setBackground(bg);
        previewPane.setForeground(fg);
    }
}
