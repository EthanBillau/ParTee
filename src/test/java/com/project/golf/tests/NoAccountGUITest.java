package com.project.golf.tests;

import com.project.golf.gui.NoAccountGUI;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

/**
 *
 * NoAccountGUITest.java
 *
 *
 *
 * JUnit test suite for NoAccountGUI class.
 *
 * Tests constructors and basic GUI component initialization for user
 * registration.
 *
 * Does NOT test event handlers (button clicks) per project requirements.
 *
 *
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 *
 */
public class NoAccountGUITest {

    /**
     *
     * Test that NoAccountGUI constructor executes without throwing exceptions.
     *
     */
    @Test

    public void testNoAccountGUIConstructorDoesNotThrow() {

        assertDoesNotThrow(() -> {

            NoAccountGUI gui = new NoAccountGUI();

            gui.dispose();

        }, "NoAccountGUI constructor should not throw exceptions");

    }

    /**
     *
     * Test that NoAccountGUI constructor creates a non-null object.
     *
     */
    @Test

    public void testNoAccountGUIConstructorNotNull() {

        NoAccountGUI gui = new NoAccountGUI();

        assertNotNull(gui, "NoAccountGUI constructor should create a non-null object");

        gui.dispose();

    }

    /**
     *
     * Test that NoAccountGUI extends JFrame.
     *
     */
    @Test

    public void testNoAccountGUIExtendsJFrame() {

        NoAccountGUI gui = new NoAccountGUI();

        assertTrue(gui instanceof JFrame, "NoAccountGUI should extend JFrame");

        gui.dispose();

    }

    /**
     *
     * Test that NoAccountGUI sets a title.
     *
     */
    @Test

    public void testNoAccountGUIHasTitle() {

        NoAccountGUI gui = new NoAccountGUI();

        String title = gui.getTitle();

        assertNotNull(title, "NoAccountGUI should have a title");

        assertTrue(title.length() > 0, "NoAccountGUI title should not be empty");

        gui.dispose();

    }

    /**
     *
     * Test that NoAccountGUI sets a default close operation.
     *
     */
    @Test

    public void testNoAccountGUIHasDefaultCloseOperation() {

        NoAccountGUI gui = new NoAccountGUI();

        int closeOp = gui.getDefaultCloseOperation();

        assertTrue(closeOp == JFrame.EXIT_ON_CLOSE || closeOp == JFrame.DISPOSE_ON_CLOSE,
                "NoAccountGUI should have appropriate close operation");

        gui.dispose();

    }

    /**
     *
     * Test that NoAccountGUI has reasonable dimensions.
     *
     */
    @Test

    public void testNoAccountGUIHasReasonableSize() {

        NoAccountGUI gui = new NoAccountGUI();

        int width = gui.getWidth();

        int height = gui.getHeight();

        assertTrue(width > 0, "NoAccountGUI width should be greater than 0");

        assertTrue(height > 0, "NoAccountGUI height should be greater than 0");

        assertTrue(width <= 1920, "NoAccountGUI width should be reasonable");

        assertTrue(height <= 1080, "NoAccountGUI height should be reasonable");

        gui.dispose();

    }

    /**
     *
     * Test that NoAccountGUI has a content pane with components.
     *
     * Should have input fields for username, password, name, email, etc.
     *
     */
    @Test

    public void testNoAccountGUIHasContentPane() {

        NoAccountGUI gui = new NoAccountGUI();

        assertNotNull(gui.getContentPane(), "NoAccountGUI should have a content pane");

        assertTrue(gui.getContentPane().getComponentCount() > 0,
                "NoAccountGUI content pane should have components (input fields, buttons)");

        gui.dispose();

    }

    /**
     *
     * Test that multiple NoAccountGUI instances can be created.
     *
     */
    @Test

    public void testMultipleNoAccountGUIInstances() {

        NoAccountGUI gui1 = new NoAccountGUI();

        NoAccountGUI gui2 = new NoAccountGUI();

        assertNotNull(gui1, "First NoAccountGUI instance should not be null");

        assertNotNull(gui2, "Second NoAccountGUI instance should not be null");

        assertNotSame(gui1, gui2, "Multiple NoAccountGUI instances should be different objects");

        gui1.dispose();

        gui2.dispose();

    }

    /**
     *
     * Test that NoAccountGUI can be disposed without errors.
     *
     */
    @Test

    public void testNoAccountGUIDispose() {

        NoAccountGUI gui = new NoAccountGUI();

        assertDoesNotThrow(() -> gui.dispose(),
                "NoAccountGUI should dispose without throwing exceptions");

    }

    /**
     *
     * Test that NoAccountGUI title contains relevant keywords.
     *
     */
    @Test

    public void testNoAccountGUIHasCorrectTitle() {

        NoAccountGUI gui = new NoAccountGUI();

        String title = gui.getTitle();
        
        boolean hasRelevantKeyword = title.contains("Golf Course Reservation System");

        assertTrue(hasRelevantKeyword,
                "NoAccountGUI title should contain registration-related keywords");

        gui.dispose();

    }

}
