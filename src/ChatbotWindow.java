import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple chatbot window for interacting with a basic chatbot.
 */
public class ChatbotWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;

    public ChatbotWindow() {
        super("Chatbot");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> handleUserInput());
    }

    private void handleUserInput() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) {
            return;
        }
        appendMessage("You: " + userText);
        inputField.setText("");

        String response = generateResponse(userText);
        appendMessage("Bot: " + response);
    }

    private void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    // Very basic chatbot logic. For now it simply echoes the input with a greeting.
    private String generateResponse(String input) {
        return "You said '" + input + "'.";
    }
}
