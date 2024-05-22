package com.sokol.simplemonerodonationservice.crypto.coin.listener;

import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;

public interface CoinListener {
    void onReceived(CryptoTransfer transfer);
    void onSpent(CryptoTransfer transfer);
}
