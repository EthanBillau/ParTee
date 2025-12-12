package com.project.golf.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * EmailSender
 * 
 * Utility class for sending emails from the parteesignup@gmail.com account.
 * Uses JavaMail API to send automated emails to users requesting account access.
 *
 * @author Nikhil Kodali (kodali3), Ethan Billau (ebillau)
 */
public class EmailSender {
    
    private static final String FROM_EMAIL = "parteesignup@gmail.com";
    private static final String FROM_PASSWORD = "gpmc zpgc dekm anxd";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    /**
     * Sends an email from parteesignup@gmail.com to the specified recipient.
     * 
     * @param toEmail The recipient's email address
     * @param subject The email subject line
     * @param body The email body content
     * @return true if email was sent successfully, false otherwise
     */
    public static boolean sendEmail(String toEmail, String subject, String body) {
        /**
         * Configure SMTP server properties for Gmail.
         * Uses TLS encryption on port 587 for secure email transmission.
         */
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        /**
         * Create authenticated session with Gmail SMTP server.
         * Uses the FROM_EMAIL and FROM_PASSWORD credentials.
         */
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });
        
        try {
            /**
             * Construct the email message with sender, recipient, subject, and body.
             * Uses MimeMessage for standard email format compatibility.
             */
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            
            /**
             * Send the email message through the SMTP server.
             * This is a blocking operation that may take a few seconds.
             */
            Transport.send(message);
            
            System.out.println("Email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            /**
             * Log any errors that occur during email sending.
             * Common errors include authentication failure or network issues.
             */
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends a "Hello World" email to the specified recipient.
     * This is a convenience method for the account request feature.
     * 
     * @param toEmail The recipient's email address
     * @return true if email was sent successfully, false otherwise
     */
    public static boolean sendHelloWorldEmail(String toEmail) {
        String subject = "Welcome to Par-Tee Golf Reservation System";
        String body = "Thank you for chosing the ParTee Golf Reservation software, your account must be verified by the country club before you will be able to login and create/access your reservations. Please respond with your full name, and we will create your account and send back the details.";
        return sendEmail(toEmail, subject, body);
    }
    
    /**
     * Sends a calendar invite email with iCalendar attachment for a golf reservation.
     * Creates a .ics file compatible with most calendar applications (Gmail, Outlook, Apple Calendar).
     * 
     * @param toEmail The recipient's email address
     * @param date The reservation date in MM/dd/yyyy format
     * @param time The reservation time (e.g., "9:00 AM")
     * @param hole The hole number
     * @param partySize The number of people in the party
     * @param reservationId The unique reservation ID
     * @return true if email was sent successfully, false otherwise
     */
    public static boolean sendCalendarInvite(String toEmail, String date, String time, int hole, int partySize, String reservationId) {
        try {
            System.out.println("DEBUG: Starting to send calendar invite to: " + toEmail);
            System.out.println("DEBUG: Date: " + date + ", Time: " + time + ", Hole: " + hole);
            
            /**
             * Test internet connectivity before attempting to send.
             * Check if we can resolve the Gmail SMTP server.
             */
            try {
                java.net.InetAddress.getByName(SMTP_HOST);
                System.out.println("DEBUG: Successfully resolved " + SMTP_HOST);
            } catch (java.net.UnknownHostException e) {
                System.err.println("ERROR: Cannot reach " + SMTP_HOST + ". Check your internet connection.");
                System.err.println("Email sending requires an active internet connection.");
                return false;
            }
            
            /**
             * Parse the date and time to create a proper calendar event.
             * Convert MM/dd/yyyy to yyyyMMdd format for iCalendar.
             */
            String[] dateParts = date.split("/");
            String month = dateParts[0].length() == 1 ? "0" + dateParts[0] : dateParts[0];
            String day = dateParts[1].length() == 1 ? "0" + dateParts[1] : dateParts[1];
            String year = dateParts[2];
            String dateFormatted = year + month + day;
            System.out.println("DEBUG: Formatted date: " + dateFormatted);
            
            /**
             * Convert time to 24-hour format for iCalendar.
             * Assumes 1-hour duration for golf reservations.
             */
            String timeFormatted = convertTo24Hour(time);
            String startDateTime = dateFormatted + "T" + timeFormatted + "00";
            
            // Calculate end time (1 hour later)
            int startHour = Integer.parseInt(timeFormatted.substring(0, 2));
            int endHour = (startHour + 1) % 24;
            String endTimeFormatted = "%02d".formatted(endHour) + timeFormatted.substring(2);
            String endDateTime = dateFormatted + "T" + endTimeFormatted + "00";
            
            /**
             * Create iCalendar format (.ics) content.
             * This format is recognized by all major calendar applications.
             * Use actual \r\n line breaks, not escaped strings.
             */
            StringBuilder icsContent = new StringBuilder();
            icsContent.append("BEGIN:VCALENDAR\r\n");
            icsContent.append("VERSION:2.0\r\n");
            icsContent.append("PRODID:-//Par-Tee Golf//Reservation System//EN\r\n");
            icsContent.append("METHOD:REQUEST\r\n");
            icsContent.append("BEGIN:VEVENT\r\n");
            icsContent.append("UID:").append(reservationId).append("@parteegolf.com\r\n");
            icsContent.append("DTSTAMP:").append(getCurrentTimestamp()).append("\r\n");
            icsContent.append("DTSTART:").append(startDateTime).append("\r\n");
            icsContent.append("DTEND:").append(endDateTime).append("\r\n");
            icsContent.append("SUMMARY:Golf Reservation - Hole ").append(hole).append("\r\n");
            icsContent.append("DESCRIPTION:Golf course reservation for ").append(partySize)
                       .append(" people at Hole ").append(hole)
                       .append(".\\nReservation ID: ").append(reservationId).append("\r\n");
            icsContent.append("LOCATION:Par-Tee Golf Course - Hole ").append(hole).append("\r\n");
            icsContent.append("STATUS:CONFIRMED\r\n");
            icsContent.append("SEQUENCE:0\r\n");
            icsContent.append("END:VEVENT\r\n");
            icsContent.append("END:VCALENDAR\r\n");
            
            /**
             * Create email with calendar attachment.
             * Uses multipart message to include both text and calendar data.
             */
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Golf Reservation - " + date + " at " + time);
            
            /**
             * Create multipart message with text body and calendar attachment.
             * Use actual newlines in text, not escaped strings.
             */
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Your golf reservation has been confirmed!\n\n" +
                           "Date: " + date + "\n" +
                           "Time: " + time + "\n" +
                           "Hole: " + hole + "\n" +
                           "Party Size: " + partySize + "\n" +
                           "Reservation ID: " + reservationId + "\n\n" +
                           "A calendar invite is attached to this email.");
            
            MimeBodyPart calendarPart = new MimeBodyPart();
            calendarPart.setContent(icsContent.toString(), "text/calendar;method=REQUEST");
            calendarPart.setFileName("reservation.ics");
            
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(calendarPart);
            
            message.setContent(multipart);
            
            Transport.send(message);
            System.out.println("Calendar invite sent successfully to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send calendar invite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Converts 12-hour time format to 24-hour format.
     * 
     * @param time Time in format "9:00 AM" or "2:30 PM"
     * @return Time in 24-hour format like "0900" or "1430"
     */
    private static String convertTo24Hour(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        String minutePart = parts[1].split(" ")[0];
        String ampm = parts[1].split(" ")[1];
        
        if (ampm.equalsIgnoreCase("PM") && hour != 12) {
            hour += 12;
        } else if (ampm.equalsIgnoreCase("AM") && hour == 12) {
            hour = 0;
        }
        
        return "%02d%s".formatted(hour, minutePart);
    }
    
    /**
     * Gets current timestamp in iCalendar format.
     * 
     * @return Timestamp string in format yyyyMMddTHHmmssZ
     */
    private static String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(new java.util.Date());
    }
}

