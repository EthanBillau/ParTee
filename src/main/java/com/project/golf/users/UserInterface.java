package com.project.golf.users;

/**
 * UserInterface.java
 * 
 * Interface defining user account properties and operations.
 * Specifies contracts for user information access and file serialization.
 * 
 * Data structures: User profile fields (username, password, name, email, payment/admin status).
 * Algorithm: Simple property accessor pattern with serialization support.
 * Features: User data access, profile modification, payment status tracking, admin role designation,
 * file-based persistence serialization.
 * 
 * @author Connor Landzettel (clandzet), L15
 *
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
