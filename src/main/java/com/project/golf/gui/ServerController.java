package com.project.golf.gui;

import com.project.golf.server.Server;

public class ServerController {
    private Server server;
    private Thread serverThread;
    private volatile boolean running = false;

    public boolean isRunning() {
        return running;
    }

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
