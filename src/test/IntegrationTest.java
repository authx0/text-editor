package test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

/**
 * Integration test for the text editor application.
 * This test verifies that the application can be launched and used.
 */
public class IntegrationTest {
    /**
     * Main method to run the integration test.
     */
    public static void main(String[] args) {
        System.out.println("Running integration test...");
        
        try {
            // Test that the application can be launched
            testApplicationLaunch();
            
            System.out.println("Integration test passed!");
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
     * Test that the application can be launched.
     */
    private static void testApplicationLaunch() throws Exception {
        System.out.println("Testing application launch...");
        
        // Get the Main class
        Class<?> mainClass = Class.forName("Main");
        
        // Get the main method
        Method mainMethod = mainClass.getMethod("main", String[].class);
        
        // Create a flag to track if a TextEditor frame is created
        final boolean[] textEditorCreated = new boolean[1];
        
        // Add a window listener to detect when frames are created
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getSource() instanceof JFrame) {
                JFrame frame = (JFrame) event.getSource();
                if (frame.getTitle() != null && frame.getTitle().contains("Text Editor")) {
                    textEditorCreated[0] = true;
                    
                    // Close the frame to prevent it from staying open
                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                    });
                }
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
        
        // Launch the application
        mainMethod.invoke(null, (Object) new String[0]);
        
        // Wait for the frame to be created
        for (int i = 0; i < 50 && !textEditorCreated[0]; i++) {
            Thread.sleep(100);
        }
        
        // Verify that the TextEditor frame was created
        if (!textEditorCreated[0]) {
            throw new AssertionError("TextEditor frame was not created");
        }
        
        System.out.println("PASS: TextEditor frame was created");
    }
}