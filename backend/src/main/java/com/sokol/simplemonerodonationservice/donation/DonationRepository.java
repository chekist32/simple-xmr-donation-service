package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DonationRepository extends CrudRepository<DonationEntity, UUID> {
    Optional<DonationEntity> findDonationByPayment(PaymentEntity payment);

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
