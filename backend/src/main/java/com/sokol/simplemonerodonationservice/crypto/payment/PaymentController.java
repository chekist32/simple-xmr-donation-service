package com.sokol.simplemonerodonationservice.crypto.payment;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final static HashMap<UUID, DeferredResult<ResponseEntity<Object>>> pendingDeferredResults = new HashMap<>();

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}/status")
    public DeferredResult<ResponseEntity<Object>> getPaymentStatus(@PathVariable String paymentId) {
        UUID paymentIdUUID = UUID.fromString(paymentId);
        if (pendingDeferredResults.containsKey(paymentIdUUID)) return pendingDeferredResults.get(paymentIdUUID);

        PaymentEntity payment = paymentService.findPaymentById(paymentIdUUID);
        DeferredResult<ResponseEntity<Object>> result = new DeferredResult<>(50*60*60*1000L);

        switch (payment.getPaymentStatus()) {
            case PENDING -> pendingDeferredResults.put(paymentIdUUID, result);
            case CONFIRMED -> result.setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));
            case EXPIRED -> result.setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
        }

        return result;
    }

    @EventListener(classes = ExpiredPaymentEvent.class)
    private void handleExpiredPaymentEvent(ExpiredPaymentEvent paymentEvent) {
        PaymentEntity payment = paymentEvent.getPayment();
        if (pendingDeferredResults.containsKey(payment.getId()))
            pendingDeferredResults
                    .remove(payment.getId())
                    .setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
    }

    @EventListener(classes = ConfirmedPaymentEvent.class)
    private void handleConfirmedPaymentEvent(ConfirmedPaymentEvent paymentEvent) {
        PaymentEntity payment = paymentEvent.getPayment();
        if (pendingDeferredResults.containsKey(payment.getId()))
            pendingDeferredResults
                    .remove(payment.getId())
                    .setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));

    }
}
