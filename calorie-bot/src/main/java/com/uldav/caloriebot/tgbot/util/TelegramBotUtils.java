package com.uldav.caloriebot.tgbot.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@UtilityClass
public class TelegramBotUtils {
    public static void sendResponse(TelegramClient client, Message requestMessage, String messageText) {
        long chatId = requestMessage.getChatId();
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            log.debug("An exception occurred: {}", e.getMessage());
        }
    }
}
