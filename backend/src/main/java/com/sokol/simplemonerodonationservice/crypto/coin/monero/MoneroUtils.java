package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressEntity;
import monero.wallet.model.MoneroIncomingTransfer;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroSubaddress;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public final class MoneroUtils {
    public static MoneroSubaddressEntity MoneroSubaddressToMoneroSubaddressEntityMapper(
            MoneroSubaddress moneroSubaddress,
            String primaryAddress
    ) {
        return new MoneroSubaddressEntity(
                moneroSubaddress.getAddress(),
                primaryAddress,
                moneroSubaddress.getIndex()
        );
    }

    public static CryptoTransfer MoneroTransferToCryptoTransferMapper(MoneroIncomingTransfer incomingTransfer, String from) {
        return new CryptoTransfer(
                incomingTransfer.getAddress(),
                from,
                incomingTransfer.getAmount().doubleValue() / MoneroConfig.pp,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(incomingTransfer.getTx().getReceivedTimestamp()), ZoneOffset.UTC),
                CoinType.XMR,
                incomingTransfer.isIncoming(),
                incomingTransfer.isOutgoing()
        );
    }
}
