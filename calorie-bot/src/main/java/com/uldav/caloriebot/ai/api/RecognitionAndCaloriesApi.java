package com.uldav.caloriebot.ai.api;


/**
 * Responsible for food recognition and calories count.
 */
public interface RecognitionAndCaloriesApi {
    default String processRecognitionAndCaloriesCountRequest(byte[] photo) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
