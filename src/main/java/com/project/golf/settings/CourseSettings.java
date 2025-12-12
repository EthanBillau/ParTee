package com.project.golf.settings;

import java.util.*;

/**
 * CourseSettings.java
 *
 * <p>Configuration and operational settings for golf course management. Stores course details,
 * hours of operation, pricing, and booking constraints. Used by server to enforce business rules
 * for tee time scheduling and reservations.
 *
 * <p>Data structures: String fields for time/name, int fields for numeric settings, HashMap for
 * day-of-week operational status. Algorithm: Direct property access with validation. Settings
 * loaded/saved to file format. Features: Course hours, pricing defaults, party size limits, advance
 * booking windows.
 *
 * @author Nikhil Kodali (kodali3), L15
 * @version November 9, 2025
 */
public class CourseSettings implements CourseSettingsInterface {

  private String courseName; // name of the golf course
  private String openingTime; // course opening time (HH:MM)
  private String closingTime; // course closing time (HH:MM)
  private double defaultPricePerPerson; // default price per golfer
  private int teeTimeInterval; // minutes between consecutive tee times
  private int maxPartySize; // maximum golfers per reservation
  private int numberOfTeeBoxes; // number of holes/tee boxes
  private int advanceBookingDays; // days in advance users can book
  private Map<String, Boolean> daysOfOperation; // which days course is open

  // Constructor with default values

  public CourseSettings() {
    this.courseName = "Golf Course";
    this.openingTime = "07:00";
    this.closingTime = "19:00";
    this.defaultPricePerPerson = 30.0;
    this.teeTimeInterval = 15; // 15 minutes between tee times
    this.maxPartySize = 4;
    this.numberOfTeeBoxes = 18; // Standard 18-hole course
    this.advanceBookingDays = 7;

    // Initialize days of operation (open all week by default)
    daysOfOperation = new HashMap<>();
    daysOfOperation.put("Monday", true);
    daysOfOperation.put("Tuesday", true);
    daysOfOperation.put("Wednesday", true);
    daysOfOperation.put("Thursday", true);
    daysOfOperation.put("Friday", true);
    daysOfOperation.put("Saturday", true);
    daysOfOperation.put("Sunday", true);
  }

  /**
   * Constructor with all parameters
   *
   * @param courseName name of the golf course
   * @param openingTime opening time (HH:MM format)
   * @param closingTime closing time (HH:MM format)
   * @param defaultPricePerPerson default price per golfer
   * @param teeTimeInterval minutes between tee times
   * @param maxPartySize maximum party size
   * @param numberOfTeeBoxes number of holes/tee boxes
   * @param advanceBookingDays how many days in advance can book
   */
  public CourseSettings(
      String courseName,
      String openingTime,
      String closingTime,
      double defaultPricePerPerson,
      int teeTimeInterval,
      int maxPartySize,
      int numberOfTeeBoxes,
      int advanceBookingDays) {
    this();
    this.courseName = courseName;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
    this.defaultPricePerPerson = defaultPricePerPerson;
    this.teeTimeInterval = teeTimeInterval;
    this.maxPartySize = maxPartySize;
    this.numberOfTeeBoxes = numberOfTeeBoxes;
    this.advanceBookingDays = advanceBookingDays;
  }

  @Override
  public String getCourseName() {
    return courseName;
  }

  @Override
  public void setCourseName(String courseName) {
    if (courseName == null || courseName.trim().isEmpty()) {
      throw new IllegalArgumentException("Course name cannot be null or empty");
    }
    this.courseName = courseName;
  }

  @Override
  public String getOpeningTime() {
    return openingTime;
  }

  @Override
  public void setOpeningTime(String openingTime) {
    if (openingTime == null || !isValidTimeFormat(openingTime)) {
      throw new IllegalArgumentException("Invalid opening time format. Use HH:MM");
    }
    this.openingTime = openingTime;
  }

  @Override
  public String getClosingTime() {
    return closingTime;
  }

  @Override
  public void setClosingTime(String closingTime) {
    if (closingTime == null || !isValidTimeFormat(closingTime)) {
      throw new IllegalArgumentException("Invalid closing time format. Use HH:MM");
    }
    this.closingTime = closingTime;
  }

  @Override
  public double getDefaultPricePerPerson() {
    return defaultPricePerPerson;
  }

  @Override
  public void setDefaultPricePerPerson(double price) {
    if (price < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    this.defaultPricePerPerson = price;
  }

  @Override
  public int getTeeTimeInterval() {
    return teeTimeInterval;
  }

  @Override
  public void setTeeTimeInterval(int minutes) {
    if (minutes <= 0) {
      throw new IllegalArgumentException("Interval must be positive");
    }
    this.teeTimeInterval = minutes;
  }

  @Override
  public int getMaxPartySize() {
    return maxPartySize;
  }

  @Override
  public void setMaxPartySize(int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Party size must be positive");
    }
    this.maxPartySize = size;
  }

  @Override
  public int getNumberOfTeeBoxes() {
    return numberOfTeeBoxes;
  }

  @Override
  public void setNumberOfTeeBoxes(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Number of tee boxes must be positive");
    }
    this.numberOfTeeBoxes = count;
  }

  @Override
  public boolean isOpenOnDay(String dayOfWeek) {
    if (dayOfWeek == null) {
      return false;
    }
    return daysOfOperation.getOrDefault(dayOfWeek, false);
  }

  @Override
  public void setDayOperation(String dayOfWeek, boolean isOpen) {
    if (dayOfWeek == null) {
      throw new IllegalArgumentException("Day of week cannot be null");
    }
    daysOfOperation.put(dayOfWeek, isOpen);
  }

  @Override
  public int getAdvanceBookingDays() {
    return advanceBookingDays;
  }

  @Override
  public void setAdvanceBookingDays(int days) {
    if (days <= 0) {
      throw new IllegalArgumentException("Advance booking days must be positive");
    }
    this.advanceBookingDays = days;
  }

  /**
   * Check if a time string is in HH:MM format
   *
   * @param time time string to validate
   * @return true if valid format, false otherwise
   */
  private boolean isValidTimeFormat(String time) {
    if (time == null || time.length() != 5) {
      return false;
    }

    String[] parts = time.split(":");
    if (parts.length != 2) {
      return false;
    }

    try {
      int hour = Integer.parseInt(parts[0]);
      int minute = Integer.parseInt(parts[1]);
      return hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Check if a time is within operating hours
   *
   * @param time time to check in HH:MM format
   * @return true if within hours, false otherwise
   */
  public boolean isWithinOperatingHours(String time) {
    if (time == null || !isValidTimeFormat(time)) {
      return false;
    }

    return time.compareTo(openingTime) >= 0 && time.compareTo(closingTime) <= 0;
  }

  @Override
  public String toFileString() {
    StringBuilder sb = new StringBuilder();
    sb.append(courseName).append(",");
    sb.append(openingTime).append(",");
    sb.append(closingTime).append(",");
    sb.append(defaultPricePerPerson).append(",");
    sb.append(teeTimeInterval).append(",");
    sb.append(maxPartySize).append(",");
    sb.append(numberOfTeeBoxes).append(",");
    sb.append(advanceBookingDays).append(",");

    // Add days of operation
    for (String day :
        new String[] {
          "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        }) {
      sb.append(daysOfOperation.get(day) ? "1" : "0").append(",");
    }

    // Remove trailing comma
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
      sb.setLength(sb.length() - 1);
    }

    return sb.toString();
  }

  /**
   * Create CourseSettings from file string
   *
   * @param fileString formatted string from file
   * @return CourseSettings object or null if invalid
   */
  public static CourseSettings fromFileString(String fileString) {
    if (fileString == null || fileString.trim().isEmpty()) {
      return null;
    }

    try {
      String[] parts = fileString.split(",");
      if (parts.length < 15) {
        return null;
      }

      String courseName = parts[0];
      String openingTime = parts[1];
      String closingTime = parts[2];
      double defaultPrice = Double.parseDouble(parts[3]);
      int interval = Integer.parseInt(parts[4]);
      int maxParty = Integer.parseInt(parts[5]);
      int numTeeBoxes = Integer.parseInt(parts[6]);
      int advanceBooking = Integer.parseInt(parts[7]);

      CourseSettings settings =
          new CourseSettings(
              courseName,
              openingTime,
              closingTime,
              defaultPrice,
              interval,
              maxParty,
              numTeeBoxes,
              advanceBooking);

      // Load days of operation
      String[] days = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
      };
      for (int i = 0; i < 7 && i + 8 < parts.length; i++) {
        boolean isOpen = parts[i + 8].equals("1");
        settings.setDayOperation(days[i], isOpen);
      }

      return settings;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return String.format(
        "CourseSettings[%s | Hours: %s-%s | $%.2f | %d tee boxes | Book %d days ahead]",
        courseName,
        openingTime,
        closingTime,
        defaultPricePerPerson,
        numberOfTeeBoxes,
        advanceBookingDays);
  }
}
