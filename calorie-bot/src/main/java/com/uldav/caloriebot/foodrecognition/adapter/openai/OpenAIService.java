package com.uldav.caloriebot.foodrecognition.adapter.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uldav.caloriebot.foodrecognition.RecognitionAndAnalysesReady;
import com.uldav.caloriebot.foodrecognition.adapter.RecognitionAdapter;
import com.uldav.caloriebot.foodrecognition.PhotoForRecognitionReceived;
import com.uldav.caloriebot.foodrecognition.domain.RecognitionResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

/**
 * Service for processing food recognition requests using OpenAI ChatGPT API.
 * <p>
 * Listens for {@link PhotoForRecognitionReceived} events, sends the photo to ChatGPT
 * for food recognition, parses the JSON response, and publishes a
 * {@link RecognitionAndAnalysesReady} event with the formatted result.
 */
@AllArgsConstructor
@Service
@Slf4j
public class OpenAIService implements RecognitionAdapter {
    private static final String USER_MESSAGE = "По изображению ниже распознай продукты питания по их количеству. Также предоставь количество калорий и БЖУ";
    private final ChatClient chatClient;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Processes a food recognition request by sending the photo to OpenAI API.
     * <p>
     * The response is parsed into {@link RecognitionResult} and formatted as a
     * user-friendly message before publishing the result event.
     *
     * @param photo the event containing the photo bytes and chat ID
     */
    @Override
    public void processRecognitionAndCaloriesCountRequest(PhotoForRecognitionReceived photo) {
        log.info("Start processing recognition and calories count using OpenAI API");
        UserMessage userMessage = UserMessage.builder()
                .text(USER_MESSAGE)
                .media(Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG).data(photo.getPhoto()).build())
                .build();

        String response = chatClient.prompt(new Prompt(userMessage)).call().content();
        RecognitionResult result = parseResponse(response);
        String message = formatResultMessage(result);
        applicationEventPublisher.publishEvent(new RecognitionAndAnalysesReady(message, photo.getChatId()));
    }

    /**
     * Parses the OpenAI response string into a {@link RecognitionResult} object.
     *
     * @param response the raw response from ChatGPT
     * @return parsed recognition result
     * @throws RuntimeException if JSON parsing fails
     */
    RecognitionResult parseResponse(String response) {
        String json = extractJson(response);
        try {
            return objectMapper.readValue(json, RecognitionResult.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI response: {}", response, e);
            throw new RuntimeException("Failed to parse food recognition response", e);
        }
    }

    /**
     * Extracts JSON object from a response that may contain surrounding text.
     * <p>
     * ChatGPT sometimes wraps JSON in explanatory text; this method extracts
     * just the JSON portion between the first '{' and last '}'.
     *
     * @param response the raw response string
     * @return extracted JSON string, or original response if no JSON found
     */
    String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    /**
     * Formats a recognition result into a user-friendly Telegram message.
     *
     * @param result the parsed recognition result
     * @return formatted message with Markdown formatting
     */
    String formatResultMessage(RecognitionResult result) {
        return String.format("""
                *Распознанный продукт:* %s

                *Пищевая ценность:*
                🔥 Калории: %d ккал
                🥩 Белки: %.1f г
                🍞 Углеводы: %.1f г
                🧈 Жиры: %.1f г

                _Точность: %d%%_""",
                result.getGeneralRecognitionInfo(),
                result.getCalories(),
                result.getProtein(),
                result.getCarbs(),
                result.getFats(),
                result.getConfidence());
    }
}
