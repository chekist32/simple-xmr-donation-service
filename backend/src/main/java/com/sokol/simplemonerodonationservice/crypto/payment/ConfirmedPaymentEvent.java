package com.sokol.simplemonerodonationservice.crypto.payment;

public class ConfirmedPaymentEvent extends PaymentEvent {

    public ConfirmedPaymentEvent(Class<?> source, PaymentEntity payment) {
        super(source, payment);
    }
}
