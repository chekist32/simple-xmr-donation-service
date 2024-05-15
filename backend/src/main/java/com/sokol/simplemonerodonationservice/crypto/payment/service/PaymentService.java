package com.sokol.simplemonerodonationservice.crypto.payment.service;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentPurposeType;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentEntity generateCryptoPayment(CoinType coinType, PaymentPurposeType paymentPurpose);
    PaymentEntity generateCryptoPayment(CoinType coinType, PaymentPurposeType paymentPurpose, double requiredAmount);

    PaymentEntity findPaymentById(UUID paymentId);
    PaymentEntity findPendingPaymentByCryptoAddress(String cryptoAddress);
    List<PaymentEntity> findAllPendingPayments();

    PaymentEntity expirePayment(PaymentEntity payment);

    PaymentEntity confirmPayment(PaymentEntity payment, double amount);
    PaymentEntity confirmPayment(String paymentId, double amount);

    PaymentEntity confirmPaymentByCryptoAddress(String cryptoAddress, double amount);
}
