package com.sokol.simplemonerodonationservice.crypto;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class CryptoTransactionEvent extends ApplicationEvent {
    private static final Clock clock = Clock.systemUTC();

    private final CryptoTransfer cryptoTransfer;

    public CryptoTransactionEvent(Class<?> sourceType, CryptoTransfer cryptoTransfer) {
        super(sourceType, clock);
        this.cryptoTransfer = cryptoTransfer;
    }

    public CryptoTransfer getCryptoTransfer() {
        return cryptoTransfer;
    }
}
