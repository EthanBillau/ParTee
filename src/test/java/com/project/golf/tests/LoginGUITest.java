package com.project.golf.tests;

import com.project.golf.gui.LoginGUI;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * LoginGUITest.java
 *
 * Unit test suite for LoginGUI constructor and component initialization.
 * Tests GUI creation, field initialization, and component configuration.
 *
 * Data structures: LoginGUI instance, JTextField/JPasswordField/JButton components.
 * Algorithm: JUnit 5 with reflection-based component verification.
 * Features: Constructor validation, component initialization, frame properties,
 * button and field verification per project requirements.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */

public class LoginGUITest {

    private LoginGUI loginGUI;

    @BeforeEach
    public void setUp() {
        // Create LoginGUI instance for testing
        loginGUI = new LoginGUI();
    }

    @AfterEach
    public void tearDown() {
        if (loginGUI != null) {
            loginGUI.dispose();
        }
    }

    /**
     * Test that LoginGUI constructor initializes without errors
     */
    @Test
    public void testConstructor() {
        assertNotNull(loginGUI, "LoginGUI should be instantiated");
    }

    /**
     * Test that the frame is properly configured
     */
    @Test
    public void testFrameProperties() {
        assertEquals("Golf Course Reservation System", loginGUI.getTitle(),
                "Frame title should match");
        assertEquals(JFrame.EXIT_ON_CLOSE, loginGUI.getDefaultCloseOperation(),
                "Default close operation should be EXIT_ON_CLOSE");
        Dimension size = loginGUI.getSize();
        assertEquals(600, size.width, "Frame width should be 600");
        assertEquals(400, size.height, "Frame height should be 400");
    }

    /**
     * Test that username field is initialized
     */
    @Test
    public void testUsernameFieldExists() throws Exception {
        Field usernameField = LoginGUI.class.getDeclaredField("usernameField");
        usernameField.setAccessible(true);
        JTextField field = (JTextField) usernameField.get(loginGUI);
        assertNotNull(field, "Username field should be initialized");
    }

    /**
     * Test that password field is initialized
     */
    @Test
    public void testPasswordFieldExists() throws Exception {
        Field passwordField = LoginGUI.class.getDeclaredField("passwordField");
        passwordField.setAccessible(true);
        JPasswordField field = (JPasswordField) passwordField.get(loginGUI);
        assertNotNull(field, "Password field should be initialized");
    }

    /**
     * Test that login button is initialized
     */
    @Test
    public void testLoginButtonExists() throws Exception {
        Field loginButton = LoginGUI.class.getDeclaredField("loginButton");
        loginButton.setAccessible(true);
        JButton button = (JButton) loginButton.get(loginGUI);
        assertNotNull(button, "Login button should be initialized");
        assertEquals("Login", button.getText(), "Login button text should be 'Login'");
    }

    /**
     * Test that forgot password button is initialized
     */
    @Test
    public void testForgotPasswordButtonExists() throws Exception {
        Field forgotPasswordButton = LoginGUI.class.getDeclaredField("forgotPasswordButton");
        forgotPasswordButton.setAccessible(true);
        JButton button = (JButton) forgotPasswordButton.get(loginGUI);
        assertNotNull(button, "Forgot password button should be initialized");
    }

    /**
     * Test that show/hide password button is initialized
     */
    @Test
    public void testShowHideButtonExists() throws Exception {
        Field showHideButton = LoginGUI.class.getDeclaredField("showHideButton");
        showHideButton.setAccessible(true);
        JButton button = (JButton) showHideButton.get(loginGUI);
        assertNotNull(button, "Show/hide button should be initialized");
    }

    /**
     * Test that serverHost field is initialized with default value
     */
    @Test
    public void testServerHostInitialized() throws Exception {
        Field serverHostField = LoginGUI.class.getDeclaredField("serverHost");
        serverHostField.setAccessible(true);
        String serverHost = (String) serverHostField.get(loginGUI);
        assertNotNull(serverHost, "Server host should be initialized");
    }

    /**
     * Test that serverPort field is initialized with default value
     */
    @Test
    public void testServerPortInitialized() throws Exception {
        Field serverPortField = LoginGUI.class.getDeclaredField("serverPort");
        serverPortField.setAccessible(true);
        int serverPort = (int) serverPortField.get(loginGUI);
        assertTrue(serverPort > 0, "Server port should be a positive number");
    }

    /**
     * Test that the frame is not visible by default (until setVisible is called)
     */
    @Test
    public void testFrameIsVisibleAfterConstruction() {
        assertTrue(loginGUI.isVisible(),
                    "Frame should be visible after construction since the user clicked to open it and needs to interact with it");
    }

    /**
     * Test that content pane is not null
     */
    @Test
    public void testContentPaneExists() {
        Container contentPane = loginGUI.getContentPane();
        assertNotNull(contentPane, "Content pane should exist");
    }
}
