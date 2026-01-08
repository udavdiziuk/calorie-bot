package com.uldav.caloriebot.foodrecognition.adapter;


import com.uldav.caloriebot.foodrecognition.PhotoForRecognitionReceived;
import org.springframework.modulith.events.ApplicationModuleListener;

/**
 * Responsible for food recognition and calories count.
 */
public interface RecognitionAdapter {

    @ApplicationModuleListener
    default void processRecognitionAndCaloriesCountRequest(PhotoForRecognitionReceived photo) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
