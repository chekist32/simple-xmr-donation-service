package com.sokol.simplemonerodonationservice.crypto.payment.event;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;

public class ConfirmedPaymentEvent extends PaymentEvent {
    public ConfirmedPaymentEvent(Object source, PaymentEntity payment) {
        super(source, payment);
    }
}
