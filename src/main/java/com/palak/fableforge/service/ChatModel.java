package com.palak.fableforge.service;

import java.util.Map;

/**
 * A generic, model-independent interface representing an LLM client.
 */
public interface ChatModel {
    /**
     * Generates a text response from the model based on the system and user prompts.
     *
     * @param systemPrompt   The instructions guiding the behavior of the model.
     * @param userPrompt     The actual request from the user.
     * @param responseFormat Optional structure/format configuration for the response (e.g. JSON schema).
     * @return The model's response as a String.
     */
    String generate(String systemPrompt, String userPrompt, Map<String, Object> responseFormat);
}
