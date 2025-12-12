package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.gui.ManageReservationsGUI;
import javax.swing.*;
import org.junit.jupiter.api.*;

/**
 * ManageReservationsGUITest.java
 *
 * <p>JUnit test suite for ManageReservationsGUI class.
 *
 * <p>Tests constructors and basic GUI component initialization for reservation management.
 *
 * <p>Does NOT test event handlers (button clicks) per project requirements.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */
public class ManageReservationsGUITest {

  /** Test that ManageReservationsGUI constructor with username and null client works. */
  @Test
  public void testManageReservationsGUIConstructorBasic() {

    assertDoesNotThrow(
        () -> {
          ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

          gui.dispose();
        },
        "ManageReservationsGUI constructor should not throw exceptions");
  }

  /** Test that ManageReservationsGUI constructor creates a non-null object. */
  @Test
  public void testManageReservationsGUIConstructorNotNull() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    assertNotNull(gui, "ManageReservationsGUI should create a non-null object");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI no-arg constructor works (for compatibility). */
  @Test
  public void testManageReservationsGUINoArgConstructor() {

    assertDoesNotThrow(
        () -> {
          ManageReservationsGUI gui = new ManageReservationsGUI();

          gui.dispose();
        },
        "ManageReservationsGUI no-arg constructor should not throw exceptions");
  }

  /** Test that ManageReservationsGUI extends JFrame. */
  @Test
  public void testManageReservationsGUIExtendsJFrame() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    assertTrue(gui instanceof JFrame, "ManageReservationsGUI should extend JFrame");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI sets a title. */
  @Test
  public void testManageReservationsGUIHasTitle() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    String title = gui.getTitle();

    assertNotNull(title, "ManageReservationsGUI should have a title");

    assertTrue(title.length() > 0, "ManageReservationsGUI title should not be empty");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI sets a default close operation. */
  @Test
  public void testManageReservationsGUIHasDefaultCloseOperation() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    int closeOp = gui.getDefaultCloseOperation();

    assertTrue(
        closeOp == JFrame.EXIT_ON_CLOSE || closeOp == JFrame.DISPOSE_ON_CLOSE,
        "ManageReservationsGUI should have appropriate close operation");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI has reasonable dimensions. */
  @Test
  public void testManageReservationsGUIHasReasonableSize() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    int width = gui.getWidth();

    int height = gui.getHeight();

    assertTrue(width > 0, "ManageReservationsGUI width should be greater than 0");

    assertTrue(height > 0, "ManageReservationsGUI height should be greater than 0");

    assertTrue(width <= 1920, "ManageReservationsGUI width should be reasonable");

    assertTrue(height <= 1080, "ManageReservationsGUI height should be reasonable");

    gui.dispose();
  }

  /**
   * Test that ManageReservationsGUI has a content pane with components.
   *
   * <p>Should have table for reservations or empty state message.
   */
  @Test
  public void testManageReservationsGUIHasContentPane() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    assertNotNull(gui.getContentPane(), "ManageReservationsGUI should have a content pane");

    assertTrue(
        gui.getContentPane().getComponentCount() > 0,
        "ManageReservationsGUI content pane should have components");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI works with different usernames. */
  @Test
  public void testManageReservationsGUIWithDifferentUsernames() {

    assertDoesNotThrow(
        () -> {
          ManageReservationsGUI gui1 = new ManageReservationsGUI("user1", null);

          ManageReservationsGUI gui2 = new ManageReservationsGUI("user2", null);

          ManageReservationsGUI gui3 = new ManageReservationsGUI("admin", null);

          gui1.dispose();

          gui2.dispose();

          gui3.dispose();
        },
        "ManageReservationsGUI should work with different usernames");
  }

  /** Test that ManageReservationsGUI handles null username gracefully. */
  @Test
  public void testManageReservationsGUIWithNullUsername() {

    assertDoesNotThrow(
        () -> {
          ManageReservationsGUI gui = new ManageReservationsGUI(null, null);

          gui.dispose();
        },
        "ManageReservationsGUI should handle null username");
  }

  /** Test that ManageReservationsGUI handles empty username gracefully. */
  @Test
  public void testManageReservationsGUIWithEmptyUsername() {

    assertDoesNotThrow(
        () -> {
          ManageReservationsGUI gui = new ManageReservationsGUI("", null);

          gui.dispose();
        },
        "ManageReservationsGUI should handle empty username");
  }

  /** Test that multiple ManageReservationsGUI instances can be created. */
  @Test
  public void testMultipleManageReservationsGUIInstances() {

    ManageReservationsGUI gui1 = new ManageReservationsGUI("user1", null);

    ManageReservationsGUI gui2 = new ManageReservationsGUI("user2", null);

    assertNotNull(gui1, "First ManageReservationsGUI instance should not be null");

    assertNotNull(gui2, "Second ManageReservationsGUI instance should not be null");

    assertNotSame(gui1, gui2, "Multiple instances should be different objects");

    gui1.dispose();

    gui2.dispose();
  }

  /** Test that ManageReservationsGUI title contains relevant keywords. */
  @Test
  public void testManageReservationsGUITitleRelevant() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    String title = gui.getTitle().toLowerCase();

    boolean hasRelevantKeyword =
        title.contains("manage") || title.contains("reservation") || title.contains("my");

    assertTrue(hasRelevantKeyword, "ManageReservationsGUI title should contain relevant keywords");

    gui.dispose();
  }

  /** Test that ManageReservationsGUI can be disposed without errors. */
  @Test
  public void testManageReservationsGUIDispose() {

    ManageReservationsGUI gui = new ManageReservationsGUI("testuser", null);

    assertDoesNotThrow(
        () -> gui.dispose(), "ManageReservationsGUI should dispose without exceptions");
  }
}
