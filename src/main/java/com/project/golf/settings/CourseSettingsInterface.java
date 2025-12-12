package com.project.golf.settings;

/**
 * CourseSettingsInterface.java
 * 
 * Interface for course configuration and operational settings.
 * Stores information that the server uses to manage the golf course,
 * such as operating hours, default pricing, and course details.
 *
 * @author Nikhil Kodali (kodali3), L15
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
