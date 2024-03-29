package com.sokol.simplemonerodonationservice.payment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<PaymentEntity, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE PaymentEntity SET paymentStatus = :paymentStatus WHERE id = :id ")
    void updatePaymentStatus(@Param("id") UUID id, @Param("paymentStatus") PaymentStatus paymentStatus);
}
