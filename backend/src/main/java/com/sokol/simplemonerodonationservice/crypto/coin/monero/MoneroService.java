package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinService;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressEntity;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressService;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentService;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentServiceImpl;
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
