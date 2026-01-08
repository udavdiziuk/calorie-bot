package com.uldav.caloriebot.tgbot.bot;

import com.uldav.caloriebot.tgbot.handler.NonCommandHandler;
import com.uldav.caloriebot.tgbot.handler.RecognizeCommandHandler;
import com.uldav.caloriebot.tgbot.handler.UnknownCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class AIFoodCalorieBot extends CommandLongPollingTelegramBot {

    private final NonCommandHandler nonCommandHandler;
    private final UnknownCommandHandler unknownCommandHandler;

    public AIFoodCalorieBot(TelegramClient telegramClient, @Value("${BOT_NAME}") String botName,
                            NonCommandHandler nonCommandHandler, UnknownCommandHandler unknownCommandHandler) {
        super(telegramClient, true, () -> botName);
        //command handlers
        this.nonCommandHandler = nonCommandHandler;
        this.unknownCommandHandler = unknownCommandHandler;
        this.registerAll(new RecognizeCommandHandler());

        this.setMenuButton();
        log.info("Created AIFoodCalorieBot");
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        log.info("Received non command update: {}", update);
        nonCommandHandler.handleNonCommand(telegramClient, update.getMessage());
    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        log.info("Received invalid command update: {}", update);
        unknownCommandHandler.handleUnknownCommand(telegramClient, update.getMessage());
    }

    @Override
    public boolean filter(Message message) {
        boolean result = super.filter(message);
        log.info("Filter result: {}, text={}", result, message.getText());
        return result;
    }

    /**
     * Configures Menu button in the Telegram Bot. Setups all available to user commands.
     */
    private void setMenuButton() {
        log.info("setMenuButton()");
        List<BotCommand> commands = new ArrayList<>();
        this.getRegisteredCommands().forEach(iBotCommand -> commands.add((BotCommand) iBotCommand));
        try {
            this.telegramClient.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            log.error("Error adding menu commands", ex);
        }
    }
}
