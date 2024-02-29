package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinService;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressEntity;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressService;
import com.sokol.simplemonerodonationservice.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class MoneroService implements CoinService {
    private final MoneroSubaddressService moneroSubaddressService;
    private final MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService;
    private final PaymentService paymentService;

    public MoneroService(MoneroSubaddressService moneroSubaddressService, MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService, PaymentService paymentService) {
        this.moneroSubaddressService = moneroSubaddressService;
        this.moneroSubaddressScheduledExecutorService = moneroSubaddressScheduledExecutorService;
        this.paymentService = paymentService;
    }

    public String getDonationCryptoAddress() {
        MoneroSubaddressEntity moneroSubaddress = moneroSubaddressService.getIdleDonationMoneroSubaddress(0);
        PaymentEntity payment = paymentService.createPayment(moneroSubaddress.getSubaddress(), CoinType.XMR);
        moneroSubaddressScheduledExecutorService.setOccupationTimeout(moneroSubaddress, payment);

        return moneroSubaddress.getSubaddress();
    }

    @Override
    public void updateIsIdleByCryptoAddress(String address, boolean isIdle) {

    }
}
