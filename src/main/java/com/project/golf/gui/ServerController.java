package com.project.golf.gui;

import com.project.golf.server.Server;

/**
 * ServerController.java
 *
 * Server lifecycle management controller for GUI-based administration.
 * Manages start/stop operations without blocking GUI thread.
 *
 * Data structures: Server instance, Thread for server execution,
 * volatile boolean flag for thread-safe running state tracking.
 * Algorithm: Non-blocking server startup in separate thread with optional
 * callback, graceful shutdown, running state monitoring.
 * Features: Async server startup with callbacks, clean shutdown, running state tracking,
 * exception handling and logging, thread-safe status monitoring.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */

public class ServerController implements ServerControllerInterface {
    private Server server;  // golf reservation server instance
    private Thread serverThread;  // thread running the server
    private volatile boolean running = false;  // thread-safe flag for running state

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void startServer(int port, Runnable onStart) {
        if (running) {
            System.out.println("Server is already running.");
            return;
        }

        /**
         * Run server in separate thread to avoid blocking the GUI.
         * The server will continue running until explicitly stopped.
         */
        serverThread = new Thread(() -> {
            try {
                System.out.println("Starting server on port " + port + "...");
                server = new Server(port);
                running = true;
                if (onStart != null) onStart.run(); // Notify caller that server started
                server.start(); // Blocking call that runs until server stops
            } catch (Exception e) {
                System.out.println("Server failed: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        });
        serverThread.start();
    }

    @Override
    public void stopServer() {
        if (!running || server == null) {
            System.out.println("Server is not running.");
            return;
        }

        try {
            System.out.println("Stopping server...");
            /**
             * Call server's stop method to cleanly shut down.
             * This should close all connections and free the port.
             */
            server.stop();
        } catch (Exception e) {
            System.out.println("Error stopping server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            running = false; // Always mark as stopped
        }
    }
}
