package com.palak.fableforge.service.impl;

import com.palak.fableforge.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LocalFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Override
    public void saveHtml(String htmlContent, String targetPath) throws IOException {
        Path path = Paths.get(targetPath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, htmlContent);
        logger.info("Saved HTML file to: {}", path.toAbsolutePath());
    }

    @Override
    public void copyResource(String resourcePath, String targetPath) throws IOException {
        Path path = Paths.get(targetPath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found on classpath: " + resourcePath);
            }
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Copied CSS resource to: {}", path.toAbsolutePath());
        }
    }
}
