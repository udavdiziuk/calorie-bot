package com.uldav.caloriebot.userprofile.api;

import com.uldav.caloriebot.userprofile.api.dto.UserProfileDto;

/**
 * Public API for user profile operations.
 * <p>
 * This is the main entry point for other modules to interact with the userprofile module.
 */
public interface UserProfileService {

    /**
     * Retrieves a user profile by Telegram user ID.
     * If the profile does not exist, creates a new one from the provided data and returns it.
     */
    UserProfileDto getOrCreateProfile(UserProfileDto profileData);

    /**
     * Retrieves a user profile by Telegram user ID.
     * Returns null if the profile does not exist.
     */
    UserProfileDto findByTelegramUserId(long telegramUserId);

    /**
     * Updates an existing user profile with new data.
     * Missing fields in the provided DTO preserve existing values.
     * Returns the updated profile, or null if the profile does not exist.
     */
    UserProfileDto updateProfile(UserProfileDto profileData);

    /**
     * Soft-deletes a user profile by Telegram user ID.
     */
    void deleteProfile(long telegramUserId);
}
