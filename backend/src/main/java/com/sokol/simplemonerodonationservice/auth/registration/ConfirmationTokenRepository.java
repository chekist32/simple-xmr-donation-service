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

    @Modifying
    @Transactional
    @Query("""
           UPDATE ConfirmationTokenEntity ct
           SET ct.isActive = false
           WHERE ct.user.id = :userId
               AND ct.isActive = true
               AND EXISTS (SELECT mr FROM UserEntityModificationRequestEntity mr
                           WHERE mr.id = ct.modificationRequest.id AND mr.modificationRequestEntityType = :requestType)
           """)
    void deactivateAllTokensByUserIdAndUserEntityModificationRequestEntityType(@Param("userId") int userId, @Param("requestType") UserEntityModificationRequestEntityType requestType);
}
