package com.project.golf.gui;

import com.project.golf.utils.EmailSender;

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
    private JButton submitEmailButton;  // button to submit account request
    private JTextField emailField;  // input field for requesting user email

    public NoAccountGUI() {
        setTitle("Golf Course Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        showNoLoginScreen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showLoginButton) {
            switchToLoginGUI();
        } else if (e.getSource() == submitEmailButton) {
            handleEmailSubmission();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoAccountGUI());
    }
    
    public void switchToLoginGUI() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }

    private void handleEmailSubmission() {
        String email = emailField.getText().trim();
        
        /**
         * Basic email validation.
         * Check if field is not empty and contains @ symbol.
         */
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your email address.",
                "Email Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Invalid Email",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        /**
         * Send email in a background thread to prevent UI freezing.
         * Shows a "Sending..." message while the email is being sent.
         */
        submitEmailButton.setEnabled(false);
        submitEmailButton.setText("Sending...");
        
        new Thread(() -> {
            boolean success = EmailSender.sendHelloWorldEmail(email);
            
            /**
             * Update UI on the Event Dispatch Thread after email attempt.
             * Shows success or failure message based on email send result.
             */
            SwingUtilities.invokeLater(() -> {
                submitEmailButton.setEnabled(true);
                submitEmailButton.setText("Submit Email");
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Thank you! Please check your email for instructions on creating an account.",
                        "Request Submitted",
                        JOptionPane.INFORMATION_MESSAGE);
                    emailField.setText(""); // Clear the field
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to send email. Please try again later or contact support.",
                        "Email Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    private void showNoLoginScreen() {
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Golf Registration Signup");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title);
        title.setForeground(Color.WHITE);

        panel.add(Box.createVerticalStrut(20));

        JLabel noAccountLabel = new JLabel("You do not have an account.");
        noAccountLabel.setAlignmentX(CENTER_ALIGNMENT);
        noAccountLabel.setForeground(Color.WHITE);
        panel.add(noAccountLabel);

        panel.add(Box.createVerticalStrut(20));

        // Create account section
        JLabel emailLabel = new JLabel("Request Account Access:");
        emailLabel.setAlignmentX(CENTER_ALIGNMENT);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(emailLabel);

        panel.add(Box.createVerticalStrut(10));

        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(300, 30));
        emailField.setAlignmentX(CENTER_ALIGNMENT);
        emailField.addActionListener(e -> submitEmailButton.doClick()); // Allow Enter key
        panel.add(emailField);

        panel.add(Box.createVerticalStrut(10));

        submitEmailButton = new JButton("Submit Email");
        submitEmailButton.setAlignmentX(CENTER_ALIGNMENT);
        submitEmailButton.setMinimumSize(new Dimension(150, 35));
        submitEmailButton.setMaximumSize(new Dimension(200, 35));
        submitEmailButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitEmailButton.addActionListener(this);
        panel.add(submitEmailButton);

        panel.add(Box.createVerticalStrut(20));

        // Separator
        JLabel orLabel = new JLabel("- OR -");
        orLabel.setAlignmentX(CENTER_ALIGNMENT);
        orLabel.setForeground(Color.WHITE);
        panel.add(orLabel);

        panel.add(Box.createVerticalStrut(20));

        showLoginButton = new JButton("Go to Login");
        showLoginButton.setAlignmentX(CENTER_ALIGNMENT);
        showLoginButton.setMinimumSize(new Dimension(150, 35));
        showLoginButton.setMaximumSize(new Dimension(200, 35));
        showLoginButton.addActionListener(this);
        panel.add(showLoginButton);

        // Set default button for Enter key
        getRootPane().setDefaultButton(submitEmailButton);

        setContentPane(panel);
        setVisible(true);
    }
}
