package com.sokol.simplemonerodonationservice.donation;

public record DonationResponseDTO(
        String cryptoAddress,

        String paymentId
) { }
