package com.project.golf.reservation;

/**
 * ReservationsInterface.java
 *
 * Interface for Reservations class
 * Defines all methods for managing golf course reservations
 *
 * @author Ethan Billau (ethanbillau), L15
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
