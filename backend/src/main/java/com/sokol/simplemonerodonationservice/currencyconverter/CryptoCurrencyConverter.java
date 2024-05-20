package com.sokol.simplemonerodonationservice.currencyconverter;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;

public interface CryptoCurrencyConverter {
    double convertUsdToCrypto(double amount, CoinType coinType);
    double convertCryptoToUsd(double amount, CoinType coinType);
}
