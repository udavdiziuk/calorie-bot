package com.uldav.caloriebot.tgbot.util;

import com.uldav.caloriebot.tgbot.exception.BotException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotUtilsTest {

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private Message message;

    @Test
    @DisplayName("sendResponse should execute SendMessage when called")
    void sendResponse_ShouldExecuteSendMessage() throws TelegramApiException {
        // Given
        Long chatId = 12345L;
        String text = "Test message";
        when(message.getChatId()).thenReturn(chatId);

        // When
        TelegramBotUtils.sendResponse(telegramClient, message, text);

        // Then
        verify(telegramClient).execute(any(SendMessage.class));
    }

    @Test
    @DisplayName("sendResponse should catch TelegramApiException and log it")
    void sendResponse_ShouldCatchTelegramApiException() throws TelegramApiException {
        // Given
        when(message.getChatId()).thenReturn(12345L);
        when(telegramClient.execute(any(SendMessage.class))).thenThrow(new TelegramApiException("API Error"));

        // When & Then
        assertDoesNotThrow(() -> TelegramBotUtils.sendResponse(telegramClient, message, "text"));
        verify(telegramClient).execute(any(SendMessage.class));
    }

    @Test
    @DisplayName("downloadImage should return byte array on success")
    void downloadImage_ShouldReturnBytesOnSuccess() throws TelegramApiException, IOException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        when(photoSize.getFileId()).thenReturn("fileId");
        List<PhotoSize> photos = List.of(photoSize);

        File file = mock(File.class);
        when(file.getFilePath()).thenReturn("path/to/image.jpg");
        when(telegramClient.execute(any(GetFile.class))).thenReturn(file);

        byte[] expectedBytes = "image content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedBytes);
        when(telegramClient.downloadFileAsStream("path/to/image.jpg")).thenReturn(inputStream);

        // When
        byte[] actualBytes = TelegramBotUtils.downloadImage(telegramClient, photos);

        // Then
        assertArrayEquals(expectedBytes, actualBytes);
        verify(telegramClient).execute(any(GetFile.class));
        verify(telegramClient).downloadFileAsStream("path/to/image.jpg");
    }

    @Test
    @DisplayName("downloadImage should throw BotException when TelegramApiException occurs during GetFile")
    void downloadImage_ShouldThrowBotExceptionOnTelegramApiException() throws TelegramApiException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        when(photoSize.getFileId()).thenReturn("fileId");
        List<PhotoSize> photos = List.of(photoSize);

        when(telegramClient.execute(any(GetFile.class))).thenThrow(new TelegramApiException("API Error"));

        // When & Then
        BotException exception = assertThrows(BotException.class, () -> TelegramBotUtils.downloadImage(telegramClient, photos));
        assertEquals("API Error", exception.getMessage());
    }

    @Test
    @DisplayName("downloadImage should throw BotException when IOException occurs during stream reading")
    void downloadImage_ShouldThrowBotExceptionOnIOException() throws TelegramApiException, IOException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        when(photoSize.getFileId()).thenReturn("fileId");
        List<PhotoSize> photos = List.of(photoSize);

        File file = mock(File.class);
        when(file.getFilePath()).thenReturn("path/to/image.jpg");
        when(telegramClient.execute(any(GetFile.class))).thenReturn(file);

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException("Read Error"));
        when(telegramClient.downloadFileAsStream("path/to/image.jpg")).thenReturn(inputStream);

        // When & Then
        BotException exception = assertThrows(BotException.class, () -> TelegramBotUtils.downloadImage(telegramClient, photos));
        assertEquals("Read Error", exception.getMessage());
    }

    @Test
    @DisplayName("downloadImage should handle empty filePath gracefully")
    void downloadImage_ShouldHandleEmptyFilePath() throws TelegramApiException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        when(photoSize.getFileId()).thenReturn("fileId");
        List<PhotoSize> photos = List.of(photoSize);

        File file = mock(File.class);
        when(file.getFilePath()).thenReturn(null);
        when(telegramClient.execute(any(GetFile.class))).thenReturn(file);

        byte[] expectedBytes = "image content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedBytes);
        when(telegramClient.downloadFileAsStream((String) null)).thenReturn(inputStream);

        // When
        byte[] actualBytes = TelegramBotUtils.downloadImage(telegramClient, photos);

        // Then
        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    @DisplayName("downloadImage should throw exception when photos list is empty")
    void downloadImage_ShouldThrowExceptionWhenPhotosListIsEmpty() {
        // Given
        List<PhotoSize> photos = Collections.emptyList();

        // When & Then
        assertThrows(RuntimeException.class, () -> TelegramBotUtils.downloadImage(telegramClient, photos));
    }
}
