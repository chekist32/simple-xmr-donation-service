package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityModificationRequestEntityRepository extends CrudRepository<UserEntityModificationRequestEntity, UUID> {
    @Query("""
           SELECT mr FROM UserEntityModificationRequestEntity mr
           WHERE mr.confirmationToken.id = :token
           """)
    Optional<UserEntityModificationRequestEntity> findByConfirmationToken(@Param("token") String token);
}
