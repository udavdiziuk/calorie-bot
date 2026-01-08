package com.uldav.caloriebot.tgbot.util;

import com.uldav.caloriebot.tgbot.exception.TelegramSendMessageException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@UtilityClass
public class SendMessageUtil {

    public static void sendMessage(TelegramClient telegramClient, String chatId, String message) {
        SendMessage snd = new SendMessage(chatId, message);
        snd.enableMarkdown(true);
        snd.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(snd);
        } catch (TelegramApiException e) {
            log.error("Telegram API exception");
            throw new TelegramSendMessageException(e.getMessage());
        }
    }
}
