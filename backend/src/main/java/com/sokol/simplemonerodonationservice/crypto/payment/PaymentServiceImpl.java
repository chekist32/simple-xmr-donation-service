package com.sokol.simplemonerodonationservice.crypto.payment;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinServiceFactory;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final CoinServiceFactory coinServiceFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              CoinServiceFactory coinServiceFactory) {
        this.paymentRepository = paymentRepository;
        this.coinServiceFactory = coinServiceFactory;
    }

    public PaymentEntity expirePayment(PaymentEntity payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) return payment;

        payment.setPaymentStatus(PaymentStatus.EXPIRED);

        return paymentRepository.save(payment);
    }

    public PaymentEntity confirmPaymentByCryptoAddress(String cryptoAddress, double amount) {
        PaymentEntity payment = paymentRepository.findPendingPaymentByCryptoAddress(cryptoAddress)
                .orElseThrow(() -> new ResourceNotFoundException("There is no payment associated with such cryptoAddress"));

        return this.confirmPayment(payment, amount);
    }

    public PaymentEntity confirmPayment(String paymentId, double amount) {
        return this.confirmPayment(findPaymentById(UUID.fromString(paymentId)), amount);
    }

    public PaymentEntity confirmPayment(PaymentEntity payment, double amount) {
        payment.setConfirmedAt(LocalDateTime.now(ZoneOffset.UTC));
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        payment.setAmount(amount);

        return paymentRepository.save(payment);
    }

    @Override
    public PaymentEntity generateCryptoPayment(CoinType coinType, PaymentPurposeType paymentPurpose) {
        return paymentRepository.save(
                new PaymentEntity(
                        coinServiceFactory.getCoinService(coinType).getDonationCryptoAddress(),
                        coinType,
                        paymentPurpose
                )
        );
    }

    @Override
    public PaymentEntity generateCryptoPayment(CoinType coinType, PaymentPurposeType paymentPurpose, double requiredAmount) {
       return paymentRepository.save(
                new PaymentEntity(
                        coinServiceFactory.getCoinService(coinType).getDonationCryptoAddress(),
                        requiredAmount,
                        coinType,
                        paymentPurpose
                )
        );
    }

    @Override
    public PaymentEntity findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no payment associated with such paymentId"));
    }

    @Override
    public PaymentEntity findPendingPaymentByCryptoAddress(String cryptoAddress) {
        return paymentRepository.findPendingPaymentByCryptoAddress(cryptoAddress)
                .orElseThrow(() -> new ResourceNotFoundException("There is no payment associated with such cryptoAddress"));
    }

    @Override
    public List<PaymentEntity> findAllPendingPayments() {
        return paymentRepository.findAllByPaymentStatus(PaymentStatus.PENDING);
    }
}
