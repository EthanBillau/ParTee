package com.project.golf.client;

import java.io.IOException;

/**
 * ClientInterface.java
 * 
 * Interface for the client class
 * 
 * @author Ethan Billau (ebillau), L15
 * 
 * @version November 19, 2025
 */

public interface ClientInterface {

    //Connect to the server.
    void connect(String host, int port) throws IOException;

    // Disconnect from the server.
    void disconnect() throws IOException;

    /**
     * Send a raw command and receive the raw response line.
     * Useful for testing and for commands not wrapped by convenience methods.
     */
    String sendCommand(String command) throws IOException;

    // Convenience API (examples - implemented in Client.java)
    boolean login(String username, String password) throws IOException;
    String listTeeTimes(String date) throws IOException; // returns compact list string
    String bookTeeTime(String teeTimeId, int partySize, String username) throws IOException;
    String listEvents() throws IOException;
    String bookEvent(String eventId, int partySize, String username) throws IOException;
    String getReservations(String username) throws IOException;
    String cancelReservation(String reservationId) throws IOException;
    boolean addUser(String username, String password, String firstName, String lastName, String email, boolean hasPaid) throws IOException;
}
