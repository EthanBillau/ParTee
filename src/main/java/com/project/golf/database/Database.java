package com.project.golf.database;

import com.project.golf.events.*;
import com.project.golf.reservation.*;
import com.project.golf.settings.*;
import com.project.golf.users.*;
import com.project.golf.utils.PasswordUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Database.java
 * 
 * Central data management system for the ParTee golf reservation system.
 * Implements Singleton pattern to ensure single instance manages all persistent data.
 * Thread-safe operations using ReentrantReadWriteLock for concurrent access.
 *
 * Data structures: ArrayLists for users, reservations, events, and tee times.
 * Algorithm: File-based persistence with in-memory caching for fast access.
 * Major features: User management, reservation handling, event approval, tee time scheduling.
 *
 * @author Aman Wakankar (awakanka), Anoushka Chakravarty (chakr181),
 *         Connor Landzettel (clandzet), Nikhil Kodali (kodali3), Ethan Billau (ethanbillau), L15
 *
 * @version November 9, 2025
 */

public class Database implements DatabaseInterface {

    // Singleton instance - single Database manages all data
    private static Database instance = null;
    
    // In-memory data structures for system data
    private ArrayList<User> users;                     // all registered users
    private ArrayList<Reservations> reservations;     // all golf reservations
    private ArrayList<Event> events;                   // all pending events
    private ArrayList<TeeTime> teeTimes;               // all available tee times
    private CourseSettings courseSettings;             // golf course operational settings
    
    // File paths for data persistence
    private static final String USERS_FILE = "users.txt";              // user account storage
    private static final String RESERVATIONS_FILE = "reservations.txt"; // reservation records
    // Note: Events are stored as part of reservations.txt (Event extends Reservations)
    // Pending status is tracked using isPending flag on each reservation/event
    private static final String TEETIMES_FILE = "teetimes.txt";        // available tee time slots
    private static final String SETTINGS_FILE = "settings.txt";        // course configuration
    
    // Thread synchronization for concurrent access safety
    private final ReentrantReadWriteLock lock;                  // main synchronization lock
    private final ReentrantReadWriteLock.ReadLock readLock;     // lock for read operations
    private final ReentrantReadWriteLock.WriteLock writeLock;   // lock for write operations
    
    /**
     * Private constructor for Database (Singleton pattern which is like static but different)
     * Initializes all data structures and loads existing data from disk
     */
    
    private Database() {
        users = new ArrayList<>();
        reservations = new ArrayList<>();
        events = new ArrayList<>();
        teeTimes = new ArrayList<>();
        courseSettings = new CourseSettings(); // Initialize with defaults
        
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        
        // Try to load existing data
        try {
            loadFromFile();
        } catch (IOException e) {
            // Starts empty if no data
            System.out.println("No existing data files found. Starting with empty database.");
        }
    }
    
    /**
     * Get the singleton instance of Database
     * Thread-safe lazy initialization
     * 
     * @return the single Database instance
     */
    
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    /**
     * Reset the singleton instance (for testing purposes ONLy)
     */
    
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.clearAllData();
            instance = null;
        }
    }
    
    // USER MANAGEMENT --------------------------------------------------
    
    /**
     * Adds a new user to the database
     * Thread-safe operation using write lock
     * 
     * @param user the User object to add
     * @return true if user was added successfully,
     *         false if username already exists
     */
    
    @Override
    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Checks if username already exists
            for (User u : users) {
                if (u.getUsername().equals(user.getUsername())) {
                    return false;
                }
            }
            // Check if email already exists
            for (User u : users) {
                if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(u.getEmail())) {
                    return false;
                }
            }
            users.add(user);
            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Removes a user from the database
     * thread safe ops using write lock
     * 
     * @param username the username of the user to remove
     * @return true if user was removed successfully,
     *         false if user not found
     */
    
    @Override
    public boolean removeUser(String username) {
        if (username == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(username)) {
                    users.remove(i);
                    return true;
                }
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Finds a user by username
     * thread safe ops using read lock
     * 
     * @param username the username to search for
     * @return the User object if found,
     *         null otherwise
     */
    
    @Override
    public User findUser(String username) {
        if (username == null) {
            return null;
        }
        
        readLock.lock();
        try {
            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    return u;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Finds a user by email
     * thread safe ops using read lock
     * 
     * @param email the email to search for
     * @return the User object if found,
     *         null otherwise
     */
    public User findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        
        readLock.lock();
        try {
            for (User u : users) {
                if (email.equalsIgnoreCase(u.getEmail())) {
                    return u;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all users in the database
     * Thread safe ops using read lock
     * Returns a copy to prevent external modification
     * 
     * @return ArrayList of all users
     */
    
    @Override
    public ArrayList<User> getAllUsers() {
        readLock.lock();
        try {
            return new ArrayList<>(users);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Validates user login credentials
     * Thread safe ops using read lock
     * Supports login with username or email
     * 
     * @param usernameOrEmail the username or email
     * @param password the password (plaintext)
     * @return true if credentials are valid,
     *         false otherwise
     */
    
    @Override
    public boolean validateLogin(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return false;
        }
        
        readLock.lock();
        try {
            // Try to find user by username first, then by email
            User user = findUser(usernameOrEmail);
            if (user == null) {
                user = findUserByEmail(usernameOrEmail);
            }
            
            if (user == null) {
                return false;
            }
            
            String storedPassword = user.getPassword();
            
            // Check if stored password is hashed (BCrypt)
            if (PasswordUtil.isHashed(storedPassword)) {
                return PasswordUtil.verifyPassword(password, storedPassword);
            } else {
                // Legacy plaintext password support
                return storedPassword.equals(password);
            }
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets the email address for a given username
     * Thread safe operation using read lock
     * 
     * @param username the username to look up
     * @return the user's email address, or null if user not found
     */
    public String getUserEmail(String username) {
        if (username == null) {
            return null;
        }
        
        readLock.lock();
        try {
            User user = findUser(username);
            return user != null ? user.getEmail() : null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Updates an existing user's information
     * Thread safe operation using write lock
     * 
     * @param oldUsername the current username of the user to update
     * @param newUsername the new username (can be same as old)
     * @param password the new password
     * @param firstName the new first name
     * @param lastName the new last name
     * @param email the new email
     * @return true if update was successful, false if user not found or new username already taken
     */
    public boolean updateUser(String oldUsername, String newUsername, String password, 
                             String firstName, String lastName, String email) {
        if (oldUsername == null || newUsername == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            User user = findUser(oldUsername);
            if (user == null) {
                return false;
            }
            
            // Check if new username is taken by another user
            if (!oldUsername.equals(newUsername)) {
                User existing = findUser(newUsername);
                if (existing != null) {
                    return false; // Username already taken
                }
            }
            
            // Update user fields
            user.setUsername(newUsername);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            
            // Save to file
            try {
                saveToFile();
                return true;
            } catch (IOException e) {
                System.err.println("Error saving users after update: " + e.getMessage());
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    // RESERVATION MANAGMENT --------------------------------------------------
    
    /**
     * Adds a new reservation to the database
     * thread safe ops using write lock
     * 
     * @param reservation the Reservations object to add
     * @return true if reservation was added successfully,
     *         false otherwise
     */
    
    @Override
    public boolean addReservation(Reservations reservation) {
        if (reservation == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Checks for reservation id
            for (Reservations r : reservations) {
                if (r.getReservationId().equals(reservation.getReservationId())) {
                    return false;
                }
            }
            reservations.add(reservation);
            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Removes a reservation from the database
     * thread safe ops using write lock
     * 
     * @param reservationId the ID of the reservation to remove
     * @return true if reservation was removed successfully,
     *         false if not found
     */
    
    @Override
    public boolean removeReservation(String reservationId) {
        if (reservationId == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            boolean removed = false;
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).getReservationId().equals(reservationId)) {
                    Reservations r = reservations.remove(i);
                    // If it's an event, also remove from events list by ID
                    if (r instanceof Event) {
                        for (int j = 0; j < events.size(); j++) {
                            if (events.get(j).getReservationId().equals(reservationId)) {
                                events.remove(j);
                                break;
                            }
                        }
                    }
                    removed = true;
                    break;
                }
            }
            return removed;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Finds a reservation by ID
     * thread safe ops using read lock
     * 
     * @param reservationId the reservation ID to search for
     * @return the Reservations object if found,
     *         null otherwise
     */
    
    @Override
    public Reservations findReservation(String reservationId) {
        if (reservationId == null) {
            return null;
        }
        
        readLock.lock();
        try {
            for (Reservations r : reservations) {
                if (r.getReservationId().equals(reservationId)) {
                    return r;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all reservations for a specific user
     * thread safe ops using read lock
     * 
     * @param username the username to get reservations for
     * @return ArrayList of all reservations for the user
     */
    
    @Override
    public ArrayList<Reservations> getReservationsByUser(String username) {
        if (username == null) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            ArrayList<Reservations> userReservations = new ArrayList<>();
            for (Reservations r : reservations) {
                if (r.getUsername().equals(username)) {
                    userReservations.add(r);
                }
            }
            return userReservations;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all reservations for a specific date
     * thread safe ops using read lock
     * 
     * @param date the date to get reservations for (format: YYYY-MM-DD)
     * @return ArrayList of all reservations for the date
     */
    
    @Override
    public ArrayList<Reservations> getReservationsByDate(String date) {
        if (date == null) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            ArrayList<Reservations> dateReservations = new ArrayList<>();
            for (Reservations r : reservations) {
                if (r.getDate().equals(date)) {
                    dateReservations.add(r);
                }
            }
            return dateReservations;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all reservations in the database
     * thread safe ops using read lock
     * Returns a copy to prevent external modification
     * 
     * @return ArrayList of all reservations
     */
    
    @Override
    public ArrayList<Reservations> getAllReservations() {
        readLock.lock();
        try {
            return new ArrayList<>(reservations);
        } finally {
            readLock.unlock();
        }
    }
    
    // EVENT MANAGEMENT --------------------------------------------------
    
    /**
     * Adds a new event to the database
     * thread safe ops using write lock
     * 
     * @param event the Event object to add
     * @return true if event was added successfully,
     *         false otherwise
     */
    
    @Override
    public boolean addEvent(Event event) {
        if (event == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Check for event id
            for (Event e : events) {
                if (e.getId().equals(event.getId())) {
                    return false;
                }
            }
            events.add(event);
            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Removes an event from the database
     * thread safe ops using write lock
     * 
     * @param eventId the ID of the event to remove
     * @return true if event was removed successfully,
     *         false if not found
     */
    
    @Override
    public boolean removeEvent(String eventId) {
        if (eventId == null) {
            return false;
        }
        
        writeLock.lock();

        try {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId().equals(eventId)) {
                    events.remove(i);
                    return true;
                }
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Finds an event by ID
     * thread safe ops using read lock
     * 
     * @param eventId the event ID to search for
     * @return the Event object if found,
     *         null otherwise
     */
    
    @Override
    public Event findEvent(String eventId) {
        if (eventId == null) {
            return null;
        }
        
        readLock.lock();
        try {
            for (Event e : events) {
                if (e.getId().equals(eventId)) {
                    return e;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all events in the database
     * thread safe ops using read lock
     * Returns a copy to prevent external modification
     * 
     * @return ArrayList of all events
     */
    
    @Override
    public ArrayList<Event> getAllEvents() {
        readLock.lock();
        try {
            return new ArrayList<>(events);
        } finally {
            readLock.unlock();
        }
    }
    
    // PENDING EVENTS MANAGEMENT (Admin Approval Required) --------------------------------------------------
    
    /**
     * Adds an event to the pending list (awaiting admin approval)
     * Thread-safe operation using write lock
     * 
     * @param event the Event object to add to pending
     * @return true if event was added successfully
     */
    public boolean addPendingEvent(Event event) {
        if (event == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            event.setPending(true); // Mark as pending
            reservations.add(event);  // Add to reservations list
            // Note: events list is maintained separately and will include this automatically
            // since Event extends Reservations and events list is rebuilt from reservations
            if (!events.contains(event)) {
                events.add(event);  // Add to events list for immediate conflict checking
            }
            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Gets all pending events awaiting admin approval
     * 
     * @return ArrayList of all pending events
     */
    public ArrayList<Event> getAllPendingEvents() {
        readLock.lock();
        try {
            ArrayList<Event> pendingList = new ArrayList<>();
            for (Event e : events) {
                if (e.isPending()) {
                    pendingList.add(e);
                }
            }
            return pendingList;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets pending events for a specific user
     * 
     * @param username the username to filter by
     * @return ArrayList of pending events for this user
     */
    public ArrayList<Event> getPendingEventsByUser(String username) {
        if (username == null || username.isEmpty()) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            ArrayList<Event> userPendingEvents = new ArrayList<>();
            for (Event e : events) {
                if (e.isPending() && username.equals(e.getUsername())) {
                    userPendingEvents.add(e);
                }
            }
            return userPendingEvents;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Reloads all data from file including pending events
     * Useful when admin GUI needs to see latest pending events
     * 
     * @throws IOException if there's an error reading from file
     */
    public void reloadPendingEvents() throws IOException {
        // Just reload from file - pending status is stored in the file now
        loadFromFile();
    }
    
    /**
     * Removes a pending event from the list
     * 
     * @param eventId the ID of the pending event to remove
     * @return true if event was removed successfully
     */
    public boolean removePendingEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return false;
        }
        
        // removeReservation already handles removing from both lists
        return removeReservation(eventId);
    }
    
    /**
     * Finds a pending event by its ID
     * 
     * @param eventId the ID of the pending event
     * @return the Event if found, null otherwise
     */
    public Event findPendingEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return null;
        }
        
        readLock.lock();
        try {
            for (Event e : events) {
                if (e.isPending() && e.getId().equals(eventId)) {
                    return e;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Approves a pending event and moves it to active events
     * Also removes all conflicting reservations and events
     * 
     * @param eventId the ID of the pending event to approve
     * @return ArrayList of removed items (for confirmation display), null if event not found
     */
    public ArrayList<Reservations> approvePendingEvent(String eventId) {
        Event pendingEvent = findPendingEvent(eventId);
        if (pendingEvent == null) {
            return null;
        }
        
        writeLock.lock();
        try {
            // Find all conflicts
            ArrayList<Reservations> conflicts = findConflicts(pendingEvent);
            
            // Remove all conflicting reservations and events
            for (Reservations conflict : conflicts) {
                String conflictId = conflict.getReservationId();
                if (conflict.isEvent()) {
                    // Remove from both events and reservations lists
                    removeEvent(conflictId);
                    removeReservation(conflictId);
                } else {
                    removeReservation(conflictId);
                }
            }
            
            // Change event from pending to approved
            pendingEvent.setPending(false);
            
            return conflicts;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Finds all reservations and events that conflict with a given event
     * Conflicts occur when date/time ranges overlap
     * 
     * @param event the event to check for conflicts
     * @return ArrayList of conflicting Reservations/Events
     */
    public ArrayList<Reservations> findConflicts(Event event) {
        ArrayList<Reservations> conflicts = new ArrayList<>();
        
        readLock.lock();
        try {
            // Parse event times
            int eventStartMinutes = parseTimeToMinutes(event.getDate(), event.getTime());
            int eventEndMinutes = parseTimeToMinutes(event.getEndDate(), event.getEndTime());
            
            // Check all reservations
            for (Reservations r : reservations) {
                // Skip if it's the same event (don't conflict with yourself)
                if (r.getReservationId().equals(event.getReservationId())) {
                    continue;
                }
                
                int resStartMinutes = parseTimeToMinutes(r.getDate(), r.getTime());
                // Assume reservations last 2 hours (120 minutes)
                int resEndMinutes = resStartMinutes + 120;
                
                if (timesOverlap(eventStartMinutes, eventEndMinutes, resStartMinutes, resEndMinutes)) {
                    conflicts.add(r);
                }
            }
            
            // Check all active events
            for (Event e : events) {
                // Skip if it's the same event (don't conflict with yourself)
                if (e.getId().equals(event.getId())) {
                    continue;
                }
                
                int eStartMinutes = parseTimeToMinutes(e.getDate(), e.getTime());
                int eEndMinutes = parseTimeToMinutes(e.getEndDate(), e.getEndTime());
                
                if (timesOverlap(eventStartMinutes, eventEndMinutes, eStartMinutes, eEndMinutes)) {
                    conflicts.add(e);
                }
            }
            
            return conflicts;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Checks if a reservation/event conflicts with any existing events or reservations
     * 
     * @param date the date of the reservation
     * @param time the time of the reservation
     * @return true if there's a conflict with an active event or reservation
     */
    public boolean hasEventConflict(String date, String time) {
        readLock.lock();
        try {
            int resStartMinutes = parseTimeToMinutes(date, time);
            int resEndMinutes = resStartMinutes + 120; // Assume 2-hour reservation
            
            // Check against all active events
            for (Event e : events) {
                int eventStartMinutes = parseTimeToMinutes(e.getDate(), e.getTime());
                int eventEndMinutes = parseTimeToMinutes(e.getEndDate(), e.getEndTime());
                
                if (timesOverlap(resStartMinutes, resEndMinutes, eventStartMinutes, eventEndMinutes)) {
                    return true;
                }
            }
            
            return false;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Checks if a new reservation conflicts with existing reservations or events
     * 
     * @param date the date of the new reservation
     * @param time the time of the new reservation
     * @param teeBox the tee box for the reservation
     * @param excludeId optional reservation ID to exclude (for editing)
     * @return true if there's a conflict
     */
    public boolean hasReservationConflict(String date, String time, String teeBox, String excludeId) {
        readLock.lock();
        try {
            int newStartMinutes = parseTimeToMinutes(date, time);
            int newEndMinutes = newStartMinutes + 120; // Assume 2-hour reservation
            
            // Check against all events - events block the ENTIRE course during their time
            for (Reservations r : reservations) {
                // Skip if it's the same reservation (for editing)
                if (excludeId != null && r.getReservationId().equals(excludeId)) {
                    continue;
                }
                
                // If it's an event, check if times overlap (events block entire course)
                if (r.isEvent()) {
                    Event e = (Event) r;
                    // Skip pending events - they don't block until approved
                    if (e.isPending()) continue;
                    
                    int eventStartMinutes = parseTimeToMinutes(e.getDate(), e.getTime());
                    int eventEndMinutes = parseTimeToMinutes(e.getEndDate(), e.getEndTime());
                    
                    if (timesOverlap(newStartMinutes, newEndMinutes, eventStartMinutes, eventEndMinutes)) {
                        return true; // Event blocks this time slot on ALL tee boxes
                    }
                } else {
                    // Regular reservation - only check same tee box
                    if (!r.getTeeBox().equals(teeBox)) {
                        continue;
                    }
                    
                    // Skip pending reservations
                    if (r.isPending()) continue;
                    
                    int resStartMinutes = parseTimeToMinutes(r.getDate(), r.getTime());
                    int resEndMinutes = resStartMinutes + 120;
                    
                    if (timesOverlap(newStartMinutes, newEndMinutes, resStartMinutes, resEndMinutes)) {
                        return true;
                    }
                }
            }
            
            return false;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Parses date and time to minutes since epoch for comparison
     * Format: date "YYYY-MM-DD", time "HH:MM" or "H:MM AM/PM"
     * 
     * @param date the date string
     * @param time the time string
     * @return total minutes since a reference point
     */
    private int parseTimeToMinutes(String date, String time) {
        try {
            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            
            // Handle both "9:00 AM" and "09:00" formats
            int hour;
            int minute;
            
            if (time.contains("AM") || time.contains("PM")) {
                // Format: "9:00 AM" or "12:30 PM"
                boolean isPM = time.contains("PM");
                String timeOnly = time.replace("AM", "").replace("PM", "").trim();
                String[] timeParts = timeOnly.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
                
                // Convert to 24-hour format
                if (isPM && hour != 12) {
                    hour += 12;
                } else if (!isPM && hour == 12) {
                    hour = 0;
                }
            } else {
                // Format: "09:00" or "14:30"
                String[] timeParts = time.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }
            
            // Convert to total minutes (simple calculation)
            return (year * 525600) + (month * 43800) + (day * 1440) + (hour * 60) + minute;
        } catch (Exception e) {
            System.err.println("Error parsing time: date=" + date + ", time=" + time + ", error=" + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Checks if two time ranges overlap
     * 
     * @param start1 start time of first range
     * @param end1 end time of first range
     * @param start2 start time of second range
     * @param end2 end time of second range
     * @return true if ranges overlap
     */
    private boolean timesOverlap(int start1, int end1, int start2, int end2) {
        return (start1 < end2 && end1 > start2);
    }
    
    // TEE-TIME MANAGMENT --------------------------------------------------
    
    /**
     * Adds a new tee time to the database
     * thread safe ops using write lock
     * 
     * @param teeTime the TeeTime object to add
     * @return true if tee time was added successfully,
     *         false otherwise
     */
    
    public boolean addTeeTime(TeeTime teeTime) {
        if (teeTime == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            // Checks if teetime id exists
            for (TeeTime tt : teeTimes) {
                if (tt.getTeeTimeId().equals(teeTime.getTeeTimeId())) {
                    return false;
                }
            }
            teeTimes.add(teeTime);
            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Removes a tee time from the database
     * thread safe ops using write lock
     * 
     * @param teeTimeId the ID of the tee time to remove
     * @return true if tee time was removed successfully,
     *         false if not found
     */
    
    public boolean removeTeeTime(String teeTimeId) {
        if (teeTimeId == null) {
            return false;
        }
        
        writeLock.lock();
        try {
            for (int i = 0; i < teeTimes.size(); i++) {
                if (teeTimes.get(i).getTeeTimeId().equals(teeTimeId)) {
                    teeTimes.remove(i);
                    return true;
                }
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Finds a tee time by ID
     * thread safe ops using read lock
     * 
     * @param teeTimeId the tee time ID to search for
     * @return the TeeTime object if found,
     *         null otherwise
     */
    
    public TeeTime findTeeTime(String teeTimeId) {
        if (teeTimeId == null) {
            return null;
        }
        
        readLock.lock();
        try {
            for (TeeTime tt : teeTimes) {
                if (tt.getTeeTimeId().equals(teeTimeId)) {
                    return tt;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all tee times for a specific date
     * thread safe ops using read lock
     * 
     * @param date the date to search for (YYYY-MM-DD format)
     * @return ArrayList of tee times for that date
     */
    
    public ArrayList<TeeTime> getTeeTimesByDate(String date) {
        if (date == null) {
            return new ArrayList<>();
        }
        
        readLock.lock();
        try {
            ArrayList<TeeTime> result = new ArrayList<>();
            for (TeeTime tt : teeTimes) {
                if (tt.getDate().equals(date)) {
                    result.add(tt);
                }
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Gets all tee times in the database
     * thread safe ops using read lock
     * Returns a copy to prevent external modification
     * 
     * @return ArrayList of all tee times
     */
    
    public ArrayList<TeeTime> getAllTeeTimes() {
        readLock.lock();
        try {
            return new ArrayList<>(teeTimes);
        } finally {
            readLock.unlock();
        }
    }
    
    // COURSE SETTINGS MANAGMENT --------------------------------------------------
    
    /**
     * Gets the course settings
     * thread safe ops using read lock
     * 
     * @return CourseSettings object
     */
    
    public CourseSettings getCourseSettings() {
        readLock.lock();
        try {
            return courseSettings;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Updates the course settings
     * thread safe ops using write lock
     * 
     * @param settings new CourseSettings object
     */
    
    public void setCourseSettings(CourseSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Course settings cannot be null");
        }
        
        writeLock.lock();
        try {
            this.courseSettings = settings;
        } finally {
            writeLock.unlock();
        }
    }
    
    // DATA PERSISTENCE --------------------------------------------------
    
    /**
     * Saves all database data to disk
     * Creates/updates files for users, reservations, events etc.
     * thread safe ops using read lock
     * 
     * @throws IOException if there's an error writing to files
     */
    
    @Override
    public void saveToFile() throws IOException {
        readLock.lock();
        try {
            // Save users
            try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
                for (User u : users) {
                    pw.println(u.toFileString());
                }
            }
            
            // Save reservations
            try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
                for (Reservations r : reservations) {
                    pw.println(r.toFileString());
                }
            }
            
            // Save tee times
            try (PrintWriter pw = new PrintWriter(new FileWriter(TEETIMES_FILE))) {
                for (TeeTime tt : teeTimes) {
                    pw.println(tt.toFileString());
                }
            }
            
            // Save course settings
            try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
                pw.println(courseSettings.toFileString());
            }
            
            // Note: Events are saved as part of reservations (Event extends Reservations)
            // The events list is maintained in-memory for conflict checking only
            // We don't save to events.txt anymore to avoid duplicates
            // Pending status is stored in the isPending flag in reservations.txt
            
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Loads all database data from disk
     * Reads data from files for users, reservations, events etc.
     * thread safe ops using write lock
     * 
     * @throws IOException if there's an error reading from files
     */
    
    @Override
    public void loadFromFile() throws IOException {
        writeLock.lock();
        try {
            // Clear existing data to avoid duplicates
            users.clear();
            reservations.clear();
            events.clear();
            teeTimes.clear();
            
            // loads users
            File usersFile = new File(USERS_FILE);
            if (usersFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        User u = User.fromFileString(line);
                        if (u != null) {
                            users.add(u);
                        }
                    }
                }
            }
            
            // Loads reservations
            File reservationsFile = new File(RESERVATIONS_FILE);
            if (reservationsFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(reservationsFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Reservations r = Reservations.fromFileString(line);
                        if (r != null) {
                            reservations.add(r);
                        }
                    }
                }
            }
            
            // Loads tee times
            File teeTimesFile = new File(TEETIMES_FILE);
            if (teeTimesFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(teeTimesFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        TeeTime tt = TeeTime.fromFileString(line);
                        if (tt != null) {
                            teeTimes.add(tt);
                        }
                    }
                }
            }
            
            // Loads course settings
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(settingsFile))) {
                    String line = br.readLine();
                    if (line != null) {
                        CourseSettings loaded = CourseSettings.fromFileString(line);
                        if (loaded != null) {
                            courseSettings = loaded;
                        }
                    }
                }
            }
            
            // Rebuild events list from reservations (events are stored as part of reservations)
            events.clear();
            for (Reservations r : reservations) {
                if (r instanceof Event) {
                    events.add((Event) r);
                }
            }
            // Pending events are now part of reservations list with isPending flag
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Clears all data from the database
     * Used primarily for testing purposes
     * thread safe ops using write lock
     */
    
    @Override
    public void clearAllData() {
        writeLock.lock();
        try {
            users.clear();
            reservations.clear();
            events.clear();
            teeTimes.clear();
        } finally {
            writeLock.unlock();
        }
    }
}
