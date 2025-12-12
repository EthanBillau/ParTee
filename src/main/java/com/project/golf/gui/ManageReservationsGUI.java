package com.project.golf.gui;

import com.project.golf.client.Client;
import com.project.golf.reservation.Reservations;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * ManageReservationsGUI.java
 *
 * Displays and manages user's existing golf course reservations and events.
 * Provides table view with edit/delete actions and new reservation creation.
 *
 * Data structures: JTable with DefaultTableModel for reservation display,
 * JPanel layouts for content organization, Lists for reservation/event data,
 * String username and Client for server synchronization.
 * Algorithm: Table-driven display pattern with action button event handling,
 * dynamic refresh with server queries, column customization for readability.
 * Features: Reservation viewing and management, edit/delete actions, event display,
 * creation navigation, refresh capability, server data synchronization.
 *
 * @author Nikhil Kodali (kodali3), L15
 *
 * @version December 7, 2025
 */

public class ManageReservationsGUI extends JFrame implements ActionListener {

    private JPanel contentHolder;  // main content panel that switches between states
    private JPanel emptyPanel;  // displayed when user has no reservations
    private JPanel tablePanel;  // displayed when user has reservations
    private JTable reservationsTable;  // table displaying reservation list
    private DefaultTableModel tableModel;  // data model for the table
    
    private JButton refreshButton;  // button to reload reservations from server
    private JButton backButton;  // button to return to main menu
    private JButton createButton;  // button to create new reservation
    private JButton createButtonFooter;  // footer button to create new reservation

    private String username;  // currently logged-in username
    private Client client;  // client connection for server communication
    private List<Reservations> currentReservations;  // list of user's confirmed reservations
    private List<com.project.golf.events.Event> currentPendingEvents;  // list of user's pending events

    public ManageReservationsGUI(String username, Client client) {
        super("Reservations - Manage");
        this.username = username;
        this.client = client;
        this.currentReservations = new ArrayList<>();
        this.currentPendingEvents = new ArrayList<>();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        
        initUI();
        loadReservations();
    }

    // No-arg constructor for compatibility
    public ManageReservationsGUI() {
        this("defaultUser", null);
    }

    private void initUI() {
        BackgroundPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        JPanel topPanel = buildTopPanel();
        root.add(topPanel, BorderLayout.NORTH);

        contentHolder = new JPanel(new CardLayout());
        contentHolder.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentHolder.setOpaque(false);
        root.add(contentHolder, BorderLayout.CENTER);

        emptyPanel = buildEmptyPanel();
        tablePanel = buildTablePanel();

        contentHolder.add(emptyPanel, "EMPTY");
        contentHolder.add(tablePanel, "TABLE");

        setVisible(true);
    }

    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Back button on left
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        leftPanel.add(backButton);
        
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Title in center
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        JLabel title = new JLabel("Your Reservations");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        centerPanel.add(title);
        topPanel.add(centerPanel, BorderLayout.CENTER);

        // Refresh button on right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        refreshButton = new JButton("\u21bb " + "Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(this);
        rightPanel.add(refreshButton);
        
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel buildEmptyPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(100, 20, 100, 20));

        JLabel msg = new JLabel("You don't have any upcoming reservations.");
        msg.setFont(new Font("Arial", Font.PLAIN, 18));
        msg.setAlignmentX(CENTER_ALIGNMENT);
        msg.setForeground(Color.WHITE);
        p.add(msg);

        p.add(Box.createVerticalStrut(30));

        createButton = new JButton("+ Create Reservation");
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setFocusable(false);
        createButton.setAlignmentX(CENTER_ALIGNMENT);
        createButton.setMinimumSize(new Dimension(250, 50));
        createButton.setMaximumSize(new Dimension(250, 50));
        createButton.addActionListener(this);
        p.add(createButton);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] colNames = new String[]{"Status", "Date", "Time", "Hole", "Party Size", "Actions"};
        tableModel = new DefaultTableModel(null, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column editable
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setFillsViewportHeight(true);
        reservationsTable.setRowHeight(44);
        reservationsTable.setShowGrid(false);
        reservationsTable.setIntercellSpacing(new Dimension(0, 0));
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setOpaque(false);
        reservationsTable.setBackground(new Color(255, 255, 255, 220));

        JTableHeader header = reservationsTable.getTableHeader();
        header.setBackground(new Color(220, 230, 230));
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        reservationsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        reservationsTable.setForeground(new Color(32, 33, 36));

        TableColumn actionsColumn = reservationsTable.getColumnModel().getColumn(5);
        actionsColumn.setCellRenderer(new ActionsCellRenderer());
        actionsColumn.setCellEditor(new ActionsCellEditor());
        actionsColumn.setMaxWidth(180);
        actionsColumn.setMinWidth(180);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setOpaque(true);
        centerRenderer.setBackground(new Color(255, 255, 255, 220));
        
        for (int i = 0; i < 5; i++) {
            reservationsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Re-apply actions column renderer after center renderer
        actionsColumn.setCellRenderer(new ActionsCellRenderer());

        JScrollPane scroll = new JScrollPane(reservationsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);

        // Footer with create button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        createButtonFooter = new JButton("+ Create Reservation");
        createButtonFooter.setFont(new Font("Arial", Font.BOLD, 16));
        createButtonFooter.setFocusable(false);
        createButtonFooter.setMinimumSize(new Dimension(250, 50));
        createButtonFooter.setMaximumSize(new Dimension(250, 50));
        createButtonFooter.addActionListener(this);
        footerPanel.add(createButtonFooter);

        p.add(footerPanel, BorderLayout.SOUTH);

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenuGUI(username, client));
        } else if (e.getSource() == refreshButton) {
            // Refresh the data and UI
            loadReservations();
            // Force the entire content pane to repaint
            contentHolder.revalidate();
            contentHolder.repaint();
        } else if (e.getSource() == createButton || e.getSource() == createButtonFooter) {
            dispose();
            SwingUtilities.invokeLater(() -> new MakeReservationGUI(username, client));
        }
    }

    private void loadReservations() {
        if (username == null) {
            showEmptyState();
            return;
        }

        try {
            java.util.ArrayList<Reservations> allReservations;
            
            // Check if we have a client connection (multi-device mode)
            if (client != null) {
                try {
                    // Fetch reservations from server
                    String response = client.getReservations(username);
                    allReservations = new java.util.ArrayList<>();
                    
                    if (response.startsWith("RESP|OK|")) {
                        String data = response.substring(8); // Remove "RESP|OK|"
                        if (!data.isEmpty()) {
                            String[] reservationStrings = data.split("\\|");
                            for (String resStr : reservationStrings) {
                                if (!resStr.isEmpty()) {
                                    try {
                                        Reservations r = Reservations.fromFileString(resStr);
                                        if (r != null) {
                                            allReservations.add(r);
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Error parsing reservation: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Connection error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    showEmptyState();
                    return;
                }
            } else {
                // Local mode - load from database
                com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
                try {
                    db.loadFromFile();
                } catch (Exception ex) {
                    System.err.println("Could not reload from file: " + ex.getMessage());
                }
                allReservations = db.getReservationsByUser(username);
            }

            currentReservations.clear();
            currentPendingEvents.clear();
            
            // Separate pending events from confirmed reservations
            if (allReservations != null) {
                for (Reservations r : allReservations) {
                    if (r instanceof com.project.golf.events.Event && r.isPending()) {
                        currentPendingEvents.add((com.project.golf.events.Event) r);
                    } else if (!r.isPending()) {
                        currentReservations.add(r);
                    }
                }
            }

            /**
             * Show appropriate view based on whether user has any reservations or pending events.
             * Empty state shows a message, otherwise display the table.
             */
            if (currentReservations.isEmpty() && currentPendingEvents.isEmpty()) {
                showEmptyState();
            } else {
                showWithReservationsAndPending(currentReservations, currentPendingEvents);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            showEmptyState();
        }
    }

    public void showEmptyState() {
        CardLayout cl = (CardLayout) (contentHolder.getLayout());
        cl.show(contentHolder, "EMPTY");
    }

    public void showWithReservations(List<Reservations> reservations) {
        showWithReservationsAndPending(reservations, new ArrayList<>());
    }
    
    public void showWithReservationsAndPending(List<Reservations> reservations, List<com.project.golf.events.Event> pendingEvents) {
        // Clear all existing rows
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        // First, add pending events with PENDING status
        for (com.project.golf.events.Event e : pendingEvents) {
            String dateDisplay = e.getDate() + " → " + e.getEndDate();
            String timeDisplay = e.getTime() + " → " + e.getEndTime();
            String teeBoxDisplay = "EVENT (All Holes)";
            int partySizeDisplay = 200;

            tableModel.addRow(new Object[]{
                "PENDING EVENT",
                dateDisplay,
                timeDisplay,
                teeBoxDisplay,
                partySizeDisplay,
                "PENDING:" + e.getReservationId()
            });
        }

        // Then, add confirmed reservations and approved events
        for (Reservations r : reservations) {
            String statusDisplay = "Reservation";
            String dateDisplay = r.getDate();
            String timeDisplay = r.getTime();
            String teeBoxDisplay = r.getTeeBox();
            int partySizeDisplay = r.getPartySize();

            if (r instanceof com.project.golf.events.Event) {
                com.project.golf.events.Event e = (com.project.golf.events.Event) r;
                statusDisplay = "EVENT";
                teeBoxDisplay = "EVENT (All Holes)";
                partySizeDisplay = 200;

                timeDisplay = r.getTime() + " → " + e.getEndTime();
                dateDisplay = r.getDate() + " → " + e.getEndDate();
            }

            tableModel.addRow(new Object[]{
                statusDisplay,
                dateDisplay,
                timeDisplay,
                teeBoxDisplay,
                partySizeDisplay,
                r.getReservationId()
            });
        }

        // Switch to table view
        CardLayout cl = (CardLayout) contentHolder.getLayout();
        cl.show(contentHolder, "TABLE");
        
        // Force the scroll pane and table to update
        SwingUtilities.invokeLater(() -> {
            tableModel.fireTableDataChanged();
            if (reservationsTable != null) {
                reservationsTable.revalidate();
                reservationsTable.repaint();
            }
            contentHolder.revalidate();
            contentHolder.repaint();
        });
    }

    private void editReservation(String reservationId) {
        Reservations reservation = null;
        for (Reservations r : currentReservations) {
            if (r.getReservationId().equals(reservationId)) {
                reservation = r;
                break;
            }
        }

        if (reservation == null) {
            JOptionPane.showMessageDialog(this,
                    "Reservation not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pass the full Reservations object so MakeReservationGUI can pre-fill the form
        final Reservations resForEdit = reservation;
        dispose();
        SwingUtilities.invokeLater(() -> {
            MakeReservationGUI makeResGUI = new MakeReservationGUI(username, client, resForEdit);
            makeResGUI.setVisible(true);
        });
    }

    private boolean deleteReservation(String reservationId) {
        /**
         * Confirm with user before deleting. Deletion is permanent and can't be
         * undone.
         */
        boolean isPending = reservationId.startsWith("PENDING:");
        String actualId = isPending ? reservationId.substring(8) : reservationId;
        String itemType = isPending ? "pending event" : "reservation";
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this " + itemType + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return false;
        }

        try {
            // Check if we have a client connection (multi-device mode)
            if (client != null) {
                try {
                    String response = client.cancelReservation(actualId);
                    
                    if (response.startsWith("RESP|OK|")) {
                        JOptionPane.showMessageDialog(this,
                                itemType.substring(0, 1).toUpperCase() + itemType.substring(1) + " deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadReservations();
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete reservation: " + response,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Connection error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                // Local mode - direct database access
                com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
                boolean removed;
                
                if (isPending) {
                    removed = db.removePendingEvent(actualId);
                } else {
                    removed = db.removeReservation(actualId);
                }

                if (removed) {
                    db.saveToFile();
                    JOptionPane.showMessageDialog(this,
                            itemType.substring(0, 1).toUpperCase() + itemType.substring(1) + " deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadReservations();
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete reservation.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting reservation: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private class ActionsCellRenderer implements TableCellRenderer {

        private final JPanel panel;
        private final JPanel pendingPanel;
        private final JButton editBtn;
        private final JButton deleteBtn;
        private final JButton deletePendingBtn;

        ActionsCellRenderer() {
            // Panel for regular reservations (with Edit and Delete)
            panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            panel.setOpaque(true);
            panel.setBackground(new Color(255, 255, 255, 220));

            editBtn = new JButton("Edit");
            editBtn.setToolTipText("Edit reservation");
            styleActionButton(editBtn);

            deleteBtn = new JButton("Delete");
            deleteBtn.setToolTipText("Delete reservation");
            styleActionButton(deleteBtn);

            panel.add(editBtn);
            panel.add(deleteBtn);
            
            // Panel for pending events (only Delete)
            pendingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            pendingPanel.setOpaque(true);
            
            deletePendingBtn = new JButton("Delete");
            deletePendingBtn.setToolTipText("Delete pending event");
            styleActionButton(deletePendingBtn);
            
            pendingPanel.add(deletePendingBtn);
        }

        private void styleActionButton(JButton b) {
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setFocusable(false);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            b.setBackground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            // Check if this row is a pending event
            String id = (String) table.getModel().getValueAt(row, 5);
            boolean isPending = id != null && id.startsWith("PENDING:");
            return isPending ? pendingPanel : panel;
        }
    }

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private final JPanel pendingPanel;
        private final JButton editBtn;
        private final JButton deleteBtn;
        private final JButton deletePendingBtn;
        private String currentReservationId;
        private boolean isPending;

        ActionsCellEditor() {
            // Panel for regular reservations (with Edit and Delete)
            panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            panel.setOpaque(true);
            panel.setBackground(new Color(255, 255, 255, 220));

            editBtn = new JButton("Edit");
            styleActionButton(editBtn);
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                editReservation(currentReservationId);
            });

            deleteBtn = new JButton("Delete");
            styleActionButton(deleteBtn);
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                deleteReservation(currentReservationId);
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
            
            // Panel for pending events (only Delete)
            pendingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            pendingPanel.setOpaque(false);
            
            deletePendingBtn = new JButton("Delete");
            styleActionButton(deletePendingBtn);
            deletePendingBtn.addActionListener(e -> {
                fireEditingStopped();
                deleteReservation(currentReservationId);
            });
            
            pendingPanel.add(deletePendingBtn);
        }

        private void styleActionButton(JButton b) {
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setFocusable(false);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            b.setBackground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected, int row, int column) {
            // Get the reservation ID from the last column (Actions column)
            currentReservationId = (String) table.getModel().getValueAt(row, 5);
            isPending = currentReservationId.startsWith("PENDING:");
            return isPending ? pendingPanel : panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentReservationId;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageReservationsGUI ui = new ManageReservationsGUI();
            ui.setVisible(true);
        });
    }
}