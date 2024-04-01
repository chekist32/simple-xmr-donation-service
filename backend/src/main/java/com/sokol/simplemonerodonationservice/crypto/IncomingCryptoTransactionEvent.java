package com.sokol.simplemonerodonationservice.crypto;

public class IncomingCryptoTransactionEvent extends CryptoTransactionEvent {
    public IncomingCryptoTransactionEvent(Class<?> sourceType, CryptoTransfer cryptoTransfer) {
        super(sourceType, cryptoTransfer);
    }
}
