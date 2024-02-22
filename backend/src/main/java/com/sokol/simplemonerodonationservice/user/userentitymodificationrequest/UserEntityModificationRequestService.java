package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenService;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;

public class UserEntityModificationRequestService {
    private final UserEntityModificationRequestEntityRepository userEntityModificationRequestEntityRepository;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public UserEntityModificationRequestService(UserEntityModificationRequestEntityRepository userEntityModificationRequestEntityRepository,
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
                        confirmationTokenService.createConfirmationToken(user), user
                )
        );
    }

    public UserEntityModificationRequestEntity createChangeEmailModificationRequest(String newEmail, UserEntity user) {
        return userEntityModificationRequestEntityRepository.save(
                new UserEntityModificationRequestEntity(
                        UserEntityModificationRequestEntityType.CHANGE_EMAIL,
                        confirmationTokenService.createConfirmationToken(user), user
                ).setNewEmail(newEmail)
        );
    }

    public UserEntityModificationRequestEntity createSetEnabledModificationRequest(boolean setEnabled, UserEntity user) {
        return userEntityModificationRequestEntityRepository.save(
                new UserEntityModificationRequestEntity(
                        UserEntityModificationRequestEntityType.REGISTRATION,
                        confirmationTokenService.createConfirmationToken(user), user
                ).setSetEnabled(setEnabled)
        );
    }

    public UserEntityModificationRequestEntity createRegistrationConfirmationToken(UserEntity user) {
        return this.createSetEnabledModificationRequest(true, user);
    }

    public UserEntityModificationRequestEntity updateNewPasswordForResetPasswordModificationRequest(String token, String newPassword) {
        UserEntityModificationRequestEntity userEntityModificationRequest =
                userEntityModificationRequestEntityRepository.findByConfirmationToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Bad token"));

        return userEntityModificationRequest.setNewPassword(newPassword);
    }

    public UserEntity implementUserEntityModificationRequest(String token) {
        UserEntityModificationRequestEntity userEntityModificationRequest =
                userEntityModificationRequestEntityRepository.findByConfirmationToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Bad token"));

        return this.implementUserEntityModificationRequest(userEntityModificationRequest);
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
