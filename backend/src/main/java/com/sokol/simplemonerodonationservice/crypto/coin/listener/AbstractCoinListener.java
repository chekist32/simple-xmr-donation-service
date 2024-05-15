package com.sokol.simplemonerodonationservice.crypto.coin.listener;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.event.IncomingCryptoTransactionEvent;
import com.sokol.simplemonerodonationservice.crypto.event.OutgoingCryptoTransactionEvent;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractCoinListener implements CoinListener {
    private final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractCoinListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected void publishIncomingCryptoTransactionEvent(CryptoTransfer transfer) {
        applicationEventPublisher.publishEvent(new IncomingCryptoTransactionEvent(null, transfer));
    }
    protected void publishOutgoingCryptoTransactionEvent(CryptoTransfer transfer) {
        applicationEventPublisher.publishEvent(new OutgoingCryptoTransactionEvent(null, transfer));
    }

    public abstract void onReceived(CryptoTransfer transfer);
    public abstract void onSpent(CryptoTransfer transfer);
}
