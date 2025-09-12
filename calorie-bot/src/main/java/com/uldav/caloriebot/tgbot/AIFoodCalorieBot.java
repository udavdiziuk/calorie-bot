package com.uldav.caloriebot.tgbot;

import com.uldav.caloriebot.tgbot.util.TelegramBotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;


@Slf4j
@Component
public class AIFoodCalorieBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;

    public AIFoodCalorieBot(@Value("${BOT_TOKEN}") final String botToken) {
        log.info("AIFoodCalorieBot init calorieBot");
        this.botToken = botToken;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        log.info("User Id: {}", update.getMessage().getFrom().getId());
        if (update.hasCallbackQuery() && update.getMessage().hasPhoto()) {
            log.debug("Received user photo. Store user info.");
            List<PhotoSize> photos = update.getMessage().getPhoto();
            if (photos.size() > 1) {
                TelegramBotUtils.sendResponse(telegramClient, update.getMessage(), "Please provide only 1 photo");
            } else {
                //TODO: food recognition
            }
        } else {
            TelegramBotUtils.sendResponse(telegramClient, update.getMessage(), "Unsupported operation.\nPlease provide your food photo instead.");
        }
    }
}
