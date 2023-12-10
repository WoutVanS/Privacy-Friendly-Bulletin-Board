import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Timer;

public class ClientGUI extends JFrame {
    private final Client client;
    private String selectedFriend;

    private Map<String, String> messageCash;
    private JTextArea chatTextArea;
    private JTextField messageTextField;
    private JButton sendButton;
    private JLabel nameLabel;

    public ClientGUI(Client client) {
        this.client = client;
        this.messageCash = new HashMap<>();

        setVisible(true);
        // Create and configure the main JFrame
        setTitle("Chat App: " + client.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a JPanel for the friends area
        JPanel friendsPanel = new JPanel(new BorderLayout());

        // Create a JLabel for "Friends" above the friends list
        JLabel friendsLabel = new JLabel("Friends");
        friendsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        friendsLabel.setOpaque(true);  // Make it opaque
        friendsLabel.setBackground(Color.LIGHT_GRAY); // Set background color
        friendsLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY)); // Set border
        friendsPanel.add(friendsLabel, BorderLayout.NORTH);

        // Create a JList for the friends on the left
        JList<String> friendsList = new JList<>(client.getFriends().toArray(new String[0]));
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setPreferredSize(new Dimension(150, 0));
        friendsScrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.DARK_GRAY)); // Set border
        friendsPanel.add(friendsScrollPane, BorderLayout.CENTER);

        add(friendsPanel, BorderLayout.WEST);

        // Create a JPanel for the chat area and input box on the right
        JPanel chatPanel = new JPanel(new BorderLayout());

        // Create a JLabel for the name of the friend above the chat area
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size to match friendsLabel
        nameLabel.setOpaque(true); // Make it opaque
        nameLabel.setBackground(Color.LIGHT_GRAY); // Set background color
        nameLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.DARK_GRAY)); // Set border
        chatPanel.add(nameLabel, BorderLayout.NORTH);

        // Create a JTextArea for the chat messages
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Create a JPanel for the message input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageTextField = new JTextField();
        inputPanel.add(messageTextField, BorderLayout.CENTER);
        sendButton = new JButton("Send");
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        // Add action listeners for the send button
        sendButton.addActionListener(e -> sendMessage());

        messageTextField.addActionListener(e -> sendMessage());

        // Add a listener for selecting a friend from the list
        friendsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                messageCash.put(selectedFriend, chatTextArea.getText());
                selectedFriend = friendsList.getSelectedValue();
                showChatWithFriend();
            }
        });

        // Select the first friend in the list when the frame opens
        if (!client.getFriends().isEmpty()) {
            friendsList.setSelectedIndex(0);
            selectedFriend = (String) client.getFriends().toArray()[0];
            messageCash.put(selectedFriend, chatTextArea.getText());
            showChatWithFriend();
        }

        // Create a Timer to execute the receiveMessage method every second
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    receiveMessage();
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                }
            }
        }, 0, 1000); // Execute every 1000 milliseconds (1 second)
    }


    private void sendMessage() {
        String message = messageTextField.getText();
        client.sendMessage(selectedFriend, message);
        chatTextArea.append("You: " + message + "\n");
        messageTextField.setText("");
    }

    private void receiveMessage() {
        String message = client.receiveMessage(selectedFriend);
        if(message != null)
            chatTextArea.append(selectedFriend + ": " + message + "\n");
    }

    private void showChatWithFriend() {
        nameLabel.setText("Chat with " + selectedFriend);
        chatTextArea.setText("");
        chatTextArea.append(messageCash.get(selectedFriend));
        // Implement the logic to display the chat with the selected friendf
        // Update the chatTextArea with the friend's messages here
    }
}
