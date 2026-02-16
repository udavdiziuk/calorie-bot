package com.uldav.caloriebot.userprofile.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for user profile information.
 * <p>
 * Used for cross-module communication via the userprofile API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long telegramUserId;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isBot;
}
