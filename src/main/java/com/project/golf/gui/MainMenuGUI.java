package com.project.golf.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.project.golf.client.Client;

/**
 * MainMenuGUI.java
 *
 * Main menu screen with three navigation options
 *
 * @author Anoushka Chakravarty (chakr181), L15
 *
 * @version 12/04/2025
 */
public class MainMenuGUI extends JFrame implements ActionListener {

    private JButton makeReservationButton;
    private JButton manageReservationButton;
    private JButton accountOptionsButton;
    private String username;
    private Client client;

    public MainMenuGUI(String username, com.project.golf.client.Client client) {
        this.username = username;
        this.client = client;
        setTitle("Golf Course Reservation System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        showMainMenu();
    }

    // No-arg constructor for compatibility
    public MainMenuGUI() {
        this("defaultUser", null);
    }

    private void showMainMenu() {
        // Left side: main menu panel
        BackgroundPanel menuPanel = new BackgroundPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(new Color(34, 139, 34));

        JLabel title = new JLabel("Main Menu");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        menuPanel.add(title);

        if (username != null && !username.isEmpty()) {
            JLabel userLabel = new JLabel("Welcome, " + username + "!");
            userLabel.setAlignmentX(CENTER_ALIGNMENT);
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            userLabel.setForeground(Color.WHITE);
            menuPanel.add(userLabel);
        }

        menuPanel.add(Box.createVerticalStrut(40));

        // Make Reservation Button
        makeReservationButton = new JButton("Make Reservation");
        makeReservationButton.setAlignmentX(CENTER_ALIGNMENT);
        makeReservationButton.setMinimumSize(new Dimension(250, 50));
        makeReservationButton.setMaximumSize(new Dimension(250, 50));
        makeReservationButton.setFont(new Font("Arial", Font.BOLD, 16));
        makeReservationButton.addActionListener(this);
        menuPanel.add(makeReservationButton);

        menuPanel.add(Box.createVerticalStrut(20));

        // Manage Reservation Button
        manageReservationButton = new JButton("Manage Reservation");
        manageReservationButton.setAlignmentX(CENTER_ALIGNMENT);
        manageReservationButton.setMinimumSize(new Dimension(250, 50));
        manageReservationButton.setMaximumSize(new Dimension(250, 50));
        manageReservationButton.setFont(new Font("Arial", Font.BOLD, 16));
        manageReservationButton.addActionListener(this);
        menuPanel.add(manageReservationButton);

        menuPanel.add(Box.createVerticalStrut(20));

        // Account Options Button
        accountOptionsButton = new JButton("Account Options");
        accountOptionsButton.setAlignmentX(CENTER_ALIGNMENT);
        accountOptionsButton.setMinimumSize(new Dimension(250, 50));
        accountOptionsButton.setMaximumSize(new Dimension(250, 50));
        accountOptionsButton.setFont(new Font("Arial", Font.BOLD, 16));
        accountOptionsButton.addActionListener(this);
        menuPanel.add(accountOptionsButton);

        // Right side: AI chatbot panel
        ChatBotPanelGemini25Flash chatBotPanel = new ChatBotPanelGemini25Flash();
        chatBotPanel.setPreferredSize(new Dimension(350, 400)); // adjust as needed

        // Combine both panels in a split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, chatBotPanel);
        splitPane.setResizeWeight(0.6); // 60% left (menu), 40% right (chatbot)
        splitPane.setOneTouchExpandable(true);

        setContentPane(splitPane);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == makeReservationButton) {
            System.out.println("Make Reservation button clicked");
            this.dispose();
            new MakeReservationGUI(username, client);
        } else if (e.getSource() == manageReservationButton) {
            System.out.println("Manage Reservation button clicked");
            this.dispose();
            new ManageReservationsGUI(username, client);
        } else if (e.getSource() == accountOptionsButton) {
            System.out.println("Account Options button clicked");
            this.dispose();
            new AccountOptionsGUI(username, client);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuGUI("defaultUser", null));
    }
}
