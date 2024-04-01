package com.sokol.simplemonerodonationservice.crypto;

public class OutgoingCryptoTransactionEvent extends CryptoTransactionEvent {
    public OutgoingCryptoTransactionEvent(Class<?> sourceType, CryptoTransfer cryptoTransfer) {
        super(sourceType, cryptoTransfer);
    }
}
