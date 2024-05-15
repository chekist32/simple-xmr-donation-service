package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.service;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntity;

public interface UserEntityModificationRequestService {
    UserEntityModificationRequestEntity createResetPasswordModificationRequest(UserEntity user);

    UserEntityModificationRequestEntity createChangeEmailModificationRequest(String newEmail, UserEntity user);

    UserEntityModificationRequestEntity createSetEnabledModificationRequest(boolean setEnabled, UserEntity user);

    UserEntityModificationRequestEntity createRegistrationConfirmationToken(UserEntity user);

    UserEntityModificationRequestEntity updateNewPasswordForResetPasswordModificationRequest(String token, String newPassword);

    UserEntity implementUserEntityModificationRequest(String token);
    UserEntity implementUserEntityModificationRequest(UserEntityModificationRequestEntity request);
}
