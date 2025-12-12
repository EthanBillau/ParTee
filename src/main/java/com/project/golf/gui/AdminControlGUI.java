package com.project.golf.gui;

import com.project.golf.users.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AdminControlGUI.java
 *
 * Admin panel to start/stop server and add users.
 */
public class AdminControlGUI extends JFrame implements ActionListener {
    private final ServerController controller;
    private final UserManager manager;

    private final JButton startButton, stopButton, addUserButton;
    private final JLabel serverStatus;
    private final JTextField usernameField, passwordField, firstNameField, lastNameField, emailField;
    private final JCheckBox hasPaidCheck;

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

        // Add User Panel
        JPanel userPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("Add User"));

        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        userPanel.add(usernameField);

        userPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
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
            JOptionPane.showMessageDialog(this, "User added successfully!");
            usernameField.setText("");
            passwordField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            hasPaidCheck.setSelected(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user (username may exist).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
