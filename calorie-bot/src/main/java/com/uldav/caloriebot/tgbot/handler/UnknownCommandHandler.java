package com.uldav.caloriebot.tgbot.handler;

import com.uldav.caloriebot.tgbot.exception.BotException;
import com.uldav.caloriebot.tgbot.util.SendMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Handler for unknown commands (not registered or mistyped) from telegram bot users
 */
@Slf4j
@Component
public class UnknownCommandHandler {
    public void handleUnknownCommand(TelegramClient telegramClient, Message message) throws BotException {
        log.info("A message received: messageId: {}, userId: {}, chatId: {}",
                message.getMessageId(), message.getFrom().getId(), message.getChatId());
        SendMessageUtil.sendMessage(telegramClient, message.getChatId().toString(), "Неизвестная команда.");
    }
}
