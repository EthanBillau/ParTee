package com.project.golf.client;

import java.io.IOException;

/**
 * ClientInterface.java
 *
 * <p>Interface defining client-side network operations and server communication contracts.
 * Specifies methods for connection management and request sending with pipe-delimited protocol.
 *
 * <p>Data structures: Connection interfaces (connect/disconnect), request transmission methods.
 * Algorithm: Network communication via sockets with serialized request/response handling. Features:
 * Server connection management, request sending, response handling, I/O exception propagation.
 *
 * @author Ethan Billau (ebillau), L15
 * @version November 19, 2025
 */
public interface ClientInterface {

  // Connect to the server.
  void connect(String host, int port) throws IOException;

  // Disconnect from the server.
  void disconnect() throws IOException;

  /**
   * Send a raw command and receive the raw response line. Useful for testing and for commands not
   * wrapped by convenience methods.
   */
  String sendCommand(String command) throws IOException;

  // Convenience API (examples - implemented in Client.java)
  String login(String username, String password) throws IOException;

  String listTeeTimes(String date) throws IOException; // returns compact list string

  String bookTeeTime(String teeTimeId, int partySize, String username) throws IOException;

  String listEvents() throws IOException;

  String bookEvent(String eventId, int partySize, String username) throws IOException;

  String getReservations(String username) throws IOException;

  String cancelReservation(String reservationId) throws IOException;

  boolean addUser(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      boolean hasPaid)
      throws IOException;
}
