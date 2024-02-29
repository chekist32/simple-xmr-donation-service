package com.sokol.simplemonerodonationservice.crypto;

import com.sokol.simplemonerodonationservice.donation.DonationResponseDTO;
import com.sokol.simplemonerodonationservice.payment.PaymentService;

public class CryptoPaymentServiceImpl implements CryptoPaymentService {
    private final PaymentService paymentService;

    public CryptoPaymentServiceImpl(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @Override
    public DonationResponseDTO generateCryptoPayment() {
        return null;
    }
}
