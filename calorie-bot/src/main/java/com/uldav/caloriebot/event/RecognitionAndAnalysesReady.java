package com.uldav.caloriebot.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RecognitionAndAnalysesReady {
    private String message;
    private long chatId;
}
