package com.project.golf.gui;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;
import okhttp3.*;

/**
 * Enhanced Swing panel that provides an AI chatbot interface powered by Google Gemini 2.5 Flash.
 * Features improved visual design with modern colors, fonts, and layout.
 *
 * <p>NOTE: You must create a local .env file in the project root with:
 *
 * <p>GEMINI_API_KEY=your_real_key_here
 *
 * <p>Do NOT commit your real API key to source control. The .env in this project is intentionally
 * left as a template.
 *
 * @author Ethan Billau, Connor Landzettel
 * @version December 12, 2025
 */
public class ChatBotPanelGemini25Flash extends JPanel {

  // Gemini HTTP endpoint for text-only generateContent
  private static final String GEMINI_ENDPOINT =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

  // High-level behavior instructions for the model
  private static final String SYSTEM_PROMPT =
      "You are Golfee, an assistant for a golf course reservation system known as ParTee. "
          + "You help with questions about golf, tee times, memberships, reservations, "
          + "and how to use this application. Keep responses concise and helpful. "
          + "If the user asks about anything unrelated to golf or this app, "
          + "politely reply: \"I'm only able to help with golf-related questions and this app.\"";

  // Color scheme
  private static final Color BACKGROUND_COLOR = new Color(245, 248, 250);
  private static final Color CHAT_AREA_BG = Color.WHITE;
  private static final Color USER_MESSAGE_COLOR = new Color(33, 150, 83);
  private static final Color AI_MESSAGE_COLOR = new Color(52, 73, 94);
  private static final Color BORDER_COLOR = new Color(189, 195, 199);
  private static final Color BUTTON_COLOR = new Color(46, 204, 113);
  private static final Color BUTTON_HOVER_COLOR = new Color(39, 174, 96);

  private final JTextArea chatArea;
  private final JTextField inputField;
  private final JButton sendButton;
  private final String apiKey;
  private final OkHttpClient client;

  public ChatBotPanelGemini25Flash() {
    setLayout(new BorderLayout(0, 0));
    setPreferredSize(new Dimension(350, 0)); // Slightly wider for better readability
    setBackground(BACKGROUND_COLOR);

    // Create header panel
    JPanel headerPanel = createHeaderPanel();
    add(headerPanel, BorderLayout.NORTH);

    // Configure chat area with improved styling
    chatArea = new JTextArea();
    chatArea.setEditable(false);
    chatArea.setLineWrap(true);
    chatArea.setWrapStyleWord(true);
    chatArea.setBackground(CHAT_AREA_BG);
    chatArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
    chatArea.setMargin(new Insets(12, 12, 12, 12));
    chatArea.setBorder(BorderFactory.createEmptyBorder());

    // Add welcome message
    chatArea.setText(
        "🏌️ Golfee: Hello! I'm your golf assistant.\n"
            + "Ask me about tee times, reservations,\n"
            + "or how to use ParTee.\n\n");
    chatArea.setCaretPosition(chatArea.getDocument().getLength());

    JScrollPane scrollPane = new JScrollPane(chatArea);
    scrollPane.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    scrollPane.setBackground(CHAT_AREA_BG);
    add(scrollPane, BorderLayout.CENTER);

    // Initialize input field and button before creating panel
    inputField = new JTextField();
    sendButton = new JButton("Send");

    // Create input panel with improved styling
    JPanel inputPanel = createInputPanel();
    add(inputPanel, BorderLayout.SOUTH);

    // Load API key from .env (but don't crash if .env is missing)
    Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    apiKey = dotenv.get("GEMINI_API_KEY");

    client = new OkHttpClient();

    // Send on button click
    sendButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            sendMessage();
          }
        });

    // Send when user presses Enter in the text field
    inputField.addActionListener(e -> sendMessage());
  }

  /** Creates an attractive header panel for the chatbot. */
  private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BorderLayout());
    headerPanel.setBackground(new Color(46, 204, 113));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

    JLabel titleLabel = new JLabel("⛳ Golf Assistant");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    titleLabel.setForeground(Color.WHITE);

    JLabel subtitleLabel = new JLabel("Powered by Gemini AI");
    subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
    subtitleLabel.setForeground(new Color(230, 245, 235));

    JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
    textPanel.setOpaque(false);
    textPanel.add(titleLabel);
    textPanel.add(subtitleLabel);

    headerPanel.add(textPanel, BorderLayout.WEST);
    return headerPanel;
  }

  /** Creates a styled input panel with text field and send button. */
  private JPanel createInputPanel() {
    JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
    inputPanel.setBackground(BACKGROUND_COLOR);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

    // Style the input field
    inputField.setFont(new Font("SansSerif", Font.PLAIN, 13));
    inputField.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));

    // Style the send button
    sendButton.setFont(new Font("SansSerif", Font.BOLD, 13));
    sendButton.setBackground(BUTTON_COLOR);
    sendButton.setForeground(Color.WHITE);
    sendButton.setFocusPainted(false);
    sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Add hover effect
    sendButton.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
            sendButton.setBackground(BUTTON_HOVER_COLOR);
          }

          public void mouseExited(java.awt.event.MouseEvent evt) {
            sendButton.setBackground(BUTTON_COLOR);
          }
        });

    inputPanel.add(inputField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);
    return inputPanel;
  }

  private void sendMessage() {
    String userInput = inputField.getText().trim();
    if (userInput.isEmpty()) {
      return;
    }

    // Display user message with formatting
    chatArea.append("━━━━━━━━━━━━━━━━━━━━━━\n");
    chatArea.append("👤 You: " + userInput + "\n\n");
    inputField.setText("");

    // Show "thinking" indicator
    chatArea.append("🤔 Golfee is thinking...\n");
    chatArea.setCaretPosition(chatArea.getDocument().getLength());

    // Make API call in a background thread so we don't block the UI
    new Thread(
            () -> {
              String responseText = callGeminiAPI(userInput);
              SwingUtilities.invokeLater(
                  () -> {
                    // Remove "thinking" indicator
                    String currentText = chatArea.getText();
                    int thinkingIndex = currentText.lastIndexOf("🤔 Golfee is thinking...\n");
                    if (thinkingIndex != -1) {
                      chatArea.setText(currentText.substring(0, thinkingIndex));
                    }

                    // Display AI response with formatting
                    chatArea.append("🏌️ Golfee: " + responseText + "\n\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                  });
            })
        .start();
  }

  private String callGeminiAPI(String userPrompt) {
    if (apiKey == null || apiKey.isEmpty()) {
      return "⚠️ API key not configured. Please add GEMINI_API_KEY to your .env file.";
    }

    try {
      // Combine system instructions + user message
      String combinedPrompt = SYSTEM_PROMPT + "\n\nUser: " + userPrompt + "\nAssistant:";

      // Build request JSON:
      //
      // {
      //   "contents": [{
      //     "parts": [{ "text": "combinedPrompt" }]
      //   }]
      // }
      JsonObject root = new JsonObject();

      JsonArray contents = new JsonArray();
      JsonObject userContent = new JsonObject();
      JsonArray parts = new JsonArray();
      JsonObject textPart = new JsonObject();

      // Use the combined prompt instead of just the raw user text
      textPart.addProperty("text", combinedPrompt);
      parts.add(textPart);
      userContent.add("parts", parts);
      contents.add(userContent);

      root.add("contents", contents);

      RequestBody body = RequestBody.create(root.toString(), MediaType.parse("application/json"));

      Request request =
          new Request.Builder()
              .url(GEMINI_ENDPOINT)
              .addHeader("Content-Type", "application/json")
              .addHeader("x-goog-api-key", apiKey)
              .post(body)
              .build();

      try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          return "❌ API Error: HTTP " + response.code() + " - " + response.message();
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
          return "❌ Empty response from API.";
        }

        String respBody = responseBody.string();
        if (respBody.isEmpty()) {
          return "❌ Empty response from API.";
        }

        JsonObject respJson = JsonParser.parseString(respBody).getAsJsonObject();

        if (!respJson.has("candidates")) {
          return "❌ No response candidates from API.";
        }

        JsonArray candidates = respJson.getAsJsonArray("candidates");
        if (candidates.size() == 0) {
          return "❌ No response candidates from API.";
        }

        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        if (!firstCandidate.has("content")) {
          return "❌ No content in API response.";
        }

        JsonObject content = firstCandidate.getAsJsonObject("content");
        if (!content.has("parts")) {
          return "❌ No text parts in API response.";
        }

        JsonArray partsArray = content.getAsJsonArray("parts");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partsArray.size(); i++) {
          JsonObject part = partsArray.get(i).getAsJsonObject();
          if (part.has("text")) {
            sb.append(part.get("text").getAsString());
          }
        }

        String text = sb.toString().trim();
        return text.isEmpty() ? "❌ No text in response." : text;
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      return "❌ Connection failed: " + ex.getMessage();
    } catch (Exception ex) {
      ex.printStackTrace();
      return "❌ Unexpected error: " + ex.getMessage();
    }
  }
}
