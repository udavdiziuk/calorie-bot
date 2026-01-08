package com.uldav.caloriebot.tgbot.exception;

/**
 * General exceptions from Telegram API
 */
public class BotException extends RuntimeException {
    public BotException(String message) { super(message); }
}
