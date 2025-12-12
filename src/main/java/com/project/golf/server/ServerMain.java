package com.project.golf.server;

import com.project.golf.gui.*;

import javax.swing.*;

/**
 * ServerMain.java
 * 
 * Entry point for the golf course server application.
 * Initializes a Server instance on a specified port and starts it.
 * Handles exceptions if the server fails to start.
 *
 * @author Ethan Billau (ethanbillau), L15
 * @version November 21, 2025
 */

public class ServerMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLoginGUI());
    }
}
