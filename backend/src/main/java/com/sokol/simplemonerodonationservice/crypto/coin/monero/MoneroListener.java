package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.coin.AbstractCoinListener;
import monero.daemon.MoneroDaemonRpc;
import monero.daemon.model.MoneroDaemonListener;
import monero.wallet.MoneroWalletFull;
import monero.wallet.model.*;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigInteger;

public class MoneroListener extends AbstractCoinListener implements MoneroWalletListenerI {
    private CryptoConfirmationType confirmationType;
    private final MoneroWalletFull moneroWalletFull;

    public MoneroListener(ApplicationEventPublisher applicationEventPublisher, MoneroWalletFull moneroWalletFull) {
        this(CryptoConfirmationType.PARTIALLY_CONFIRMED, applicationEventPublisher, moneroWalletFull);
    }

    public MoneroListener(CryptoConfirmationType confirmationType,
                          ApplicationEventPublisher applicationEventPublisher, MoneroWalletFull moneroWalletFull) {
        super(applicationEventPublisher);
        this.confirmationType = confirmationType;
        this.moneroWalletFull = moneroWalletFull;
    }

    private boolean checkRequirements(MoneroTxWallet moneroTxWallet) {
//        if (moneroTxWallet == null) return false;

        boolean result = moneroTxWallet.isIncoming();

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
        String from = output.getStealthPublicKey();

        moneroWalletFull.save();

        System.out.println("########Start###########");
        System.out.println(output);
        System.out.println("---------------");
        System.out.println(output.getTx());
        System.out.println("#########End##########");

        var etst = moneroWalletFull.getTxs(new MoneroTxQuery());

        MoneroTxWallet moneroTxWallet = moneroWalletFull.getTx(output.getTx().getHash());
        var test1 = moneroWalletFull.getIncomingTransfers();

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
    public void onSyncProgress(long height, long startHeight, long endHeight, double percentDone, String message) {
        System.out.println(moneroWalletFull.getTxs().get(moneroWalletFull.getTxs().size()-1));
        System.out.println(height);
    }

    @Override
    public void onNewBlock(long height) { }

    @Override
    public void onBalancesChanged(BigInteger newBalance, BigInteger newUnlockedBalance) { }

    @Override
    public void onOutputSpent(MoneroOutputWallet output) { }
}
