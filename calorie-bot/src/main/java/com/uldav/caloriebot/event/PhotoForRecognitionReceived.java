package com.uldav.caloriebot.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PhotoForRecognitionReceived {
    private byte[] photo;
    private long chatId;
}
