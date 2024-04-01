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
        return confirmationTokenRepository.save(new ConfirmationTokenEntity(user));
    }

    public ConfirmationTokenEntity findConfirmationToken(String token) {
        return confirmationTokenRepository.findConfirmationTokenEntityByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Bad token"));
    }

    public ConfirmationTokenEntity confirmToken(String token) {
        return this.confirmToken(findConfirmationToken(token));
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
