package com.uldav.caloriebot.foodrecognition.api;


import com.uldav.caloriebot.foodrecognition.PhotoForRecognitionReceived;

/**
 * Responsible for food recognition and calories count.
 */
public interface RecognitionAndCaloriesApi {
    default void processRecognitionAndCaloriesCountRequest(PhotoForRecognitionReceived photo) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
