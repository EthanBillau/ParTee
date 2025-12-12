package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.gui.AdminLoginGUI;
import java.awt.*;
import javax.swing.*;
import org.junit.jupiter.api.*;

/**
 * AdminLoginGUITest.java
 *
 * <p>Comprehensive JUnit test suite for AdminLoginGUI class. Tests constructors, GUI component
 * initialization, layout, components, visibility, and properties for admin authentication. Does NOT
 * test event handlers (button clicks) per project requirements.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), Nikhil Kodali (kodali3), L15
 * @version December 7, 2025
 */
public class AdminLoginGUITest {

  /** Test that AdminLoginGUI constructor executes without throwing exceptions. */
  @Test
  public void testAdminLoginGUIConstructorDoesNotThrow() {
    assertDoesNotThrow(
        () -> {
          AdminLoginGUI gui = new AdminLoginGUI();
          gui.dispose();
        },
        "AdminLoginGUI constructor should not throw exceptions");
  }

  /** Test that AdminLoginGUI constructor creates a non-null object. */
  @Test
  public void testAdminLoginGUIConstructorNotNull() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertNotNull(gui, "AdminLoginGUI constructor should create a non-null object");
    gui.dispose();
  }

  /** Test that AdminLoginGUI extends JFrame. */
  @Test
  public void testAdminLoginGUIExtendsJFrame() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertTrue(gui instanceof JFrame, "AdminLoginGUI should extend JFrame");
    gui.dispose();
  }

  /** Test that AdminLoginGUI implements ActionListener. */
  @Test
  public void testAdminLoginGUIImplementsActionListener() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertTrue(
        gui instanceof java.awt.event.ActionListener,
        "AdminLoginGUI should implement ActionListener");
    gui.dispose();
  }

  /** Test that AdminLoginGUI sets a title. */
  @Test
  public void testAdminLoginGUIHasTitle() {
    AdminLoginGUI gui = new AdminLoginGUI();
    String title = gui.getTitle();
    assertNotNull(title, "AdminLoginGUI should have a title");
    assertTrue(title.length() > 0, "AdminLoginGUI title should not be empty");
    gui.dispose();
  }

  /** Test that AdminLoginGUI sets a default close operation. */
  @Test
  public void testAdminLoginGUIHasDefaultCloseOperation() {
    AdminLoginGUI gui = new AdminLoginGUI();
    int closeOp = gui.getDefaultCloseOperation();
    assertEquals(JFrame.EXIT_ON_CLOSE, closeOp, "AdminLoginGUI should have EXIT_ON_CLOSE");
    gui.dispose();
  }

  /** Test that AdminLoginGUI has reasonable dimensions. */
  @Test
  public void testAdminLoginGUIHasReasonableSize() {
    AdminLoginGUI gui = new AdminLoginGUI();
    int width = gui.getWidth();
    int height = gui.getHeight();

    assertTrue(width > 0, "AdminLoginGUI width should be greater than 0");
    assertTrue(height > 0, "AdminLoginGUI height should be greater than 0");
    assertTrue(width <= 1920, "AdminLoginGUI width should be reasonable");
    assertTrue(height <= 1080, "AdminLoginGUI height should be reasonable");

    gui.dispose();
  }

  /** Test that AdminLoginGUI has exact expected size (600x400). */
  @Test
  public void testAdminLoginGUIHasCorrectSize() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertEquals(600, gui.getWidth(), "AdminLoginGUI width should be 600");
    assertEquals(400, gui.getHeight(), "AdminLoginGUI height should be 400");
    gui.dispose();
  }

  /** Test that AdminLoginGUI has a content pane with components. */
  @Test
  public void testAdminLoginGUIHasContentPane() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertNotNull(gui.getContentPane(), "AdminLoginGUI should have a content pane");
    assertTrue(
        gui.getContentPane().getComponentCount() > 0,
        "AdminLoginGUI content pane should have components");
    gui.dispose();
  }

  /** Test that AdminLoginGUI content pane has green background color. */
  @Test
  public void testAdminLoginGUIHasGreenBackground() {
    AdminLoginGUI gui = new AdminLoginGUI();
    Container contentPane = gui.getContentPane();
    Color backgroundColor = contentPane.getBackground();

    // Check for green color (34, 139, 34)
    assertNotNull(backgroundColor, "Content pane should have a background color");
    assertEquals(
        new Color(34, 139, 34),
        backgroundColor,
        "Content pane should have green background (34, 139, 34)");

    gui.dispose();
  }

  /** Test that AdminLoginGUI content pane uses BoxLayout. */
  @Test
  public void testAdminLoginGUIUsesBoxLayout() {
    AdminLoginGUI gui = new AdminLoginGUI();
    Container contentPane = gui.getContentPane();
    LayoutManager layout = contentPane.getLayout();

    assertTrue(layout instanceof BoxLayout, "AdminLoginGUI content pane should use BoxLayout");

    gui.dispose();
  }

  /** Test that AdminLoginGUI contains JTextField for username. */
  @Test
  public void testAdminLoginGUIHasUsernameField() {
    AdminLoginGUI gui = new AdminLoginGUI();
    boolean hasTextField = findComponentByType(gui.getContentPane(), JTextField.class);
    assertTrue(hasTextField, "AdminLoginGUI should contain a JTextField for username");
    gui.dispose();
  }

  /** Test that AdminLoginGUI contains JPasswordField for password. */
  @Test
  public void testAdminLoginGUIHasPasswordField() {
    AdminLoginGUI gui = new AdminLoginGUI();
    boolean hasPasswordField = findComponentByType(gui.getContentPane(), JPasswordField.class);
    assertTrue(hasPasswordField, "AdminLoginGUI should contain a JPasswordField for password");
    gui.dispose();
  }

  /** Test that AdminLoginGUI contains at least two JButtons (login and show/hide). */
  @Test
  public void testAdminLoginGUIHasButtons() {
    AdminLoginGUI gui = new AdminLoginGUI();
    int buttonCount = countComponentsByType(gui.getContentPane(), JButton.class);
    assertTrue(
        buttonCount >= 2, "AdminLoginGUI should have at least 2 buttons (login and show/hide)");
    gui.dispose();
  }

  /** Test that AdminLoginGUI contains JLabels for text display. */
  @Test
  public void testAdminLoginGUIHasLabels() {
    AdminLoginGUI gui = new AdminLoginGUI();
    int labelCount = countComponentsByType(gui.getContentPane(), JLabel.class);
    assertTrue(
        labelCount >= 3, "AdminLoginGUI should have at least 3 labels (title, username, password)");
    gui.dispose();
  }

  /** Test that AdminLoginGUI has a title label with proper formatting. */
  @Test
  public void testAdminLoginGUIHasTitleLabel() {
    AdminLoginGUI gui = new AdminLoginGUI();
    JLabel titleLabel = findFirstLabelByType(gui.getContentPane());

    assertNotNull(titleLabel, "AdminLoginGUI should have a title label");
    assertNotNull(titleLabel.getText(), "Title label should have text");
    assertTrue(titleLabel.getText().length() > 0, "Title label text should not be empty");

    gui.dispose();
  }

  /** Test that AdminLoginGUI has a default button (Enter key support). */
  @Test
  public void testAdminLoginGUIHasDefaultButton() {
    AdminLoginGUI gui = new AdminLoginGUI();
    JButton defaultButton = gui.getRootPane().getDefaultButton();

    assertNotNull(defaultButton, "AdminLoginGUI should have a default button for Enter key");

    gui.dispose();
  }

  /** Test that multiple AdminLoginGUI instances can be created. */
  @Test
  public void testMultipleAdminLoginGUIInstances() {
    AdminLoginGUI gui1 = new AdminLoginGUI();
    AdminLoginGUI gui2 = new AdminLoginGUI();

    assertNotNull(gui1, "First AdminLoginGUI instance should not be null");
    assertNotNull(gui2, "Second AdminLoginGUI instance should not be null");
    assertNotSame(gui1, gui2, "Multiple instances should be different objects");

    gui1.dispose();
    gui2.dispose();
  }

  /** Test that AdminLoginGUI title contains admin-related keywords. */
  @Test
  public void testAdminLoginGUITitleContainsAdminKeywords() {
    AdminLoginGUI gui = new AdminLoginGUI();
    String title = gui.getTitle().toLowerCase();

    boolean hasAdminKeyword =
        title.contains("admin") || title.contains("administrator") || title.contains("manager");

    assertTrue(hasAdminKeyword, "AdminLoginGUI title should contain admin-related keywords");
    gui.dispose();
  }

  /** Test that AdminLoginGUI title matches expected format. */
  @Test
  public void testAdminLoginGUITitleFormat() {
    AdminLoginGUI gui = new AdminLoginGUI();
    String title = gui.getTitle();

    assertEquals(
        "Admin Login - Golf Server", title, "AdminLoginGUI title should match expected format");

    gui.dispose();
  }

  /** Test that AdminLoginGUI can be disposed without errors. */
  @Test
  public void testAdminLoginGUIDispose() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertDoesNotThrow(() -> gui.dispose(), "AdminLoginGUI should dispose without exceptions");
  }

  /** Test that AdminLoginGUI is visible after construction. */
  @Test
  public void testAdminLoginGUIIsVisible() {
    AdminLoginGUI gui = new AdminLoginGUI();
    assertTrue(gui.isVisible(), "AdminLoginGUI should be visible after construction");
    gui.dispose();
  }

  // Helper methods

  /** Recursively searches for a component of the specified type. */
  private boolean findComponentByType(Container container, Class<?> type) {
    for (Component comp : container.getComponents()) {
      if (type.isInstance(comp)) {
        return true;
      }
      if (comp instanceof Container) {
        if (findComponentByType((Container) comp, type)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Recursively counts components of the specified type. */
  private int countComponentsByType(Container container, Class<?> type) {
    int count = 0;
    for (Component comp : container.getComponents()) {
      if (type.isInstance(comp)) {
        count++;
      }
      if (comp instanceof Container) {
        count += countComponentsByType((Container) comp, type);
      }
    }
    return count;
  }

  /** Finds the first JLabel in the container (typically the title). */
  private JLabel findFirstLabelByType(Container container) {
    for (Component comp : container.getComponents()) {
      if (comp instanceof JLabel) {
        return (JLabel) comp;
      }
      if (comp instanceof Container) {
        JLabel label = findFirstLabelByType((Container) comp);
        if (label != null) {
          return label;
        }
      }
    }
    return null;
  }
}
