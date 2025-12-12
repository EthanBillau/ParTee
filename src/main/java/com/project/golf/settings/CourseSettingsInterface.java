package com.project.golf.settings;

/**
 * CourseSettingsInterface.java
 * 
 * Interface defining golf course configuration and business rule parameters.
 * Specifies contracts for operational settings management and pricing configuration.
 *
 * Data structures: Course properties (hours, pricing, party size limits), day-of-week
 * settings HashMap for schedule customization.
 * Algorithm: Configuration property accessor pattern with day-specific overrides.
 * Features: Operating hours definition, pricing policy management, party size constraints,
 * advance booking policies, day-of-week schedule customization.
 *
 * @author Nikhil Kodali (kodali3), L15
 *
 * @version November 9, 2025
 */

public interface CourseSettingsInterface {
    
    // Getters for all varaibles in CourseSettings
    String getCourseName();
    String getOpeningTime();
    String getClosingTime();
    double getDefaultPricePerPerson();
    int getTeeTimeInterval();
    int getMaxPartySize();
    int getNumberOfTeeBoxes();
    boolean isOpenOnDay(String dayOfWeek);
    int getAdvanceBookingDays();
    String toFileString();

    // Setters for those same variables
    void setCourseName(String courseName);
    void setOpeningTime(String openingTime);
    void setClosingTime(String closingTime);
    void setDefaultPricePerPerson(double price);
    void setTeeTimeInterval(int minutes);
    void setMaxPartySize(int size);
    void setNumberOfTeeBoxes(int count);
    void setDayOperation(String dayOfWeek, boolean isOpen);
    void setAdvanceBookingDays(int days);


}
