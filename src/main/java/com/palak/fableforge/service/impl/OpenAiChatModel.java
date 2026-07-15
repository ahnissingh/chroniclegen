package com.palak.fableforge.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palak.fableforge.service.ChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatModel implementation for calling OpenAI chat completion API.
 */
public class OpenAiChatModel implements ChatModel {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiChatModel.class);
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAiChatModel() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set. Please set it before running the application.");
        }
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generate(String systemPrompt, String userPrompt, Map<String, Object> responseFormat) {
        try {
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("model", "gpt-4o-mini");
            requestPayload.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
            ));

            if (responseFormat != null && !responseFormat.isEmpty()) {
                requestPayload.put("response_format", responseFormat);
            }

            String requestBody = objectMapper.writeValueAsString(requestPayload);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            logger.debug("Executing request to OpenAI ChatModel...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("OpenAI ChatModel API call failed with status code " + response.statusCode() + ": " + response.body());
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            String responseText = rootNode
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();

            if (responseText.isBlank()) {
                throw new RuntimeException("Empty response received from OpenAI: " + response.body());
            }

            return responseText;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing OpenAI ChatModel call", e);
        }
    }
}
