package com.project.golf.users;
    
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * UserManager.java
 *
 * Manages user account data with thread-safe file persistence.
 * Provides operations for user creation, authentication, lookup, and deletion.
 * Automatically loads existing users from disk on initialization.
 *
 * Data structures: ArrayList of User objects, ReentrantReadWriteLock for synchronization.
 * Algorithm: Linear search for user lookup, file I/O for persistence.
 * Features: User CRUD operations, login validation, thread-safe read/write access.
 *
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), L15
 *
 * @version November 10, 2025
 */

public class UserManager implements UserManagerInterface {
    // In-memory user list
    private ArrayList<User> users;
    // Thread safety locks for read/write operations
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    // File path for persisting user data
    private static final String USERS_FILE = "users.txt";

    /**
     * Constructor for UserManager
     * Initializes the users list
     */
    
    public UserManager() {
        users = new ArrayList<>();
        try {
            loadUsersFromFile(); // Load existing users on startup
        } catch (IOException e) {
            System.out.println("No existing user data found. Starting fresh.");
        }
    }

    /**
     * Adds a new user to the manager if username doesn't exist
     * Thread safe with write lock. Persists change to file.
     * 
     * @param username the new username
     * @param password the password
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email
     * @param hasPaid whether user has paid
     * @return true if user added successfully, false if username exists or error
     */
    @Override
    public boolean addUser(String username, String password, String firstName, 
                           String lastName, String email, boolean hasPaid) {
        writeLock.lock();
        try {
            if (findUser(username) != null) {
                return false;
            }
            User newUser = new User(username, password, firstName, lastName, email, hasPaid);
            users.add(newUser);
            saveUsersToFile(); // Persist change
            return true;
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Searches for a user by username
     * Thread safe with read lock
     * 
     * @param username the username to search for
     * @return the User object if found, null otherwise
     */
    @Override
    public User findUser(String username) {
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
     * Validates user login credentials
     * Thread safe with read lock
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    @Override
    public boolean login(String username, String password) {
        readLock.lock();
        try {
            User u = findUser(username);
            return u != null && u.getPassword().equals(password);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Removes a user by username
     * Thread safe with write lock. Persists change to file.
     * 
     * @param username the username of the user to remove
     * @return true if user was removed, false if not found or error
     */
    public boolean removeUser(String username) {
        writeLock.lock();
        try {
            boolean removed = users.removeIf(u -> u.getUsername().equals(username));
            if (removed) saveUsersToFile(); // Persist change
            return removed;
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Gets a copy of all users
     * Thread safe with read lock. Returns a copy to prevent external modification.
     * 
     * @return ArrayList containing all users
     */
    public ArrayList<User> getAllUsers() {
        readLock.lock();
        try {
            return new ArrayList<>(users);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Persists all users to disk file
     * Thread safe with read lock. Writes all user data as comma-delimited lines.
     * 
     * @throws IOException if file write operation fails
     */
    public void saveUsersToFile() throws IOException {
        readLock.lock();
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                pw.println(u.toFileString());
            }
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Loads users from disk file
     * Thread safe with write lock. Parses each line into User objects.
     * If file doesn't exist, starts with empty user list.
     * 
     * @throws IOException if file read operation fails
     */
    public void loadUsersFromFile() throws IOException {
        writeLock.lock();
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) return;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    User u = User.fromFileString(line);
                    if (u != null) users.add(u);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }
}
