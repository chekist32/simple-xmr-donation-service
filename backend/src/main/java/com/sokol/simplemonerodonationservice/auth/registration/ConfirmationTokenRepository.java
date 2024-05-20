package com.sokol.simplemonerodonationservice.auth.registration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationTokenEntity, UUID> {
}
