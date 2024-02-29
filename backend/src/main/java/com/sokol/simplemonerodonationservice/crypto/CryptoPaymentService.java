package com.sokol.simplemonerodonationservice.crypto;

import com.sokol.simplemonerodonationservice.donation.DonationResponseDTO;

public interface CryptoPaymentService {
    DonationResponseDTO generateCryptoPayment();
}
