package com.project.golf.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server.java
 *
 * <p>Multi-client server for ParTee golf reservation system. Listens for incoming client
 * connections and spawns ServerWorker threads for handling. Manages graceful shutdown with proper
 * resource cleanup and thread termination.
 *
 * <p>Data structures: ServerSocket for listening, List of Threads for workers, List of Sockets for
 * client management. Algorithm: Thread-per-client pattern with synchronized collections for thread
 * safety. Features: Multi-threaded client handling, graceful shutdown, connection management.
 *
 * @author Ethan Billau (ebillau), L15
 * @version Nov 24, 2025
 */
public class Server implements ServerInterface, Runnable {

  private final int port;
  // Read by tests via reflection, must be visible across threads
  private volatile boolean running = false;
  // Read by tests via reflection to check open/closed state
  private ServerSocket serverSocket = null;

  /**
   * List of worker threads. Tests reflect on this field and check: - that it exists (non-null) -
   * that its size grows with client connections - that threads are not alive after stop()
   *
   * <p>Using a synchronizedList gives us safe publication and visibility when ServerTest calls
   * size() from a different thread.
   */
  private final List<Thread> workerThreads = Collections.synchronizedList(new ArrayList<>());

  /**
   * List of client sockets so stop() can close them and unblock worker threads. Tests don't inspect
   * this field; it's purely for graceful shutdown.
   */
  private final List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());

  public Server(int port) {
    this.port = port;
  }

  @Override
  public void start() throws IOException {
    // If already running, do nothing (ServerTest expects this to be safe)
    if (running) {
      return;
    }
    Thread t = new Thread(this, "Server-Main");
    t.start();
  }

  @Override
  public void stop() throws IOException {
    // Tell the accept loop to exit
    running = false;

    // Closing serverSocket will unblock accept()
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }

    // Close all client sockets so worker threads unblock from readLine()
    synchronized (clientSockets) {
      for (Socket s : clientSockets) {
        if (s != null && !s.isClosed()) {
          try {
            s.close();
          } catch (IOException ignored) {
          }
        }
      }
    }

    // Interrupt all worker threads so they can terminate.
    // We synchronize because we're iterating the synchronizedList.
    synchronized (workerThreads) {
      for (Thread wt : workerThreads) {
        if (wt != null && wt.isAlive()) {
          wt.interrupt();
        }
      }
    }
  }

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public void run() {
    try (ServerSocket ss = new ServerSocket(port)) {
      this.serverSocket = ss;
      running = true;
      String boundHost = ss.getInetAddress().getHostAddress();
      if ("0.0.0.0".equals(boundHost) || "::".equals(boundHost)) {
        try {
          boundHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
          boundHost = "localhost"; // fallback if hostname lookup fails
        }
      }
      System.out.println("Server listening on " + boundHost + ":" + port);

      while (running) {
        try {
          Socket clientSocket = ss.accept();

          // Track client sockets so stop() can close them
          clientSockets.add(clientSocket);

          // Wrap worker creation & startup so that *any* exception here
          // cannot kill the main accept loop and close the server.
          try {
            ServerWorker worker = new ServerWorker(clientSocket);
            Thread wt = new Thread(worker, "ServerWorker-" + port + "-" + System.nanoTime());

            // Track the worker thread so tests & stop() can see it
            workerThreads.add(wt);

            wt.start();
          } catch (Exception workerError) {
            System.err.println("Error creating/starting worker: " + workerError.getMessage());
            // Clean up this client socket if worker failed to start
            try {
              clientSocket.close();
            } catch (IOException ignored) {
            }
          }

        } catch (SocketException se) {
          // Happens when serverSocket is closed from stop()
          if (!running) {
            break; // normal shutdown path
          }
          System.err.println("Socket error in accept(): " + se.getMessage());
        } catch (IOException ioe) {
          if (!running) {
            break; // shutting down
          }
          System.err.println("I/O error in accept(): " + ioe.getMessage());
        }
      }

    } catch (IOException e) {
      System.err.println("Server socket error: " + e.getMessage());
    } finally {
      running = false;
      if (serverSocket != null && !serverSocket.isClosed()) {
        try {
          serverSocket.close();
        } catch (IOException ignored) {
        }
      }
      System.out.println("Server stopped.");
    }
  }
}
