package com.project.golf.server;

import com.project.golf.gui.*;
import javax.swing.*;

/**
 * ServerMain.java
 *
 * <p>Application entry point for golf reservation server with admin GUI. Launches the administrator
 * login interface for server control.
 *
 * <p>Data structures: AdminLoginGUI for authentication interface. Algorithm: Swing event-dispatch
 * thread invocation for thread-safe GUI initialization. Features: Server application startup, admin
 * interface initialization, exception handling.
 *
 * @author Ethan Billau (ethanbillau), L15
 * @version November 21, 2025
 */
public class ServerMain {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new AdminLoginGUI());
  }
}
