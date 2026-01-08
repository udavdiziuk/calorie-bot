package com.uldav.caloriebot.tgbot.handler;

import com.uldav.caloriebot.tgbot.exception.BotException;
import com.uldav.caloriebot.tgbot.service.RecognizeCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecognizeCommandHandlerTests {
    @Mock
    private TelegramClient telegramClient;

    @Mock
    private Message message;

    @InjectMocks
    private RecognizeCommandService recognizeCommandService;

    @Test
    @DisplayName("downloadImage should throw BotException when TelegramApiException occurs during GetFile")
    void downloadImage_ShouldThrowBotExceptionOnTelegramApiException() throws TelegramApiException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        List<PhotoSize> photos = List.of(photoSize);
        when(photoSize.getFileId()).thenReturn("fileId");
        when(message.hasPhoto()).thenReturn(true);
        when(message.getPhoto()).thenReturn(photos);


        when(telegramClient.execute(any(GetFile.class))).thenThrow(new TelegramApiException("API Error"));

        // When & Then
        BotException exception = assertThrows(BotException.class, () -> recognizeCommandService.handleRecognizeCommand(telegramClient, message));
        assertEquals("API Error", exception.getMessage());
    }

    @Test
    @DisplayName("downloadImage should throw BotException when IOException occurs during stream reading")
    void downloadImage_ShouldThrowBotExceptionOnIOException() throws TelegramApiException, IOException {
        // Given
        PhotoSize photoSize = mock(PhotoSize.class);
        List<PhotoSize> photos = List.of(photoSize);
        when(photoSize.getFileId()).thenReturn("fileId");
        when(message.hasPhoto()).thenReturn(true);
        when(message.getPhoto()).thenReturn(photos);

        File file = mock(File.class);
        when(file.getFilePath()).thenReturn("path/to/image.jpg");
        when(telegramClient.execute(any(GetFile.class))).thenReturn(file);

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException("Read Error"));
        when(telegramClient.downloadFileAsStream("path/to/image.jpg")).thenReturn(inputStream);

        // When & Then
        BotException exception = assertThrows(BotException.class, () -> recognizeCommandService.handleRecognizeCommand(telegramClient, message));
        assertEquals("Read Error", exception.getMessage());
    }
}
