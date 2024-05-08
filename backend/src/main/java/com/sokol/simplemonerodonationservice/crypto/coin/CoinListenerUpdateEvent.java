package com.sokol.simplemonerodonationservice.crypto.coin;

import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import org.springframework.context.ApplicationEvent;

public class CoinListenerUpdateEvent extends ApplicationEvent {
    private final DonationSettingsDataDTO donationSettingsDataDTO;
    private final String principal;

    public CoinListenerUpdateEvent(DonationSettingsDataDTO donationSettingsDataDTO, String principal, Object source) {
        super(source);
        this.donationSettingsDataDTO = donationSettingsDataDTO;
        this.principal = principal;
    }

    public DonationSettingsDataDTO getDonationSettingsDataDTO() {
        return donationSettingsDataDTO;
    }

    public String getPrincipal() {
        return principal;
    }
}
