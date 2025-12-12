package com.project.golf.gui;

import com.project.golf.client.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

/**
 * LoginGUI.java
 *
 * GUI for customer users Login, menu, and booking/reservation managment
 *
 * @author Connor Landzettel (clandzet), L15
 *
 * @version 12/4/2025
 */

public class LoginGUI extends JFrame implements ActionListener {

    private static Client client;
    private String currUsername;
    private final String serverHost = "localhost";
    private final int serverPort = 5050;

    private JButton loginButton;
    private JButton signupButton;
    private JButton showHideButton;
    private JButton forgotPasswordButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String oneTimeCode = null;
    private String oneTimeCodeUsername = null;

    public LoginGUI() {
        setTitle("Golf Course Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Golf Registration Login");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title);
        title.setForeground(Color.WHITE);

        panel.add(Box.createVerticalStrut(10));

        JLabel usernameText = new JLabel("Username:");
        usernameField = new JTextField(16);
        usernameField.setMaximumSize(new Dimension(250, usernameField.getPreferredSize().height * 2));
        usernameField.setMinimumSize(new Dimension(25, usernameField.getPreferredSize().height));
        usernameText.setAlignmentX(CENTER_ALIGNMENT);
        usernameText.setForeground(Color.WHITE);
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(usernameText);
        panel.add(usernameField);

        JLabel passwordText = new JLabel("Password:");
        passwordField = new JPasswordField(16);
        passwordField.setMaximumSize(new Dimension(250, usernameField.getPreferredSize().height * 2));
        passwordField.setMinimumSize(new Dimension(25, usernameField.getPreferredSize().height));
        passwordText.setAlignmentX(CENTER_ALIGNMENT);
        passwordText.setForeground(Color.WHITE);
        passwordField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(passwordText);
        panel.add(passwordField);

        JPanel passwordButtonPanel = new JPanel();
        passwordButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        passwordButtonPanel.setOpaque(false);
        passwordButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        showHideButton = new JButton("Show");
        showHideButton.addActionListener(this);
        passwordButtonPanel.add(showHideButton);
        
        forgotPasswordButton = new JButton("Forgot Password");
        forgotPasswordButton.addActionListener(this);
        passwordButtonPanel.add(forgotPasswordButton);
        
        panel.add(passwordButtonPanel);
        panel.add(Box.createVerticalStrut(20));

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setMinimumSize(new Dimension(100, usernameField.getPreferredSize().height));
        loginButton.setMaximumSize(new Dimension(200, usernameField.getPreferredSize().height * 2));
        loginButton.addActionListener(this);
        panel.add(loginButton);

        panel.add(Box.createVerticalStrut(10));

        signupButton = new JButton("Sign Up");
        signupButton.setAlignmentX(CENTER_ALIGNMENT);
        signupButton.setMinimumSize(new Dimension(100, usernameField.getPreferredSize().height));
        signupButton.setMaximumSize(new Dimension(200, usernameField.getPreferredSize().height * 2));
        signupButton.addActionListener(this);
        panel.add(signupButton);

        // Add Enter key support
        usernameField.addActionListener(e -> loginButton.doClick());
        passwordField.addActionListener(e -> loginButton.doClick());
        getRootPane().setDefaultButton(loginButton);

        setContentPane(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            switchToNoAccountGUI();
        } else if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            attemptLogin(username, password);
        } else if (e.getSource() == showHideButton) {
            /**
             * Toggle password visibility.
             * If currently hidden, reveal the password by removing the echo char.
             * If currently shown, hide it again with bullet points.
             */
            if (showHideButton.getText().equals("Show")) {
                passwordField.setEchoChar((char) 0); // Removes masking
                showHideButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•'); // Mask with bullets
                showHideButton.setText("Show");
            }
        } else if (e.getSource() == forgotPasswordButton) {
            handleForgotPassword();
        }
    }

    public static void main(String[] args) {
        client = new Client("localhost", 5050);
        try {
            client.connect("localhost", 5050);
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> new LoginGUI());
    }

    public void switchToNoAccountGUI() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new NoAccountGUI());
    }

    // Switch to main menu for the specific logged-in user
    public void switchToMainMenuGUI(String username) {
        this.dispose();
        SwingUtilities.invokeLater(() -> new MainMenuGUI(username, client));
    }

    private void handleForgotPassword() {
        /**
         * Prompt user for their username to send password reset code.
         * Validates that user exists before sending code.
         */
        String inputUsername = JOptionPane.showInputDialog(this,
            "Enter your username:",
            "Forgot Password",
            JOptionPane.QUESTION_MESSAGE);
        
        if (inputUsername == null || inputUsername.trim().isEmpty()) {
            return; // User cancelled
        }
        
        final String username = inputUsername.trim();
        
        /**
         * Get user's email from database.
         * If user doesn't exist or has no email, show error.
         */
        new Thread(() -> {
            com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
            String email = db.getUserEmail(username);
            
            if (email == null || email.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Username not found or no email on file.\nPlease contact an administrator.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
                return;
            }
            
            /**
             * Generate 6-digit one-time code.
             * Store it for validation when user tries to log in.
             */
            oneTimeCode = String.format("%06d", (int)(Math.random() * 1000000));
            oneTimeCodeUsername = username;
            
            /**
             * Send email with one-time code.
             * Display message based on success/failure.
             */
            boolean emailSent = com.project.golf.utils.EmailSender.sendEmail(
                email,
                "Password Reset Code - Par-Tee Golf",
                "Your one-time login code is: " + oneTimeCode + "\n\n" +
                "Enter this code in the password field to log in.\n" +
                "This code will expire after one use.\n\n" +
                "If you did not request this code, please ignore this email."
            );
            
            SwingUtilities.invokeLater(() -> {
                if (emailSent) {
                    JOptionPane.showMessageDialog(this,
                        "A one-time login code has been sent to your email.\n" +
                        "Please check your email and enter the code in the password field.",
                        "Code Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                    usernameField.setText(username);
                    passwordField.setText("");
                    passwordField.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to send email. Please try again or contact support.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }
    
    private void attemptLogin(String u, String p) {
        /**
         * Check if user is trying to log in with one-time code.
         * If code matches, allow login without checking password.
         */
        if (oneTimeCode != null && oneTimeCodeUsername != null && 
            oneTimeCodeUsername.equals(u) && oneTimeCode.equals(p)) {
            // One-time code is valid, clear it and log in
            oneTimeCode = null;
            oneTimeCodeUsername = null;
            currUsername = u;
            SwingUtilities.invokeLater(() -> switchToMainMenu());
            return;
        }
        
        /**
         * Run login in background thread to avoid freezing the UI.
         * Connect to server and validate credentials.
         */
        new Thread(() -> {
            try {
                client.connect(serverHost, serverPort);
                boolean ok = client.login(u, p);
                /**
                 * Switch back to UI thread for any GUI updates.
                 * This prevents threading issues with Swing.
                 */
                SwingUtilities.invokeLater(() -> {
                    if (ok) {
                        currUsername = u;
                        switchToMainMenu();
                    } else {
                        /**
                         * Show error dialog with two options.
                         * User can either try logging in again or go to the no account screen.
                         */
                        int choice = JOptionPane.showOptionDialog(this,
                            "Invalid username or password.\nIf you don't have an account, please contact ParTEE.",
                            "Login Failed",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new String[]{"Try Again", "No Account"},
                            "Try Again");
                        
                        if (choice == 1) { // Second button clicked
                            switchToNoAccountGUI();
                        }
                    }
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Unable to connect to server: " + ex.getMessage() + 
                        "\nPlease make sure the server is running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private void switchToMainMenu() {
        // Delegate to switchToMainMenuGUI to ensure client is passed
        switchToMainMenuGUI(currUsername);
    }
}
