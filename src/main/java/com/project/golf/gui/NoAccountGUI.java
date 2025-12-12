package com.project.golf.gui;

import com.project.golf.client.Client;
import com.project.golf.utils.EmailSender;
import com.project.golf.utils.ServerConfig;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * NoAccountGUI.java
 *
 * Displays when user lacks account authorization and handles account requests.
 * Guides users to request account creation through email submission.
 *
 * Data structures: JButton for navigation and email submission, JTextField
 * for email address input.
 * Algorithm: Email validation and submission to admin system for account creation,
 * UI switching between login and account request screens.
 * Features: Account request submission, email validation, admin notification,
 * return to login navigation, email-based access control integration.
 *
 * @author Connor Landzettel (clandzet), L15
 *
 * @version December 4, 2025
 */

public class NoAccountGUI extends JFrame implements ActionListener {

    private JButton showLoginButton;  // button to return to login screen
    private JButton createAccountButton;  // button to create new account
    
    // Form fields for account creation
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    
    private Client client;

    public NoAccountGUI() {
        setTitle("Golf Course Reservation System - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 650);  // Increased size for signup form
        setLocationRelativeTo(null);
        
        // Initialize client connection
        String host = ServerConfig.getServerHost();
        int port = ServerConfig.getServerPort();
        client = new Client(host, port);
        try {
            client.connect(host, port);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }

        showSignupScreen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showLoginButton) {
            switchToLoginGUI();
        } else if (e.getSource() == createAccountButton) {
            handleAccountCreation();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoAccountGUI());
    }
    
    public void switchToLoginGUI() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }

    /**
     * Handle account creation
     * Validates form inputs and creates new user account
     */
    private void handleAccountCreation() {
        // Get form values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || 
            lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate username length
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this,
                "Username must be at least 3 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate password length
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email format
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create account
        createAccountButton.setEnabled(false);
        createAccountButton.setText("Creating...");
        
        new Thread(() -> {
            try {
                boolean success = client.addUser(username, password, firstName, lastName, email, false);
                
                SwingUtilities.invokeLater(() -> {
                    createAccountButton.setEnabled(true);
                    createAccountButton.setText("Create Account");
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Account created successfully! You can now log in.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        switchToLoginGUI();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to create account. Username or email may already exist.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    createAccountButton.setEnabled(true);
                    createAccountButton.setText("Create Account");
                    JOptionPane.showMessageDialog(this,
                        "Error connecting to server: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void showSignupScreen() {
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Create Account");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        panel.add(title);

        panel.add(Box.createVerticalStrut(20));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(CENTER_ALIGNMENT);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameLabel);
        
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));

        // Password field
        JLabel passwordLabel = new JLabel("Password (min 6 characters):");
        passwordLabel.setAlignmentX(CENTER_ALIGNMENT);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordLabel);
        
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));

        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setAlignmentX(CENTER_ALIGNMENT);
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(300, 35));
        confirmPasswordField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(10));

        // First name field
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setAlignmentX(CENTER_ALIGNMENT);
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(firstNameLabel);
        
        firstNameField = new JTextField(20);
        firstNameField.setMaximumSize(new Dimension(300, 35));
        firstNameField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(firstNameField);
        panel.add(Box.createVerticalStrut(10));

        // Last name field
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setAlignmentX(CENTER_ALIGNMENT);
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lastNameLabel);
        
        lastNameField = new JTextField(20);
        lastNameField.setMaximumSize(new Dimension(300, 35));
        lastNameField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lastNameField);
        panel.add(Box.createVerticalStrut(10));

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(CENTER_ALIGNMENT);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(emailLabel);
        
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(300, 35));
        emailField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(emailField);

        panel.add(Box.createVerticalStrut(20));

        // Create account button
        createAccountButton = new JButton("Create Account");
        createAccountButton.setAlignmentX(CENTER_ALIGNMENT);
        createAccountButton.setMinimumSize(new Dimension(200, 40));
        createAccountButton.setMaximumSize(new Dimension(250, 40));
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 16));
        createAccountButton.setBackground(new Color(40, 167, 69));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.addActionListener(this);
        panel.add(createAccountButton);

        panel.add(Box.createVerticalStrut(15));

        // Back to login button
        showLoginButton = new JButton("Back to Login");
        showLoginButton.setAlignmentX(CENTER_ALIGNMENT);
        showLoginButton.setMinimumSize(new Dimension(200, 35));
        showLoginButton.setMaximumSize(new Dimension(250, 35));
        showLoginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        showLoginButton.addActionListener(this);
        panel.add(showLoginButton);

        // Set default button for Enter key
        getRootPane().setDefaultButton(createAccountButton);

        setContentPane(panel);
        setVisible(true);
    }
}
