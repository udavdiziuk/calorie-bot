package com.uldav.caloriebot.userprofile.application;

import com.uldav.caloriebot.userprofile.api.dto.UserProfileDto;
import com.uldav.caloriebot.userprofile.api.UserProfileService;
import com.uldav.caloriebot.userprofile.application.mapper.UserProfileMapper;
import com.uldav.caloriebot.userprofile.domain.UserProfile;
import com.uldav.caloriebot.userprofile.infrastructure.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link UserProfileService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional
    public UserProfileDto getOrCreateProfile(UserProfileDto profileData) {
        return repository.findByTelegramUserIdAndDeletedFalse(profileData.getTelegramUserId())
                .map(mapper::toDto)
                .orElseGet(() -> {
                    UserProfile profile = mapper.toEntity(profileData);
                    LocalDateTime now = LocalDateTime.now();
                    profile.setCreatedAt(now);
                    profile.setUpdatedAt(now);
                    return mapper.toDto(repository.save(profile));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto findByTelegramUserId(long telegramUserId) {
        return repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(UserProfileDto profileData) {
        return repository.findByTelegramUserIdAndDeletedFalse(profileData.getTelegramUserId())
                .map(existing -> {
                    mapper.updateEntityFromDto(profileData, existing);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return mapper.toDto(repository.save(existing));
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteProfile(long telegramUserId) {
        repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)
                .ifPresent(profile -> {
                    profile.setDeleted(true);
                    profile.setUpdatedAt(LocalDateTime.now());
                    repository.save(profile);
                });
    }
}
