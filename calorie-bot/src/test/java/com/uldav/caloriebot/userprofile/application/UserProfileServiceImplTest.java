package com.uldav.caloriebot.userprofile.application;

import com.uldav.caloriebot.userprofile.api.dto.UserProfileDto;
import com.uldav.caloriebot.userprofile.application.mapper.UserProfileMapper;
import com.uldav.caloriebot.userprofile.domain.UserProfile;
import com.uldav.caloriebot.userprofile.infrastructure.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository repository;

    @Mock
    private UserProfileMapper mapper;

    private UserProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserProfileServiceImpl(repository, mapper);
    }

    @Nested
    @DisplayName("getOrCreateProfile tests")
    class GetOrCreateProfileTests {

        @Test
        @DisplayName("Should return existing profile when found")
        void returnsExistingProfile() {
            long telegramUserId = 123L;
            UserProfile existing = buildProfile(telegramUserId, "john");
            UserProfileDto expectedDto = buildDto(telegramUserId, "john");

            when(repository.findByTelegramUserId(telegramUserId)).thenReturn(Optional.of(existing));
            when(mapper.toDto(existing)).thenReturn(expectedDto);

            UserProfileDto result = service.getOrCreateProfile(expectedDto);

            assertEquals(expectedDto, result);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should create new profile when not found")
        void createsNewProfile() {
            long telegramUserId = 456L;
            UserProfileDto inputDto = buildDto(telegramUserId, "jane");
            UserProfile newEntity = buildProfile(telegramUserId, "jane");
            UserProfile savedEntity = buildProfile(telegramUserId, "jane");
            savedEntity.setId(1L);
            UserProfileDto resultDto = buildDto(telegramUserId, "jane");

            when(repository.findByTelegramUserId(telegramUserId)).thenReturn(Optional.empty());
            when(mapper.toEntity(inputDto)).thenReturn(newEntity);
            when(repository.save(newEntity)).thenReturn(savedEntity);
            when(mapper.toDto(savedEntity)).thenReturn(resultDto);

            UserProfileDto result = service.getOrCreateProfile(inputDto);

            assertEquals(resultDto, result);
            verify(repository).save(newEntity);
            assertNotNull(newEntity.getCreatedAt());
            assertNotNull(newEntity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should be idempotent — repeated calls return same profile")
        void idempotentCreate() {
            long telegramUserId = 789L;
            UserProfile existing = buildProfile(telegramUserId, "bob");
            UserProfileDto dto = buildDto(telegramUserId, "bob");

            when(repository.findByTelegramUserId(telegramUserId)).thenReturn(Optional.of(existing));
            when(mapper.toDto(existing)).thenReturn(dto);

            UserProfileDto first = service.getOrCreateProfile(dto);
            UserProfileDto second = service.getOrCreateProfile(dto);

            assertEquals(first, second);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should restore soft-deleted profile instead of creating duplicate")
        void restoresSoftDeletedProfile() {
            long telegramUserId = 321L;
            UserProfile deletedProfile = buildProfile(telegramUserId, "old_name");
            deletedProfile.setDeleted(true);
            LocalDateTime originalCreatedAt = deletedProfile.getCreatedAt();
            UserProfileDto inputDto = buildDto(telegramUserId, "new_name");
            UserProfileDto restoredDto = buildDto(telegramUserId, "new_name");

            when(repository.findByTelegramUserId(telegramUserId)).thenReturn(Optional.of(deletedProfile));
            when(repository.save(deletedProfile)).thenReturn(deletedProfile);
            when(mapper.toDto(deletedProfile)).thenReturn(restoredDto);

            UserProfileDto result = service.getOrCreateProfile(inputDto);

            assertEquals(restoredDto, result);
            assertFalse(deletedProfile.isDeleted());
            assertEquals(originalCreatedAt, deletedProfile.getCreatedAt());
            assertNotNull(deletedProfile.getUpdatedAt());
            verify(mapper).updateEntityFromDto(inputDto, deletedProfile);
            verify(repository).save(deletedProfile);
        }
    }

    @Nested
    @DisplayName("findByTelegramUserId tests")
    class FindByTelegramUserIdTests {

        @Test
        @DisplayName("Should return profile when exists")
        void returnsProfileWhenExists() {
            long telegramUserId = 123L;
            UserProfile profile = buildProfile(telegramUserId, "john");
            UserProfileDto dto = buildDto(telegramUserId, "john");

            when(repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)).thenReturn(Optional.of(profile));
            when(mapper.toDto(profile)).thenReturn(dto);

            UserProfileDto result = service.findByTelegramUserId(telegramUserId);

            assertEquals(dto, result);
        }

        @Test
        @DisplayName("Should return null when profile does not exist")
        void returnsNullWhenNotFound() {
            when(repository.findByTelegramUserIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

            assertNull(service.findByTelegramUserId(999L));
        }
    }

    @Nested
    @DisplayName("updateProfile tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update existing profile with new data")
        void updatesExistingProfile() {
            long telegramUserId = 123L;
            UserProfile existing = buildProfile(telegramUserId, "old_name");
            UserProfileDto updateDto = buildDto(telegramUserId, "new_name");
            UserProfileDto resultDto = buildDto(telegramUserId, "new_name");

            when(repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)).thenReturn(Optional.of(existing));
            when(repository.save(existing)).thenReturn(existing);
            when(mapper.toDto(existing)).thenReturn(resultDto);

            UserProfileDto result = service.updateProfile(updateDto);

            assertEquals(resultDto, result);
            verify(mapper).updateEntityFromDto(updateDto, existing);
            assertNotNull(existing.getUpdatedAt());
        }

        @Test
        @DisplayName("Should return null when profile does not exist")
        void returnsNullWhenNotFound() {
            UserProfileDto dto = buildDto(999L, "unknown");
            when(repository.findByTelegramUserIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

            assertNull(service.updateProfile(dto));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle null fields in update — mapper IGNORE strategy preserves existing values")
        void handlesNullFieldsInUpdate() {
            long telegramUserId = 123L;
            UserProfile existing = buildProfile(telegramUserId, "john");
            UserProfileDto partialDto = UserProfileDto.builder()
                    .telegramUserId(telegramUserId)
                    .build();

            when(repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)).thenReturn(Optional.of(existing));
            when(repository.save(existing)).thenReturn(existing);
            when(mapper.toDto(existing)).thenReturn(buildDto(telegramUserId, "john"));

            UserProfileDto result = service.updateProfile(partialDto);

            assertNotNull(result);
            verify(mapper).updateEntityFromDto(partialDto, existing);
        }
    }

    @Nested
    @DisplayName("deleteProfile tests")
    class DeleteProfileTests {

        @Test
        @DisplayName("Should soft-delete existing profile")
        void softDeletesProfile() {
            long telegramUserId = 123L;
            UserProfile profile = buildProfile(telegramUserId, "john");

            when(repository.findByTelegramUserIdAndDeletedFalse(telegramUserId)).thenReturn(Optional.of(profile));
            when(repository.save(profile)).thenReturn(profile);

            service.deleteProfile(telegramUserId);

            assertTrue(profile.isDeleted());
            assertNotNull(profile.getUpdatedAt());
            verify(repository).save(profile);
        }

        @Test
        @DisplayName("Should do nothing when profile does not exist")
        void noOpWhenNotFound() {
            when(repository.findByTelegramUserIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

            service.deleteProfile(999L);

            verify(repository, never()).save(any());
        }
    }

    private UserProfile buildProfile(long telegramUserId, String username) {
        return UserProfile.builder()
                .telegramUserId(telegramUserId)
                .username(username)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private UserProfileDto buildDto(long telegramUserId, String username) {
        return UserProfileDto.builder()
                .telegramUserId(telegramUserId)
                .username(username)
                .build();
    }
}
