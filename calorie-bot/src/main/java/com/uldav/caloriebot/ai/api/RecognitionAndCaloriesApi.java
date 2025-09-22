package com.uldav.caloriebot.ai.api;

import java.io.File;

/**
 * Responsible for food recognition and calories count.
 */
public interface RecognitionAndCaloriesApi {
    default String processRecognitionAndCaloriesCountRequest(File photo) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
