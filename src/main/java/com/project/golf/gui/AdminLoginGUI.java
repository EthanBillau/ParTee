package com.project.golf.gui;

import com.project.golf.users.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AdminLoginGUI.java
 *
 * Admin login window to verify credentials and open server control panel.
 */
public class AdminLoginGUI extends JFrame implements ActionListener {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton, showHideButton;
    private final UserManager manager;

    public AdminLoginGUI() {
        manager = new UserManager();

        setTitle("Admin Login - Golf Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(34, 139, 34));

        JLabel title = new JLabel("Server Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(userLabel);

        usernameField = new JTextField(16);
        usernameField.setMaximumSize(new Dimension(250, usernameField.getPreferredSize().height * 2));
        usernameField.setMinimumSize(new Dimension(25, usernameField.getPreferredSize().height));
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(passLabel);

        passwordField = new JPasswordField(16);
        passwordField.setMaximumSize(new Dimension(250, usernameField.getPreferredSize().height * 2));
        passwordField.setMinimumSize(new Dimension(25, usernameField.getPreferredSize().height));        passwordField.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(passwordField);

        showHideButton = new JButton("Show");
        showHideButton.setAlignmentX(CENTER_ALIGNMENT);
        showHideButton.addActionListener(this);
        panel.add(showHideButton);

        panel.add(Box.createVerticalStrut(20));

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setMinimumSize(new Dimension(100, usernameField.getPreferredSize().height));
        loginButton.setMaximumSize(new Dimension(200, usernameField.getPreferredSize().height * 2));
        loginButton.addActionListener(this);
        panel.add(loginButton);

        // Add Enter key support
        usernameField.addActionListener(e -> loginButton.doClick());
        passwordField.addActionListener(e -> loginButton.doClick());
        getRootPane().setDefaultButton(loginButton);

        setContentPane(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            new Thread(() -> attemptLogin()).start();
        } else if (e.getSource() == showHideButton) {
            /**
             * Toggle password visibility for easier admin login.
             * Remove echo char to show plaintext, or add it back to hide.
             */
            if (showHideButton.getText().equals("Show")) {
                passwordField.setEchoChar((char) 0); // Show plaintext
                showHideButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•'); // Hide with bullets
                showHideButton.setText("Show");
            }
        }
    }

    private void attemptLogin() {
        String u = usernameField.getText();
        String p = new String(passwordField.getPassword());

        /**
         * Validate credentials through UserManager.
         * Even if login succeeds, verify the user has admin privileges.
         */
        if (manager.login(u, p)) {
            User user = manager.findUser(u);
            if (user.isAdmin()) {
                // Admin verified, open control panel
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new AdminControlGUI(manager);
                });
            } else {
                showError("You are not an admin."); // Regular user tried to login
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    private void showError(String msg) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, msg, "Login Failed", JOptionPane.ERROR_MESSAGE));
    }
}
