package com.uldav.caloriebot.tgbot.service;

import com.uldav.caloriebot.foodrecognition.PhotoForRecognitionReceived;
import com.uldav.caloriebot.tgbot.exception.BotException;
import com.uldav.caloriebot.tgbot.util.SendMessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class RecognizeCommandService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void handleRecognizeCommand(TelegramClient telegramClient, Message message) {
        if (message.hasPhoto()) {
            byte[] photoBytes = downloadImage(telegramClient, message.getPhoto());
            log.info("Photo size: {}", photoBytes.length);
            applicationEventPublisher.publishEvent(new PhotoForRecognitionReceived(photoBytes, message.getChatId()));
            SendMessageUtil.sendMessage(telegramClient, message.getChatId().toString(), "Обрабатываю фотографию, пожалуйста подождите...");
        }
    }

    /**
     * Downloads the photo with the highest available resolution from a Telegram bot message
     * using the Telegram API.
     *
     * @param telegramClient the client used to send requests to the Telegram API
     * @param photos the list of photo sizes from the bot message
     * @return a byte array containing the downloaded photo
     */
    private byte[] downloadImage(TelegramClient telegramClient, List<PhotoSize> photos) {
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
            try (InputStream userPhotoIS = telegramClient.downloadFileAsStream(filePath)) {
                return userPhotoIS.readAllBytes();
            }
        } catch (TelegramApiException e) {
            log.error("An exception occurred during downloading photo from Telegram Bot: {}", e.getMessage());
            throw new BotException(e.getMessage());
        } catch (IOException e) {
            log.error("An exception occurred during reading bytes of photo.");
            throw new BotException(e.getMessage());
        }
    }
}
