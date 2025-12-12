package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.settings.*;
import org.junit.jupiter.api.*;

/**
 * CourseSettingsTest.java
 *
 * <p>Unit test suite for CourseSettings configuration management. Tests operational parameters and
 * business rule enforcement.
 *
 * <p>Data structures: CourseSettings instances, day-of-week HashMap overrides. Algorithm: JUnit 5
 * with boundary testing for time/price/party size constraints. Features: Settings instantiation,
 * operational hours validation, pricing configuration, party size limits, advance booking policies,
 * day-specific customization.
 *
 * @author Nikhil Kodali (kodali3), L15
 * @version November 9, 2025
 */
public class CourseSettingsTest {

  private CourseSettings settings;

  @BeforeEach
  public void setUp() {
    settings = new CourseSettings();
  }

  // CONSTRUCTOR TESTS --------------------------------------------------

  @Test
  public void testDefaultConstructor() {
    assertNotNull(settings);
    assertEquals("Golf Course", settings.getCourseName());
    assertEquals("07:00", settings.getOpeningTime());
    assertEquals("19:00", settings.getClosingTime());
    assertEquals(30.0, settings.getDefaultPricePerPerson());
    assertEquals(15, settings.getTeeTimeInterval());
    assertEquals(4, settings.getMaxPartySize());
    assertEquals(18, settings.getNumberOfTeeBoxes());
    assertEquals(7, settings.getAdvanceBookingDays());
  }

  @Test
  public void testParameterizedConstructor() {
    CourseSettings custom =
        new CourseSettings("Pebble Beach", "06:00", "20:00", 50.0, 10, 5, 18, 30);

    assertEquals("Pebble Beach", custom.getCourseName());
    assertEquals("06:00", custom.getOpeningTime());
    assertEquals("20:00", custom.getClosingTime());
    assertEquals(50.0, custom.getDefaultPricePerPerson());
    assertEquals(10, custom.getTeeTimeInterval());
    assertEquals(5, custom.getMaxPartySize());
    assertEquals(18, custom.getNumberOfTeeBoxes());
    assertEquals(30, custom.getAdvanceBookingDays());
  }

  // COURSE NAME TESTS --------------------------------------------------

  @Test
  public void testSetCourseName() {
    settings.setCourseName("Augusta National");
    assertEquals("Augusta National", settings.getCourseName());
  }

  @Test
  public void testSetCourseNameNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setCourseName(null);
        });
  }

  @Test
  public void testSetCourseNameEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setCourseName("");
        });
  }

  // OPERATING HOURS TESTS --------------------------------------------------

  @Test
  public void testSetOpeningTime() {
    settings.setOpeningTime("06:30");
    assertEquals("06:30", settings.getOpeningTime());
  }

  @Test
  public void testSetOpeningTimeInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setOpeningTime("25:00");
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setOpeningTime("12:70");
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setOpeningTime("not a time");
        });
  }

  @Test
  public void testSetClosingTime() {
    settings.setClosingTime("21:00");
    assertEquals("21:00", settings.getClosingTime());
  }

  @Test
  public void testSetClosingTimeInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setClosingTime("25:00");
        });
  }

  @Test
  public void testIsWithinOperatingHours() {
    settings.setOpeningTime("08:00");
    settings.setClosingTime("18:00");

    assertTrue(settings.isWithinOperatingHours("08:00"));
    assertTrue(settings.isWithinOperatingHours("12:00"));
    assertTrue(settings.isWithinOperatingHours("18:00"));
    assertFalse(settings.isWithinOperatingHours("07:59"));
    assertFalse(settings.isWithinOperatingHours("18:01"));
  }

  // PRICING TESTS --------------------------------------------------

  @Test
  public void testSetDefaultPrice() {
    settings.setDefaultPricePerPerson(45.0);
    assertEquals(45.0, settings.getDefaultPricePerPerson());
  }

  @Test
  public void testSetDefaultPriceZero() {
    settings.setDefaultPricePerPerson(0.0);
    assertEquals(0.0, settings.getDefaultPricePerPerson());
  }

  @Test
  public void testSetDefaultPriceNegative() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setDefaultPricePerPerson(-10.0);
        });
  }

  // TEE TIME INTERVAL TESTS --------------------------------------------------

  @Test
  public void testSetTeeTimeInterval() {
    settings.setTeeTimeInterval(20);
    assertEquals(20, settings.getTeeTimeInterval());
  }

  @Test
  public void testSetTeeTimeIntervalInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setTeeTimeInterval(0);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setTeeTimeInterval(-5);
        });
  }

  // PARTY SIZE TESTS --------------------------------------------------

  @Test
  public void testSetMaxPartySize() {
    settings.setMaxPartySize(6);
    assertEquals(6, settings.getMaxPartySize());
  }

  @Test
  public void testSetMaxPartySizeInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setMaxPartySize(0);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setMaxPartySize(-1);
        });
  }

  // TEE BOXES TESTS --------------------------------------------------

  @Test
  public void testSetNumberOfTeeBoxes() {
    settings.setNumberOfTeeBoxes(9);
    assertEquals(9, settings.getNumberOfTeeBoxes());
  }

  @Test
  public void testSetNumberOfTeeBoxesInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setNumberOfTeeBoxes(0);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setNumberOfTeeBoxes(-1);
        });
  }

  // ADVANCE BOOKING TESTS --------------------------------------------------

  @Test
  public void testSetAdvanceBookingDays() {
    settings.setAdvanceBookingDays(30);
    assertEquals(30, settings.getAdvanceBookingDays());
  }

  @Test
  public void testSetAdvanceBookingDaysInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setAdvanceBookingDays(0);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setAdvanceBookingDays(-1);
        });
  }

  // DAYS OF OPERATION TESTS --------------------------------------------------

  @Test
  public void testDefaultDaysOfOperation() {
    assertTrue(settings.isOpenOnDay("Monday"));
    assertTrue(settings.isOpenOnDay("Tuesday"));
    assertTrue(settings.isOpenOnDay("Wednesday"));
    assertTrue(settings.isOpenOnDay("Thursday"));
    assertTrue(settings.isOpenOnDay("Friday"));
    assertTrue(settings.isOpenOnDay("Saturday"));
    assertTrue(settings.isOpenOnDay("Sunday"));
  }

  @Test
  public void testSetDayOperation() {
    settings.setDayOperation("Monday", false);
    assertFalse(settings.isOpenOnDay("Monday"));

    settings.setDayOperation("Monday", true);
    assertTrue(settings.isOpenOnDay("Monday"));
  }

  @Test
  public void testSetDayOperationNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          settings.setDayOperation(null, true);
        });
  }

  @Test
  public void testIsOpenOnDayNull() {
    assertFalse(settings.isOpenOnDay(null));
  }

  @Test
  public void testIsOpenOnDayInvalid() {
    assertFalse(settings.isOpenOnDay("NotADay"));
  }

  // FILE PERSISTENCE TESTS --------------------------------------------------

  @Test
  public void testToFileString() {
    String fileString = settings.toFileString();
    assertNotNull(fileString);
    assertTrue(fileString.contains("Golf Course"));
    assertTrue(fileString.contains("07:00"));
    assertTrue(fileString.contains("19:00"));
  }

  @Test
  public void testFromFileString() {
    String fileString = "Pebble Beach,06:00,20:00,50.0,10,5,18,30,1,1,1,1,1,1,1";
    CourseSettings loaded = CourseSettings.fromFileString(fileString);

    assertNotNull(loaded);
    assertEquals("Pebble Beach", loaded.getCourseName());
    assertEquals("06:00", loaded.getOpeningTime());
    assertEquals("20:00", loaded.getClosingTime());
    assertEquals(50.0, loaded.getDefaultPricePerPerson());
    assertEquals(10, loaded.getTeeTimeInterval());
    assertEquals(5, loaded.getMaxPartySize());
    assertEquals(18, loaded.getNumberOfTeeBoxes());
    assertEquals(30, loaded.getAdvanceBookingDays());
  }

  @Test
  public void testFromFileStringWithClosedDays() {
    String fileString = "Test Course,08:00,17:00,30.0,15,4,18,7,0,1,1,1,1,1,0";
    CourseSettings loaded = CourseSettings.fromFileString(fileString);

    assertNotNull(loaded);
    assertFalse(loaded.isOpenOnDay("Monday")); // First 0
    assertTrue(loaded.isOpenOnDay("Tuesday"));
    assertFalse(loaded.isOpenOnDay("Sunday")); // Last 0
  }

  @Test
  public void testFromFileStringNull() {
    assertNull(CourseSettings.fromFileString(null));
  }

  @Test
  public void testFromFileStringEmpty() {
    assertNull(CourseSettings.fromFileString(""));
  }

  @Test
  public void testFromFileStringInvalid() {
    assertNull(CourseSettings.fromFileString("invalid,data"));
  }

  @Test
  public void testRoundTripPersistence() {
    CourseSettings original =
        new CourseSettings("Test Course", "08:00", "17:00", 35.0, 20, 5, 9, 14);
    original.setDayOperation("Monday", false);
    original.setDayOperation("Sunday", false);

    String fileString = original.toFileString();
    CourseSettings loaded = CourseSettings.fromFileString(fileString);

    assertNotNull(loaded);
    assertEquals(original.getCourseName(), loaded.getCourseName());
    assertEquals(original.getOpeningTime(), loaded.getOpeningTime());
    assertEquals(original.getClosingTime(), loaded.getClosingTime());
    assertEquals(original.getDefaultPricePerPerson(), loaded.getDefaultPricePerPerson());
    assertEquals(original.getTeeTimeInterval(), loaded.getTeeTimeInterval());
    assertEquals(original.getMaxPartySize(), loaded.getMaxPartySize());
    assertEquals(original.getNumberOfTeeBoxes(), loaded.getNumberOfTeeBoxes());
    assertEquals(original.getAdvanceBookingDays(), loaded.getAdvanceBookingDays());
    assertEquals(original.isOpenOnDay("Monday"), loaded.isOpenOnDay("Monday"));
    assertEquals(original.isOpenOnDay("Sunday"), loaded.isOpenOnDay("Sunday"));
  }

  // TOSTRING TEST

  @Test
  public void testToString() {
    String str = settings.toString();
    assertNotNull(str);
    assertTrue(str.contains("Golf Course"));
    assertTrue(str.contains("07:00"));
    assertTrue(str.contains("19:00"));
  }

  // INTEGRATION TESTS --------------------------------------------------

  @Test
  public void testRealisticConfiguration() {
    CourseSettings course = new CourseSettings();

    // Configure for a typical golf course
    course.setCourseName("Pine Valley Golf Club");
    course.setOpeningTime("06:00");
    course.setClosingTime("20:00");
    course.setDefaultPricePerPerson(75.0);
    course.setTeeTimeInterval(10);
    course.setMaxPartySize(4);
    course.setNumberOfTeeBoxes(18);
    course.setAdvanceBookingDays(14);
    course.setDayOperation("Monday", false); // Closed Mondays

    // Verify configuration
    assertEquals("Pine Valley Golf Club", course.getCourseName());
    assertFalse(course.isOpenOnDay("Monday"));
    assertTrue(course.isOpenOnDay("Tuesday"));
    assertTrue(course.isWithinOperatingHours("12:00"));
    assertFalse(course.isWithinOperatingHours("05:30"));
  }

  // Tests for team of 5 portions
  @Test
  public void testTeamOf5Configuration() {
    CourseSettings course = new CourseSettings();

    // Team of 5 needs to schedule 3 months ahead
    course.setAdvanceBookingDays(90);

    assertEquals(90, course.getAdvanceBookingDays());
  }
}
