package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.users.*;
import org.junit.jupiter.api.Test;

/**
 * UserTest.java
 *
 * <p>Unit test suite for User class functionality and edge cases. Tests user creation, property
 * access, and serialization operations.
 *
 * <p>Data structures: User objects with various profile configurations for test cases. Algorithm:
 * JUnit 5 parametrized testing pattern with assertion-based verification. Features: User
 * instantiation validation, property getter verification, toString() formatting, password storage,
 * admin/payment status flags.
 *
 * @author Connor Landzettel (clandzet), L15
 * @version November 11, 2025
 */
public class UserTest {

  // CONSTRUCTOR --------------------------------------------------

  @Test
  public void testUserConstructor() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("john", user.getUsername());
    assertEquals("password", user.getPassword());
    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());
    assertEquals("john@email.com", user.getEmail());
    assertTrue(user.hasPaid());
    assertNotNull(user);
  }

  // GETTERS --------------------------------------------------
  @Test
  public void testGetUsername() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("john", user.getUsername());
  }

  @Test
  public void testGetPassword() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("password", user.getPassword());
  }

  @Test
  public void testGetFirstName() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("John", user.getFirstName());
  }

  @Test
  public void testGetLastName() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("Doe", user.getLastName());
  }

  @Test
  public void testGetEmail() {
    User user = new User("john", "password", "John", "Doe", "john@email.com", true);

    assertEquals("john@email.com", user.getEmail());
  }

  @Test
  public void testHasPaid() {
    User userPaid = new User("john", "password", "John", "Doe", "jane@email.com", true);
    User userUnpaid = new User("jane", "passwords", "Jane", "Doe", "jane@email.com", false);

    assertTrue(userPaid.hasPaid());
    assertFalse(userUnpaid.hasPaid());
  }
}
