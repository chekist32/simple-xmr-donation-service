package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.base.annotations.enumvalue.EnumValue;
import com.sokol.simplemonerodonationservice.crypto.CoinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DonationRequestDTO(
        @NotBlank
        @Size(max = 64, message = "Max size is 64 characters")
        String senderUsername,

        @Size(max = 300, message = "Max size is 300 characters")
        String donationText,

        @NotBlank
        @EnumValue(enumClass = CoinType.class)
        String coinType
) { }
