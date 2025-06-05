import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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

    // Toolbar components
    private JToolBar toolBar;
    private JButton drawButton;
    private JButton textButton;
    private JButton highlightButton;
    private JButton eraseButton;
    private JButton selectButton;

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
        add(scrollPane, BorderLayout.CENTER);

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
        highlightButton = createToolbarButton("Highlight", "\u2591"); // Shade symbol
        eraseButton = createToolbarButton("Erase", "\u232B"); // Erase symbol
        selectButton = createToolbarButton("Select", "\u25FB"); // Empty square symbol

        // Add buttons to toolbar
        toolBar.add(drawButton);
        toolBar.add(textButton);
        toolBar.add(highlightButton);
        toolBar.add(eraseButton);
        toolBar.add(selectButton);

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
            JOptionPane.showMessageDialog(this, 
                toolTip + " tool selected", 
                "Tool Selection", JOptionPane.INFORMATION_MESSAGE);
        });

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
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
        plusButton.setToolTipText("Click or hover for options");

        // Create options menu
        optionsMenu = new JPopupMenu();
        JMenuItem createSpreadsheetItem = new JMenuItem("Create Spreadsheet");
        JMenuItem addCodeItem = new JMenuItem("Add Code");

        // Add action listeners
        createSpreadsheetItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Creating a new spreadsheet...", 
                "Create Spreadsheet", JOptionPane.INFORMATION_MESSAGE);
        });

        addCodeItem.addActionListener(e -> {
            String codeSnippet = "// Add your code here\n";
            textArea.insert(codeSnippet, textArea.getCaretPosition());
        });

        // Add items to menu
        optionsMenu.add(createSpreadsheetItem);
        optionsMenu.add(addCodeItem);

        // Add action listener for click
        plusButton.addActionListener(e -> {
            optionsMenu.show(plusButton, 0, -optionsMenu.getPreferredSize().height);
        });

        // Add mouse listeners for hover effect
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                optionsMenu.show(plusButton, 0, -optionsMenu.getPreferredSize().height);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Hide menu only if mouse is not over the menu
                Point p = e.getPoint();
                p = SwingUtilities.convertPoint(plusButton, p, optionsMenu);
                if (!optionsMenu.contains(p)) {
                    optionsMenu.setVisible(false);
                }
            }
        });

        // Add mouse listener to the menu to hide it when mouse exits
        optionsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                optionsMenu.setVisible(false);
            }
        });

        // Create a panel for the button and position it at the bottom right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(plusButton);

        // Add the panel to the bottom of the frame
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
