package com.uldav.caloriebot.foodrecognition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhotoForRecognitionReceived {
    private byte[] photo;
    private long chatId;
}
