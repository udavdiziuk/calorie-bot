package com.uldav.caloriebot.foodrecognition.domain;

import lombok.Data;

@Data
public class RecognitionResult {
    private String generalRecognitionInfo;
    private Integer calories;
    private Double protein;
    private Double carbs;
    private Double fats;
    private int confidence;
}
