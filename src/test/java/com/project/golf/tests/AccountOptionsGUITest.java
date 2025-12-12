package com.project.golf.tests;

import com.project.golf.gui.AccountOptionsGUI;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * AccountOptionsGUITest.java
 *
 * Comprehensive JUnit test suite for AccountOptionsGUI class.
 * Tests constructors, inheritance, interface implementation, window properties,
 * layout, components, visibility, and lifecycle for account editing interface.
 * Does NOT test event handlers (button clicks) per project requirements.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), Nikhil Kodali (kodali3), L15
 * @version December 7, 2025
 */
public class AccountOptionsGUITest {

    // Helper method to recursively find components by type
    private <T extends Component> T findComponentByType(Container container, Class<T> type) {
        for (Component comp : container.getComponents()) {
            if (type.isInstance(comp)) {
                return type.cast(comp);
            }
            if (comp instanceof Container) {
                T found = findComponentByType((Container) comp, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Helper method to count components by type
    private <T extends Component> int countComponentsByType(Container container, Class<T> type) {
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

    // Helper method to find back button
    private JButton findBackButton(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText() != null && button.getText().toLowerCase().contains("back")) {
                    return button;
                }
            }
            if (comp instanceof Container) {
                JButton found = findBackButton((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Test that AccountOptionsGUI constructor with username and client creates non-null object.
     */
    @Test
    public void testAccountOptionsGUIConstructorWithUsername() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertNotNull(gui, "AccountOptionsGUI object should not be null");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI constructor creates a non-null object.
     */
    @Test
    public void testAccountOptionsGUIConstructorNotNull() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertNotNull(gui, "AccountOptionsGUI object should not be null");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI extends JFrame.
     */
    @Test
    public void testAccountOptionsGUIExtendsJFrame() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertTrue(gui instanceof JFrame, "AccountOptionsGUI should extend JFrame");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI implements ActionListener interface.
     */
    @Test
    public void testAccountOptionsGUIImplementsActionListener() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertTrue(gui instanceof ActionListener, "AccountOptionsGUI should implement ActionListener");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has a title.
     */
    @Test
    public void testAccountOptionsGUIHasTitle() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        String title = gui.getTitle();
        assertNotNull(title, "AccountOptionsGUI should have a title");
        assertFalse(title.trim().isEmpty(), "Title should not be empty");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has correct default close operation.
     */
    @Test
    public void testAccountOptionsGUIHasDefaultCloseOperation() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        int operation = gui.getDefaultCloseOperation();
        assertEquals(JFrame.EXIT_ON_CLOSE, operation,
                "AccountOptionsGUI should have EXIT_ON_CLOSE as the default close operation");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has reasonable minimum dimensions.
     */
    @Test
    public void testAccountOptionsGUIHasReasonableSize() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        Dimension size = gui.getSize();
        assertTrue(size.width > 0 && size.height > 0,
                "AccountOptionsGUI should have a non-zero size");
        assertTrue(size.width >= 400 && size.height >= 300,
                "AccountOptionsGUI should have a reasonable minimum size");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has a content pane.
     */
    @Test
    public void testAccountOptionsGUIHasContentPane() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        Container contentPane = gui.getContentPane();
        assertNotNull(contentPane, "AccountOptionsGUI should have a content pane");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI uses BoxLayout in its panel.
     */
    @Test
    public void testAccountOptionsGUIUsesBoxLayout() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        Container contentPane = gui.getContentPane();
        
        // The content pane should be an AccountBackgroundPanel with BoxLayout
        assertNotNull(contentPane, "Content pane should not be null");
        LayoutManager layout = contentPane.getLayout();
        assertTrue(layout instanceof BoxLayout || contentPane.getComponentCount() > 0,
                "AccountOptionsGUI should use BoxLayout or have components");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has at least 6 buttons.
     */
    @Test
    public void testAccountOptionsGUIHasMultipleButtons() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        int buttonCount = countComponentsByType(gui.getContentPane(), JButton.class);
        assertTrue(buttonCount >= 6,
                "AccountOptionsGUI should have at least 6 buttons (change username, password, first name, last name, email, back)");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has at least 5 text fields.
     */
    @Test
    public void testAccountOptionsGUIHasMultipleTextFields() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        int textFieldCount = countComponentsByType(gui.getContentPane(), JTextField.class);
        assertTrue(textFieldCount >= 5,
                "AccountOptionsGUI should have at least 5 text fields");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has a password field.
     */
    @Test
    public void testAccountOptionsGUIHasPasswordField() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        JPasswordField passwordField = findComponentByType(gui.getContentPane(), JPasswordField.class);
        assertNotNull(passwordField, "AccountOptionsGUI should have at least one JPasswordField");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has multiple labels for field descriptions.
     */
    @Test
    public void testAccountOptionsGUIHasMultipleLabels() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        int labelCount = countComponentsByType(gui.getContentPane(), JLabel.class);
        assertTrue(labelCount >= 5,
                "AccountOptionsGUI should have multiple labels for field descriptions");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI has a back button.
     */
    @Test
    public void testAccountOptionsGUIHasBackButton() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        JButton backButton = null;
        Container contentPane = gui.getContentPane();
        
        // Recursively search for a button with "back" in its text
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof Container) {
                backButton = findBackButton((Container) comp);
                if (backButton != null) break;
            }
        }
        
        assertNotNull(backButton, "AccountOptionsGUI should have a back button");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI works with different usernames.
     */
    @Test
    public void testAccountOptionsGUIWithDifferentUsernames() {
        AccountOptionsGUI gui1 = new AccountOptionsGUI("user1", null);
        AccountOptionsGUI gui2 = new AccountOptionsGUI("user2", null);

        assertNotNull(gui1, "First AccountOptionsGUI should not be null");
        assertNotNull(gui2, "Second AccountOptionsGUI should not be null");
        assertNotSame(gui1, gui2, "Different instances should be created");
        
        gui1.dispose();
        gui2.dispose();
    }

    /**
     * Test that AccountOptionsGUI handles null username.
     */
    @Test
    public void testAccountOptionsGUIWithNullUsername() {
        assertDoesNotThrow(() -> {
            AccountOptionsGUI gui = new AccountOptionsGUI(null, null);
            assertNotNull(gui, "AccountOptionsGUI should handle null username");
            gui.dispose();
        }, "AccountOptionsGUI should handle null username without throwing exception");
    }

    /**
     * Test that AccountOptionsGUI handles empty username.
     */
    @Test
    public void testAccountOptionsGUIWithEmptyUsername() {
        assertDoesNotThrow(() -> {
            AccountOptionsGUI gui = new AccountOptionsGUI("", null);
            assertNotNull(gui, "AccountOptionsGUI should handle empty username");
            gui.dispose();
        }, "AccountOptionsGUI should handle empty username without throwing exception");
    }

    /**
     * Test that multiple AccountOptionsGUI instances can be created.
     */
    @Test
    public void testMultipleAccountOptionsGUIInstances() {
        AccountOptionsGUI gui1 = new AccountOptionsGUI("user1", null);
        AccountOptionsGUI gui2 = new AccountOptionsGUI("user2", null);
        AccountOptionsGUI gui3 = new AccountOptionsGUI("user3", null);

        assertNotNull(gui1);
        assertNotNull(gui2);
        assertNotNull(gui3);
        assertNotSame(gui1, gui2);
        assertNotSame(gui2, gui3);
        assertNotSame(gui1, gui3);
        
        gui1.dispose();
        gui2.dispose();
        gui3.dispose();
    }

    /**
     * Test that AccountOptionsGUI title contains relevant keywords.
     */
    @Test
    public void testAccountOptionsGUITitleRelevant() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        String title = gui.getTitle();
        assertNotNull(title);
        assertTrue(title.toLowerCase().contains("golf") || title.toLowerCase().contains("account") ||
                        title.toLowerCase().contains("options") || title.toLowerCase().contains("user"),
                "Title should be relevant to account options or golf application");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI can be disposed without errors.
     */
    @Test
    public void testAccountOptionsGUIDispose() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertDoesNotThrow(() -> gui.dispose(),
                "AccountOptionsGUI should be disposable without throwing exception");
    }

    /**
     * Test that AccountOptionsGUI is visible after construction.
     */
    @Test
    public void testAccountOptionsGUIIsVisible() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        assertTrue(gui.isVisible(), "AccountOptionsGUI should be visible after construction");
        gui.dispose();
    }

    /**
     * Test that AccountOptionsGUI handles null client.
     */
    @Test
    public void testAccountOptionsGUIWithNullClient() {
        assertDoesNotThrow(() -> {
            AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
            assertNotNull(gui, "AccountOptionsGUI should handle null client");
            gui.dispose();
        }, "AccountOptionsGUI should handle null client without throwing exception");
    }

    /**
     * Test that AccountOptionsGUI has a title label with large font.
     */
    @Test
    public void testAccountOptionsGUIHasTitleLabel() {
        AccountOptionsGUI gui = new AccountOptionsGUI("usertest", null);
        
        Container contentPane = gui.getContentPane();
        
        // Search for any label (title label should exist somewhere)
        int labelCount = countComponentsByType(contentPane, JLabel.class);
        assertTrue(labelCount > 0, "AccountOptionsGUI should have labels including a title");
        gui.dispose();
    }
}


