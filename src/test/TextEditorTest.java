package test;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Simple test class for the TextEditor application.
 * This is a manual test framework that doesn't require JUnit.
 */
public class TextEditorTest {
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
        System.out.println("Running TextEditor tests...");

        try {
            // Test that we can create a TextEditor instance
            testCreateTextEditor();

            // Test basic text editing
            testBasicTextEditing();

            System.out.println("All tests passed!");
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
     * Test that we can create a TextEditor instance.
     */
    private static void testCreateTextEditor() throws Exception {
        System.out.println("Testing TextEditor creation...");

        // Create the text editor on the EDT
        final JFrame[] editorFrame = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            // We can't directly access TextEditor here because it's in the default package
            // Instead, we'll use reflection to create it
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                editorFrame[0] = (JFrame) textEditorClass.newInstance();

                // Make it invisible for testing
                editorFrame[0].setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create TextEditor: " + e.getMessage(), e);
            }
        });

        assertNotNull("TextEditor frame should not be null", editorFrame[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            editorFrame[0].dispose();
        });
    }

    /**
     * Test basic text editing functionality.
     */
    private static void testBasicTextEditing() throws Exception {
        System.out.println("Testing basic text editing...");

        // Create the text editor on the EDT
        final JFrame[] editorFrame = new JFrame[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                editorFrame[0] = (JFrame) textEditorClass.newInstance();
                editorFrame[0].setVisible(false);

                // Find the text area component
                findTextAreaComponent(editorFrame[0], textArea);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create TextEditor: " + e.getMessage(), e);
            }
        });

        assertNotNull("TextArea component should not be null", textArea[0]);

        // Test setting text
        final String testText = "This is a test text.";
        SwingUtilities.invokeAndWait(() -> {
            textArea[0].setText(testText);
        });

        // Wait for EDT to process
        Thread.sleep(100);

        // Verify text was set
        final String[] actualText = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            actualText[0] = textArea[0].getText();
        });

        assertEquals("Text area should contain the test text", testText, actualText[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            editorFrame[0].dispose();
        });
    }

    /**
     * Helper method to find the JTextArea component in the frame.
     */
    private static void findTextAreaComponent(Container container, JTextArea[] result) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JTextArea) {
                result[0] = (JTextArea) component;
                return;
            } else if (component instanceof Container) {
                findTextAreaComponent((Container) component, result);
                if (result[0] != null) {
                    return;
                }
            }
        }
    }
}
