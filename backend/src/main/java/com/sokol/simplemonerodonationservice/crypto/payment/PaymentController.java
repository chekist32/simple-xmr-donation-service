package com.sokol.simplemonerodonationservice.crypto.payment;

import com.sokol.simplemonerodonationservice.base.exception.BadRequestException;
import com.sokol.simplemonerodonationservice.crypto.payment.event.ConfirmedPaymentEvent;
import com.sokol.simplemonerodonationservice.crypto.payment.event.ExpiredPaymentEvent;
import com.sokol.simplemonerodonationservice.crypto.payment.service.PaymentService;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final static HashMap<UUID, DeferredResult<ResponseEntity<Object>>> pendingDeferredResults = new HashMap<>();
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}/status")
    public DeferredResult<ResponseEntity<Object>> getPaymentStatus(@PathVariable String paymentId) {
        UUID paymentIdUUID = null;

        try { paymentIdUUID = UUID.fromString(paymentId); }
        catch (IllegalArgumentException e) { throw new BadRequestException("Invalid paymentId"); }

        if (pendingDeferredResults.containsKey(paymentIdUUID)) return pendingDeferredResults.get(paymentIdUUID);

        PaymentEntity payment = paymentService.findPaymentById(paymentIdUUID);
        DeferredResult<ResponseEntity<Object>> result = new DeferredResult<>(Long.MAX_VALUE);

        switch (payment.getPaymentStatus()) {
            case PENDING -> pendingDeferredResults.put(paymentIdUUID, result);
            case CONFIRMED -> result.setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));
            case EXPIRED -> result.setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
        }

        return result;
    }

    @EventListener(classes = ExpiredPaymentEvent.class)
    protected void handleExpiredPaymentEvent(ExpiredPaymentEvent paymentEvent) {
        PaymentEntity payment = paymentEvent.getPayment();
        if (pendingDeferredResults.containsKey(payment.getId()))
            pendingDeferredResults
                    .remove(payment.getId())
                    .setResult(new ResponseEntity<>("Payment has been expired", HttpStatus.REQUEST_TIMEOUT));
    }

    @EventListener(classes = ConfirmedPaymentEvent.class)
    protected void handleConfirmedPaymentEvent(ConfirmedPaymentEvent paymentEvent) {
        PaymentEntity payment = paymentEvent.getPayment();
        if (pendingDeferredResults.containsKey(payment.getId()))
            pendingDeferredResults
                    .remove(payment.getId())
                    .setResult(new ResponseEntity<>("Payment has been confirmed", HttpStatus.OK));

    }
}
