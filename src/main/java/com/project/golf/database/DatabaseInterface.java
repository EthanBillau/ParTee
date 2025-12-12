package com.project.golf.database;

import com.project.golf.events.*;
import com.project.golf.reservation.*;
import com.project.golf.settings.*;
import com.project.golf.users.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * DatabaseInterface.java
 *
 * <p>Interface defining central data management operations for golf reservation system. Specifies
 * contracts for user, reservation, event, and settings data operations.
 *
 * <p>Data structures: User management, reservation/event storage, course settings. Algorithm:
 * Persistence layer operations with file-based caching strategy. Features: CRUD operations for all
 * domain objects, thread-safe access patterns, file persistence synchronization, data query and
 * filtering.
 *
 * @author Aman Wakankar (awakanka), L15
 * @version November 9, 2025
 */
public interface DatabaseInterface {

  // USER MANAGEMENT --------------------------------------------------

  /**
   * Adds new user to database
   *
   * @param user the User object to add
   * @return true if user was added successfully, false if username already exists
   */
  boolean addUser(User user);

  /**
   * Removes a user from the database
   *
   * @param username the username of the user to remove
   * @return true if user was removed successfully, false if user not found
   */
  boolean removeUser(String username);

  /**
   * Finds a user by username
   *
   * @param username the username to search for
   * @return the User object if found, null otherwise
   */
  User findUser(String username);

  /**
   * Gets all users in the database
   *
   * @return ArrayList of all users
   */
  ArrayList<User> getAllUsers();

  /**
   * Validates user login credentials
   *
   * @param username the username
   * @param password the password
   * @return true if credentials are valid, false otherwise
   */
  boolean validateLogin(String username, String password);

  // RESERVATION MANAGMENT --------------------------------------------------

  /**
   * Adds a new reservation to the database
   *
   * @param reservation the Reservations object to add
   * @return true if reservation was added successfully, false otherwise
   */
  boolean addReservation(Reservations reservation);

  /**
   * Removes a reservation from the database
   *
   * @param reservationId the ID of the reservation to remove
   * @return true if reservation was removed successfully, false if not found
   */
  boolean removeReservation(String reservationId);

  /**
   * Finds a reservation by ID
   *
   * @param reservationId the reservation ID to search for
   * @return the Reservations object if found, null otherwise
   */
  Reservations findReservation(String reservationId);

  /**
   * Gets all reservations for a specific user
   *
   * @param username the username to get reservations for
   * @return ArrayList of all reservations for the user
   */
  ArrayList<Reservations> getReservationsByUser(String username);

  /**
   * Gets all reservations for a specific date
   *
   * @param date the date to get reservations for (format: YYYY-MM-DD)
   * @return ArrayList of all reservations for the date
   */
  ArrayList<Reservations> getReservationsByDate(String date);

  /**
   * Gets all reservations in the database
   *
   * @return ArrayList of all reservations
   */
  ArrayList<Reservations> getAllReservations();

  // EVENT MANAGEMENT --------------------------------------------------

  /**
   * Adds a new event to the database
   *
   * @param event the Event object to add
   * @return true if event was added successfully, false otherwise
   */
  boolean addEvent(Event event);

  /**
   * Removes an event from the database
   *
   * @param eventId the ID of the event to remove
   * @return true if event was removed successfully, false if not found
   */
  boolean removeEvent(String eventId);

  /**
   * Finds an event by ID
   *
   * @param eventId the event ID to search for
   * @return the Event object if found, null otherwise
   */
  Event findEvent(String eventId);

  /**
   * Gets all events in the database
   *
   * @return ArrayList of all events
   */
  ArrayList<Event> getAllEvents();

  // DATA PERSISTENCE --------------------------------------------------

  /**
   * Saves all database data to disk Creates/updates files for users, reservations, and events
   *
   * @throws IOException if there's an error writing to files
   */
  void saveToFile() throws IOException;

  /**
   * Loads all database data from disk Reads data from files for users, reservations, and events
   *
   * @throws IOException if there's an error reading from files
   */
  void loadFromFile() throws IOException;

  /** Clears all data from the database (for tests) */
  void clearAllData();

  public boolean addTeeTime(TeeTime teeTime);

  public boolean removeTeeTime(String teeTimeId);

  public TeeTime findTeeTime(String teeTimeId);

  public ArrayList<TeeTime> getTeeTimesByDate(String date);

  public ArrayList<TeeTime> getAllTeeTimes();

  public CourseSettings getCourseSettings();

  public void setCourseSettings(CourseSettings settings);
}
