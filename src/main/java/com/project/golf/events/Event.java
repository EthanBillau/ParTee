package com.project.golf.events;

import com.project.golf.reservation.Reservations;

public class Event extends Reservations implements EventInterface {

    private String endDate;
    private String endTime;

    public Event(String reservationId, String username, String date,
                 String time, int partySize, String teeBox, double price,
                 String endDate, String endTime) {

        super(reservationId, username, date, time, 200, "All", price);

        this.endDate = endDate;
        this.endTime = endTime;
    }

    /**
     * Gets the end date of the event
     *
     * @return end date as a String
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Gets the end time of the event
     *
     * @return end time as a String
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Indicates if this reservation is an event
     * 
     * @return true, since this is an event
     */
    @Override
    public boolean isEvent() {
        return true;
    }

    /**
     * Gets the name associated with the event
     * 
     * @return the username as the event name
     */
    @Override
    public String getName() {
        return getUsername();
    }

    /**
     * Gets the ID of the event
     * 
     * @return the reservation ID as the event ID
     */
    @Override
    public String getId() {
        return getReservationId();
    }

    /**
     * Converts the event details to a file string format
     *
     * @return formatted string for file storage
     */
    @Override
    public String toFileString() {
        return String.format("EVENT,%s,%s,%s,%s,%d,%s,%.2f,%b,%s,%s",
                getReservationId(), getUsername(), getDate(), getTime(),
                getPartySize(), getTeeBox(), getPrice(), getIsPaid(),
                endDate, endTime);
    }
}
