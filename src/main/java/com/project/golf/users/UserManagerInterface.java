package com.project.golf.users;

/**
 * UserManagerInterface.java
 * 
 * Interface defining user account management operations with persistence.
 * Specifies contracts for user CRUD operations and file-based synchronization.
 * 
 * Data structures: User collection management with file persistence.
 * Algorithm: File I/O synchronization strategy for user data persistence.
 * Features: User account creation/modification/deletion, file-based persistence,
 * credential validation, bulk user operations.
 * 
 * @author Connor Landzettel (clandzet), L15
 *
 * @version November 20, 2025
 */

public interface UserManagerInterface {
    //Add new user
    public boolean addUser(String username, String password, String firstName, 
                           String lastName, String email, boolean hasPaid);
    
    // Find a user by username
    public User findUser(String username);

    // Check login
    public boolean login(String username, String password);
}
