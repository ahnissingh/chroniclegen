package com.palak.fableforge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palak.fableforge.model.Story;
import com.palak.fableforge.service.*;
import com.palak.fableforge.service.impl.DefaultStoryGenerator;
import com.palak.fableforge.service.impl.LocalFileStorageService;
import com.palak.fableforge.service.impl.OpenAiChatModel;
import com.palak.fableforge.service.impl.ThymeleafHtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entry point for FableForge.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== FableForge Startup ===");

        // 1. Determine the story prompt
        String prompt = "Create a fable about a lazy squirrel who learned the value of preparation from an  ant.";
        if (args.length > 0 && !args[0].isBlank()) {
            prompt = String.join(" ", args);
        }
        logger.info("Prompt: \"{}\"", prompt);

        try {
            // 2. Instantiate and wire components (Plain Java manual DI)
            ObjectMapper objectMapper = new ObjectMapper();

            ChatModel chatModel = new OpenAiChatModel();
            StoryGenerator storyGenerator = new DefaultStoryGenerator(chatModel, objectMapper);
            HtmlRenderer htmlRenderer = new ThymeleafHtmlRenderer();
            FileStorageService fileStorageService = new LocalFileStorageService();

            // 3. Generate story via general StoryGenerator
            logger.info("Generating story using configured ChatModel...");
            Story story = storyGenerator.generateStory(prompt);

            logger.info("--- Generated Fable Details ---");
            logger.info("Title: {}", story.title());
            logger.info("Moral: {}", story.moral());
            logger.info("--------------------------------");

            // 4. Render HTML
            logger.info("Rendering Thymeleaf HTML...");
            String htmlContent = htmlRenderer.renderStory(story);

            // 5. Save output files
            logger.info("Saving files to output/ directory...");
            fileStorageService.saveHtml(htmlContent, "output/story.html");
            fileStorageService.copyResource("style.css", "output/style.css");

            logger.info("=== FableForge Success! Output saved at output/story.html ===");

        } catch (IllegalStateException e) {
            logger.error("Configuration Error: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Execution Error", e);
            System.exit(1);
        }
    }
}
