package com.sokol.simplemonerodonationservice.crypto;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;

import java.time.LocalDateTime;

public record CryptoTransfer(
    String cryptoAddressTo,
    String cryptoAddressFrom,
    double amount,
    LocalDateTime timestamp,
    CoinType coinType,
    boolean isIncoming,
    boolean isOutgoing
) { }
