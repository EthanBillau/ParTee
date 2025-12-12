package com.project.golf.tests;

import com.project.golf.database.*;
import com.project.golf.reservation.*;
import com.project.golf.users.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;

/**
 * DatabaseTest.java
 * 
 * Unit test suite for Database Singleton operations and persistence.
 * Tests user/reservation/event data operations and file synchronization.
 *
 * Data structures: Database instance (Singleton), ArrayLists for data storage,
 * file-based persistence with pipe-delimited format.
 * Algorithm: JUnit 5 with Singleton pattern validation, file I/O testing, state isolation.
 * Features: Database getInstance(), CRUD operations, file loading/saving, thread-safe locking,
 * data consistency validation, persistence synchronization.
 *
 * @author Aman Wakankar (awakanka), Ethan Billau (ebillau), L15
 *
 * @version November 10, 2025
 */

public class DatabaseTest {
    
    private Database database;
    private static final String TEST_USERS_FILE = "users.txt";
    private static final String TEST_RESERVATIONS_FILE = "reservations.txt";
    private static final String TEST_EVENTS_FILE = "events.txt";
    private static final String TEST_TEETIMES_FILE = "teetimes.txt";
    
    // Set up a fresh database before each test

    @BeforeEach
    public void setUp() {
        // Clean up any existing test files
        deleteTestFiles();
        
        // Reset singleton and get fresh instance
        Database.resetInstance();
        database = Database.getInstance();
        database.clearAllData();
    }
    
    // Clean up after each test

    @AfterEach
    public void tearDown() {
        if (database != null) {
            database.clearAllData();
        }
        Database.resetInstance();
        deleteTestFiles();
    }
    
    // Helper method to delete test files

    private void deleteTestFiles() {
        new File(TEST_USERS_FILE).delete();
        new File(TEST_RESERVATIONS_FILE).delete();
        new File(TEST_EVENTS_FILE).delete();
        new File(TEST_TEETIMES_FILE).delete();
    }
    
    // SINGLETON PATTERN --------------------------------------------------
    
    @Test
    public void testSingletonInstance() {
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();
        
        assertSame(db1, db2, "getInstance should return the same instance");
    }
    
    @Test
    public void testSingletonDataPersistence() {
        Database db1 = Database.getInstance();
        User user = new User("test123", "password", "Test", "User", "test@email.com", false);
        db1.addUser(user);
        
        Database db2 = Database.getInstance();
        User found = db2.findUser("test123");
        
        assertNotNull(found, "Data should persist across getInstance calls");
        assertEquals("test123", found.getUsername());
    }
    
    // USER MANAGEMENT --------------------------------------------------
    
    @Test
    public void testAddUser() {
        User user = new User("john123", "password", "John", "Doe", "john@email.com", false);
        assertTrue(database.addUser(user), "Should add user successfully");
        
        User found = database.findUser("john123");
        assertNotNull(found, "Should find the added user");
        assertEquals("john123", found.getUsername());
    }
    
    @Test
    public void testAddNullUser() {
        assertFalse(database.addUser(null), "Should not add null user");
    }
    
    @Test
    public void testAddDuplicateUser() {
        User user1 = new User("john123", "password", "John", "Doe", "john@email.com", false);
        User user2 = new User("john123", "different", "Jane", "Smith", "jane@email.com", true);
        
        assertTrue(database.addUser(user1), "Should add first user");
        assertFalse(database.addUser(user2), "Should not add duplicate username");
    }
    
    @Test
    public void testRemoveUser() {
        User user = new User("john123", "password", "John", "Doe", "john@email.com", false);
        database.addUser(user);
        
        assertTrue(database.removeUser("john123"), "Should remove user successfully");
        assertNull(database.findUser("john123"), "User should not be found after removal");
    }
    
    @Test
    public void testRemoveNonexistentUser() {
        assertFalse(database.removeUser("nonexistent"), "Should return false for nonexistent user");
    }
    
    @Test
    public void testRemoveNullUsername() {
        assertFalse(database.removeUser(null), "Should return false for null username");
    }
    
    @Test
    public void testFindUser() {
        User user = new User("john123", "password", "John", "Doe", "john@email.com", false);
        database.addUser(user);
        
        User found = database.findUser("john123");
        assertNotNull(found, "Should find the user");
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }
    
    @Test
    public void testFindNonexistentUser() {
        assertNull(database.findUser("nonexistent"), "Should return null for nonexistent user");
    }
    
    @Test
    public void testFindNullUsername() {
        assertNull(database.findUser(null), "Should return null for null username");
    }
    
    @Test
    public void testGetAllUsers() {
        User user1 = new User("john123", "password", "John", "Doe", "john@email.com", false);
        User user2 = new User("jane456", "pass456", "Jane", "Smith", "jane@email.com", true);
        
        database.addUser(user1);
        database.addUser(user2);
        
        ArrayList<User> users = database.getAllUsers();
        assertEquals(2, users.size(), "Should return all users");
    }
    
    @Test
    public void testGetAllUsersEmpty() {
        ArrayList<User> users = database.getAllUsers();
        assertEquals(0, users.size(), "Should return empty list for no users");
    }
    
    @Test
    public void testValidateLogin() {
        User user = new User("john123", "password", "John", "Doe", "john@email.com", false);
        database.addUser(user);
        
        assertTrue(database.validateLogin("john123", "password"), "Should validate correct credentials");
        assertFalse(database.validateLogin("john123", "wrong"), "Should reject wrong password");
        assertFalse(database.validateLogin("wrong", "password"), "Should reject wrong username");
    }
    
    @Test
    public void testValidateLoginNull() {
        assertFalse(database.validateLogin(null, "password"), "Should reject null username");
        assertFalse(database.validateLogin("john123", null), "Should reject null password");
        assertFalse(database.validateLogin(null, null), "Should reject both null");
    }
    
    // RESERVATION MANAGEMENT --------------------------------------------------
    
    @Test
    public void testAddReservation() {
        Reservations res = new Reservations("R1", "john123", "2025-11-15", 
                                           "09:00", 4, "Hole 1", 120.0);
        assertTrue(database.addReservation(res), "Should add reservation successfully");
        
        Reservations found = database.findReservation("R1");
        assertNotNull(found, "Should find the added reservation");
        assertEquals("john123", found.getUsername());
    }
    
    @Test
    public void testAddNullReservation() {
        assertFalse(database.addReservation(null), "Should not add null reservation");
    }
    
    @Test
    public void testAddDuplicateReservation() {
        Reservations res1 = new Reservations("R1", "john123", "2025-11-15", 
                                            "09:00", 4, "Hole 1", 120.0);
        Reservations res2 = new Reservations("R1", "jane456", "2025-11-16", 
                                            "10:00", 2, "Hole 2", 60.0);
        
        assertTrue(database.addReservation(res1), "Should add first reservation");
        assertFalse(database.addReservation(res2), "Should not add duplicate reservation ID");
    }
    
    @Test
    public void testRemoveReservation() {
        Reservations res = new Reservations("R1", "john123", "2025-11-15", 
                                           "09:00", 4, "Hole 1", 120.0);
        database.addReservation(res);
        
        assertTrue(database.removeReservation("R1"), "Should remove reservation successfully");
        assertNull(database.findReservation("R1"), "Reservation should not be found after removal");
    }
    
    @Test
    public void testRemoveNonexistentReservation() {
        assertFalse(database.removeReservation("R999"), "Should return false for nonexistent reservation");
    }
    
    @Test
    public void testFindReservation() {
        Reservations res = new Reservations("R1", "john123", "2025-11-15", 
                                           "09:00", 4, "Hole 1", 120.0);
        database.addReservation(res);
        
        Reservations found = database.findReservation("R1");
        assertNotNull(found, "Should find the reservation");
        assertEquals(4, found.getPartySize());
        assertEquals("Hole 1", found.getTeeBox());
    }
    
    @Test
    public void testGetReservationsByUser() {
        Reservations res1 = new Reservations("R1", "john123", "2025-11-15", 
                                            "09:00", 4, "Hole 1", 120.0);
        Reservations res2 = new Reservations("R2", "john123", "2025-11-16", 
                                            "10:00", 2, "Hole 2", 60.0);
        Reservations res3 = new Reservations("R3", "jane456", "2025-11-15", 
                                            "11:00", 3, "Hole 3", 90.0);
        
        database.addReservation(res1);
        database.addReservation(res2);
        database.addReservation(res3);
        
        ArrayList<Reservations> johnRes = database.getReservationsByUser("john123");
        assertEquals(2, johnRes.size(), "Should return all reservations for john123");
        
        ArrayList<Reservations> janeRes = database.getReservationsByUser("jane456");
        assertEquals(1, janeRes.size(), "Should return all reservations for jane456");
    }
    
    @Test
    public void testGetReservationsByDate() {
        Reservations res1 = new Reservations("R1", "john123", "2025-11-15", 
                                            "09:00", 4, "Hole 1", 120.0);
        Reservations res2 = new Reservations("R2", "jane456", "2025-11-15", 
                                            "10:00", 2, "Hole 2", 60.0);
        Reservations res3 = new Reservations("R3", "bob789", "2025-11-16", 
                                            "11:00", 3, "Hole 3", 90.0);
        
        database.addReservation(res1);
        database.addReservation(res2);
        database.addReservation(res3);
        
        ArrayList<Reservations> nov15 = database.getReservationsByDate("2025-11-15");
        assertEquals(2, nov15.size(), "Should return all reservations for Nov 15");
        
        ArrayList<Reservations> nov16 = database.getReservationsByDate("2025-11-16");
        assertEquals(1, nov16.size(), "Should return all reservations for Nov 16");
    }
    
    @Test
    public void testGetAllReservations() {
        Reservations res1 = new Reservations("R1", "john123", "2025-11-15", 
                                            "09:00", 4, "Hole 1", 120.0);
        Reservations res2 = new Reservations("R2", "jane456", "2025-11-16", 
                                            "10:00", 2, "Hole 2", 60.0);
        
        database.addReservation(res1);
        database.addReservation(res2);
        
        ArrayList<Reservations> all = database.getAllReservations();
        assertEquals(2, all.size(), "Should return all reservations");
    }
    
    // TEE-TIME MANAGEMENT --------------------------------------------------
    
    @Test
    public void testAddTeeTime() {
        TeeTime teeTime = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        assertTrue(database.addTeeTime(teeTime), "Should add tee time successfully");
        
        TeeTime found = database.findTeeTime(teeTime.getTeeTimeId());
        assertNotNull(found, "Should find the added tee time");
        assertEquals("2025-11-15", found.getDate());
    }
    
    @Test
    public void testAddNullTeeTime() {
        assertFalse(database.addTeeTime(null), "Should not add null tee time");
    }
    
    @Test
    public void testAddDuplicateTeeTime() {
        TeeTime tt1 = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        String id = tt1.getTeeTimeId();
        TeeTime tt2 = new TeeTime(id, "2025-11-16", "10:00", "Hole 2", 4, 35.0);
        
        assertTrue(database.addTeeTime(tt1), "Should add first tee time");
        assertFalse(database.addTeeTime(tt2), "Should not add duplicate tee time ID");
    }
    
    @Test
    public void testRemoveTeeTime() {
        TeeTime teeTime = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        database.addTeeTime(teeTime);
        
        assertTrue(database.removeTeeTime(teeTime.getTeeTimeId()), 
                  "Should remove tee time successfully");
        assertNull(database.findTeeTime(teeTime.getTeeTimeId()), 
                  "Tee time should not be found after removal");
    }
    
    @Test
    public void testRemoveNonexistentTeeTime() {
        assertFalse(database.removeTeeTime("TT999"), 
                   "Should return false for nonexistent tee time");
    }
    
    @Test
    public void testFindTeeTime() {
        TeeTime teeTime = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        database.addTeeTime(teeTime);
        
        TeeTime found = database.findTeeTime(teeTime.getTeeTimeId());
        assertNotNull(found, "Should find the tee time");
        assertEquals("09:00", found.getTime());
        assertEquals("Hole 1", found.getTeeBox());
    }
    
    @Test
    public void testGetTeeTimesByDate() {
        TeeTime tt1 = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        TeeTime tt2 = new TeeTime("2025-11-15", "10:00", "Hole 2", 4, 30.0, 50.0);
        TeeTime tt3 = new TeeTime("2025-11-16", "09:00", "Hole 1", 4, 30.0, 50.0);
        
        database.addTeeTime(tt1);
        database.addTeeTime(tt2);
        database.addTeeTime(tt3);
        
        ArrayList<TeeTime> nov15 = database.getTeeTimesByDate("2025-11-15");
        assertEquals(2, nov15.size(), "Should return all tee times for Nov 15");
        
        ArrayList<TeeTime> nov16 = database.getTeeTimesByDate("2025-11-16");
        assertEquals(1, nov16.size(), "Should return all tee times for Nov 16");
    }
    
    @Test
    public void testGetAllTeeTimes() {
        TeeTime tt1 = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        TeeTime tt2 = new TeeTime("2025-11-16", "10:00", "Hole 2", 4, 35.0, 50.0);
        
        database.addTeeTime(tt1);
        database.addTeeTime(tt2);
        
        ArrayList<TeeTime> all = database.getAllTeeTimes();
        assertEquals(2, all.size(), "Should return all tee times");
    }
    
    // DATA PERSISTANCE --------------------------------------------------
    
    @Test
    public void testSaveAndLoadUsers() throws IOException {
        User user1 = new User("john123", "password", "John", "Doe", "john@email.com", false);
        User user2 = new User("jane456", "pass456", "Jane", "Smith", "jane@email.com", true);
        
        database.addUser(user1);
        database.addUser(user2);
        
        // Save to file
        database.saveToFile();
        
        // Create new database instance and load
        Database.resetInstance();
        Database newDb = Database.getInstance();
        
        // Verify users were loaded
        assertNotNull(newDb.findUser("john123"), "Should load john123");
        assertNotNull(newDb.findUser("jane456"), "Should load jane456");
        
        User loadedJohn = newDb.findUser("john123");
        assertEquals("John", loadedJohn.getFirstName());
        assertEquals("Doe", loadedJohn.getLastName());
    }
    
    @Test
    public void testSaveAndLoadReservations() throws IOException {
        Reservations res1 = new Reservations("R1", "john123", "2025-11-15", 
                                            "09:00", 4, "Hole 1", 120.0);
        Reservations res2 = new Reservations("R2", "jane456", "2025-11-16", 
                                            "10:00", 2, "Hole 2", 60.0);
        
        database.addReservation(res1);
        database.addReservation(res2);
        
        // Save to file
        database.saveToFile();
        
        // Create new database instance and load
        Database.resetInstance();
        Database newDb = Database.getInstance();
        
        // Verify reservations were loaded
        assertNotNull(newDb.findReservation("R1"), "Should load R1");
        assertNotNull(newDb.findReservation("R2"), "Should load R2");
        
        Reservations loadedR1 = newDb.findReservation("R1");
        assertEquals(4, loadedR1.getPartySize());
        assertEquals("Hole 1", loadedR1.getTeeBox());
    }
    
    @Test
    public void testSaveAndLoadTeeTimes() throws IOException {
        TeeTime tt1 = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        TeeTime tt2 = new TeeTime("2025-11-16", "10:00", "Hole 2", 4, 35.0, 50.0);
        
        database.addTeeTime(tt1);
        database.addTeeTime(tt2);
        
        // Save to file
        database.saveToFile();
        
        // Create new database instance and load
        Database.resetInstance();
        Database newDb = Database.getInstance();
        
        // Verify tee times were loaded
        assertEquals(2, newDb.getAllTeeTimes().size(), "Should load 2 tee times");
        
        ArrayList<TeeTime> nov15 = newDb.getTeeTimesByDate("2025-11-15");
        assertEquals(1, nov15.size(), "Should have 1 tee time for Nov 15");
        assertEquals("09:00", nov15.get(0).getTime());
    }
    
    @Test
    public void testClearAllData() {
        User user = new User("john123", "password", "John", "Doe", "john@email.com", false);
        Reservations res = new Reservations("R1", "john123", "2025-11-15", 
                                           "09:00", 4, "Hole 1", 120.0);
        TeeTime teeTime = new TeeTime("2025-11-15", "09:00", "Hole 1", 4, 30.0, 50.0);
        
        database.addUser(user);
        database.addReservation(res);
        database.addTeeTime(teeTime);
        
        database.clearAllData();
        
        assertEquals(0, database.getAllUsers().size(), "Should have no users after clear");
        assertEquals(0, database.getAllReservations().size(), "Should have no reservations after clear");
        assertEquals(0, database.getAllTeeTimes().size(), "Should have no tee times after clear");
    }
    
    // THREAD SAFETY --------------------------------------------------
    
    @Test
    public void testConcurrentUserAddition() throws InterruptedException {
        final int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                User user = new User("user" + threadNum, "pass" + threadNum, 
                                    "First" + threadNum, "Last" + threadNum, 
                                    "user" + threadNum + "@email.com", false);
                database.addUser(user);
            });
        }
        
        // Starts all threads
        for (Thread t : threads) {
            t.start();
        }
        
        // Qaits for all threads to complete
        for (Thread t : threads) {
            t.join();
        }
        
        // Verifies all users were added
        assertEquals(numThreads, database.getAllUsers().size(), 
                    "Should have added all users safely");
    }
    
    @Test
    public void testConcurrentReservationAddition() throws InterruptedException {
        final int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                Reservations res = new Reservations("R" + threadNum, "user" + threadNum, 
                                                   "2025-11-15", "09:00", 4, 
                                                   "Hole " + threadNum, 120.0);
                database.addReservation(res);
            });
        }
        
        // Start all threads
        for (Thread t : threads) {
            t.start();
        }
        
        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }
        
        // Verify all reservations were added
        assertEquals(numThreads, database.getAllReservations().size(), 
                    "Should have added all reservations safely");
    }
}
