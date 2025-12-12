package com.project.golf.gui;

import com.project.golf.client.Client;
import com.project.golf.users.User;
import com.project.golf.users.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * AccountOptionsGUI.java
 *
 * GUI for customer users to edit account details.
 *
 * Uses AccountBackgroundPanel for a properly scaled background image.
 *
 * @author Connor Landzettel (clandzet), L15
 * @version 12/4/2025
 */
public class AccountOptionsGUI extends JFrame implements ActionListener {

    private static Client client;
    private String currUsername;
    private String serverHost = "localhost";
    private int serverPort = 5050;

    private User currentUser;
    private UserManager userManager;

    private JButton changeUsernameButton;
    private JButton changePasswordButton;
    private JButton changeFirstNameButton;
    private JButton changeLastNameButton;
    private JButton changeEmailButton;
    private JButton backButton;

    private JTextField usernameField;
    private JPasswordField confirmPasswordField; // current password (for verification)
    private JTextField passwordField;            // new password
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;

    private String username;

    public AccountOptionsGUI(String username, Client client) {
        this.username = username;
        AccountOptionsGUI.client = client;
        this.currUsername = username;

        setTitle("Golf Course Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load user data
        userManager = new UserManager();
        currentUser = userManager.findUser(username);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "User '" + username + "' not found.",
                    "User Not Found",
                    JOptionPane.WARNING_MESSAGE);
        }

        showAccountOptionsScreen();
    }

    // No-arg constructor for compatibility / testing
    public AccountOptionsGUI() {
        this("defaultUser", null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == backButton) {
            switchToMainMenuGUI();
        } else if (src == changeUsernameButton) {
            handleChangeUsername();
        } else if (src == changePasswordButton) {
            handleChangePassword();
        } else if (src == changeFirstNameButton) {
            handleChangeFirstName();
        } else if (src == changeLastNameButton) {
            handleChangeLastName();
        } else if (src == changeEmailButton) {
            handleChangeEmail();
        }
    }

    private boolean ensureUserLoaded() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "No user loaded.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveUsers() {
        try {
            userManager.saveUsersToFile();
        } catch (IOException ioEx) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save user data: " + ioEx.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChangeUsername() {
        if (!ensureUserLoaded()) return;

        String newU = usernameField.getText().trim();
        if (newU.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User existing = userManager.findUser(newU);
        if (existing != null && existing != currentUser) {
            JOptionPane.showMessageDialog(this,
                    "Username already taken.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setUsername(newU);
        // update local fields so going back uses the new username
        this.username = newU;
        this.currUsername = newU;

        saveUsers();
    }

    private void handleChangePassword() {
        if (!ensureUserLoaded()) return;

        String currentPwd = new String(confirmPasswordField.getPassword());
        if (!currentPwd.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this,
                    "Current password does not match.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newPwd = passwordField.getText();
        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "New password cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setPassword(newPwd);
        saveUsers();
    }

    private void handleChangeFirstName() {
        if (!ensureUserLoaded()) return;
        currentUser.setFirstName(firstNameField.getText());
        saveUsers();
    }

    private void handleChangeLastName() {
        if (!ensureUserLoaded()) return;
        currentUser.setLastName(lastNameField.getText());
        saveUsers();
    }

    private void handleChangeEmail() {
        if (!ensureUserLoaded()) return;
        currentUser.setEmail(emailField.getText());
        saveUsers();
    }

    public static void main(String[] args) {
        client = new Client("localhost", 5050);
        try {
            client.connect("localhost", 5050);
        } catch (Exception e) {
            // ignore connection errors in standalone testing
        }

        SwingUtilities.invokeLater(() ->
                new AccountOptionsGUI("defaultUser", client));
    }

    public void switchToMainMenuGUI() {
        System.out.println("Switching to Main Menu GUI");
        this.dispose();
        SwingUtilities.invokeLater(() -> new MainMenuGUI(username, client));
    }

    private void showAccountOptionsScreen() {
        // Use the special background panel that keeps the image scaling you want
        AccountBackgroundPanel panel = new AccountBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Account Options");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title);

        panel.add(Box.createVerticalStrut(10));

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(CENTER_ALIGNMENT);
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel);

        usernameField = new JTextField(16);
        usernameField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        usernameField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
        }
        panel.add(usernameField);

        changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setAlignmentX(CENTER_ALIGNMENT);
        changeUsernameButton.addActionListener(this);
        panel.add(changeUsernameButton);

        panel.add(Box.createVerticalStrut(10));

        // Password
        JLabel passwordLabel = new JLabel("New Password:");
        passwordLabel.setAlignmentX(CENTER_ALIGNMENT);
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel);

        passwordField = new JTextField(16);
        passwordField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        passwordField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        panel.add(passwordField);

        JLabel confirmLabel = new JLabel("Current Password:");
        confirmLabel.setAlignmentX(CENTER_ALIGNMENT);
        confirmLabel.setForeground(Color.WHITE);
        panel.add(confirmLabel);

        confirmPasswordField = new JPasswordField(16);
        confirmPasswordField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        confirmPasswordField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        panel.add(confirmPasswordField);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(CENTER_ALIGNMENT);
        changePasswordButton.addActionListener(this);
        panel.add(changePasswordButton);

        panel.add(Box.createVerticalStrut(10));

        // First name
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setAlignmentX(CENTER_ALIGNMENT);
        firstNameLabel.setForeground(Color.WHITE);
        panel.add(firstNameLabel);

        firstNameField = new JTextField(16);
        firstNameField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        firstNameField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        if (currentUser != null) {
            firstNameField.setText(currentUser.getFirstName());
        }
        panel.add(firstNameField);

        changeFirstNameButton = new JButton("Change First Name");
        changeFirstNameButton.setAlignmentX(CENTER_ALIGNMENT);
        changeFirstNameButton.addActionListener(this);
        panel.add(changeFirstNameButton);

        panel.add(Box.createVerticalStrut(10));

        // Last name
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setAlignmentX(CENTER_ALIGNMENT);
        lastNameLabel.setForeground(Color.WHITE);
        panel.add(lastNameLabel);

        lastNameField = new JTextField(16);
        lastNameField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        lastNameField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        if (currentUser != null) {
            lastNameField.setText(currentUser.getLastName());
        }
        panel.add(lastNameField);

        changeLastNameButton = new JButton("Change Last Name");
        changeLastNameButton.setAlignmentX(CENTER_ALIGNMENT);
        changeLastNameButton.addActionListener(this);
        panel.add(changeLastNameButton);

        panel.add(Box.createVerticalStrut(10));

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(CENTER_ALIGNMENT);
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel);

        emailField = new JTextField(16);
        emailField.setMaximumSize(
                new Dimension(250, usernameField.getPreferredSize().height * 2));
        emailField.setMinimumSize(
                new Dimension(25, usernameField.getPreferredSize().height));
        if (currentUser != null) {
            emailField.setText(currentUser.getEmail());
        }
        panel.add(emailField);

        changeEmailButton = new JButton("Change Email");
        changeEmailButton.setAlignmentX(CENTER_ALIGNMENT);
        changeEmailButton.addActionListener(this);
        panel.add(changeEmailButton);

        panel.add(Box.createVerticalStrut(10));

        // Back button
        backButton = new JButton("Back to Main Menu");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(this);
        panel.add(backButton);

        setContentPane(panel);

        // Now the preferred size comes from AccountBackgroundPanel's
        // image+children logic, so the image keeps its scaling.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
