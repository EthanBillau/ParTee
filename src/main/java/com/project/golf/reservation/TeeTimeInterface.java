package com.project.golf.reservation;

import java.util.ArrayList;

/**
 * TeeTimeInterface.java
 * 
 * Interface defining golf course tee time slot management and reservation booking.
 * Specifies contracts for tee time capacity and reservation operations.
 *
 * Data structures: Tee time properties (ID, date, time, tee box, maxPartySize, price),
 * ArrayList of reservations for bookings.
 * Algorithm: Capacity validation with party size checking, ArrayList iteration
 * for reservation management.
 * Features: Tee time property access, reservation booking/cancellation, capacity validation,
 * party size calculation, file serialization.
 * 
 * @author Anoushka Chakravarty (chakr181), L15
 *
 * @version November 9, 2025
 */

public interface TeeTimeInterface {
    
    // Getters
    String getTeeTimeId();
    String getDate();
    String getTime();
    String getTeeBox();
    int getMaxPartySize();
    int getReservedSpots();
    int getAvailableSpots();
    double getPricePerPerson();

    // Setter
    void setPricePerPerson(double price);
    
    
    /**
     * Check if this tee time is available for a party of given size
     * 
     * @param partySize number of golfers
     * @return true if space available, false otherwise
     */
    
    boolean isAvailable(int partySize);
    
    /**
     * Check if this tee time has been completely booked
     * 
     * @return true if no spots available, false otherwise
     */
    
    boolean isFullyBooked();
    
    /**
     * Book spots for a party (creates and adds a reservation)
     * 
     * @param partySize number of golfers
     * @param username user making the booking
     * @return the created Reservations object, or null if unavailable
     */
    
    Reservations bookTeeTime(int partySize, String username);
    
    /**
     * Cancel a reservation and free up spots
     * 
     * @param reservationId ID of reservation to cancel
     * @return true if cancelled successfully, false if not found
     */
    
    boolean cancelReservation(String reservationId);
    
    /**
     * Get all reservations for this tee time
     * 
     * @return ArrayList of all reservations
     */
    
    ArrayList<Reservations> getReservations();
    
    /**
     * Get a specific reservation by ID
     * 
     * @param reservationId reservation identifier
     * @return Reservations object if found, null otherwise
     */
    
    Reservations getReservation(String reservationId);
    
    /**
     * Convert tee time to string format for file storage
     * 
     * @return formatted string representation
     */
    
    String toFileString();
}
