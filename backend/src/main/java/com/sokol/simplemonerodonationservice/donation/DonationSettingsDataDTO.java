package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;

public record DonationSettingsDataDTO(
        String userToken,

        CryptoConfirmationType confirmationType,

        int timeout,

        double minAmount
) { }
