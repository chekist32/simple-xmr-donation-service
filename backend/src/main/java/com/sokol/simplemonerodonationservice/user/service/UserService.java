package com.sokol.simplemonerodonationservice.user.service;

import com.sokol.simplemonerodonationservice.auth.dto.ChangeEmailRequestDTO;
import com.sokol.simplemonerodonationservice.auth.dto.ChangePasswordRequestDTO;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.user.UserDataResponseDTO;
import com.sokol.simplemonerodonationservice.user.UserEntity;

public interface UserService {
    ConfirmationTokenEntity registerUser(RegistrationRequestDTO registrationDTO);

    UserDataResponseDTO getUserDataByUsername(String username);
    UserDataResponseDTO getUserDataByPrincipal(String principal);

    UserEntity changeUserPassword(String principal, ChangePasswordRequestDTO changePasswordRequestDTO);

    ConfirmationTokenEntity changeEmail(String principal, ChangeEmailRequestDTO changeEmailRequestDTO);
    UserEntity changeEmail(String token);

    ConfirmationTokenEntity resetUserPassword(String email);
    UserEntity resetUserPassword(String token, String newPassword);

    UserEntity activateUser(String token);
}
