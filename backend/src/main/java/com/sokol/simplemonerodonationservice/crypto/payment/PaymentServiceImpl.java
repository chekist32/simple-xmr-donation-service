package com.sokol.simplemonerodonationservice.crypto.payment;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.IncomingCryptoTransactionEvent;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinServiceFactory;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final CoinServiceFactory coinServiceFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              CoinServiceFactory coinServiceFactory) {
        this.paymentRepository = paymentRepository;
        this.coinServiceFactory = coinServiceFactory;
    }

//    public void getResponseWhenPaymentIsConfirmed(DeferredResult<ResponseEntity<Object>> result, String paymentId) {
//        UUID paymentUUID = UUID.fromString(paymentId);
//        Optional<PaymentEntity> payment = paymentRepository.findById(paymentUUID);
//
//        if (payment.isEmpty()) {
//            result.setErrorResult("There is no payment associated with such paymentId");
//            return;
//        }
//
//        PaymentEntity paymentEntity = payment.get();
//        switch (paymentEntity.getPaymentStatus()) {
//            case PENDING -> pendingPayments.put(paymentUUID, result);
//            case CONFIRMED -> result.setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));
//            case EXPIRED -> result.setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
//        }
//    }

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
}
