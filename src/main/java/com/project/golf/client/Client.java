package com.project.golf.client;
    
import java.io.*;
import java.net.Socket;

/**
 * Client.java
 * 
 * Client side of server-client architecture for golf course reservation system.
 * Simple line-based client that connects to server, sends commands, and reads responses.
 * Implements some convenience methods defined in ClientInterface.
 * 
 * @author Ethan Billau (ebillau), Connor Landzettel (clandzet), L15
 * 
 * @version November 19, 2025
 */

public class Client implements ClientInterface {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // CONSTRUCTORS --------------------------------------------------
    // Used for dependency injection in tests
    
    public Client(String host, int port) {
        this.socket = null;
        this.in = null;
        this.out = null;
    }

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

    @Override
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public String sendCommand(String command) throws IOException {
        if (out == null) throw new IOException("Not connected");
        out.println(command);
        String resp = in.readLine();
        return resp;
    }

    @Override
    public boolean login(String username, String password) throws IOException {
        String resp = sendCommand(String.format("LOGIN|%s|%s", username, password));
        return resp != null && resp.startsWith("RESP|OK");
    }

    @Override
    public String listTeeTimes(String date) throws IOException {
        String resp = sendCommand(String.format("LIST_TT|%s", date));
        return resp;
    }

    @Override
    public String bookTeeTime(String teeTimeId, int partySize, String username) throws IOException {
        String resp = sendCommand(String.format("BOOK_TT|%s|%d|%s", teeTimeId, partySize, username));
        return resp;
    }

    @Override
    public String listEvents() throws IOException {
        return sendCommand("LIST_EVENTS");
    }

    @Override
    public String bookEvent(String eventId, int partySize, String username) throws IOException {
        return sendCommand(String.format("BOOK_EVENT|%s|%d|%s", eventId, partySize, username));
    }

    @Override
    public String getReservations(String username) throws IOException {
        return sendCommand(String.format("GET_RESERVATIONS|%s", username));
    }

    @Override
    public String cancelReservation(String reservationId) throws IOException {
        return sendCommand(String.format("CANCEL_RESERVATION|%s", reservationId));
    }

    @Override
    public boolean addUser(String username, String password, String firstName, String lastName, String email, boolean hasPaid) throws IOException {
        String resp = sendCommand(String.format("ADD_USER|%s|%s|%s|%s|%s|%b",
                username, password, firstName, lastName, email, hasPaid));
        return resp != null && resp.startsWith("RESP|OK");
    }
}
