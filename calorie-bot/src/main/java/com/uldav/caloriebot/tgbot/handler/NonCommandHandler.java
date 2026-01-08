package com.uldav.caloriebot.tgbot.handler;

import com.uldav.caloriebot.tgbot.exception.BotException;
import com.uldav.caloriebot.tgbot.service.RecognizeCommandService;
import com.uldav.caloriebot.tgbot.util.SendMessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Handler for non command messages for telegram bot users
 */
@AllArgsConstructor
@Slf4j
@Component
public class NonCommandHandler {
    private final RecognizeCommandService recognizeCommandService;

    public void handleNonCommand(TelegramClient telegramClient, Message message) throws BotException {
        log.info("A message received: messageId: {}, userId: {}, chatId: {}",
                message.getMessageId(), message.getFrom().getId(), message.getChatId());
        if (message.hasPhoto()
                && message.getCaption() != null
                && message.getCaption().startsWith("/recognize")) {
            //Telegram not able to send combination of /recognize + photo to a corresponding command handler,
            //that's why a workaround here
            recognizeCommandService.handleRecognizeCommand(telegramClient, message);
        } else {
            SendMessageUtil.sendMessage(telegramClient, message.getChatId().toString(), "Пожалуйста, используйте команды для коммуникации.");
        }
    }
}
