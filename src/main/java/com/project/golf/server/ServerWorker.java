package com.project.golf.server;

import com.project.golf.database.*;
import com.project.golf.events.*;
import com.project.golf.reservation.*;
import com.project.golf.utils.PasswordUtil;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ServerWorker.java
 *
 * Handles individual client connections in the multi-threaded server.
 * Processes requests from one client in dedicated thread, preventing blocking of other clients.
 * Implements pipe-delimited command protocol for client-server communication.
 *
 * Protocol format: COMMAND|arg1|arg2|... -> RESP|OK|payload... or RESP|ERROR|message
 * Data structures: Socket connection, BufferedReader/PrintWriter streams for I/O.
 * Algorithm: Command dispatcher pattern routing to handler methods based on command type.
 * Features: User authentication, tee time booking, reservation management, event approval, admin operations.
 *
 * @author Ethan Billau (ebillau), Nikhil Kodali (kodali3), L15
 *
 * @version December 5, 2025
 */

public class ServerWorker implements Runnable {

    // Client connection management
    private final Socket socket;         // connected client socket
    private BufferedReader in;           // input stream from client
    private PrintWriter out;             // output stream to client

    /**
     * Constructor for ServerWorker
     * 
     * @param socket the client socket to handle
     */
    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("Client connected: " + remote);

        try {
            // Initialize input/output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // Read and process commands in a loop until client disconnects
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    String response = handleCommand(line);
                    out.println(response);
                } catch (Exception e) {
                    out.println("RESP|ERROR|Internal server error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Connection closed: " + remote);
        } finally {
            // Clean up all resources
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    /**
     * Routes incoming command to appropriate handler method
     * Protocol: COMMAND|arg1|arg2|...
     * 
     * @param line the command line from client
     * @return server response (RESP|OK|... or RESP|ERROR|...)
     */
    private String handleCommand(String line) {
        String[] parts = line.split("\\|");
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "PING":
                return "RESP|OK|PONG";
            case "LOGIN":
                return handleLogin(parts);
            case "ADD_USER":
                return handleAddUser(parts);
            case "UPDATE_USER":
                return handleUpdateUser(parts);
            case "GET_USER":
                return handleGetUser(parts);
            case "GET_USER_EMAIL":
                return handleGetUserEmail(parts);
            case "LIST_TT":
                return handleListTeeTimes(parts);
            case "BOOK_TT":
                return handleBookTeeTime(parts);
            case "LIST_EVENTS":
                return handleListEvents(parts);
            case "BOOK_EVENT":
                return handleBookEvent(parts);
            case "CREATE_EVENT":
                return handleCreateEvent(parts);
            case "LIST_PENDING_EVENTS":
                return handleListPendingEvents(parts);
            case "APPROVE_EVENT":
                return handleApproveEvent(parts);
            case "REJECT_EVENT":
                return handleRejectEvent(parts);
            case "CREATE_RESERVATION":
                return handleCreateReservation(parts);
            case "GET_RESERVATIONS":
                return handleGetReservations(parts);
            case "CANCEL_RESERVATION":
                return handleCancelReservation(parts);
            default:
                return "RESP|ERROR|Unknown command: " + cmd;
        }
    }

    /**
     * Handles LOGIN command: authenticates user credentials
     * Verifies user exists, password matches, and account is paid
     * 
     * Protocol: LOGIN|username|password
     * Response: RESP|OK|... if successful, RESP|ERROR|... if failed
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response
     */
    private String handleLogin(String[] parts) {
        if (parts.length < 3) {
            return "RESP|ERROR|LOGIN requires username and password";
        }
        String username = parts[1];
        String password = parts[2];

        Database db = Database.getInstance();
        
        // Reload users from file to get latest user data (e.g., newly added users)
        try {
            db.loadFromFile();
        } catch (Exception e) {
            System.err.println("Warning: Could not reload user data: " + e.getMessage());
        }
        
        com.project.golf.users.User user = db.findUser(username);
        
        if (user == null) {
            return "RESP|ERROR|Invalid credentials";
        }
        
        if (!user.getPassword().equals(password)) {
            return "RESP|ERROR|Invalid credentials";
        }
        
        if (!user.hasPaid()) {
            return "RESP|ERROR|UNPAID|Your account has not been paid. " +
                   "Please contact the golf course to complete payment.";
        }
        
        return "RESP|OK|Login successful";
    }

    /**
     * Handles ADD_USER command: creates new user account
     * Validates username and email are unique, hashes password, and creates user if valid
     * 
     * Protocol: ADD_USER|username|password|first|last|email|hasPaid
     * Response: RESP|OK|... if created, RESP|ERROR|... if failed
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response
     */
    private String handleAddUser(String[] parts) {
        if (parts.length < 7) {
            return "RESP|ERROR|ADD_USER requires 6 args";
        }
        String username = parts[1];
        String password = parts[2];
        String first = parts[3];
        String last = parts[4];
        String email = parts[5];
        boolean hasPaid = Boolean.parseBoolean(parts[6]);

        Database db = Database.getInstance();
        
        // Check if username already exists
        if (db.findUser(username) != null) {
            return "RESP|ERROR|Username already exists";
        }
        
        // Check if email already exists
        if (db.findUserByEmail(email) != null) {
            return "RESP|ERROR|Email already in use";
        }
        
        // Hash the password before storing
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        boolean added = db.addUser(new com.project.golf.users.User(username, hashedPassword, first, last, email, hasPaid));
        if (added) {
            try {
                db.saveToFile();
            } catch (Exception e) {
                // Intentionally left blank
            }
            return "RESP|OK|User added";
        } else {
            return "RESP|ERROR|Could not add user";
        }
    }
    
    /**
     * Handles UPDATE_USER command: modifies existing user information
     * Can change username, password, name, or email
     * 
     * Protocol: UPDATE_USER|oldUsername|newUsername|password|firstName|lastName|email
     * Response: RESP|OK|... if updated, RESP|ERROR|... if failed
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response
     */
    private String handleUpdateUser(String[] parts) {
        if (parts.length < 7) {
            return "RESP|ERROR|UPDATE_USER requires 6 args";
        }
        String oldUsername = parts[1];
        String newUsername = parts[2];
        String password = parts[3];
        String firstName = parts[4];
        String lastName = parts[5];
        String email = parts[6];
        
        Database db = Database.getInstance();
        boolean updated = db.updateUser(oldUsername, newUsername, password, firstName, lastName, email);
        return updated ? "RESP|OK|User updated" : "RESP|ERROR|Could not update user";
    }
    
    // GET_USER|username
    private String handleGetUser(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|GET_USER requires username";
        }
        String username = parts[1];
        
        Database db = Database.getInstance();
        com.project.golf.users.User user = db.findUser(username);
        
        if (user == null) {
            return "RESP|ERROR|User not found";
        }
        
        // Return user data as pipe-delimited string
        return String.format("RESP|OK|%s", user.toFileString());
    }
    
    // GET_USER_EMAIL|username
    private String handleGetUserEmail(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|GET_USER_EMAIL requires username";
        }
        String username = parts[1];
        
        Database db = Database.getInstance();
        
        // Reload users to get latest data
        try {
            db.loadFromFile();
        } catch (Exception e) {
            System.err.println("Warning: Could not reload user data: " + e.getMessage());
        }
        
        String email = db.getUserEmail(username);
        
        if (email == null || email.isEmpty()) {
            return "RESP|ERROR|User not found or no email";
        }
        
        return String.format("RESP|OK|%s", email);
    }

    /**
     * Handles LIST_TT command: retrieves available tee times for a date
     * Returns pipe-separated list of tee time details
     * 
     * Protocol: LIST_TT|YYYY-MM-DD
     * Response: RESP|OK|teeTime1|teeTime2|...
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response with available tee times
     */
    private String handleListTeeTimes(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|LIST_TT requires a date (YYYY-MM-DD)";
        }
        String date = parts[1];
        Database db = Database.getInstance();
        ArrayList<TeeTime> list = db.getTeeTimesByDate(date);
        if (list == null || list.isEmpty()) {
            return "RESP|OK|"; // empty payload

        } 
        StringBuilder sb = new StringBuilder();
        for (TeeTime tt : list) {
            sb.append(String.format("%s;%s;%s;%d;%d;%.2f|",
                    tt.getTeeTimeId(), tt.getDate(), tt.getTime(), tt.getAvailableSpots(),
                                    tt.getMaxPartySize(), tt.getPricePerPerson()));
        }
        // Remove trailing |
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return "RESP|OK|" + sb.toString();
    }

    /**
     * Handles BOOK_TT command: books a tee time for a user
     * Checks availability, validates party size, and creates reservation
     * 
     * Protocol: BOOK_TT|teeTimeId|partySize|username
     * Response: RESP|OK|reservationData if successful, RESP|ERROR|... if failed
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response with reservation details or error message
     */
    private String handleBookTeeTime(String[] parts) {
        if (parts.length < 4) {
            return "RESP|ERROR|BOOK_TT requires teetimeId, partySize, username";
        }
        String ttId = parts[1];
        int partySize;
        try {
            partySize = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return "RESP|ERROR|Invalid partySize";
        }
        String username = parts[3];

        Database db = Database.getInstance();
        TeeTime tt = db.findTeeTime(ttId);
        if (tt == null) {
            return "RESP|ERROR|TeeTime not found";
        }
        
        // Check for conflicts with existing reservations and events
        if (db.hasReservationConflict(tt.getDate(), tt.getTime(), tt.getTeeBox(), null)) {
            return "RESP|ERROR|Cannot book - time slot conflicts with existing reservation or event";
        }
        
        if (!tt.isAvailable(partySize)) {
            return "RESP|ERROR|Not enough spots available";
        }

        // Book and persist via Database if desired
        Reservations r = tt.bookTeeTime(partySize, username);
        boolean saved = false;
        // add reservation to global DB list so Database.saveToFile will persist it
        if (r != null) {
            saved = db.addReservation(r);
            try {
                db.saveToFile();
            } catch (Exception e) {
                // Intentionally left blank
            }
        }
        if (r != null && saved) {
            return "RESP|OK|" + r.toFileString();
        } else if (r != null) {
            return "RESP|OK|Reservation created (not persisted)";
        } else {
            return "RESP|ERROR|Could not create reservation";
        }
    }

    /**
     * Handles LIST_EVENTS command: retrieves all approved events
     * Returns pipe-separated list of event details
     * 
     * Protocol: LIST_EVENTS
     * Response: RESP|OK|event1|event2|...
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response with list of events
     */
    private String handleListEvents(String[] parts) {
        Database db = Database.getInstance();
        ArrayList<Event> events = db.getAllEvents();
        if (events == null || events.isEmpty()) {
            return "RESP|OK|";
        }
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            sb.append(String.format("%s;%s;%s %s;%s %s;%d;%s;%.2f|",
                    e.getId(), e.getName(),
                    e.getDate(), e.getTime(),
                    e.getEndDate(), e.getEndTime(),
                    e.getPartySize(), e.getTeeBox(), e.getPrice()));
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return "RESP|OK|" + sb.toString();
    }

    // BOOK_EVENT|E3|2|username
    private String handleBookEvent(String[] parts) {
        if (parts.length < 4) {
            return "RESP|ERROR|BOOK_EVENT requires eventId, partySize, username";
        }
        String eventId = parts[1];
        int partySize;
        try {
            partySize = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return "RESP|ERROR|Invalid partySize";
        }
        String username = parts[3];

        Database db = Database.getInstance();
        Event ev = db.findEvent(eventId);
        if (ev == null) {
            return "RESP|ERROR|Event not found";
        }
        try {
            // Since Event extends Reservations, we can add it directly as a reservation
            // Create a new reservation for this event booking
            String reservationId = "R" + System.currentTimeMillis();
            Reservations r = new Reservations(reservationId, username, ev.getDate(), ev.getTime(), 
                                               partySize, ev.getTeeBox(), ev.getPrice(), false);
            boolean saved = db.addReservation(r);
            if (saved) {
                try {
                    db.saveToFile();
                } catch (Exception e) {
                    // Intentionally left blank
                }
                return "RESP|OK|" + r.toFileString();
            } else {
                return "RESP|ERROR|Failed to save reservation";
            }
        } catch (IllegalArgumentException ex) {
            return "RESP|ERROR|" + ex.getMessage();
        }
    }

    /**
     * Handles GET_RESERVATIONS command: retrieves all reservations for a user
     * Returns pipe-separated list of reservation details
     * 
     * Protocol: GET_RESERVATIONS|username
     * Response: RESP|OK|reservation1|reservation2|...
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response with user's reservations
     */
    private String handleGetReservations(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|GET_RESERVATIONS requires username";
        }
        String username = parts[1];
        Database db = Database.getInstance();
        ArrayList<Reservations> list = db.getReservationsByUser(username);
        if (list == null || list.isEmpty()) {
            return "RESP|OK|";
        }
        StringBuilder sb = new StringBuilder();
        for (Reservations r : list) {
            sb.append(r.toFileString()).append("|");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return "RESP|OK|" + sb.toString();
    }

    /**
     * Handles CANCEL_RESERVATION command: removes a reservation
     * Finds and deletes the specified reservation from database
     * 
     * Protocol: CANCEL_RESERVATION|reservationId
     * Response: RESP|OK|Cancelled if successful, RESP|ERROR|... if not found
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response indicating success or failure
     */
    private String handleCancelReservation(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|CANCEL_RESERVATION requires reservationId";
        }
        String resId = parts[1];
        Database db = Database.getInstance();
        boolean removed = db.removeReservation(resId);
        try {
            db.saveToFile();
        } catch (Exception e) {
            // Intentionally left blank
        }
        if (removed) {
            return "RESP|OK|Cancelled"; 
        } else {
            return "RESP|ERROR|Reservation not found";
        }
    }
    
    // CREATE_EVENT|eventName|date|time|endDate|endTime|price|username
    private String handleCreateEvent(String[] parts) {
        if (parts.length < 8) {
            return "RESP|ERROR|CREATE_EVENT requires name, date, time, endDate, endTime, price, username";
        }
        
        try {
            String eventName = parts[1];
            String date = parts[2];
            String time = parts[3];
            String endDate = parts[4];
            String endTime = parts[5];
            double price = Double.parseDouble(parts[6]);
            
            // Generate event ID
            String eventId = "E" + System.currentTimeMillis();
            
            // Create event and add to pending list
            Event event = new Event(eventId, eventName, date, time, 200, "All", price, endDate, endTime);
            Database db = Database.getInstance();
            
            if (db.addPendingEvent(event)) {
                try {
                    db.saveToFile();
                } catch (Exception e) {
                    // Intentionally left blank
                }
                return "RESP|OK|Event request submitted for admin approval|" + eventId;
            } else {
                return "RESP|ERROR|Failed to submit event request";
            }
        } catch (NumberFormatException e) {
            return "RESP|ERROR|Invalid price format";
        } catch (Exception e) {
            return "RESP|ERROR|Failed to create event: " + e.getMessage();
        }
    }
    
    // LIST_PENDING_EVENTS
    private String handleListPendingEvents(String[] parts) {
        Database db = Database.getInstance();
        ArrayList<Event> pendingEvents = db.getAllPendingEvents();
        
        if (pendingEvents.isEmpty()) {
            return "RESP|OK|";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Event e : pendingEvents) {
            sb.append(e.toFileString()).append("|");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return "RESP|OK|" + sb.toString();
    }
    
    // APPROVE_EVENT|eventId
    private String handleApproveEvent(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|APPROVE_EVENT requires eventId";
        }
        
        String eventId = parts[1];
        Database db = Database.getInstance();
        
        // Find the pending event first
        Event pendingEvent = db.findPendingEvent(eventId);
        if (pendingEvent == null) {
            return "RESP|ERROR|Pending event not found";
        }
        
        // Approve and remove conflicts
        ArrayList<Reservations> removed = db.approvePendingEvent(eventId);
        
        try {
            db.saveToFile();
        } catch (Exception e) {
            // Intentionally left blank
        }
        
        if (removed != null) {
            // Return conflicts info so admin can see what was deleted
            StringBuilder sb = new StringBuilder();
            sb.append("Event approved");
            if (!removed.isEmpty()) {
                sb.append("|CONFLICTS:");
                for (Reservations r : removed) {
                    sb.append(r.toFileString()).append(";");
                }
            }
            return "RESP|OK|" + sb.toString();
        } else {
            return "RESP|ERROR|Failed to approve event";
        }
    }
    
    // REJECT_EVENT|eventId
    private String handleRejectEvent(String[] parts) {
        if (parts.length < 2) {
            return "RESP|ERROR|REJECT_EVENT requires eventId";
        }
        
        String eventId = parts[1];
        Database db = Database.getInstance();
        
        boolean removed = db.removePendingEvent(eventId);
        try {
            db.saveToFile();
        } catch (Exception e) {
            // Intentionally left blank
        }
        
        if (removed) {
            return "RESP|OK|Event request rejected";
        } else {
            return "RESP|ERROR|Pending event not found";
        }
    }
    
    /**
     * Handles CREATE_RESERVATION command: creates a new reservation
     * Validates date/time availability and creates reservation record
     * Optionally replaces an existing reservation if provided
     * 
     * Protocol: CREATE_RESERVATION|username|date|time|partySize|teeBox|price[|editingId]
     * Response: RESP|OK|reservationData if successful, RESP|ERROR|... if failed
     * 
     * @param parts command parts split by pipe delimiter
     * @return server response with reservation details or error message
     */
    private String handleCreateReservation(String[] parts) {
        if (parts.length < 7) {
            return "RESP|ERROR|CREATE_RESERVATION requires username, date, time, partySize, teeBox, price";
        }
        
        try {
            String username = parts[1];
            String date = parts[2];
            String time = parts[3];
            int partySize = Integer.parseInt(parts[4]);
            String teeBox = parts[5];
            double price = Double.parseDouble(parts[6]);
            String editingReservationId = parts.length > 7 ? parts[7] : null;
            
            Database db = Database.getInstance();
            
            // Check for conflicts
            if (db.hasReservationConflict(date, time, teeBox, editingReservationId)) {
                return "RESP|ERROR|This time slot conflicts with an existing reservation or event";
            }
            
            // Generate reservation ID
            String reservationId = "R" + System.currentTimeMillis();
            
            // Create reservation
            Reservations reservation = new Reservations(reservationId, username, date, time,
                                                         partySize, teeBox, price, false);
            
            // If editing, remove old reservation first
            if (editingReservationId != null && !editingReservationId.isEmpty()) {
                db.removeReservation(editingReservationId);
            }
            
            // Add new reservation
            if (db.addReservation(reservation)) {
                try {
                    db.saveToFile();
                } catch (Exception e) {
                    // Intentionally left blank
                }
                return "RESP|OK|" + reservation.toFileString();
            } else {
                return "RESP|ERROR|Failed to create reservation";
            }
        } catch (NumberFormatException e) {
            return "RESP|ERROR|Invalid number format";
        } catch (Exception e) {
            return "RESP|ERROR|Failed to create reservation: " + e.getMessage();
        }
    }
}
