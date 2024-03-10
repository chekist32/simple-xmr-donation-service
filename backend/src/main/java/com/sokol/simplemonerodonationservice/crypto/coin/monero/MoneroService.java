package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinService;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressService;
import org.springframework.stereotype.Service;

@Service
public class MoneroService implements CoinService {
    private final MoneroSubaddressService moneroSubaddressService;

    public MoneroService(MoneroSubaddressService moneroSubaddressService) {
        this.moneroSubaddressService = moneroSubaddressService;
    }

    public String getDonationCryptoAddress() {
        return moneroSubaddressService.getIdleDonationMoneroSubaddress(0).getSubaddress();
    }
}
