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
 * Handler for non command messages for telegram bot users
 */
@Slf4j
@UtilityClass
public class NonCommandHandler {
    public static void handleNonCommand(TelegramClient telegramClient, Message message) throws BotException {
        log.info("A message received: messageId: {}, userId: {}, chatId: {}",
                message.getMessageId(), message.getFrom().getId(), message.getChatId());
        SendMessage snd = new SendMessage(message.getChatId().toString(), "Non-command message received: " + message.getText() + ". I can receive only commands.");
        snd.enableMarkdown(true);
        snd.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(snd);
        } catch (TelegramApiException e) {
            throw new BotException(e.getMessage());
        }
    }
}
