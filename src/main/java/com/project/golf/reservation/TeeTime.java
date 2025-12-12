package com.project.golf.reservation;

import java.util.ArrayList;

/**
 * TeeTime.java
 * 
 * Represents a specific tee time slot with capacity and reservation management.
 * Tracks available golf course times with booking capacity and pricing.
 *
 * Data structures: Final String fields for teeTimeId, date, time, teeBox,
 * final int maxPartySize, double pricePerPerson, ArrayList of Reservations for bookings.
 * Static int teeTimeCounter for ID generation. 
 * Algorithm: Counter-based ID generation, ArrayList for reservation storage,
 * capacity checking against maxPartySize, price calculation per party size.
 * Features: Tee time creation and tracking, reservation management, capacity validation,
 * pricing calculation, ID generation, file serialization.
 *
 * @author Anoushka Chakravarty (chakr181), L15
 *
 * @version November 9, 2025
 */

public class TeeTime implements TeeTimeInterface {
    
    private static int teeTimeCounter = 1;  // counter for generating unique tee time IDs
    
    private final String teeTimeId;  // unique identifier for this tee time slot
    private final String date;  // date of the tee time in system date format
    private final String time;  // time of the tee time in system time format (HH:MM)
    private final String teeBox;  // tee box or hole designation (e.g., "Hole 1")
    private final int maxPartySize;  // maximum number of golfers allowed for this time
    private double pricePerPerson;  // price charged per golfer for this tee time
    
    private final ArrayList<Reservations> reservations;  // list of all reservations for this tee time
    
    /**
     * Constructor for TeeTime
     *
     * @param date           date of tee time (YYYY-MM-DD)
     * @param time           time of tee time (HH:MM)
     * @param teeBox         which hole/tee box (e.g., "Hole 1")
     * @param maxPartySize   maximum golfers allowed (typically 4)
     * @param pricePerPerson price per golfer
     * @param perPerson
     */
    
    public TeeTime(String date, String time, String teeBox, int maxPartySize, double pricePerPerson, double perPerson) {
        this.teeTimeId = generateTeeTimeId();
        this.date = date;
        this.time = time;
        this.teeBox = teeBox;
        this.maxPartySize = maxPartySize;
        this.pricePerPerson = pricePerPerson;
        this.reservations = new ArrayList<>();
    }
    
    /**
     * Constructor for loading from file
     * 
     * @param teeTimeId existing ID
     * @param date date of tee time
     * @param time time of tee time
     * @param teeBox which hole/tee box
     * @param maxPartySize maximum party size
     * @param pricePerPerson price per golfer
     */
    
    public TeeTime(String teeTimeId, String date, String time, String teeBox, 
                   int maxPartySize, double pricePerPerson) {
        this.teeTimeId = teeTimeId;
        this.date = date;
        this.time = time;
        this.teeBox = teeBox;
        this.maxPartySize = maxPartySize;
        this.pricePerPerson = pricePerPerson;
        this.reservations = new ArrayList<>();
    }
    
    /**
     * Generate unique tee time ID
     * 
     * @return new unique ID (format: TT1, TT2, etc.)
     */
    
    private static synchronized String generateTeeTimeId() {
        String id = "TT" + teeTimeCounter;
        teeTimeCounter++;
        return id;
    }
    
    /**
     * Generate unique reservation ID
     * 
     * @return new unique reservation ID
     */
    
    private static synchronized String generateReservationId() {
        return "R" + System.currentTimeMillis();
    }

    //Getters
    @Override
    public String getTeeTimeId() {
        return teeTimeId;
    }
    
    @Override
    public String getDate() {
        return date;
    }
    
    @Override
    public String getTime() {
        return time;
    }
    
    @Override
    public String getTeeBox() {
        return teeBox;
    }
    
    @Override
    public int getMaxPartySize() {
        return maxPartySize;
    }
    
    @Override
    public synchronized int getReservedSpots() {
        int total = 0;
        for (Reservations r : reservations) {
            total += r.getPartySize();
        }
        return total;
    }
    
    @Override
    public synchronized int getAvailableSpots() {
        return maxPartySize - getReservedSpots();
    }
    
    @Override
    public double getPricePerPerson() {
        return pricePerPerson;
    }

    //Setters
    @Override
    public synchronized void setPricePerPerson(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.pricePerPerson = price;
    }
    
    @Override
    public synchronized boolean isAvailable(int partySize) {
        if (partySize <= 0) {
            return false;
        }
        return getAvailableSpots() >= partySize;
    }
    
    @Override
    public synchronized boolean isFullyBooked() {
        return getAvailableSpots() == 0;
    }
    
    @Override
    public synchronized Reservations bookTeeTime(int partySize, String username) {
        // Validate inputs
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        // Check availability
        if (!isAvailable(partySize)) {
            return null; // Not enough space
        }
        
        // Calculate total price
        double totalPrice = pricePerPerson * partySize;
        
        // Create reservation
        String reservationId = generateReservationId();
        Reservations reservation = new Reservations(
            reservationId,
            username,
            date,
            time,
            partySize,
            teeBox,
            totalPrice
        );
        
        // Add to list
        reservations.add(reservation);
        
        return reservation;
    }
    
    @Override
    public synchronized boolean cancelReservation(String reservationId) {
        if (reservationId == null) {
            return false;
        }
        
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getReservationId().equals(reservationId)) {
                reservations.remove(i);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public synchronized ArrayList<Reservations> getReservations() {
        return new ArrayList<>(reservations);
    }
    
    @Override
    public synchronized Reservations getReservation(String reservationId) {
        if (reservationId == null) {
            return null;
        }
        
        for (Reservations r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                return r;
            }
        }
        
        return null;
    }
    
    @Override
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%d,%.2f",
            teeTimeId, date, time, teeBox, maxPartySize, pricePerPerson);
    }
    
    /**
     * Create TeeTime from file string
     * 
     * @param fileString format: teeTimeId,date,time,teeBox,maxPartySize,pricePerPerson
     * @return TeeTime object or null if invalid
     */
    
    public static TeeTime fromFileString(String fileString) {
        if (fileString == null || fileString.trim().isEmpty()) {
            return null;
        }
        
        try {
            String[] parts = fileString.split(",");
            if (parts.length != 6) {
                return null;
            }
            
            String teeTimeId = parts[0];
            String date = parts[1];
            String time = parts[2];
            String teeBox = parts[3];
            int maxPartySize = Integer.parseInt(parts[4]);
            double pricePerPerson = Double.parseDouble(parts[5]);
            
            return new TeeTime(teeTimeId, date, time, teeBox, maxPartySize, pricePerPerson);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.format("TeeTime[%s] %s at %s - %s (Available: %d/%d, $%.2f/person)",
            teeTimeId, date, time, teeBox, getAvailableSpots(), maxPartySize, pricePerPerson);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TeeTime other = (TeeTime) obj;
        return teeTimeId.equals(other.teeTimeId);
    }
    
    @Override
    public int hashCode() {
        return teeTimeId.hashCode();
    }
}
