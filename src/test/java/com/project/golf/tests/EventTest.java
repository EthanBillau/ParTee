package com.project.golf.tests;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.events.Event;
import org.junit.jupiter.api.*;

/**
 * EventTest.java
 *
 * <p>Unit test suite for Event multi-day reservation creation and operations. Tests event duration
 * tracking and inherited reservation functionality.
 *
 * <p>Data structures: Event objects extending Reservations, start/end date/time fields. Algorithm:
 * JUnit 5 with duration validation and inheritance testing. Features: Event instantiation with
 * duration, end date/time property access, inherited reservation methods, date range validation.
 *
 * @author Nikhil Kodali (kodali3), L15
 * @version December 5, 2025
 */
public class EventTest {

  private Event event;

  @BeforeEach
  void setUp() {
    event =
        new Event(
            "E001",
            "GolfTournament",
            "2025-12-15",
            "09:00",
            200,
            "All",
            75.0,
            "2025-12-15",
            "12:00");
  }

  @AfterEach
  void tearDown() {
    event = null;
  }

  // CONSTRUCTOR --------------------------------------------------
  @Test
  void testConstructorAndAccessors() {
    assertTimeout(
        ofMillis(1000),
        () -> {
          assertNotNull(event, "Event should not be null");
          assertEquals("GolfTournament", event.getName(), "Event name should match");
          assertEquals("E001", event.getId(), "Event ID should match");
          assertEquals("2025-12-15", event.getDate(), "Event date should match");
          assertEquals("09:00", event.getTime(), "Event time should match");
          assertEquals("2025-12-15", event.getEndDate(), "Event end date should match");
          assertEquals("12:00", event.getEndTime(), "Event end time should match");
        });
  }

  // EVENT SPECIFIC METHODS --------------------------------------------------
  @Test
  void testEventSpecificMethods() {
    assertTimeout(
        ofMillis(1000),
        () -> {
          assertTrue(event.isEvent(), "isEvent() should return true");
          assertEquals("GolfTournament", event.getName(), "getName() should return username");
          assertEquals("E001", event.getId(), "getId() should return reservation ID");
        });
  }

  // INHERITED RESERVATIONS METHODS --------------------------------------------------
  @Test
  void testInheritedReservationsMethods() {
    assertTimeout(
        ofMillis(1000),
        () -> {
          assertEquals(200, event.getPartySize(), "Party size should be 200");
          assertEquals("All", event.getTeeBox(), "Tee box should be 'All'");
          assertEquals(75.0, event.getPrice(), "Price should be 75.0");
          assertEquals("GolfTournament", event.getUsername(), "Username should match");
        });
  }

  // FILE STRING FORMAT --------------------------------------------------
  @Test
  void testToFileString() {
    assertTimeout(
        ofMillis(1000),
        () -> {
          String fileString = event.toFileString();
          assertNotNull(fileString, "File string should not be null");
          assertTrue(fileString.startsWith("EVENT,"), "File string should start with EVENT,");
          assertTrue(
              fileString.contains("GolfTournament"), "File string should contain event name");
          assertTrue(fileString.contains("2025-12-15"), "File string should contain date");
        });
  }
}
