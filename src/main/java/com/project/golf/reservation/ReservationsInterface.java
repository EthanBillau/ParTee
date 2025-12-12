package com.project.golf.reservation;

/**
 * ReservationsInterface.java
 *
 * Interface defining golf course reservation data and operations.
 * Specifies contracts for tee time reservation management and state tracking.
 *
 * Data structures: Reservation details (ID, user, date, time, party size, tee box, price),
 * payment and approval state flags.
 * Algorithm: State tracking pattern with getter/setter accessors.
 * Features: Reservation property access, payment status management, approval state tracking,
 * reservation comparison and serialization.
 * 
 * @author Ethan Billau (ethanbillau), L15
 *
 * @version November 13, 2025
 */

public interface ReservationsInterface {
    
 /**
  * Getter methods
  *  
  * @returns ID
  * @returns username
  * @returns data
  * @return stime
  * @returns party size
  * @returns teebox
  * @returns price
  * @return ispaid
  */  
    
    String getReservationId();
    String getUsername();
    String getDate();
    String getTime();
    int getPartySize();
    String getTeeBox();
    double getPrice();
    boolean getIsPaid();
    boolean isEvent();

    // Setter methods
    void setIsPaid(boolean isPaid);
    void setPrice(double price);
    void setPartySize(int partySize);
    void setTeeBox(String teeBox);
    
    // Other methods
    String toString();
    boolean equals(Object o);
    String toFileString();
}
