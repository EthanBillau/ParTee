package com.project.golf.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil.java
 * 
 * Utility class for secure password hashing and validation using BCrypt.
 * Provides methods to hash passwords and verify them against stored hashes.
 *
 * @author GitHub Copilot Agent
 * @version December 12, 2025
 */
public class PasswordUtil {
    
    // BCrypt work factor (log rounds) - 12 is a good balance of security and performance
    private static final int WORK_FACTOR = 12;
    
    /**
     * Hash a plaintext password using BCrypt
     * 
     * @param plainPassword the plaintext password to hash
     * @return the hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    
    /**
     * Verify a plaintext password against a hashed password
     * 
     * @param plainPassword the plaintext password to verify
     * @param hashedPassword the hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format - may be plaintext password from old data
            return false;
        }
    }
    
    /**
     * Check if a string is a BCrypt hash
     * BCrypt hashes start with "$2a$", "$2b$", or "$2y$"
     * 
     * @param password the string to check
     * @return true if it appears to be a BCrypt hash, false otherwise
     */
    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
