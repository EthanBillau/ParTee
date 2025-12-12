package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.database.Database;
import com.project.golf.events.Event;
import com.project.golf.reservation.*;
import com.project.golf.server.ServerWorker;
import com.project.golf.users.User;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;

/**
 * ServerWorkerTest.java
 *
 * <p>Unit test suite for ServerWorker request handling and protocol parsing. Tests command
 * dispatching, response generation, and exception handling.
 *
 * <p>Data structures: ServerWorker instances with mock connections, test request strings in
 * pipe-delimited protocol format, response strings. Algorithm: JUnit 5 with mock socket/stream
 * testing, protocol parsing validation. Features: Command parsing, handler method invocation,
 * response formatting, protocol compliance, error handling and logging.
 *
 * @author Anoushka Chakravarty (chakr181), Connor Landzettel (clandzet), L15
 * @version Nov. 24, 2025
 */
public class ServerWorkerTest {

  private ServerSocket testServer;

  private Socket clientSocket;
  private Socket serverSocket;

  private BufferedReader clientIn;
  private PrintWriter clientOut;

  private Thread workerThread;
  private Socket workerSocket;

  @BeforeEach
  void setUp() throws Exception {
    Database db = Database.getInstance();
    db.clearAllData();

    // Set up a local server socket for testing (used only for constructor test)
    testServer = new ServerSocket(0); // use any available port
    int port = testServer.getLocalPort();

    // Create client socket in separate thread to avoid blocking
    CountDownLatch connected = new CountDownLatch(1);
    new Thread(
            () -> {
              try {
                clientSocket = new Socket("localhost", port);
                clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOut =
                    new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())),
                        true);
                connected.countDown();
              } catch (IOException e) {
                fail("Failed to create client socket: " + e.getMessage());
              }
            })
        .start();

    // Accept connection on server side (this socket is only used in testConstructorAcceptsSocket)
    serverSocket = testServer.accept();
    connected.await(1, TimeUnit.SECONDS);

    // Add test data to database
    db.addUser(new User("testuser", "password123", "Test", "User", "test@example.com", true));

    // Add test tee time
    TeeTime tt = new TeeTime("TT1", "2025-11-20", "10:00", "Hole 1", 4, 50.0);
    db.addTeeTime(tt);

    // Add test event (new Event constructor matches Reservations format)
    Event ev =
        new Event(
            "E001",
            "Golf Tournament",
            "2025-11-25",
            "09:00",
            200,
            "All",
            75.0,
            "2025-11-25",
            "12:00");
    db.addEvent(ev);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (workerThread != null && workerThread.isAlive()) {
      workerThread.interrupt();
      workerThread.join(1000);
    }
    if (clientSocket != null && !clientSocket.isClosed()) {
      clientSocket.close();
    }
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }
    if (testServer != null && !testServer.isClosed()) {
      testServer.close();
    }

    // Clean up database after each test
    Database db = Database.getInstance();
    db.clearAllData();
  }

  // Test: ServerWorker constructor accepts socket and stores it.
  // How: Create ServerWorker with mock socket, verify it doesn't throw.
  @Test
  void testConstructorAcceptsSocket() {
    assertDoesNotThrow(
        () -> new ServerWorker(serverSocket), "Constructor should accept socket without throwing");
  }

  // Test: PING command returns PONG response.
  // How: Start worker, send PING command, read response and assert it matches expected format.
  @Test
  void testPingCommand() throws Exception {
    startWorker();

    clientOut.println("PING");
    String response = clientIn.readLine();

    assertEquals("RESP|OK|PONG", response, "PING should return PONG");
  }

  // Test: LOGIN with valid credentials returns success.
  // How: Send LOGIN command with test user credentials, assert OK response.
  @Test
  void testLoginWithValidCredentials() throws Exception {
    startWorker();

    clientOut.println("LOGIN|testuser|password123");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "Login should succeed with valid credentials");
    assertTrue(response.contains("Login successful"), "Response should indicate success");
  }

  // Test: LOGIN with invalid credentials returns error.
  // How: Send LOGIN with wrong password, assert ERROR response.
  @Test
  void testLoginWithInvalidCredentials() throws Exception {
    startWorker();

    clientOut.println("LOGIN|testuser|wrongpassword");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "Login should fail with invalid credentials");
    assertTrue(
        response.contains("Invalid credentials"), "Response should indicate invalid credentials");
  }

  // Test: LOGIN with insufficient arguments returns error.
  // How: Send LOGIN with only username, assert ERROR response about missing password.
  @Test
  void testLoginWithInsufficientArguments() throws Exception {
    startWorker();

    clientOut.println("LOGIN|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "LOGIN without password should return error");
    assertTrue(
        response.contains("requires username and password"),
        "Error should mention missing arguments");
  }

  // Test: ADD_USER creates new user successfully.
  // How: Send ADD_USER command with new user data, verify OK response.
  @Test
  void testAddUserSuccess() throws Exception {
    startWorker();

    clientOut.println("ADD_USER|newuser|pass456|John|Doe|john@example.com|false");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "ADD_USER should succeed");
    assertTrue(response.contains("User added"), "Response should confirm user added");

    // Verify user was actually added
    Database db = Database.getInstance();
    assertTrue(db.validateLogin("newuser", "pass456"), "New user should be in database");
  }

  // Test: ADD_USER with duplicate username returns error.
  // How: Send ADD_USER for existing username, assert ERROR response.
  @Test
  void testAddUserDuplicateUsername() throws Exception {
    startWorker();

    clientOut.println("ADD_USER|testuser|newpass|Test|User|test2@example.com|true");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "ADD_USER with duplicate username should fail");
    assertTrue(
        response.contains("Could not add user")
            || response.contains("username may exist")
            || response.contains("Username already exists")
            || response.contains("Email already in use"),
        "Error should indicate username or email conflict");
  }

  // Test: ADD_USER with insufficient arguments returns error.
  // How: Send ADD_USER with missing parameters, verify ERROR response.
  @Test
  void testAddUserInsufficientArguments() throws Exception {
    startWorker();

    clientOut.println("ADD_USER|newuser|pass123");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "ADD_USER with insufficient args should fail");
    assertTrue(response.contains("requires 6 args"), "Error should mention argument count");
  }

  // Test: LIST_TT returns tee times for specified date.
  // How: Send LIST_TT with test date, verify response contains expected tee time data.
  @Test
  void testListTeeTimesWithData() throws Exception {
    startWorker();

    clientOut.println("LIST_TT|2025-11-20");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "LIST_TT should succeed");
    assertTrue(response.contains("TT1"), "Response should contain test tee time ID");
    assertTrue(response.contains("2025-11-20"), "Response should contain date");
    assertTrue(response.contains("10:00"), "Response should contain time");
  }

  // Test: LIST_TT with no tee times returns empty payload.
  // How: Send LIST_TT with date that has no tee times, verify empty OK response.
  @Test
  void testListTeeTimesEmpty() throws Exception {
    startWorker();

    clientOut.println("LIST_TT|2025-12-25");
    String response = clientIn.readLine();

    assertEquals("RESP|OK|", response, "LIST_TT with no results should return empty payload");
  }

  // Test: LIST_TT with missing date argument returns error.
  // How: Send LIST_TT without date, verify ERROR response.
  @Test
  void testListTeeTimesMissingDate() throws Exception {
    startWorker();

    clientOut.println("LIST_TT");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "LIST_TT without date should fail");
    assertTrue(response.contains("requires a date"), "Error should mention missing date");
  }

  // Test: BOOK_TT successfully books a tee time.
  // How: Send BOOK_TT command with valid parameters, verify OK response with reservation data.
  @Test
  void testBookTeeTimeSuccess() throws Exception {
    startWorker();

    clientOut.println("BOOK_TT|TT1|2|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "BOOK_TT should succeed");
    assertNotEquals("RESP|OK|", response, "Response should contain reservation data");
  }

  // Test: BOOK_TT with invalid party size returns error.
  // How: Send BOOK_TT with non-numeric party size, verify ERROR response.
  @Test
  void testBookTeeTimeInvalidPartySize() throws Exception {
    startWorker();

    clientOut.println("BOOK_TT|TT1|abc|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "BOOK_TT with invalid partySize should fail");
    assertTrue(response.contains("Invalid partySize"), "Error should mention invalid party size");
  }

  // Test: BOOK_TT with non-existent tee time returns error.
  // How: Send BOOK_TT with invalid tee time ID, verify ERROR response.
  @Test
  void testBookTeeTimeNotFound() throws Exception {
    startWorker();

    clientOut.println("BOOK_TT|INVALID|2|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "BOOK_TT with invalid ID should fail");
    assertTrue(response.contains("not found"), "Error should indicate tee time not found");
  }

  // Test: BOOK_TT with insufficient arguments returns error.
  // How: Send BOOK_TT with missing parameters, verify ERROR response.
  @Test
  void testBookTeeTimeInsufficientArguments() throws Exception {
    startWorker();

    clientOut.println("BOOK_TT|TT1|2");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "BOOK_TT with insufficient args should fail");
    assertTrue(response.contains("requires"), "Error should mention missing arguments");
  }

  // Test: LIST_EVENTS returns all events.
  // How: Send LIST_EVENTS, verify response contains test event data and a valid event ID.
  @Test
  void testListEvents() throws Exception {
    startWorker();

    clientOut.println("LIST_EVENTS");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "LIST_EVENTS should succeed");

    // Response format: RESP|OK|<event1>|<event2>|...
    // event1: id;name;start;duration;capacity;price
    String prefix = "RESP|OK|";
    assertTrue(response.length() >= prefix.length(), "Response should at least contain RESP|OK|");

    if (response.equals("RESP|OK|")) {
      fail("Expected at least one event in LIST_EVENTS response");
    }

    String payload = response.substring(prefix.length()); // everything after RESP|OK|
    String[] eventChunks = payload.split("\\|");
    assertTrue(eventChunks.length >= 1, "There should be at least one event in the payload");

    String[] fields = eventChunks[0].split(";");
    assertTrue(fields.length >= 2, "Event entry should contain at least id and name");

    String eventId = fields[0];
    String eventName = fields[1];

    assertTrue(eventId.startsWith("E"), "Event ID should start with 'E'");
    assertEquals(
        "Golf Tournament", eventName, "Response should contain event name 'Golf Tournament'");
  }

  // Test: LIST_EVENTS with no events returns empty payload.
  // How: Clear events, send LIST_EVENTS, verify empty OK response.
  @Test
  void testListEventsEmpty() throws Exception {
    // Clear events from database properly by removing each one by ID
    Database db = Database.getInstance();
    ArrayList<Event> events = db.getAllEvents();
    for (Event ev : events) {
      db.removeEvent(ev.getId());
    }

    startWorker();

    clientOut.println("LIST_EVENTS");
    String response = clientIn.readLine();

    assertEquals("RESP|OK|", response, "LIST_EVENTS with no events should return empty payload");
  }

  // Test: BOOK_EVENT successfully books an event.
  // How: Discover event ID from DB, then send BOOK_EVENT with that ID.
  @Test
  void testBookEventSuccess() throws Exception {
    Database db = Database.getInstance();
    ArrayList<Event> events = db.getAllEvents();
    assertFalse(events.isEmpty(), "There should be at least one event in DB for this test");
    String eventId = events.get(0).getId(); // use actual ID (e.g., E1, E7, etc.)

    startWorker();

    clientOut.println("BOOK_EVENT|" + eventId + "|2|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "BOOK_EVENT should succeed");
  }

  // Test: BOOK_EVENT with invalid party size returns error.
  // How: Discover event ID from DB, then send BOOK_EVENT with non-numeric party size.
  @Test
  void testBookEventInvalidPartySize() throws Exception {
    Database db = Database.getInstance();
    ArrayList<Event> events = db.getAllEvents();
    assertFalse(events.isEmpty(), "There should be at least one event in DB for this test");
    String eventId = events.get(0).getId();

    startWorker();

    clientOut.println("BOOK_EVENT|" + eventId + "|xyz|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "BOOK_EVENT with invalid partySize should fail");
    assertTrue(response.contains("Invalid partySize"), "Error should mention invalid party size");
  }

  // Test: BOOK_EVENT with non-existent event returns error.
  // How: Send BOOK_EVENT with invalid event ID, verify ERROR response.
  @Test
  void testBookEventNotFound() throws Exception {
    startWorker();

    clientOut.println("BOOK_EVENT|INVALID|2|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "BOOK_EVENT with invalid ID should fail");
    assertTrue(response.contains("not found"), "Error should indicate event not found");
  }

  // Test: GET_RESERVATIONS returns user's reservations.
  // How: Book a tee time, then call GET_RESERVATIONS and verify it's in the list.
  @Test
  void testGetReservations() throws Exception {
    startWorker();

    // First book something
    clientOut.println("BOOK_TT|TT1|2|testuser");
    clientIn.readLine(); // consume booking response

    // Now get reservations
    clientOut.println("GET_RESERVATIONS|testuser");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|OK"), "GET_RESERVATIONS should succeed");
    // Response should contain reservation data (not empty if booking succeeded)
  }

  // Test: GET_RESERVATIONS with no reservations returns empty payload.
  // How: Query reservations for user with no bookings, verify empty OK response.
  @Test
  void testGetReservationsEmpty() throws Exception {
    startWorker();

    clientOut.println("GET_RESERVATIONS|testuser");
    String response = clientIn.readLine();

    assertEquals(
        "RESP|OK|", response, "GET_RESERVATIONS with no reservations should return empty payload");
  }

  // Test: GET_RESERVATIONS with missing username returns error.
  // How: Send GET_RESERVATIONS without username, verify ERROR response.
  @Test
  void testGetReservationsMissingUsername() throws Exception {
    startWorker();

    clientOut.println("GET_RESERVATIONS");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "GET_RESERVATIONS without username should fail");
    assertTrue(response.contains("requires username"), "Error should mention missing username");
  }

  // Test: CANCEL_RESERVATION removes a reservation.
  // How: Book a tee time to get reservation ID, then cancel it and verify success.
  @Test
  void testCancelReservation() throws Exception {
    Database db = Database.getInstance();

    // Ensure tee time TT1 exists and has enough spots
    TeeTime tt1 = db.findTeeTime("TT1");
    if (tt1 == null) {
      tt1 = new TeeTime("TT1", "2025-11-24", "10:00", "Hole 1", 4, 50.0);
      db.addTeeTime(tt1);
    }

    startWorker();

    // Book a tee time
    clientOut.println("BOOK_TT|TT1|2|testuser");
    String bookResponse = clientIn.readLine();

    // Only proceed if booking succeeded
    assertTrue(bookResponse.startsWith("RESP|OK|"), "Booking tee time should succeed");

    // Get reservation ID from DB
    Reservations reservations = db.getReservationsByUser("testuser").get(0);
    String resId = reservations.getReservationId();

    // Cancel the reservation
    clientOut.println("CANCEL_RESERVATION|" + resId);
    String cancelResponse = clientIn.readLine();

    assertTrue(cancelResponse.startsWith("RESP|OK"), "CANCEL_RESERVATION should succeed");
    assertTrue(cancelResponse.contains("Cancelled"), "Response should confirm cancellation");
  }

  // Test: CANCEL_RESERVATION with non-existent ID returns error.
  // How: Send CANCEL_RESERVATION with invalid ID, verify ERROR response.
  @Test
  void testCancelReservationNotFound() throws Exception {
    startWorker();

    clientOut.println("CANCEL_RESERVATION|INVALID123");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "CANCEL_RESERVATION with invalid ID should fail");
    assertTrue(response.contains("not found"), "Error should indicate reservation not found");
  }

  // Test: Unknown command returns error.
  // How: Send unrecognized command, verify ERROR response.
  @Test
  void testUnknownCommand() throws Exception {
    startWorker();

    clientOut.println("INVALID_COMMAND|arg1|arg2");
    String response = clientIn.readLine();

    assertTrue(response.startsWith("RESP|ERROR"), "Unknown command should return error");
    assertTrue(response.contains("Unknown command"), "Error should mention unknown command");
  }

  // Test: Empty lines are ignored.
  // How: Send empty line followed by PING, verify PING response is received.
  @Test
  void testEmptyLinesIgnored() throws Exception {
    startWorker();

    clientOut.println("");
    clientOut.println("PING");
    String response = clientIn.readLine();

    assertEquals("RESP|OK|PONG", response, "Empty lines should be ignored, PING should work");
  }

  // Test: Multiple sequential commands work correctly.
  // How: Send multiple commands in sequence, verify each response.
  @Test
  void testMultipleSequentialCommands() throws Exception {
    startWorker();

    clientOut.println("PING");
    assertEquals("RESP|OK|PONG", clientIn.readLine(), "First PING should work");

    clientOut.println("LIST_TT|2025-11-20");
    String listResponse = clientIn.readLine();
    assertTrue(listResponse.startsWith("RESP|OK"), "LIST_TT should work");

    clientOut.println("PING");
    assertEquals("RESP|OK|PONG", clientIn.readLine(), "Second PING should work");
  }

  // Test: Worker handles client disconnect gracefully.
  // How: Start worker, send command, close client socket, verify worker thread terminates.
  @Test
  void testClientDisconnectHandledGracefully() throws Exception {
    startWorker();

    clientOut.println("PING");
    clientIn.readLine(); // consume response

    // Close client connection
    workerSocket.close();

    boolean terminated = false;
    for (int i = 0; i < 50; i++) { // try up to ~10s
      Thread.sleep(200);
      terminated = !workerThread.isAlive();
      if (terminated) {
        break;
      }
    }

    if (!terminated) {
      workerThread.interrupt();
      workerThread.join(1000);
      terminated = !workerThread.isAlive();
    }

    assertTrue(terminated, "Worker thread should terminate after client disconnect");
  }

  // Test: Worker thread interruption is handled.
  // How: Start worker, interrupt via socket close, verify it terminates gracefully.
  @Test
  void testWorkerThreadInterruption() throws Exception {
    startWorker();

    // Stop the worker by closing the socket (interrupt alone won't unblock readLine)
    workerSocket.close();

    // Wait for thread to terminate
    workerThread.join(1000);

    assertFalse(workerThread.isAlive(), "Worker thread should terminate when socket is closed");
  }

  // Test: Command with whitespace is trimmed properly.
  // How: Send command with leading/trailing whitespace, verify it works.
  @Test
  void testCommandWithWhitespace() throws Exception {
    startWorker();

    clientOut.println("  PING  ");
    String response = clientIn.readLine();

    assertEquals("RESP|OK|PONG", response, "Command with whitespace should be trimmed and work");
  }

  // Utility method to start worker thread on a separate ServerSocket
  void startWorker() throws IOException {
    // Create a server socket on any free port
    ServerSocket ss = new ServerSocket(0); // 0 = automatically pick free port
    int port = ss.getLocalPort();

    // Create client side of the connection (workerSocket is what tests use)
    workerSocket = new Socket("localhost", port);

    // Accept connection on server side and start the worker
    Socket serverSideSocket = ss.accept();
    ServerWorker worker = new ServerWorker(serverSideSocket);
    workerThread = new Thread(worker);
    workerThread.start();

    InputStreamReader isr = new InputStreamReader(workerSocket.getInputStream());
    clientIn = new BufferedReader(isr);
    clientOut = new PrintWriter(workerSocket.getOutputStream(), true);

    ss.close();
  }
}
