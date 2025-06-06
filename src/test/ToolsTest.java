package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test class for the tools functionality in the TextEditor application.
 * This tests the draw, text, highlight, erase, and select tools.
 */
public class ToolsTest {
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

    /**
     * Main method to run the tests.
     */
    public static void main(String[] args) {
        System.out.println("Running TextEditor tools tests...");

        try {
            // Test that we can access the tools
            testToolsExist();
            
            // Test each tool
            testDrawTool();
            testTextTool();
            testHighlightTool();
            testEraseTool();
            testSelectTool();

            System.out.println("All tools tests passed!");
        } catch (AssertionError e) {
            System.err.println("TEST FAILED: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Test that the tools exist in the TextEditor.
     */
    private static void testToolsExist() throws Exception {
        System.out.println("Testing tools existence...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] drawButton = new JButton[1];
        final JButton[] textButton = new JButton[1];
        final JButton[] highlightButton = new JButton[1];
        final JButton[] eraseButton = new JButton[1];
        final JButton[] selectButton = new JButton[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the tool buttons using reflection
                Field drawButtonField = textEditorClass.getDeclaredField("drawButton");
                drawButtonField.setAccessible(true);
                drawButton[0] = (JButton) drawButtonField.get(textEditor[0]);
                
                Field textButtonField = textEditorClass.getDeclaredField("textButton");
                textButtonField.setAccessible(true);
                textButton[0] = (JButton) textButtonField.get(textEditor[0]);
                
                Field highlightButtonField = textEditorClass.getDeclaredField("highlightButton");
                highlightButtonField.setAccessible(true);
                highlightButton[0] = (JButton) highlightButtonField.get(textEditor[0]);
                
                Field eraseButtonField = textEditorClass.getDeclaredField("eraseButton");
                eraseButtonField.setAccessible(true);
                eraseButton[0] = (JButton) eraseButtonField.get(textEditor[0]);
                
                Field selectButtonField = textEditorClass.getDeclaredField("selectButton");
                selectButtonField.setAccessible(true);
                selectButton[0] = (JButton) selectButtonField.get(textEditor[0]);
                
                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor tools: " + e.getMessage(), e);
            }
        });

        assertNotNull("Draw button should not be null", drawButton[0]);
        assertNotNull("Text button should not be null", textButton[0]);
        assertNotNull("Highlight button should not be null", highlightButton[0]);
        assertNotNull("Erase button should not be null", eraseButton[0]);
        assertNotNull("Select button should not be null", selectButton[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the draw tool functionality.
     */
    private static void testDrawTool() throws Exception {
        System.out.println("Testing draw tool...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] drawButton = new JButton[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the draw button using reflection
                Field drawButtonField = textEditorClass.getDeclaredField("drawButton");
                drawButtonField.setAccessible(true);
                drawButton[0] = (JButton) drawButtonField.get(textEditor[0]);
                
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

        // Click the draw button to activate the tool
        SwingUtilities.invokeAndWait(() -> {
            drawButton[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Verify the cursor is changed to crosshair
        final int[] cursorType = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            cursorType[0] = textArea[0].getCursor().getType();
        });

        assertEquals("Cursor should be crosshair when draw tool is active", 
                     Cursor.CROSSHAIR_CURSOR, cursorType[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the text tool functionality.
     */
    private static void testTextTool() throws Exception {
        System.out.println("Testing text tool...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] textButton = new JButton[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the text button using reflection
                Field textButtonField = textEditorClass.getDeclaredField("textButton");
                textButtonField.setAccessible(true);
                textButton[0] = (JButton) textButtonField.get(textEditor[0]);
                
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

        // Click the text button to activate the tool
        SwingUtilities.invokeAndWait(() -> {
            textButton[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Verify the cursor is changed to text cursor
        final int[] cursorType = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            cursorType[0] = textArea[0].getCursor().getType();
        });

        assertEquals("Cursor should be text cursor when text tool is active", 
                     Cursor.TEXT_CURSOR, cursorType[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the highlight tool functionality.
     */
    private static void testHighlightTool() throws Exception {
        System.out.println("Testing highlight tool...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] highlightButton = new JButton[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the highlight button using reflection
                Field highlightButtonField = textEditorClass.getDeclaredField("highlightButton");
                highlightButtonField.setAccessible(true);
                highlightButton[0] = (JButton) highlightButtonField.get(textEditor[0]);
                
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

        // Click the highlight button to activate the tool
        SwingUtilities.invokeAndWait(() -> {
            highlightButton[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Verify the cursor is changed to hand cursor
        final int[] cursorType = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            cursorType[0] = textArea[0].getCursor().getType();
        });

        assertEquals("Cursor should be hand cursor when highlight tool is active", 
                     Cursor.HAND_CURSOR, cursorType[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the erase tool functionality.
     */
    private static void testEraseTool() throws Exception {
        System.out.println("Testing erase tool...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] eraseButton = new JButton[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the erase button using reflection
                Field eraseButtonField = textEditorClass.getDeclaredField("eraseButton");
                eraseButtonField.setAccessible(true);
                eraseButton[0] = (JButton) eraseButtonField.get(textEditor[0]);
                
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

        // Click the erase button to activate the tool
        SwingUtilities.invokeAndWait(() -> {
            eraseButton[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Verify the cursor is changed to hand cursor
        final int[] cursorType = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            cursorType[0] = textArea[0].getCursor().getType();
        });

        assertEquals("Cursor should be hand cursor when erase tool is active", 
                     Cursor.HAND_CURSOR, cursorType[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the select tool functionality.
     */
    private static void testSelectTool() throws Exception {
        System.out.println("Testing select tool...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] selectButton = new JButton[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();
                
                // Access the select button using reflection
                Field selectButtonField = textEditorClass.getDeclaredField("selectButton");
                selectButtonField.setAccessible(true);
                selectButton[0] = (JButton) selectButtonField.get(textEditor[0]);
                
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

        // Click the select button to activate the tool
        SwingUtilities.invokeAndWait(() -> {
            selectButton[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Verify the cursor is changed to default cursor
        final int[] cursorType = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            cursorType[0] = textArea[0].getCursor().getType();
        });

        assertEquals("Cursor should be default cursor when select tool is active", 
                     Cursor.DEFAULT_CURSOR, cursorType[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
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