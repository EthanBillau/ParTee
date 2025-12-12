package com.project.golf.server;

import java.io.IOException;

/**
 * Interface for per-client worker threads created by the Server. A ServerWorker manages
 * communication with a single client, including receiving protocol commands, processing them, and
 * returning responses. Workers run independently and should terminate cleanly when stopped or when
 * the client disconnects.
 *
 * <p>Thread safety note: Each worker handles only its own client connection. Shared backend
 * components (e.g., Database) must provide their own thread safety.
 *
 * @author Nikhil Kodali (kodali3)
 * @version November 24, 2025
 */
public interface ServerWorkerInterface extends Runnable {

  /**
   * Starts the worker and begins handling client communication. Typically launches the worker's run
   * loop in a separate thread.
   */
  void start();

  /**
   * Stops the worker by closing the socket and ending its run loop. Worker implementations must
   * release all resources gracefully.
   *
   * @throws IOException if the client socket cannot be closed
   */
  void stop() throws IOException;

  // run() inherited from Runnable
}
