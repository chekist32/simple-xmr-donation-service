package com.sokol.simplemonerodonationservice.unit.service;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.service.ConfirmationTokenService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntity;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntityRepository;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntityType;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.service.UserEntityModificationRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserEntityModificationRequestServiceTest {
    @InjectMocks
    private UserEntityModificationRequestServiceImpl userEntityModificationRequestService;

    @Mock
    private UserEntityModificationRequestEntityRepository userEntityModificationRequestEntityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Test
    public void createResetPasswordModificationRequest() {
        UserEntity user = new UserEntity("", "", "");

        when(confirmationTokenService.createConfirmationToken(any()))
                .thenReturn(new ConfirmationTokenEntity(user));
        when(userEntityModificationRequestEntityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityModificationRequestEntity resetPasswordModificationRequest = userEntityModificationRequestService.createResetPasswordModificationRequest(user);

        assertEquals(
                UserEntityModificationRequestEntityType.RESET_PASSWORD,
                resetPasswordModificationRequest.getModificationRequestType()
        );
        assertNotNull(resetPasswordModificationRequest.getConfirmationToken());
    }


    private static void generateId(ConfirmationTokenEntity entity) throws IllegalAccessException {
        Field id = ReflectionUtils.findField(ConfirmationTokenEntity.class, "token");
        id.setAccessible(true);
        id.set(entity, UUID.randomUUID());
    }

    @Test
    public void createChangeEmailModificationRequest() {
        UserEntity user = new UserEntity("", "", "");

        when(confirmationTokenService.createConfirmationToken(any()))
                .thenReturn(new ConfirmationTokenEntity(user));
        when(userEntityModificationRequestEntityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityModificationRequestEntity changeEmailModificationRequest = userEntityModificationRequestService.createChangeEmailModificationRequest("newEmail", user);

        assertEquals(
                UserEntityModificationRequestEntityType.CHANGE_EMAIL,
                changeEmailModificationRequest.getModificationRequestType()
        );
        assertNotNull(changeEmailModificationRequest.getConfirmationToken());
        assertEquals("newEmail", changeEmailModificationRequest.getNewEmail());
    }

    @Test
    public void createSetEnabledModificationRequest() {
        UserEntity user = new UserEntity("", "", "");

        when(confirmationTokenService.createConfirmationToken(any()))
                .thenReturn(new ConfirmationTokenEntity(user));
        when(userEntityModificationRequestEntityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityModificationRequestEntity setEnabledModificationRequest = userEntityModificationRequestService.createSetEnabledModificationRequest(true, user);

        assertEquals(
                UserEntityModificationRequestEntityType.REGISTRATION,
                setEnabledModificationRequest.getModificationRequestType()
        );
        assertNotNull(setEnabledModificationRequest.getConfirmationToken());
        assertTrue(setEnabledModificationRequest.getSetEnabled());
    }

    @Test
    public void createRegistrationConfirmationToken() {
        UserEntity user = new UserEntity("", "", "");

        when(confirmationTokenService.createConfirmationToken(any()))
                .thenReturn(new ConfirmationTokenEntity(user));
        when(userEntityModificationRequestEntityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityModificationRequestEntity setEnabledModificationRequest = userEntityModificationRequestService.createRegistrationConfirmationToken(user);

        assertEquals(
                UserEntityModificationRequestEntityType.REGISTRATION,
                setEnabledModificationRequest.getModificationRequestType()
        );
        assertNotNull(setEnabledModificationRequest.getConfirmationToken());
        assertTrue(setEnabledModificationRequest.getSetEnabled());
    }

    @Test
    public void updateNewPasswordForResetPasswordModificationRequest() throws IllegalAccessException {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword";

        UserEntity user = new UserEntity("", "", "");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(user);
        generateId(confirmationToken);

        when(userEntityModificationRequestEntityRepository.findByConfirmationToken(any()))
                .thenReturn(
                        Optional.of(new UserEntityModificationRequestEntity(
                                UserEntityModificationRequestEntityType.RESET_PASSWORD,
                                confirmationToken,
                                user
                        ))
                );

        UserEntityModificationRequestEntity updateNewPasswordForResetPasswordModificationRequest = userEntityModificationRequestService
                .updateNewPasswordForResetPasswordModificationRequest(token, newPassword);

        assertEquals(
                UserEntityModificationRequestEntityType.RESET_PASSWORD,
                updateNewPasswordForResetPasswordModificationRequest.getModificationRequestType()
        );
        assertNotNull(updateNewPasswordForResetPasswordModificationRequest.getConfirmationToken());
        assertEquals(newPassword, updateNewPasswordForResetPasswordModificationRequest.getNewPassword());
    }

    @Test
    public void implementUserEntityModificationRequest_ShouldSetIsEnabledOnTrue() throws IllegalAccessException {
        UserEntity user = new UserEntity("", "", "");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(user);
        generateId(confirmationToken);

        when(userEntityModificationRequestEntityRepository.findByConfirmationToken(any()))
                .thenReturn(
                        Optional.of(new UserEntityModificationRequestEntity(
                                UserEntityModificationRequestEntityType.REGISTRATION,
                                confirmationToken,
                                user
                        ).setSetEnabled(true))
                );

        userEntityModificationRequestService.implementUserEntityModificationRequest("anyToken");

        assertTrue(user.isEnabled());
    }

    @Test
    public void implementUserEntityModificationRequest_ShouldChangePassword() throws IllegalAccessException {
        UserEntity user = new UserEntity("", "", "");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(user);
        generateId(confirmationToken);

        String newPassword = "newPassword";

        when(userEntityModificationRequestEntityRepository.findByConfirmationToken(any()))
                .thenReturn(
                        Optional.of(new UserEntityModificationRequestEntity(
                                UserEntityModificationRequestEntityType.RESET_PASSWORD,
                                confirmationToken,
                                user
                        ).setNewPassword(newPassword))
                );

        userEntityModificationRequestService.implementUserEntityModificationRequest("anyToken");

        assertEquals(newPassword, user.getPassword());
    }

    @Test
    public void implementUserEntityModificationRequest_ShouldChangeEmail() throws IllegalAccessException {
        UserEntity user = new UserEntity("", "", "");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(user);
        generateId(confirmationToken);

        String newEmail = "newEmail";

        when(userEntityModificationRequestEntityRepository.findByConfirmationToken(any()))
                .thenReturn(
                        Optional.of(new UserEntityModificationRequestEntity(
                                UserEntityModificationRequestEntityType.RESET_PASSWORD,
                                confirmationToken,
                                user
                        ).setNewPassword(newEmail))
                );

        userEntityModificationRequestService.implementUserEntityModificationRequest("anyToken");

        assertEquals(newEmail, user.getPassword());
    }


}
