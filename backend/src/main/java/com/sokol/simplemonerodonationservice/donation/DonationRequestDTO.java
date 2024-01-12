package com.sokol.simplemonerodonationservice.donation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DonationRequestDTO(
        @NotBlank
        @Size(max = 64, message = "Max size is 64 characters")
        String senderUsername,
        @Size(max = 300, message = "Max size is 300 characters")
        String donationText
) {
}
