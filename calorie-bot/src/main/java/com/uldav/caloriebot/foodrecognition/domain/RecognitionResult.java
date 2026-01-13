package com.uldav.caloriebot.foodrecognition.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Domain model representing the result of food recognition from OpenAI.
 * <p>
 * This class is deserialized from JSON response returned by ChatGPT.
 * The expected JSON format matches the system prompt specification.
 */
@Data
public class RecognitionResult {
    /** General description of the recognized food item and estimated weight. */
    private String generalRecognitionInfo;

    /** Total calories in kcal. */
    private Integer calories;

    /**
     * Protein content in grams.
     * Mapped from "proteins" field in JSON response.
     */
    @JsonProperty("proteins")
    private Double protein;

    /** Carbohydrates content in grams. */
    private Double carbs;

    /** Fat content in grams. */
    private Double fats;

    /** Confidence score from 0 to 100 indicating recognition certainty. */
    private int confidence;
}
