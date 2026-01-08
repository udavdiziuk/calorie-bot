package com.uldav.caloriebot.tgbot.handler;


import com.uldav.caloriebot.tgbot.util.SendMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Handler for /recognize command from Telegram bot users.
 * It expects photo provided by users.
 */

@Slf4j
public class RecognizeCommandHandler extends BotCommand implements IBotCommand {

    public RecognizeCommandHandler() {
        super("recognize", "Распознает еду и предоставляет информацию о количестве калорий/белков/жиров/углеводов");
    }

    @Override
    public String getCommandIdentifier() {
        return getCommand();
    }

    @Override
    public void processMessage(TelegramClient telegramClient, Message message, String[] strings) {
        log.info("Received message: {}", message.toString());
        //recognizeCommandService.handleRecognizeCommand(telegramClient, message);
        //As of now telegram is not able to work with commands + photo so /recognize + photo is a non-command message.
        //so here we message user he should add photo to command
        SendMessageUtil.sendMessage(telegramClient, message.getChatId().toString(), "Пожалуйста, добавьте фото к команде.");
    }

}
