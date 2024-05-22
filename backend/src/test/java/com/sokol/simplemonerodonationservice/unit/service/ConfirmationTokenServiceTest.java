package com.sokol.simplemonerodonationservice.unit.service;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenRepository;
import com.sokol.simplemonerodonationservice.auth.registration.service.ConfirmationTokenServiceImpl;
import com.sokol.simplemonerodonationservice.base.exception.BadRequestException;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {
    @InjectMocks
    private ConfirmationTokenServiceImpl confirmationTokenService;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Test
    void createConfirmationToken() {
        UserEntity user = new UserEntity("", "", "");

        when(confirmationTokenRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConfirmationTokenEntity createdToken = confirmationTokenService.createConfirmationToken(user);

        assertNull(createdToken.getConfirmedAt());
        assertTrue(createdToken.isActive());
    }

    private static void generateId(ConfirmationTokenEntity entity) throws IllegalAccessException {
        Field id = ReflectionUtils.findField(ConfirmationTokenEntity.class, "token");
        id.setAccessible(true);
        id.set(entity, UUID.randomUUID());
    }

    @Test
    void findConfirmationToken_ShouldReturnConfirmationEntity() throws IllegalAccessException {
        ConfirmationTokenEntity token = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        generateId(token);

        when(confirmationTokenRepository.findById(not(eq(token.getToken()))))
                .thenReturn(Optional.empty());
        when(confirmationTokenRepository.findById(eq(token.getToken())))
                .thenReturn(Optional.of(token));

        ConfirmationTokenEntity confirmationToken = confirmationTokenService.findConfirmationToken(token.getToken().toString());

        assertEquals(token.getToken(), confirmationToken.getToken());
    }

    @Test
    void findConfirmationToken_ShouldThrowResourceNotFoundException() {
        when(confirmationTokenRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> confirmationTokenService.findConfirmationToken(UUID.randomUUID().toString()));
    }

    @Test
    void confirmToken_ShouldReturnConfirmedToken() throws IllegalAccessException {
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        generateId(confirmationToken);

        when(confirmationTokenRepository.save(confirmationToken))
                .thenReturn(confirmationToken);
        when(confirmationTokenRepository.findById(not(eq(confirmationToken.getToken()))))
                .thenReturn(Optional.empty());
        when(confirmationTokenRepository.findById(eq(confirmationToken.getToken())))
                .thenReturn(Optional.of(confirmationToken));

        ConfirmationTokenEntity confirmedToken = confirmationTokenService.confirmToken(confirmationToken.getToken().toString());

        assertNotNull(confirmedToken.getConfirmedAt());
        assertFalse(confirmedToken.isActive());
        assertTrue(confirmedToken.getExpirationDate().isAfter(confirmedToken.getConfirmedAt()));
    }

    @Test
    void confirmToken_ShouldThrowBadRequestException() throws IllegalAccessException {
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        generateId(confirmationToken);

        when(confirmationTokenRepository.save(confirmationToken))
                .thenReturn(confirmationToken);
        when(confirmationTokenRepository.findById(not(eq(confirmationToken.getToken()))))
                .thenReturn(Optional.empty());
        when(confirmationTokenRepository.findById(eq(confirmationToken.getToken())))
                .thenReturn(Optional.of(confirmationToken));


        // Expired Token
        Field expirationDateField = ReflectionUtils.findField(ConfirmationTokenEntity.class, "expirationDate");
        expirationDateField.setAccessible(true);
        expirationDateField.set(confirmationToken, LocalDateTime.now(ZoneOffset.UTC));

        assertThrows(BadRequestException.class, () -> confirmationTokenService.confirmToken(confirmationToken.getToken().toString()));


        // Deactivated Token
        ConfirmationTokenEntity confirmationToken1 = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        generateId(confirmationToken1);

        Field isActiveField = ReflectionUtils.findField(ConfirmationTokenEntity.class, "isActive");
        isActiveField.setAccessible(true);
        isActiveField.set(confirmationToken1, false);

        assertThrows(BadRequestException.class, () -> confirmationTokenService.confirmToken(confirmationToken.getToken().toString()));


        // Already used token
        ConfirmationTokenEntity confirmationToken2 = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        generateId(confirmationToken2);

        Field confirmedAtField = ReflectionUtils.findField(ConfirmationTokenEntity.class, "confirmedAt");
        confirmedAtField.setAccessible(true);
        confirmedAtField.set(confirmationToken2, LocalDateTime.now(ZoneOffset.UTC));

        assertThrows(BadRequestException.class, () -> confirmationTokenService.confirmToken(confirmationToken.getToken().toString()));
        
    }
}