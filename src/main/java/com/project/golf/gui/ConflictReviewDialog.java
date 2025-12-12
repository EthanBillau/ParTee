package com.project.golf.gui;

import com.project.golf.events.Event;
import com.project.golf.reservation.Reservations;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * ConflictReviewDialog.java
 *
 * <p>Modal conflict resolution dialog for event approval workflow. Displays conflicting
 * reservations and captures admin approval decision.
 *
 * <p>Data structures: ArrayList of Reservations for conflicts, boolean approved flag for decision
 * tracking, JButton controls for user action. Algorithm: Modal dialog displaying event details and
 * conflict table with user decision capture (approve/cancel) affecting internal state. Features:
 * Conflict display and review, admin confirmation dialog, decision persistence, deletion impact
 * warning, parent dialog modal relationship.
 *
 * @author Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */
public class ConflictReviewDialog extends JDialog implements ActionListener {
  private final ArrayList<Reservations> conflicts; // list of conflicting reservations
  private boolean approved = false; // flag indicating admin decision to approve

  private JButton approveButton; // button to approve event despite conflicts
  private JButton cancelButton; // button to cancel approval

  public ConflictReviewDialog(
      JDialog parent, Event pendingEvent, ArrayList<Reservations> conflicts) {
    super(parent, "Review Conflicts", true);
    this.conflicts = conflicts;

    setSize(800, 500);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    // Title panel
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel warningLabel =
        new JLabel(
            "⚠ WARNING: The following conflicts will be " + "DELETED if you approve this event:");
    warningLabel.setFont(new Font("Arial", Font.BOLD, 14));
    warningLabel.setForeground(Color.RED);
    titlePanel.add(warningLabel, BorderLayout.NORTH);

    JPanel eventInfoPanel = new JPanel(new GridLayout(3, 1));
    eventInfoPanel.add(new JLabel("Event: " + pendingEvent.getName()));
    eventInfoPanel.add(
        new JLabel(
            "Time: "
                + pendingEvent.getDate()
                + " "
                + pendingEvent.getTime()
                + " to "
                + pendingEvent.getEndDate()
                + " "
                + pendingEvent.getEndTime()));
    eventInfoPanel.add(new JLabel("Number of conflicts: " + conflicts.size()));
    titlePanel.add(eventInfoPanel, BorderLayout.CENTER);

    add(titlePanel, BorderLayout.NORTH);

    // Conflicts table
    String[] columnNames = {"Type", "ID", "User", "Date", "Time", "Party Size", "Tee Box"};
    DefaultTableModel tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };

    for (Reservations conflict : conflicts) {
      String type = conflict.isEvent() ? "EVENT" : "RESERVATION";
      Object[] row = {
        type,
        conflict.getReservationId(),
        conflict.getUsername(),
        conflict.getDate(),
        conflict.getTime(),
        conflict.getPartySize(),
        conflict.getTeeBox()
      };
      tableModel.addRow(row);
    }

    JTable conflictsTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(conflictsTable);
    add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    approveButton = new JButton("Approve Event & Delete Conflicts");
    approveButton.setBackground(new Color(255, 100, 100));
    approveButton.setForeground(Color.WHITE);
    approveButton.addActionListener(this);
    buttonPanel.add(approveButton);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == approveButton) {
      int confirm =
          JOptionPane.showConfirmDialog(
              this,
              "Are you SURE you want to delete "
                  + conflicts.size()
                  + " conflicting item(s)?\nThis cannot be undone!",
              "Final Confirmation",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.WARNING_MESSAGE);

      if (confirm == JOptionPane.YES_OPTION) {
        approved = true;
        dispose();
      }
    } else if (e.getSource() == cancelButton) {
      approved = false;
      dispose();
    }
  }

  public boolean isApproved() {
    return approved;
  }
}
