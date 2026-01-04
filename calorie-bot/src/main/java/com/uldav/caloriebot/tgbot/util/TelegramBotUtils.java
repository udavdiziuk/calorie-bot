package com.uldav.caloriebot.tgbot.util;

import com.uldav.caloriebot.tgbot.exception.BotException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@UtilityClass
public class TelegramBotUtils {
    /**
     * Creates generic response from telegram bot with given parameters
     * @param client Telegram client to send message
     * @param chatId chat where to send response
     * @param messageText text of the message
     */
    public static void sendResponse(TelegramClient client, long chatId, String messageText) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            log.debug("An exception occurred: {}", e.getMessage());
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
    public static byte[] downloadImage(TelegramClient telegramClient, List<PhotoSize> photos) {
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
