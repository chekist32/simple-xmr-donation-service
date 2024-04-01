package com.sokol.simplemonerodonationservice.crypto.coin;

import com.sokol.simplemonerodonationservice.crypto.coin.monero.MoneroService;
import org.springframework.stereotype.Component;

@Component
public class CoinServiceFactory {
    private final MoneroService moneroService;

    public CoinServiceFactory(MoneroService moneroService) {
        this.moneroService = moneroService;
    }

    public CoinService getCoinService(CoinType coinType) {
        switch (coinType) {
            case XMR -> { return moneroService; }
            default -> throw new RuntimeException("");
        }
    }
}
