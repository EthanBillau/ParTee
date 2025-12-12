package com.project.golf.reservation;

import java.util.Objects;

/**
 * Reservations.java
 *
 * Manages individual golf course tee time reservations with payment and approval tracking.
 * Core model class for golf booking system storing all reservation details.
 *
 * Data structures: String fields for ID, username, date, time, teeBox;
 * int partySize, double price for cost calculation; boolean flags isPaid and isPending
 * for state tracking.
 * Algorithm: Direct storage of reservation attributes with getter/setter accessors,
 * equals/hashCode based on reservationId, toFileString() serialization for persistence.
 * Features: Reservation creation, payment state tracking, approval status management,
 * file serialization, object comparison.
 *
 * @author Anoushka Chakravarty (chakr181), Aman Wakankar (awakanka), L15
 *
 * @version November 6, 2025
 */

public class Reservations implements ReservationsInterface {

    private String reservationId;  // unique identifier for this reservation
    private String username;  // username of the user who made this reservation
    private String date;  // tee time date in system date format (e.g., MM/DD/YYYY)
    private String time;  // tee time in system time format (e.g., HH:MM AM/PM)
    private int partySize;  // number of golfers in the party
    private String teeBox;  // which tee box or hole designation
    private double price;  // total cost of the reservation
    private boolean isPaid;  // true if payment has been received
    private boolean isPending;  // true if awaiting admin approval

    /**
     * Constructor for Reservations (defaults to unpaid)
     *
     * @param reservationId unique identifier for the reservation
     * @param username user who made the reservation
     * @param date date of the tee time
     * @param time time of the tee time
     * @param partySize number of golfers
     * @param teeBox which tee box/hole
     * @param price total cost of reservation
     */
    public Reservations(String reservationId, String username, String date,
            String time, int partySize, String teeBox, double price) {
        this.reservationId = reservationId;
        this.username = username;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.teeBox = teeBox;
        this.price = price;
        this.isPaid = false; // Default to unpaid
        this.isPending = false; // Default to confirmed
    }

    /**
     * Constructor for Reservations with payment status (used when loading from
     * file)
     *
     * @param reservationId unique identifier for the reservation
     * @param username user who made the reservation
     * @param date date of the tee time
     * @param time time of the tee time
     * @param partySize number of golfers
     * @param teeBox which tee box/hole
     * @param price total cost of reservation
     * @param isPaid whether the reservation has been paid for
     */
    public Reservations(String reservationId, String username, String date,
            String time, int partySize, String teeBox, double price, boolean isPaid) {
        this.reservationId = reservationId;
        this.username = username;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.teeBox = teeBox;
        this.price = price;
        this.isPaid = isPaid;
        this.isPending = false; // Default to confirmed
    }

    /**
     * Constructor for Reservations with all fields including pending status
     *
     * @param reservationId unique identifier for the reservation
     * @param username user who made the reservation
     * @param date date of the tee time
     * @param time time of the tee time
     * @param partySize number of golfers
     * @param teeBox which tee box/hole
     * @param price total cost of reservation
     * @param isPaid whether the reservation has been paid for
     * @param isPending whether the reservation is pending admin approval
     */
    public Reservations(String reservationId, String username, String date,
            String time, int partySize, String teeBox, double price, boolean isPaid, boolean isPending) {
        this.reservationId = reservationId;
        this.username = username;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.teeBox = teeBox;
        this.price = price;
        this.isPaid = isPaid;
        this.isPending = isPending;
    }

    /**
     * Gets the reservation ID
     *
     * @return the unique reservation ID
     */
    @Override
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Gets the username
     *
     * @return username of person who made reservation
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Gets the date
     *
     * @return date of the tee time
     */
    @Override
    public String getDate() {
        return date;
    }

    /**
     * Gets the time
     *
     * @return time of the tee time
     */
    @Override
    public String getTime() {
        return time;
    }

    /**
     * Gets the party size
     *
     * @return number of golfers in the party
     */
    @Override
    public int getPartySize() {
        return partySize;
    }

    /**
     * Gets the tee box
     *
     * @return which tee box or hole
     */
    @Override
    public String getTeeBox() {
        return teeBox;
    }

    /**
     * Gets the price
     *
     * @return total price of reservation
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Gets payment status
     *
     * @return true if paid, false otherwise
     */
    @Override
    public boolean getIsPaid() {
        return isPaid;
    }

    /**
     * Gets pending status
     *
     * @return true if pending admin approval, false otherwise
     */
    public boolean isPending() {
        return isPending;
    }

    /**
     * Sets pending status
     *
     * @param pending whether this reservation is pending approval
     */
    public void setPending(boolean pending) {
        this.isPending = pending;
    }

    /**
     * Indicates if this reservation is an event
     * 
     * @return false, unless overridden in subclass
     */
    @Override
    public boolean isEvent() {
        return false;
    }

    /**
     * Sets isPaid to parameter
     *
     * @param isPaid boolean value on reservation payment status
     */
    @Override
    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    /**
     * Sets price to parameter
     *
     * @param price total cost of reservation
     */
    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets party size to parameter
     *
     * @param partySize number of golfers in the party
     */
    @Override
    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    /**
     * Sets tee box to parameter
     *
     * @param teeBox which tee box or hole
     */
    @Override
    public void setTeeBox(String teeBox) {
        this.teeBox = teeBox;
    }

    /**
     * Convert reservation to file string for persistence Format:
     * reservationId,username,date,time,partySize,teeBox,price,isPaid
     *
     * @return formatted string representation
     */
    @Override
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%d,%s,%.2f,%b,%b",
                reservationId, username, date, time, partySize, teeBox, price, isPaid, isPending);
    }

    /**
     * Create Reservations from file string
     *
     * @param fileString format:
     * reservationId,username,date,time,partySize,teeBox,price,isPaid
     * @return Reservations object or null if invalid
     */
    public static Reservations fromFileString(String fileString) {
        if (fileString == null || fileString.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = fileString.split(",");

            // EVENT FORMAT: EVENT,id,user,date,time,party,tee,price,isPaid,endDate,endTime[,isPending]
            if (parts[0].equals("EVENT")) {
                com.project.golf.events.Event event = new com.project.golf.events.Event(
                        parts[1], // reservationId
                        parts[2], // username
                        parts[3], // date
                        parts[4], // time
                        Integer.parseInt(parts[5]), // partySize (ignored)
                        parts[6], // teeBox (ignored)
                        Double.parseDouble(parts[7]), // price
                        parts[9], // endDate
                        parts[10] // endTime
                );
                // Only set pending if the flag exists (backward compatible)
                if (parts.length >= 12) {
                    event.setPending(Boolean.parseBoolean(parts[11]));
                }
                return event;
            }

            // STANDARD RESERVATIONS - support both old (8 parts) and new (9 parts) formats
            if (parts.length < 8) {
                return null;
            }

            Reservations reservation = new Reservations(
                    parts[0], parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5],
                    Double.parseDouble(parts[6]),
                    Boolean.parseBoolean(parts[7])
            );
            // Only set pending if the flag exists (backward compatible)
            if (parts.length >= 9) {
                reservation.setPending(Boolean.parseBoolean(parts[8]));
            }
            return reservation;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns string representation of the reservation
     *
     * @return formatted reservation details
     */
    @Override
    public String toString() {
        return String.format("Reservation ID: %s\nUsername: %s\nDate: %s\nTime: %s\n"
                + "Party Size: %d\nTee Box: %s\nPrice: $%.2f\nPaid: %s",
                reservationId, username, date, time, partySize,
                teeBox, price, isPaid ? "Yes" : "No");
    }

    /**
     * Check equality with another object
     *
     * @param obj object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Reservations other = (Reservations) obj;
        return this.isPaid == other.isPaid
                && this.partySize == other.partySize
                && Double.compare(this.price, other.price) == 0
                && Objects.equals(this.reservationId, other.reservationId)
                && Objects.equals(this.username, other.username)
                && Objects.equals(this.date, other.date)
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.teeBox, other.teeBox);
    }

    /**
     * Generate hash code for this reservation
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(reservationId, username, date, time, partySize, teeBox, price, isPaid);
    }
}
