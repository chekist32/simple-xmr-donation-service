package com.sokol.simplemonerodonationservice.crypto.coin;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.IncomingCryptoTransactionEvent;
import com.sokol.simplemonerodonationservice.crypto.OutgoingCryptoTransactionEvent;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractCoinListener {
    private final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractCoinListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected void publishIncomingCryptoTransactionEvent(CryptoTransfer transfer) {
        applicationEventPublisher.publishEvent(new IncomingCryptoTransactionEvent(this.getClass(), transfer));
    }
    protected void publishOutgoingCryptoTransactionEvent(CryptoTransfer transfer) {
        applicationEventPublisher.publishEvent(new OutgoingCryptoTransactionEvent(this.getClass(), transfer));
    }

    protected abstract void onReceived(CryptoTransfer transfer);
    protected abstract void onSpent(CryptoTransfer transfer);

}
