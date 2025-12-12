package com.project.golf.events;

/**
 * EventInterface.java
 *
 * <p>Interface defining multi-day or timed event reservation management. Specifies contracts for
 * extended-duration reservations with end date/time.
 *
 * <p>Data structures: Event duration tracking (endDate, endTime) extending Reservations. Algorithm:
 * Extends base Reservations contract with additional temporal fields. Features: Event duration
 * property access, extended reservation semantics, multi-day event support, calendar integration.
 *
 * @author Nikhil Kodali (kodali3), Ethan Billau (ethanbillau), L15
 * @version November 7, 2025
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
