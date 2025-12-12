package com.project.golf.database;

import com.project.golf.users.*;
import com.project.golf.reservation.*;
import com.project.golf.events.*;
import com.project.golf.settings.*;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Database.java
 * 
 * Database for managing all users, reservations, and events in the golf course reservation system. 
 * Implements data persistence by reading from and writing to disk.
 *
 * @author Aman Wakankar (awakanka), Anoushka Chakravarty (chakr181), Connor Landzettel (clandzet), Nikhil Kodali (kodali3), Ethan Billau (ethanbillau), L15
 * @version November 9, 2025
 */

public class Database implements DatabaseInterface {

    //Database file (singleton)
    private static Database instance = null;
    
    // Data storage
    private ArrayList<User> users;
    private ArrayList<Reservations> reservations;
    private ArrayList<Event> events;
    private ArrayList<TeeTime> teeTimes;
    private CourseSettings courseSettings;
    
    // File paths for persistence
    private static final String USERS_FILE = "users.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";
    private static final String EVENTS_FILE = "events.txt";
    private static final String TEETIMES_FILE = "teetimes.txt";
    private static final String SETTINGS_FILE = "settings.txt";
    
    // Thread safety using ReentrantReadWriteLock for better concurrency
    private final ReentrantReadWriteLock lock;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    
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
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials are valid,
     *         false otherwise
     */
    
    @Override
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        readLock.lock();
        try {
            User user = findUser(username);
            return user != null && user.getPassword().equals(password);
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
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).getReservationId().equals(reservationId)) {
                    reservations.remove(i);
                    return true;
                }
            }
            return false;
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
            
            // Save events - Note: Event persistence is handled by Event.saveToFile()
            // This is a simplified version for Phase 1
            try (PrintWriter pw = new PrintWriter(new FileWriter(EVENTS_FILE))) {
                for (Event e : events) {
                    pw.println(e.getId() + "," + e.getName());
                }
            }
            
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
            
            // load events (not done yet, left for latr)
            File eventsFile = new File(EVENTS_FILE);
            if (eventsFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(eventsFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // have to add this shit right fucking away
                    }
                }
            }
            
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
