package com.uldav.caloriebot.tgbot.handler;

import com.uldav.caloriebot.tgbot.exception.BotException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Handler for unknown commands (not registered or mistyped) from telegram bot users
 */
@Slf4j
@UtilityClass
public class UnknownCommandHandler {
    public static void handleUnknownCommand(TelegramClient telegramClient, Message message) throws BotException {
        log.info("A message received: messageId: {}, userId: {}, chatId: {}",
                message.getMessageId(), message.getFrom().getId(), message.getChatId());
        SendMessage snd = new SendMessage(message.getChatId().toString(), "Unknown command received: " + message.getText());
        snd.enableMarkdown(true);
        snd.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(snd);
        } catch (TelegramApiException e) {
            throw new BotException(e.getMessage());
        }
    }
}
