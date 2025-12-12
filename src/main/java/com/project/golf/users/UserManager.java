package com.project.golf.users;
    
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * UserManager.java
 *
 * Implements UserManagerInterface
 * Adds thread safety and file persistence for Phase 1
 * 
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), L15
 * @version November 10, 2025
 */

public class UserManager implements UserManagerInterface {
    private ArrayList<User> users;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private static final String USERS_FILE = "users.txt"; // File path for persistence

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

    //Adds a new user if username is not already taken

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

    //Finds a user by username

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

    //Validates login credentials

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

    // Removes a user by username

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

    //Returns a copy of all users

    public ArrayList<User> getAllUsers() {
        readLock.lock();
        try {
            return new ArrayList<>(users);
        } finally {
            readLock.unlock();
        }
    }

    //Saves all users to disk

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

    // Loads users from save
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
