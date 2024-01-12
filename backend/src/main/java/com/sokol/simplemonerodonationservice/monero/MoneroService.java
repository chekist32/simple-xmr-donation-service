package com.sokol.simplemonerodonationservice.monero;

import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressEntity;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressService;
import org.springframework.stereotype.Service;

@Service
public class MoneroService {
    private final MoneroSubaddressService moneroSubaddressService;
    public MoneroService(MoneroSubaddressService moneroSubaddressService) {
        this.moneroSubaddressService = moneroSubaddressService;
    }

    public MoneroSubaddressEntity getDonationMoneroSubaddress() {
        return moneroSubaddressService.getIdleDonationMoneroSubaddress(0);
    }
}
