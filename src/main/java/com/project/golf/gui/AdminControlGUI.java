package com.project.golf.gui;

import com.project.golf.users.UserManager;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * AdminControlGUI.java
 *
 * Administrative control panel for server and user management operations.
 * Enables server start/stop, user creation, and event approval functions.
 *
 * Data structures: ServerController for server management, UserManager for user operations,
 * JButton array for admin actions, JTextField/JPasswordField/JCheckBox for user creation form,
 * JLabel for status display.
 * Algorithm: Event-driven button callbacks for server control and user administration,
 * form validation for new user creation, status indicator updates.
 * Features: Server startup/shutdown, user account creation with role assignment,
 * event approval interface, payment status management, admin operation logging.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */
    
public class AdminControlGUI extends JFrame implements ActionListener {
    private final ServerController controller;  // controller for server operations
    private final UserManager manager;  // manager for user administration

    private final JButton startButton;  // button to start server
    private final JButton stopButton;  // button to stop server
    private final JButton addUserButton;  // button to add new user account
    private final JButton manageEventsButton;  // button to manage event approvals
    private final JLabel serverStatus;  // label showing server online/offline status
    private final JTextField usernameField;  // input field for new user username
    private final JTextField passwordField;  // input field for new user password
    private final JTextField firstNameField;  // input field for new user first name
    private final JTextField lastNameField;  // input field for new user last name
    private final JTextField emailField;  // input field for new user email
    private final JCheckBox hasPaidCheck;  // checkbox for new user payment status

    public AdminControlGUI(UserManager manager) {
        this.manager = manager;
        controller = new ServerController();

        setTitle("Admin Control Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Server Control Panel");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        // Server buttons
        JPanel serverPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        serverPanel.add(startButton);
        serverPanel.add(stopButton);
        panel.add(serverPanel);

        serverStatus = new JLabel("Server: STOPPED");
        serverStatus.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(serverStatus);
        panel.add(Box.createVerticalStrut(20));
        
        // Manage Events Button
        manageEventsButton = new JButton("Manage Pending Events");
        manageEventsButton.addActionListener(this);
        manageEventsButton.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(manageEventsButton);
        panel.add(Box.createVerticalStrut(20));

        // Add User Panel
        JPanel userPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("Add User"));

        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        userPanel.add(usernameField);

        userPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        passwordField.setText("TempPassword"); // Default temporary password
        userPanel.add(passwordField);

        userPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        userPanel.add(firstNameField);

        userPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        userPanel.add(lastNameField);

        userPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        userPanel.add(emailField);

        hasPaidCheck = new JCheckBox("Has Paid");
        userPanel.add(hasPaidCheck);

        addUserButton = new JButton("Add User");
        addUserButton.addActionListener(this);
        userPanel.add(addUserButton);

        panel.add(userPanel);

        setContentPane(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startServer();
        } else if (e.getSource() == stopButton) {
            stopServer();
        } else if (e.getSource() == addUserButton) {
            addUser();
        } else if (e.getSource() == manageEventsButton) {
            openEventManagementDialog();
        }
    }

    private void startServer() {
        startButton.setEnabled(false);
        controller.startServer(5050, () -> serverStatus.setText("Server: RUNNING"));
    }

    private void stopServer() {
        controller.stopServer();
        serverStatus.setText("Server: STOPPED");
        startButton.setEnabled(true);
    }

    private void addUser() {
        String u = usernameField.getText();
        String p = passwordField.getText();
        String fn = firstNameField.getText();
        String ln = lastNameField.getText();
        String em = emailField.getText();
        boolean paid = hasPaidCheck.isSelected();

        boolean ok = manager.addUser(u, p, fn, ln, em, paid);
        if (ok) {
            // Send welcome email to new user
            new Thread(() -> {
                String emailBody = String.format(
                    "Welcome to ParTee Golf!\n\n" +
                    "Your account has been created with the following information:\n\n" +
                    "Username: %s\n" +
                    "Temporary Password: %s\n" +
                    "First Name: %s\n" +
                    "Last Name: %s\n" +
                    "Email: %s\n\n" +
                    "IMPORTANT: For security reasons, you must change your password when you first log in.\n\n" +
                    "You can now log in to the golf course reservation system at your convenience.\n\n" +
                    "Thank you,\n" +
                    "ParTee Golf Team",
                    u, p, fn, ln, em
                );
                
                boolean emailSent = com.project.golf.utils.EmailSender.sendEmail(
                    em,
                    "Welcome to ParTee Golf - Account Created",
                    emailBody
                );
                
                SwingUtilities.invokeLater(() -> {
                    if (emailSent) {
                        JOptionPane.showMessageDialog(this, 
                            "User added successfully!\nWelcome email sent to " + em);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "User added successfully!\nHowever, email could not be sent.");
                    }
                });
            }).start();
            
            usernameField.setText("");
            passwordField.setText("TempPassword");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            hasPaidCheck.setSelected(false);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to add user (username may exist).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openEventManagementDialog() {
        new EventApprovalGUI(this).setVisible(true);
    }
}
