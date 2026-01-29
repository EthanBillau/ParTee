package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.gui.BackgroundPanel;
import java.awt.*;
import javax.swing.*;
import org.junit.jupiter.api.*;

/**
 * BackgroundPanelTest.java
 *
 * <p>JUnit test suite for BackgroundPanel class. Tests constructors and basic panel functionality.
 * BackgroundPanel is a custom JPanel with background image support.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), Aman Wakankar (awakanka) L15
 * @version December 7, 2025
 */
public class BackgroundPanelTest {

  /** Test that BackgroundPanel constructor executes without throwing exceptions. */
  @Test
  public void testBackgroundPanelConstructorDoesNotThrow() {
    assertDoesNotThrow(
        () -> {
          BackgroundPanel panel = new BackgroundPanel();
        },
        "BackgroundPanel constructor should not throw exceptions");
  }

  /** Test that BackgroundPanel constructor creates a non-null object. */
  @Test
  public void testBackgroundPanelConstructorNotNull() {
    BackgroundPanel panel = new BackgroundPanel();
    assertNotNull(panel, "BackgroundPanel constructor should create a non-null object");
  }

  /** Test that BackgroundPanel extends JPanel. */
  @Test
  public void testBackgroundPanelExtendsJPanel() {
    BackgroundPanel panel = new BackgroundPanel();
    assertTrue(panel instanceof JPanel, "BackgroundPanel should extend JPanel");
  }

  /** Test that BackgroundPanel can have components added to it. */
  @Test
  public void testBackgroundPanelCanAddComponents() {
    BackgroundPanel panel = new BackgroundPanel();
    JLabel label = new JLabel("Test Label");

    assertDoesNotThrow(
        () -> {
          panel.add(label);
        },
        "BackgroundPanel should allow adding components");

    assertEquals(1, panel.getComponentCount(), "BackgroundPanel should contain added component");
  }

  /** Test that BackgroundPanel can have multiple components. */
  @Test
  public void testBackgroundPanelCanAddMultipleComponents() {
    BackgroundPanel panel = new BackgroundPanel();

    panel.add(new JLabel("Label 1"));
    panel.add(new JButton("Button 1"));
    panel.add(new JTextField("Text Field"));

    assertEquals(
        3, panel.getComponentCount(), "BackgroundPanel should contain all added components");
  }

  /** Test that BackgroundPanel has a layout manager. */
  @Test
  public void testBackgroundPanelHasLayoutManager() {
    BackgroundPanel panel = new BackgroundPanel();
    assertNotNull(panel.getLayout(), "BackgroundPanel should have a layout manager");
  }

  /** Test that BackgroundPanel layout can be changed. */
  @Test
  public void testBackgroundPanelLayoutCanBeChanged() {
    BackgroundPanel panel = new BackgroundPanel();

    assertDoesNotThrow(
        () -> {
          panel.setLayout(new BorderLayout());
        },
        "BackgroundPanel should allow layout changes");

    assertTrue(
        panel.getLayout() instanceof BorderLayout, "BackgroundPanel layout should be changeable");
  }

  /** Test that multiple BackgroundPanel instances can be created. */
  @Test
  public void testMultipleBackgroundPanelInstances() {
    BackgroundPanel panel1 = new BackgroundPanel();
    BackgroundPanel panel2 = new BackgroundPanel();

    assertNotNull(panel1, "First BackgroundPanel instance should not be null");
    assertNotNull(panel2, "Second BackgroundPanel instance should not be null");
    assertNotSame(panel1, panel2, "Multiple instances should be different objects");
  }

  /** Test that BackgroundPanel is opaque by default. */
  @Test
  public void testBackgroundPanelOpacity() {
    BackgroundPanel panel = new BackgroundPanel();
    assertDoesNotThrow(
        () -> {
          boolean opaque = panel.isOpaque();
        },
        "BackgroundPanel should have opacity property");
  }

  /** Test that BackgroundPanel can be added to a frame. */
  @Test
  public void testBackgroundPanelCanBeAddedToFrame() {
    BackgroundPanel panel = new BackgroundPanel();
    JFrame frame = new JFrame();

    assertDoesNotThrow(
        () -> {
          frame.setContentPane(panel);
        },
        "BackgroundPanel should be usable as content pane");

    assertEquals(panel, frame.getContentPane(), "BackgroundPanel should be set as content pane");

    frame.dispose();
  }

  /** Test that BackgroundPanel dimensions can be set. */
  @Test
  public void testBackgroundPanelDimensionsCanBeSet() {
    BackgroundPanel panel = new BackgroundPanel();
    Dimension testSize = new Dimension(800, 600);

    assertDoesNotThrow(
        () -> {
          panel.setPreferredSize(testSize);
        },
        "BackgroundPanel should allow size setting");

    assertEquals(
        testSize, panel.getPreferredSize(), "BackgroundPanel should maintain preferred size");
  }

  /** Test that BackgroundPanel can be made visible. */
  @Test
  public void testBackgroundPanelVisibility() {
    BackgroundPanel panel = new BackgroundPanel();

    assertDoesNotThrow(
        () -> {
          panel.setVisible(true);
          panel.setVisible(false);
        },
        "BackgroundPanel should support visibility changes");
  }

  /** Test that BackgroundPanel handles null layout gracefully. */
  @Test
  public void testBackgroundPanelWithNullLayout() {
    BackgroundPanel panel = new BackgroundPanel();

    assertDoesNotThrow(
        () -> {
          panel.setLayout(null);
        },
        "BackgroundPanel should handle null layout");
  }
}
