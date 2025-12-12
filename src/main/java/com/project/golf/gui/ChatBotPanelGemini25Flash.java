package com.project.golf.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import okhttp3.*;
import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;

/**
 * Simple Swing panel that talks to Google Gemini 2.5 Flash via the Gemini API.
 *
 * NOTE: You must create a local .env file in the project root with:
 *
 *   GEMINI_API_KEY=your_real_key_here
 *
 * Do NOT commit your real API key to source control. The .env in this project
 * is intentionally left as a template.
 */
public class ChatBotPanelGemini25Flash extends JPanel {

    // Gemini HTTP endpoint for text-only generateContent
    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        // High-level behavior instructions for the model
    private static final String SYSTEM_PROMPT =
            "You are Golfee, an assistant for a golf course reservation system known as ParTee. "
          + "You help with questions about golf, tee times, memberships, reservations, "
          + "and how to use this application. "
          + "If the user asks about anything unrelated to golf or this app, "
          + "politely reply: \"I'm only able to help with golf-related questions and this app.\"";

    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final String apiKey;
    private final OkHttpClient client;

    public ChatBotPanelGemini25Flash() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0)); // fixed width on the side of the app

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Load API key from .env (but don't crash if .env is missing)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        apiKey = dotenv.get("GEMINI_API_KEY");

        client = new OkHttpClient();

        // Send on button click
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Send when user presses Enter in the text field
        inputField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        chatArea.append("You: " + userInput + "\n");
        inputField.setText("");

        // Make API call in a background thread so we don't block the UI
        new Thread(() -> {
            String responseText = callGeminiAPI(userInput);
            SwingUtilities.invokeLater(() -> chatArea.append("AI: " + responseText + "\n"));
        }).start();
    }

    private String callGeminiAPI(String userPrompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: GEMINI_API_KEY is not set. Add it to your .env file in the project root.";
        }

        try {
            // Combine system instructions + user message
            String combinedPrompt = SYSTEM_PROMPT
                    + "\n\nUser: " + userPrompt + "\nAssistant:";

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

            RequestBody body = RequestBody.create(
                    root.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_ENDPOINT)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-goog-api-key", apiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Error from API: HTTP " + response.code() + " - " + response.message();
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    return "Empty response body from API.";
                }

                String respBody = responseBody.string();
                if (respBody.isEmpty()) {
                    return "Empty response from API.";
                }

                JsonObject respJson = JsonParser.parseString(respBody).getAsJsonObject();

                if (!respJson.has("candidates")) {
                    return "No candidates in API response.";
                }

                JsonArray candidates = respJson.getAsJsonArray("candidates");
                if (candidates.size() == 0) {
                    return "No candidates in API response.";
                }

                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                if (!firstCandidate.has("content")) {
                    return "No content in first candidate.";
                }

                JsonObject content = firstCandidate.getAsJsonObject("content");
                if (!content.has("parts")) {
                    return "No parts in first candidate content.";
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
                return text.isEmpty() ? "No text in response." : text;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Failed to get response: " + ex.getMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Unexpected error: " + ex.getMessage();
        }
    }

}
