package com.sokol.simplemonerodonationservice.auth.registration.service;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;

public interface ConfirmationTokenService {
    ConfirmationTokenEntity createConfirmationToken(UserEntity user);

    ConfirmationTokenEntity findConfirmationToken(String token);

    ConfirmationTokenEntity confirmToken(String token);
    ConfirmationTokenEntity confirmToken(ConfirmationTokenEntity confirmationToken);
}
