package com.project.golf.users;

/**
 * User.java
 *
 * <p>Represents a user in the ParTee golf reservation system. Stores user information including
 * username, password, name, email, payment status, and admin privileges. Implements UserInterface
 * for abstraction.
 *
 * <p>Data structures: String fields for user data, boolean flags for status Major features: User
 * authentication, profile management, file serialization
 *
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), L15
 * @version November 6, 2025
 */
public class User implements UserInterface {

  private String username; // username identifier for login
  private String password; // password for authentication
  private String firstName; // user's first name
  private String lastName; // user's last name
  private String email; // user's email address
  private boolean hasPaid; // whether user has paid membership fee
  private boolean isAdmin = false; // whether user has administrator privileges

  /**
   * Constructor for Users
   *
   * @param username the username of the user
   * @param password the password of the user
   * @param firstName the first name of the user
   * @param lastName the last name of the user
   * @param email the email of the user
   * @param hasPaid whether the user has paid or not
   */
  public User(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      boolean hasPaid) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.hasPaid = hasPaid;
  }

  /**
   * Constructor for Users
   *
   * @param username the username of the user
   * @param password the password of the user
   * @param firstName the first name of the user
   * @param lastName the last name of the user
   * @param email the email of the user
   * @param hasPaid whether the user has paid or not
   * @param isAdmin whether the user is an admin or not
   */
  public User(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      boolean hasPaid,
      boolean isAdmin) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.hasPaid = hasPaid;
    this.isAdmin = isAdmin;
  }

  // Getters
  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public boolean hasPaid() {
    return hasPaid;
  }

  @Override
  public boolean isAdmin() {
    return isAdmin;
  }

  // Setters
  @Override
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public void setHasPaid(boolean hasPaid) {
    this.hasPaid = hasPaid;
  }

  @Override
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  /**
   * Convert user to file string for persistence Format:
   * username,password,firstName,lastName,email,hasPaid,isAdmin
   *
   * @return formatted string representation
   */
  @Override
  public String toFileString() {
    return String.format(
        "%s,%s,%s,%s,%s,%b,%b", username, password, firstName, lastName, email, hasPaid, isAdmin);
  }

  /**
   * Create User from file string
   *
   * @param fileString format: username,password,firstName,lastName,email,hasPaid,isAdmin
   * @return User object or null if invalid
   */
  public static User fromFileString(String fileString) {
    if (fileString == null || fileString.trim().isEmpty()) {
      return null;
    }

    try {
      String[] parts = fileString.split(",");
      if (parts.length < 6) {
        return null;
      }

      String username = parts[0];
      String password = parts[1];
      String firstName = parts[2];
      String lastName = parts[3];
      String email = parts[4];
      boolean hasPaid = Boolean.parseBoolean(parts[5]);
      boolean isAdmin = (parts.length > 6) ? Boolean.parseBoolean(parts[6]) : false;

      return new User(username, password, firstName, lastName, email, hasPaid, isAdmin);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * String representation of user
   *
   * @return formatted user information
   */
  @Override
  public String toString() {
    return String.format(
        "User[%s] %s %s <%s> | Paid: %s | Role: %s",
        username,
        firstName,
        lastName,
        email,
        hasPaid ? "Yes" : "No",
        isAdmin ? "Admin" : "Customer");
  }
}
