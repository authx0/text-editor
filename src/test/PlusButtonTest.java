package test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test class for the plus button functionality in the TextEditor application.
 * This tests the plus button and its options menu.
 */
public class PlusButtonTest {
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
        System.out.println("Running TextEditor plus button tests...");

        try {
            // Test that the plus button exists
            testPlusButtonExists();

            // Test the options menu
            testOptionsMenuExists();

            // Test the upload image option
            testUploadImageOption();

            // Test the create spreadsheet option
            testCreateSpreadsheetOption();

            // Test the add code option
            testAddCodeOption();

            System.out.println("All plus button tests passed!");
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
     * Test that the plus button exists in the TextEditor.
     */
    private static void testPlusButtonExists() throws Exception {
        System.out.println("Testing plus button existence...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JButton[] plusButton = new JButton[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();

                // Access the plus button using reflection
                Field plusButtonField = textEditorClass.getDeclaredField("plusButton");
                plusButtonField.setAccessible(true);
                plusButton[0] = (JButton) plusButtonField.get(textEditor[0]);

                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor plus button: " + e.getMessage(), e);
            }
        });

        assertNotNull("Plus button should not be null", plusButton[0]);

        // Verify the plus button text
        final String[] buttonText = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            buttonText[0] = plusButton[0].getText();
        });

        assertEquals("Plus button should have '+' text", "+", buttonText[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test that the options menu exists.
     */
    private static void testOptionsMenuExists() throws Exception {
        System.out.println("Testing options menu existence...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JPopupMenu[] optionsMenu = new JPopupMenu[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();

                // Access the options menu using reflection
                Field optionsMenuField = textEditorClass.getDeclaredField("optionsMenu");
                optionsMenuField.setAccessible(true);
                optionsMenu[0] = (JPopupMenu) optionsMenuField.get(textEditor[0]);

                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor options menu: " + e.getMessage(), e);
            }
        });

        assertNotNull("Options menu should not be null", optionsMenu[0]);

        // Verify the options menu has items
        final int[] itemCount = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            itemCount[0] = optionsMenu[0].getComponentCount();
        });

        assertTrue("Options menu should have at least 3 items", itemCount[0] >= 3);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the upload image option.
     */
    private static void testUploadImageOption() throws Exception {
        System.out.println("Testing upload image option...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JPopupMenu[] optionsMenu = new JPopupMenu[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();

                // Access the options menu using reflection
                Field optionsMenuField = textEditorClass.getDeclaredField("optionsMenu");
                optionsMenuField.setAccessible(true);
                optionsMenu[0] = (JPopupMenu) optionsMenuField.get(textEditor[0]);

                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor options menu: " + e.getMessage(), e);
            }
        });

        // Find the upload image menu item
        final JMenuItem[] uploadImageItem = new JMenuItem[1];
        SwingUtilities.invokeAndWait(() -> {
            Component[] components = optionsMenu[0].getComponents();
            for (Component component : components) {
                if (component instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) component;
                    if (menuItem.getText().equals("Upload Image")) {
                        uploadImageItem[0] = menuItem;
                        break;
                    }
                }
            }
        });

        assertNotNull("Upload Image menu item should exist", uploadImageItem[0]);

        // Click the menu item
        SwingUtilities.invokeAndWait(() -> {
            uploadImageItem[0].doClick();
        });

        // Wait for the dialog to appear and close it
        Thread.sleep(500);
        closeAllDialogs();

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the create spreadsheet option.
     */
    private static void testCreateSpreadsheetOption() throws Exception {
        System.out.println("Testing create spreadsheet option...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JPopupMenu[] optionsMenu = new JPopupMenu[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();

                // Access the options menu using reflection
                Field optionsMenuField = textEditorClass.getDeclaredField("optionsMenu");
                optionsMenuField.setAccessible(true);
                optionsMenu[0] = (JPopupMenu) optionsMenuField.get(textEditor[0]);

                // Make it invisible for testing
                ((JFrame)textEditor[0]).setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access TextEditor options menu: " + e.getMessage(), e);
            }
        });

        // Find the create spreadsheet menu item
        final JMenuItem[] createSpreadsheetItem = new JMenuItem[1];
        SwingUtilities.invokeAndWait(() -> {
            Component[] components = optionsMenu[0].getComponents();
            for (Component component : components) {
                if (component instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) component;
                    if (menuItem.getText().equals("Create Spreadsheet")) {
                        createSpreadsheetItem[0] = menuItem;
                        break;
                    }
                }
            }
        });

        assertNotNull("Create Spreadsheet menu item should exist", createSpreadsheetItem[0]);

        // Click the menu item
        SwingUtilities.invokeAndWait(() -> {
            createSpreadsheetItem[0].doClick();
        });

        // Wait for the spreadsheet window to appear
        Thread.sleep(500);

        // Check if a spreadsheet window was opened
        final boolean[] spreadsheetWindowFound = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame && window != textEditor[0]) {
                    JFrame frame = (JFrame) window;
                    if (frame.getTitle().equals("Spreadsheet")) {
                        spreadsheetWindowFound[0] = true;
                        frame.dispose(); // Close the spreadsheet window
                    }
                }
            }
        });

        assertTrue("A spreadsheet window should be opened", spreadsheetWindowFound[0]);

        // Clean up
        SwingUtilities.invokeAndWait(() -> {
            ((JFrame)textEditor[0]).dispose();
        });
    }

    /**
     * Test the add code option.
     */
    private static void testAddCodeOption() throws Exception {
        System.out.println("Testing add code option...");

        // Create the text editor on the EDT
        final Object[] textEditor = new Object[1];
        final JPopupMenu[] optionsMenu = new JPopupMenu[1];
        final JTextArea[] textArea = new JTextArea[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> textEditorClass = Class.forName("TextEditor");
                textEditor[0] = textEditorClass.newInstance();

                // Access the options menu using reflection
                Field optionsMenuField = textEditorClass.getDeclaredField("optionsMenu");
                optionsMenuField.setAccessible(true);
                optionsMenu[0] = (JPopupMenu) optionsMenuField.get(textEditor[0]);

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

        // Find the add code menu item
        final JMenuItem[] addCodeItem = new JMenuItem[1];
        SwingUtilities.invokeAndWait(() -> {
            Component[] components = optionsMenu[0].getComponents();
            for (Component component : components) {
                if (component instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) component;
                    if (menuItem.getText().equals("Add Code")) {
                        addCodeItem[0] = menuItem;
                        break;
                    }
                }
            }
        });

        assertNotNull("Add Code menu item should exist", addCodeItem[0]);

        // Clear the text area
        SwingUtilities.invokeAndWait(() -> {
            textArea[0].setText("");
        });

        // Click the menu item
        SwingUtilities.invokeAndWait(() -> {
            addCodeItem[0].doClick();
        });

        // Verify that code snippet was added
        final String[] text = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            text[0] = textArea[0].getText();
        });

        assertEquals("Text area should contain code snippet", "// Add your code here\n", text[0]);

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
