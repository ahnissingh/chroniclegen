package com.palak.fableforge.service;

import com.palak.fableforge.model.Story;

public interface StoryGenerator {
    Story generateStory(String prompt);
}
