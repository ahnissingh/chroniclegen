package com.palak.fableforge.service;

import java.io.IOException;

public interface FileStorageService {
    void saveHtml(String htmlContent, String targetPath) throws IOException;
    void copyResource(String resourcePath, String targetPath) throws IOException;
}
