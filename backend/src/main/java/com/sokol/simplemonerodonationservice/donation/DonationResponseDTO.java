package com.sokol.simplemonerodonationservice.donation;

public record DonationResponseDTO(
        String subaddress,

        String paymentId
) { }
