package test;

/**
 * Main test runner class that runs all the tests for the TextEditor application.
 */
public class AllTests {
    /**
     * Main method to run all the tests.
     */
    public static void main(String[] args) {
        System.out.println("Running all TextEditor tests...");
        
        try {
            // Run the existing tests
            System.out.println("\n=== Running TextEditorTest ===");
            TextEditorTest.main(args);
            
            System.out.println("\n=== Running IntegrationTest ===");
            IntegrationTest.main(args);
            
            // Run the new tests
            System.out.println("\n=== Running ToolsTest ===");
            ToolsTest.main(args);
            
            System.out.println("\n=== Running PlusButtonTest ===");
            PlusButtonTest.main(args);
            
            System.out.println("\n=== Running FileOperationsTest ===");
            FileOperationsTest.main(args);
            
            System.out.println("\n=== Running EditOperationsTest ===");
            EditOperationsTest.main(args);
            
            System.out.println("\n=== All tests completed successfully! ===");
        } catch (Exception e) {
            System.err.println("TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}