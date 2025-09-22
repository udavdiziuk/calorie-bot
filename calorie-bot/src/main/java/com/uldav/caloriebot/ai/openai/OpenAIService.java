package com.uldav.caloriebot.ai.openai;

import com.uldav.caloriebot.ai.api.RecognitionAndCaloriesApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class OpenAIService implements RecognitionAndCaloriesApi {
    @Override
    public String processRecognitionAndCaloriesCountRequest(File photo) {
        log.info("Start processing recognition and calories count using OpenAI API");

        return "";
    }
}
