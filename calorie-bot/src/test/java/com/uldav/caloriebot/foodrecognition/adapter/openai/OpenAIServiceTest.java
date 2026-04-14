package com.uldav.caloriebot.foodrecognition.adapter.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uldav.caloriebot.foodrecognition.PhotoForRecognitionReceived;
import com.uldav.caloriebot.foodrecognition.RecognitionAndAnalysesReady;
import com.uldav.caloriebot.foodrecognition.domain.RecognitionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.ai.chat.prompt.Prompt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ObjectMapper objectMapper;

    private OpenAIService openAIService;

    private static final String VALID_JSON = """
            {
                "generalRecognitionInfo": "Яблоко, примерно 150г",
                "calories": 78,
                "carbs": 20.5,
                "fats": 0.3,
                "proteins": 0.4,
                "confidence": 85
            }
            """;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        openAIService = new OpenAIService(chatClient, applicationEventPublisher, objectMapper);
    }

    @Nested
    @DisplayName("parseResponse tests")
    class ParseResponseTests {

        @Test
        @DisplayName("Should parse valid JSON response")
        void parseResponse_ValidJson_ReturnsRecognitionResult() {
            RecognitionResult result = openAIService.parseResponse(VALID_JSON);

            assertEquals("Яблоко, примерно 150г", result.getGeneralRecognitionInfo());
            assertEquals(78, result.getCalories());
            assertEquals(0.4, result.getProtein());
            assertEquals(20.5, result.getCarbs());
            assertEquals(0.3, result.getFats());
            assertEquals(85, result.getConfidence());
        }

        @Test
        @DisplayName("Should parse JSON wrapped in explanatory text")
        void parseResponse_JsonWithSurroundingText_ReturnsRecognitionResult() {
            String responseWithText = "Here is the analysis of the food:\n" + VALID_JSON + "\nHope this helps!";

            RecognitionResult result = openAIService.parseResponse(responseWithText);

            assertEquals("Яблоко, примерно 150г", result.getGeneralRecognitionInfo());
            assertEquals(78, result.getCalories());
        }

        @Test
        @DisplayName("Should throw RuntimeException for invalid JSON")
        void parseResponse_InvalidJson_ThrowsRuntimeException() {
            String invalidJson = "{ invalid json }";

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> openAIService.parseResponse(invalidJson));

            assertEquals("Failed to parse food recognition response", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw RuntimeException for empty response")
        void parseResponse_EmptyResponse_ThrowsRuntimeException() {
            String emptyResponse = "";

            assertThrows(RuntimeException.class,
                    () -> openAIService.parseResponse(emptyResponse));
        }
    }

    @Nested
    @DisplayName("extractJson tests")
    class ExtractJsonTests {

        @Test
        @DisplayName("Should extract JSON from response with surrounding text")
        void extractJson_WithSurroundingText_ReturnsJson() {
            String response = "Some text before {\"key\": \"value\"} some text after";

            String result = openAIService.extractJson(response);

            assertEquals("{\"key\": \"value\"}", result);
        }

        @Test
        @DisplayName("Should return original response when no JSON braces found")
        void extractJson_NoJsonBraces_ReturnsOriginal() {
            String response = "No JSON here";

            String result = openAIService.extractJson(response);

            assertEquals("No JSON here", result);
        }

        @Test
        @DisplayName("Should handle nested JSON objects")
        void extractJson_NestedJson_ReturnsFullJson() {
            String response = "Text {\"outer\": {\"inner\": \"value\"}} more text";

            String result = openAIService.extractJson(response);

            assertEquals("{\"outer\": {\"inner\": \"value\"}}", result);
        }

        @Test
        @DisplayName("Should return original when only opening brace found")
        void extractJson_OnlyOpeningBrace_ReturnsOriginal() {
            String response = "Text { without closing";

            String result = openAIService.extractJson(response);

            assertEquals("Text { without closing", result);
        }
    }

    @Nested
    @DisplayName("formatResultMessage tests")
    class FormatResultMessageTests {

        @Test
        @DisplayName("Should format result message with all fields")
        void formatResultMessage_ValidResult_ReturnsFormattedMessage() {
            RecognitionResult result = new RecognitionResult();
            result.setGeneralRecognitionInfo("Яблоко, примерно 150г");
            result.setCalories(78);
            result.setProtein(0.4);
            result.setCarbs(20.5);
            result.setFats(0.3);
            result.setConfidence(85);

            String message = openAIService.formatResultMessage(result);

            assertTrue(message.contains("*Распознанный продукт:* Яблоко, примерно 150г"));
            assertTrue(message.contains("🔥 Калории: 78 ккал"));
            assertTrue(message.contains("🥩 Белки: 0,4 г") || message.contains("🥩 Белки: 0.4 г"));
            assertTrue(message.contains("🍞 Углеводы: 20,5 г") || message.contains("🍞 Углеводы: 20.5 г"));
            assertTrue(message.contains("🧈 Жиры: 0,3 г") || message.contains("🧈 Жиры: 0.3 г"));
            assertTrue(message.contains("_Точность: 85%_"));
        }
        @Test
        @DisplayName("Should escape Telegram Markdown control characters in model text")
        void formatResultMessage_EscapesMarkdownCharacters() {
            RecognitionResult result = new RecognitionResult();
            result.setGeneralRecognitionInfo("Fish_[salad]*`chef` \\ special");
            result.setCalories(78);
            result.setProtein(0.4);
            result.setCarbs(20.5);
            result.setFats(0.3);
            result.setConfidence(85);

            String message = openAIService.formatResultMessage(result);

            assertTrue(message.contains("Fish\\_\\[salad]\\*\\`chef\\` \\\\ special"));
        }

        @Test
        @DisplayName("Should show placeholders when model omits nutrition fields")
        void formatResultMessage_MissingNutritionFields_ReturnsPlaceholders() {
            RecognitionResult result = new RecognitionResult();
            result.setGeneralRecognitionInfo("Яблоко");
            result.setConfidence(85);

            String message = openAIService.formatResultMessage(result);

            assertTrue(message.contains("🔥 Калории: N/A"));
            assertTrue(message.contains("🥩 Белки: N/A"));
            assertTrue(message.contains("🍞 Углеводы: N/A"));
            assertTrue(message.contains("🧈 Жиры: N/A"));
        }
    }

    @Nested
    @DisplayName("processRecognitionAndCaloriesCountRequest tests")
    class ProcessRecognitionTests {

        @Test
        @DisplayName("Should process photo and publish event with formatted message")
        void processRecognition_ValidPhoto_PublishesEvent() {
            // Given
            byte[] photoBytes = new byte[]{1, 2, 3};
            long chatId = 12345L;
            PhotoForRecognitionReceived photo = new PhotoForRecognitionReceived(photoBytes, chatId);

            ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
            ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.content()).thenReturn(VALID_JSON);

            // When
            openAIService.processRecognitionAndCaloriesCountRequest(photo);

            // Then
            ArgumentCaptor<RecognitionAndAnalysesReady> eventCaptor =
                    ArgumentCaptor.forClass(RecognitionAndAnalysesReady.class);
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

            RecognitionAndAnalysesReady event = eventCaptor.getValue();
            assertEquals(chatId, event.getChatId());
            assertTrue(event.getMessage().contains("Яблоко, примерно 150г"));
            assertTrue(event.getMessage().contains("78 ккал"));
        }

        @Test
        @DisplayName("Should throw exception when ChatGPT returns invalid JSON")
        void processRecognition_InvalidResponse_ThrowsException() {
            // Given
            byte[] photoBytes = new byte[]{1, 2, 3};
            PhotoForRecognitionReceived photo = new PhotoForRecognitionReceived(photoBytes, 12345L);

            ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
            ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.content()).thenReturn("Not a valid JSON");

            // When & Then
            assertThrows(RuntimeException.class,
                    () -> openAIService.processRecognitionAndCaloriesCountRequest(photo));

            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should publish event when ChatGPT omits nutrition fields")
        void processRecognition_MissingNutritionFields_PublishesEvent() {
            byte[] photoBytes = new byte[]{1, 2, 3};
            long chatId = 12345L;
            PhotoForRecognitionReceived photo = new PhotoForRecognitionReceived(photoBytes, chatId);
            String partialJson = """
                    {
                        "generalRecognitionInfo": "Яблоко",
                        "confidence": 85
                    }
                    """;

            ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
            ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.content()).thenReturn(partialJson);

            openAIService.processRecognitionAndCaloriesCountRequest(photo);

            ArgumentCaptor<RecognitionAndAnalysesReady> eventCaptor =
                    ArgumentCaptor.forClass(RecognitionAndAnalysesReady.class);
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

            RecognitionAndAnalysesReady event = eventCaptor.getValue();
            assertEquals(chatId, event.getChatId());
            assertTrue(event.getMessage().contains("🔥 Калории: N/A"));
            assertTrue(event.getMessage().contains("🥩 Белки: N/A"));
        }
    }
}
