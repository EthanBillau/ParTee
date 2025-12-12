package com.project.golf.tests;

import com.project.golf.client.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

/**
 * ClientTest.java
 * 
 * Unit test suite for Client network communication and protocol handling.
 * Tests server connection, request sending, and response parsing.
 *
 * Data structures: Client instances, Socket/stream mocks, request/response strings
 * in pipe-delimited protocol format.
 * Algorithm: JUnit 5 with mock networking, protocol string parsing, I/O exception handling.
 * Features: Connection establishment, server communication, protocol compliance,
 * request formatting, response handling, exception propagation.
 *
 * @author Connor Landzettel (clandzet), L15
 *
 * @version November 18, 2025
 */

public class ClientTest {
    private Client client;
    private String host = "localhost";
    private int port = 5050;

    @BeforeEach
    public void setUp() {
        client = new Client(host, port);
    }

    @Test
    public void testConnectFailsWithoutServer() {
        assertThrows(IOException.class, () -> {
            client.connect("localhost", 8080);
        });
    }

    // CONNECTION --------------------------------------------------

    @Test
    public void testDisconnect() {
        BufferedReader mockIn = new BufferedReader(new StringReader(""));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        assertDoesNotThrow(() -> {
            client.disconnect();
        });
    }

    @Test
    public void testSendCommand() {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|OK\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        assertDoesNotThrow(() -> {
            String response = client.sendCommand("TEST_COMMAND");
            assertEquals("RESP|OK", response);
        });
    }

    // LOGIN --------------------------------------------------

    @Test
    public void testLogin() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|OK|Login successful\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String result = client.login("user", "pass");

        assertTrue(result.startsWith("RESP|OK"));
    }

    // TEE TIME --------------------------------------------------

    @Test
    public void testListTeeTimes() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|TT_LIST|9:00,10:00\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.listTeeTimes("2025-12-01");

        assertEquals("RESP|TT_LIST|9:00,10:00", resp);
    }

    @Test
    public void testBookTeeTime() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|TT_BOOKED\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.bookTeeTime("TT123", 4, "user");

        assertEquals("RESP|TT_BOOKED", resp);
    }

    @Test
    public void testListEvents() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|EVENT_LIST|Event1,Event2\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.listEvents();

        assertEquals("RESP|EVENT_LIST|Event1,Event2", resp);
    }

    @Test
    public void testBookEvent() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|EVENT_BOOKED\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.bookEvent("EVT123", 2, "user");

        assertEquals("RESP|EVENT_BOOKED", resp);
    }

    // RESERVATIONS --------------------------------------------------

    @Test
    public void testGetReservations() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|RES_LIST|R1,R2\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.getReservations("user");

        assertEquals("RESP|RES_LIST|R1,R2", resp);
    }

    @Test
    public void testCancelReservation() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|EVENT_CANCELLED\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        String resp = client.cancelReservation("RES123");

        assertEquals("RESP|EVENT_CANCELLED", resp);
    }

    @Test
    public void testAddUser() throws IOException {
        BufferedReader mockIn = new BufferedReader(new StringReader("RESP|OK_USER\n"));
        PrintWriter mockOut = new PrintWriter(new StringWriter(), true);

        client = new Client(null, mockIn, mockOut);

        boolean result = client.addUser("newuser", "newpass", "New", "User", "user.email.com", false);

        assertTrue(result);
    }
}
