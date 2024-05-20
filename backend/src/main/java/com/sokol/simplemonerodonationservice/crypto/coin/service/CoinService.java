package com.sokol.simplemonerodonationservice.crypto.coin.service;

import com.sokol.simplemonerodonationservice.crypto.coin.listener.CoinListenerUpdateEvent;

public interface CoinService {
    String getDonationCryptoAddress();
    void updateListenersCriteria(CoinListenerUpdateEvent coinListenerUpdateEvent);
}
