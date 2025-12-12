package com.project.golf.reservation;

import java.util.Objects;

/**
 * Reservations.java
 *
 * Reservations class for managing golf course tee time reservations Implements
 * ReservationsInterface
 *
 * @author Anoushka Chakravarty (chakr181), Aman Wakankar (awakanka), L15
 * @version November 6, 2025
 */
public class Reservations implements ReservationsInterface {

    private String reservationId;
    private String username;
    private String date;
    private String time;
    private int partySize;
    private String teeBox;
    private double price;
    private boolean isPaid;

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
        return String.format("%s,%s,%s,%s,%d,%s,%.2f,%b",
                reservationId, username, date, time, partySize, teeBox, price, isPaid);
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

            // EVENT FORMAT: EVENT,id,user,date,time,party,tee,price,isPaid,endDate,endTime
            if (parts[0].equals("EVENT")) {
                return new com.project.golf.events.Event(
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
            }

            // STANDARD RESERVATIONS
            if (parts.length != 8) {
                return null;
            }

            return new Reservations(
                    parts[0], parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5],
                    Double.parseDouble(parts[6]),
                    Boolean.parseBoolean(parts[7])
            );

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
