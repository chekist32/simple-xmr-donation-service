package com.sokol.simplemonerodonationservice.donation;

public record DonationResponseDTO(
        String cryptoAddress,

        double minAmount,

        String coinType,

        int timeout,

        String paymentId
) { }
