package com.project.golf.gui;

import com.project.golf.client.Client;
import com.project.golf.reservation.Reservations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ManageReservationsGUI.java
 *
 * GUI for viewing and managing golf course reservations
 *
 * @author Nikhil Kodali (kodali3), L15
 * @version December 4, 2025
 */
public class ManageReservationsGUI extends JFrame {

    private static final Color HEADER_BG = new Color(3, 100, 125);
    private static final Color ACCENT = new Color(0, 150, 136);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font SUB_HEADER_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private JPanel contentHolder;
    private JPanel emptyPanel;
    private JPanel tablePanel;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;

    private String username;
    private Client client;
    private List<Reservations> currentReservations;

    public ManageReservationsGUI(String username, Client client) {
        super("Reservations - Manage");
        this.username = username;
        this.client = client;
        this.currentReservations = new ArrayList<>();
        initUI();
        loadReservations();
    }

    // No-arg constructor for compatibility
    public ManageReservationsGUI() {
        this("defaultUser", null);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 420));
        setPreferredSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);

        BackgroundPanel root = new BackgroundPanel(new BorderLayout());
        setContentPane(root);

        JPanel header = buildHeaderPanel();
        root.add(header, BorderLayout.NORTH);

        contentHolder = new JPanel(new CardLayout());
        contentHolder.setBorder(new EmptyBorder(18, 18, 18, 18));
        contentHolder.setOpaque(false);
        root.add(contentHolder, BorderLayout.CENTER);

        emptyPanel = buildEmptyPanel();
        tablePanel = buildTablePanel();

        contentHolder.add(emptyPanel, "EMPTY");
        contentHolder.add(tablePanel, "TABLE");

        JPanel footer = buildFooterBar();
        root.add(footer, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Your Reservations");
        title.setForeground(Color.WHITE);
        title.setFont(HEADER_FONT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton refreshBtn = createIconButton("\u21bb", "Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(255, 255, 255, 20));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40)));
        refreshBtn.setFocusable(false);
        refreshBtn.addActionListener(e -> loadReservations());

        JButton backBtn = createIconButton("\u2190", "Back to Main Menu");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(255, 255, 255, 20));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40)));
        backBtn.setFocusable(false);
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenuGUI(username, client));
        });

        right.add(backBtn);
        right.add(refreshBtn);

        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildEmptyPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel msg = new JLabel("<html><div style='text-align:center;'>You don't have any upcoming reservations.</div></html>");
        msg.setFont(SUB_HEADER_FONT);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        p.add(msg, gbc);

        JButton createBtn = new JButton("+ Create Reservation");
        createBtn.setFont(BUTTON_FONT);
        createBtn.setBackground(ACCENT);
        createBtn.setForeground(Color.BLACK);
        createBtn.setFocusable(false);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        createBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MakeReservationGUI(username, client));
        });

        createBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createBtn.setBackground(ACCENT.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createBtn.setBackground(ACCENT);
            }
        });

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        p.add(createBtn, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        p.add(Box.createVerticalGlue(), gbc);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] colNames = new String[]{"Date", "Time", "Hole", "Party Size", "Actions"};
        tableModel = new DefaultTableModel(null, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column editable
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setFillsViewportHeight(true);
        reservationsTable.setRowHeight(44);
        reservationsTable.setShowGrid(false);
        reservationsTable.setIntercellSpacing(new Dimension(0, 0));
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setOpaque(false);
        reservationsTable.setBackground(new Color(255, 255, 255, 180));

        JTableHeader header = reservationsTable.getTableHeader();
        header.setBackground(new Color(220, 230, 230));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reservationsTable.setForeground(new Color(32, 33, 36));

        TableColumn actionsColumn = reservationsTable.getColumnModel().getColumn(4);
        actionsColumn.setCellRenderer(new ActionsCellRenderer());
        actionsColumn.setCellEditor(new ActionsCellEditor());
        actionsColumn.setMaxWidth(180);
        actionsColumn.setMinWidth(180);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 4; i++) {
            reservationsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(reservationsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);

        JPanel tableTopBar = new JPanel(new BorderLayout());
        tableTopBar.setOpaque(false);
        tableTopBar.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel info = new JLabel("Showing upcoming reservations");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(Color.WHITE);
        tableTopBar.add(info, BorderLayout.WEST);

        p.add(tableTopBar, BorderLayout.NORTH);

        return p;
    }

    private JPanel buildFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(10, 16, 10, 16));
        footer.setOpaque(false);

        JButton createBtn = new JButton("+ Create Reservation");
        createBtn.setFont(BUTTON_FONT);
        createBtn.setBackground(ACCENT);
        createBtn.setForeground(Color.BLACK);
        createBtn.setFocusable(false);
        createBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MakeReservationGUI(username, client));
        });
        footer.add(createBtn, BorderLayout.EAST);

        return footer;
    }

    private JButton createIconButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setToolTipText(tooltip);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setBackground(new Color(255, 255, 255, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadReservations() {
        if (username == null) {
            showEmptyState();
            return;
        }

        try {
            /**
             * Fetch all reservations for this user from the database. Database
             * returns only reservations matching the username.
             */
            com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
            java.util.ArrayList<Reservations> allReservations = db.getReservationsByUser(username);

            currentReservations.clear();
            if (allReservations != null) {
                currentReservations.addAll(allReservations); // Store for later use
            }

            /**
             * Show appropriate view based on whether user has any reservations.
             * Empty state shows a message, otherwise display the table.
             */
            if (currentReservations.isEmpty()) {
                showEmptyState();
            } else {
                showWithReservations(currentReservations);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            showEmptyState(); // Show empty state on error
        }
    }

    public void showEmptyState() {
        CardLayout cl = (CardLayout) (contentHolder.getLayout());
        cl.show(contentHolder, "EMPTY");
    }

    public void showWithReservations(List<Reservations> reservations) {
        tableModel.setRowCount(0);

        for (Reservations r : reservations) {

            String dateDisplay = r.getDate();
            String timeDisplay = r.getTime();
            String teeBoxDisplay = r.getTeeBox();
            int partySizeDisplay = r.getPartySize();

            if (r instanceof com.project.golf.events.Event) {
                com.project.golf.events.Event e = (com.project.golf.events.Event) r;

                teeBoxDisplay = "EVENT (All Holes)";
                partySizeDisplay = 200;

                timeDisplay = r.getTime() + " → " + e.getEndTime();
                dateDisplay = r.getDate() + " → " + e.getEndDate();
            }

            tableModel.addRow(new Object[]{
                dateDisplay,
                timeDisplay,
                teeBoxDisplay,
                partySizeDisplay,
                r.getReservationId()
            });
        }

        CardLayout cl = (CardLayout) contentHolder.getLayout();
        cl.show(contentHolder, "TABLE");
    }

    private void editReservation(String reservationId) {
        /**
         * Look up the full reservation object from our cached list. We need to
         * verify it exists before trying to edit.
         */
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

        /**
         * Open the make reservation screen in edit mode. Pass the reservation
         * ID so it knows which one to replace. The old reservation stays in the
         * database until the user confirms the changes.
         */
        dispose();
        SwingUtilities.invokeLater(() -> {
            MakeReservationGUI makeResGUI = new MakeReservationGUI(username, client, reservationId);
            makeResGUI.setVisible(true);
        });
    }

    private boolean deleteReservation(String reservationId) {
        /**
         * Confirm with user before deleting. Deletion is permanent and can't be
         * undone.
         */
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this reservation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return false; // User cancelled
        }

        try {
            /**
             * Remove from database and save changes to disk. If successful,
             * refresh the table to show updated list.
             */
            com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
            boolean removed = db.removeReservation(reservationId);

            if (removed) {
                db.saveToFile(); // Persist the deletion
                JOptionPane.showMessageDialog(this,
                        "Reservation deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadReservations(); // Refresh the display
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete reservation.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
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
        private final JButton editBtn;
        private final JButton deleteBtn;

        ActionsCellRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            panel.setOpaque(true);

            editBtn = new JButton("Edit");
            editBtn.setToolTipText("Edit reservation");
            styleActionBtn(editBtn);

            deleteBtn = new JButton("Delete");
            deleteBtn.setToolTipText("Delete reservation");
            styleActionBtn(deleteBtn);

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private void styleActionBtn(JButton b) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
            return panel;
        }
    }

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private final JButton editBtn;
        private final JButton deleteBtn;
        private String currentReservationId;

        ActionsCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
            panel.setOpaque(false);

            editBtn = new JButton("Edit");
            styleActionBtn(editBtn);
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                editReservation(currentReservationId);
            });

            deleteBtn = new JButton("Delete");
            styleActionBtn(deleteBtn);
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                deleteReservation(currentReservationId);
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private void styleActionBtn(JButton b) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setFocusable(false);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            b.setBackground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Get the reservation ID from the last column (Actions column)
            currentReservationId = (String) table.getModel().getValueAt(row, 4);
            return panel;
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
