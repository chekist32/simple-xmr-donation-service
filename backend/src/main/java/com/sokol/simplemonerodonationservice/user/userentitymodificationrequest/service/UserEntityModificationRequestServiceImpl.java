package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.service;

import com.sokol.simplemonerodonationservice.auth.registration.service.ConfirmationTokenService;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntity;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntityRepository;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntityType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserEntityModificationRequestServiceImpl implements UserEntityModificationRequestService {
    private final UserEntityModificationRequestEntityRepository userEntityModificationRequestEntityRepository;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public UserEntityModificationRequestServiceImpl(UserEntityModificationRequestEntityRepository userEntityModificationRequestEntityRepository,
                                                    UserRepository userRepository,
                                                    ConfirmationTokenService confirmationTokenService) {
        this.userEntityModificationRequestEntityRepository = userEntityModificationRequestEntityRepository;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
    }


    public UserEntityModificationRequestEntity createResetPasswordModificationRequest(UserEntity user) {
        return userEntityModificationRequestEntityRepository.save(
                new UserEntityModificationRequestEntity(
                        UserEntityModificationRequestEntityType.RESET_PASSWORD,
                        confirmationTokenService.createConfirmationToken(user),
                        user
                )
        );
    }

    public UserEntityModificationRequestEntity createChangeEmailModificationRequest(String newEmail, UserEntity user) {
        return userEntityModificationRequestEntityRepository.save(
                new UserEntityModificationRequestEntity(
                        UserEntityModificationRequestEntityType.CHANGE_EMAIL,
                        confirmationTokenService.createConfirmationToken(user),
                        user
                ).setNewEmail(newEmail)
        );
    }

    public UserEntityModificationRequestEntity createSetEnabledModificationRequest(boolean setEnabled, UserEntity user) {
        return userEntityModificationRequestEntityRepository.save(
                new UserEntityModificationRequestEntity(
                        UserEntityModificationRequestEntityType.REGISTRATION,
                        confirmationTokenService.createConfirmationToken(user),
                        user
                ).setSetEnabled(setEnabled)
        );
    }

    public UserEntityModificationRequestEntity createRegistrationConfirmationToken(UserEntity user) {
        return this.createSetEnabledModificationRequest(true, user);
    }

    private UserEntityModificationRequestEntity findUserEntityModificationRequestEntityByToken(String token) {
        try {
            return userEntityModificationRequestEntityRepository.findByConfirmationToken(UUID.fromString(token))
                    .orElseThrow(() -> new ResourceNotFoundException("Bad token"));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Bad token");
        }
    }

    public UserEntityModificationRequestEntity updateNewPasswordForResetPasswordModificationRequest(String token, String newPassword) {
        return findUserEntityModificationRequestEntityByToken(token).setNewPassword(newPassword);
    }

    public UserEntity implementUserEntityModificationRequest(String token) {
        return this.implementUserEntityModificationRequest(findUserEntityModificationRequestEntityByToken(token));
    }

    public UserEntity implementUserEntityModificationRequest(UserEntityModificationRequestEntity request) {
        confirmationTokenService.confirmToken(request.getConfirmationToken());

        UserEntity user = request.getUser();

        switch (request.getModificationRequestType()) {
            case REGISTRATION -> user.setEnabled(request.getSetEnabled());
            case CHANGE_EMAIL -> user.setEmail(request.getNewEmail());
            case RESET_PASSWORD -> user.setPassword(request.getNewPassword());
            default -> throw new RuntimeException("Illegal state (undefined ConfirmationTokenType)");
        }

        return userRepository.save(user);
    }

}
