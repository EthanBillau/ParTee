package com.project.golf.gui;

/**
 * ServerControllerInterface.java
 *
 * Interface for managing server lifecycle from the GUI layer.
 * Defines methods for starting, stopping, and checking server status.
 *
 * @author Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */

public interface ServerControllerInterface {
    
    /**
     * Checks if the server is currently running
     * 
     * @return true if server is running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Starts the server on the specified port in a separate thread
     * Non-blocking operation that allows GUI to remain responsive.
     * 
     * @param port the port to listen on
     * @param onStart optional callback to execute when server starts (can be null)
     */
    void startServer(int port, Runnable onStart);
    
    /**
     * Stops the running server and cleans up resources
     * Safe to call even if server is not running.
     */
    void stopServer();
}
