package com.sokol.simplemonerodonationservice.auth.registration;

import com.sokol.simplemonerodonationservice.base.exception.BadRequestException;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ConfirmationTokenEntity createConfirmationToken(UserEntity user) {
//        confirmationTokenRepository.deactivateAllTokensByUserIdAndUserEntityModificationRequestEntityType(
//                user.getId(),
//                userEntityModificationRequestEntity.getUserEntityModificationRequestEntityType()
//        );

        return confirmationTokenRepository.save(new ConfirmationTokenEntity(user));
    }

//    public ConfirmationTokenEntity createRegistrationConfirmationToken(UserEntity user) {
//        UserEntityModificationRequestEntity request = new UserEntityModificationRequestEntity(UserEntityModificationRequestEntityType.REGISTRATION);
//        request.setSetEnabled(true);
//        userEntityModificationRequestEntityRepository.save(request);
//        return this.createConfirmationToken(user, ConfirmationTokenType.USER_ENTITY, request);
//    }
//
//    public ConfirmationTokenEntity createChangeEmailConfirmationToken(UserEntity user, String newEmail) {
//        UserEntityModificationRequestEntity request = new UserEntityModificationRequestEntity(UserEntityModificationRequestEntityType.CHANGE_EMAIL);
//        request.setNewEmail(newEmail);
//        userEntityModificationRequestEntityRepository.save(request);
//        return this.createConfirmationToken(user, ConfirmationTokenType.USER_ENTITY, request);
//    }
//
//    public ConfirmationTokenEntity createResetPasswordConfirmationToken(UserEntity user) {
//        UserEntityModificationRequestEntity request = new UserEntityModificationRequestEntity(UserEntityModificationRequestEntityType.RESET_PASSWORD);
//        userEntityModificationRequestEntityRepository.save(request);
//        return this.createConfirmationToken(user, ConfirmationTokenType.USER_ENTITY, request);
//    }

//    public ConfirmationTokenEntity confirmResetPasswordConfirmationToken(String token, String newPassword) {
//        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findConfirmationTokenEntityByToken(token)
//                .orElseThrow(() -> new ResourceNotFoundException("Bad token"));
//
//        confirmationTokenChecks(confirmationToken);
//
//        UserEntityModificationRequestEntity request = confirmationToken.getModificationRequest();
//        request.setNewPassword(newPassword);
//        userEntityModificationRequestEntityRepository.save(request);
//
//        confirmationToken.implementToken();
//        return confirmationTokenRepository.save(confirmationToken);
//    }
//
//    public ConfirmationTokenEntity confirmEmailByRegistrationConfirmationToken(String token) {
//        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findConfirmationTokenEntityByToken(token)
//                .orElseThrow(() -> new ResourceNotFoundException("Bad token"));
//
//        confirmationTokenChecks(confirmationToken);
//
//        confirmationToken.implementToken();
//        return confirmationTokenRepository.save(confirmationToken);
//    }
//
//    public ConfirmationTokenEntity confirmEmailByChangeEmailConfirmationToken(String token) {
//        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findConfirmationTokenEntityByToken(token)
//                .orElseThrow(() -> new ResourceNotFoundException("Bad token"));
//
//        confirmationTokenChecks(confirmationToken);
//
//        confirmationToken.implementToken();
//        return confirmationTokenRepository.save(confirmationToken);
//    }

    public ConfirmationTokenEntity confirmToken(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findConfirmationTokenEntityByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Bad token"));

        return this.confirmToken(confirmationToken);
    }

    public ConfirmationTokenEntity confirmToken(ConfirmationTokenEntity confirmationToken) {
        confirmationTokenChecks(confirmationToken);

        confirmationToken.implementToken();
        return confirmationTokenRepository.save(confirmationToken);
    }

    private void confirmationTokenChecks(ConfirmationTokenEntity confirmationToken) {
        if (LocalDateTime.now(ZoneOffset.UTC).isAfter(confirmationToken.getExpirationDate())
                || confirmationToken.getConfirmedAt() != null
                || !confirmationToken.isActive()) {
            confirmationToken.expireToken();
            confirmationTokenRepository.save(confirmationToken);
            throw new BadRequestException("Token has expired");
        }
    }
}
