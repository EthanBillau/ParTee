package com.project.golf.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import com.project.golf.client.Client;
import com.project.golf.events.Event;
import com.project.golf.reservation.Reservations;
import javax.swing.*;

/**
 * MakeReservationGUI.java
 *
 * GUI for making a new golf course reservation
 *
 * @author Anoushka Chakravarty (chakr181), L15
 *
 * @version 12/04/2025
 */
public class MakeReservationGUI extends JFrame implements ActionListener {

    private JTextField dateField;
    private JButton calendarButton;
    private JButton timeButton;
    private JButton[] holeButtons;
    private JButton continueButton;
    private JButton backButton;
    private JComboBox<String> partySizeCombo;
    private JCheckBox cartCheckBox;

    // Event-specific UI
    private JCheckBox eventCheckBox;
    private JPanel eventOptionsPanel;
    private JComboBox<String> eventHoursCombo;
    private JLabel holeSelectionLabel;
    private JPanel holeGridPanel;
    private JPanel partySizePanel;

    private JCheckBox calendarInviteCheckBox;

    private int selectedHole = -1;
    private String selectedTime = "9:00 AM";
    private String username;
    private Client client;
    private String editingReservationId = null;

    public MakeReservationGUI(String username, Client client) {
        this(username, client, null);
    }

    public MakeReservationGUI(String username, Client client, String editingReservationId) {
        this.username = username;
        this.client = client;
        this.editingReservationId = editingReservationId;
        setTitle(editingReservationId != null ? "Edit Reservation" : "Make Reservation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 700);
        setLocationRelativeTo(null);

        showReservationScreen();
    }

    // No-arg constructor for compatibility
    public MakeReservationGUI() {
        this("defaultUser", null, null);
    }

    private void showReservationScreen() {
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Back button at top left
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        backButton = new JButton("<- Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        topPanel.add(backButton, BorderLayout.WEST);

        panel.add(topPanel);
        panel.add(Box.createVerticalStrut(10));

        // Title
        JLabel title = new JLabel("Make a Reservation");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title);

        panel.add(Box.createVerticalStrut(20));

        // Date section
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        datePanel.setOpaque(false);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        datePanel.add(dateLabel);

        dateField = new JTextField(15);
        dateField.setPreferredSize(new Dimension(150, 30));
        dateField.setEditable(false);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        datePanel.add(dateField);

        calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Arial", Font.PLAIN, 16));
        calendarButton.setPreferredSize(new Dimension(50, 30));
        calendarButton.addActionListener(this);
        datePanel.add(calendarButton);

        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));

        // Time section - clickable button
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setAlignmentX(CENTER_ALIGNMENT);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(timeLabel);

        timeButton = new JButton("9:00 AM");
        timeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        timeButton.setPreferredSize(new Dimension(200, 35));
        timeButton.setMaximumSize(new Dimension(200, 35));
        timeButton.setAlignmentX(CENTER_ALIGNMENT);
        timeButton.addActionListener(this);
        panel.add(timeButton);

        panel.add(Box.createVerticalStrut(15));

        // Hole selection label
        holeSelectionLabel = new JLabel("Select Hole:");
        holeSelectionLabel.setAlignmentX(CENTER_ALIGNMENT);
        holeSelectionLabel.setForeground(Color.WHITE);
        holeSelectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(holeSelectionLabel);

        panel.add(Box.createVerticalStrut(10));

        // Hole grid (3 rows x 6 columns for 18 holes)
        holeGridPanel = new JPanel(new GridLayout(3, 6, 8, 8));
        holeGridPanel.setOpaque(false);
        holeGridPanel.setMaximumSize(new Dimension(550, 180));
        holeGridPanel.setAlignmentX(CENTER_ALIGNMENT);

        holeButtons = new JButton[18];
        for (int i = 0; i < 18; i++) {
            holeButtons[i] = createHoleButton(i + 1);
            holeGridPanel.add(holeButtons[i]);
        }

        panel.add(holeGridPanel);

        panel.add(Box.createVerticalStrut(15));

        // Party Size section
        partySizePanel = new JPanel();
        partySizePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        partySizePanel.setOpaque(false);
        partySizePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel partySizeLabel = new JLabel("Party Size:");
        partySizeLabel.setForeground(Color.WHITE);
        partySizeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        partySizePanel.add(partySizeLabel);

        String[] partySizes = {"1", "2", "3", "4"};
        partySizeCombo = new JComboBox<>(partySizes);
        partySizeCombo.setPreferredSize(new Dimension(80, 30));
        partySizePanel.add(partySizeCombo);

        panel.add(partySizePanel);
        panel.add(Box.createVerticalStrut(10));

        // Golf Cart section
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        cartPanel.setOpaque(false);
        cartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        cartCheckBox = new JCheckBox("Golf Cart Needed");
        cartCheckBox.setForeground(Color.WHITE);
        cartCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        cartCheckBox.setOpaque(false);
        cartPanel.add(cartCheckBox);

        panel.add(cartPanel);
        panel.add(Box.createVerticalStrut(10));

        // Event checkbox (marks reservation as event - always 200 people, all holes)
        JPanel eventCheckPanel = new JPanel();
        eventCheckPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        eventCheckPanel.setOpaque(false);
        eventCheckPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        eventCheckBox = new JCheckBox("Mark as Event (All Holes, 200 People)");
        eventCheckBox.setForeground(Color.WHITE);
        eventCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        eventCheckBox.setOpaque(false);
        eventCheckBox.addActionListener(e -> toggleEventMode(eventCheckBox.isSelected()));
        eventCheckPanel.add(eventCheckBox);

        panel.add(eventCheckPanel);
        panel.add(Box.createVerticalStrut(10));

        // Event options panel (hidden unless eventCheckBox is checked)
        eventOptionsPanel = new JPanel();
        eventOptionsPanel.setLayout(new BoxLayout(eventOptionsPanel, BoxLayout.Y_AXIS));
        eventOptionsPanel.setOpaque(false);
        eventOptionsPanel.setVisible(false);

        // Event duration
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        durationPanel.setOpaque(false);
        JLabel eventHoursLabel = new JLabel("Event Duration (Hours):");
        eventHoursLabel.setForeground(Color.WHITE);
        eventHoursLabel.setFont(new Font("Arial", Font.BOLD, 13));
        durationPanel.add(eventHoursLabel);

        String[] hours = new String[45];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = String.valueOf(i + 4);
        }
        eventHoursCombo = new JComboBox<>(hours);
        eventHoursCombo.setSelectedItem("4"); // Default to 4 hours
        eventHoursCombo.setPreferredSize(new Dimension(90, 30));
        durationPanel.add(eventHoursCombo);
        eventOptionsPanel.add(durationPanel);

        panel.add(eventOptionsPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Calendar Invite section
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        calendarPanel.setOpaque(false);
        calendarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        calendarInviteCheckBox = new JCheckBox("Receive Calendar Invite");
        calendarInviteCheckBox.setForeground(Color.WHITE);
        calendarInviteCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        calendarInviteCheckBox.setOpaque(false);
        calendarPanel.add(calendarInviteCheckBox);
        
        panel.add(calendarPanel);
        panel.add(Box.createVerticalStrut(20));

        // Continue button
        continueButton = new JButton("Continue");
        continueButton.setAlignmentX(CENTER_ALIGNMENT);
        continueButton.setMinimumSize(new Dimension(200, 40));
        continueButton.setMaximumSize(new Dimension(200, 40));
        continueButton.setFont(new Font("Arial", Font.BOLD, 16));
        continueButton.addActionListener(this);
        panel.add(continueButton);

        setContentPane(panel);
        setVisible(true);
    }

    /**
     * Toggle UI when event checkbox is toggled.
     * Events are always all holes with 200 people, so hide hole selection and party size.
     */
    private void toggleEventMode(boolean enabled) {
        eventOptionsPanel.setVisible(enabled);
        
        // Hide/show hole selection when in event mode
        holeSelectionLabel.setVisible(!enabled);
        holeGridPanel.setVisible(!enabled);
        partySizePanel.setVisible(!enabled);
        
        if (enabled) {
            // Clear selected hole since events don't use it
            selectedHole = -1;
            for (JButton b : holeButtons) {
                b.setBackground(Color.WHITE);
                b.setForeground(Color.BLACK);
            }
        }
        
        revalidate();
        repaint();
    }

    private String[] generateTimeSlots() {
        String[] slots = new String[17]; // 17 slots from 9:00 AM to 5:00 PM
        int index = 0;

        for (int hour = 9; hour <= 17; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                if (hour == 17 && minute > 0) break;
                String period = hour < 12 ? "AM" : "PM";
                int displayHour = hour <= 12 ? hour : hour - 12;
                slots[index++] = "%d:%02d %s".formatted(displayHour, minute, period);
            }
        }

        return slots;
    }

    private void showTimePicker() {
        String[] times = generateTimeSlots();
        JList<String> timeList = new JList<>(times);
        timeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timeList.setFont(new Font("Arial", Font.PLAIN, 14));
        timeList.setSelectedValue(selectedTime, true);

        JScrollPane scrollPane = new JScrollPane(timeList);
        scrollPane.setPreferredSize(new Dimension(150, 200));

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Select Time", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && timeList.getSelectedValue() != null) {
            selectedTime = timeList.getSelectedValue();
            timeButton.setText(selectedTime);
        }
    }

    private void showCalendar() {
        CalendarDialog dialog = new CalendarDialog(this);
        dialog.setVisible(true);

        LocalDate selectedDate = dialog.getSelectedDate();
        if (selectedDate != null) {
            dateField.setText(selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        }
    }

    private JButton createHoleButton(int holeNumber) {
        JButton button = new JButton("" + holeNumber);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JButton btn : holeButtons) {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(Color.BLACK);
                }

                selectedHole = holeNumber;
                button.setBackground(new Color(70, 130, 180));
                button.setForeground(Color.WHITE);

                System.out.println("Hole " + holeNumber + " selected");
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenuGUI(username, client));
        } else if (e.getSource() == calendarButton) {
            showCalendar();
        } else if (e.getSource() == timeButton) {
            showTimePicker();
        } else if (e.getSource() == continueButton) {
            // Validation - only need hole selection for non-events
            if (!eventCheckBox.isSelected() && selectedHole == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a hole.",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            saveReservation();
        }
    }

    private void saveReservation() {
        try {
            String date = dateField.getText();
            String time = selectedTime;
            boolean isEvent = eventCheckBox.isSelected();
            boolean needsCart = cartCheckBox.isSelected();
            boolean sendCalendarInvite = calendarInviteCheckBox.isSelected();

            // Convert date UI -> DB format yyyy-MM-dd
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
            String formattedDate = parsedDate.format(outputFormatter);

            // Basic conflict check for non-event reservations
            com.project.golf.database.Database db = com.project.golf.database.Database.getInstance();
            if (!isEvent) {
                String teeBox = "Hole " + selectedHole;
                java.util.ArrayList<Reservations> existingReservations = db.getReservationsByDate(formattedDate);
                for (Reservations existing : existingReservations) {
                    if (editingReservationId != null && existing.getReservationId().equals(editingReservationId)) {
                        continue;
                    }
                    if (existing.getTime().equals(time) && existing.getTeeBox().equals(teeBox)) {
                        JOptionPane.showMessageDialog(this,
                                "This time slot is already booked for " + teeBox + " at " + time + ".\nPlease select a different time or hole.",
                                "Time Slot Unavailable",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Use timestamp-based reservation ID
            String reservationId = "R" + System.currentTimeMillis();

            // If marked as event, create Event object (always 200 people, all holes)
            if (isEvent) {
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");
                LocalTime startTime = LocalTime.parse(selectedTime, timeFormat);
                LocalDateTime startDateTime = LocalDateTime.of(parsedDate, startTime);
                
                // Get duration from dropdown
                int hours = Integer.parseInt((String) eventHoursCombo.getSelectedItem());
                int durationMinutes = hours * 60;
                LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

                String endDateStr = endDateTime.toLocalDate().format(outputFormatter);
                String endTimeStr = endDateTime.toLocalTime().format(timeFormat);

                double price = 0.0;

                // Event constructor will automatically set partySize=200 and teeBox="All"
                Event event = new Event(reservationId, username, formattedDate, selectedTime,
                        200, "All", price, endDateStr, endTimeStr);

                boolean added = db.addReservation(event);
                if (added) {
                    if (editingReservationId != null) {
                        db.removeReservation(editingReservationId);
                    }
                    db.saveToFile();

                    String cartInfo = needsCart ? " with golf cart" : "";
                    String action = editingReservationId != null ? "updated" : "confirmed";
                    
                    JOptionPane.showMessageDialog(this,
                            "Event Reservation %s!\n\nReservation ID: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\nAll Holes\nParty Size: 200%s\nDuration: %d hours".formatted(
                                    action, reservationId, date, time, endTimeStr, cartInfo, hours),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose();
                    SwingUtilities.invokeLater(() -> new ManageReservationsGUI(username, client));
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event reservation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            // Normal reservation (not an event)
            int partySize = Integer.parseInt((String) partySizeCombo.getSelectedItem());
            String teeBox = "Hole " + selectedHole;
            
            Reservations reservation = new Reservations(reservationId, username, formattedDate, time, partySize, teeBox, 0.0, false);
            boolean added = db.addReservation(reservation);
            if (added) {
                if (editingReservationId != null) {
                    db.removeReservation(editingReservationId);
                }

                db.saveToFile(); // Persist changes to disk
                
                /**
                 * Send calendar invite if user requested it.
                 * Retrieve user's email from database and send iCalendar invite.
                 */
                if (sendCalendarInvite) {
                    String userEmail = db.getUserEmail(username);
                    if (userEmail != null && !userEmail.isEmpty()) {
                        new Thread(() -> {
                            boolean emailSent = com.project.golf.utils.EmailSender.sendCalendarInvite(
                                userEmail, date, time, selectedHole, partySize, reservationId
                            );
                            if (!emailSent) {
                                System.err.println("Failed to send calendar invite to: " + userEmail);
                            }
                        }).start();
                    }
                }
                
                String cartInfo = needsCart ? " with golf cart" : "";
                String action = editingReservationId != null ? "updated" : "confirmed";
                String calendarInfo = sendCalendarInvite ? "\nCalendar invite will be sent to your email." : "";
                JOptionPane.showMessageDialog(this,
                        "Reservation %s!\n\nReservation ID: %s\nDate: %s\nTime: %s\nHole: %d\nParty Size: %d%s%s".formatted(
                                action, reservationId, date, time, selectedHole, partySize, cartInfo, calendarInfo),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Return to reservation management screen
                dispose();
                SwingUtilities.invokeLater(() -> new ManageReservationsGUI(username, client));
            } else {
                JOptionPane.showMessageDialog(this, "Failed to make reservation. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error making reservation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // For quick testing:
        // SwingUtilities.invokeLater(() -> new MakeReservationGUI("testUser", new Client("localhost",5050)));
    }
}

/**
 * CalendarDialog - Custom calendar picker with month view
 */
class CalendarDialog extends JDialog {
    private LocalDate selectedDate = null;
    private LocalDate currentMonth;
    private JLabel monthYearLabel;
    private JPanel daysPanel;
    private JButton prevButton;
    private JButton nextButton;

    public CalendarDialog(JFrame parent) {
        super(parent, "Select Date", true);
        currentMonth = LocalDate.now().withDayOfMonth(1);

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Top panel with month navigation
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        prevButton = new JButton("◀");
        prevButton.addActionListener(e -> changeMonth(-1));

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 16));

        nextButton = new JButton("▶");
        nextButton.addActionListener(e -> changeMonth(1));

        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthYearLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Days of week header
        JPanel headerPanel = new JPanel(new GridLayout(1, 7));
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            headerPanel.add(label);
        }
        add(headerPanel, BorderLayout.CENTER);

        // Days panel
        daysPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        add(daysPanel, BorderLayout.SOUTH);

        updateCalendar();
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        updateCalendar();
    }

    private void updateCalendar() {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusMonths(6);

        // Update navigation buttons
        prevButton.setEnabled(!currentMonth.isBefore(today.withDayOfMonth(1)));
        nextButton.setEnabled(!currentMonth.isAfter(maxDate.withDayOfMonth(1)));

        // Update month/year label
        monthYearLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Clear days panel
        daysPanel.removeAll();

        // Get first day of month and number of days
        YearMonth yearMonth = YearMonth.from(currentMonth);
        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = currentMonth.getDayOfWeek().getValue() % 7; // 0 = Sunday

        // Add empty cells before first day
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Add day buttons
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 12));

            // Disable past dates and dates beyond 6 months
            if (date.isBefore(today) || date.isAfter(maxDate)) {
                dayButton.setEnabled(false);
                dayButton.setBackground(Color.LIGHT_GRAY);
            } else {
                // Highlight today
                if (date.equals(today)) {
                    dayButton.setBackground(new Color(173, 216, 230));
                }

                final LocalDate selectedDate = date;
                dayButton.addActionListener(e -> {
                    this.selectedDate = selectedDate;
                    dispose();
                });
            }

            daysPanel.add(dayButton);
        }

        // Add empty cells after last day
        int totalCells = firstDayOfWeek + daysInMonth;
        int remainingCells = 42 - totalCells; // 6 rows * 7 days
        for (int i = 0; i < remainingCells; i++) {
            daysPanel.add(new JLabel(""));
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}