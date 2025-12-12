package com.project.golf.users;

/**
 * UserManagerInterface.java
 * 
 * Interface for UserManager class
 * 
 * @author Connor Landzettel (clandzet), L15
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
