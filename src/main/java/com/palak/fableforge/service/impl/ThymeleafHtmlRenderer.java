package com.palak.fableforge.service.impl;

import com.palak.fableforge.model.Story;
import com.palak.fableforge.service.HtmlRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThymeleafHtmlRenderer implements HtmlRenderer {

    private static final Logger logger = LoggerFactory.getLogger(ThymeleafHtmlRenderer.class);
    private final TemplateEngine templateEngine;

    public ThymeleafHtmlRenderer() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    @Override
    public String renderStory(Story story) {
        logger.debug("Rendering story: \"{}\" into HTML layout...", story.title());
        Context context = new Context();
        context.setVariable("story", story);
        return templateEngine.process("story", context);
    }
}
