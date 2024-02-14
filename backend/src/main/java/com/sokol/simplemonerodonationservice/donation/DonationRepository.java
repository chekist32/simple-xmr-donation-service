package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DonationRepository extends CrudRepository<DonationEntity, UUID> {
    @Query("SELECT d FROM DonationEntity d WHERE d.moneroSubaddress = :moneroSubaddress AND d.isPaymentExpired = false AND d.isPaymentConfirmed = false ORDER BY d.receivedAt DESC LIMIT 1")
    Optional<DonationEntity> findRelevantDonation(@Param("moneroSubaddress") String moneroSubaddress);

    List<DonationEntity> findByUserAndConfirmedAtNotNull(UserEntity user);

    List<DonationEntity> findByUserAndConfirmedAtNotNull(UserEntity user, Pageable pageable);

    long countByUserAndConfirmedAtNotNull(UserEntity user);
}
