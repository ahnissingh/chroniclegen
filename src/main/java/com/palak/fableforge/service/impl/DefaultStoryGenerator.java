package com.palak.fableforge.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palak.fableforge.model.Story;
import com.palak.fableforge.service.ChatModel;
import com.palak.fableforge.service.StoryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Model-independent implementation of StoryGenerator.
 * Orchestrates formatting requirements and schema configuration for general ChatModels.
 */
public class DefaultStoryGenerator implements StoryGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStoryGenerator.class);

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public DefaultStoryGenerator(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public Story generateStory(String prompt) {
        String systemPrompt = "You are a master storyteller. Generate a short fable with a clear moral. Your response must follow the strict JSON schema provided.";

        // Configure structured JSON schema response format
        Map<String, Object> responseFormat = Map.of(
            "type", "json_schema",
            "json_schema", Map.of(
                "name", "story_schema",
                "strict", true,
                "schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "title", Map.of("type", "string", "description", "The title of the story"),
                        "content", Map.of("type", "string", "description", "The main narrative/content of the fable"),
                        "moral", Map.of("type", "string", "description", "The lesson or moral of the fable")
                    ),
                    "required", List.of("title", "content", "moral"),
                    "additionalProperties", false
                )
            )
        );

        try {
            logger.debug("Requesting story generation from ChatModel...");
            String responseJson = chatModel.generate(systemPrompt, prompt, responseFormat);

            logger.debug("Parsing generated story content...");
            return objectMapper.readValue(responseJson, Story.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate and parse story using ChatModel", e);
        }
    }
}
