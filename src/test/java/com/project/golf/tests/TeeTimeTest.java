package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.reservation.*;
import org.junit.jupiter.api.*;

/**
 * TeeTimeTest.java
 *
 * <p>Unit test suite for TeeTime slot creation and reservation management. Tests capacity
 * validation, party size limits, and booking operations.
 *
 * <p>Data structures: TeeTime objects, Reservations for bookings, ArrayList of reservations.
 * Algorithm: JUnit 5 with boundary testing for capacity limits and party size validation. Features:
 * TeeTime creation validation, price calculation, capacity checking, reservation adding, ID
 * generation, slot availability verification.
 *
 * @author Anoushka Chakravarty (chakr181), L15
 * @version November 9, 2025
 */
public class TeeTimeTest {

  private TeeTime teeTime;

  @BeforeEach
  public void setUp() {
    // Create a tee time for 9:00 AM on Nov 15, 2025
    teeTime = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
  }

  // CONSTRUCTOR --------------------------------------------------

  @Test
  public void testConstructor() {
    assertNotNull(teeTime, "TeeTime should not be null");
    assertNotNull(teeTime.getTeeTimeId(), "TeeTime ID should be generated");
    assertEquals("2025-11-15", teeTime.getDate());
    assertEquals("09:00", teeTime.getTime());
    assertEquals("Hole 1", teeTime.getTeeBox());
    assertEquals(4, teeTime.getMaxPartySize());
    assertEquals(30.0, teeTime.getPricePerPerson());
  }

  // GETTERS --------------------------------------------------

  @Test
  public void testGetTeeTimeId() {
    String id = teeTime.getTeeTimeId();
    assertNotNull(id, "ID should not be null");
    assertTrue(id.startsWith("TT"), "ID should start with TT");
  }

  @Test
  public void testGetDate() {
    assertEquals("2025-11-15", teeTime.getDate());
  }

  @Test
  public void testGetTime() {
    assertEquals("09:00", teeTime.getTime());
  }

  @Test
  public void testGetTeeBox() {
    assertEquals("Hole 1", teeTime.getTeeBox());
  }

  @Test
  public void testGetMaxPartySize() {
    assertEquals(4, teeTime.getMaxPartySize());
  }

  @Test
  public void testGetPricePerPerson() {
    assertEquals(30.0, teeTime.getPricePerPerson());
  }

  // AVAILABILITY --------------------------------------------------

  @Test
  public void testInitialAvailability() {
    assertEquals(0, teeTime.getReservedSpots(), "Should start with 0 reserved spots");
    assertEquals(4, teeTime.getAvailableSpots(), "Should have all 4 spots available");
    assertFalse(teeTime.isFullyBooked(), "Should not be fully booked");
  }

  @Test
  public void testIsAvailable() {
    assertTrue(teeTime.isAvailable(1), "Should be available for 1");
    assertTrue(teeTime.isAvailable(2), "Should be available for 2");
    assertTrue(teeTime.isAvailable(3), "Should be available for 3");
    assertTrue(teeTime.isAvailable(4), "Should be available for 4");
    assertFalse(teeTime.isAvailable(5), "Should not be available for 5");
  }

  @Test
  public void testIsAvailableInvalidPartySize() {
    assertFalse(teeTime.isAvailable(0), "Should not be available for 0");
    assertFalse(teeTime.isAvailable(-1), "Should not be available for negative");
  }

  // BOOKING --------------------------------------------------

  @Test
  public void testBookTeeTime() {
    Reservations res = teeTime.bookTeeTime(2, "john123");

    assertNotNull(res, "Should create reservation");
    assertEquals("john123", res.getUsername());
    assertEquals(2, res.getPartySize());
    assertEquals("2025-11-15", res.getDate());
    assertEquals("09:00", res.getTime());
    assertEquals("Hole 1", res.getTeeBox());
    assertEquals(60.0, res.getPrice()); // 2 * $30

    assertEquals(2, teeTime.getReservedSpots());
    assertEquals(2, teeTime.getAvailableSpots());
  }

  @Test
  public void testBookMultipleReservations() {
    Reservations res1 = teeTime.bookTeeTime(2, "john123");
    Reservations res2 = teeTime.bookTeeTime(1, "jane456");

    assertNotNull(res1);
    assertNotNull(res2);
    assertEquals(3, teeTime.getReservedSpots());
    assertEquals(1, teeTime.getAvailableSpots());
    assertEquals(2, teeTime.getReservations().size());
  }

  @Test
  public void testBookUntilFull() {
    teeTime.bookTeeTime(4, "john123");

    assertTrue(teeTime.isFullyBooked());
    assertEquals(4, teeTime.getReservedSpots());
    assertEquals(0, teeTime.getAvailableSpots());
  }

  @Test
  public void testBookWhenFull() {
    teeTime.bookTeeTime(4, "john123");

    Reservations res = teeTime.bookTeeTime(1, "jane456");
    assertNull(res, "Should not be able to book when full");
  }

  @Test
  public void testBookTooManySpots() {
    Reservations res = teeTime.bookTeeTime(5, "john123");
    assertNull(res, "Should not be able to book more than max capacity");
  }

  @Test
  public void testBookPartiallyFullThenOverflow() {
    teeTime.bookTeeTime(3, "john123");

    Reservations res = teeTime.bookTeeTime(2, "jane456");
    assertNull(res, "Should not be able to book 2 when only 1 spot left");
  }

  @Test
  public void testBookInvalidPartySize() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          teeTime.bookTeeTime(0, "john123");
        },
        "Should throw exception for 0 party size");

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          teeTime.bookTeeTime(-1, "john123");
        },
        "Should throw exception for negative party size");
  }

  @Test
  public void testBookNullUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          teeTime.bookTeeTime(2, null);
        },
        "Should throw exception for null username");
  }

  @Test
  public void testBookEmptyUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          teeTime.bookTeeTime(2, "");
        },
        "Should throw exception for empty username");
  }

  // CANCELLATION --------------------------------------------------

  @Test
  public void testCancelReservation() {
    Reservations res = teeTime.bookTeeTime(2, "john123");
    String resId = res.getReservationId();

    assertEquals(2, teeTime.getReservedSpots());

    boolean cancelled = teeTime.cancelReservation(resId);
    assertTrue(cancelled, "Should successfully cancel reservation");
    assertEquals(0, teeTime.getReservedSpots());
    assertEquals(4, teeTime.getAvailableSpots());
  }

  @Test
  public void testCancelNonexistentReservation() {
    boolean cancelled = teeTime.cancelReservation("INVALID_ID");
    assertFalse(cancelled, "Should return false for nonexistent reservation");
  }

  @Test
  public void testCancelNullReservation() {
    boolean cancelled = teeTime.cancelReservation(null);
    assertFalse(cancelled, "Should return false for null reservation ID");
  }

  @Test
  public void testCancelThenRebook() {
    Reservations res1 = teeTime.bookTeeTime(4, "john123");
    teeTime.cancelReservation(res1.getReservationId());

    Reservations res2 = teeTime.bookTeeTime(3, "jane456");
    assertNotNull(res2, "Should be able to rebook after cancellation");
    assertEquals(3, teeTime.getReservedSpots());
  }

  // RESERVATION RETRIEVAL --------------------------------------------------

  @Test
  public void testGetReservations() {
    teeTime.bookTeeTime(2, "john123");
    teeTime.bookTeeTime(1, "jane456");

    var reservations = teeTime.getReservations();
    assertEquals(2, reservations.size());
  }

  @Test
  public void testGetReservationsEmpty() {
    var reservations = teeTime.getReservations();
    assertEquals(0, reservations.size());
  }

  @Test
  public void testGetReservation() {
    Reservations res = teeTime.bookTeeTime(2, "john123");
    String resId = res.getReservationId();

    Reservations found = teeTime.getReservation(resId);
    assertNotNull(found);
    assertEquals(resId, found.getReservationId());
  }

  @Test
  public void testGetReservationNotFound() {
    Reservations found = teeTime.getReservation("INVALID_ID");
    assertNull(found, "Should return null for nonexistent reservation");
  }

  @Test
  public void testGetReservationNull() {
    Reservations found = teeTime.getReservation(null);
    assertNull(found, "Should return null for null ID");
  }

  // PRICE --------------------------------------------------

  @Test
  public void testSetPricePerPerson() {
    teeTime.setPricePerPerson(40.0);
    assertEquals(40.0, teeTime.getPricePerPerson());

    // New bookings should use new price
    Reservations res = teeTime.bookTeeTime(2, "john123");
    assertEquals(80.0, res.getPrice()); // 2 * $40
  }

  @Test
  public void testSetNegativePrice() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          teeTime.setPricePerPerson(-10.0);
        },
        "Should throw exception for negative price");
  }

  @Test
  public void testPriceCalculation() {
    Reservations res1 = teeTime.bookTeeTime(1, "alice");
    Reservations res2 = teeTime.bookTeeTime(3, "bob");

    assertEquals(30.0, res1.getPrice()); // 1 * $30
    assertEquals(90.0, res2.getPrice()); // 3 * $30
  }

  // FILE PERSISTENCE --------------------------------------------------

  @Test
  public void testToFileString() {
    String fileString = teeTime.toFileString();
    assertNotNull(fileString);
    assertTrue(fileString.contains("2025-11-15"));
    assertTrue(fileString.contains("09:00"));
    assertTrue(fileString.contains("Hole 1"));
  }

  @Test
  public void testFromFileString() {
    String fileString = "TT1,2025-11-20,14:30,Hole 10,4,35.00";
    TeeTime loaded = TeeTime.fromFileString(fileString);

    assertNotNull(loaded);
    assertEquals("TT1", loaded.getTeeTimeId());
    assertEquals("2025-11-20", loaded.getDate());
    assertEquals("14:30", loaded.getTime());
    assertEquals("Hole 10", loaded.getTeeBox());
    assertEquals(4, loaded.getMaxPartySize());
    assertEquals(35.0, loaded.getPricePerPerson());
  }

  @Test
  public void testFromFileStringInvalid() {
    assertNull(TeeTime.fromFileString(null));
    assertNull(TeeTime.fromFileString(""));
    assertNull(TeeTime.fromFileString("invalid,data"));
  }

  @Test
  public void testRoundTripPersistence() {
    String fileString = teeTime.toFileString();
    TeeTime loaded = TeeTime.fromFileString(fileString);

    assertNotNull(loaded);
    assertEquals(teeTime.getDate(), loaded.getDate());
    assertEquals(teeTime.getTime(), loaded.getTime());
    assertEquals(teeTime.getTeeBox(), loaded.getTeeBox());
    assertEquals(teeTime.getMaxPartySize(), loaded.getMaxPartySize());
    assertEquals(teeTime.getPricePerPerson(), loaded.getPricePerPerson());
  }

  // THREAD SAFETY --------------------------------------------------

  @Test
  public void testConcurrentBooking() throws InterruptedException {
    final int numThreads = 4;
    Thread[] threads = new Thread[numThreads];
    final boolean[] results = new boolean[numThreads];

    for (int i = 0; i < numThreads; i++) {
      final int index = i;
      threads[i] =
          new Thread(
              () -> {
                Reservations res = teeTime.bookTeeTime(1, "user" + index);
                results[index] = (res != null);
              });
    }

    // Start all threads
    for (Thread t : threads) {
      t.start();
    }

    // Wait for completion
    for (Thread t : threads) {
      t.join();
    }

    // All 4 should succeed (exactly 4 spots available)
    int successCount = 0;
    for (boolean result : results) {
      if (result) successCount++;
    }

    assertEquals(4, successCount, "All 4 concurrent bookings should succeed");
    assertEquals(4, teeTime.getReservedSpots());
    assertTrue(teeTime.isFullyBooked());
  }

  // EQUALS AND TOSTRING --------------------------------------------------

  @Test
  public void testToString() {
    String str = teeTime.toString();
    assertNotNull(str);
    assertTrue(str.contains("2025-11-15"));
    assertTrue(str.contains("09:00"));
    assertTrue(str.contains("Hole 1"));
  }

  @Test
  public void testEquals() {
    TeeTime same = new TeeTime(teeTime.getTeeTimeId(), "2025-11-15", "09:00", "Hole 1", 4, 30.0);
    TeeTime different = new TeeTime("2025-11-16", "10:00", "Hole 2", 4, 30.0, 50.0);

    assertEquals(teeTime, teeTime, "Should equal itself");
    assertEquals(teeTime, same, "Should equal tee time with same ID");
    assertNotEquals(teeTime, different, "Should not equal tee time with different ID");
    assertNotEquals(teeTime, null, "Should not equal null");
    assertNotEquals(teeTime, "Not a TeeTime", "Should not equal different type");
  }
}
