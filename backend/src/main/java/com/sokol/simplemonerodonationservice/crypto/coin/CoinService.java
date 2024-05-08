package com.sokol.simplemonerodonationservice.crypto.coin;

public interface CoinService {
    String getDonationCryptoAddress();
    void updateListenersCriteria(CoinListenerUpdateEvent coinListenerUpdateEvent);
}
