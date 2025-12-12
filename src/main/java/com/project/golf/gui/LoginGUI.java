package com.project.golf.gui;

import com.project.golf.client.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

/**
 * LoginGUI.java
 *
 * Primary authentication interface for ParTee golf reservation system.
 * Handles user login with username/password, password reset via one-time codes,
 * account creation for new users, and navigation to main application.
 *
 * Data structures: JTextField for credentials, JPasswordField for secure password input,
 * Session variables for one-time password recovery mechanism.
 * Algorithm: Validates credentials against server, manages user sessions, routes to appropriate GUI screens.
 * Features: Login, account creation, password recovery, show/hide password toggle.
 *
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), Anoushka Chakravarty (chakr181), L15
 *
 * @version December 6, 2025
 */

public class LoginGUI extends JFrame implements ActionListener {

    private static Client client;                  // client connection to server
    private String currUsername;                   // username of currently authenticating user

    private final String serverHost = "localhost";  // set to local host to run server client both locally by default
    // private final String serverHost = "serverIPgoesHere"; // to run server remotely, set to server IP

    private final int serverPort = 5050;               // server port number

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

        JLabel title = new JLabel("ParTee Login");
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
                passwordField.setEchoChar('\u2022'); // Mask with bullets -- changed to Unicode
                showHideButton.setText("Show");
            }
        } else if (e.getSource() == forgotPasswordButton) {
            handleForgotPassword();
        }
    }

    public static void main(String[] args) {
        client = new Client("localhost", 5050); // set to local host to run server client both locally by default
        // client = new Client("serverIPgoesHere", 5050); // to run server remotely, set to server IP
        try {
            client.connect("localhost", 5050); // set to local host to run server client both locally by default
            // client.connect("serverIPgoesHere", 5050); // to run server remotely, set to server IP
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
            String email = null;
            
            // Try to get email from server if connected
            if (client != null) {
                try {
                    String response = client.getUserEmail(username);
                    if (response != null && response.startsWith("RESP|OK|")) {
                        email = response.substring(8); // Remove "RESP|OK|"
                    }
                } catch (IOException ex) {
                    System.err.println("Error fetching user email from server: " + ex.getMessage());
                }
            }
            
            // Fallback to local database if server not available or failed
            if (email == null) {
                com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
                email = db.getUserEmail(username);
            }
            
            final String userEmail = email;
            
            if (userEmail == null || userEmail.isEmpty()) {
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
                userEmail,
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
            // One-time code is valid, clear it and prompt for new password
            oneTimeCode = null;
            oneTimeCodeUsername = null;
            currUsername = u;
            
            // Show password change dialog
            SwingUtilities.invokeLater(() -> showPasswordChangeDialog(u));
            return;
        }
        
        /**
         * Run login in background thread to avoid freezing the UI.
         * Connect to server and validate credentials.
         */
        new Thread(() -> {
            try {
                client.connect(serverHost, serverPort);
                String response = client.login(u, p);
                /**
                 * Switch back to UI thread for any GUI updates.
                 * This prevents threading issues with Swing.
                 */
                SwingUtilities.invokeLater(() -> {
                    if (response != null && response.startsWith("RESP|OK")) {
                        currUsername = u;
                        
                        // Check if user is logging in with temporary password
                        if (p.equals("TempPassword")) {
                            showPasswordChangeDialog(u);
                        } else {
                            switchToMainMenu();
                        }
                    } else if (response != null && response.contains("UNPAID")) {
                        /*
                         * User credentials are correct but account hasn't been paid.
                         * Extract the custom message from the response.
                         */
                        String message = "Your account has not been paid.\n" +
                                         "Please contact the golf course to complete payment.";
                        if (response.split("\\|").length > 3) {
                            message = response.split("\\|", 4)[3];
                        }
                        JOptionPane.showMessageDialog(this,
                            message,
                            "Payment Required",
                            JOptionPane.WARNING_MESSAGE);
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
    
    private void showPasswordChangeDialog(String username) {
        /**
         * Create a dialog for the user to set a new password after using one-time code.
         */
        JDialog passwordDialog = new JDialog(this, "Change Password", true);
        passwordDialog.setSize(400, 300);
        passwordDialog.setLocationRelativeTo(this);
        passwordDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel instructionLabel = new JLabel("Please enter a new password for your account:");
        instructionLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(newPasswordLabel);
        
        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setMaximumSize(new Dimension(250, 30));
        newPasswordField.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(newPasswordField);
        
        contentPanel.add(Box.createVerticalStrut(10));
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(confirmPasswordLabel);
        
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(250, 30));
        confirmPasswordField.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(confirmPasswordField);
        
        passwordDialog.add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");
        
        submitButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(passwordDialog,
                    "Password cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(passwordDialog,
                    "Passwords do not match.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password on server
            new Thread(() -> {
                try {
                    // Get current user data first
                    String response = client.getUser(username);
                    if (response != null && response.startsWith("RESP|OK|")) {
                        String userData = response.substring(8);
                        com.project.golf.users.User user = com.project.golf.users.User.fromFileString(userData);
                        
                        if (user != null) {
                            // Update password on server
                            boolean success = client.updateUser(
                                username,
                                username,
                                newPassword,
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail()
                            );
                            
                            SwingUtilities.invokeLater(() -> {
                                if (success) {
                                    JOptionPane.showMessageDialog(passwordDialog,
                                        "Password changed successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                                    passwordDialog.dispose();
                                    switchToMainMenu();
                                } else {
                                    JOptionPane.showMessageDialog(passwordDialog,
                                        "Failed to update password on server.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(passwordDialog,
                                    "Failed to retrieve user information.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            });
                        }
                    }
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(passwordDialog,
                            "Connection error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        });
        
        cancelButton.addActionListener(e -> {
            passwordDialog.dispose();
            // Don't log them in if they cancel
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        passwordDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        passwordDialog.setVisible(true);
    }
}
