package com.sokol.simplemonerodonationservice.crypto.event;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;

public class IncomingCryptoTransactionEvent extends CryptoTransactionEvent {
    public IncomingCryptoTransactionEvent(Object source, CryptoTransfer cryptoTransfer) {
        super(source, cryptoTransfer);
    }
}
