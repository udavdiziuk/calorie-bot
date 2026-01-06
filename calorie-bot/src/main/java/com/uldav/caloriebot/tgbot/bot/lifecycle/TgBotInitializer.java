package com.uldav.caloriebot.tgbot.bot.lifecycle;

import com.uldav.caloriebot.tgbot.bot.AIFoodCalorieBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Initializes the telegram bot in Telegram system
 */
@Slf4j
@Configuration
public class TgBotInitializer {
    @Value("${BOT_TOKEN}")
    private String botToken;

    @Bean
    public BotSession sessionStart(TelegramBotsLongPollingApplication aBotApplication, AIFoodCalorieBot aBot) throws TelegramApiException {
        log.info("Telegram bot registered and polling started");
        return aBotApplication.registerBot(botToken, aBot);
    }

    @Bean
    public TelegramBotsLongPollingApplication application() {
        return new TelegramBotsLongPollingApplication();
    }
}
