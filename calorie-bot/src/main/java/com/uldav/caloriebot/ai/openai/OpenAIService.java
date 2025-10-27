package com.uldav.caloriebot.ai.openai;

import com.uldav.caloriebot.ai.api.RecognitionAndCaloriesApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@AllArgsConstructor
@Service
@Slf4j
public class OpenAIService implements RecognitionAndCaloriesApi {
    private static final String usermessage = "По изображению ниже распознай продукты питания по их количеству. Также предоставь количество калорий и БЖУ";
    private final ChatClient chatClient;

    @Override
    public String processRecognitionAndCaloriesCountRequest(byte[] photo) {
        log.info("Start processing recognition and calories count using OpenAI API");
        UserMessage userMessage = UserMessage.builder()
                .text(usermessage)
                .media(Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG).data(photo).build())
                .build();
        return chatClient.prompt(new Prompt(userMessage)).call().content();
    }
}
