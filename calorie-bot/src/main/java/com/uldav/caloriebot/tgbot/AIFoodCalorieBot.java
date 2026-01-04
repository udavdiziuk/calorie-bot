package com.uldav.caloriebot.tgbot;

import com.uldav.caloriebot.event.PhotoForRecognitionReceived;
import com.uldav.caloriebot.event.RecognitionAndAnalysesReady;
import com.uldav.caloriebot.tgbot.util.TelegramBotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Slf4j
@Component
public class AIFoodCalorieBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AIFoodCalorieBot(@Value("${BOT_TOKEN}") final String botToken, final ApplicationEventPublisher applicationEventPublisher) {
        log.info("AIFoodCalorieBot init calorieBot");
        this.botToken = botToken;
        telegramClient = new OkHttpTelegramClient(getBotToken());
        this.applicationEventPublisher = applicationEventPublisher;
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
        if (update.getMessage().hasPhoto()) {
            byte[] photoBytes = TelegramBotUtils.downloadImage(telegramClient, update.getMessage().getPhoto());
            String response = "Обрабатываем фото...";//openAIService.processRecognitionAndCaloriesCountRequest(photoBytes);
            TelegramBotUtils.sendResponse(telegramClient, update.getMessage().getChatId(), response);
            applicationEventPublisher.publishEvent(new PhotoForRecognitionReceived(photoBytes, update.getMessage().getChatId()));
        } else {
            TelegramBotUtils.sendResponse(telegramClient, update.getMessage().getChatId(), "Unsupported operation.\nPlease provide your food photo instead.");
        }
    }

    @EventListener
    public void handleRecognitionResponse(RecognitionAndAnalysesReady response) {
        TelegramBotUtils.sendResponse(telegramClient, response.getChatId(), response.getMessage());
    }
}
