import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 * Main class that launches the text editor application.
 */
public class Main {
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and display the text editor
        SwingUtilities.invokeLater(() -> new TextEditor());
    }
}
