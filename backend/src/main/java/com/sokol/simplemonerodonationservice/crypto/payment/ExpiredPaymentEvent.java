package com.sokol.simplemonerodonationservice.crypto.payment;

public class ExpiredPaymentEvent extends PaymentEvent {
    public ExpiredPaymentEvent(Class<?> source, PaymentEntity payment) {
        super(source, payment);
    }
}
