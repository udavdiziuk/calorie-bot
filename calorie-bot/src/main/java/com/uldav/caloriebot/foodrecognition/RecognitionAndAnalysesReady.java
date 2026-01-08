package com.uldav.caloriebot.foodrecognition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecognitionAndAnalysesReady {
    private String message;
    private long chatId;
}
