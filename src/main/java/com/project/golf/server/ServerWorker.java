package com.project.golf.server;

import com.project.golf.database.*;
import com.project.golf.events.*;
import com.project.golf.reservation.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ServerWorker handles one client connection. It reads line-based commands and
 * writes line-based responses using a simple protocol.
 *
 * Protocol: - Client -> Server: COMMAND|arg1|arg2|... - Server -> Client:
 * RESP|OK|payload... OR RESP|ERROR|message
 *
 * Examples: LOGIN|username|password LIST_TT|2025-11-20 BOOK_TT|TT5|2|username
 * LIST_EVENTS BOOK_EVENT|E3|2|username GET_RESERVATIONS|username
 * CANCEL_RESERVATION|R123 ADD_USER|username|password|first|last|email|true
 *
 * @author Ethan Billau (ethanbillau), L15
 *
 * @version November 20, 2025
 */
public class ServerWorker implements Runnable {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("Client connected: " + remote);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

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
            case "LIST_TT":
                return handleListTeeTimes(parts);
            case "BOOK_TT":
                return handleBookTeeTime(parts);
            case "LIST_EVENTS":
                return handleListEvents(parts);
            case "BOOK_EVENT":
                return handleBookEvent(parts);
            case "GET_RESERVATIONS":
                return handleGetReservations(parts);
            case "CANCEL_RESERVATION":
                return handleCancelReservation(parts);
            default:
                return "RESP|ERROR|Unknown command: " + cmd;
        }
    }

    // LOGIN|username|password
    private String handleLogin(String[] parts) {
        if (parts.length < 3) {
            return "RESP|ERROR|LOGIN requires username and password";
        }
        String username = parts[1];
        String password = parts[2];

        Database db = Database.getInstance();
        boolean ok = db.validateLogin(username, password);
        if (ok) {
            return "RESP|OK|Login successful"; 
        } else {
            return "RESP|ERROR|Invalid credentials";
        }
    }

    // ADD_USER|username|password|first|last|email|hasPaid
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
        boolean added = db.addUser(new com.project.golf.users.User(username, password, first, last, email, hasPaid));
        if (added) {
            try {
                db.saveToFile();
            } catch (Exception e) {
                // Intentionally left blank
            }
            return "RESP|OK|User added";
        } else {
            return "RESP|ERROR|Could not add user (username may exist)";
        }
    }

    // LIST_TT|YYYY-MM-DD
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
            sb.append("%s;%s;%s;%d;%d;%.2f|".formatted(
                    tt.getTeeTimeId(), tt.getDate(), tt.getTime(), tt.getAvailableSpots(),
                    tt.getMaxPartySize(), tt.getPricePerPerson()));
        }
        // Remove trailing |
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return "RESP|OK|" + sb.toString();
    }

    // BOOK_TT|TT5|2|username
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

    // LIST_EVENTS
    private String handleListEvents(String[] parts) {
        Database db = Database.getInstance();
        ArrayList<Event> events = db.getAllEvents();
        if (events == null || events.isEmpty()) {
            return "RESP|OK|";
        }
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            sb.append("%s;%s;%s %s;%s %s;%d;%s;%.2f|".formatted(
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

    // GET_RESERVATIONS|username
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

    // CANCEL_RESERVATION|reservationId
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
}
