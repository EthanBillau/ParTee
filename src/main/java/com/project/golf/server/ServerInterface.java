package com.project.golf.server;

import java.io.IOException;

/**
 * Interface for a multi-client server that handles incoming connections.
 * Defines basic lifecycle methods and port access.
 * 
 * @author Ethan Billau (ethanbillau), L15
 * @version November 21, 2025
 */

public interface ServerInterface {
    /**
     * Start the server on the configured port.
     * This method returns immediately and the server runs in its own thread.
     */
    void start() throws IOException;

    //Stop the server (close server socket and interrupt worker threads).

    void stop() throws IOException;

    // Get port the server is listening on.

    int getPort();
}
