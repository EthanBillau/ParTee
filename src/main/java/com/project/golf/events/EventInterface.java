package com.project.golf.events;

import com.project.golf.reservation.*;
    
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * EventInterface.java
 *
 * This interface defines the contract for a capacity-based Event that manages
 * reservations. Booking works with the project's Reservations
 * class and operates using party sizes rather than individual seats.
 *
 * @author Nikhil Kodali (kodali3), Ethan Billau (ethanbillau) L15
 *
 * @version November 7, 2025
 *
 */

// Interface for a capacity-based event using Reservations
public interface EventInterface {

    // Getters
    String getEndDate();
    String getEndTime();
    boolean isEvent();
    String getName();
    String getId();
}
