package com.project.golf.client;

import java.io.*;
import java.net.Socket;

/**
 * Client.java
 * 
 * Client-side component of client-server golf reservation system.
 * Manages network communication with server using line-based protocol.
 * Converts high-level operations into protocol messages and parses responses.
 *
 * Data structures: Socket for connection, BufferedReader/PrintWriter for I/O streams.
 * Algorithm: Pipe-delimited command protocol with request-response communication pattern.
 * Features: Login, tee time booking, reservation management, event handling, user administration.
 *
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), L15
 *
 * @version November 19, 2025
 */

public class Client implements ClientInterface {
    // Network communication components
    private Socket socket;                  // server connection socket
    private BufferedReader in;              // input stream from server
    private PrintWriter out;                // output stream to server

    // CONSTRUCTORS --------------------------------------------------
    
    /**
     * Default constructor for lazy initialization
     * Socket and streams are established later via connect()
     * Used for dependency injection in tests
     */
    public Client(String host, int port) {
        this.socket = null;
        this.in = null;
        this.out = null;
    }

    /**
     * Constructor with pre-configured socket and streams
     * Used for testing and dependency injection
     * 
     * @param socket the connected socket
     * @param in the input stream reader
     * @param out the output stream writer
     */
    public Client(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    /**
     * Closes the client connection and cleans up resources
     * 
     * @throws IOException if an I/O error occurs while closing
     */
    @Override
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Sends a raw command to the server and waits for response
     * Server protocol: COMMAND|arg1|arg2|...
     * 
     * @param command the command string to send
     * @return the server response as a string
     * @throws IOException if not connected or I/O error occurs
     */
    @Override
    public String sendCommand(String command) throws IOException {
        if (out == null) throw new IOException("Not connected");
        out.println(command);
        String resp = in.readLine();
        return resp;
    }

    /**
     * Authenticates a user with the server
     * 
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return server response with result and token
     * @throws IOException if I/O error occurs
     */
    @Override
    public String login(String username, String password) throws IOException {
        return sendCommand(String.format("LOGIN|%s|%s", username, password));
    }

    /**
     * Requests list of available tee times for a specific date
     * 
     * @param date the date to query (format: YYYY-MM-DD)
     * @return server response with list of available tee times
     * @throws IOException if I/O error occurs
     */
    @Override
    public String listTeeTimes(String date) throws IOException {
        String resp = sendCommand(String.format("LIST_TT|%s", date));
        return resp;
    }

    /**
     * Books a tee time for a user
     * 
     * @param teeTimeId unique identifier of tee time to book
     * @param partySize number of golfers in the party
     * @param username the user making the reservation
     * @return server response indicating success or failure
     * @throws IOException if I/O error occurs
     */
    @Override
    public String bookTeeTime(String teeTimeId, int partySize, String username) throws IOException {
        String resp = sendCommand(String.format("BOOK_TT|%s|%d|%s", teeTimeId, partySize, username));
        return resp;
    }

    /**
     * Requests list of all pending events
     * 
     * @return server response with list of events
     * @throws IOException if I/O error occurs
     */
    @Override
    public String listEvents() throws IOException {
        return sendCommand("LIST_EVENTS");
    }

    /**
     * Books a pending event for a user
     * 
     * @param eventId unique identifier of event to book
     * @param partySize number of golfers interested in the event
     * @param username the user booking the event
     * @return server response indicating success or failure
     * @throws IOException if I/O error occurs
     */
    @Override
    public String bookEvent(String eventId, int partySize, String username) throws IOException {
        return sendCommand(String.format("BOOK_EVENT|%s|%d|%s", eventId, partySize, username));
    }
    
    /**
     * Creates a new event on the server
     * 
     * @param username the user creating the event
     * @param date start date (YYYY-MM-DD)
     * @param time start time (HH:MM)
     * @param endDate end date (YYYY-MM-DD)
     * @param endTime end time (HH:MM)
     * @param price price per person for the event
     * @return server response indicating success or failure
     * @throws IOException if I/O error occurs
     */
    public String createEvent(String username, String date, String time,
                               String endDate, String endTime, double price) throws IOException {
        return sendCommand(String.format("CREATE_EVENT|%s|%s|%s|%s|%s|%.2f|%s", 
                username, date, time, endDate, endTime, price, username));
    }
    
    /**
     * Creates a new reservation for a user
     * Can be a new reservation or an edit of an existing one
     * 
     * @param username the user making the reservation
     * @param date reservation date (YYYY-MM-DD)
     * @param time reservation time (HH:MM)
     * @param partySize number of golfers
     * @param teeBox which tee box/hole
     * @param price total price
     * @param editingReservationId ID if editing, null or empty if new
     * @return server response indicating success or failure
     * @throws IOException if I/O error occurs
     */
    public String createReservation(String username, String date, String time, int partySize,
                                     String teeBox, double price, String editingReservationId)
                                     throws IOException {
        if (editingReservationId != null && !editingReservationId.isEmpty()) {
            return sendCommand(String.format("CREATE_RESERVATION|%s|%s|%s|%d|%s|%.2f|%s", 
                    username, date, time, partySize, teeBox, price, editingReservationId));
        } else {
            return sendCommand(String.format("CREATE_RESERVATION|%s|%s|%s|%d|%s|%.2f", 
                    username, date, time, partySize, teeBox, price));
        }
    }

    /**
     * Retrieves all reservations for a user
     * 
     * @param username the user whose reservations to retrieve
     * @return server response with list of user's reservations
     * @throws IOException if I/O error occurs
     */
    @Override
    public String getReservations(String username) throws IOException {
        return sendCommand(String.format("GET_RESERVATIONS|%s", username));
    }

    /**
     * Cancels an existing reservation
     * 
     * @param reservationId unique identifier of reservation to cancel
     * @return server response indicating success or failure
     * @throws IOException if I/O error occurs
     */
    @Override
    public String cancelReservation(String reservationId) throws IOException {
        return sendCommand(String.format("CANCEL_RESERVATION|%s", reservationId));
    }

    /**
     * Creates a new user account on the server
     * 
     * @param username the new username
     * @param password the password
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email address
     * @param hasPaid whether user has paid membership fee
     * @return true if user created successfully, false otherwise
     * @throws IOException if I/O error occurs
     */
    @Override
    public boolean addUser(String username, String password, String firstName,
                           String lastName, String email, boolean hasPaid) throws IOException {
        String resp = sendCommand(String.format("ADD_USER|%s|%s|%s|%s|%s|%b",
                username, password, firstName, lastName, email, hasPaid));
        return resp != null && resp.startsWith("RESP|OK");
    }
    
    /**
     * Updates an existing user's information
     * 
     * @param oldUsername the current username
     * @param newUsername the new username (can be same)
     * @param password the new password
     * @param firstName the new first name
     * @param lastName the new last name
     * @param email the new email address
     * @return true if update successful, false otherwise
     * @throws IOException if I/O error occurs
     */
    public boolean updateUser(String oldUsername, String newUsername, String password,
                              String firstName, String lastName, String email) throws IOException {
        String resp = sendCommand(String.format("UPDATE_USER|%s|%s|%s|%s|%s|%s",
                oldUsername, newUsername, password, firstName, lastName, email));
        return resp != null && resp.startsWith("RESP|OK");
    }
    
    /**
     * Retrieves user profile information
     * 
     * @param username the user to retrieve
     * @return server response with user data
     * @throws IOException if I/O error occurs
     */
    public String getUser(String username) throws IOException {
        return sendCommand(String.format("GET_USER|%s", username));
    }
    
    /**
     * Retrieves a user's email address
     * 
     * @param username the user whose email to retrieve
     * @return server response with email address
     * @throws IOException if I/O error occurs
     */
    public String getUserEmail(String username) throws IOException {
        return sendCommand(String.format("GET_USER_EMAIL|%s", username));
    }
}
