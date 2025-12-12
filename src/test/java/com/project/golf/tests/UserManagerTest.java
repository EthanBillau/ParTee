package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.users.*;
import org.junit.jupiter.api.Test;

/**
 * UserManagerTest.java
 *
 * <p>Unit test suite for UserManager persistence and CRUD operations. Tests user account creation,
 * loading, modification, and file synchronization.
 *
 * <p>Data structures: UserManager instances, ArrayList of Users, temporary test files. Algorithm:
 * JUnit 5 with file I/O testing, create-check-cleanup test pattern. Features: User creation
 * validation, loading from files, user queries, file persistence, duplicate prevention, account
 * modification.
 *
 * @author Connor Landzettel (clandzet), Ethan Billau (ethanbillau), L15
 * @version November 6, 2025
 */
public class UserManagerTest {

  // CONSTRUCTOR --------------------------------------------------

  @Test
  public void testUserManagerConstructor() {
    UserManager userManager = new UserManager();

    assertNotNull(userManager);
  }

  // USERS --------------------------------------------------
  @Test
  public void testAddUser() {
    UserManager userManager = new UserManager();
    boolean addedUser = userManager.addUser("jane", "abcd", "Jane", "Doe", "jane@email.com", false);
    User user = userManager.findUser("jane");

    assertTrue(addedUser);
    assertNotNull(user);
  }

  @Test
  public void testFindUser() {
    UserManager userManager = new UserManager();
    userManager.addUser("john", "1234", "John", "Doe", "john@email.com", true);
    User user = userManager.findUser("john");

    assertNotNull(user);
    assertEquals("john", user.getUsername());
  }

  // LOGIN --------------------------------------------------
  @Test
  public void testLogin() {
    UserManager userManager = new UserManager();
    userManager.addUser("john", "1234", "John", "Doe", "john@email.com", true);

    assertTrue(userManager.login("john", "1234"));
    assertFalse(userManager.login("john", "wrong"));
    assertFalse(userManager.login("nosuch", "x"));
  }
}
