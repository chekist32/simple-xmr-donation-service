package com.sokol.simplemonerodonationservice.donation.donationuserdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DonationUserDataDTO(
        @NotBlank
        @Size(max = 64, message = "Max size is 64 characters")
        String username,

        @Size(max = 225, message = "Max size is 225 characters")
        String greetingText


) { }
