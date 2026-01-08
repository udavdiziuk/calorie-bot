package com.uldav.caloriebot.tgbot.adapter;

import com.uldav.caloriebot.foodrecognition.RecognitionAndAnalysesReady;
import com.uldav.caloriebot.tgbot.util.SendMessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@AllArgsConstructor
@Slf4j
@Component
public class TelegramMessageSender {
    private final TelegramClient telegramClient;

    @ApplicationModuleListener
    public void handleRecognitionAndAnalysesReady(RecognitionAndAnalysesReady event) {
        log.info("Received RecognitionAndAnalysesReady event");
        SendMessageUtil.sendMessage(telegramClient, String.valueOf(event.getChatId()), event.getMessage());
    }
}
