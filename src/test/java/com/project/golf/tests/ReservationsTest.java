package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.reservation.*;
import org.junit.jupiter.api.*;

/**
 * ReservationsTest.java
 *
 * <p>Unit test suite for Reservations data model and state management. Tests reservation creation,
 * property access, and state transitions.
 *
 * <p>Data structures: Reservations objects with various configurations, payment/pending flags.
 * Algorithm: JUnit 5 with assertion-based verification of state changes. Features: Reservation
 * instantiation, property getters/setters, payment status tracking, approval state management,
 * object equality and hashing.
 *
 * @author Anoushka Chakravarty (chakr181), L15
 * @version November 6, 2025
 */
public class ReservationsTest {

  private Reservations testReservation;

  @BeforeEach
  void setUp() {
    // Create a test reservation before each test
    testReservation =
        new Reservations("R001", "maya_user", "2024-11-15", "10:00 AM", 4, "Hole 1", 120.00);
  }

  @AfterEach
  void tearDown() {
    testReservation = null;
  }

  // CONSTRUCTOR --------------------------------------------------

  @Test
  void testConstructor() {
    Reservations res =
        new Reservations("R002", "john_doe", "2024-11-16", "2:00 PM", 2, "Hole 10", 80.00);
    assertNotNull(res, "Reservation object should not be null");
  }

  @Test
  void testConstructorMinPartySize() {
    Reservations res =
        new Reservations("R003", "solo_golfer", "2024-11-20", "7:00 AM", 1, "Hole 1", 40.00);
    assertEquals(1, res.getPartySize(), "Party size should be 1");
  }

  @Test
  void testConstructorMaxPartySize() {
    Reservations res =
        new Reservations("R004", "big_group", "2024-11-20", "9:00 AM", 4, "Hole 1", 160.00);
    assertEquals(4, res.getPartySize(), "Party size should be 4");
  }

  @Test
  void testConstructorZeroPrice() {
    Reservations res =
        new Reservations("R005", "free_play", "2024-11-20", "6:00 AM", 2, "Hole 1", 0.00);
    assertEquals(0.00, res.getPrice(), 0.01, "Price should be 0.00");
  }

  // GETTER TESTS --------------------------------------------------

  @Test
  void testGetReservationId() {
    assertEquals("R001", testReservation.getReservationId(), "Reservation ID should match");
  }

  @Test
  void testGetUsername() {
    assertEquals("maya_user", testReservation.getUsername(), "Username should match");
  }

  @Test
  void testGetDate() {
    assertEquals("2024-11-15", testReservation.getDate(), "Date should match");
  }

  @Test
  void testGetTime() {
    assertEquals("10:00 AM", testReservation.getTime(), "Time should match");
  }

  @Test
  void testGetPartySize() {
    assertEquals(4, testReservation.getPartySize(), "Party size should match");
  }

  @Test
  void testGetTeeBox() {
    assertEquals("Hole 1", testReservation.getTeeBox(), "Tee box should match");
  }

  @Test
  void testGetPrice() {
    assertEquals(120.00, testReservation.getPrice(), 0.01, "Price should match");
  }

  @Test
  void testGetIsPaidDefault() {
    assertFalse(testReservation.getIsPaid(), "New reservation should not be paid by default");
  }

  // TOSTRING METHODS --------------------------------------------------

  @Test
  void testToStringContainsId() {
    String result = testReservation.toString();
    assertTrue(result.contains("R001"), "toString should contain reservation ID");
  }

  @Test
  void testToStringContainsUsername() {
    String result = testReservation.toString();
    assertTrue(result.contains("maya_user"), "toString should contain username");
  }

  @Test
  void testToStringContainsDate() {
    String result = testReservation.toString();
    assertTrue(result.contains("2024-11-15"), "toString should contain date");
  }

  @Test
  void testToStringContainsTime() {
    String result = testReservation.toString();
    assertTrue(result.contains("10:00 AM"), "toString should contain time");
  }

  @Test
  void testToStringContainsPartySize() {
    String result = testReservation.toString();
    assertTrue(result.contains("4"), "toString should contain party size");
  }

  @Test
  void testToStringContainsTeeBox() {
    String result = testReservation.toString();
    assertTrue(result.contains("Hole 1"), "toString should contain tee box");
  }

  @Test
  void testToStringContainsPrice() {
    String result = testReservation.toString();
    assertTrue(result.contains("120.00"), "toString should contain price");
  }

  @Test
  void testToStringDifferentReservation() {
    Reservations res2 =
        new Reservations("R999", "jane_smith", "2024-12-01", "8:00 AM", 1, "Hole 5", 60.00);
    String result = res2.toString();
    assertTrue(
        result.contains("R999") && result.contains("jane_smith"),
        "toString should contain new reservation data");
  }
}
