package com.sokol.simplemonerodonationservice.crypto.payment.event;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class PaymentEvent extends ApplicationEvent {
    private final static Clock clock = Clock.systemUTC();

    private final PaymentEntity payment;

    public PaymentEvent(Object source, PaymentEntity payment) {
        super(source, clock);
        this.payment = payment;
    }

    public PaymentEntity getPayment() {
        return payment;
    }
}
