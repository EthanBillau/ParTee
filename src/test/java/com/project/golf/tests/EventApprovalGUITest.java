package com.project.golf.tests;

import com.project.golf.gui.EventApprovalGUI;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;

/**
 * EventApprovalGUITest.java
 *
 * JUnit test suite for EventApprovalGUI class.
 * Tests constructors and basic GUI component initialization for event approval.
 * Does NOT test event handlers (button clicks) per project requirements.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */
public class EventApprovalGUITest {

    /**
     * Test that EventApprovalGUI constructor executes without throwing exceptions.
     */
    @Test
    public void testEventApprovalGUIConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> {
            JFrame parent = new JFrame();
            EventApprovalGUI gui = new EventApprovalGUI(parent);
            gui.dispose();
            parent.dispose();
        }, "EventApprovalGUI constructor should not throw exceptions");
    }

    /**
     * Test that EventApprovalGUI constructor creates a non-null object.
     */
    @Test
    public void testEventApprovalGUIConstructorNotNull() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        assertNotNull(gui, "EventApprovalGUI constructor should create a non-null object");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI extends JDialog.
     */
    @Test
    public void testEventApprovalGUIExtendsJDialog() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        assertTrue(gui instanceof JDialog, "EventApprovalGUI should extend JDialog");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI sets a title.
     */
    @Test
    public void testEventApprovalGUIHasTitle() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        String title = gui.getTitle();
        assertNotNull(title, "EventApprovalGUI should have a title");
        assertTrue(title.length() > 0, "EventApprovalGUI title should not be empty");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI is modal.
     * Modal dialogs block interaction with parent window.
     */
    @Test
    public void testEventApprovalGUIIsModal() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        assertTrue(gui.isModal(), "EventApprovalGUI should be a modal dialog");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI has reasonable dimensions.
     */
    @Test
    public void testEventApprovalGUIHasReasonableSize() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        int width = gui.getWidth();
        int height = gui.getHeight();
        
        assertTrue(width > 0, "EventApprovalGUI width should be greater than 0");
        assertTrue(height > 0, "EventApprovalGUI height should be greater than 0");
        assertTrue(width <= 1920, "EventApprovalGUI width should be reasonable");
        assertTrue(height <= 1080, "EventApprovalGUI height should be reasonable");
        
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI has a content pane with components.
     * Should have list/table of pending events and approve/reject buttons.
     */
    @Test
    public void testEventApprovalGUIHasContentPane() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        assertNotNull(gui.getContentPane(), "EventApprovalGUI should have a content pane");
        assertTrue(gui.getContentPane().getComponentCount() > 0, 
                   "EventApprovalGUI content pane should have components");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that multiple EventApprovalGUI instances can be created.
     */
    @Test
    public void testMultipleEventApprovalGUIInstances() {
        JFrame parent1 = new JFrame();
        JFrame parent2 = new JFrame();
        EventApprovalGUI gui1 = new EventApprovalGUI(parent1);
        EventApprovalGUI gui2 = new EventApprovalGUI(parent2);
        
        assertNotNull(gui1, "First EventApprovalGUI instance should not be null");
        assertNotNull(gui2, "Second EventApprovalGUI instance should not be null");
        assertNotSame(gui1, gui2, "Multiple instances should be different objects");
        
        gui1.dispose();
        gui2.dispose();
        parent1.dispose();
        parent2.dispose();
    }

    /**
     * Test that EventApprovalGUI title contains event-related keywords.
     */
    @Test
    public void testEventApprovalGUITitleContainsEventKeywords() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        String title = gui.getTitle().toLowerCase();
        
        boolean hasEventKeyword = title.contains("event") || 
                                  title.contains("approval") ||
                                  title.contains("pending") ||
                                  title.contains("request");
        
        assertTrue(hasEventKeyword, 
                  "EventApprovalGUI title should contain event-related keywords");
        gui.dispose();
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI can be disposed without errors.
     */
    @Test
    public void testEventApprovalGUIDispose() {
        JFrame parent = new JFrame();
        EventApprovalGUI gui = new EventApprovalGUI(parent);
        assertDoesNotThrow(() -> gui.dispose(), 
                          "EventApprovalGUI should dispose without exceptions");
        parent.dispose();
    }

    /**
     * Test that EventApprovalGUI handles database initialization.
     * This tests that the GUI initializes correctly even with empty event list.
     */
    @Test
    public void testEventApprovalGUIHandlesEmptyEventList() {
        assertDoesNotThrow(() -> {
            JFrame parent = new JFrame();
            EventApprovalGUI gui = new EventApprovalGUI(parent);
            // Just verify it doesn't crash when there might be no events
            assertNotNull(gui.getContentPane());
            gui.dispose();
            parent.dispose();
        }, "EventApprovalGUI should handle empty event list");
    }
}
