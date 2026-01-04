package com.uldav.caloriebot.ai.api;


import com.uldav.caloriebot.event.PhotoForRecognitionReceived;

/**
 * Responsible for food recognition and calories count.
 */
public interface RecognitionAndCaloriesApi {
    default void processRecognitionAndCaloriesCountRequest(PhotoForRecognitionReceived photo) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
