package com.sokol.simplemonerodonationservice.crypto.payment.event;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;

public class ExpiredPaymentEvent extends PaymentEvent {
    public ExpiredPaymentEvent(Object source, PaymentEntity payment) {
        super(source, payment);
    }
}
