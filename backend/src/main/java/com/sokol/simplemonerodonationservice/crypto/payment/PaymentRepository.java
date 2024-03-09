package com.sokol.simplemonerodonationservice.crypto.payment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends CrudRepository<PaymentEntity, UUID> {
    @Query("""
           SELECT p FROM PaymentEntity p
           WHERE p.cryptoAddress = :cryptoAddress
                 AND p.paymentStatus = PENDING
           ORDER BY p.createdAt DESC LIMIT 1
           """)
    Optional<PaymentEntity> findPendingPaymentByCryptoAddress(@Param("cryptoAddress") String cryptoAddress);
}
