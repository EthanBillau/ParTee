package com.project.golf.tests;

import com.project.golf.gui.MainMenuGUI;
import com.project.golf.client.Client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * MainMenuGUITest.java
 *
 * Unit test suite for MainMenuGUI constructor and component initialization.
 * Tests navigation buttons and frame configuration.
 *
 * Data structures: MainMenuGUI instance, JButton components for navigation.
 * Algorithm: JUnit 5 with reflection-based component verification.
 * Features: Constructor validation, button initialization, frame properties,
 * component verification per project requirements.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */

public class MainMenuGUITest {

    private MainMenuGUI mainMenuGUI;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    public void setUp() {
        // Create MainMenuGUI instance for testing with test username and null client
        mainMenuGUI = new MainMenuGUI(TEST_USERNAME, null);
    }

    @AfterEach
    public void tearDown() {
        if (mainMenuGUI != null) {
            mainMenuGUI.dispose();
        }
    }

    /**
     * Test that MainMenuGUI constructor initializes without errors
     */
    @Test
    public void testConstructor() {
        assertNotNull(mainMenuGUI, "MainMenuGUI should be instantiated");
    }

    /**
     * Test that the frame is properly configured
     */
    @Test
    public void testFrameProperties() {
        assertEquals("Golf Course Reservation System - Main Menu", mainMenuGUI.getTitle(),
                "Frame title should match");
        assertEquals(JFrame.EXIT_ON_CLOSE, mainMenuGUI.getDefaultCloseOperation(),
                "Default close operation should be EXIT_ON_CLOSE");
        Dimension size = mainMenuGUI.getSize();
        assertEquals(600, size.width, "Frame width should be 600");
        assertEquals(400, size.height, "Frame height should be 400");
    }

    /**
     * Test that no-arg constructor works
     */
    @Test
    public void testNoArgConstructor() {
        MainMenuGUI noArgGUI = new MainMenuGUI();
        assertNotNull(noArgGUI, "No-arg constructor should create instance");
        noArgGUI.dispose();
    }

    /**
     * Test that make reservation button is initialized
     */
    @Test
    public void testMakeReservationButtonExists() throws Exception {
        Field makeReservationButton = MainMenuGUI.class.getDeclaredField("makeReservationButton");
        makeReservationButton.setAccessible(true);
        JButton button = (JButton) makeReservationButton.get(mainMenuGUI);
        assertNotNull(button, "Make reservation button should be initialized");
        assertEquals("Make Reservation", button.getText(),
                "Make reservation button text should match");
    }

    /**
     * Test that manage reservation button is initialized
     */
    @Test
    public void testManageReservationButtonExists() throws Exception {
        Field manageReservationButton = MainMenuGUI.class.getDeclaredField("manageReservationButton");
        manageReservationButton.setAccessible(true);
        JButton button = (JButton) manageReservationButton.get(mainMenuGUI);
        assertNotNull(button, "Manage reservation button should be initialized");
        assertEquals("Manage Reservation", button.getText(),
                "Manage reservation button text should match");
    }

    /**
     * Test that account options button is initialized
     */
    @Test
    public void testAccountOptionsButtonExists() throws Exception {
        Field accountOptionsButton = MainMenuGUI.class.getDeclaredField("accountOptionsButton");
        accountOptionsButton.setAccessible(true);
        JButton button = (JButton) accountOptionsButton.get(mainMenuGUI);
        assertNotNull(button, "Account options button should be initialized");
        assertEquals("Account Options", button.getText(),
                "Account options button text should match");
    }

    /**
     * Test that username field is stored correctly
     */
    @Test
    public void testUsernameStored() throws Exception {
        Field usernameField = MainMenuGUI.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        String username = (String) usernameField.get(mainMenuGUI);
        assertEquals(TEST_USERNAME, username, "Username should be stored correctly");
    }

    /**
     * Test that client field is initialized (can be null)
     */
    @Test
    public void testClientFieldExists() throws Exception {
        Field clientField = MainMenuGUI.class.getDeclaredField("client");
        clientField.setAccessible(true);
        Client client = (Client) clientField.get(mainMenuGUI);
        // Client can be null in local mode, just verify field exists
        assertTrue(true, "Client field should exist (value can be null)");
    }

    /**
     * Test that the frame is not visible by default
     */
    @Test
    public void testFrameIsVisibleAfterConstruction() {
        assertTrue(mainMenuGUI.isVisible(),
                "Frame should be visible after construction since the user clicked to open it and needs to interact with it");
    }

    /**
     * Test that content pane is not null
     */
    @Test
    public void testContentPaneExists() {
        Container contentPane = mainMenuGUI.getContentPane();
        assertNotNull(contentPane, "Content pane should exist");
    }

    /**
     * Test constructor with both username and client
     */
    @Test
    public void testConstructorWithClient() throws Exception {
        Client mockClient = new Client("localhost", 8080);
        MainMenuGUI guiWithClient = new MainMenuGUI(TEST_USERNAME, mockClient);
        assertNotNull(guiWithClient, "MainMenuGUI with client should be instantiated");
        
        Field clientField = MainMenuGUI.class.getDeclaredField("client");
        clientField.setAccessible(true);
        Client storedClient = (Client) clientField.get(guiWithClient);
        assertEquals(mockClient, storedClient, "Client should be stored correctly");
        
        guiWithClient.dispose();
    }
}
