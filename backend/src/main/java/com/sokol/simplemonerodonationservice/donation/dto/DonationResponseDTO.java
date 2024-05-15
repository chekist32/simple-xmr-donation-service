package com.sokol.simplemonerodonationservice.donation.dto;

public record DonationResponseDTO(
        String cryptoAddress,

        double minAmount,

        String coinType,

        int timeout,

        String paymentId
) { }
