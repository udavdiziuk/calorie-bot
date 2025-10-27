package com.uldav.caloriebot.tgbot;

import com.uldav.caloriebot.ai.openai.OpenAIService;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Slf4j
@Component
public class AIFoodCalorieBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;
    private final OpenAIService openAIService;

    public AIFoodCalorieBot(@Value("${BOT_TOKEN}") final String botToken, OpenAIService openAIService) {
        log.info("AIFoodCalorieBot init calorieBot");
        this.botToken = botToken;
        telegramClient = new OkHttpTelegramClient(getBotToken());
        this.openAIService = openAIService;
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
            //TODO: move download file logic
            List<PhotoSize> photos = update.getMessage().getPhoto();
            // Choose the photo with the highest resolution (usually the last in the list)
            PhotoSize bestPhoto = photos.getLast();
            try {
                // Retrieve file path from Telegram using fileId
                GetFile getFile = GetFile.builder().fileId(bestPhoto.getFileId()).build();
                org.telegram.telegrambots.meta.api.objects.File tgFile = telegramClient.execute(getFile);
                String filePath = tgFile.getFilePath();

                // Log the file path and image format (by extension if available)
                String imageFormat = "unknown";
                if (filePath != null) {
                    int dotIdx = filePath.lastIndexOf('.');
                    if (dotIdx > -1 && dotIdx < filePath.length() - 1) {
                        imageFormat = filePath.substring(dotIdx + 1).toLowerCase();
                    }
                    log.info("Telegram photo path: {}", filePath);
                }
                log.info("Detected image format: {}", imageFormat);

                // Download the file as stream using the resolved path
                InputStream userPhotoIS = telegramClient.downloadFileAsStream(filePath);
                byte[] photoBytes = userPhotoIS.readAllBytes();

                String response = openAIService.processRecognitionAndCaloriesCountRequest(photoBytes);
                TelegramBotUtils.sendResponse(telegramClient, update.getMessage(), response);
            } catch (TelegramApiException e) {
                //TODO: exception handling
                throw new RuntimeException(e);
            } catch (IOException e) {
                //TODO: exception handling
                throw new RuntimeException(e);
            }
        } else {
            TelegramBotUtils.sendResponse(telegramClient, update.getMessage(), "Unsupported operation.\nPlease provide your food photo instead.");
        }
    }
}
