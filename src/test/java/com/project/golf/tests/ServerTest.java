package com.project.golf.tests;

import com.project.golf.server.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * ServerTest.java
 *
 * Unit test suite for Server startup, client connection, and shutdown operations.
 * Tests multi-client handling and graceful server termination.
 *
 * Data structures: Server instances, client connection threads, ServerSocket management.
 * Algorithm: JUnit 5 with thread testing, socket connection validation, lifecycle verification.
 * Features: Server startup on port, client connection acceptance, worker thread spawning,
 * server shutdown, resource cleanup, thread interrupt handling.
 *
 * @author Nikhil Kodali (kodali3), Anoushka Chakravarty (chakr181), Connor Landzettel (clandzet), L15
 *
 * @version November 19, 2025
 */

public class ServerTest {

    private Server serverUnderTest = null;

    @AfterEach
    void tearDown() throws IOException {
        if (serverUnderTest != null) {
            try {
                serverUnderTest.stop();
            } catch (Exception ignored) {
                // Intentionally left blank
            }
        }
    }

    // Test: Constructor sets port and initial internal state.
    // How: Create server with chosen port, assert getPort() returns expected value,
    //       and inspect private fields 'running' (should be false) and `serverSocket` (should be null).
    @Test
    void testConstructorInitializesFields() {
        serverUnderTest = new Server(25001);
        assertEquals(25001, serverUnderTest.getPort(), "Port should be set by constructor");
        assertFalse(getPrivateBoolean(serverUnderTest, "running"), "Server should not be running after construction");
        assertNull(getPrivate(serverUnderTest, "serverSocket"), "Server socket should be null before start");
    }

    // Test: start() causes server to listen and set running=true.
    // How: Start server, wait until 'running' becomes true (polling). Then verify serverSocket is non-null
    //       and that a socket can connect to the configured port.
    @Test
    void testStartSetsRunningAndAcceptsConnections() throws Exception {
        int port = 25002;
        serverUnderTest = new Server(port);
        serverUnderTest.start();

        // Wait up to 2 seconds for server to begin listening
        assertTrue(waitForRunning(serverUnderTest, 2000), "Server should become running after start()");

        // Inspect serverSocket via reflection
        ServerSocket ss = (ServerSocket) getPrivate(serverUnderTest, "serverSocket");
        assertNotNull(ss, "serverSocket should be set when server is running");
        assertFalse(ss.isClosed(), "ServerSocket should be open");

        // Attempt to create a client connection to ensure accept() will succeed
        try (Socket client = new Socket("localhost", port)) {
            assertTrue(client.isConnected(), "Client socket should connect to server");
        }
    }

    // Test: stop() stops the server and interrupts workers spawned for clients.
    // How: Start server, open two client sockets to cause worker threads to be created,
    //       assert workerThreads list size >= 2, then call stop() and verify 'running' becomes false,
    //       serverSocket is closed, and worker threads are either interrupted or not alive.
    @Test
    void testStopStopsServerAndInterruptsWorkerThreads() throws Exception {
        int port = 25003;
        serverUnderTest = new Server(port);
        serverUnderTest.start();
        assertTrue(waitForRunning(serverUnderTest, 2000), "Server should be running before creating clients");

        // Create two client sockets to force two worker threads to spawn
        Socket c1 = new Socket("localhost", port);
        Socket c2 = new Socket("localhost", port);

        // Give a small moment for server to accept connections and create worker threads
        Thread.sleep(200);

        // Ensure worker threads were spawned
        @SuppressWarnings("unchecked")
        List<Thread> workers = (List<Thread>) getPrivate(serverUnderTest, "workerThreads");
        assertNotNull(workers, "workerThreads list should exist");
        assertTrue(workers.size() >= 2, "At least two worker threads should have been created after two connections");

        // Stop server and allow termination
        serverUnderTest.stop();
        Thread.sleep(200); // allow threads to be interrupted/clean up

        assertFalse(getPrivateBoolean(serverUnderTest, "running"), "Server running flag should be false after stop()");

        ServerSocket ss = (ServerSocket) getPrivate(serverUnderTest, "serverSocket");
        // serverSocket may be closed but still referenced; check closed condition if non-null
        if (ss != null) {
            assertTrue(ss.isClosed() || !ss.isBound(), "Server socket should be closed or unbound after stop()");
        }

        // Check worker threads are not left running
        boolean anyAlive = false;
        for (Thread wt : workers) {
            if (wt != null && wt.isAlive()) anyAlive = true;
        }
        assertFalse(anyAlive, "Worker threads should not remain alive after stop()");
        // cleanup client sockets
        c1.close();
        c2.close();
    }

    // Test: calling start() when already running does not create a second server main thread nor crash.
    // How: Start server, wait for running true, then call start() again and assert no exception and still running.
    @Test
    void testDoubleStartDoesNotCrashWhenAlreadyRunning() throws Exception {
        int port = 25004;
        serverUnderTest = new Server(port);
        serverUnderTest.start();
        assertTrue(waitForRunning(serverUnderTest, 2000), "Server should be running after first start()");
        // second call to start() should not throw and should not crash
        assertDoesNotThrow(() -> serverUnderTest.start(), 
                           "Calling start() again should not throw when already running");
        assertTrue(getPrivateBoolean(serverUnderTest, "running"), "Server should remain running after second start()");
    }

    // Test: stop() before start() is safe (no exception).
    // How: Create Server instance and call stop() immediately; assert no exception is thrown.
    @Test
    void testStopBeforeStartIsSafe() {
        serverUnderTest = new Server(25005);
        assertDoesNotThrow(() -> serverUnderTest.stop(), "Calling stop() before start() should not throw");
        assertFalse(getPrivateBoolean(serverUnderTest, "running"), 
                    "Server should not be running after stop() called before start()");
    }

    // Test: concurrent stop() calls are thread-safe and do not throw.
    // How: Start the server, then spawn two threads that call stop() at the same time, join them,
    //      and assert server is no longer running.
    @Test
    void testConcurrentStopsAreSafe() throws Exception {
        int port = 25006;
        serverUnderTest = new Server(port);
        serverUnderTest.start();
        assertTrue(waitForRunning(serverUnderTest, 2000), "Server should be running before concurrent stop()");

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go = new CountDownLatch(1);
        Runnable stopper = () -> {
            ready.countDown();
            try {
                go.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                // Intentionally left blank
            }
            try {
                serverUnderTest.stop();
            } catch (IOException ignored) {
                // Intentionally left blank
            }
        };

        Thread t1 = new Thread(stopper);
        Thread t2 = new Thread(stopper);
        t1.start();
        t2.start();

        // wait until both threads are ready, then release simultaneously
        ready.await(500, TimeUnit.MILLISECONDS);
        go.countDown();

        t1.join(1000);
        t2.join(1000);

        // After concurrent stops, server should not be running
        assertFalse(
                getPrivateBoolean(serverUnderTest, "running"),
                "Server should not be running after concurrent stop() calls"
        );
    }

    // Test: multiple clients connecting in quick succession spawn worker threads.
    // How: Start server, open three client sockets sequentially, then inspect workerThreads list and assert size >= 3.
    @Test
    void testMultipleClientsSpawnWorkerThreads() throws Exception {
        int port = 25007;
        serverUnderTest = new Server(port);
        serverUnderTest.start();
        assertTrue(waitForRunning(serverUnderTest, 2000), "Server should be running before connecting clients");

        Socket a = new Socket("localhost", port);
        Socket b = new Socket("localhost", port);
        Socket c = new Socket("localhost", port);

        // Give a bit of time for accept() to create worker threads
        Thread.sleep(300);

        @SuppressWarnings("unchecked")
        List<Thread> workers = (List<Thread>) getPrivate(serverUnderTest, "workerThreads");
        assertNotNull(workers, "workerThreads list should exist");
        assertTrue(
                workers.size() >= 3,
                "Three or more worker threads should be present after three client connections"
        );

        // cleanup
        a.close();
        b.close();
        c.close();
    }

    // Utility reflection helper methods used by tests

    private Object getPrivate(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (NoSuchFieldException e1) {
            // try parent (in case of refactor)
            try {
                Field f = obj.getClass().getSuperclass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.get(obj);
            } catch (Exception e2) {
                fail("Failed to access private field '" + fieldName + "': " + e2.getMessage());
                return null;
            }
        } catch (Exception e) {
            fail("Failed to access private field '" + fieldName + "': " + e.getMessage());
            return null;
        }
    }

    private boolean getPrivateBoolean(Object obj, String fieldName) {
        Object val = getPrivate(obj, fieldName);
        return val instanceof Boolean && (Boolean) val;
    }

    private boolean waitForRunning(Server server, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (getPrivateBoolean(server, "running")) return true;
            Thread.sleep(50);
        }
        return false;
    }
}
