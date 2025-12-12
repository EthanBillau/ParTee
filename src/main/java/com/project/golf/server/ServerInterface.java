package com.project.golf.server;

import java.io.IOException;

/**
 * ServerInterface.java
 *
 * <p>Interface defining multi-client server lifecycle and connection management. Specifies
 * contracts for server startup, shutdown, and port configuration.
 *
 * <p>Data structures: Port configuration, server socket management. Algorithm: Server lifecycle
 * pattern with start/stop operations and port management. Features: Server startup and shutdown,
 * port configuration access, multi-client request handling.
 *
 * @author Ethan Billau (ethanbillau), L15
 * @version November 21, 2025
 */
public interface ServerInterface {
  /**
   * Start the server on the configured port. This method returns immediately and the server runs in
   * its own thread.
   */
  void start() throws IOException;

  // Stop the server (close server socket and interrupt worker threads).

  void stop() throws IOException;

  // Get port the server is listening on.

  int getPort();
}
