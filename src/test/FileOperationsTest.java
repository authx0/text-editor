package test;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test class for the file operations in the TextEditor application.
 * This tests the new, open, save, and saveAs functionality.
 */
public class FileOperationsTest {
    // Simple assertion methods
    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("PASS: " + message);
    }

    private static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            System.out.println("PASS: " + message);
            return;
        }
        if (expected != null && expected.equals(actual)) {
            System.out.println("PASS: " + message);
            return;
        }
        throw new AssertionError(message + " expected:<" + expected + "> but was:<" + actual + ">");
    }

    private static void assertNotNull(String message, Object object) {
        if (object == null) {
            throw new AssertionError(message);
        }
        System.out.println("PASS: " + message);
    }

    // Test file paths
    private static final String TEST_FILE_PATH = System.getProperty("java.io.tmpdir") + "/texteditor_test.txt";
    private static final String TEST_FILE_CONTENT = "This is a test file.\nIt has multiple lines.\nEnd of test file.";

    /**
     * Main method to run the tests.
     */
    public static void main(String[] args) {
        System.out.println("Running TextEditor file operations tests...");

        try {
            // Create a test file
            createTestFile();

            // Test file operations
            testNewFile();
            testOpenFile();
            testSaveFile();
            testSaveAsFile();

            System.out.println("All file operations tests passed!");
        } catch (AssertionError e) {
            System.err.println("TEST FAILED: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        } finally {
            // Clean up test files
            deleteTestFile();
        }
    }

    /**
     * Create a test file for use in the tests.
     */
    private static void createTestFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE_PATH))) {
            writer.write(TEST_FILE_CONTENT);
        }
        System.out.println("Created test file: " + TEST_FILE_PATH);
    }

    /**
     * Delete the test file after tests are complete.
     */
    private static void deleteTestFile() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
            System.out.println("Deleted test file: " + TEST_FILE_PATH);
        }
        
        // Also delete any other test files that might have been created
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = tempDir.listFiles((dir, name) -> name.startsWith("texteditor_test"));
        if (files != null) {
            for (File f : files) {
                f.delete();
                System.out.println("Deleted additional test file: " + f.getPath());
            }
        }
    }

    /**
     * Test the new file functionality.
     */
    private static void testNewFile() throws Exception {
        System.out.println("Testing new file functionality...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the text area using reflection
                Field textAreaField = textEditorClass.getDeclaredField("textArea");
                textAreaField.setAccessible(true);
                textArea[0] = (JTextArea) textAreaField.get(textEditor[0]);
                
                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor components: " + e.getMessage(), e);
            }
        });

        // Set some text in the text area
        final String initialText = "Initial text";
        SwingUtilities.invokeAndWait(() -> {
            textArea[0].setText(initialText);
        });

        // Call the newFile method using reflection
        SwingUtilities.invokeAndWait(() -> {
            try {
                Method newFileMethod = textEditor[0].getClass().getDeclaredMethod("newFile");
                newFileMethod.setAccessible(true);
                newFileMethod.invoke(textEditor[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call newFile method: " + e.getMessage(), e);
            }
        });

        // Close any confirmation dialogs
        Thread.sleep(500);
        closeAllDialogs();

        // Verify that the text area is empty
        final String[] text = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            text[0] = textArea[0].getText();
        });
        
        assertEquals("Text area should be empty after new file", "", text[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the open file functionality.
     */
    private static void testOpenFile() throws Exception {
        System.out.println("Testing open file functionality...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JTextArea[] textArea = new JTextArea[1];
        final JFileChooser[] fileChooser = new JFileChooser[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the text area using reflection
                Field textAreaField = textEditorClass.getDeclaredField("textArea");
                textAreaField.setAccessible(true);
                textArea[0] = (JTextArea) textAreaField.get(textEditor[0]);
                
                // Access the file chooser using reflection
                Field fileChooserField = textEditorClass.getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooser[0] = (JFileChooser) fileChooserField.get(textEditor[0]);
                
                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor components: " + e.getMessage(), e);
            }
        });

        // Set up the file chooser to select our test file
        SwingUtilities.invokeAndWait(() -> {
            fileChooser[0].setSelectedFile(new File(TEST_FILE_PATH));
        });

        // Mock the showOpenDialog method to return APPROVE_OPTION
        final JFileChooser originalFileChooser = fileChooser[0];
        final JFileChooser mockFileChooser = new JFileChooser() {
            @Override
            public int showOpenDialog(Component parent) {
                return JFileChooser.APPROVE_OPTION;
            }
            
            @Override
            public File getSelectedFile() {
                return new File(TEST_FILE_PATH);
            }
        };
        
        SwingUtilities.invokeAndWait(() -> {
            try {
                Field fileChooserField = textEditor[0].getClass().getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(textEditor[0], mockFileChooser);
            } catch (Exception e) {
                throw new RuntimeException("Failed to mock file chooser: " + e.getMessage(), e);
            }
        });

        // Call the openFile method using reflection
        SwingUtilities.invokeAndWait(() -> {
            try {
                Method openFileMethod = textEditor[0].getClass().getDeclaredMethod("openFile");
                openFileMethod.setAccessible(true);
                openFileMethod.invoke(textEditor[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call openFile method: " + e.getMessage(), e);
            }
        });

        // Close any confirmation dialogs
        Thread.sleep(500);
        closeAllDialogs();

        // Verify that the text area contains the test file content
        final String[] text = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            text[0] = textArea[0].getText().trim();
        });
        
        assertEquals("Text area should contain test file content", TEST_FILE_CONTENT.trim(), text[0]);

        // Restore the original file chooser
        SwingUtilities.invokeAndWait(() -> {
            try {
                Field fileChooserField = textEditor[0].getClass().getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(textEditor[0], originalFileChooser);
            } catch (Exception e) {
                throw new RuntimeException("Failed to restore file chooser: " + e.getMessage(), e);
            }
        });

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the save file functionality.
     */
    private static void testSaveFile() throws Exception {
        System.out.println("Testing save file functionality...");

        // Create a temporary file path for saving
        String saveFilePath = System.getProperty("java.io.tmpdir") + "/texteditor_test_save.txt";
        File saveFile = new File(saveFilePath);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the text area using reflection
                Field textAreaField = textEditorClass.getDeclaredField("textArea");
                textAreaField.setAccessible(true);
                textArea[0] = (JTextArea) textAreaField.get(textEditor[0]);
                
                // Set the current file using reflection
                Field currentFileField = textEditorClass.getDeclaredField("currentFile");
                currentFileField.setAccessible(true);
                currentFileField.set(textEditor[0], saveFilePath);
                
                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor components: " + e.getMessage(), e);
            }
        });

        // Set some text in the text area
        final String saveText = "This is text to be saved.";
        SwingUtilities.invokeAndWait(() -> {
            textArea[0].setText(saveText);
        });

        // Call the saveFile method using reflection
        SwingUtilities.invokeAndWait(() -> {
            try {
                Method saveFileMethod = textEditor[0].getClass().getDeclaredMethod("saveFile");
                saveFileMethod.setAccessible(true);
                saveFileMethod.invoke(textEditor[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call saveFile method: " + e.getMessage(), e);
            }
        });

        // Verify that the file was saved with the correct content
        String savedContent = new String(Files.readAllBytes(Paths.get(saveFilePath))).trim();
        assertEquals("Saved file should contain the text from the text area", saveText, savedContent);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
        
        // Delete the saved file
        saveFile.delete();
    }

    /**
     * Test the save as file functionality.
     */
    private static void testSaveAsFile() throws Exception {
        System.out.println("Testing save as file functionality...");

        // Create a temporary file path for saving
        String saveAsFilePath = System.getProperty("java.io.tmpdir") + "/texteditor_test_saveas.txt";
        File saveAsFile = new File(saveAsFilePath);
        if (saveAsFile.exists()) {
            saveAsFile.delete();
        }

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JTextArea[] textArea = new JTextArea[1];
        final JFileChooser[] fileChooser = new JFileChooser[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the text area using reflection
                Field textAreaField = textEditorClass.getDeclaredField("textArea");
                textAreaField.setAccessible(true);
                textArea[0] = (JTextArea) textAreaField.get(textEditor[0]);
                
                // Access the file chooser using reflection
                Field fileChooserField = textEditorClass.getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooser[0] = (JFileChooser) fileChooserField.get(textEditor[0]);
                
                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor components: " + e.getMessage(), e);
            }
        });

        // Set some text in the text area
        final String saveAsText = "This is text to be saved with save as.";
        SwingUtilities.invokeAndWait(() -> {
            textArea[0].setText(saveAsText);
        });

        // Mock the showSaveDialog method to return APPROVE_OPTION
        final JFileChooser originalFileChooser = fileChooser[0];
        final JFileChooser mockFileChooser = new JFileChooser() {
            @Override
            public int showSaveDialog(Component parent) {
                return JFileChooser.APPROVE_OPTION;
            }
            
            @Override
            public File getSelectedFile() {
                return new File(saveAsFilePath);
            }
        };
        
        SwingUtilities.invokeAndWait(() -> {
            try {
                Field fileChooserField = textEditor[0].getClass().getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(textEditor[0], mockFileChooser);
            } catch (Exception e) {
                throw new RuntimeException("Failed to mock file chooser: " + e.getMessage(), e);
            }
        });

        // Call the saveFileAs method using reflection
        SwingUtilities.invokeAndWait(() -> {
            try {
                Method saveFileAsMethod = textEditor[0].getClass().getDeclaredMethod("saveFileAs");
                saveFileAsMethod.setAccessible(true);
                saveFileAsMethod.invoke(textEditor[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call saveFileAs method: " + e.getMessage(), e);
            }
        });

        // Verify that the file was saved with the correct content
        String savedContent = new String(Files.readAllBytes(Paths.get(saveAsFilePath))).trim();
        assertEquals("Saved file should contain the text from the text area", saveAsText, savedContent);

        // Verify that the current file was updated
        final String[] currentFile = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                Field currentFileField = textEditor[0].getClass().getDeclaredField("currentFile");
                currentFileField.setAccessible(true);
                currentFile[0] = (String) currentFileField.get(textEditor[0]);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get current file: " + e.getMessage(), e);
            }
        });
        
        assertEquals("Current file should be updated to the save as file path", saveAsFilePath, currentFile[0]);

        // Restore the original file chooser
        SwingUtilities.invokeAndWait(() -> {
            try {
                Field fileChooserField = textEditor[0].getClass().getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(textEditor[0], originalFileChooser);
            } catch (Exception e) {
                throw new RuntimeException("Failed to restore file chooser: " + e.getMessage(), e);
            }
        });

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
        
        // Delete the saved file
        saveAsFile.delete();
    }

    /**
     * Helper method to close all open dialogs.
     */
    private static void closeAllDialogs() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                if (dialog.isVisible()) {
                    dialog.dispose();
                }
            }
        }
    }
}