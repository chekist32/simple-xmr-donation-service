package com.sokol.simplemonerodonationservice.crypto.coin;

import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;

public interface CoinService {
    String getDonationCryptoAddress();
    void updateListenersCriteria(CoinListenerUpdateEvent coinListenerUpdateEvent);
}
