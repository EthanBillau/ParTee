package com.project.golf.gui;

import com.project.golf.database.Database;
import com.project.golf.events.Event;
import com.project.golf.reservation.Reservations;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * EventApprovalGUI.java
 *
 * Modal dialog for administrator event approval workflow.
 * Reviews pending events with approve/reject decision capability.
 *
 * Data structures: JTable with DefaultTableModel for event display,
 * Database instance for persistence, JButton controls for actions.
 * Algorithm: Modal dialog pattern with event table filtering for pending status,
 * action handlers for approval/rejection with database synchronization.
 * Features: Pending event viewing, approval/rejection actions, conflict detection,
 * refresh capability, admin decision persistence, event state management.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */

public class EventApprovalGUI extends JDialog implements ActionListener {
    private final Database database;  // database instance for event persistence
    private JTable pendingEventsTable;  // table displaying pending event requests
    private DefaultTableModel tableModel;  // data model for the events table
    private JButton approveButton;  // button to approve selected event
    private JButton rejectButton;  // button to reject selected event
    private JButton refreshButton;  // button to reload pending events
    private JButton closeButton;  // button to close dialog

    public EventApprovalGUI(JFrame parent) {
        super(parent, "Pending Event Requests", true);
        this.database = Database.getInstance();

        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Pending Event Requests (Awaiting Admin Approval)");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Event ID", "Name", "Start Date", "Start Time", "End Date", "End Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingEventsTable = new JTable(tableModel);
        pendingEventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(pendingEventsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        approveButton = new JButton("Approve Selected Event");
        approveButton.addActionListener(this);
        buttonPanel.add(approveButton);

        rejectButton = new JButton("Reject Selected Event");
        rejectButton.addActionListener(this);
        buttonPanel.add(rejectButton);

        refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(this);
        buttonPanel.add(refreshButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadPendingEvents();
    }

    private void loadPendingEvents() {
        tableModel.setRowCount(0);
        
        // Reload pending events from file to get latest data
        try {
            database.reloadPendingEvents();
        } catch (Exception ex) {
            System.err.println("Error reloading pending events: " + ex.getMessage());
        }
        
        ArrayList<Event> pendingEvents = database.getAllPendingEvents();

        for (Event event : pendingEvents) {
            Object[] row = {
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getTime(),
                event.getEndDate(),
                event.getEndTime()
            };
            tableModel.addRow(row);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton) {
            approveSelectedEvent();
        } else if (e.getSource() == rejectButton) {
            rejectSelectedEvent();
        } else if (e.getSource() == refreshButton) {
            loadPendingEvents();
        } else if (e.getSource() == closeButton) {
            dispose();
        }
    }

    private void approveSelectedEvent() {
        int selectedRow = pendingEventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to approve.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String eventId = (String) tableModel.getValueAt(selectedRow, 0);
        Event pendingEvent = database.findPendingEvent(eventId);

        if (pendingEvent == null) {
            JOptionPane.showMessageDialog(this, "Event not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find conflicts
        ArrayList<Reservations> conflicts = database.findConflicts(pendingEvent);

        if (!conflicts.isEmpty()) {
            // Show conflicts dialog
            ConflictReviewDialog dialog = new ConflictReviewDialog(this, pendingEvent, conflicts);
            dialog.setVisible(true);

            if (dialog.isApproved()) {
                // Approve the event (conflicts will be removed automatically)
                database.approvePendingEvent(eventId);
                try {
                    database.saveToFile();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Event approved! " + conflicts.size() + 
                    " conflicting reservation(s)/event(s) removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingEvents();
            }
        } else {
            // No conflicts, approve directly
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Approve this event? There are no conflicts.", 
                "Confirm Approval", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                database.approvePendingEvent(eventId);
                try {
                    database.saveToFile();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Event approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingEvents();
            }
        }
    }

    private void rejectSelectedEvent() {
        int selectedRow = pendingEventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to reject.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String eventId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to reject this event request?", 
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean removed = database.removePendingEvent(eventId);
            try {
                database.saveToFile();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

            if (removed) {
                JOptionPane.showMessageDialog(this, "Event request rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
