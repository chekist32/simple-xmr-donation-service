package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.coin.AbstractCoinListener;
import com.sokol.simplemonerodonationservice.crypto.IncomingCryptoTransactionEvent;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroTxWallet;
import monero.wallet.model.MoneroWalletListenerI;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MoneroListener extends AbstractCoinListener implements MoneroWalletListenerI {
    private CryptoConfirmationType confirmationType;

    public MoneroListener(ApplicationEventPublisher applicationEventPublisher) {
        this(CryptoConfirmationType.UNCONFIRMED, applicationEventPublisher);
    }

    public MoneroListener(CryptoConfirmationType confirmationType,
                          ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
        this.confirmationType = confirmationType;
    }

    private boolean checkRequirements(MoneroTxWallet moneroTxWallet) {
        boolean result = moneroTxWallet.isIncoming() && !moneroTxWallet.isDoubleSpendSeen();

        switch (confirmationType) {
            case UNCONFIRMED -> result &= !moneroTxWallet.isConfirmed();
            case PARTIALLY_CONFIRMED -> result &= moneroTxWallet.isConfirmed() && moneroTxWallet.getNumConfirmations() >= 1;
            case FULLY_CONFIRMED -> result &= moneroTxWallet.isConfirmed() && moneroTxWallet.getNumConfirmations() >= 10;
            default -> { return false; }
        }

        return result;
    }

    @Override
    public void onOutputReceived(MoneroOutputWallet output) {
        MoneroTxWallet moneroTxWallet = output.getTx();
        String from = output.getStealthPublicKey();

        if (checkRequirements(moneroTxWallet)) {
            moneroTxWallet.getIncomingTransfers()
                    .stream()
                    .map(incomingTransfer -> MoneroUtils.MoneroTransferToCryptoTransferMapper(incomingTransfer, from))
                    .forEach(this::onReceived);
        }
    }

    @Override
    protected void onReceived(CryptoTransfer transfer) {
        publishIncomingCryptoTransactionEvent(transfer);
    }

    @Override
    protected void onSpent(CryptoTransfer transfer) {

    }

    public void updateCryptoConfirmationType(CryptoConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    @Override
    public void onSyncProgress(long height, long startHeight, long endHeight, double percentDone, String message) { }

    @Override
    public void onNewBlock(long height) { }

    @Override
    public void onBalancesChanged(BigInteger newBalance, BigInteger newUnlockedBalance) { }

    @Override
    public void onOutputSpent(MoneroOutputWallet output) { }
}
