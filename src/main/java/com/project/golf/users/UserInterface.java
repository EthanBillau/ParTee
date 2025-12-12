package com.project.golf.users;

/**
 * UserInterface.java
 * 
 * Interface defining the properties and actions for a user in the system.
 * Provides methods for getting/setting user information and file serialization.
 * 
 * @author Connor Landzettel (clandzet), L15
 * @version November 20, 2025
 */

public interface UserInterface {

    // Getters
    String getUsername();
    String getPassword();
    String getFirstName();
    String getLastName();
    String getEmail();
    boolean hasPaid();
    boolean isAdmin();

    // Setters
    void setUsername(String username);
    void setPassword(String password);
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setEmail(String email);
    void setHasPaid(boolean hasPaid);
    void setIsAdmin(boolean isAdmin);

    // File serialization
    String toFileString();
}
