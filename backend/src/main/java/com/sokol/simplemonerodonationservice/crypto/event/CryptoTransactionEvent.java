package com.sokol.simplemonerodonationservice.crypto.event;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class CryptoTransactionEvent extends ApplicationEvent {
    private static final Clock clock = Clock.systemUTC();

    private final CryptoTransfer cryptoTransfer;

    public CryptoTransactionEvent(Object source, CryptoTransfer cryptoTransfer) {
        super(source, clock);
        this.cryptoTransfer = cryptoTransfer;
    }

    public CryptoTransfer getCryptoTransfer() {
        return cryptoTransfer;
    }
}
