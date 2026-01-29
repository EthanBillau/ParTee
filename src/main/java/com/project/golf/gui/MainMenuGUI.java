package com.project.golf.gui;

import com.project.golf.client.Client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * MainMenuGUI.java
 *
 * <p>Primary navigation hub for authenticated users after login. Presents three main options for
 * reservation management and account settings.
 *
 * <p>Data structures: JFrame for window, JButtons for user options, String username, Client
 * reference for server communication. BackgroundPanel for themed rendering. Algorithm: Swing
 * event-driven architecture with ActionListener button callbacks. Features: Main menu display,
 * button navigation to reservation/account GUIs, user context propagation, graceful session
 * management.
 *
 * @author Anoushka Chakravarty (chakr181), L15
 * @version December 7, 2025
 */
public class MainMenuGUI extends JFrame implements ActionListener {

  private JButton makeReservationButton; // button to navigate to make reservation screen
  private JButton manageReservationButton; // button to navigate to manage reservations screen
  private JButton accountOptionsButton; // button to navigate to account options screen
  private JButton chatBotButton; // button to open AI chatbot assistant
  private JButton logoutButton; // button to logout and return to login screen
  private String username; // currently logged-in username
  private Client client; // client connection for server communication

  public MainMenuGUI(String username, com.project.golf.client.Client client) {
    this.username = username;
    this.client = client;
    setTitle("Golf Course Reservation System - Main Menu");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(700, 550); // Increased from 600x400 for better accessibility
    setLocationRelativeTo(null);

    showMainMenu();
  }

  // No-arg constructor for compatibility
  public MainMenuGUI() {
    this("defaultUser", null);
  }

  private void showMainMenu() {
    BackgroundPanel panel = new BackgroundPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    panel.setBackground(new Color(34, 139, 34));

    JLabel title = new JLabel("Main Menu");
    title.setAlignmentX(CENTER_ALIGNMENT);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    title.setForeground(Color.WHITE);
    panel.add(title);

    // Display logged-in username if available
    if (username != null && !username.isEmpty()) {
      JLabel userLabel = new JLabel("Welcome, " + username + "!");
      userLabel.setAlignmentX(CENTER_ALIGNMENT);
      userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      userLabel.setForeground(Color.WHITE);
      panel.add(userLabel);
    }

    panel.add(Box.createVerticalStrut(40));

    // Make Reservation Button
    makeReservationButton = new JButton("Make Reservation");
    makeReservationButton.setAlignmentX(CENTER_ALIGNMENT);
    makeReservationButton.setMinimumSize(new Dimension(250, 50));
    makeReservationButton.setMaximumSize(new Dimension(250, 50));
    makeReservationButton.setFont(new Font("Arial", Font.BOLD, 16));
    makeReservationButton.addActionListener(this);
    panel.add(makeReservationButton);

    panel.add(Box.createVerticalStrut(20));

    // Manage Reservation Button
    manageReservationButton = new JButton("Manage Reservation");
    manageReservationButton.setAlignmentX(CENTER_ALIGNMENT);
    manageReservationButton.setMinimumSize(new Dimension(250, 50));
    manageReservationButton.setMaximumSize(new Dimension(250, 50));
    manageReservationButton.setFont(new Font("Arial", Font.BOLD, 16));
    manageReservationButton.addActionListener(this);
    panel.add(manageReservationButton);

    panel.add(Box.createVerticalStrut(20));

    // Account Options Button
    accountOptionsButton = new JButton("Account Options");
    accountOptionsButton.setAlignmentX(CENTER_ALIGNMENT);
    accountOptionsButton.setMinimumSize(new Dimension(250, 50));
    accountOptionsButton.setMaximumSize(new Dimension(250, 50));
    accountOptionsButton.setFont(new Font("Arial", Font.BOLD, 16));
    accountOptionsButton.addActionListener(this);
    panel.add(accountOptionsButton);

    panel.add(Box.createVerticalStrut(20));

    // AI Chatbot Button
    chatBotButton = new JButton("AI Assistant");
    chatBotButton.setAlignmentX(CENTER_ALIGNMENT);
    chatBotButton.setMinimumSize(new Dimension(250, 50));
    chatBotButton.setMaximumSize(new Dimension(250, 50));
    chatBotButton.setFont(new Font("Arial", Font.BOLD, 16));
    chatBotButton.setBackground(new Color(70, 130, 180));
    chatBotButton.setForeground(Color.WHITE);
    chatBotButton.addActionListener(this);
    panel.add(chatBotButton);

    panel.add(Box.createVerticalStrut(30));

    // Logout Button
    logoutButton = new JButton("Logout");
    logoutButton.setAlignmentX(CENTER_ALIGNMENT);
    logoutButton.setMinimumSize(new Dimension(250, 45));
    logoutButton.setMaximumSize(new Dimension(250, 45));
    logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
    logoutButton.setBackground(new Color(220, 53, 69)); // Red color for logout
    logoutButton.setForeground(Color.WHITE);
    logoutButton.addActionListener(this);
    panel.add(logoutButton);

    setContentPane(panel);
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
    } else if (e.getSource() == chatBotButton) {
      System.out.println("AI Assistant button clicked");
      openChatBotWindow();
    } else if (e.getSource() == logoutButton) {
      System.out.println("Logout button clicked");
      handleLogout();
    }
  }

  /** Handle logout action Disconnects from server and returns to login screen */
  private void handleLogout() {
    int confirm =
        JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
      // Disconnect client if available
      if (client != null) {
        try {
          client.disconnect();
        } catch (Exception ex) {
          System.err.println("Error disconnecting client: " + ex.getMessage());
        }
      }

      // Close current window and return to login
      this.dispose();
      SwingUtilities.invokeLater(() -> new LoginGUI());
    }
  }

  private void openChatBotWindow() {
    JFrame chatFrame = new JFrame("AI Golf Assistant");
    chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    chatFrame.setSize(600, 500);
    chatFrame.setLocationRelativeTo(this);

    ChatBotPanelGemini25Flash chatPanel = new ChatBotPanelGemini25Flash();
    chatFrame.add(chatPanel);

    chatFrame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MainMenuGUI("defaultUser", null));
  }
}
