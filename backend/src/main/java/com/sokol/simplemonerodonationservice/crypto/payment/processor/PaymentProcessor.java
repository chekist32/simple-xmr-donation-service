package com.sokol.simplemonerodonationservice.crypto.payment.processor;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.event.IncomingCryptoTransactionEvent;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentPurposeType;

public interface PaymentProcessor {
    PaymentEntity generateCryptoPayment(
            CoinType coinType,
            PaymentPurposeType paymentPurpose,
            double requiredAmount,
            long timeout
    );

    void onIncomingCryptoTransaction(IncomingCryptoTransactionEvent incomingCryptoTransactionEvent);
}
