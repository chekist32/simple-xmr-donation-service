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
                  AND d.payment.paymentStatus = 'PENDING'
            ORDER BY d.payment.createdAt DESC LIMIT 1
            """)
    Optional<DonationEntity> findRelevantDonation(@Param("cryptoAddress") String cryptoAddress);

    @Query("""
            SELECT d FROM DonationEntity d
            JOIN FETCH d.payment p
            WHERE d.user = :user AND p.paymentStatus = 'CONFIRMED'
            """)
    List<DonationEntity> findByUser(@Param("user") UserEntity user);

    @Query("""
            SELECT d FROM DonationEntity d
            JOIN FETCH d.payment p
            WHERE d.user = :user AND p.paymentStatus = 'CONFIRMED'
            """)
    List<DonationEntity> findByUser(@Param("user") UserEntity user, Pageable pageable);

    @Query("""
            SELECT COUNT(*) FROM DonationEntity
            WHERE user = :user
                  AND payment.paymentStatus = 'CONFIRMED'
            """)
    long countByUser(@Param("user") UserEntity user);
}
