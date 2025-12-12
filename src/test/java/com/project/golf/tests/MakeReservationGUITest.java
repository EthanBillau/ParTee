package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.client.Client;
import com.project.golf.gui.MakeReservationGUI;
import com.project.golf.reservation.Reservations;
import java.awt.*;
import java.lang.reflect.Field;
import javax.swing.*;
import org.junit.jupiter.api.*;

/**
 * MakeReservationGUITest.java
 *
 * <p>Unit test suite for MakeReservationGUI constructor and component initialization. Tests
 * date/time selection, hole selection, party size, cart and event options.
 *
 * <p>Data structures: MakeReservationGUI instance, various Swing components for date/time, hole
 * grid, checkboxes, combo boxes, and navigation buttons. Algorithm: JUnit 5 with reflection-based
 * component verification for both new reservation mode and edit reservation mode. Features:
 * Constructor validation (new and edit modes), component initialization, frame properties, date
 * field, time button, hole buttons array, party size combo, cart checkbox, event checkbox,
 * continue/back buttons per project requirements.
 *
 * @author Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */
public class MakeReservationGUITest {

  private MakeReservationGUI makeReservationGUI;
  private static final String TEST_USERNAME = "testuser";

  @BeforeEach
  public void setUp() {
    // Create MakeReservationGUI instance for testing with test username and null client
    makeReservationGUI = new MakeReservationGUI(TEST_USERNAME, null);
  }

  @AfterEach
  public void tearDown() {
    if (makeReservationGUI != null) {
      makeReservationGUI.dispose();
    }
  }

  /** Test that MakeReservationGUI constructor initializes without errors */
  @Test
  public void testConstructor() {
    assertNotNull(makeReservationGUI, "MakeReservationGUI should be instantiated");
  }

  /** Test that the frame is properly configured for new reservations */
  @Test
  public void testFramePropertiesNewReservation() {
    assertEquals(
        "Make Reservation",
        makeReservationGUI.getTitle(),
        "Frame title should be 'Make Reservation' for new bookings");
    assertEquals(
        JFrame.DISPOSE_ON_CLOSE,
        makeReservationGUI.getDefaultCloseOperation(),
        "Default close operation should be DISPOSE_ON_CLOSE");
    Dimension size = makeReservationGUI.getSize();
    assertEquals(720, size.width, "Frame width should be 720");
    assertEquals(700, size.height, "Frame height should be 700");
  }

  /** Test constructor for edit mode */
  @Test
  public void testConstructorEditMode() {
    Reservations mockReservation =
        new Reservations(
            "TEST-001", TEST_USERNAME, "2024-12-15", "10:00 AM", 4, "1", 50.0, false, false);
    MakeReservationGUI editGUI = new MakeReservationGUI(TEST_USERNAME, null, mockReservation);
    assertNotNull(editGUI, "MakeReservationGUI in edit mode should be instantiated");
    assertEquals(
        "Edit Reservation",
        editGUI.getTitle(),
        "Frame title should be 'Edit Reservation' when editing");
    editGUI.dispose();
  }

  /** Test that no-arg constructor works */
  @Test
  public void testNoArgConstructor() {
    MakeReservationGUI noArgGUI = new MakeReservationGUI();
    assertNotNull(noArgGUI, "No-arg constructor should create instance");
    noArgGUI.dispose();
  }

  /** Test that date field is initialized */
  @Test
  public void testDateFieldExists() throws Exception {
    Field dateField = MakeReservationGUI.class.getDeclaredField("dateField");
    dateField.setAccessible(true);
    JTextField field = (JTextField) dateField.get(makeReservationGUI);
    assertNotNull(field, "Date field should be initialized");
  }

  /** Test that calendar button is initialized */
  @Test
  public void testCalendarButtonExists() throws Exception {
    Field calendarButton = MakeReservationGUI.class.getDeclaredField("calendarButton");
    calendarButton.setAccessible(true);
    JButton button = (JButton) calendarButton.get(makeReservationGUI);
    assertNotNull(button, "Calendar button should be initialized");
  }

  /** Test that time button is initialized */
  @Test
  public void testTimeButtonExists() throws Exception {
    Field timeButton = MakeReservationGUI.class.getDeclaredField("timeButton");
    timeButton.setAccessible(true);
    JButton button = (JButton) timeButton.get(makeReservationGUI);
    assertNotNull(button, "Time button should be initialized");
  }

  /** Test that hole buttons array is initialized */
  @Test
  public void testHoleButtonsExists() throws Exception {
    Field holeButtons = MakeReservationGUI.class.getDeclaredField("holeButtons");
    holeButtons.setAccessible(true);
    JButton[] buttons = (JButton[]) holeButtons.get(makeReservationGUI);
    assertNotNull(buttons, "Hole buttons array should be initialized");
    assertTrue(buttons.length > 0, "Hole buttons array should have buttons");
  }

  /** Test that continue button is initialized */
  @Test
  public void testContinueButtonExists() throws Exception {
    Field continueButton = MakeReservationGUI.class.getDeclaredField("continueButton");
    continueButton.setAccessible(true);
    JButton button = (JButton) continueButton.get(makeReservationGUI);
    assertNotNull(button, "Continue button should be initialized");
  }

  /** Test that back button is initialized */
  @Test
  public void testBackButtonExists() throws Exception {
    Field backButton = MakeReservationGUI.class.getDeclaredField("backButton");
    backButton.setAccessible(true);
    JButton button = (JButton) backButton.get(makeReservationGUI);
    assertNotNull(button, "Back button should be initialized");
  }

  /** Test that map button is initialized */
  @Test
  public void testMapButtonExists() throws Exception {
    Field mapButton = MakeReservationGUI.class.getDeclaredField("mapButton");
    mapButton.setAccessible(true);
    JButton button = (JButton) mapButton.get(makeReservationGUI);
    assertNotNull(button, "Map button should be initialized");
  }

  /** Test that party size combo box is initialized */
  @Test
  public void testPartySizeComboExists() throws Exception {
    Field partySizeCombo = MakeReservationGUI.class.getDeclaredField("partySizeCombo");
    partySizeCombo.setAccessible(true);
    JComboBox<String> combo = (JComboBox<String>) partySizeCombo.get(makeReservationGUI);
    assertNotNull(combo, "Party size combo box should be initialized");
    assertTrue(combo.getItemCount() > 0, "Party size combo box should have items");
  }

  /** Test that cart checkbox is initialized */
  @Test
  public void testCartCheckBoxExists() throws Exception {
    Field cartCheckBox = MakeReservationGUI.class.getDeclaredField("cartCheckBox");
    cartCheckBox.setAccessible(true);
    JCheckBox checkBox = (JCheckBox) cartCheckBox.get(makeReservationGUI);
    assertNotNull(checkBox, "Cart checkbox should be initialized");
  }

  /** Test that event checkbox is initialized */
  @Test
  public void testEventCheckBoxExists() throws Exception {
    Field eventCheckBox = MakeReservationGUI.class.getDeclaredField("eventCheckBox");
    eventCheckBox.setAccessible(true);
    JCheckBox checkBox = (JCheckBox) eventCheckBox.get(makeReservationGUI);
    assertNotNull(checkBox, "Event checkbox should be initialized");
  }

  /** Test that event options panel is initialized */
  @Test
  public void testEventOptionsPanelExists() throws Exception {
    Field eventOptionsPanel = MakeReservationGUI.class.getDeclaredField("eventOptionsPanel");
    eventOptionsPanel.setAccessible(true);
    JPanel panel = (JPanel) eventOptionsPanel.get(makeReservationGUI);
    assertNotNull(panel, "Event options panel should be initialized");
  }

  /** Test that event hours combo box is initialized */
  @Test
  public void testEventHoursComboExists() throws Exception {
    Field eventHoursCombo = MakeReservationGUI.class.getDeclaredField("eventHoursCombo");
    eventHoursCombo.setAccessible(true);
    JComboBox<String> combo = (JComboBox<String>) eventHoursCombo.get(makeReservationGUI);
    assertNotNull(combo, "Event hours combo box should be initialized");
  }

  /** Test that hole selection label is initialized */
  @Test
  public void testHoleSelectionLabelExists() throws Exception {
    Field holeSelectionLabel = MakeReservationGUI.class.getDeclaredField("holeSelectionLabel");
    holeSelectionLabel.setAccessible(true);
    JLabel label = (JLabel) holeSelectionLabel.get(makeReservationGUI);
    assertNotNull(label, "Hole selection label should be initialized");
  }

  /** Test that hole grid panel is initialized */
  @Test
  public void testHoleGridPanelExists() throws Exception {
    Field holeGridPanel = MakeReservationGUI.class.getDeclaredField("holeGridPanel");
    holeGridPanel.setAccessible(true);
    JPanel panel = (JPanel) holeGridPanel.get(makeReservationGUI);
    assertNotNull(panel, "Hole grid panel should be initialized");
  }

  /** Test that party size panel is initialized */
  @Test
  public void testPartySizePanelExists() throws Exception {
    Field partySizePanel = MakeReservationGUI.class.getDeclaredField("partySizePanel");
    partySizePanel.setAccessible(true);
    JPanel panel = (JPanel) partySizePanel.get(makeReservationGUI);
    assertNotNull(panel, "Party size panel should be initialized");
  }

  /** Test that calendar invite checkbox is initialized */
  @Test
  public void testCalendarInviteCheckBoxExists() throws Exception {
    Field calendarInviteCheckBox =
        MakeReservationGUI.class.getDeclaredField("calendarInviteCheckBox");
    calendarInviteCheckBox.setAccessible(true);
    JCheckBox checkBox = (JCheckBox) calendarInviteCheckBox.get(makeReservationGUI);
    assertNotNull(checkBox, "Calendar invite checkbox should be initialized");
  }

  /** Test that selected hole is initialized to -1 */
  @Test
  public void testSelectedHoleInitialized() throws Exception {
    Field selectedHole = MakeReservationGUI.class.getDeclaredField("selectedHole");
    selectedHole.setAccessible(true);
    int hole = (int) selectedHole.get(makeReservationGUI);
    assertEquals(-1, hole, "Selected hole should be initialized to -1");
  }

  /** Test that selected time is initialized */
  @Test
  public void testSelectedTimeInitialized() throws Exception {
    Field selectedTime = MakeReservationGUI.class.getDeclaredField("selectedTime");
    selectedTime.setAccessible(true);
    String time = (String) selectedTime.get(makeReservationGUI);
    assertNotNull(time, "Selected time should be initialized");
    assertEquals("9:00 AM", time, "Default selected time should be 9:00 AM");
  }

  /** Test that username is stored correctly */
  @Test
  public void testUsernameStored() throws Exception {
    Field usernameField = MakeReservationGUI.class.getDeclaredField("username");
    usernameField.setAccessible(true);
    String username = (String) usernameField.get(makeReservationGUI);
    assertEquals(TEST_USERNAME, username, "Username should be stored correctly");
  }

  /** Test that client field is initialized (can be null) */
  @Test
  public void testClientFieldExists() throws Exception {
    Field clientField = MakeReservationGUI.class.getDeclaredField("client");
    clientField.setAccessible(true);
    Client client = (Client) clientField.get(makeReservationGUI);
    // Client can be null in local mode, just verify field exists
    assertTrue(true, "Client field should exist (value can be null)");
  }

  /** Test that editing reservation field is initialized correctly */
  @Test
  public void testEditingReservationField() throws Exception {
    Field editingReservationField = MakeReservationGUI.class.getDeclaredField("editingReservation");
    editingReservationField.setAccessible(true);
    Reservations editingReservation =
        (Reservations) editingReservationField.get(makeReservationGUI);
    assertNull(editingReservation, "Editing reservation should be null for new reservations");
  }

  /** Test that the frame becomes visible after construction */
  @Test
  public void testFrameIsVisibleAfterConstruction() {
    assertTrue(
        makeReservationGUI.isVisible(),
        "Frame should be visible after construction since the user clicked to open it and needs to"
            + " interact with it");
  }

  /** Test that content pane is not null */
  @Test
  public void testContentPaneExists() {
    Container contentPane = makeReservationGUI.getContentPane();
    assertNotNull(contentPane, "Content pane should exist");
  }
}
