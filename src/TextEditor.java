import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// Chatbot window

/**
 * A simple text editor application with basic functionality.
 */
public class TextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String currentFile = null;
    private boolean changed = false;
    private UndoManager undoManager;
    private JButton plusButton;
    private JPopupMenu optionsMenu;

    // Drawing canvas
    private DrawingCanvas canvas;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private static final String TEXT_CARD = "TEXT_EDITOR";
    private static final String CANVAS_CARD = "DRAWING_CANVAS";

    // Toolbar components
    private JToolBar toolBar;
    private JButton drawButton;
    private JButton textButton;

    // Constructor
    public TextEditor() {
        // Set up the frame
        super("Simple Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize components
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Add document listener to track changes
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                changed = true;
            }
            public void removeUpdate(DocumentEvent e) {
                changed = true;
            }
            public void changedUpdate(DocumentEvent e) {
                changed = true;
            }
        });

        // Set up undo manager
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        // Set up file chooser
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        // Create scroll pane for text area
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Initialize drawing canvas
        canvas = new DrawingCanvas();

        // Set up card layout for switching between text area and canvas
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(scrollPane, TEXT_CARD);
        contentPanel.add(canvas, CANVAS_CARD);

        // Show text editor by default
        cardLayout.show(contentPanel, TEXT_CARD);

        add(contentPanel, BorderLayout.CENTER);

        // Create menu bar
        createMenuBar();

        // Create toolbar
        createToolbar();

        // Create plus button with options
        createPlusButton();

        // Make the frame visible
        setVisible(true);
    }

    // Create the menu bar with all options
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(e -> newFile());
        openMenuItem.addActionListener(e -> openFile());
        saveMenuItem.addActionListener(e -> saveFile());
        saveAsMenuItem.addActionListener(e -> saveFileAs());
        exitMenuItem.addActionListener(e -> exit());

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        JMenuItem findMenuItem = new JMenuItem("Find");
        JMenuItem selectAllMenuItem = new JMenuItem("Select All");

        undoMenuItem.addActionListener(e -> undo());
        redoMenuItem.addActionListener(e -> redo());
        cutMenuItem.addActionListener(e -> textArea.cut());
        copyMenuItem.addActionListener(e -> textArea.copy());
        pasteMenuItem.addActionListener(e -> textArea.paste());
        findMenuItem.addActionListener(e -> find());
        selectAllMenuItem.addActionListener(e -> textArea.selectAll());

        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(findMenuItem);
        editMenu.add(selectAllMenuItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // Set the menu bar
        setJMenuBar(menuBar);
    }

    // File operations
    private void newFile() {
        if (confirmSave()) {
            textArea.setText("");
            undoManager.discardAllEdits();
            currentFile = null;
            changed = false;
            setTitle("Simple Text Editor");
        }
    }

    private void openFile() {
        if (confirmSave()) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    undoManager.discardAllEdits();
                    currentFile = file.getPath();
                    changed = false;
                    setTitle("Simple Text Editor - " + file.getName());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error reading file: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(textArea.getText());
                changed = false;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFileAs() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textArea.getText());
                currentFile = file.getPath();
                changed = false;
                setTitle("Simple Text Editor - " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exit() {
        if (confirmSave()) {
            System.exit(0);
        }
    }

    // Edit operations
    private void undo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException e) {
            // Ignore
        }
    }

    private void redo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException e) {
            // Ignore
        }
    }

    private void find() {
        String searchText = JOptionPane.showInputDialog(this, 
            "Enter text to search:", "Find", JOptionPane.QUESTION_MESSAGE);

        if (searchText != null && !searchText.isEmpty()) {
            String text = textArea.getText();
            int index = text.indexOf(searchText);

            if (index != -1) {
                textArea.setCaretPosition(index);
                textArea.select(index, index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Text not found", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Helper methods
    private boolean confirmSave() {
        if (changed) {
            int option = JOptionPane.showConfirmDialog(this, 
                "The current file has been modified. Save changes?", 
                "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                saveFile();
                return !changed; // Return false if save failed
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    // Getters for testing
    public JTextArea getTextArea() {
        return textArea;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    // Create toolbar with editing tools
    private void createToolbar() {
        // Create vertical toolbar
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.WHITE);
        toolBar.setBorder(new LineBorder(new Color(220, 220, 220), 1));

        // Create toolbar buttons with icons
        drawButton = createToolbarButton("Draw", "\u270F"); // Pencil symbol
        textButton = createToolbarButton("Text", "T");

        // Add buttons to toolbar
        toolBar.add(drawButton);
        toolBar.add(textButton);

        // Add toolbar to the frame
        add(toolBar, BorderLayout.WEST);
    }

    // Helper method to create toolbar buttons
    private JButton createToolbarButton(String toolTip, String symbol) {
        JButton button = new JButton(symbol);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.DARK_GRAY);
        button.setBackground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setToolTipText(toolTip);

        // Add action listener
        button.addActionListener(e -> {
            // Reset all button backgrounds
            drawButton.setBackground(Color.WHITE);
            textButton.setBackground(Color.WHITE);

            // Set this button as selected
            button.setBackground(new Color(220, 220, 255));

            // Implement specific tool functionality
            switch (toolTip) {
                case "Draw":
                    implementDrawTool();
                    break;
                case "Text":
                    implementTextTool();
                    break;
            }
        });

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground().equals(Color.WHITE)) {
                    button.setBackground(new Color(240, 240, 240));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground().equals(new Color(240, 240, 240))) {
                    button.setBackground(Color.WHITE);
                }
            }
        });

        return button;
    }

    // Tool implementation methods
    private void implementDrawTool() {
        // Switch to canvas view
        cardLayout.show(contentPanel, CANVAS_CARD);
    }

    private void implementTextTool() {
        // Switch to text editor view
        cardLayout.show(contentPanel, TEXT_CARD);

        textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        // Remove any existing mouse listeners
        for (MouseListener ml : textArea.getMouseListeners()) {
            if (ml instanceof TextMouseListener) {
                textArea.removeMouseListener(ml);
            }
        }

        // Add text mouse listener
        textArea.addMouseListener(new TextMouseListener());
    }


    // Mouse listener classes for each tool

    private class TextMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int pos = textArea.viewToModel2D(e.getPoint());
            textArea.setCaretPosition(pos);
        }
    }


    /**
     * A canvas for drawing with the mouse.
     */
    private class DrawingCanvas extends JPanel {
        private ArrayList<Point> points = new ArrayList<>();
        private ArrayList<ArrayList<Point>> lines = new ArrayList<>();
        private boolean isDrawing = false;

        public DrawingCanvas() {
            setBackground(Color.WHITE);

            // Add mouse listeners for drawing
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isDrawing = true;
                    points = new ArrayList<>();
                    points.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isDrawing = false;
                    if (!points.isEmpty()) {
                        lines.add(new ArrayList<>(points));
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDrawing) {
                        points.add(e.getPoint());
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Set rendering hints for smoother lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);

            // Draw all saved lines
            for (ArrayList<Point> line : lines) {
                drawLine(g2d, line);
            }

            // Draw the current line
            if (!points.isEmpty()) {
                drawLine(g2d, points);
            }
        }

        private void drawLine(Graphics2D g2d, ArrayList<Point> points) {
            if (points.size() < 2) return;

            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        public void clear() {
            points.clear();
            lines.clear();
            repaint();
        }
    }

    // Method to open a spreadsheet in a new tab/window
    private void openSpreadsheet() {
        // Create a new JFrame for the spreadsheet
        JFrame spreadsheetFrame = new JFrame("Spreadsheet");
        spreadsheetFrame.setSize(600, 400);
        spreadsheetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a table to represent the spreadsheet
        String[] columnNames = {"A", "B", "C", "D", "E", "F", "G", "H"};
        Object[][] data = new Object[20][8]; // 20 rows, 8 columns

        // Initialize with empty cells
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 8; j++) {
                data[i][j] = "";
            }
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        spreadsheetFrame.add(scrollPane);

        // Make the frame visible
        spreadsheetFrame.setVisible(true);
    }

    // Create plus button with options menu
    private void createPlusButton() {
        // Create plus button
        plusButton = new JButton("+");
        plusButton.setFont(new Font("Arial", Font.BOLD, 16));
        plusButton.setForeground(new Color(0, 120, 215));
        plusButton.setBackground(Color.WHITE);
        plusButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        plusButton.setFocusPainted(false);
        plusButton.setToolTipText("Click for options");

        // Create options menu
        optionsMenu = new JPopupMenu();
        JMenuItem uploadImageItem = new JMenuItem("Upload Image");
        JMenuItem createSpreadsheetItem = new JMenuItem("Create Spreadsheet");
        JMenuItem addCodeItem = new JMenuItem("Add Code");
        JMenuItem openChatbotItem = new JMenuItem("Open Chatbot");

        // Add action listeners
        uploadImageItem.addActionListener(e -> {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, 
                    "Image uploaded: " + fileChooser.getSelectedFile().getName(), 
                    "Upload Image", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        createSpreadsheetItem.addActionListener(e -> {
            openSpreadsheet();
        });

        addCodeItem.addActionListener(e -> {
            String codeSnippet = "// Add your code here\n";
            textArea.insert(codeSnippet, textArea.getCaretPosition());
        });

        openChatbotItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ChatbotWindow().setVisible(true));
        });

        // Add items to menu
        optionsMenu.add(uploadImageItem);
        optionsMenu.add(createSpreadsheetItem);
        optionsMenu.add(addCodeItem);
        optionsMenu.add(openChatbotItem);

        // Add action listener for click
        plusButton.addActionListener(e -> {
            optionsMenu.show(plusButton, 0, -optionsMenu.getPreferredSize().height);
        });

        // Add hover effect for visual feedback
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                plusButton.setBackground(new Color(240, 240, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                plusButton.setBackground(Color.WHITE);
            }
        });

        // We want the menu to stay visible when mouse moves away
        // No need to add a mouseExited listener

        // Create a panel for the button and position it at the bottom right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(plusButton);

        // Add the panel to the bottom of the frame
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
