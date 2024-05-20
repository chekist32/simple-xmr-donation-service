package com.sokol.simplemonerodonationservice.crypto.event;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;

public class OutgoingCryptoTransactionEvent extends CryptoTransactionEvent {
    public OutgoingCryptoTransactionEvent(Object source, CryptoTransfer cryptoTransfer) {
        super(source, cryptoTransfer);
    }
}
