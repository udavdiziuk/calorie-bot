package com.uldav.caloriebot.tgbot.exception;

/**
 * Throws in case an error occurred during send message operation
 */
public class TelegramSendMessageException extends RuntimeException {
    public TelegramSendMessageException(String message) {
        super(message);
    }
}
