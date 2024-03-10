package com.sokol.simplemonerodonationservice.donation;

public record DonationResponseDTO(
        String cryptoAddress,

        double minAmount,

        int timeout,

        String paymentId
) { }
