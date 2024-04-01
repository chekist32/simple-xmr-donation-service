package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinListenerUpdateEvent;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinService;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressService;
import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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

    @EventListener(classes = CoinListenerUpdateEvent.class)
    @Override
    public void updateListenersCriteria(CoinListenerUpdateEvent coinListenerUpdateEvent) {
        moneroSubaddressService.setNewListener(CryptoConfirmationType.valueOf(coinListenerUpdateEvent.getDonationSettingsDataDTO().confirmationType()));
    }
}
