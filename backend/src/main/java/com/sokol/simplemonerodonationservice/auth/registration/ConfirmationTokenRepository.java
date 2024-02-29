package com.sokol.simplemonerodonationservice.auth.registration;

import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestEntityType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationTokenEntity, UUID> {
    Optional<ConfirmationTokenEntity> findConfirmationTokenEntityByToken(String token);
}
