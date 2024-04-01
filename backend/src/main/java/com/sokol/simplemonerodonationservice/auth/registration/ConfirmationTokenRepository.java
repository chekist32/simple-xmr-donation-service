package com.sokol.simplemonerodonationservice.auth.registration;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationTokenEntity, UUID> {
    Optional<ConfirmationTokenEntity> findConfirmationTokenEntityByToken(String token);
}
