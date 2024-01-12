package com.sokol.simplemonerodonationservice.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    public final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}/status")
    public DeferredResult<ResponseEntity<Object>> getPaymentStatus(@PathVariable String paymentId) {
        DeferredResult<ResponseEntity<Object>> result = new DeferredResult<>(50*60*60*1000L);

        paymentService.getResponseWhenPaymentIsConfirmed(result, paymentId);

        return result;
    }
}
