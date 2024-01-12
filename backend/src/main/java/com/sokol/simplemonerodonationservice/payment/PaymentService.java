package com.sokol.simplemonerodonationservice.payment;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ConcurrentHashMap<UUID, DeferredResult<ResponseEntity<Object>>> pendindPaymentsHashMap = new ConcurrentHashMap<>();

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentEntity createPayment() {
        return paymentRepository.save(new PaymentEntity());
    }
    public PaymentEntity createPayment(double amount) {
        return paymentRepository.save(new PaymentEntity(amount));
    }

    @Async
    public void getResponseWhenPaymentIsConfirmed(DeferredResult<ResponseEntity<Object>> result, String paymentId) {
        UUID paymentIdUUID = UUID.fromString(paymentId);
        Optional<PaymentEntity> payment = paymentRepository.findById(paymentIdUUID);

        if (payment.isEmpty()) {
            result.setErrorResult("There is no payment associated with such paymentId");
            return;
        }

        PaymentEntity paymentEntity = payment.get();
        switch (paymentEntity.getPaymentStatus()) {
            case PENDING -> pendindPaymentsHashMap.put(paymentIdUUID, result);
            case CONFIRMED -> result.setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));
            case EXPIRED -> result.setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
        }


    }

    public void expirePayment(PaymentEntity payment) {
        payment.setPaymentStatus(PaymentStatus.EXPIRED);
        paymentRepository.save(payment);

        DeferredResult<ResponseEntity<Object>> result = pendindPaymentsHashMap.get(payment.getId());
        if (result != null) {
            result.setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
            pendindPaymentsHashMap.remove(payment.getId());
        }
    }

    public PaymentEntity confirmPayment(String paymentId) {
        UUID paymentIdUUID = UUID.fromString(paymentId);

        PaymentEntity payment = paymentRepository.findById(paymentIdUUID)
                .orElseThrow(() -> new ResourceNotFoundException("There is no payment associated with such paymentId"));

        return this.confirmPayment(payment);
    }

    public PaymentEntity confirmPayment(PaymentEntity payment) {
        payment.setConfirmedAt(LocalDateTime.now(ZoneOffset.UTC));
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);

        DeferredResult<ResponseEntity<Object>> result = pendindPaymentsHashMap.get(payment.getId());
        if (result != null) {
            result.setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));
            pendindPaymentsHashMap.remove(payment.getId());
        }

        return paymentRepository.save(payment);
    }



}
