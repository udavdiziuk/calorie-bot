package com.uldav.caloriebot.userprofile.infrastructure;

import com.uldav.caloriebot.userprofile.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for user profile persistence.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByTelegramUserId(long telegramUserId);

    Optional<UserProfile> findByTelegramUserIdAndDeletedFalse(long telegramUserId);
}
