package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityModificationRequestEntityRepository extends CrudRepository<UserEntityModificationRequestEntity, UUID> {
    Optional<UserEntityModificationRequestEntity> findByConfirmationToken(String token);
}
