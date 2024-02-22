package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DonationRepository extends CrudRepository<DonationEntity, UUID> {
    @Query("""
            SELECT d FROM DonationEntity d
            WHERE d.payment.cryptoAddress = :cryptoAddress
                  AND d.payment.paymentStatus = CONFIRMED
            ORDER BY d.payment.receivedAt DESC LIMIT 1
            """)
    Optional<DonationEntity> findRelevantDonation(@Param("cryptoAddress") String cryptoAddress);

    @Query("""
            SELECT d FROM DonationEntity d
            WHERE d.payment.cryptoAddress = :cryptoAddress
                  AND d.payment.paymentStatus = CONFIRMED
            ORDER BY d.payment.receivedAt DESC LIMIT 1
            """)
    @EntityGraph(attributePaths = )
    List<DonationEntity> findByUserAndConfirmedAtNotNull(UserEntity user);

    List<DonationEntity> findByUserAndConfirmedAtNotNull(UserEntity user, Pageable pageable);

    long countByUserAndConfirmedAtNotNull(UserEntity user);
}
