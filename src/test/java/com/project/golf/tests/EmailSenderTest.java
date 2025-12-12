package com.project.golf.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.project.golf.utils.EmailSender;
import java.lang.reflect.*;
import org.junit.jupiter.api.*;

/**
 * EmailSenderTest.java
 *
 * <p>JUnit test suite for EmailSender class. Tests email sending functionality structure and
 * methods. Note: These tests verify class structure without requiring actual SMTP configuration.
 * Actual email sending requires proper JavaMail setup and is tested manually.
 *
 * @author Anoushka Chakravarty (chakr181), Ethan Billau (ebillau), L15
 * @version December 7, 2025
 */
public class EmailSenderTest {

  /** Test that EmailSender class exists and can be referenced. */
  @Test
  public void testEmailSenderClassExists() {
    assertDoesNotThrow(
        () -> {
          Class<?> clazz = EmailSender.class;
          assertNotNull(clazz, "EmailSender class should exist");
        },
        "EmailSender class should be accessible");
  }

  /** Test that EmailSender is in the correct package. */
  @Test
  public void testEmailSenderPackage() {
    Class<?> clazz = EmailSender.class;
    String packageName = clazz.getPackage().getName();
    assertEquals(
        "com.project.golf.utils",
        packageName,
        "EmailSender should be in com.project.golf.utils package");
  }

  /** Test that EmailSender is a public class. */
  @Test
  public void testEmailSenderIsPublic() {
    Class<?> clazz = EmailSender.class;
    assertTrue(Modifier.isPublic(clazz.getModifiers()), "EmailSender should be public");
  }

  /** Test that EmailSender is not an interface. */
  @Test
  public void testEmailSenderIsNotInterface() {
    Class<?> clazz = EmailSender.class;
    assertFalse(clazz.isInterface(), "EmailSender should be a class, not an interface");
  }

  /** Test that EmailSender is not abstract. */
  @Test
  public void testEmailSenderIsNotAbstract() {
    Class<?> clazz = EmailSender.class;
    assertFalse(Modifier.isAbstract(clazz.getModifiers()), "EmailSender should not be abstract");
  }

  /** Test that EmailSender has sendEmail method. */
  @Test
  public void testEmailSenderHasSendEmailMethod() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendEmail", String.class, String.class, String.class);
          assertNotNull(method, "sendEmail method should exist");
        },
        "EmailSender should have sendEmail(String, String, String) method");
  }

  /** Test that sendEmail method is static. */
  @Test
  public void testSendEmailMethodIsStatic() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendEmail", String.class, String.class, String.class);
          assertTrue(Modifier.isStatic(method.getModifiers()), "sendEmail method should be static");
        },
        "sendEmail should be a static method");
  }

  /** Test that sendEmail method is public. */
  @Test
  public void testSendEmailMethodIsPublic() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendEmail", String.class, String.class, String.class);
          assertTrue(Modifier.isPublic(method.getModifiers()), "sendEmail method should be public");
        },
        "sendEmail should be public");
  }

  /** Test that sendEmail returns boolean. */
  @Test
  public void testSendEmailReturnsBoolean() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendEmail", String.class, String.class, String.class);
          assertEquals(boolean.class, method.getReturnType(), "sendEmail should return boolean");
        },
        "sendEmail return type should be boolean");
  }

  /** Test that EmailSender has sendHelloWorldEmail method. */
  @Test
  public void testEmailSenderHasSendHelloWorldEmailMethod() {
    assertDoesNotThrow(
        () -> {
          Method method = EmailSender.class.getDeclaredMethod("sendHelloWorldEmail", String.class);
          assertNotNull(method, "sendHelloWorldEmail method should exist");
        },
        "EmailSender should have sendHelloWorldEmail(String) method");
  }

  /** Test that sendHelloWorldEmail method is static. */
  @Test
  public void testSendHelloWorldEmailMethodIsStatic() {
    assertDoesNotThrow(
        () -> {
          Method method = EmailSender.class.getDeclaredMethod("sendHelloWorldEmail", String.class);
          assertTrue(
              Modifier.isStatic(method.getModifiers()),
              "sendHelloWorldEmail method should be static");
        },
        "sendHelloWorldEmail should be static");
  }

  /** Test that sendHelloWorldEmail method is public. */
  @Test
  public void testSendHelloWorldEmailMethodIsPublic() {
    assertDoesNotThrow(
        () -> {
          Method method = EmailSender.class.getDeclaredMethod("sendHelloWorldEmail", String.class);
          assertTrue(
              Modifier.isPublic(method.getModifiers()),
              "sendHelloWorldEmail method should be public");
        },
        "sendHelloWorldEmail should be public");
  }

  /** Test that sendHelloWorldEmail returns boolean. */
  @Test
  public void testSendHelloWorldEmailReturnsBoolean() {
    assertDoesNotThrow(
        () -> {
          Method method = EmailSender.class.getDeclaredMethod("sendHelloWorldEmail", String.class);
          assertEquals(
              boolean.class, method.getReturnType(), "sendHelloWorldEmail should return boolean");
        },
        "sendHelloWorldEmail return type should be boolean");
  }

  /** Test that EmailSender has sendCalendarInvite method. */
  @Test
  public void testEmailSenderHasSendCalendarInviteMethod() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendCalendarInvite",
                  String.class,
                  String.class,
                  String.class,
                  int.class,
                  int.class,
                  String.class);
          assertNotNull(method, "sendCalendarInvite method should exist");
        },
        "EmailSender should have sendCalendarInvite method with 6 parameters");
  }

  /** Test that sendCalendarInvite method is static. */
  @Test
  public void testSendCalendarInviteMethodIsStatic() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendCalendarInvite",
                  String.class,
                  String.class,
                  String.class,
                  int.class,
                  int.class,
                  String.class);
          assertTrue(
              Modifier.isStatic(method.getModifiers()),
              "sendCalendarInvite method should be static");
        },
        "sendCalendarInvite should be static");
  }

  /** Test that sendCalendarInvite method is public. */
  @Test
  public void testSendCalendarInviteMethodIsPublic() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendCalendarInvite",
                  String.class,
                  String.class,
                  String.class,
                  int.class,
                  int.class,
                  String.class);
          assertTrue(
              Modifier.isPublic(method.getModifiers()),
              "sendCalendarInvite method should be public");
        },
        "sendCalendarInvite should be public");
  }

  /** Test that sendCalendarInvite returns boolean. */
  @Test
  public void testSendCalendarInviteReturnsBoolean() {
    assertDoesNotThrow(
        () -> {
          Method method =
              EmailSender.class.getDeclaredMethod(
                  "sendCalendarInvite",
                  String.class,
                  String.class,
                  String.class,
                  int.class,
                  int.class,
                  String.class);
          assertEquals(
              boolean.class, method.getReturnType(), "sendCalendarInvite should return boolean");
        },
        "sendCalendarInvite return type should be boolean");
  }

  /** Test that EmailSender has at least 3 public methods. */
  @Test
  public void testEmailSenderHasMultiplePublicMethods() {
    Class<?> clazz = EmailSender.class;
    Method[] methods = clazz.getDeclaredMethods();

    int publicMethodCount = 0;
    for (Method method : methods) {
      if (Modifier.isPublic(method.getModifiers())) {
        publicMethodCount++;
      }
    }

    assertTrue(
        publicMethodCount >= 3,
        "EmailSender should have at least 3 public methods (sendEmail, sendHelloWorldEmail,"
            + " sendCalendarInvite)");
  }

  /** Test that EmailSender has private helper methods. */
  @Test
  public void testEmailSenderHasPrivateHelperMethods() {
    Class<?> clazz = EmailSender.class;
    Method[] methods = clazz.getDeclaredMethods();

    int privateMethodCount = 0;
    for (Method method : methods) {
      if (Modifier.isPrivate(method.getModifiers())) {
        privateMethodCount++;
      }
    }

    assertTrue(privateMethodCount >= 1, "EmailSender should have private helper methods");
  }

  /** Test that EmailSender class can be loaded. */
  @Test
  public void testEmailSenderClassLoading() {
    assertDoesNotThrow(
        () -> {
          ClassLoader classLoader = EmailSender.class.getClassLoader();
          Class<?> loadedClass = classLoader.loadClass("com.project.golf.utils.EmailSender");
          assertNotNull(loadedClass, "EmailSender should load successfully");
        },
        "EmailSender class should load without errors");
  }

  /** Test that all public methods return boolean (success indicator). */
  @Test
  public void testAllPublicMethodsReturnBoolean() {
    Class<?> clazz = EmailSender.class;
    Method[] methods = clazz.getDeclaredMethods();

    boolean allPublicReturnBoolean = true;
    for (Method method : methods) {
      if (Modifier.isPublic(method.getModifiers())) {
        if (!method.getReturnType().equals(boolean.class)
            && !method.getReturnType().equals(Boolean.class)
            && !method.getReturnType().equals(void.class)) {
          allPublicReturnBoolean = false;
          break;
        }
      }
    }

    assertTrue(
        allPublicReturnBoolean, "All public EmailSender methods should return boolean or void");
  }

  /** Test that EmailSender has proper utility class structure. */
  @Test
  public void testEmailSenderUtilityClassStructure() {
    Class<?> clazz = EmailSender.class;

    // Check for static methods (utility class pattern)
    Method[] methods = clazz.getDeclaredMethods();
    int staticMethodCount = 0;
    for (Method method : methods) {
      if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
        staticMethodCount++;
      }
    }

    assertTrue(
        staticMethodCount >= 3,
        "EmailSender should have at least 3 public static methods (utility class pattern)");
  }

  /** Test that EmailSender methods accept appropriate parameters. */
  @Test
  public void testEmailSenderMethodParameters() {
    assertDoesNotThrow(
        () -> {
          // sendEmail should accept 3 Strings
          Method sendEmail =
              EmailSender.class.getDeclaredMethod(
                  "sendEmail", String.class, String.class, String.class);
          assertEquals(3, sendEmail.getParameterCount(), "sendEmail should accept 3 parameters");

          // sendHelloWorldEmail should accept 1 String
          Method sendHelloWorld =
              EmailSender.class.getDeclaredMethod("sendHelloWorldEmail", String.class);
          assertEquals(
              1,
              sendHelloWorld.getParameterCount(),
              "sendHelloWorldEmail should accept 1 parameter");

          // sendCalendarInvite should accept 6 parameters
          Method sendCalendar =
              EmailSender.class.getDeclaredMethod(
                  "sendCalendarInvite",
                  String.class,
                  String.class,
                  String.class,
                  int.class,
                  int.class,
                  String.class);
          assertEquals(
              6, sendCalendar.getParameterCount(), "sendCalendarInvite should accept 6 parameters");
        },
        "EmailSender methods should have correct parameter counts");
  }
}
